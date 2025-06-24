// api-gateway/src/services/rabbitMQService.js
const amqp = require('amqplib');

const EXCHANGE = 'auth.exchange';
const QUEUE    = 'auth.cadastrar.queue';
const ROUTING  = 'cadastrar';

let channel;

async function connect() {
  if (channel) return channel;
  const rabbitUrl = process.env.RABBITMQ_URL || 'amqp://guest:guest@rabbitmq:5672';
  const conn = await amqp.connect(rabbitUrl);
  channel = await conn.createChannel();

  // 1) garante exchange
  await channel.assertExchange(EXCHANGE, 'direct', { durable: true });
  // 2) garante fila
  await channel.assertQueue   (QUEUE,    { durable: true });
  // 3) conecta exchange→fila
  await channel.bindQueue     (QUEUE, EXCHANGE, ROUTING);

  console.log('RabbitMQ conectado e exchange/queue criados');
  return channel;
}

async function enviarCadastro(payload) {
  const ch  = await connect();
  const msg = JSON.stringify(payload);
  console.log('Enviando cadastro para auth:', msg);
  // publica no exchange, não diret- na fila
  ch.publish(EXCHANGE, ROUTING, Buffer.from(msg), { persistent: true });
}

module.exports = { enviarCadastro };
