// Function to check if user has admin role
export const hasAdminRole = (user) => {
    if (!user) return false;
    
    // Handle case where roles is an array
    if (Array.isArray(user.roles)) {
        return user.roles.includes('ROLE_ADMIN');
    }
    
    // Handle case where role is a single string
    if (typeof user.role === 'string') {
        return user.role === 'ROLE_ADMIN';
    }
    
    return false;
};