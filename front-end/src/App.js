import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Login } from "./Login/Login";
import { Autocadastro } from "./AutoCadastro/Autocadastro";
import { TelaInicialCli } from "./Cliente/TelaInicialCli/TelaInicialCli";
import { VerReserva } from "./Cliente/VerReserva/VerReserva";
import { TelaInicialFunc } from "./Funcionario/TelaInicialFunc/TelaInicialFunc";
import { ConfirmacaoEmbarque } from "./Funcionario/ConfirmacaoEmbarque/ConfirmacaoEmbarque";
import { BuscarVoos } from "./Cliente/BuscarVoos/BuscarVoos";
import { SideMenuCliente } from "./Cliente/SideMenuCliente/SideMenuCliente";
import { SideMenuFunc } from "./Funcionario/SideMenuFunc/SideMenuFunc";
import { EscolherVoo } from "./Cliente/EscolherVoo/EscolherVoo";
import { Reservar } from "./Cliente/Reservar/Reservar";
import { Consulta } from "./Cliente/ConsultarReserva/ConsultarReserva";
import { ComprarMilhas } from "./Cliente/ComprarMilhas/ComprarMilhas";
import { Checkin } from "./Cliente/Checkin/Checkin";
import { Extrato } from "./Cliente/ExtratoMilhas/ExtratoMilhas";
import InserirFunc from "./Funcionario/InserirFunc/InserirFunc";
import ListarFunc from "./Funcionario/ListarFunc/ListarFunc";
import { CadastroVoo } from './Funcionario/CadastroVoo/FlightSCD';
import AlterarFunc from "./Funcionario/AlterarFunc/AlterarFunc";
import { AuthProvider } from "./AuthContext";
import { PrivateRoute } from "./PrivateRoute";
import "./App.css";

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Públicas */}
          <Route path="/" element={<Login />} />
          <Route path="/cadastro" element={<Autocadastro />} />

          {/* Cliente */}
          <Route
            element={
              <PrivateRoute allowedRoles={["CLIENTE"]}>
                <SideMenuCliente />
              </PrivateRoute>
            }
          >
            <Route path="/homepageC" element={<TelaInicialCli />} />
            <Route path="/homepageC/ver-reserva/:codigo" element={<VerReserva />} />
            <Route path="/buscar-voos" element={<BuscarVoos />} />
            <Route path="/buscar-voos/escolher-voo" element={<EscolherVoo />} />
            <Route path="/buscar-voos/escolher-voo/reservar/:codigo" element={<Reservar />} />
            <Route path="/consulta" element={<Consulta />} />
            <Route path="/compra" element={<ComprarMilhas />} />
            <Route path="/checkin" element={<Checkin />} />
            <Route path="/extrato" element={<Extrato />} />
          </Route>

          {/* Funcionário */}
          <Route
            element={
              <PrivateRoute allowedRoles={["FUNCIONARIO"]}>
                <SideMenuFunc />
              </PrivateRoute>
            }
          >
            <Route path="/homepageF" element={<TelaInicialFunc />} />
            <Route path="/inserirfunc" element={<InserirFunc />} />
            <Route path="/alterarfunc" element={<AlterarFunc />} />
            <Route path="/cadastrovoo" element={<CadastroVoo />} />
            <Route path="/confirmar-embarque/:codigo" element={<ConfirmacaoEmbarque />} />
          </Route>

          {/* Func sem menu */}
          <Route
            path="/listarfunc"
            element={
              <PrivateRoute allowedRoles={["FUNCIONARIO"]}>
                <ListarFunc />
              </PrivateRoute>
            }
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
