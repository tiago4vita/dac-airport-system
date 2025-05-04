import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./EscolherVoo.css";

export const EscolherVoo = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [voos, setVoos] = useState([]);
  const [dataSelecionada, setDataSelecionada] = useState(new Date());
  const [datas, setDatas] = useState([]);
  const [paginaDatas, setPaginaDatas] = useState(0);
  const diasPorPagina = 7;

  useEffect(() => {
    fetch("http://localhost:8080/voos")
      .then((res) => res.json())
      .then((data) => {
        const origem = state?.origem?.trim();
        const destino = state?.destino?.trim();

        const filtrados = data.filter((voo) => {
          const origemOk = !origem || voo.aeroporto_origem.codigo === origem;
          const destinoOk = !destino || voo.aeroporto_destino.codigo === destino;
          return origemOk && destinoOk;
        });

        setVoos(filtrados);

        const datasUnicas = Array.from(
          new Set(filtrados.map((v) => new Date(v.data).toDateString()))
        ).map((d) => new Date(d));

        datasUnicas.sort((a, b) => a - b);
        setDatas(datasUnicas);

        if (datasUnicas.length > 0) {
          setDataSelecionada(datasUnicas[0]);
          setPaginaDatas(0);
        }
      })
      .catch((err) => {
        console.error("Erro ao buscar voos:", err);
      });
  }, [state]);

  const datasPaginadas = datas.slice(
    paginaDatas * diasPorPagina,
    (paginaDatas + 1) * diasPorPagina
  );

  const temAnterior = paginaDatas > 0;
  const temProxima = (paginaDatas + 1) * diasPorPagina < datas.length;

  const voosDoDia = voos.filter((voo) => {
    const dataVoo = new Date(voo.data);
    return dataVoo.toDateString() === dataSelecionada.toDateString();
  });

  const handleSelecionarVoo = (codigoVoo) => {
    // Verifica se o código do voo existe
    if (!codigoVoo) {
      console.error("Código do voo não encontrado");
      return;
    }
    
    // Navega para a página de reserva com o código do voo
    navigate(`/buscar-voos/escolher-voo/reservar/${codigoVoo}`);
  };

  return (
    <div className="escolher-voo">
      <button className="voltar" onClick={() => navigate(-1)}>Voltar</button>
      <h2>Escolha um voo</h2>

      <div className="datas">
        {temAnterior && (
          <button className="seta" onClick={() => setPaginaDatas(paginaDatas - 1)}>
            ◀
          </button>
        )}

        {datasPaginadas.map((d, i) => (
          <button
            key={i}
            className={`data-button ${d.toDateString() === dataSelecionada.toDateString() ? "ativa" : ""}`}
            onClick={() => setDataSelecionada(d)}
          >
            {d.toLocaleDateString("pt-BR")}
          </button>
        ))}

        {temProxima && (
          <button className="seta" onClick={() => setPaginaDatas(paginaDatas + 1)}>
            ▶
          </button>
        )}
      </div>

      <div className="lista-voos">
        {voosDoDia.map((voo, i) => (
          <div 
            className="card-voo" 
            key={i}
            onClick={() => handleSelecionarVoo(voo.codigo)}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => e.key === 'Enter' && handleSelecionarVoo(voo.codigo)}
          >
            <span><strong>{new Date(voo.data).toLocaleTimeString("pt-BR", { hour: '2-digit', minute: '2-digit' })}</strong></span>
            <span>{voo.aeroporto_origem.codigo}</span>
            <span>→</span>
            <span>{voo.aeroporto_destino.codigo}</span>
            <span>
              <strong>
                {voo.valor_passagem.toLocaleString("pt-BR", {
                  style: "currency",
                  currency: "BRL",
                })}
              </strong>
              <div>Valor por pessoa</div>
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};