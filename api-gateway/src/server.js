const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');
const { validateLoginRequest } = require('./middleware/loginValidation');

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

    // Forward the status and response from ms-auth
    res.status(response.status);
    
    if (response.ok) {
      res.json(await response.json());
    } else {
      res.end();
    }
  } catch (error) {
    console.error('Error forwarding login request:', error);
    res.status(500).json({ error: 'Internal Server Error' });
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