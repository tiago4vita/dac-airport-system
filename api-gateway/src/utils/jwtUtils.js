const jwt = require('jsonwebtoken');
const dotenv = require('dotenv');

// Load environment variables
dotenv.config();

/**
 * Secret key for signing JWT tokens - in production, use environment variable
 * @type {string}
 */
const JWT_SECRET = process.env.JWT_SECRET || 'airport-system-jwt-secret';

/**
 * Expiration time for JWT tokens (in seconds or as a string describing a time span)
 * @type {string}
 */
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '24h';

/**
 * Generate a JWT token
 * @param {string} email - User's email address
 * @param {string} tipo - User type/role (e.g., 'CLIENTE', 'FUNCIONARIO')
 * @returns {string} JWT token
 */
const generateToken = (email, tipo = {}) => {
  // Create the payload with standard claims
  const payload = {
    sub: email,        // Subject (whom the token refers to)
    role: tipo,        // Role or user type
    iss: 'ms-auth',    // Issuer of the token
    iat: Math.floor(Date.now() / 1000),  // Issued at timestamp
  };

  // Sign the token
  return jwt.sign(
    payload,
    JWT_SECRET,
    { expiresIn: JWT_EXPIRES_IN }
  );
};

/**
 * Verify a JWT token
 * @param {string} token - JWT token to verify
 * @returns {Object|null} Decoded token payload or null if invalid
 */
const verifyToken = (token) => {
  try {
    return jwt.verify(token, JWT_SECRET);
  } catch (error) {
    console.error('JWT verification failed:', error.message);
    return null;
  }
};

module.exports = {
  generateToken,
  verifyToken
}; 