import React, { useEffect, useState } from "react";
import axios from "axios";
import { Plane, LogOut, MapPin, ArrowRight } from "lucide-react";
import { useNavigate, useParams } from "react-router-dom";
import "./VerReserva.css";

export const VerReserva = () => {
  const { codigo } = useParams();
  const [reserva, setReserva] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    axios
      .get(`http://localhost:8080/reservas?codigo=${codigo}`)
      .then((res) => setReserva(res.data[0]));
  }, [codigo]);

  if (!reserva) return <div>Carregando...</div>;

  const voo = reserva.voo;
  const dataReserva = new Date(reserva.data);
  const dataVoo = new Date(voo?.data);

  const subtotal = voo?.valor_passagem ?? 0;
  const milhas = reserva.milhas_utilizadas ?? 0;
  const desconto = milhas * 5;
  const total = subtotal - desconto;

  return (
    <div className="ver-reserva-container">

      <aside className="menu-lateral">
        <div>
          <div className="logo">
            <Plane className="icone-aviao" />
            <span className="logo-texto">DAC Aéreo</span>
          </div>
          <nav className="navegacao">
            {["Página Inicial", "Reservar", "Consultar Reserva", "Comprar Milhas", "Extrato de Milhas", "Check-in"].map(
              (item, index) => (
                <button
                  key={index}
                  className={`menu-item ${index === 0 ? "ativo" : ""}`}
                >
                  {item}
                </button>
              )
            )}
          </nav>
        </div>
        <button className="logout" onClick={() => navigate("/")}>
          <LogOut className="icone-logout" /> Sair
        </button>
      </aside>

      <main className="ver-reserva-content">
        <div className="card reserva-info">
          <div className="reserva-header">
            <div>
              <h2>Reserva #{reserva.codigo}</h2>
              <span>{dataReserva.toLocaleDateString("pt-BR")} - {dataReserva.toLocaleTimeString("pt-BR", {
                hour: "2-digit",
                minute: "2-digit"
              })}</span>
            </div>
            <span className={`status ${reserva.estado.toLowerCase().replace(/\s|-/g, "")}`}>
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
                <td>{dataVoo.toLocaleTimeString("pt-BR", {
                  hour: "2-digit",
                  minute: "2-digit"
                })}</td>
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

        {/* Localização visual separada */}
        <div className="card flight-info">
          <div className="flight-location">
            <MapPin className="icon" />
            <div>
              <strong>{voo?.aeroporto_origem?.codigo}</strong>
              <div style={{ marginTop: "2px", color: "#64748b", fontSize: "0.875rem" }}>
                {voo?.aeroporto_origem?.nome}
              </div>
            </div>
          </div>

          <ArrowRight className="arrow-icon" />

          <div className="flight-location">
            <MapPin className="icon" />
            <div>
              <strong>{voo?.aeroporto_destino?.codigo}</strong>
              <div style={{ marginTop: "2px", color: "#64748b", fontSize: "0.875rem" }}>
                {voo?.aeroporto_destino?.nome}
              </div>
            </div>
          </div>
        </div>

        <button className="return-btn" onClick={() => navigate("/homepageC")}>
          Voltar à Página Inicial
        </button>
      </main>
    </div>
  );
};
