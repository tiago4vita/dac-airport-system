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
 * @param {string} clienteCode - Optional cliente code for authorization
 * @returns {string} JWT token
 */
const generateToken = (email, tipo = {}, clienteCode = null) => {
  // Create the payload with standard claims
  const payload = {
    sub: email,        // Subject (whom the token refers to)
    role: tipo,        // Role or user type
    iss: 'ms-auth',    // Issuer of the token
    iat: Math.floor(Date.now() / 1000),  // Issued at timestamp
  };

  // Add cliente code if provided
  if (clienteCode) {
    payload.clienteCode = clienteCode;
  }

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

/**
 * Check if the user type in the JWT token matches the expected type
 * @param {string} token - JWT token to verify
 * @param {string} expectedType - Expected user type/role
 * @returns {boolean} True if token is valid and user type matches, false otherwise
 */
const hasUserType = (token, expectedType) => {
  // First verify the token is valid
  const decoded = verifyToken(token);
  
  // If token is invalid or role doesn't match, return false
  if (!decoded || decoded.role !== expectedType) {
    return false;
  }
  
  return true;
};

module.exports = {
  generateToken,
  verifyToken,
  hasUserType
}; 