import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./InserirFunc.css";
import api from "../../api/axiosInstance";

export default function InserirFunc() {
  const [funcionarios, setFuncionarios] = useState([]);
  const [form, setForm] = useState({ nome: "", cpf: "", email: "", telefone: "" });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get("/funcionarios");
        setFuncionarios(data || []);
      } catch (err) {
        console.error("Erro ao buscar funcionários:", err);
      }
    })();
  }, []);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors(prev => ({ ...prev, [name]: null }));
  };

  const validateCPF = cpf => {
    const clean = cpf.replace(/\D/g, "");
    if (clean.length !== 11 || /^(\d)\1+$/.test(clean)) return false;
    let sum = 0;
    for (let i = 0; i < 9; i++) sum += parseInt(clean[i]) * (10 - i);
    let mod = sum % 11;
    if (parseInt(clean[9]) !== (mod < 2 ? 0 : 11 - mod)) return false;
    sum = 0;
    for (let i = 0; i < 10; i++) sum += parseInt(clean[i]) * (11 - i);
    mod = sum % 11;
    return parseInt(clean[10]) === (mod < 2 ? 0 : 11 - mod);
  };

  const validateBrazilianPhone = phone => {
    const clean = phone.replace(/\D/g, "");
    if (clean.length < 10 || clean.length > 11) return false;
    if (clean.length === 11 && clean.charAt(2) !== "9") return false;
    const ddd = parseInt(clean.substring(0, 2));
    return ddd >= 11 && ddd <= 99;
  };

  const validateEmail = email =>
    /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const validateForm = () => {
    const newErrors = {};
    if (!form.nome.trim()) newErrors.nome = "Nome é obrigatório";
    else if (form.nome.trim().length < 3) newErrors.nome = "Nome deve ter pelo menos 3 caracteres";

    if (!form.cpf.trim()) newErrors.cpf = "CPF é obrigatório";
    else if (!validateCPF(form.cpf)) newErrors.cpf = "CPF inválido";
    else if (funcionarios.some(f => f.cpf === form.cpf.replace(/\D/g, "")))
      newErrors.cpf = "CPF já cadastrado";

    if (!form.email.trim()) newErrors.email = "Email é obrigatório";
    else if (!validateEmail(form.email)) newErrors.email = "Email inválido";
    else if (funcionarios.some(f => f.email === form.email.trim()))
      newErrors.email = "Email já cadastrado";

    if (!form.telefone.trim()) newErrors.telefone = "Telefone é obrigatório";
    else if (!validateBrazilianPhone(form.telefone))
      newErrors.telefone = "Telefone inválido";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const formatCPF = cpf => {
    const d = cpf.replace(/\D/g, "").slice(0, 11);
    if (d.length > 9) return d.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, "$1.$2.$3-$4");
    if (d.length > 6) return d.replace(/(\d{3})(\d{3})(\d{0,3})/, "$1.$2.$3");
    if (d.length > 3) return d.replace(/(\d{3})(\d{0,3})/, "$1.$2");
    return d;
  };

  const formatPhone = phone => {
    const d = phone.replace(/\D/g, "").slice(0, 11);
    if (d.length > 10) return d.replace(/(\d{2})(\d{1})(\d{4})(\d{4})/, "($1) $2$3-$4");
    if (d.length > 6) return d.replace(/(\d{2})(\d{4})(\d{0,4})/, "($1) $2-$3");
    if (d.length > 2) return d.replace(/(\d{2})(\d{0,5})/, "($1) $2");
    return d;
  };

  const handleFormattedChange = e => {
    const { name, value } = e.target;
    if (name === "cpf") setForm(prev => ({ ...prev, cpf: formatCPF(value) }));
    else if (name === "telefone") setForm(prev => ({ ...prev, telefone: formatPhone(value) }));
    else handleChange(e);
  };

  const genPass = () => Math.floor(1000 + Math.random() * 9000).toString();

  const handleSubmit = async e => {
    e.preventDefault();
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const senhaGerada = genPass();
      const payload = {
        cpf: form.cpf.replace(/\D/g, ""),
        nome: form.nome.trim(),
        email: form.email.trim(),
        telefone: form.telefone.replace(/\D/g, ""),
        senha: senhaGerada
      };
      await api.post("/funcionarios", payload);
      alert(`Funcionário cadastrado! Senha ${senhaGerada} enviada para ${form.email}`);
      navigate("/listarfunc");
    } catch (err) {
      console.error("Erro ao cadastrar funcionário:", err);
      alert("Falha ao cadastrar. Tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="form-func">
      <div className="form-func-container">
        <h1 className="form-func-title">Adicionar Novo Funcionário</h1>
        <p className="form-func-subtitle">Preencha os dados para cadastrar um novo funcionário</p>
        <form className="form-func-form" onSubmit={handleSubmit}>
          <div className="form-func-group">
            <label className="form-func-label" htmlFor="nome">Nome*</label>
            <input
              id="nome"
              name="nome"
              className={`form-func-input ${errors.nome ? "form-func-input-error" : ""}`}
              value={form.nome}
              onChange={handleChange}
              disabled={isSubmitting}
            />
            {errors.nome && <span className="form-func-error">{errors.nome}</span>}
          </div>
          <div className="form-func-group">
            <label className="form-func-label" htmlFor="cpf">CPF*</label>
            <input
              id="cpf"
              name="cpf"
              className={`form-func-input ${errors.cpf ? "form-func-input-error" : ""}`}
              value={form.cpf}
              onChange={handleFormattedChange}
              disabled={isSubmitting}
            />
            {errors.cpf && <span className="form-func-error">{errors.cpf}</span>}
          </div>
          <div className="form-func-group">
            <label className="form-func-label" htmlFor="email">E-mail*</label>
            <input
              id="email"
              name="email"
              type="email"
              className={`form-func-input ${errors.email ? "form-func-input-error" : ""}`}
              value={form.email}
              onChange={handleChange}
              disabled={isSubmitting}
            />
            {errors.email && <span className="form-func-error">{errors.email}</span>}
          </div>
          <div className="form-func-group">
            <label className="form-func-label" htmlFor="telefone">Telefone*</label>
            <input
              id="telefone"
              name="telefone"
              className={`form-func-input ${errors.telefone ? "form-func-input-error" : ""}`}
              value={form.telefone}
              onChange={handleFormattedChange}
              disabled={isSubmitting}
            />
            {errors.telefone && <span className="form-func-error">{errors.telefone}</span>}
          </div>
          <div className="form-func-buttons">
            <button
              type="button"
              className="form-func-button cancel"
              onClick={() => navigate("/listarfunc")}
              disabled={isSubmitting}
            >Cancelar</button>
            <button
              type="submit"
              className="form-func-button submit"
              disabled={isSubmitting}
            >{isSubmitting ? "Adicionando..." : "Adicionar"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}
