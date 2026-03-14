// Test file to verify hasAdminRole function

import { hasAdminRole } from './composables/role.js';

// Test cases
console.log('Test 1 - Admin user with array roles:');
const adminUserArray = {
  username: 'admin',
  roles: ['ROLE_ADMIN', 'USER']
};
console.log('Result:', hasAdminRole(adminUserArray));

console.log('Test 2 - Regular user with array roles:');
const regularUserArray = {
  username: 'user',
  roles: ['USER']
};
console.log('Result:', hasAdminRole(regularUserArray));

console.log('Test 3 - Admin user with string role:');
const adminUserString = {
  username: 'admin',
  role: 'ROLE_ADMIN'
};
console.log('Result:', hasAdminRole(adminUserString));

console.log('Test 4 - Regular user with string role:');
const regularUserString = {
  username: 'user',
  role: 'USER'
};
console.log('Result:', hasAdminRole(regularUserString));

console.log('Test 5 - User with no roles:');
const noRoleUser = {
  username: 'guest'
};
console.log('Result:', hasAdminRole(noRoleUser));

console.log('Test 6 - Null user:');
console.log('Result:', hasAdminRole(null));