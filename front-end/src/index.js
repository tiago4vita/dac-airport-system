import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import { Login } from './Login/Login';
import { AutoCadastro }  from './AutoCadastro/Autocadastro';
import { CadastroVoo } from './CadastroVoo/FlightSCD';
import reportWebVitals from './reportWebVitals';
import { TelaInicialCli } from './TelaInicialCli/TelaInicialCli';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <CadastroVoo />  {/* Aqui vai o roteador com Login e Cadastro */}
  </React.StrictMode>
);

reportWebVitals();
