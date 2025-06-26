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
    const altura = window.innerHeight;
    if (altura > 1000) setItensPorPagina(13);
    else if (altura > 850) setItensPorPagina(10);
    else if (altura > 700) setItensPorPagina(8);
    else if (altura > 550) setItensPorPagina(5);
    else setItensPorPagina(4);
  }, []);

  useEffect(() => {
    api.get(`/clientes/${codigoCliente}/milhas`)
      .then((res) => {
        // res.data = { saldo_milhas: number, transacoes: [...] }
        setCliente(res.data);
        setTransacoes(res.data.transacoes || []);
      })
      .catch((err) => {
        console.error("Erro ao buscar extrato de milhas:", err);
        alert("Erro ao carregar extrato.");
      });
  }, [codigoCliente]);

  const totalPaginas = Math.ceil(transacoes.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const transacoesPaginadas = transacoes.slice(inicio, inicio + itensPorPagina);

  return (
    <>
      {/* Passa o saldo direto do campo do JSON */}
      <SaldoMilhas saldo={cliente?.saldo_milhas ?? 0} />

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
            {transacoesPaginadas.map((t, i) => (
              <tr key={i}>
                <td style={{
                  color: t.tipo === "SAIDA" ? "red" : "green",
                  fontWeight: "bold"
                }}>
                  {t.tipo}
                </td>
                <td>{t.codigo_reserva || "—"}</td>
                <td>{new Date(t.data).toLocaleDateString("pt-BR")}</td>
                <td>{t.quantidade_milhas}</td>
                <td>
                  {t.valor_reais.toLocaleString("pt-BR", {
                    style: "currency",
                    currency: "BRL"
                  })}
                </td>
                <td>{t.descricao ?? "-"}</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="paginacao">
          {Array.from({ length: totalPaginas }, (_, idx) => (
            <button
              key={idx}
              onClick={() => setPaginaAtual(idx + 1)}
              className={paginaAtual === idx + 1 ? "ativa" : ""}
            >
              {idx + 1}
            </button>
          ))}
        </div>
      </section>
    </>
  );
};
