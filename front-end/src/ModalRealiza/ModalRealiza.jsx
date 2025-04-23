import React from "react";
import "./ModalRealiza.css"
import alertaIcon from "../assets/alerta.svg";

export const ModalRealiza = ({ isOpen, onConfirm, onCancel }) => {
    if(!isOpen) return null;

    return (
        <div className="overlay">
            <div className="modal-confirmacao">
                <img src={alertaIcon} alt="Alerta" />
                <h3>Deseja confirmar a ocorrência deste voo?</h3>
                <div className="botoes">
                    <button className="botao-nao" onClick={onCancel}>Não</button>
                    <button className="botao-sim" onClick={onConfirm}>Sim</button>
                </div>
            </div>
        </div>
    );
};