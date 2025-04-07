import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { Login } from './Login';
import { CadastroCliente }  from './SignUp';
import { CadastroVoo } from './FlightSCD';
import reportWebVitals from './reportWebVitals';
import { TelaInicialCli } from './TelaInicialCli';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />  {/* Aqui vai o roteador com Login e Cadastro */}
  </React.StrictMode>
);

reportWebVitals();
