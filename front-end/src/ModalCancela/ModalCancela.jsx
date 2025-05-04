import React from "react";
import "./ModalCancela.css"; 
import alertaIcon from "../assets/alerta.svg";

export const ModalCancela = ({ isOpen, onConfirm, onCancel }) => {
  if (!isOpen) return null;

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja cancelar a reserva?</h3>
        <p>Ao cancelar a reserva você perderá seu assento e suas milhas retornarão à sua carteira</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={onConfirm}>Sim</button>
        </div>
      </div>
    </div>
  );
};
