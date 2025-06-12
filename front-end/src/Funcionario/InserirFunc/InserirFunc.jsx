import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./InserirFunc.css";
import api from "../../api/axiosInstance"; 

export default function InserirFunc() {
  const [funcionarios, setFuncionarios] = useState([]);
  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    status: "ATIVO"
  });
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchFuncionarios = async () => {
      try {
        const response = await api.get("/funcionarios");
        setFuncionarios(response.data || []);
      } catch (error) {
        console.error("Erro ao buscar funcionários:", error);
      }
    };

    fetchFuncionarios();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const validateCPF = (cpf) => {
    cpf = cpf.replace(/\D/g, "");
    if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;
    let sum = 0;
    for (let i = 0; i < 9; i++) sum += parseInt(cpf.charAt(i)) * (10 - i);
    let mod = sum % 11;
    if (parseInt(cpf.charAt(9)) !== (mod < 2 ? 0 : 11 - mod)) return false;
    sum = 0;
    for (let i = 0; i < 10; i++) sum += parseInt(cpf.charAt(i)) * (11 - i);
    mod = sum % 11;
    return parseInt(cpf.charAt(10)) === (mod < 2 ? 0 : 11 - mod);
  };

  const validateBrazilianPhone = (phone) => {
    const clean = phone.replace(/\D/g, "");
    if (clean.length < 10 || clean.length > 11) return false;
    if (clean.length === 11 && clean.charAt(2) !== "9") return false;
    const ddd = parseInt(clean.substring(0, 2));
    return ddd >= 11 && ddd <= 99;
  };

  const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const validateForm = () => {
    const newErrors = {};
    if (!form.nome.trim()) newErrors.nome = "Nome é obrigatório";
    else if (form.nome.length < 3) newErrors.nome = "Nome deve ter pelo menos 3 caracteres";

    if (!form.cpf.trim()) newErrors.cpf = "CPF é obrigatório";
    else if (!validateCPF(form.cpf)) newErrors.cpf = "CPF inválido";
    else if (funcionarios.some((f) => f.cpf === form.cpf.replace(/\D/g, ""))) newErrors.cpf = "CPF já cadastrado";

    if (!form.email.trim()) newErrors.email = "Email é obrigatório";
    else if (!validateEmail(form.email)) newErrors.email = "Email inválido";
    else if (funcionarios.some((f) => f.email === form.email)) newErrors.email = "Email já cadastrado";

    if (!form.telefone.trim()) newErrors.telefone = "Telefone é obrigatório";
    else if (!validateBrazilianPhone(form.telefone)) newErrors.telefone = "Telefone inválido";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const formatCPF = (cpf) => {
    cpf = cpf.replace(/\D/g, "").slice(0, 11);
    if (cpf.length > 9) return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, "$1.$2.$3-$4");
    if (cpf.length > 6) return cpf.replace(/(\d{3})(\d{3})(\d{0,3})/, "$1.$2.$3");
    if (cpf.length > 3) return cpf.replace(/(\d{3})(\d{0,3})/, "$1.$2");
    return cpf;
  };

  const formatPhone = (phone) => {
    phone = phone.replace(/\D/g, "").slice(0, 11);
    if (phone.length > 10) return phone.replace(/(\d{2})(\d{1})(\d{4})(\d{4})/, "($1) $2$3-$4");
    if (phone.length > 6) return phone.replace(/(\d{2})(\d{4})(\d{0,4})/, "($1) $2-$3");
    if (phone.length > 2) return phone.replace(/(\d{2})(\d{0,5})/, "($1) $2");
    return phone;
  };

  const handleFormattedChange = (e) => {
    const { name, value } = e.target;
    if (name === "cpf") setForm((prev) => ({ ...prev, cpf: formatCPF(value) }));
    else if (name === "telefone") setForm((prev) => ({ ...prev, telefone: formatPhone(value) }));
    else handleChange(e);
  };

  const genPass = () => Math.floor(1000 + Math.random() * 9000).toString();

  const sendEmail = (email, senha) => {
    console.log(`Enviando e-mail para ${email} com a senha: ${senha}`);
    return new Promise((resolve) => setTimeout(resolve, 1000));
  };

  const getNextCodigo = () => {
    if (!funcionarios.length) return 1;
    return funcionarios.reduce((max, f) => Math.max(max, parseInt(f.codigo) || 0), 0) + 1;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      setIsSubmitting(true);
      const senha = genPass();

      const newFuncionario = {
        codigo: getNextCodigo(),
        nome: form.nome,
        cpf: form.cpf.replace(/\D/g, ""),
        email: form.email,
        telefone: form.telefone.replace(/\D/g, ""),
        senha,
        status: "ATIVO"
      };

      await api.post("/funcionarios", newFuncionario);
      await sendEmail(form.email, senha);
      alert(`Funcionário cadastrado com sucesso! Senha enviada para ${form.email}: ${senha}`);

      setForm({ nome: "", cpf: "", email: "", telefone: "" });
      navigate("/listarfunc");
    } catch (error) {
      console.error("Erro ao cadastrar funcionário:", error);
      alert("Erro ao cadastrar funcionário. Tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setForm({ nome: "", cpf: "", email: "", telefone: "" });
    navigate("/listarfunc");
  };

  return (
    <div className="form-func">
      <div className="form-func-container">
        <h1 className="form-func-title">Adicionar Novo Funcionário</h1>
        <p className="form-func-subtitle">Preencha os dados para cadastrar um novo funcionário no sistema</p>

        <form onSubmit={handleSubmit} className="form-func-form">
          <div className="form-func-group">
            <label htmlFor="nome" className="form-func-label">Nome*</label>
            <input
              id="nome"
              name="nome"
              value={form.nome}
              onChange={handleChange}
              required
              className={`form-func-input ${errors.nome ? "form-func-input-error" : ""}`}
              placeholder="Nome completo"
              disabled={isSubmitting}
            />
            {errors.nome && <span className="form-func-error">{errors.nome}</span>}
          </div>

          <div className="form-func-group">
            <label htmlFor="cpf" className="form-func-label">CPF*</label>
            <input
              id="cpf"
              name="cpf"
              value={form.cpf}
              onChange={handleFormattedChange}
              required
              className={`form-func-input ${errors.cpf ? "form-func-input-error" : ""}`}
              placeholder="000.000.000-00"
              disabled={isSubmitting}
            />
            {errors.cpf && <span className="form-func-error">{errors.cpf}</span>}
          </div>

          <div className="form-func-group">
            <label htmlFor="email" className="form-func-label">E-mail*</label>
            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              required
              className={`form-func-input ${errors.email ? "form-func-input-error" : ""}`}
              placeholder="email@exemplo.com"
              disabled={isSubmitting}
            />
            {errors.email && <span className="form-func-error">{errors.email}</span>}
          </div>

          <div className="form-func-group">
            <label htmlFor="telefone" className="form-func-label">Telefone*</label>
            <input
              id="telefone"
              name="telefone"
              value={form.telefone}
              onChange={handleFormattedChange}
              required
              className={`form-func-input ${errors.telefone ? "form-func-input-error" : ""}`}
              placeholder="(00) 00000-0000"
              disabled={isSubmitting}
            />
            {errors.telefone && <span className="form-func-error">{errors.telefone}</span>}
          </div>

          <div className="form-func-buttons">
            <button type="button" onClick={handleCancel} className="form-func-button cancel" disabled={isSubmitting}>
              Cancelar
            </button>
            <button type="submit" className="form-func-button submit" disabled={isSubmitting}>
              {isSubmitting ? "Adicionando..." : "Adicionar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
