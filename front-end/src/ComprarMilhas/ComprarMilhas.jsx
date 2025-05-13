import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./ComprarMilhas.css";

export const ComprarMilhas = () => {
  const [cliente, setCliente] = useState(null);
  const [quantidade, setQuantidade] = useState(0);
  const valorMilha = 5;
  const codigoCliente = 1010;
  const navigate = useNavigate();

  useEffect(() => {
    axios.get(`http://localhost:8080/clientes?codigo=${codigoCliente}`).then((res) => {
      setCliente(res.data[0]);
    });
  }, []);

  const total = quantidade * valorMilha;
  const dataHoje = new Date().toLocaleDateString("pt-BR");

  const handleCompra = () => {
    // Aqui você pode fazer lógica de POST ou PATCH no futuro, se desejar salvar a compra
    navigate("/extrato");
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
