import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./ComprarMilhas.css";

export const ComprarMilhas = () => {
  const [cliente, setCliente] = useState(null);
  const [quantidade, setQuantidade] = useState(0);
  const valorMilha = 5;
  const navigate = useNavigate();

  const token = sessionStorage.getItem("token");
  const codigoCliente = sessionStorage.getItem("codigo");

  const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  useEffect(() => {
    api.get(`/clientes/${codigoCliente}`)
      .then((res) => setCliente(res.data))
      .catch((err) => {
        console.error("Erro ao buscar cliente:", err);
        alert("Erro ao buscar dados do cliente.");
      });
  }, []);

  const total = quantidade * valorMilha;
  const dataHoje = new Date().toLocaleDateString("pt-BR");

  const handleCompra = async () => {
    try {
      await api.put(`/clientes/${codigoCliente}/milhas`, {
        quantidade: quantidade,
      });

      alert("Milhas compradas com sucesso!");
      navigate("/extrato");
    } catch (error) {
      console.error("Erro ao comprar milhas:", error);
      alert("Erro ao processar a compra.");
    }
  };

  return (
    <div className="pagina-milhas">
      <main className="conteudo-milhas">
        <section className="form-milhas">
          <h2>Quantas milhas deseja comprar?</h2>
          <input
            type="number"
            placeholder="Quantidade de Milhas"
            min={0}
            value={quantidade}
            onChange={(e) => setQuantidade(Number(e.target.value))}
          />
        </section>

        <section className="checkout-milhas">
          <h3>Checkout</h3>
          <div className="checkout-esquerda">
            <table>
              <thead>
                <tr>
                  <th>Quantidade de Milhas</th>
                  <th>Data</th>
                  <th>Valor por Milha</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>{quantidade}</td>
                  <td>{dataHoje}</td>
                  <td>{valorMilha} BRL</td>
                </tr>
              </tbody>
            </table>
            <p className="saldo">
              Seu saldo atual de Milhas: <strong>{cliente?.saldoMilhas ?? 0}</strong>
            </p>
          </div>

          <div className="total-abaixo">
            <span>Total a pagar</span>
            <strong>
              {total.toLocaleString("pt-BR", {
                style: "currency",
                currency: "BRL",
              })}
            </strong>
          </div>
        </section>

        <div className="botoes-footer">
          <button className="btn-comprar" disabled={quantidade <= 0} onClick={handleCompra}>
            Comprar
          </button>
        </div>
      </main>
    </div>
  );
};
