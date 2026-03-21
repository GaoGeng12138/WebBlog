export function hasRole(user, role) {
  if (!user || !role) return false
  return Array.isArray(user.roles) && user.roles.includes(role)
}

export function hasPermission(user, permission) {
  if (!user || !permission) return false
  if (hasRole(user, 'ROLE_ADMIN')) return true
  return Array.isArray(user.permissions) && user.permissions.includes(permission)
}

export function hasAccess(user, permission) {
  if (!permission) return true
  return hasPermission(user, permission)
}
