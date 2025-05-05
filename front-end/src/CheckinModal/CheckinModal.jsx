import React from "react";
import "./CheckinModal.css"; 
import alertaIcon from "../assets/alerta.svg";

export const CheckinModal = ({ isOpen, onConfirm, onCancel }) => {
  if (!isOpen) return null;

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja fazer o Check-In?</h3>
        <p>Apenas confirme caso os horários e local de embarque/desembarque estejam corretos</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={onConfirm}>Sim</button>
        </div>
      </div>
    </div>
  );
};
