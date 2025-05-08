import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./InserirFunc.css"; 
import axios from "axios";

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

  // Fetch existing funcionarios to check for duplicates and get next codigo
  useEffect(() => {
    const fetchFuncionarios = async () => {
      try {
        const response = await axios.get('http://localhost:8080/funcionarios');
        setFuncionarios(response.data || []);
      } catch (error) {
        console.error('Erro ao buscar funcionários:', error);
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
    
    // Clear error when user types
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  // CPF validation
  const validateCPF = (cpf) => {
    // Remove any non-digit character
    cpf = cpf.replace(/\D/g, '');
    
    if (cpf.length !== 11) return false;
    
    // Check if all digits are the same
    if (/^(\d)\1+$/.test(cpf)) return false;
    
    // Validate first check digit
    let sum = 0;
    for (let i = 0; i < 9; i++) {
      sum += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let mod = sum % 11;
    let checkDigit1 = mod < 2 ? 0 : 11 - mod;
    
    if (parseInt(cpf.charAt(9)) !== checkDigit1) return false;
    
    // Validate second check digit
    sum = 0;
    for (let i = 0; i < 10; i++) {
      sum += parseInt(cpf.charAt(i)) * (11 - i);
    }
    mod = sum % 11;
    let checkDigit2 = mod < 2 ? 0 : 11 - mod;
    
    return parseInt(cpf.charAt(10)) === checkDigit2;
  };

  // Brazilian phone validation
  const validateBrazilianPhone = (phone) => {
    // Remove all non-digit characters
    const cleanPhone = phone.replace(/\D/g, '');
    
    // Brazilian phone numbers should have 10 or 11 digits
    // 10 digits: (XX) XXXX-XXXX (landline or old mobile)
    // 11 digits: (XX) 9XXXX-XXXX (mobile with leading 9)
    if (cleanPhone.length < 10 || cleanPhone.length > 11) {
      return false;
    }
    
    // For 11 digits, the 3rd digit should be 9 (for mobile numbers)
    if (cleanPhone.length === 11 && cleanPhone.charAt(2) !== '9') {
      return false;
    }
    
    // DDD (area code) should be valid (between 11 and 99)
    const ddd = parseInt(cleanPhone.substring(0, 2));
    if (ddd < 11 || ddd > 99) {
      return false;
    }
    
    return true;
  };

  // Email validation
  const validateEmail = (email) => {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  };

  // Form validation
  const validateForm = () => {
    const newErrors = {};
    
    if (!form.nome.trim()) {
      newErrors.nome = "Nome é obrigatório";
    } else if (form.nome.trim().length < 3) {
      newErrors.nome = "Nome deve ter pelo menos 3 caracteres";
    }
    
    if (!form.cpf.trim()) {
      newErrors.cpf = "CPF é obrigatório";
    } else if (!validateCPF(form.cpf)) {
      newErrors.cpf = "CPF inválido";
    } else if (funcionarios.some(func => func.cpf === form.cpf.replace(/\D/g, ''))) {
      newErrors.cpf = "CPF já cadastrado";
    }
    
    if (!form.email.trim()) {
      newErrors.email = "Email é obrigatório";
    } else if (!validateEmail(form.email)) {
      newErrors.email = "Email inválido";
    } else if (funcionarios.some(func => func.email === form.email)) {
      newErrors.email = "Email já cadastrado";
    }
    
    if (!form.telefone.trim()) {
      newErrors.telefone = "Telefone é obrigatório";
    } else if (!validateBrazilianPhone(form.telefone)) {
      newErrors.telefone = "Telefone inválido, use o formato (XX) XXXXX-XXXX";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Format CPF as it's typed
  const formatCPF = (cpf) => {
    cpf = cpf.replace(/\D/g, '');
    if (cpf.length > 11) cpf = cpf.substring(0, 11);
    
    if (cpf.length > 9) {
      cpf = cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
    } else if (cpf.length > 6) {
      cpf = cpf.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
    } else if (cpf.length > 3) {
      cpf = cpf.replace(/(\d{3})(\d{1,3})/, '$1.$2');
    }
    
    return cpf;
  };

  // Format phone number as it's typed (Brazilian format)
  const formatPhone = (phone) => {
    phone = phone.replace(/\D/g, '');
    if (phone.length > 11) phone = phone.substring(0, 11);
    
    if (phone.length > 10) {
      // 11 digits mobile: (XX) 9XXXX-XXXX
      phone = phone.replace(/(\d{2})(\d{1})(\d{4})(\d{4})/, '($1) $2$3-$4');
    } else if (phone.length > 6) {
      // 10 digits landline: (XX) XXXX-XXXX 
      phone = phone.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
    } else if (phone.length > 2) {
      // Just the area code: (XX)
      phone = phone.replace(/(\d{2})(\d{0,5})/, '($1) $2');
    }
    
    return phone;
  };

  // Handle input formatting
  const handleFormattedChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'cpf') {
      setForm(prev => ({
        ...prev,
        cpf: formatCPF(value)
      }));
    } else if (name === 'telefone') {
      setForm(prev => ({
        ...prev,
        telefone: formatPhone(value)
      }));
    } else {
      handleChange(e);
    }
  };

  // Gera a senha aleatória de 4 dígitos
  const genPass = () => {
    return Math.floor(1000 + Math.random() * 9000).toString();
  };

  // Envio de email com a senha gerada
  const sendEmail = (email, senha) => {
    console.log(`Enviando e-mail para ${email} com a senha: ${senha}`);
    return new Promise(resolve => {
      setTimeout(() => {
        resolve();
      }, 1000);
    });
  };

  // Get next codigo (auto-incremental)
  const getNextCodigo = () => {
    if (!funcionarios.length) return 1;
    
    const maxCodigo = funcionarios.reduce((max, func) => {
      const codigo = func.codigo ? parseInt(func.codigo) : 0;
      return codigo > max ? codigo : max;
    }, 0);
    
    return maxCodigo + 1;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setIsSubmitting(true);
      const senha = genPass();
      
      // Prepare funcionario object
      const newFuncionario = {
        codigo: getNextCodigo(),
        nome: form.nome,
        cpf: form.cpf.replace(/\D/g, ''),
        email: form.email,
        telefone: form.telefone.replace(/\D/g, ''),
        senha: senha,
        status: "ATIVO"
      };
      
      // Post to the API
      await axios.post('http://localhost:8080/funcionarios', newFuncionario);
      
      // Send email with password
      await sendEmail(form.email, senha);
      
      alert(`Funcionário cadastrado com sucesso! Senha enviada para ${form.email}: ${senha}`);
      
      // Reset form
      setForm({
        nome: "",
        cpf: "",
        email: "",
        telefone: "",
      });
      
      // Navigate back to list
      navigate("/listarfunc");
    } catch (error) {
      console.error("Erro ao cadastrar funcionário:", error);
      alert("Erro ao cadastrar funcionário. Tente novamente.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // Limpa campos e redireciona
  const handleCancel = () => {
    setForm({
      nome: "",
      cpf: "",
      email: "",
      telefone: "",
    });
    navigate("/listarfunc");
  };

  return (
    <div className="container">
      <div className="card">
        <h1 className="title">Adicionar Novo Funcionário</h1>
        <p className="subtitle">Preencha os dados para cadastrar um novo funcionário no sistema</p>
        
        <form onSubmit={handleSubmit} className="form">
          <div className="form-group">
            <label htmlFor="nome" className="label">Nome*</label>
            <input 
              id="nome" 
              name="nome" 
              value={form.nome} 
              onChange={handleChange} 
              required 
              className={`input ${errors.nome ? 'input-error' : ''}`}
              placeholder="Nome completo"
              disabled={isSubmitting}
            />
            {errors.nome && <span className="error-message">{errors.nome}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="cpf" className="label">CPF*</label>
            <input 
              id="cpf" 
              name="cpf" 
              value={form.cpf} 
              onChange={handleFormattedChange} 
              required 
              className={`input ${errors.cpf ? 'input-error' : ''}`}
              placeholder="000.000.000-00"
              disabled={isSubmitting}
            />
            {errors.cpf && <span className="error-message">{errors.cpf}</span>}
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
              className={`input ${errors.email ? 'input-error' : ''}`}
              placeholder="email@exemplo.com"
              disabled={isSubmitting}
            />
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>
          
          <div className="form-group">
            <label htmlFor="telefone" className="label">Telefone*</label>
            <input 
              id="telefone" 
              name="telefone" 
              value={form.telefone} 
              onChange={handleFormattedChange} 
              required 
              className={`input ${errors.telefone ? 'input-error' : ''}`}
              placeholder="(00) 00000-0000"
              disabled={isSubmitting}
            />
            {errors.telefone && <span className="error-message">{errors.telefone}</span>}
          </div>
          
          <div className="buttons">
            <button 
              type="button" 
              onClick={handleCancel} 
              className="button cancel-button"
              disabled={isSubmitting}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              className="button submit-button"
              disabled={isSubmitting}
            >
              {isSubmitting ? "Adicionando..." : "Adicionar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
