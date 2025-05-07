import React, { useState } from "react";
import "./InserirFunc.css"; 

// Para puxar CPF cadastrados
const cpfCadastrado = [];

export default function InserirFunc() {
  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Gera a senha aleatória de 4 dígitos
  const genPass = () => {
    return Math.floor(1000 + Math.random() * 9000).toString();
  };

  // Envio de email com a senha gerada
  const sendEmail = (email, senha) => {
    console.log(`Enviando e-mail para ${email} com a senha: ${senha}`);
    alert(`Senha enviada para ${email}: ${senha}`);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (cpfCadastrado.includes(form.cpf)) {
      alert("CPF já cadastrado!");
      return;
    }

    const senha = genPass();
    sendEmail(form.email, senha);

    cpfCadastrado.push(form.cpf);

    alert("Funcionário cadastrado com sucesso!");
    setForm({
      nome: "",
      cpf: "",
      email: "",
      telefone: "",
    });
  };

  // Limpa campos (Atualizar caso precise redirecionar para outra página)
  const handleCancel = () => {
    setForm({
      nome: "",
      cpf: "",
      email: "",
      telefone: "",
    });
  };

  return (
    <div className="container">
      <div className="card">
        <h1 className="title">Adicionar Novo Funcionário</h1>
        <form onSubmit={handleSubmit} className="form">
          <div>
            <label htmlFor="nome" className="label">Nome*</label>
            <input id="nome" name="nome" value={form.nome} onChange={handleChange} required className="input"/>
          </div>
          <div>
            <label htmlFor="cpf" className="label">CPF*</label>
            <input id="cpf" name="cpf" value={form.cpf} onChange={handleChange} required pattern="\d{11}" title="Digite 11 números do CPF" className="input"/>
          </div>
          <div>
            <label htmlFor="email" className="label">E-mail*</label>
            <input id="email" name="email" type="email" value={form.email} onChange={handleChange} required className="input"/>
          </div>
          <div>
            <label htmlFor="telefone" className="label">Telefone*</label>
            <input id="telefone" name="telefone" value={form.telefone} onChange={handleChange} required className="input"/>
          </div>
          <div className="buttons">
            <button type="button" onClick={handleCancel} className="button cancel-button">
              Cancelar
            </button>
            <button type="submit" className="button">
              Adicionar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
