const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');
const { validateLoginRequest } = require('./middleware/loginValidation');
const fetch = require('node-fetch');
const { generateRandomPassword, hashPassword } = require('./utils/passwordUtils');
const { sendToQueue, connect } = require('./services/rabbitMQService');

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
    // Create a copy of the request body
    const authRequest = { ...req.body };
    
    // Hash the password before sending to auth service
    if (authRequest.senha) {
      authRequest.senha = hashPassword(authRequest.senha);
    }
    
    const authUrl = `${process.env.MICROSERVICE_AUTH_URL}/api/auth/login`;
    const response = await fetch(authUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(authRequest)
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

app.post('/clientes', async (req, res) => {
  try {
    // Step 1: Call ms-cliente to create the cliente
    const clienteUrl = `${process.env.MICROSERVICE_CLIENTE_URL}/api/clientes`;
    const response = await fetch(clienteUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(req.body)
    });

    // Forward status and headers from ms-cliente
    res.status(response.status);
    for (const [key, value] of response.headers.entries()) {
      res.setHeader(key, value);
    }

    // Process response from ms-cliente
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
    }

    // Step 2: If cliente was created successfully (201), generate password and send to ms-auth
    if (response.status === 201) {
      // Generate random 4-digit PIN
      const rawPassword = generateRandomPassword();
      console.log(`Generated password for ${req.body.email}: ${rawPassword}`);
      
      // Hash the password with SHA-256 + salt
      const hashedPassword = hashPassword(rawPassword);
      
      // Create user in ms-auth using RabbitMQ (SAGA pattern)
      await sendToQueue('auth-service-queue', {
        action: 'CREATE_USER',
        payload: {
          email: req.body.email,
          senha: hashedPassword,
          tipo: 'CLIENTE',
          ativo: true
        }
      });
      
      // Enhance response to include information about the password
      if (responseBody) {
        responseBody.message = 'Cliente created successfully. Password sent to console.';
      } else {
        responseBody = { 
          message: 'Cliente created successfully. Password sent to console.' 
        };
      }
    }

    // Send the response
    if (responseBody) {
      res.json(responseBody);
    } else {
      res.end();
    }
  } catch (error) {
    console.error('Error forwarding cliente request:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// Route traffic to microservices
app.use('/service1', createProxyMiddleware(process.env.MICROSERVICE_AUTH_URL));
app.use('/service2', createProxyMiddleware(process.env.MICROSERVICE2_URL));

// Connect to RabbitMQ on startup
(async () => {
  try {
    await connect();
    console.log('Connected to RabbitMQ');
  } catch (error) {
    console.error('Failed to connect to RabbitMQ:', error);
    // You might want to implement retry logic here
  }
})();

// Define the port
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`API Gateway running on port ${PORT}`);
}); 