import React, {useEffect, useState} from "react";
import { Plane, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./TelaInicialFunc.css";

export const TelaInicialFunc = () => {
    const [funcionario, setFuncionario] = useState(null);
    const [reservas, setReservas] = useState([]);
    const [paginaAtual, setPaginaAtual] = useState(1);
    const navigate = useNavigate();
    const [itensPorPagina, setItensPorPagina] = useState(10);

    useEffect(() => {
        const calcularItensPorAltura = () => {
            const altura = window.innerHeight;
            if (altura > 1000) return 13;
            if (altura > 850) return 10;
            if (altura > 700) return 8;
            if (altura > 550) return 5;
            return 4;
        };
        setItensPorPagina(calcularItensPorAltura());
    }, []);

    useEffect(() => {
        const codigoFuncionario = 1011;
    });

    const totalPaginas = Math.ceil(reservas.length / itensPorPagina);
    const inicio = (paginaAtual - 1) * itensPorPagina;
    const reservasPaginadas = reservas.slice(inicio, inicio + itensPorPagina);

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
                            {reservasPaginadas.map((reserva, index) => {
                                const voo = reserva.voo;
                                const dataObj = new Date(voo?.data);
                                const statusClass = reserva.estado.toLowerCase().replace(/\s/g, "-");

                                return (
                                    <tr key={index}>
                                        <td>{reserva.codigo}</td>
                                        <td>{voo?.aeroporto_origem?.codigo}</td>
                                        <td>{voo?.aeroporto_destino?.codigo}</td>
                                        <td>{dataObj.toLocaleDateString("pt-BR")}</td>
                                        <td>
                                            {dataObj.toLocaleTimeString("pt-BR", {
                                                hour: "2-digit",
                                                minute: "2-digit",
                                            })}
                                        </td>
                                        <td>
                                            <span className={`status ${statusClass}`}>
                                                {reserva.estado}
                                            </span>
                                        </td>
                                        <td>
                                            {/*O botão inserido abaixo é o de confirmar embarque. O path (caminho /confirmar-embarque/) ainda foi setado, porém funções visuais ainda são necessárias.*/}
                                            <button
                                                className="confirmar"
                                                onClick={() => navigate(`/confirmar-embarque/${reserva.codigo}`)}
                                                >
                                                    Confirmar
                                            </button>
                                            {/*O botão inserido abaixo é o de cancelar embarque. As funções ainda não estão completamente implementadas, mas serão.*/}
                                            <button
                                                className="cancelar"
                                                disabled={!["confirmada"].includes(reserva.estado.toLowerCase().replace("-", ""))}
                                                style={{
                                                    opacity: ["confirmada"].includes(reserva.estado.toLowerCase().replace("-", ""))
                                                    ? "1"
                                                    : "0.5",
                                                    cursor: ["confirmada"].includes(reserva.estado.toLowerCase().replace("-", ""))
                                                    ? "pointer"
                                                    : "not-allowed",
                                                }}
                                            >
                                                Cancelar
                                            </button>
                                            {/*O botão inserido aqui é o de realizar embarque. As funções ainda não estão completamente implementadas, mas serão.*/}
                                            <button
                                                className="realizar"
                                                >
                                                <Plane className="icone-aviao" />
                                            </button>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                    
                    {/*Seção de paginação*/}
                    <div className="paginacao">
                        {Array.from({ length: totalPaginas }, (_, i) => (
                            <button
                                key={i}
                                onClick={() => setPaginaAtual(i+1)}
                                className={`pagina ${paginaAtual === i + 1 ? "ativa" : ""}`}
                            >
                                {i+1}
                            </button>
                        ))}
                    </div>
                </section>
            </main>
        </div>
    );
};