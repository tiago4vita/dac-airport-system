const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');

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

// Route traffic to microservices
app.use('/service1', createProxyMiddleware(process.env.MICROSERVICE_AUTH_URL));
app.use('/service2', createProxyMiddleware(process.env.MICROSERVICE2_URL));

// Define the port
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`API Gateway running on port ${PORT}`);
}); 