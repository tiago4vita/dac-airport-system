import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { ModalCancela } from "../ModalCancela/ModalCancela";
import { SaldoMilhas } from "../SaldoMilhas/SaldoMilhas";
import "./TelaInicialCli.css";

export const TelaInicialCli = () => {
  const [cliente, setCliente] = useState(null);
  const [reservas, setReservas] = useState([]);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina, setItensPorPagina] = useState(10);
  const [reservaSelecionada, setReservaSelecionada] = useState(null);
  const navigate = useNavigate();

  const token = sessionStorage.getItem("token");
  const codigoCliente = sessionStorage.getItem("codigo");

  const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

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

  const carregarDados = async () => {
    if (!codigoCliente || !token) return;

    try {
      const clienteRes = await api.get(`/clientes/${codigoCliente}`);
      setCliente(clienteRes.data);

      const reservasRes = await api.get(`/clientes/${codigoCliente}/reservas`);
      setReservas(reservasRes.data);
    } catch (error) {
      console.error("Erro ao carregar dados:", error);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const totalPaginas = Math.ceil(reservas.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const reservasPaginadas = reservas.slice(inicio, inicio + itensPorPagina);

  const handleCancelar = (reserva) => {
    setReservaSelecionada(reserva);
  };

  const confirmarCancelamento = async () => {
    if (!reservaSelecionada) return;

    try {
      await api.patch(`/reservas/${reservaSelecionada.codigo}/estado`, {
        estado: "CANCELADA",
      });
      carregarDados(); // atualiza lista após cancelamento
      setReservaSelecionada(null);
    } catch (error) {
      console.error("Erro ao cancelar reserva:", error);
    }
  };

  return (
    <>
      <SaldoMilhas saldo={cliente?.saldoMilhas ?? 0} />

      <section className="tabela-reservas-cliente">
        <table>
          <thead>
            <tr>
              <th>Código</th>
              <th>Origem</th>
              <th>Destino</th>
              <th>Data</th>
              <th>Hora</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {reservasPaginadas.map((reserva, index) => {
              const voo = reserva.voo;
              const dataObj = new Date(voo?.data);
              const statusClass = reserva.estado.toLowerCase().replace(/\s/g, "-");

              return (
                <tr key={index}>
                  <td>{reserva.codigo}</td>
                  <td>{voo?.aeroporto_origem?.codigo}</td>
                  <td>{voo?.aeroporto_destino?.codigo}</td>
                  <td>{dataObj.toLocaleDateString("pt-BR")}</td>
                  <td>
                    {dataObj.toLocaleTimeString("pt-BR", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </td>
                  <td>
                    <span className={`status ${statusClass}`}>
                      {reserva.estado}
                    </span>
                  </td>
                  <td>
                    <button
                      className="ver"
                      onClick={() => navigate(`ver-reserva/${reserva.codigo}`)}
                    >
                      Ver
                    </button>
                    <button
                      className="cancelar-reserva"
                      disabled={!["criada", "check-in"].includes(reserva.estado.toLowerCase())}
                      style={{
                        opacity: ["criada", "check-in"].includes(reserva.estado.toLowerCase()) ? "1" : "0.5",
                        cursor: ["criada", "check-in"].includes(reserva.estado.toLowerCase()) ? "pointer" : "not-allowed",
                      }}
                      onClick={() => handleCancelar(reserva)}
                    >
                      Cancelar
                    </button>
                  </td>
                </tr>
              );
            })}
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

      {reservaSelecionada && (
        <ModalCancela
          isOpen={!!reservaSelecionada}
          onConfirm={confirmarCancelamento}
          onCancel={() => setReservaSelecionada(null)}
        />
      )}
    </>
  );
};
