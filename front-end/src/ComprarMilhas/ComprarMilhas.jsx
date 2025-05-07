import React, { useEffect, useState } from "react";
import axios from "axios";
import { Plane, LogOut } from "lucide-react";
import { useNavigate } from "react-router-dom";
import "./ComprarMilhas.css";
import { SaldoMilhas } from "../SaldoMilhas/SaldoMilhas";

export const ComprarMilhas = () => {
  const [cliente, setCliente] = useState(null);
  const navigate = useNavigate();

  const carregarDados = () => {
    const codigoCliente = 1010;
    axios.get(`http://localhost:8080/clientes?codigo=${codigoCliente}`).then((res) => {
      setCliente(res.data[0]);
    });
  };

  useEffect(() => {
    carregarDados();
  }, []);

  return (
      <div className="tela-inicial">
        <main className="conteudo">
              <section className="campo-milhas">
                  <div>
                      <h2>Quantas milhas deseja comprar?</h2>
                      <p>
                          <input className="inserir-milhas"
                          type="number"
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
                          <tr>
                              <td>0</td>
                              <td>25/03/2023</td>
                              <td>R$ 5,00</td>
                          </tr>
                      </tbody>
                  </table>

                  <div className="saldo">
                      <p>Seu saldo atual de milhas: 1.500</p>
                    </div>
                  <div className="valor">
                    <p>Total a pagar &nbsp;</p>
                      <p>R$ 0,00 &nbsp;</p>
                  </div>
                  <div className="botao">
                    <button className="botao-confirma" onClick={() => navigate('../homepageC')}>Comprar</button>
                  </div>
              </section>
        </main>
      </div>
  );
};