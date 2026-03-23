const PROVIDER_PATTERNS = {
  aliyunOss: /(?:aliyuncs\.com|\.oss-[^.]+\.aliyuncs\.com)$/i,
  qiniu: /(?:qiniucdn\.com|clouddn\.com|qnssl\.com)$/i,
  imagekit: /imagekit\.io$/i,
  cloudinary: /res\.cloudinary\.com$/i
}

function isSpecialUrl(url) {
  return /^(data:|blob:)/i.test(url)
}

function normalizeUrl(src) {
  if (typeof src !== 'string') return ''
  const value = src.trim()
  return value || ''
}

function appendSearchParams(url, params) {
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, String(value))
    }
  })
  return url.toString()
}

export function getOptimizedImageUrl(src, options = {}) {
  const normalizedSrc = normalizeUrl(src)
  if (!normalizedSrc || isSpecialUrl(normalizedSrc)) {
    return normalizedSrc
  }

  const {
    width,
    height,
    quality = 78,
    fit = 'cover'
  } = options

  try {
    const url = new URL(normalizedSrc, window.location.origin)
    const host = url.hostname

    if (PROVIDER_PATTERNS.aliyunOss.test(host)) {
      const resizeMode = fit === 'contain' ? 'm_lfit' : 'm_fill'
      const parts = ['image/format,webp', `resize,${resizeMode}`]
      if (width) parts.push(`w_${Math.round(width)}`)
      if (height) parts.push(`h_${Math.round(height)}`)
      if (quality) parts.push(`q_${Math.round(quality)}`)
      url.searchParams.set('x-oss-process', parts.join('/'))
      return url.toString()
    }

    if (PROVIDER_PATTERNS.qiniu.test(host)) {
      const mode = fit === 'contain' ? 0 : 1
      const parts = [`imageView2/${mode}`]
      if (width) parts.push(`w/${Math.round(width)}`)
      if (height) parts.push(`h/${Math.round(height)}`)
      if (quality) parts.push(`q/${Math.round(quality)}`)
      parts.push('format/webp')
      const separator = url.search ? '&' : '?'
      return `${normalizedSrc}${separator}${parts.join('/')}`
    }

    if (PROVIDER_PATTERNS.imagekit.test(host)) {
      const transforms = []
      if (width) transforms.push(`w-${Math.round(width)}`)
      if (height) transforms.push(`h-${Math.round(height)}`)
      transforms.push(`q-${Math.round(quality)}`, 'f-webp', fit === 'contain' ? 'c-at_max' : 'c-maintain_ratio')
      return appendSearchParams(url, { tr: transforms.join(',') })
    }

    if (PROVIDER_PATTERNS.cloudinary.test(host)) {
      return normalizedSrc
    }

    return normalizedSrc
  } catch {
    return normalizedSrc
  }
}

export function getImageLoadingAttrs(index = 0, eagerCount = 3) {
  const isPriority = index < eagerCount
  return {
    loading: isPriority ? 'eager' : 'lazy',
    fetchpriority: isPriority ? 'high' : 'low',
    decoding: 'async'
  }
}
