import React, { useEffect, useState } from "react";
import "./Login.css";
import vector from "../assets/vector.svg";
import group4174 from "../assets/group-1000004174.png";
import { Link, useNavigate } from "react-router-dom";

export const Login = () => {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [emailError, setEmailError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    setEmail("");
    setSenha("");
  }, []);

  const handleEmailChange = (e) => {
    const value = e.target.value;
    setEmail(value);

    const regex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i;
    if (!regex.test(value)) {
      setEmailError("Digite um email válido");
    } else {
      setEmailError("");
    }
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();

    if (emailError || !email || !senha) {
      alert("Preencha todos os campos corretamente.");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/usuarios?login=${email}&senha=${senha}`
      );

      const data = await response.json();

      if (data.length > 0) {
        navigate("/homepageC");
      } else {
        alert("Login inválido. Verifique suas credenciais.");
      }
    } catch (error) {
      console.error("Erro ao fazer login:", error);
      alert("Erro ao conectar com o servidor.");
    }
  };

  return (
    <div className="login">
      <div className="content-login">
        <h1 className="text-wrapper-login">Login</h1>

        <div className="group-image-wrapper-login">
          <img
            className="group-image"
            src={group4174}
            alt="Ilustração do sistema"
          />
        </div>

        <form className="form-login" onSubmit={handleLoginSubmit}>
          <div className="form-group-login">
            <label htmlFor="email" className="label-login">Email</label>
            <input
              id="email"
              type="email"
              className="input-login filled"
              value={email}
              onChange={handleEmailChange}
              required
            />
            {emailError && <p className="error-message">{emailError}</p>}
          </div>

          <div className="form-group-login">
            <label htmlFor="senha" className="label-login">Senha</label>
            <input
              id="senha"
              type="password"
              className="input-login filled"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>

          <p className="n-o-possui-uma-conta">
            <span>Não possui uma conta? </span>
            <Link className="text-wrapper-4" to="/cadastro">Cadastre-se!</Link>
          </p>

          <button type="submit" className="button-login">Login</button>
        </form>
      </div>

      <img className="vector-bg" alt="Fundo decorativo" src={vector} />
    </div>
  );
};
