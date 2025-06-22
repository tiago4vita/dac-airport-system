import React, { useState } from "react";
import "./Login.css";
import vector from "../assets/vector.svg";
import group4174 from "../assets/group-1000004174.png";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";
import axios from "axios";

export const Login = () => {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [emailError, setEmailError] = useState("");
  const [loginError, setLoginError] = useState("");
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleEmailChange = (e) => {
    const value = e.target.value;
    setEmail(value);

    const regex = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/i;
    setEmailError(!regex.test(value) ? "Digite um email válido" : "");
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();

    if (emailError || !email || !senha) {
      setLoginError("Preencha todos os campos corretamente.");
      return;
    }

    try {
      const response = await axios.post(`${process.env.REACT_APP_API_URL}/login`, {
        login: email,
        senha: senha,
      });

      const { access_token, usuario, tipo } = response.data;

      if (access_token && usuario && tipo) {
        const usuarioCompleto = {
          ...usuario,
          tipo,
          access_token,
        };

        // Let AuthContext handle sessionStorage
        login(usuarioCompleto);

        if (tipo === "CLIENTE") {
          navigate("/homepageC");
        } else if (tipo === "FUNCIONARIO") {
          navigate("/homepageF");
        } else {
          setLoginError("Tipo de usuário inválido.");
        }
      } else {
        setLoginError("Resposta inválida do servidor.");
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        setLoginError("Email ou senha incorretos.");
      } else {
        console.error("Erro ao fazer login:", error);
        setLoginError("Erro ao conectar com o servidor.");
      }
    }
  };

  return (
    <div className="login">
      <div className="content-login">
        <h1 className="text-wrapper-login">Login</h1>

        <div className="group-image-wrapper-login">
          <img className="group-image" src={group4174} alt="Ilustração do sistema" />
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

          {loginError && <p className="error-message">{loginError}</p>}

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
