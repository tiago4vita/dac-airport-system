import React, { useState, useEffect } from "react";
import axios from "axios";
import "./AlterarFunc.css";

const AlterarFunc = ({ funcionario, onClose, onSuccess }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    status: "ATIVO"
  });

  // Initialize form with funcionario data
  useEffect(() => {
    if (funcionario) {
      setForm({
        nome: funcionario.nome || "",
        cpf: funcionario.cpf || "",
        email: funcionario.email || "",
        telefone: funcionario.telefone || "",
        status: funcionario.status || "ATIVO"
      });
    }
  }, [funcionario]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateForm = () => {
    // Basic validation
    if (!form.nome.trim()) return "Nome é obrigatório";
    if (!form.cpf.trim()) return "CPF é obrigatório";
    if (!form.cpf.match(/^\d{11}$/)) return "CPF deve conter 11 números";
    if (!form.email.trim()) return "Email é obrigatório";
    if (!form.email.includes("@")) return "Email inválido";
    if (!form.telefone.trim()) return "Telefone é obrigatório";
    
    return null; // No errors
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validate form
    const validationError = validateForm();
    if (validationError) {
      alert(validationError);
      return;
    }

    try {
      setLoading(true);
      const funcionarioId = funcionario.id || funcionario.codigo;
      
      // Try with codigo first
      await axios.put(`http://localhost:8080/funcionarios/${funcionarioId}`, form);
      
      alert("Funcionário atualizado com sucesso!");
      onSuccess(); // Notify parent component about success
    } catch (err) {
      // Try with id as fallback if código fails
      try {
        const altId = funcionario.codigo || funcionario.id;
        await axios.put(`http://localhost:8080/funcionarios/${altId}`, form);
        alert("Funcionário atualizado com sucesso!");
        onSuccess();
      } catch (idErr) {
        console.error("Erro ao atualizar funcionário:", err);
        setError("Erro ao atualizar funcionário: " + (err.response?.data?.message || err.message));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Alterar Funcionário</h2>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nome" className="label">Nome*</label>
            <input 
              id="nome" 
              name="nome" 
              value={form.nome} 
              onChange={handleChange} 
              required 
              className="input"
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="cpf" className="label">CPF*</label>
            <input 
              id="cpf" 
              name="cpf" 
              value={form.cpf} 
              onChange={handleChange} 
              required 
              pattern="\d{11}" 
              title="Digite 11 números do CPF" 
              className="input"
              disabled // CPF shouldn't be editable
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="email" className="label">E-mail*</label>
            <input 
              id="email" 
              name="email" 
              type="email" 
              value={form.email} 
              onChange={handleChange} 
              required 
              className="input"
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="telefone" className="label">Telefone*</label>
            <input 
              id="telefone" 
              name="telefone" 
              value={form.telefone} 
              onChange={handleChange} 
              required 
              className="input"
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="status" className="label">Status</label>
            <select
              id="status"
              name="status"
              value={form.status}
              onChange={handleChange}
              className="input"
              disabled={loading}
            >
              <option value="ATIVO">ATIVO</option>
              <option value="INATIVO">INATIVO</option>
            </select>
          </div>
          
          <div className="modal-buttons">
            <button 
              type="button" 
              onClick={onClose} 
              className="cancel-btn"
              disabled={loading}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              style={{ backgroundColor: '#1976d2', color: 'white' }}
              className="confirm-btn"
              disabled={loading}
            >
              {loading ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AlterarFunc;