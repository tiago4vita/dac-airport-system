const crypto = require('crypto');

// Salt to be used in password hashing - in production, use an environment variable
const SALT = process.env.PASSWORD_SALT || 'airport-system-salt';

/**
 * Generate a random 4-digit PIN
 * @returns {string} 4-digit PIN
 */
const generateRandomPassword = () => {
  return Math.floor(1000 + Math.random() * 9000).toString();
};

/**
 * Hash a password using SHA-256 + salt
 * @param {string} password - The password to hash
 * @returns {string} The hashed password
 */
const hashPassword = (password) => {
  return crypto
    .createHash('sha256')
    .update(password + SALT)
    .digest('hex');
};

module.exports = {
  generateRandomPassword,
  hashPassword
}; 