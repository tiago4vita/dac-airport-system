import React, { useState } from "react";
import { Plane, LogOut } from "lucide-react";
import "./FlightSCD.css";

const calcularMilhas = (valor) => valor * 0.2;

const CadastroVoo = () => {
  const [codigoVoo, setCodigoVoo] = useState("");
  const [dataHora, setDataHora] = useState("");
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [valor, setValor] = useState("");
  const [poltronas, setPoltronas] = useState("");

const aeroportos = ["GRU - São Paulo", "GIG - Rio de Janeiro", "BSB - Brasília", "CNF - Belo Horizonte"];


  return (
    <div className="tela-inicial-func">
      <main className="main-content">
        <h1>Cadastro de Voo</h1>
        <h3>Preencha os campos abaixo para criar um novo voo</h3>
        <form className="box-infos">
          <label>Código do Voo:</label>
          <input
            type="text"
            value={codigoVoo}
            onChange={(e) => setCodigoVoo(e.target.value)}
            required
          />

          <label>Data/Hora:</label>
          <input
            type="datetime-local"
            value={dataHora}
            onChange={(e) => setDataHora(e.target.value)}
            required
          />

          <label>Aeroporto Origem:</label>
          <select value={origem} onChange={(e) => setOrigem(e.target.value)} required>
            <option value="" disabled>Selecione o aeroporto de origem</option>
            {aeroportos.map((aeroporto, index) => (
              <option key={index} value={aeroporto}>{aeroporto}</option>
            ))}
          </select>

          <label>Aeroporto Destino:</label>
          <select value={destino} onChange={(e) => setDestino(e.target.value)} required>
            <option value="" disabled>Selecione o aeroporto de destino</option>
            {aeroportos.map((aeroporto, index) => (
              <option key={index} value={aeroporto}>{aeroporto}</option>
            ))}
          </select>
          <label>Valor da Passagem (R$):</label>
          <input
            type="number"
            value={valor}
            onChange={(e) => setValor(e.target.value)}
            required
          />
          <p>Equivalente em Milhas: {valor ? calcularMilhas(valor) : 0}</p>

          <div style={{ width: "100%" }}></div>
          
          <label>Quantidade de Poltronas:</label>
          <input
            type="number"
            value={poltronas}
            onChange={(e) => setPoltronas(e.target.value)}
            required
          />

          <div className="button-container">
            <button type="button" className="cancelar">
              Cancelar
            </button>
            <button type="submit" className="cadastrar">
              Cadastrar Voo
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};

export { CadastroVoo };
