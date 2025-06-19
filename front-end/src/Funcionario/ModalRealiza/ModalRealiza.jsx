import React from "react";
import axiosInstance from "../../api/axiosInstance";
import "./ModalRealiza.css";
import alertaIcon from "../../assets/alerta.svg";

export const ModalRealiza = ({ isOpen, vooCodigo, onCancel, onSuccess }) => {
  if (!isOpen) return null;

  const handleConfirmar = async () => {
    try {
      await axiosInstance.patch(`/voos/${vooCodigo}/estado`, { estado: "REALIZADO" });
      onSuccess(); 
    } catch (error) {
      console.error("Erro ao confirmar voo:", error);
      alert("Erro ao confirmar o voo. Tente novamente.");
    }
  };

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Deseja confirmar a ocorrência deste voo?</h3>
        <p>Ao confirmar o voo, todos os passageiros que ainda não embarcaram perderão sua reserva.</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={handleConfirmar}>Sim</button>
        </div>
      </div>
    </div>
  );
};
