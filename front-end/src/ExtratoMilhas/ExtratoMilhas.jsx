import React, { useEffect, useState } from "react";
import axios from "axios";
import { Wallet } from "lucide-react";
import "./ExtratoMilhas.css";

export const Extrato = () => {
  const [cliente, setCliente] = useState(null);
  const [transacoes, setTransacoes] = useState([]);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina, setItensPorPagina] = useState(10);

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
    const codigoCliente = 1010;

    axios.get(`http://localhost:8080/clientes?codigo=${codigoCliente}`).then((res) => {
      setCliente(res.data[0]);
    });

    axios.get(`http://localhost:8080/transacoes?codigo_cliente=${codigoCliente}`).then((res) => {
      setTransacoes(res.data);
    });
  }, []);

  const totalPaginas = Math.ceil(transacoes.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const transacoesPaginadas = transacoes.slice(inicio, inicio + itensPorPagina);

  const formatarDescricao = (transacao) => {
    if (transacao.tipo === "ENTRADA") return "COMPRA DE MILHAS";
    return `${transacao.origem} → ${transacao.destino}`;
  };

  return (
    <>
      <section className="card-milhas-cliente">
        <Wallet className="icone-carteira" />
        <div>
          <h2>Saldo Atual</h2>
          <p>
            {(cliente?.saldoMilhas ?? 0).toLocaleString("pt-BR")}
            <span> Milhas</span>
          </p>
        </div>
      </section>

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
                <td style={{ color: transacao.tipo === "SAÍDA" ? "red" : "green", fontWeight: "bold" }}>
                  {transacao.tipo}
                </td>
                <td>{transacao.codigo_reserva || "------"}</td>
                <td>{new Date(transacao.data).toLocaleDateString("pt-BR")}</td>
                <td>{transacao.milhas}</td>
                <td>
                  {transacao.valor.toLocaleString("pt-BR", {
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
