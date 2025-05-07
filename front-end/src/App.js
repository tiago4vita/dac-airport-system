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
import { SideMenuFunc } from "./SideMenuFunc/SideMenuFunc"
import { EscolherVoo } from "./EscolherVoo/EscolherVoo";
import { Reservar } from "./Reservar/Reservar";
import { Consulta } from "./ConsultarReserva/ConsultarReserva"
import { ComprarMilhas } from "./ComprarMilhas/ComprarMilhas";
import { Checkin } from "./Checkin/Checkin"
import { Extrato } from "./ExtratoMilhas/ExtratoMilhas"
import InserirFunc from "./InserirFunc/InserirFunc";
import "./App.css";
import { CadastroVoo } from "./CadastroVoo/FlightSCD";

function App() {
  return (
    <Router>
      <Routes>
        {/* Rotas públicas */}
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Autocadastro />} />

        {/* Cliente */}
        <Route element={<SideMenuCliente />}>
          <Route path="/homepageC" element={<TelaInicialCli />} />
          <Route path="/homepageC/ver-reserva/:codigo" element={<VerReserva />} />
          <Route path="/buscar-voos" element={<BuscarVoos />} />
          <Route path="/buscar-voos/escolher-voo" element={<EscolherVoo />} />
          <Route path="buscar-voos/escolher-voo/reservar/:codigo" element={<Reservar />} />
          <Route path="/consulta" element={<Consulta />} />
          <Route path="/compra" element={<ComprarMilhas />} />
          <Route path="/checkin" element={<Checkin />} />
          <Route path="/extrato" element={<Extrato />} />
        </Route>

        {/* Funcionário */}
        <Route element={<SideMenuFunc />}>
          <Route path="/homepageF" element={<TelaInicialFunc />} />
          <Route path="/inserirfunc" element={<InserirFunc />} />
          <Route path="/cadastrovoo" element={<CadastroVoo />} />
          <Route path="/confirmar-embarque/:codigo" element={<ConfirmacaoEmbarque />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
