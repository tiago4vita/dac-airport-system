import React, { useState, useEffect } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import "./Reservar.css";
import imagemAssentos from "../assets/image-1.png";

export const Reservar = () => {
  const { state } = useLocation();
  const { codigo } = useParams();
  const navigate = useNavigate();
  const [quantidade, setQuantidade] = useState(1);
  const [assentos, setAssentos] = useState([{ coluna: "", fileira: "" }]);
  const [usarMilhas, setUsarMilhas] = useState(false);
  const [milhas, setMilhas] = useState(0);
  const [cliente, setCliente] = useState(null);
  const [voo, setVoo] = useState(null);

  const codigoCliente = 1010;
  const valorMilha = 5;

  useEffect(() => {
    axios.get(`http://localhost:8080/clientes?codigo=${codigoCliente}`)
      .then((res) => setCliente(res.data[0]));

    axios.get(`http://localhost:8080/voos?codigo=${codigo}`)
      .then((res) => setVoo(res.data[0]));
  }, [codigo]);

  const atualizarAssentos = (qtd) => {
    const novos = [];
    for (let i = 0; i < qtd; i++) {
      novos.push(assentos[i] || { coluna: "", fileira: "" });
    }
    setAssentos(novos);
  };

  const handleConfirmar = () => {
    const confirmar = window.confirm("Deseja confirmar a reserva?");
    if (confirmar) {
      navigate("/homepageC");
    }
  };

  if (!voo) return <p>Carregando voo...</p>;

  const saldoMilhas = cliente?.saldoMilhas ?? 0;
  const valorPorPassagem = voo.valor_passagem;
  const subtotal = valorPorPassagem * quantidade;
  const maximoMilhasPermitido = Math.floor(subtotal / valorMilha);
    const milhasUsadas = usarMilhas
    ? Math.min(milhas, saldoMilhas, maximoMilhasPermitido)
    : 0;
  const desconto = milhasUsadas * valorMilha;
  const total = Math.max(0, subtotal - desconto);

  const dataVoo = new Date(voo.data);
  const dataStr = dataVoo.toLocaleDateString("pt-BR");
  const horaStr = dataVoo.toLocaleTimeString("pt-BR", { hour: '2-digit', minute: '2-digit' });

  const assentosPreenchidos = assentos.every(a => a.coluna && a.fileira);

  return (
    <div className="reservar-page">
      <button className="voltar" onClick={() => navigate(-1)}>Voltar</button>

      <div className="container-reservar">
        <section className="assentos-section">
          <div className="topo-reservar">
            <h2>Escolha os assentos</h2>
            <div className="quantidade-wrapper-reservar">
              <span>Quantidade</span>
              <div className="quantidade-reservar">
                <button onClick={() => {
                  const nova = Math.max(1, quantidade - 1);
                  setQuantidade(nova);
                  atualizarAssentos(nova);
                }}>−</button>
                <span>{quantidade}</span>
                <button onClick={() => {
                  const nova = quantidade + 1;
                  setQuantidade(nova);
                  atualizarAssentos(nova);
                }}>+</button>
              </div>
            </div>
          </div>

          <img src={imagemAssentos} alt="Mapa de Assentos" className="imagem-assentos" />

          {assentos.map((a, i) => (
            <div className="linha-assento" key={i}>
              <select value={a.coluna} onChange={(e) => {
                const novos = [...assentos];
                novos[i].coluna = e.target.value;
                setAssentos(novos);
              }}>
                <option value="">Coluna</option>
                {"ABCDEFGHIJK".split("").map((letra) => (
                  <option key={letra} value={letra}>{letra}</option>
                ))}
              </select>

              <select value={a.fileira} onChange={(e) => {
                const novos = [...assentos];
                novos[i].fileira = e.target.value;
                setAssentos(novos);
              }}>
                <option value="">Fileira</option>
                {Array.from({ length: 40 }, (_, j) => (
                  <option key={j + 1} value={j + 1}>{j + 1}</option>
                ))}
              </select>
            </div>
          ))}
        </section>

        <section className="checkout-reservar">
          <h3>Checkout</h3>
          <table>
            <thead>
              <tr>
                <th>Origem</th>
                <th>Destino</th>
                <th>Data</th>
                <th>Hora</th>
                <th>Valor por Passagem</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{voo.aeroporto_origem.codigo}</td>
                <td>{voo.aeroporto_destino.codigo}</td>
                <td>{dataStr}</td>
                <td>{horaStr}</td>
                <td>{valorPorPassagem.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</td>
              </tr>
            </tbody>
          </table>

          <div className="milhas-box-reservar">
            <p>Seu saldo de Milhas: <strong>{saldoMilhas}</strong></p>

            <label className="checkbox-container-reservar">
            <input
                type="checkbox"
                checked={usarMilhas}
                onChange={(e) => {
                setUsarMilhas(e.target.checked);
                if (!e.target.checked) setMilhas(0);
                }}
            />
            <span>Usar Milhas</span>
            </label>


            <input
            type="number"
            disabled={!usarMilhas}
            value={milhas}
            min={0}
            max={Math.min(saldoMilhas, maximoMilhasPermitido)}
            onChange={(e) => {
                const input = Number(e.target.value);
                const limite = Math.min(saldoMilhas, maximoMilhasPermitido);
                setMilhas(Math.min(input, limite));
            }}
            placeholder="Quantidade de Milhas"
            />
          </div>

          <div className="resumo-reservar">
            <p>
              Subtotal: <strong>{quantidade} x {valorPorPassagem.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</strong>
            </p>
            <p>Milhas Utilizadas (-): <strong>{desconto.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</strong></p>
            <p>Total a pagar: <strong>{total.toLocaleString("pt-BR", { style: "currency", currency: "BRL" })}</strong></p>
          </div>

          <div style={{ display: "flex", justifyContent: "flex-end", marginTop: "1.5rem" }}>
            <button className="confirmar-reservar" onClick={handleConfirmar} disabled={!assentosPreenchidos}>
              Confirmar Reserva
            </button>
          </div>
        </section>
      </div>
    </div>
  );
};
