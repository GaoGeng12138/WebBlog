const ENCRYPTED_PREFIX = 'ENC::'
const DEFAULT_TRANSPORT_KEY = 'WebLogTransportKey2026Secret!!@#'

function sanitizeTransportKey(rawKey) {
  if (typeof rawKey !== 'string') {
    return ''
  }

  const trimmedKey = rawKey.trim()
  if (
    (trimmedKey.startsWith('"') && trimmedKey.endsWith('"')) ||
    (trimmedKey.startsWith("'") && trimmedKey.endsWith("'"))
  ) {
    return trimmedKey.slice(1, -1).trim()
  }

  return trimmedKey
}

function getTransportKey() {
  const envKey = sanitizeTransportKey(import.meta.env.VITE_TRANSPORT_CRYPTO_KEY)
  if (envKey) {
    return envKey
  }

  return sanitizeTransportKey(DEFAULT_TRANSPORT_KEY)
}

function normalizeKeyBytes(rawKey) {
  const keyBytes = new TextEncoder().encode(rawKey)
  if ([16, 24, 32].includes(keyBytes.length)) {
    return keyBytes
  }

  return null
}

function toBase64(bytes) {
  let binary = ''
  bytes.forEach((byte) => {
    binary += String.fromCharCode(byte)
  })
  return btoa(binary)
}

function fromBase64(base64) {
  const binary = atob(base64)
  const bytes = new Uint8Array(binary.length)
  for (let i = 0; i < binary.length; i += 1) {
    bytes[i] = binary.charCodeAt(i)
  }
  return bytes
}

async function importAesKey() {
  const normalizedKey = normalizeKeyBytes(getTransportKey())
  if (!normalizedKey) {
    console.warn('[transport-crypto] invalid key length, fallback to plain transport')
    return null
  }

  return crypto.subtle.importKey(
    'raw',
    normalizedKey,
    { name: 'AES-CBC' },
    false,
    ['encrypt', 'decrypt']
  )
}

export function isEncryptedTransportValue(value) {
  return typeof value === 'string' && value.startsWith(ENCRYPTED_PREFIX)
}

export async function encryptTransportValue(value) {
  if (value === null || value === undefined || value === '') {
    return value
  }

  if (isEncryptedTransportValue(value)) {
    return value
  }

  const key = await importAesKey()
  if (!key) {
    return value
  }

  const iv = crypto.getRandomValues(new Uint8Array(16))
  const payload = new TextEncoder().encode(String(value))
  const encrypted = await crypto.subtle.encrypt({ name: 'AES-CBC', iv }, key, payload)

  return `${ENCRYPTED_PREFIX}${toBase64(iv)}::${toBase64(new Uint8Array(encrypted))}`
}

export async function decryptTransportValue(value) {
  if (!isEncryptedTransportValue(value)) {
    return value
  }

  const payload = value.slice(ENCRYPTED_PREFIX.length)
  const [ivBase64, cipherBase64] = payload.split('::')
  if (!ivBase64 || !cipherBase64) {
    return value
  }

  const key = await importAesKey()
  if (!key) {
    return value
  }

  const iv = fromBase64(ivBase64)
  const encrypted = fromBase64(cipherBase64)
  const plainBuffer = await crypto.subtle.decrypt({ name: 'AES-CBC', iv }, key, encrypted)

  return new TextDecoder().decode(plainBuffer)
}

export async function encryptPayloadFields(payload, sensitiveFields = []) {
  if (!payload || typeof payload !== 'object' || Array.isArray(payload) || sensitiveFields.length === 0) {
    return payload
  }

  const clonedPayload = { ...payload }
  for (const fieldName of sensitiveFields) {
    if (typeof clonedPayload[fieldName] === 'string' && clonedPayload[fieldName] !== '') {
      clonedPayload[fieldName] = await encryptTransportValue(clonedPayload[fieldName])
    }
  }

  return clonedPayload
}

export async function decryptTransportData(data) {
  if (typeof data === 'string') {
    return decryptTransportValue(data)
  }

  if (Array.isArray(data)) {
    const result = []
    for (const item of data) {
      result.push(await decryptTransportData(item))
    }
    return result
  }

  if (data && typeof data === 'object') {
    const result = {}
    for (const [key, value] of Object.entries(data)) {
      result[key] = await decryptTransportData(value)
    }
    return result
  }

  return data
}
