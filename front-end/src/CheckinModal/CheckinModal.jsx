import React from "react";
import axios from "axios";
import "./CheckinModal.css";
import alertaIcon from "../assets/alerta.svg";

export const CheckinModal = ({ isOpen, onConfirm, onCancel, reserva }) => {
  if (!isOpen) return null;

  const handleConfirmar = async () => {
    try {
      await axios.patch(`http://localhost:8080/reservas/${reserva.codigo}/estado`, {
        estado: "CHECK-IN"
      });
      onConfirm();
    } catch (error) {
      console.error("Erro ao fazer check-in:", error);
      alert("Erro ao realizar o check-in.");
    }
  };

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja fazer o Check-In?</h3>
        <p>Apenas confirme caso os horários e local de embarque/desembarque estejam corretos</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={handleConfirmar}>Sim</button>
        </div>
      </div>
    </div>
  );
};
