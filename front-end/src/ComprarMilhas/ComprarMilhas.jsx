import React, { useEffect, useState } from "react";
import axios from "axios";
import { Plane, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./ComprarMilhas.css";

export const ComprarMilhas = () => {
  const [cliente, setCliente] = useState(null);
  const navigate = useNavigate();


  useEffect(() => {
    const codigoCliente = 1010;
    axios.get(`http://localhost:8080/clientes?codigo=${codigoCliente}`).then((res) => {
      setCliente(res.data[0]);
    });    
  }, []);

  return (
    <div className="tela-inicial">
      <aside className="menu-lateral">
        <div>
          <div className="logo">
            <Plane className="icone-aviao" />
            <span className="logo-texto">DAC Aéreo</span>
          </div>
          <nav className="navegacao">
            {["Página Inicial", "Reservar", "Consultar Reserva", "Comprar Milhas", "Extrato de Milhas", "Check-in"].map(
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
            <section className="campo-milhas">
                <div>
                    <h2>Quantas milhas deseja comprar?</h2>
                    <p>
                        <input className="inserir-milhas"
                        type="search"
                        placeholder="Quantidade de milhas"
                        ></input>
                    </p>
                </div>
            </section>

            <section className="checkout-milhas">
                <div>
                    <h2>Checkout</h2>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>Quantidade de milhas</th>
                            <th>Data</th>
                            <th>Valor por Milha</th>
                        </tr>
                    </thead>
                    <tbody>

                    </tbody>
                </table>

                <div>
                    <p>Seu saldo atual de milhas:</p>
                </div>
            </section>
      </main>
    </div>
  );
};