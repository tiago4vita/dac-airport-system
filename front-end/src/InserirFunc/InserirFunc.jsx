// src/pages/AddEmployee.jsx

import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

//Para puxar CPF cadastrados
const existingCpfs = [];

export default function AddEmployee() {
  const [form, setForm] = useState({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  //Gera a senha aleatória de 4 digitos
  const generatePassword = () => {
    return Math.floor(1000 + Math.random() * 9000).toString(); 
  };

  //Envio de email com a senha gerada
  const sendEmail = (email, senha) => {
    console.log(`Enviando e-mail para ${email} com a senha: ${senha}`);
    toast.success(`Senha enviada para ${email}: ${senha}`);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    if (existingCpfs.includes(form.cpf)) {
      toast.error("CPF já cadastrado!");
      return;
    }

    const senha = generatePassword();
    sendEmail(form.email, senha);

    existingCpfs.push(form.cpf);

    toast.success("Funcionário cadastrado com sucesso!");
    setForm({
      nome: "",
      cpf: "",
      email: "",
      telefone: "",
    });
  };

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <Card className="w-full max-w-md p-6">
        <CardContent>
          <h1 className="text-2xl font-bold mb-6 text-center">Cadastrar Funcionário</h1>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="nome">Nome</Label>
              <Input
                id="nome"
                name="nome"
                value={form.nome}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <Label htmlFor="cpf">CPF</Label>
              <Input
                id="cpf"
                name="cpf"
                value={form.cpf}
                onChange={handleChange}
                required
                pattern="\d{11}"
                title="Digite 11 números do CPF"
              />
            </div>
            <div>
              <Label htmlFor="email">E-mail</Label>
              <Input
                id="email"
                name="email"
                type="email"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <Label htmlFor="telefone">Telefone</Label>
              <Input
                id="telefone"
                name="telefone"
                value={form.telefone}
                onChange={handleChange}
                required
              />
            </div>
            <Button type="submit" className="w-full">
              Cadastrar
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
