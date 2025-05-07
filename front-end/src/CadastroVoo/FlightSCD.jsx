import React, { useState } from "react";
import { Plane, LogOut } from "lucide-react";
import "./FlightSCD.css";

const calcularMilhas = (valor) => valor * 100;

const CadastroVoo = () => {
  const [codigoVoo, setCodigoVoo] = useState("");
  const [dataHora, setDataHora] = useState("");
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [valor, setValor] = useState("");
  const [poltronas, setPoltronas] = useState("");

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
          <input
            type="text"
            value={origem}
            onChange={(e) => setOrigem(e.target.value)}
            required
          />

          <label>Aeroporto Destino:</label>
          <input
            type="text"
            value={destino}
            onChange={(e) => setDestino(e.target.value)}
            required
          />

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
