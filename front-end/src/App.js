import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Login } from "./Login/Login";
import { Autocadastro } from "./AutoCadastro/Autocadastro";
import { TelaInicialCli } from "./TelaInicialCli/TelaInicialCli";
import { VerReserva } from "./VerReserva/VerReserva";
import { TelaInicialFunc } from "./TelaInicialFunc/TelaInicialFunc";
import { ConfirmacaoEmbarque } from "./ConfirmacaoEmbarque/ConfirmacaoEmbarque";
import { BuscarVoos } from "./BuscarVoos/BuscarVoos";
import { SideMenuCliente } from "./SideMenuCliente/SideMenuCliente";
import { EscolherVoo } from "./EscolherVoo/EscolherVoo";
import { Reservar } from "./Reservar/Reservar";
import "./App.css";

function App() {
  return (
    <Router>
      <Routes>
        {/* Rotas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Autocadastro />} />
        <Route path="/confirmar-embarque/:codigo" element={<ConfirmacaoEmbarque />} />


        {/* Rotas com menu lateral (SideMenuCliente) */}
        <Route element={<SideMenuCliente />}>
          <Route path="/homepageC" element={<TelaInicialCli />} />
          <Route path="/homepageC/ver-reserva/:codigo" element={<VerReserva />} />
          <Route path="/buscar-voos" element={<BuscarVoos />} />
          <Route path="/buscar-voos/escolher-voo" element={<EscolherVoo />} />
          <Route path="buscar-voos/escolher-voo/reservar/:codigo" element={<Reservar />} />

        </Route>
      </Routes>
    </Router>
  );
}

export default App;
