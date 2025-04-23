import React, { useState } from "react";
import { Plane, LogOut } from "lucide-react"
import "./ConfirmacaoEmbarque.css";
import { useNavigate } from "react-router-dom";

export const ConfirmacaoEmbarque = () => {
    const [reservas, setReservas] = useState([]);
    const [reservaSelecionada, setReservaSelecionada] = useState(null);
    const navigate = useNavigate();


    return (
        <div className="tela-inicial">
            <aside className="menu-lateral">
                <div>
                    <div className="logo">
                        <Plane className="icone-aviao" />
                        <span className="logo-texto">DAC Aéreo</span>
                    </div>
                    <nav className="navegacao">
                        {["Página Inicial", "Cadastro de Voo", "Listagem de Funcionários", "Inserção de Funcionário", "Alteração de Funcionário", "Remoção de Funcionário"].map(
                                (item, index) => (
                                    <button
                                    key={index}
                                    className={`menu-item ${index === 0 ? "ativo" : ""}`}
                                    >
                                        {item}
                                    </button>
                                )
                            )}
                    </nav>
                </div>
                <button className="logout" onClick={() => navigate("/")}>
                    <LogOut className="icone-logout" /> Log Out
                </button>
            </aside>

            <main className="conteudo">
                {/*Inserir elementos, como input de texto para confirmação. Faria isso agora mas é muito tempo para agora.*/}
            </main>
        </div>
    )
}