import React from "react";
import "./ModalCancelaVoo.css";
import alertaIcon from "../assets/alerta.svg";

export const ModalCancelaVoo = ({ isOpen, onConfirm, onCancel }) => {
    if (!isOpen) return null;

    return (
        <div className="overlay">
            <div className="modal-confirmacao">
                <img src={alertaIcon} alt="Alerta" />
                <h3>Tem certeza que deseja cancelar esse voo?</h3>
                <p>Ao cancelar esse voo, todas as reservas de voos de clientes serão canceladas!</p>
                <div className="botoes">
                    <button className="botao-nao" onClick={onCancel}>Não</button>
                    <button className="botao-sim" onClick={onConfirm}>Sim</button>
                </div>
            </div>
        </div>
    );
};