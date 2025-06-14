import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../api/axiosInstance";
import { EtiquetaFuncionario } from "../LabelFunc/LabelFunc";
import { MapPin, ArrowRight, Search } from "lucide-react";
import "./ConfirmacaoEmbarque.css";

export const ConfirmacaoEmbarque = () => {
  const { codigo } = useParams();
  const navigate = useNavigate();

  const [codigoReserva, setCodigoReserva] = useState("");
  const [reserva, setReserva] = useState(null);
  const [erro, setErro] = useState(null);

  const buscarReserva = async () => {
    try {
      const res = await api.get(`/reservas/${codigoReserva}`);
      const encontrada = res.data;

      if (!encontrada) {
        setErro("Reserva não encontrada.");
        setReserva(null);
      } else if (encontrada.voo?.codigo !== codigo) {
        setErro("Esta reserva não pertence ao voo selecionado.");
        setReserva(null);
      } else {
        setReserva(encontrada);
        setErro(null);
      }
    } catch (error) {
      setErro("Erro ao buscar reserva.");
      setReserva(null);
    }
  };

  const confirmarEmbarque = async () => {
    try {
      await api.patch(`/reservas/${reserva.codigo}/estado`, {
        estado: "EMBARCADA",
      });
      navigate("/homepageF");
    } catch (error) {
      alert("Erro ao confirmar embarque.");
      console.error(error);
    }
  };

  const voo = reserva?.voo;
  const dataVoo = new Date(voo?.data);

  return (
    <>
      <EtiquetaFuncionario />

      <main className="confirmacao-embarque-content">
        <h2>Voo #{codigo}</h2>

        <div className="busca-codigo">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            placeholder="Código da reserva (ex: XPT005)"
            value={codigoReserva}
            onChange={(e) => setCodigoReserva(e.target.value)}
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
                    {dataVoo.toLocaleDateString("pt-BR")} -{" "}
                    {dataVoo.toLocaleTimeString("pt-BR", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </span>
                </div>
                <span className={`status ${reserva.estado.toLowerCase().replace(/\s/g, "")}`}>
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
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>{voo?.aeroporto_origem?.codigo}</td>
                    <td>{voo?.aeroporto_destino?.codigo}</td>
                    <td>{dataVoo.toLocaleDateString("pt-BR")}</td>
                    <td>{dataVoo.toLocaleTimeString("pt-BR", { hour: "2-digit", minute: "2-digit" })}</td>
                  </tr>
                </tbody>
              </table>
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
              <button className="cancel-reserva-button" onClick={() => navigate("/homepageF")}>
                Voltar
              </button>
              <button className="checkin-button" onClick={confirmarEmbarque}>
                Confirmar Embarque
              </button>
            </div>
          </>
        )}
      </main>
    </>
  );
};
