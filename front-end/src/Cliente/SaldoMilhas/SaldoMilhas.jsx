import React from "react";
import { Wallet } from "lucide-react";
import "./SaldoMilhas.css";

export const SaldoMilhas = ({ saldo }) => {
  return (
    <div className="saldo-milhas">
      <div className="esquerda">
        <Wallet />
        <h2>Saldo Atual</h2>
      </div>
      <div className="direita">
        <div className="milhas">{saldo.toLocaleString("pt-BR")}</div>
        <div className="unidade">Milhas</div>
      </div>
    </div>
  );
};
