import React, {useEffect, useState} from "react";
import "./TelaInicialFunc.css";

return (
    <div className="tela-inicial">
        <aside className="menu-lateral">
            <div>
                <div className="logo">
                    <Plane className="icone-aviao" />
                    <span className="logo-texto">DAC Aéreo</span>
                </div>
                <nav className="navegacao">
                    {["Página Inicial", "Cadastro de Voo", "Listagem de Funcionários", "Inserção de Funcionário", "Alteração de Funcionário", "Remoção de Funcionário"]}
                </nav>
                <button className="logout" onClick={() => navigate("/")}>
                    <LogOut className="icone-logout" /> Log Out
                </button>
            </div>
        </aside>

        <main className="conteudo">
            <section className="tabela-reservas">
                <table>
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Origem</th>
                            <th>Destino</th>
                            <th>Data</th>
                            <th>Hora</th>
                            <th>Status</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        {/*Manutenção de voos - inserir dados*/}
                    </tbody>
                </table>
            </section>
        </main>
    </div>
)