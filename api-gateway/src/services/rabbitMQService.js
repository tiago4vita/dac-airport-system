const amqp = require('amqplib');

let connection = null;
let channel = null;

/**
 * Connect to RabbitMQ server
 */
async function connect() {
  try {
    const rabbitUrl = process.env.RABBITMQ_URL || 'amqp://guest:guest@rabbitmq:5672';
    connection = await amqp.connect(rabbitUrl);
    channel = await connection.createChannel();
    
    // Declare queues
    await channel.assertQueue('auth-service-queue', { durable: true });
    
    console.log('Connected to RabbitMQ');
    return channel;
  } catch (error) {
    console.error('Error connecting to RabbitMQ:', error);
    throw error;
  }
}

/**
 * Send a message to a specified queue
 * @param {string} queue - Queue name
 * @param {object} message - Message to send
 */
async function sendToQueue(queue, message) {
  try {
    if (!channel) await connect();
    
    channel.sendToQueue(queue, Buffer.from(JSON.stringify(message)), {
      persistent: true
    });
    
    console.log(`Message sent to queue ${queue}`);
  } catch (error) {
    console.error(`Error sending message to queue ${queue}:`, error);
    throw error;
  }
}

/**
 * Close RabbitMQ connection
 */
async function close() {
  try {
    if (channel) await channel.close();
    if (connection) await connection.close();
    console.log('Closed RabbitMQ connection');
  } catch (error) {
    console.error('Error closing RabbitMQ connection:', error);
  }
}

module.exports = {
  connect,
  sendToQueue,
  close
}; 