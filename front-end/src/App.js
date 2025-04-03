import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import { Login } from "./Login";
import { Autocadastro } from "./Autocadastro"; // ou "./Cadastro"
import "./App.css";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Autocadastro />} />
      </Routes>
    </Router>
  );
}

export default App;
