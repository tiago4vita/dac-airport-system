import React, { useState } from "react";
import "./FlightSCD.css";

const calcularMilhas = (valor) => {
  return valor * 100;
};

const CadastroVoo = () => {
  const [codigoVoo, setCodigoVoo] = useState("");
  const [dataHora, setDataHora] = useState("");
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [valor, setValor] = useState("");
  const [poltronas, setPoltronas] = useState("");
  const [estado, setEstado] = useState("CONFIRMADO");



  return (
    <div className="tela-inicial">
      <div className="menu-titulo">
        <h1>Cadastro de Voo</h1>
        </div>
      <div className="menu-subtitulo">
        <h1>Preencha os campos abaixo para criar um novo vôo</h1>
      </div>
          <div className="box-infos">
            <form>
            <label>Código do Voo:</label>
            <input type="text" value={codigoVoo} onChange={(e) => setCodigoVoo(e.target.value)} required />

            <label>Data/Hora:</label>
            <input type="datetime-local" value={dataHora} onChange={(e) => setDataHora(e.target.value)} required />

            <label>Aeroporto Origem:</label>
            <input type="text" value={origem} onChange={(e) => setOrigem(e.target.value)} required />

            <label>Aeroporto Destino:</label>
            <input type="text" value={destino} onChange={(e) => setDestino(e.target.value)} required />

            <label>Valor da Passagem (R$):</label>
            <input type="number" value={valor} onChange={(e) => setValor(e.target.value)} required />
            <p>Equivalente em Milhas: {valor ? calcularMilhas(valor) : 0}</p>

            <label>Quantidade de Poltronas:</label>
            <input type="number" value={poltronas} onChange={(e) => setPoltronas(e.target.value)} required />

            <button type="submit">Cadastrar Voo</button>
            </form>
        </div>
    </div>
  );
};

export { CadastroVoo } ;
