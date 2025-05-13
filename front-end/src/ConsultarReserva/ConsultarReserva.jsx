import React, { useState } from "react";
import axios from "axios";
import { MapPin, ArrowRight, Search } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { ModalCancela } from "../ModalCancela/ModalCancela";
import { CheckinModal } from "../CheckinModal/CheckinModal";
import "./ConsultarReserva.css";

export const Consulta = () => {
  const [codigoBusca, setCodigoBusca] = useState("");
  const [reserva, setReserva] = useState(null);
  const [erro, setErro] = useState(null);
  const [reservaParaCancelar, setReservaParaCancelar] = useState(null);
  const [reservaParaCheckin, setReservaParaCheckin] = useState(null);
  const navigate = useNavigate();

  const clienteCodigo = 1010;

  const buscarReserva = async () => {
    try {
      const res = await axios.get(
        `http://localhost:8080/reservas?codigo=${codigoBusca}&codigo_cliente=${clienteCodigo}`
      );
      setReserva(res.data[0] || null);
      setErro(res.data.length === 0 ? "Reserva não encontrada." : null);
    } catch (err) {
      setErro("Erro ao buscar reserva.");
      setReserva(null);
    }
  };

  const voo = reserva?.voo;
  const dataReserva = new Date(reserva?.data);
  const dataVoo = new Date(voo?.data);
  const subtotal = voo?.valor_passagem ?? 0;
  const milhas = reserva?.milhas_utilizadas ?? 0;
  const desconto = milhas * 5;
  const total = subtotal - desconto;

  const estado = reserva?.estado?.toLowerCase();
  const podeCancelar = estado === "criada" || estado === "check-in";
  const podeFazerCheckin = estado === "criada";

  return (
    <div className="consulta-content">
    <h2>Insira o Código da Reserva que deseja consultar:</h2>

    <div className="consulta-wrapper">
      <div className="busca-codigo">
        <Search size={16} className="search-icon" />
        <input
          type="text"
          placeholder="ex: XTR945"
          value={codigoBusca}
          onChange={(e) => setCodigoBusca(e.target.value)}
        />
        <button onClick={buscarReserva}>Buscar</button>
      </div>

      {erro && <p className="erro">{erro}</p>}

      {reserva && (
        <>
          <div className="card reserva-info">
            <div className="reserva-header">
              <div>
                <h2>Reserva #{reserva.codigo}</h2>
                <span>
                  {dataReserva.toLocaleDateString("pt-BR")} - {dataReserva.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}
                </span>
              </div>
              <span className={`status ${estado?.replace(/\s|-/g, "")}`}>
                {reserva.estado}
              </span>
            </div>

            <table className="reserva-table">
              <thead>
                <tr>
                  <th>Origem</th>
                  <th>Destino</th>
                  <th>Data</th>
                  <th>Hora</th>
                  <th>Valor</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{voo?.aeroporto_origem?.codigo}</td>
                  <td>{voo?.aeroporto_destino?.codigo}</td>
                  <td>{dataVoo.toLocaleDateString("pt-BR")}</td>
                  <td>{dataVoo.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}</td>
                  <td>{subtotal.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
                </tr>
              </tbody>
            </table>

            <div className="reserva-summary">
              <div>
                <span>Subtotal</span>
                <span>{subtotal.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</span>
              </div>
              <div>
                <span>Milhas Utilizadas (-)</span>
                <span>{milhas}</span>
              </div>
              <div className="total">
                <span>Total Pago</span>
                <span>{total.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</span>
              </div>
            </div>
          </div>

          <div className="flight-info">
            <div className="flight-location">
              <MapPin className="icon" />
              <div>
                <strong>{voo?.aeroporto_origem?.codigo}</strong>
                <span>{voo?.aeroporto_origem?.nome}</span>
              </div>
            </div>

            <ArrowRight className="arrow-icon" />

            <div className="flight-location">
              <MapPin className="icon" />
              <div>
                <strong>{voo?.aeroporto_destino?.codigo}</strong>
                <span>{voo?.aeroporto_destino?.nome}</span>
              </div>
            </div>
          </div>

          <div className="acoes">
            <button
              onClick={() => setReservaParaCancelar(reserva)}
              disabled={!podeCancelar}
              className="cancel-reserva-button"
            >
              Cancelar
            </button>
            <button
              onClick={() => setReservaParaCheckin(reserva)}
              disabled={!podeFazerCheckin}
              className="checkin-button"
            >
              Fazer Checkin
            </button>
          </div>
        </>
      )}

      {reservaParaCancelar && (
        <ModalCancela
          isOpen={true}
          onConfirm={() => {
            setReservaParaCancelar(null);
            navigate("/homepageC");
          }}
          onCancel={() => setReservaParaCancelar(null)}
          reserva={reservaParaCancelar}
        />
      )}

      {reservaParaCheckin && (
        <CheckinModal
          isOpen={true}
          onConfirm={() => {
            setReservaParaCheckin(null);
            navigate("/homepageC");
          }}
          onCancel={() => setReservaParaCheckin(null)}
          reserva={reservaParaCheckin}
        />
      )}
    </div>
    </div>
  );
};