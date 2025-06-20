const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const createProxyMiddleware = require('./middleware/proxy');
const { validateLoginRequest } = require('./middleware/loginValidation');
const fetch = require('node-fetch');
const { generateRandomPassword, hashPassword } = require('./utils/passwordUtils');
const { sendToQueue, connect } = require('./services/rabbitMQService');
const { generateToken, verifyToken } = require('./utils/jwtUtils');

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

// PERMISSÃO : NENHUMA
// R02a - LOGIN
app.post('/login', validateLoginRequest, async (req, res) => {
  try {
    // Create a copy of the request body
    const authRequest = { ...req.body };
    
    // Hash the password before sending to auth service
    // if (authRequest.senha) {
    //   authRequest.senha = hashPassword(authRequest.senha);
    // }
    
    const authUrl = `${process.env.ORCHESTRATOR_URL}/login`;
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

    // If login is successful (status 200), generate JWT token
    if (response.status === 200 && responseBody) {
      // Generate JWT token with user email and tipo
      const token = generateToken(responseBody.email || authRequest.email, responseBody.tipo);
      
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

// PERMISSÃO : CLIENTE
// R03 - TELA INICIAL DE CLIENTE
app.get('/clientes/:codigoCliente', async (req, res) => {
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

  //TODO: Implementar a lógica para buscar o cliente usando saga
  //Verificar se o codigo do cliente é o mesmo do JWT
  //Se não for, retornar 403
});

// R04 - LISTAR RESERVAS
app.get('/clientes/:codigoCliente/reservas', async (req, res) => {
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
  //TODO: Implementar a lógica para buscar as reservas do cliente usando saga
  //Verificar se o codigo do cliente é o mesmo do JWT
  //Se não for, retornar 403
});

// R05 - COMPRAR MILHAS
app.put('/clientes/{codigoCliente}/milhas', async (req, res) => {
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
    const { quantidade } = req.body;

    // Validate quantidade field
    if (!quantidade || !Number.isInteger(quantidade) || quantidade <= 0 || quantidade > 10000) {
      return res.status(400).json({
        error: 'Invalid quantidade',
        message: 'Quantidade must be a positive integer between 1 and 10000'
      });
    }

    //TODO: Implementar a lógica para atualizar as milhas do cliente usando saga
    //Verificar se o codigo do cliente é o mesmo do JWT
    //Se não for, retornar 403

  } catch (error) {
    console.error('Error updating client miles:', error);
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