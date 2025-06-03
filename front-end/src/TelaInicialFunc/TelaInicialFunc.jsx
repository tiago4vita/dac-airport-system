import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { EtiquetaFuncionario } from "../LabelFunc/LabelFunc";
import { ModalCancelaVoo } from "../ModalCancelaVoo/ModalCancelaVoo";
import { ModalRealiza } from "../ModalRealiza/ModalRealiza";
import "./TelaInicialFunc.css";

export const TelaInicialFunc = () => {
  const [voos, setVoos] = useState([]);
  const [paginaAtual, setPaginaAtual] = useState(1);
  const [itensPorPagina, setItensPorPagina] = useState(10);
  const [vooSelecionado, setVooSelecionado] = useState(null);
  const [modalCancelamentoAberto, setModalCancelamentoAberto] = useState(false);
  const [modalConfirmacaoAberto, setModalConfirmacaoAberto] = useState(false);
  const navigate = useNavigate();

  const token = sessionStorage.getItem("token");

  const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

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
    const buscarVoos = async () => {
      const hoje = new Date();
      const dataInicio = hoje.toISOString().split("T")[0];
      const dataFim = new Date(hoje.getTime() + 48 * 60 * 60 * 1000)
        .toISOString()
        .split("T")[0];

      try {
        const res = await api.get(`/voos?data=${dataInicio}&data-fim=${dataFim}`);
        const filtrados = res.data.filter((voo) => voo.estado?.toLowerCase() === "criada");
        setVoos(filtrados);
      } catch (err) {
        console.error("Erro ao buscar voos:", err);
      }
    };

    buscarVoos();
  }, []);

  const totalPaginas = Math.ceil(voos.length / itensPorPagina);
  const inicio = (paginaAtual - 1) * itensPorPagina;
  const voosPaginados = voos.slice(inicio, inicio + itensPorPagina);

  const abrirModalCancelamento = (voo) => {
    setVooSelecionado(voo);
    setModalCancelamentoAberto(true);
  };

  const abrirModalConfirmacao = (voo) => {
    setVooSelecionado(voo);
    setModalConfirmacaoAberto(true);
  };

  const confirmarCancelamento = async () => {
    try {
      await api.patch(`/voos/${vooSelecionado.codigo}/estado`, {
        estado: "CANCELADO",
      });
      setModalCancelamentoAberto(false);
      setVooSelecionado(null);
      window.location.reload();
    } catch (err) {
      console.error("Erro ao cancelar voo:", err);
    }
  };

  const confirmarRealizacao = async () => {
    try {
      await api.patch(`/voos/${vooSelecionado.codigo}/estado`, {
        estado: "REALIZADO",
      });
      setModalConfirmacaoAberto(false);
      setVooSelecionado(null);
      window.location.reload();
    } catch (err) {
      console.error("Erro ao confirmar voo:", err);
    }
  };

  return (
    <>
      <EtiquetaFuncionario />

      <div className="tela-inicial-func">
        <div className="titulo-funcionario">
          <h2>Reservas Pendentes para Embarque</h2>
          <p className="subtitulo">
            Apenas voos que acontecerão dentro de 48 horas estão listados
          </p>
        </div>

        <section className="tabela-reservas-func">
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Origem</th>
                <th>Destino</th>
                <th>Data</th>
                <th>Hora</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {voosPaginados.map((voo, index) => {
                const dataObj = new Date(voo.data);
                return (
                  <tr key={index}>
                    <td>{voo.codigo}</td>
                    <td>{voo.aeroporto_origem?.codigo}</td>
                    <td>{voo.aeroporto_destino?.codigo}</td>
                    <td>{dataObj.toLocaleDateString("pt-BR")}</td>
                    <td>
                      {dataObj.toLocaleTimeString("pt-BR", {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </td>
                    <td>
                      <button
                        className="ver"
                        onClick={() => navigate(`/confirmar-embarque/${voo.codigo}`)}
                      >
                        Embarque
                      </button>
                      <button
                        className="confirmar"
                        onClick={() => abrirModalConfirmacao(voo)}
                      >
                        Confirmar Vôo
                      </button>
                      <button
                        className="cancelar"
                        onClick={() => abrirModalCancelamento(voo)}
                      >
                        Cancelar
                      </button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </section>

        <div className="paginacao">
          {Array.from({ length: totalPaginas }, (_, i) => (
            <button
              key={i}
              onClick={() => setPaginaAtual(i + 1)}
              className={`pagina ${paginaAtual === i + 1 ? "ativa" : ""}`}
            >
              {i + 1}
            </button>
          ))}
        </div>

        {/* Modais */}
        <ModalCancelaVoo
          isOpen={modalCancelamentoAberto}
          onConfirm={confirmarCancelamento}
          onCancel={() => setModalCancelamentoAberto(false)}
        />

        <ModalRealiza
          isOpen={modalConfirmacaoAberto}
          onConfirm={confirmarRealizacao}
          onCancel={() => setModalConfirmacaoAberto(false)}
        />
      </div>
    </>
  );
};
