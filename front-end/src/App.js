import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Login } from "./Login/Login";
import { Autocadastro } from "./AutoCadastro/Autocadastro";
import { TelaInicialCli } from "./TelaInicialCli/TelaInicialCli";
import { VerReserva } from "./VerReserva/VerReserva";
import { ConfirmacaoEmbarque } from "./ConfirmacaoEmbarque/ConfirmacaoEmbarque";
import "./App.css";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Autocadastro />} />
        <Route path="/homepageC" element={<TelaInicialCli/>} />
        <Route path="/ver-reserva/:codigo" element={<VerReserva />} />
        <Route path="/confirmar-embarque/:codigo" element={<ConfirmacaoEmbarque />} />
      </Routes>
    </Router>
  );
}

export default App;
