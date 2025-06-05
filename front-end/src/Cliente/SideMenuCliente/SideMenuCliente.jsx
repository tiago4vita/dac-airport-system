import React from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { Plane, LogOut } from "lucide-react";
import axios from "axios";
import { useAuth } from "../../AuthContext";
import "./SideMenuCliente.css";

export const SideMenuCliente = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();
  const token = localStorage.getItem("token");

  const menuItems = [
    { label: "Página Inicial", path: "/homepageC" },
    { label: "Reservar", path: "/buscar-voos" },
    { label: "Consultar Reserva", path: "/consulta" },
    { label: "Comprar Milhas", path: "/compra" },
    { label: "Extrato de Milhas", path: "/extrato" },
    { label: "Check-in", path: "/checkin" },
  ];

  const handleLogout = async () => {
    try {
      await axios.post(
        "http://localhost:8080/logout",
        { login: user?.usuario?.email },
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (err) {
      console.warn("Erro ao fazer logout no backend:", err);
    } finally {
      logout();
      navigate("/");
    }
  };

  return (
    <div className="tela-inicial">
      <aside className="menu-lateral">
        <div>
          <div className="logo">
            <Plane className="icone-aviao" />
            <span className="logo-texto">DAC Aéreo</span>
          </div>
          <nav className="navegacao">
            {menuItems.map((item, index) => {
              const isAtiva = location.pathname.startsWith(item.path);
              return (
                <button
                  key={index}
                  className={`menu-item ${isAtiva ? "ativo" : ""}`}
                  onClick={() => {
                    if (item.path !== "#") navigate(item.path);
                  }}
                >
                  {item.label}
                </button>
              );
            })}
          </nav>
        </div>

        <button className="logout" onClick={handleLogout}>
          <LogOut className="icone-logout" /> Log Out
        </button>
      </aside>

      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  );
};
