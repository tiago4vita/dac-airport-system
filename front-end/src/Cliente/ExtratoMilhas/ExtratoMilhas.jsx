import React, { useEffect, useState } from "react";
import api from "../../api/axiosInstance"; 
import { SaldoMilhas } from "../SaldoMilhas/SaldoMilhas";
import "./ExtratoMilhas.css";

export const Extrato = () => {
  const [cliente, setCliente] = useState(null);
  const [transacoes, setTransacoes] = useState([]);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina, setItensPorPagina] = useState(10);

  const codigoCliente = sessionStorage.getItem("codigo");

  useEffect(() => {
    const calcularItensPorAltura = () => {
      const altura = window.innerHeight;
      if (altura > 1000) return 13;
      if (altura > 850) return 10;
      if (altura > 700) return 8;
      if (altura > 550) return 5;
      return 4;
    };
    setItensPorPagina(calcularItensPorAltura());
  }, []);

  useEffect(() => {
    api.get(`/clientes/${codigoCliente}/milhas`)
      .then((res) => {
        setCliente({ saldoMilhas: res.data.saldo_milhas });
        setTransacoes(res.data.transacoes || []);
      })
      .catch((err) => {
        console.error("Erro ao buscar extrato de milhas:", err);
        alert("Erro ao carregar extrato.");
      });
  }, []);

  const totalPaginas = Math.ceil(transacoes.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const transacoesPaginadas = transacoes.slice(inicio, inicio + itensPorPagina);

  const formatarDescricao = (transacao) => {
    return transacao.descricao || "-";
  };

  return (
    <>
      <SaldoMilhas saldo={cliente?.saldoMilhas ?? 0} />

      <section className="tabela-reservas">
        <table>
          <thead>
            <tr>
              <th>Tipo</th>
              <th>Código da Reserva</th>
              <th>Data da Transação</th>
              <th>Milhas</th>
              <th>Valor (BRL)</th>
              <th>Descrição</th>
            </tr>
          </thead>
          <tbody>
            {transacoesPaginadas.map((transacao, index) => (
              <tr key={index}>
                <td style={{ color: transacao.tipo === "SAIDA" ? "red" : "green", fontWeight: "bold" }}>
                  {transacao.tipo}
                </td>
                <td>{transacao.codigo_reserva || "------"}</td>
                <td>{new Date(transacao.data).toLocaleDateString("pt-BR")}</td>
                <td>{transacao.quantidade_milhas}</td>
                <td>
                  {transacao.valor_reais.toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL"
                  })}
                </td>
                <td>{formatarDescricao(transacao)}</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="paginacao">
          {Array.from({ length: totalPaginas }, (_, i) => (
            <button
              key={i}
              onClick={() => setPaginaAtual(i + 1)}
              className={`pagina ${paginaAtual === i + 1 ? "ativa" : ""}`}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </section>
    </>
  );
};
