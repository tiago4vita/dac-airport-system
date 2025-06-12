import React from "react";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { Plane, LogOut } from "lucide-react";
import axiosInstance from "../../api/axiosInstance"; 
import { useAuth } from "../../AuthContext";
import "./SideMenuFunc.css";

export const SideMenuFunc = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const menuItems = [
    { label: "Página Inicial", path: "/homepageF" },
    { label: "Cadastro de Voo", path: "/cadastrovoo" },
    { label: "Lista de Funcionários", path: "/listarfunc" },
  ];

  const handleLogout = async () => {
    try {
      await axiosInstance.post("/logout", {
        login: user?.usuario?.email,
      });
    } catch (err) {
      console.warn("Falha ao comunicar logout:", err);
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
