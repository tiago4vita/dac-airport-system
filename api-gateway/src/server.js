const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');
const { validateLoginRequest } = require('./middleware/loginValidation');
const fetch = require('node-fetch');
const { generateRandomPassword, hashPassword } = require('./utils/passwordUtils');
const { enviarCadastro } = require('./services/rabbitMQService');
const { generateToken, verifyToken, hasUserType } = require('./utils/jwtUtils');
const jwt = require('jsonwebtoken');
const axios = require('axios');

// Load environment variables
dotenv.config();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Validation helper functions
const isValidDate = (dateString) => {
  const date = new Date(dateString);
  return date instanceof Date && !isNaN(date) && date > new Date();
};

const isValidAirport = (airportCode) => {
  // Check if it's a valid 3-letter IATA airport code
  return /^[A-Z]{3}$/.test(airportCode);
};

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'OK' });
});

// PERMISSÃO : NENHUMA
// R02a - LOGIN
app.post('/login', validateLoginRequest, async (req, res) => {
  try {
    console.log('Login request received:', req.body);
    
    // Create a copy of the request body
    const authRequest = { ...req.body };
    
    // Generate random 4-digit password if none provided
    if (!authRequest.senha) {
      const randomPassword = Math.floor(1000 + Math.random() * 9000).toString();
      authRequest.senha = randomPassword;
      console.log('Generated random password:', randomPassword);
    }

    // Hash the password before sending to auth service
    if (authRequest.senha) {
      authRequest.senha = hashPassword(authRequest.senha);
    }
    
    const authUrl = `${process.env.ORCHESTRATOR_URL}/login`;
    console.log('Forwarding to orchestrator:', authUrl);
    
    const response = await fetch(authUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(authRequest)
    });

    console.log('Orchestrator response status:', response.status);

    // Get the response content if any
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
      console.log('Orchestrator response body:', responseBody);
    }

    // If login is successful (status 200), generate JWT token
    if (response.status === 200 && responseBody) {
      console.log('Generating JWT token for email:', responseBody.email || authRequest.email);
      
      // Extract cliente code from the response if available
      let clienteCode = null;
      if (responseBody.usuario && responseBody.usuario.codigo) {
        clienteCode = responseBody.usuario.codigo;
      }
      
      // Generate JWT token with user email, tipo, and cliente code
      const token = generateToken(responseBody.email || authRequest.email, responseBody.tipo, clienteCode);
      
      // Add JWT token to the response
      responseBody.access_token = token;
      responseBody.token_type = 'bearer';
      
      // Set Authorization header
      res.setHeader('Authorization', `Bearer ${token}`);
    }

    // Forward the exact status code from ms-auth
    res.status(response.status);

    // Forward all headers from ms-auth (except Authorization which we set above)
    for (const [key, value] of response.headers.entries()) {
      if (key.toLowerCase() !== 'authorization') {
        res.setHeader(key, value);
      }
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

// PERMISSÃO : TODOS
// R02b - LOGOUT
app.post('/logout', async (req, res) => {
  try {
    // Get token from Authorization header
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'No token provided'
      });
    }

    const token = authHeader.split(' ')[1];

    // Verify if token is valid
    const decodedToken = verifyToken(token);
    if (!decodedToken) {
      return res.status(401).json({
        error: 'Unauthorized', 
        message: 'Invalid or expired token'
      });
    }

    // If we get here, token was valid
    res.status(200).json({
      message: 'Successfully logged out'
    });

  } catch (error) {
    console.error('Error during logout:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// PERMISSÃO : NENHUMA
// R01 - AUTOCADASTRO
app.post('/clientes', async (req, res) => {
  try {
    console.log('Cliente creation request received:', req.body);
    
    // Create a copy of the request body
    const clienteRequest = { ...req.body };
    
    // Hash the password before sending to orchestrator
    if (!clienteRequest.senha) {
      const randomPassword = Math.floor(1000 + Math.random() * 9000).toString();
      clienteRequest.senha = randomPassword;
      console.log('Generated random password for new client:', randomPassword);
    }
    clienteRequest.senha = hashPassword(clienteRequest.senha);
    
// publica as credenciais no ms-auth via RabbitMQ
  await enviarCadastro({
    login: clienteRequest.email,
    senha: clienteRequest.senha,
   // (opcional) repasse outros campos que o ms-auth aguarde:
    cpf: clienteRequest.cpf,
    nome: clienteRequest.nome
  });

    const orchestratorUrl = `${process.env.ORCHESTRATOR_URL}/clientes`;
    console.log('Forwarding to orchestrator:', orchestratorUrl);
    
    const response = await fetch(orchestratorUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(clienteRequest)
    });

    // Get the response content if any
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
      console.log('Orchestrator response body:', responseBody);
    }

    // Forward all headers from orchestrator except status
    for (const [key, value] of response.headers.entries()) {
      res.setHeader(key, value);
    }

    // Send the response body if it exists, otherwise just end the response
    if (responseBody) {
      // Check if it's a conflict error
      if (responseBody.success === false && responseBody.message && responseBody.message.includes('já existe')) {
        return res.status(409).json(responseBody);
      }
      // Return 201 Created for successful creation
      res.status(201).json(responseBody);
    } else {
      res.status(201).end();
    }

  } catch (error) {
    console.error('Error forwarding cliente request:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// R03-TESTE: busca todos os clientes
app.get('/clientes', async (req, res) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader) {
      return res
        .status(401)
        .json({ error: 'Unauthorized', message: 'No token provided' });
    }
    const token = authHeader.replace(/^Bearer\s+/i, '');

    // Permitir FUNCIONARIO, ADMIN ou CLIENTE
    if (
      !hasUserType(token, 'FUNCIONARIO') &&
      !hasUserType(token, 'ADMIN') &&
      !hasUserType(token, 'CLIENTE')
    ) {
      return res
        .status(403)
        .json({ error: 'Forbidden', message: 'Insufficient permissions' });
    }

    const orchestratorUrl = `${process.env.ORCHESTRATOR_URL}/clientes`;
    console.log('Forwarding GET /clientes to orchestrator:', orchestratorUrl);

    const response = await fetch(orchestratorUrl, {
      method: 'GET',
      headers: { Authorization: `Bearer ${token}` },
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    return res.status(response.status).json(data);
  } catch (error) {
    console.error('Error in GET /clientes:', error);
    return res.status(500).json({ error: 'Internal Server Error' });
  }
});

// PERMISSÃO : CLIENTE
// R03 - TELA INICIAL DE CLIENTE
app.get('/clientes/:codigoCliente', async (req, res) => {
  try {
    // Get token from Authorization header
    const authHeader = req.headers.authorization;
    if (!authHeader) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'No authorization token provided'
      });
    }

    const token = authHeader.split(' ')[1];
    
    // Verify token and check if user type is CLIENTE
    if (!hasUserType(token, 'CLIENTE')) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'User must be of type CLIENTE to access this resource'
      });
    }

    // Verify if the cliente code in the URL matches the one in the JWT
    const decodedToken = verifyToken(token);
    if (!decodedToken) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Invalid or expired token'
      });
    }

    const { codigoCliente } = req.params;

    console.log('Cliente search request received for codigo:', codigoCliente);
    
    const orchestratorUrl = `${process.env.ORCHESTRATOR_URL}/clientes/${codigoCliente}`;
    console.log('Forwarding to orchestrator:', orchestratorUrl);
    
    const response = await fetch(orchestratorUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });

    console.log('Orchestrator response status:', response.status);

    // Get the response content if any
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
      console.log('Orchestrator response body:', responseBody);
    }

    // Forward the exact status code from orchestrator
    res.status(response.status);

    // Forward all headers from orchestrator
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
    console.error('Error forwarding cliente search request:', error);
    res.status(500).json({ error: 'Internal Server Error', message: error.message });
  }
});

// PERMISSÃO: CLIENTE
// R-something: Listar reservas do cliente
app.get('/clientes/:codigoCliente/reservas', async (req, res) => {
  try {
    const { codigoCliente } = req.params;
    
    // Validate JWT token
    const token = req.headers.authorization?.replace('Bearer ', '');
    if (!token) {
      return res.status(401).json({ success: false, message: 'Token não fornecido' });
    }

    const decoded = verifyToken(token);
    if (!decoded) {
      return res.status(401).json({ success: false, message: 'Token inválido ou expirado' });
    }
    console.log('JWT decoded for reservas endpoint:', decoded);
    
    // Ensure the client can only access their own reservations
    if (decoded.role === 'CLIENTE' && decoded.clienteCode !== codigoCliente) {
      return res.status(403).json({ success: false, message: 'Acesso negado' });
    }

    console.log('Forwarding to orchestrator: http://orchestrator:3002/clientes/' + codigoCliente + '/reservas');
    
    const response = await axios.get(`http://orchestrator:3002/clientes/${codigoCliente}/reservas`);
    console.log('Orchestrator response status:', response.status);
    console.log('Orchestrator response data:', response.data);
    
    // Return 204 if response data is an empty array
    if (Array.isArray(response.data) && response.data.length === 0) {
      return res.status(204).end();
    }
    
    res.json(response.data);
  } catch (error) {
    console.error('Error in /clientes/:codigoCliente/reservas:', error.message);
    if (error.response) {
      console.error('Error response data:', error.response.data);
      res.status(error.response.status).json(error.response.data);
    } else {
      res.status(500).json({ success: false, message: 'Erro interno do servidor' });
    }
  }
});

// R05 - COMPRAR MILHAS
app.put('/clientes/:codigoCliente/milhas', async (req, res) => {
  try {
    // Get token from Authorization header
    const authHeader = req.headers.authorization;
    if (!authHeader) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'No authorization token provided'
      });
    }

    const token = authHeader.split(' ')[1];
    
    // Verify token and check if user type is CLIENTE
    if (!hasUserType(token, 'CLIENTE')) {
      return res.status(403).json({
        error: 'Forbidden',
        message: 'User must be of type CLIENTE to access this resource'
      });
    }

    // Verify if the cliente code in the URL matches the one in the JWT
    const decodedToken = verifyToken(token);
    if (!decodedToken) {
      return res.status(401).json({
        error: 'Unauthorized',
        message: 'Invalid or expired token'
      });
    }

    const { codigoCliente } = req.params;
    const { quantidade } = req.body;

    // Validate quantidade field
    if (!quantidade || !Number.isInteger(quantidade) || quantidade <= 0 || quantidade > 10000) {
      return res.status(400).json({
        error: 'Invalid quantidade',
        message: 'Quantidade must be a positive integer between 1 and 10000'
      });
    }

    // Verify if the cliente code in the URL matches the one in the JWT
    if (decodedToken.clienteCode && decodedToken.clienteCode !== codigoCliente) {
      return res.status(403).json({
        error: 'Forbidden',
        message: 'User can only access their own miles'
      });
    }

    console.log('Comprar milhas request received for cliente:', codigoCliente, 'quantidade:', quantidade);
    
    const orchestratorUrl = `${process.env.ORCHESTRATOR_URL}/clientes/${codigoCliente}/milhas`;
    console.log('Forwarding to orchestrator:', orchestratorUrl);
    
    const response = await fetch(orchestratorUrl, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ quantidade })
    });

    console.log('Orchestrator response status:', response.status);

    // Get the response content if any
    let responseBody;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      responseBody = await response.json();
      console.log('Orchestrator response body:', responseBody);
    }

    // Forward the exact status code from orchestrator
    res.status(response.status);

    // Forward all headers from orchestrator
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
    console.error('Error forwarding comprar milhas request:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// R06 - CONSULTAR EXTRATO DE MILHAS
app.get('/clientes/{codigoCliente}/milhas', async (req, res) => { 
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is CLIENTE
  if (!hasUserType(token, 'CLIENTE')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type CLIENTE to access this resource'
    });
  }
  //TODO: Implementar a lógica para buscar as milhas do cliente usando saga
  //Verificar se o codigo do cliente é o mesmo do JWT
  //Se não for, retornar 403
});

// R07a - BUSCAR VOOS POR DATA, ORIGEM E DESTINO
app.get('/voos', async (req, res) => {
  const { data, origem, destino } = req.query;

  // Validate optional query parameters
  if (data || origem || destino) {
    // Parameters are present but invalid
    if ((data && !isValidDate(data)) || 
        (origem && !isValidAirport(origem)) || 
        (destino && !isValidAirport(destino))) {
      return res.status(400).json({
        error: 'Invalid parameters',
        message: 'If provided, data, origem and destino must be valid values'
      });
    }
  }
  //TODO: Implementar a lógica para buscar os voos usando saga
  //Verificar se a data, origem e destino são válidos
  //Se não for, retornar 400
});

// R07b - CRIAR RESERVA
app.post('/reservas', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is CLIENTE
  if (!hasUserType(token, 'CLIENTE')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type CLIENTE to access this resource'
    });
  }
  try {
    const { 
      codigo_cliente,
      valor,
      milhas_utilizadas,
      quantidade_poltronas,
      codigo_voo,
      codigo_aeroporto_origem,
      codigo_aeroporto_destino
    } = req.body;

    // Validate required fields
    if (!codigo_cliente || !valor || !quantidade_poltronas || !codigo_voo || 
        !codigo_aeroporto_origem || !codigo_aeroporto_destino) {
      return res.status(400).json({
        error: 'Missing required fields',
        message: 'All fields are required except milhas_utilizadas'
      });
    }

    // Validate field types and values
    if (!Number.isInteger(codigo_cliente) || codigo_cliente <= 0) {
      return res.status(400).json({
        error: 'Invalid codigo_cliente',
        message: 'codigo_cliente must be a positive integer'
      });
    }

    if (typeof valor !== 'number' || valor <= 0) {
      return res.status(400).json({
        error: 'Invalid valor',
        message: 'valor must be a positive number'
      });
    }

    if (milhas_utilizadas && (!Number.isInteger(milhas_utilizadas) || milhas_utilizadas < 0)) {
      return res.status(400).json({
        error: 'Invalid milhas_utilizadas',
        message: 'milhas_utilizadas must be a non-negative integer'
      });
    }

    if (!Number.isInteger(quantidade_poltronas) || quantidade_poltronas <= 0) {
      return res.status(400).json({
        error: 'Invalid quantidade_poltronas',
        message: 'quantidade_poltronas must be a positive integer'
      });
    }

    if (typeof codigo_voo !== 'string' || !codigo_voo.trim()) {
      return res.status(400).json({
        error: 'Invalid codigo_voo',
        message: 'codigo_voo must be a non-empty string'
      });
    }

    if (typeof codigo_aeroporto_origem !== 'string' || codigo_aeroporto_origem.length !== 3) {
      return res.status(400).json({
        error: 'Invalid codigo_aeroporto_origem',
        message: 'codigo_aeroporto_origem must be a 3-letter airport code'
      });
    }

    if (typeof codigo_aeroporto_destino !== 'string' || codigo_aeroporto_destino.length !== 3) {
      return res.status(400).json({
        error: 'Invalid codigo_aeroporto_destino',
        message: 'codigo_aeroporto_destino must be a 3-letter airport code'
      });
    }

    //TODO: Implementar a lógica para criar a reserva usando saga
    //Verificar se o codigo do cliente é o mesmo do JWT
    //Se não for, retornar 403
    //sem tokens retornar 401
    //campos invalidos retornar 400
  } catch (error) {
    console.error('Error searching flights:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
}); 

// R08 - CANCELAR RESERVA
app.delete('/reservas/{codigoReserva}', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is CLIENTE
  if (!hasUserType(token, 'CLIENTE')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type CLIENTE to access this resource'
    });
  }
  //TODO: Implementar a lógica para cancelar a reserva usando saga
  //Verificar se o codigo da reserva é o mesmo do JWT
  //Se não for, retornar 403
  //sem tokens retornar 401
  //campos invalidos retornar 400
  //se a reserva não existir retornar 404
});

// R09 - BUSCAR RESERVA
app.get('/reservas/{codigoReserva}', async (req, res) => {
  //TODO: Implementar a lógica para buscar a reserva usando saga
  //Verificar se o codigo da reserva é o mesmo do JWT
  //Se não for, retornar 403
  //sem tokens retornar 401
  //campos invalidos retornar 400
});

// R10a - FAZER CHECK-IN (CLIENTE) / R12 - Alterar estado da Reserva para Embarcado (FUNCIONARIO)
app.patch('/reservas/:codigoReserva/estado', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is CLIENTE or FUNCIONARIO
  const decodedToken = verifyToken(token);
  if (!decodedToken || (decodedToken.role !== 'CLIENTE' && decodedToken.role !== 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type CLIENTE or FUNCIONARIO to access this resource'
    });
  }

  try {
    const { estado } = req.body;
    const { codigoReserva } = req.params;

    // CLIENTE can only set CHECK-IN
    if (decodedToken.role === 'CLIENTE' && (!estado || estado !== 'CHECK-IN')) {
      return res.status(400).json({
        error: 'Invalid estado',
        message: 'CLIENTE can only set estado to "CHECK-IN"'
      });
    }

    // FUNCIONARIO can only set EMBARCADO
    if (decodedToken.role === 'FUNCIONARIO' && (!estado || estado !== 'EMBARCADO')) {
      return res.status(400).json({
        error: 'Invalid estado',
        message: 'FUNCIONARIO can only set estado to "EMBARCADO"'
      });
    }

    //TODO: Implementar a lógica para atualizar o estado da reserva usando saga
    //Verificar se o codigo de cliente no jwt é o mesmo do codigo de cliente da reserva
    //Se não for, retornar 403
    //sem tokens retornar 401
    //campos invalidos retornar 400
    //se a reserva não existir retornar 404
  } catch (error) {
    console.error('Error updating reservation state:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// R13 - CANCELAMENTO DE VOO / R14 - REALIZAR VOO
app.patch('/voos/{codigoVoo}/estado', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is FUNCIONARIO
  if (!hasUserType(token, 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type FUNCIONARIO to access this resource'
    });
  }

  try {
    const { estado } = req.body;
    const { codigoVoo } = req.params;

    // FUNCIONARIO can only set CANCELADO or REALIZADO
    if (!estado || (estado !== 'CANCELADO' && estado !== 'REALIZADO')) {
      return res.status(400).json({
        error: 'Invalid estado',
        message: 'FUNCIONARIO can only set estado to "CANCELADO" or "REALIZADO"'
      });
    }

    //TODO: Implementar a lógica para cancelar o voo usando saga
    //Verificar se o codigo do voo é o mesmo do JWT
    //Se não for, retornar 403
    //sem tokens retornar 401
    //campos invalidos retornar 400
    //se o voo não existir retornar 404
  } catch (error) {
    console.error('Error updating flight state:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

// R15a - CRIAR VOO
app.post('/voos', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is FUNCIONARIO
  if (!hasUserType(token, 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type FUNCIONARIO to access this resource'
    });
  }

  try {
    const { 
      data,
      valor_passagem,
      quantidade_poltronas_total,
      quantidade_poltronas_ocupadas,
      codigo_aeroporto_origem,
      codigo_aeroporto_destino
    } = req.body;

    // Validate required fields
    if (!data || !valor_passagem || !quantidade_poltronas_total || 
        !quantidade_poltronas_ocupadas || !codigo_aeroporto_origem || 
        !codigo_aeroporto_destino) {
      return res.status(400).json({
        error: 'Missing required fields',
        message: 'All fields are required'
      });
    }

    // Validate date format and future date
    const flightDate = new Date(data);
    if (isNaN(flightDate) || flightDate < new Date()) {
      return res.status(400).json({
        error: 'Invalid date',
        message: 'Date must be valid and in the future'
      });
    }

    // Validate numeric values
    if (typeof valor_passagem !== 'number' || valor_passagem <= 0) {
      return res.status(400).json({
        error: 'Invalid valor_passagem',
        message: 'valor_passagem must be a positive number'
      });
    }

    if (!Number.isInteger(quantidade_poltronas_total) || quantidade_poltronas_total <= 0) {
      return res.status(400).json({
        error: 'Invalid quantidade_poltronas_total',
        message: 'quantidade_poltronas_total must be a positive integer'
      });
    }

    if (!Number.isInteger(quantidade_poltronas_ocupadas) || quantidade_poltronas_ocupadas < 0) {
      return res.status(400).json({
        error: 'Invalid quantidade_poltronas_ocupadas',
        message: 'quantidade_poltronas_ocupadas must be a non-negative integer'
      });
    }

    if (quantidade_poltronas_ocupadas > quantidade_poltronas_total) {
      return res.status(400).json({
        error: 'Invalid seat numbers',
        message: 'quantidade_poltronas_ocupadas cannot be greater than quantidade_poltronas_total'
      });
    }

    // Validate airport codes (assuming 3-letter IATA codes)
    if (!/^[A-Z]{3}$/.test(codigo_aeroporto_origem) || !/^[A-Z]{3}$/.test(codigo_aeroporto_destino)) {
      return res.status(400).json({
        error: 'Invalid airport code',
        message: 'Airport codes must be 3-letter IATA codes'
      });
    }

    if (codigo_aeroporto_origem === codigo_aeroporto_destino) {
      return res.status(400).json({
        error: 'Invalid airport codes',
        message: 'Origin and destination airports must be different'
      });
    }

    //TODO: Implementar a lógica para criar o voo usando saga
    //Verificar se o codigo do voo é o mesmo do JWT
    //Se não for, retornar 403
    //sem tokens retornar 401
    //campos invalidos retornar 400

  } catch (error) {
    console.error('Error creating flight:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});

//R15b - buscar aeroportos
app.get('/aeroportos', async (req, res) => {
  //TODO: Implementar a lógica para buscar os aeroportos no ms-voo usando saga
  //sem tokens retornar 401
  //sem aeroportos retornar 204
});

// R? - BUSCAR VOO POR CODIGO
app.get('/voos/{codigoVoo}', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token is valid
  const decodedToken = verifyToken(token);
  if (!decodedToken) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'Invalid or expired token'
    });
  }

  //TODO: Implementar a lógica para buscar o voo no ms-voo usando saga
  //sem voo retornar 204
});

//R16 - BUSCAR TODOS OS FUNCIONARIOS
app.get('/funcionarios', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is FUNCIONARIO
  if (!hasUserType(token, 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type FUNCIONARIO to access this resource'
    });
  }

  //TODO: Implementar a lógica para buscar todos os funcionarios no ms-func usando saga
  //sem tokens retornar 401
  //sem funcionarios retornar 204
});

//R17 - CRIAR FUNCIONARIO
app.post('/funcionarios', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is FUNCIONARIO
  if (!hasUserType(token, 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type FUNCIONARIO to access this resource'
    });
  }

  try {
    const { cpf, email, nome, telefone, senha } = req.body;

    // Validate required fields
    if (!cpf || !email || !nome || !telefone || !senha) {
      return res.status(400).json({
        error: 'Missing required fields',
        message: 'All fields are required'
      });
    }

    // Validate CPF format (11 digits)
    if (!/^\d{11}$/.test(cpf)) {
      return res.status(400).json({
        error: 'Invalid CPF',
        message: 'CPF must be 11 digits'
      });
    }

    // Validate email format
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return res.status(400).json({
        error: 'Invalid email',
        message: 'Email must be in valid format'
      });
    }

    // Validate nome is not empty
    if (!nome.trim()) {
      return res.status(400).json({
        error: 'Invalid nome',
        message: 'Nome cannot be empty'
      });
    }

    // Validate telefone format (11 digits)
    if (!/^\d{11}$/.test(telefone)) {
      return res.status(400).json({
        error: 'Invalid telefone',
        message: 'Telefone must be 11 digits'
      });
    }

    // Validate senha is not empty
    if (!senha.trim()) {
      return res.status(400).json({
        error: 'Invalid senha',
        message: 'Senha cannot be empty'
      });
    }

    // Hash the password before sending to service
    const hashedPassword = hashPassword(senha);

    const funcionarioData = {
      cpf,
      email,
      nome,
      telefone,
      senha: hashedPassword
    };

    //TODO: Implementar a lógica para criar funcionário usando saga
    //sem tokens retornar 401
    //campos invalidos retornar 400
    //se o funcionário já existir retornar 409

  } catch (error) {
    console.error('Error creating employee:', error);
    res.status(500).json({
      error: 'Internal Server Error', 
      message: error.message
    });
  }
});

//R18 - UPDATE FUNCIONARIO
app.put('/funcionarios/{codigoFuncionario}', async (req, res) => {
  // Get token from Authorization header
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.status(401).json({
      error: 'Unauthorized',
      message: 'No authorization token provided'
    });
  }

  const token = authHeader.split(' ')[1];
  
  // Verify token and check if user type is FUNCIONARIO
  if (!hasUserType(token, 'FUNCIONARIO')) {
    return res.status(403).json({
      error: 'Forbidden',
      message: 'User must be of type FUNCIONARIO to access this resource'
    });
  }

  try {
    const { codigo, cpf, email, nome, telefone, senha } = req.body;

    // Validate required fields
    if (!codigo || !cpf || !email || !nome || !telefone || !senha) {
      return res.status(400).json({
        error: 'Missing required fields',
        message: 'All fields are required'
      });
    }

    // Validate codigo is positive integer
    if (!Number.isInteger(codigo) || codigo <= 0) {
      return res.status(400).json({
        error: 'Invalid codigo',
        message: 'Codigo must be a positive integer'
      });
    }

    // Validate CPF format (11 digits)
    if (!/^\d{11}$/.test(cpf)) {
      return res.status(400).json({
        error: 'Invalid CPF',
        message: 'CPF must be 11 digits'
      });
    }

    // Validate email format
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      return res.status(400).json({
        error: 'Invalid email',
        message: 'Email must be in valid format'
      });
    }

    // Validate nome is not empty
    if (!nome.trim()) {
      return res.status(400).json({
        error: 'Invalid nome',
        message: 'Nome cannot be empty'
      });
    }

    // Validate telefone format (11 digits)
    if (!/^\d{11}$/.test(telefone)) {
      return res.status(400).json({
        error: 'Invalid telefone',
        message: 'Telefone must be 11 digits'
      });
    }

    // Validate senha is not empty
    if (!senha.trim()) {
      return res.status(400).json({
        error: 'Invalid senha',
        message: 'Senha cannot be empty'
      });
    }

    //TODO: Implementar a lógica para atualizar o funcionário ( e ms auth se necessário) usando saga
    //Se não for, retornar 403
    //sem tokens retornar 401
    //campos invalidos retornar 400
    //se o funcionário não existir retornar 404

  } catch (error) {
    console.error('Error updating employee:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});


/* Test JWT endpoint
app.post('/test-jwt', (req, res) => {
  try {
    const { email, password, tipo } = req.body;
    
    // Validate input
    if (!email || !password || !tipo) {
      return res.status(400).json({
        error: 'Missing required fields',
        message: 'Email, password, and tipo are required'
      });
    }
    
    // Generate token
    const token = generateToken(email, tipo);
    
    // Verify the token
    const verifiedToken = verifyToken(token);
    
    // Log the verification result
    console.log('Test JWT Verification Result:', JSON.stringify(verifiedToken, null, 2));
    
    // Calculate expiration time from JWT payload
    const expirationDate = new Date(verifiedToken.exp * 1000).toLocaleString();
    
    // Return token and decoded information
    res.status(200).json({
      access_token: token,
      token_type: 'bearer',
      expires_at: expirationDate,
      decoded: {
        subject: verifiedToken.sub,
        role: verifiedToken.role,
        issuer: verifiedToken.iss,
        issued_at: new Date(verifiedToken.iat * 1000).toLocaleString(),
        expires_at: expirationDate
      }
    });
  } catch (error) {
    console.error('Error in test-jwt endpoint:', error);
    res.status(500).json({
      error: 'Internal Server Error',
      message: error.message
    });
  }
});
*/

// Define the port
const PORT = process.env.PORT || 3001;

app.listen(PORT, () => {
  console.log(`API Gateway running on port ${PORT}`);
}); 