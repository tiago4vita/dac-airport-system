// BuscarVoos.jsx
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./BuscarVoos.css";

export const BuscarVoos = () => {
  const navigate = useNavigate();

  const [aeroportos, setAeroportos] = useState([]);
  const [origem, setOrigem] = useState("");
  const [destino, setDestino] = useState("");
  const [sugestoesOrigem, setSugestoesOrigem] = useState([]);
  const [sugestoesDestino, setSugestoesDestino] = useState([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    const token = sessionStorage.getItem("token");
      axios.get("http://localhost:8080/aeroportos", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
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

  const extrairCodigo = (texto) => texto.match(/\((.*?)\)/)?.[1]?.trim().toUpperCase() || "";

  const handleBuscar = async () => {
    const origemCodigo = extrairCodigo(origem);
    const destinoCodigo = extrairCodigo(destino);

    try {
      const params = new URLSearchParams();
      if (origemCodigo) params.set("origem", origemCodigo);
      if (destinoCodigo) params.set("destino", destinoCodigo);

      let url = "http://localhost:8080/voos";
      if (params.toString()) {
        url += `?${params.toString()}`;
      }

      const res = await axios.get(url);
      const voos = res.data;

      if (!voos.length) {
        setErro("Nenhum voo disponível para essa rota.");
        return;
      }

      setErro("");
      navigate("/buscar-voos/escolher-voo", {
        state: { voosFiltrados: voos },
      });
    } catch (error) {
      console.error("Erro ao buscar voos:", error);
      setErro("Erro ao buscar voos. Tente novamente.");
    }
  };


  return (
    <div className="buscar-voos">
      <h2>Onde gostaria de ir?</h2>
      <div className="campos-busca">
        <div className="inputs-linha">
          <div className="campo">
            <input
              type="text"
              placeholder="Origem"
              value={origem}
              onChange={handleOrigemChange}
              className="campo-texto"
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

          <span className="seta">→</span>

          <div className="campo">
            <input
              type="text"
              placeholder="Destino"
              value={destino}
              onChange={handleDestinoChange}
              className="campo-texto"
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

        <div className="botoes-linha">
          <button className="botao-buscar" onClick={handleBuscar}>Buscar</button>
        </div>
      </div>

      {erro && <p className="erro-mensagem">{erro}</p>}
    </div>
  );
};
