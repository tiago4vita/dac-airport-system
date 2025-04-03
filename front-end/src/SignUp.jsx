import React, { useState } from 'react';
import axios from 'axios';

// Função para gerar uma senha aleatória de 4 dígitos
const gerarSenha = () => {
  return Math.floor(1000 + Math.random() * 9000); // Gera um número entre 1000 e 9999
};

// Função para enviar a senha por e-mail (simulação)
const enviarEmail = (email, senha) => {
  // Simulação de envio de e-mail
  alert(`Senha enviada para o e-mail ${email}: ${senha}`);
};

const CadastroCliente = () => {
  const [cpf, setCpf] = useState('');
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [cep, setCep] = useState('');
  const [endereco, setEndereco] = useState({
    rua: '',
    numero: '',
    complemento: '',
    cidade: '',
    estado: ''
  });
  const [milhas, setMilhas] = useState(0);
  const [senha, setSenha] = useState(null);

  const buscarEndereco = async (cep) => {
    try {
      const response = await axios.get(`https://viacep.com.br/ws/${cep}/json/`);
      if (response.data.erro) {
        alert('CEP não encontrado');
        return;
      }
      const { logradouro, complemento, localidade, uf } = response.data;
      setEndereco({
        rua: logradouro,
        numero: '',
        complemento: complemento,
        cidade: localidade,
        estado: uf
      });
    } catch (error) {
      alert('Erro ao buscar o endereço');
    }
  };

  const handleCadastro = () => {
    const senhaGerada = gerarSenha();
    setSenha(senhaGerada);

    enviarEmail(email, senhaGerada);

    alert(`Cadastro realizado com sucesso! CPF: ${cpf}, Nome: ${nome}, E-mail: ${email}, Milhas: ${milhas}`);
  };

  return (
    <div>
      <h1>Cadastro de Clientes</h1>
      <form onSubmit={(e) => { e.preventDefault(); handleCadastro(); }}>
        <div>
          <label>CPF:</label>
          <input
            type="text"
            value={cpf}
            onChange={(e) => setCpf(e.target.value)}
            placeholder="Digite seu CPF"
            required
          />
        </div>
        <div>
          <label>Nome:</label>
          <input
            type="text"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
            placeholder="Digite seu nome"
            required
          />
        </div>
        <div>
          <label>E-mail:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Digite seu e-mail"
            required
          />
        </div>
        <div>
          <label>CEP:</label>
          <input
            type="text"
            value={cep}
            onChange={(e) => setCep(e.target.value)}
            placeholder="Digite seu CEP"
            onBlur={() => buscarEndereco(cep)} 
            required
          />
        </div>
        <div>
          <label>Rua:</label>
          <input type="text" value={endereco.rua} disabled />
        </div>
        <div>
          <label>Número:</label>
          <input
            type="text"
            value={endereco.numero}
            onChange={(e) => setEndereco({ ...endereco, numero: e.target.value })}
            placeholder="Número"
            required
          />
        </div>
        <div>
          <label>Complemento:</label>
          <input type="text" value={endereco.complemento} disabled />
        </div>
        <div>
          <label>Cidade:</label>
          <input type="text" value={endereco.cidade} disabled />
        </div>
        <div>
          <label>Estado:</label>
          <input type="text" value={endereco.estado} disabled />
        </div>
        <div>
          <label>Milhas:</label>
          <input type="number" value={milhas} disabled />
        </div>
        <button type="submit">Cadastrar</button>
      </form>
    </div>
  );
};

export {CadastroCliente};