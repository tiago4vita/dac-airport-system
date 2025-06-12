import React, { useState, useEffect } from "react";
import api from "../../api/axiosInstance"; 
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
    if (!form.nome.trim()) return "Nome é obrigatório";
    if (!form.cpf.trim()) return "CPF é obrigatório";
    if (!form.cpf.match(/^\d{11}$/)) return "CPF deve conter 11 números";
    if (!form.email.trim()) return "Email é obrigatório";
    if (!form.email.includes("@")) return "Email inválido";
    if (!form.telefone.trim()) return "Telefone é obrigatório";
    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      alert(validationError);
      return;
    }

    try {
      setLoading(true);
      const funcionarioId = funcionario.id || funcionario.codigo;

      await api.put(`/funcionarios/${funcionarioId}`, form);

      alert("Funcionário atualizado com sucesso!");
      onSuccess();
    } catch (err) {
      try {
        const altId = funcionario.codigo || funcionario.id;
        await api.put(`/funcionarios/${altId}`, form);
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
    <div className="editar-func-modal">
      <div className="editar-func-container">
        <h2 className="editar-func-titulo">Editar Funcionário</h2>

        {error && <div className="editar-func-error">{error}</div>}

        <form onSubmit={handleSubmit} className="editar-func-form">
          <div className="editar-func-group">
            <label htmlFor="nome" className="editar-func-label">Nome*</label>
            <input
              id="nome"
              name="nome"
              value={form.nome}
              onChange={handleChange}
              required
              className="editar-func-input"
              disabled={loading}
            />
          </div>

          <div className="editar-func-group">
            <label htmlFor="cpf" className="editar-func-label">CPF*</label>
            <input
              id="cpf"
              name="cpf"
              value={form.cpf}
              onChange={handleChange}
              required
              pattern="\d{11}"
              title="Digite 11 números do CPF"
              className="editar-func-input"
              disabled
            />
          </div>

          <div className="editar-func-group">
            <label htmlFor="email" className="editar-func-label">E-mail*</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              required
              className="editar-func-input"
              disabled={loading}
            />
          </div>

          <div className="editar-func-group">
            <label htmlFor="telefone" className="editar-func-label">Telefone*</label>
            <input
              id="telefone"
              name="telefone"
              value={form.telefone}
              onChange={handleChange}
              required
              className="editar-func-input"
              disabled={loading}
            />
          </div>

          <div className="editar-func-group">
            <label htmlFor="status" className="editar-func-label">Status</label>
            <select
              id="status"
              name="status"
              value={form.status}
              onChange={handleChange}
              className="editar-func-input"
              disabled={loading}
            >
              <option value="ATIVO">ATIVO</option>
              <option value="INATIVO">INATIVO</option>
            </select>
          </div>

          <div className="editar-func-buttons">
            <button
              type="button"
              className="editar-func-cancelar"
              onClick={onClose}
              disabled={loading}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="editar-func-salvar"
              disabled={loading}
            >
              {loading ? "Salvando..." : "Salvar Alterações"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AlterarFunc;
