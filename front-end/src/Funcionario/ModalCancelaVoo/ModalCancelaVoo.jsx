import React from "react";
import axiosInstance from "../api/axiosInstance"; 
import "./ModalCancelaVoo.css";
import alertaIcon from "../assets/alerta.svg";

export const ModalCancelaVoo = ({ isOpen, vooId, onCancel, onSuccess }) => {
  if (!isOpen) return null;

  const handleConfirm = async () => {
    try {
      await axiosInstance.patch(`/voos/${vooId}`, { status: "CANCELADO" }); 
      onSuccess();
    } catch (err) {
      console.error("Erro ao cancelar voo:", err);
      alert("Erro ao cancelar o voo. Tente novamente.");
    }
  };

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja cancelar esse voo?</h3>
        <p>Ao cancelar esse voo, todas as reservas de voos de clientes serão canceladas!</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={handleConfirm}>Sim</button>
        </div>
      </div>
    </div>
  );
};
