import React from "react";
import "./ModalDeleta.css"; 
import alertaIcon from "../assets/alerta.svg";

export const ModalDeleta = ({ isOpen, onConfirm, onCancel }) => {
  if (!isOpen) return null;

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja remover usuário?</h3>
        <p>O usuário será removido da lista permanentemente.</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={onConfirm}>Sim</button>
        </div>
      </div>
    </div>
  );
};
