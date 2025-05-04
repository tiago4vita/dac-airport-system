import React from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { Plane, LogOut } from "lucide-react";
import "./SideMenuCliente.css";

export const SideMenuCliente = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    { label: "Página Inicial", path: "/homepageC" },
    { label: "Reservar", path: "/buscar-voos" },
    { label: "Consultar Reserva", path: "#" },
    { label: "Comprar Milhas", path: "#" },
    { label: "Extrato de Milhas", path: "#" },
    { label: "Check-in", path: "#" },
  ];

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
              const isAtiva = location.pathname.startsWith(item.path); // ← aqui é a chave
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
        <button className="logout" onClick={() => navigate("/")}>
          <LogOut className="icone-logout" /> Log Out
        </button>
      </aside>

      <main className="conteudo">
        <Outlet />
      </main>
    </div>
  );
};
