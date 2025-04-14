const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');
const { validateLoginRequest } = require('./middleware/loginValidation');
const fetch = require('node-fetch');

// Load environment variables
dotenv.config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'OK' });
});

// Login endpoint with validation
app.post('/login', validateLoginRequest, async (req, res) => {
  try {
    const authUrl = `${process.env.MICROSERVICE_AUTH_URL}/api/auth/login`;
    const response = await fetch(authUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(req.body)
    });

    // Get the response content if any
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
    }

    // Forward the exact status code from ms-auth
    res.status(response.status);

    // Forward all headers from ms-auth
    for (const [key, value] of response.headers.entries()) {
      res.setHeader(key, value);
    }

    // Send the response body if it exists, otherwise just end the response
    if (responseBody) {
      res.json(responseBody);
    } else {
      res.end();
    }

  } catch (error) {
    console.error('Error forwarding login request:', error);
    res.status(500).json({ 
      error: 'Internal Server Error',
      message: error.message 
    });
  }
});

// Route traffic to microservices
app.use('/service1', createProxyMiddleware(process.env.MICROSERVICE_AUTH_URL));
app.use('/service2', createProxyMiddleware(process.env.MICROSERVICE2_URL));

// Define the port
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`API Gateway running on port ${PORT}`);
}); 