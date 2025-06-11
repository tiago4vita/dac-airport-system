import React from "react";
import api from "../api/axiosInstance"; 
import "./ModalCancela.css";
import alertaIcon from "../assets/alerta.svg";

export const ModalCancela = ({ isOpen, onConfirm, onCancel, reserva }) => {
  if (!isOpen) return null;

  const handleCancelar = async () => {
    try {
      await api.delete(`/reservas/${reserva.codigo}`); 
      onConfirm();
    } catch (error) {
      console.error("Erro ao cancelar reserva:", error);
      alert("Erro ao cancelar reserva.");
    }
  };

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja cancelar a reserva?</h3>
        <p>Ao cancelar a reserva você perderá seu assento e suas milhas retornarão à sua carteira</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={handleCancelar}>Sim</button>
        </div>
      </div>
    </div>
  );
};
