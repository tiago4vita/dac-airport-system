import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Login } from "./Login/Login";
import { Autocadastro } from "./AutoCadastro/Autocadastro";
import { TelaInicialCli } from "./TelaInicialCli/TelaInicialCli";
import { VerReserva } from "./VerReserva/VerReserva";
import { TelaInicialFunc } from "./TelaInicialFunc/TelaInicialFunc";
import "./App.css";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Autocadastro />} />
        <Route path="/homepageC" element={<TelaInicialCli/>} />
        <Route path="/ver-reserva/:codigo" element={<VerReserva />} />
        <Route path="/homepageF" element={<TelaInicialFunc/>} />
      </Routes>
    </Router>
  );
}

export default App;
