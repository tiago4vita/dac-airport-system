import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./FlightSCD.css"; 

const calcularMilhas = (valor) => valor * 0.2;

const CadastroVoo = () => {
  const [codigoVoo, setCodigoVoo] = useState("");
  const [dataHora, setDataHora] = useState("");
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [valor, setValor] = useState("");
  const [poltronas, setPoltronas] = useState("");

  const [aeroportos, setAeroportos] = useState([]);
  const [sugestoesOrigem, setSugestoesOrigem] = useState([]);
  const [sugestoesDestino, setSugestoesDestino] = useState([]);

  const navigate = useNavigate();

  const token = sessionStorage.getItem("token");

  const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  useEffect(() => {
    api.get("/aeroportos")
      .then((res) => {
        const tratados = res.data.map((a) => ({
          nome: (a[" nome"] || a.nome || "").trim(),
          codigo: (a.codigo || "").trim().toUpperCase(),
          cidade: (a.cidade || "").trim(),
        }));
        setAeroportos(tratados);
      })
      .catch((err) => console.error("Erro ao buscar aeroportos:", err));
  }, []);

  const filtrar = (input) => {
    const termo = input.toLowerCase();
    return aeroportos.filter((a) =>
      a.nome.toLowerCase().includes(termo) ||
      a.cidade.toLowerCase().includes(termo) ||
      a.codigo.toLowerCase().includes(termo)
    );
  };

  const handleOrigemChange = (e) => {
    const value = e.target.value;
    setOrigem(value);
    setSugestoesOrigem(value ? filtrar(value) : []);
  };

  const handleDestinoChange = (e) => {
    const value = e.target.value;
    setDestino(value);
    setSugestoesDestino(value ? filtrar(value) : []);
  };

  const selecionarOrigem = (texto) => {
    setOrigem(texto);
    setSugestoesOrigem([]);
  };

  const selecionarDestino = (texto) => {
    setDestino(texto);
    setSugestoesDestino([]);
  };

  return (
    <div className="cadastro-voo">
      <div className="cadastro-voo-container">
        <h1 className="cadastro-voo-titulo">Cadastro de Voo</h1>

        <form className="cadastro-voo-form">
          <div className="cadastro-voo-linha">
            <label className="cadastro-voo-label">Código do Voo*</label>
            <input
              type="text"
              value={codigoVoo}
              onChange={(e) => setCodigoVoo(e.target.value)}
              className="cadastro-voo-input"
              required
            />
          </div>

          <div className="cadastro-voo-linha">
            <label className="cadastro-voo-label">Data/Hora*</label>
            <input
              type="datetime-local"
              value={dataHora}
              onChange={(e) => setDataHora(e.target.value)}
              className="cadastro-voo-input"
              required
            />
          </div>

          <div className="cadastro-voo-linha-dupla cadastro-voo-linha">
            <div className="cadastro-voo-col sugestao-wrapper">
              <label className="cadastro-voo-label">Aeroporto Origem*</label>
              <input
                type="text"
                value={origem}
                onChange={handleOrigemChange}
                className="cadastro-voo-input"
                placeholder="Origem"
                required
              />
              {sugestoesOrigem.length > 0 && (
                <ul className="sugestoes">
                  {sugestoesOrigem.map((a, i) => (
                    <li key={i} onClick={() => selecionarOrigem(`${a.nome} (${a.codigo})`)}>
                      {a.nome} ({a.codigo})
                    </li>
                  ))}
                </ul>
              )}
            </div>

            <div className="cadastro-voo-col sugestao-wrapper">
              <label className="cadastro-voo-label">Aeroporto Destino*</label>
              <input
                type="text"
                value={destino}
                onChange={handleDestinoChange}
                className="cadastro-voo-input"
                placeholder="Destino"
                required
              />
              {sugestoesDestino.length > 0 && (
                <ul className="sugestoes">
                  {sugestoesDestino.map((a, i) => (
                    <li key={i} onClick={() => selecionarDestino(`${a.nome} (${a.codigo})`)}>
                      {a.nome} ({a.codigo})
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>

          <div className="cadastro-voo-linha-dupla cadastro-voo-linha">
            <div className="cadastro-voo-col">
              <label className="cadastro-voo-label">Valor da Passagem (R$)*</label>
              <input
                type="number"
                value={valor}
                onChange={(e) => setValor(e.target.value)}
                className="cadastro-voo-input"
                required
              />
            </div>

            <div className="cadastro-voo-col">
              <label className="cadastro-voo-label">Milhas Calculadas</label>
              <input
                value={valor ? Math.floor(valor / 5) : 0}
                className="cadastro-voo-input cadastro-disabled"
                readOnly
              />
            </div>
          </div>

          <div className="cadastro-voo-linha">
            <label className="cadastro-voo-label">Quantidade de Poltronas*</label>
            <input
              type="number"
              value={poltronas}
              onChange={(e) => setPoltronas(e.target.value)}
              className="cadastro-voo-input"
              required
            />
          </div>

          <div className="cadastro-voo-botoes">
            <button type="button" className="cadastro-voo-cancelar" onClick={() => navigate("/")}>
              Cancelar
            </button>
            <button type="submit" className="cadastro-voo-cadastrar">
              Cadastrar Voo
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export { CadastroVoo };
