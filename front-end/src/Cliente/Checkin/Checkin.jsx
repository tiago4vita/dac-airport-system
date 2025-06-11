import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CheckinModal } from "../CheckinModal/CheckinModal";
import api from "../../api/axiosInstance"; 
import "./Checkin.css";

export const Checkin = () => {
  const [reservas, setReservas] = useState([]);
  const [reservaSelecionada, setReservaSelecionada] = useState(null);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina, setItensPorPagina] = useState(10);
  const navigate = useNavigate();

  const codigoCliente = sessionStorage.getItem("codigo");

  useEffect(() => {
    const buscarReservas = async () => {
      try {
        const res = await api.get(`/clientes/${codigoCliente}/reservas`);
        const agora = new Date();

        const dentro48h = res.data.filter((reserva) => {
          const dataVoo = new Date(reserva.voo?.data);
          const em48h = (dataVoo - agora) / (1000 * 60 * 60) <= 48;
          return reserva.estado.toLowerCase() === "criada" && em48h;
        });

        setReservas(dentro48h);
      } catch (err) {
        console.error("Erro ao buscar reservas para check-in", err);
      }
    };

    buscarReservas();
  }, []);

  const totalPaginas = Math.ceil(reservas.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const reservasPaginadas = reservas.slice(inicio, inicio + itensPorPagina);

  return (
    <div className="checkin-page">
      <h2>Reservas Disponíveis Para Check-In</h2>
      <p className="subtitulo">Apenas reservas que acontecerão dentro de 48 horas estão listadas</p>

      <section className="tabela-reservas">
        <table>
          <thead>
            <tr>
              <th>CÓDIGO</th>
              <th>ORIGEM</th>
              <th>DESTINO</th>
              <th>DATA</th>
              <th>HORA</th>
              <th>STATUS</th>
              <th>AÇÕES</th>
            </tr>
          </thead>
          <tbody>
            {reservasPaginadas.map((reserva, index) => {
              const voo = reserva.voo;
              const dataObj = new Date(voo?.data);

              return (
                <tr key={index}>
                  <td>{reserva.codigo}</td>
                  <td>{voo?.aeroporto_origem?.codigo}</td>
                  <td>{voo?.aeroporto_destino?.codigo}</td>
                  <td>{dataObj.toLocaleDateString("pt-BR")}</td>
                  <td>{dataObj.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}</td>
                  <td>
                    <span className="status criada">{reserva.estado}</span>
                  </td>
                  <td>
                    <button className="checkin-btn" onClick={() => setReservaSelecionada(reserva)}>
                      Fazer Check-in
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
        <CheckinModal
          isOpen={true}
          reserva={reservaSelecionada}
          onCancel={() => setReservaSelecionada(null)}
          onConfirm={() => {
            setReservaSelecionada(null);
            navigate("/homepageC");
          }}
        />
      )}
    </div>
  );
};
