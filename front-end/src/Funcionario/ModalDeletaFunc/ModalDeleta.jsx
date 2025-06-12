import React from "react";
import axiosInstance from "../api/axiosInstance"; // ajuste o caminho conforme necessário
import "./ModalDeleta.css"; 
import alertaIcon from "../assets/alerta.svg";

export const ModalDeleta = ({ isOpen, usuarioId, onCancel, onSuccess }) => {
  if (!isOpen) return null;

  const handleDelete = async () => {
    try {
      await axiosInstance.delete(`/usuarios/${usuarioId}`);
      onSuccess();
    } catch (err) {
      console.error("Erro ao deletar usuário:", err);
      alert("Erro ao remover o usuário. Tente novamente.");
    }
  };

  return (
    <div className="overlay">
      <div className="modal-confirmacao">
        <img src={alertaIcon} alt="Alerta" />
        <h3>Tem certeza que deseja remover usuário?</h3>
        <p>O usuário será removido da lista permanentemente.</p>
        <div className="botoes">
          <button className="botao-nao" onClick={onCancel}>Não</button>
          <button className="botao-sim" onClick={handleDelete}>Sim</button>
        </div>
      </div>
    </div>
  );
};
