import React from "react";
import { useForm, Controller } from "react-hook-form";
import { IMaskInput } from "react-imask";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import vector from "../assets/vector.svg";
import "./AutoCadastro.css";

console.log(process.env.REACT_APP_API_URL);

// Validador de CPF
function validarCPF(cpf) {
  cpf = cpf.replace(/[^\d]+/g, "");
  if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i);
  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.charAt(9))) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.charAt(10))) return false;

  return true;
}

// Gerador de senha aleatória de 4 dígitos
function gerarSenha() {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

export const Autocadastro = () => {
  const {
    register,
    handleSubmit,
    control,
    setValue,
    getValues,
    formState: { errors },
  } = useForm();
  const navigate = useNavigate();

  const buscarEndereco = () => {
    const cep = getValues("cep")?.replace(/\D/g, "");
    if (cep?.length === 8) {
      fetch(`https://viacep.com.br/ws/${cep}/json/`)
        .then((res) => res.json())
        .then((data) => {
          if (!data.erro) {
            setValue("rua", data.logradouro);
            setValue("cidade", data.localidade);
            setValue("estado", data.uf);
            setValue("bairro", data.bairro);
          } else {
            alert("CEP não encontrado");
          }
        });
    }
  };

  const onSubmit = async (data) => {
    const senha = gerarSenha();
    
    const cliente = {
      cpf: data.cpf.replace(/\D/g, ""),
      email: data.email,
      nome: data.nome,
      senha: senha,
      saldo_milhas: 0,
      endereco: {
        cep: data.cep.replace(/\D/g, ""),
        uf: data.estado,
        cidade: data.cidade,
        bairro: data.bairro || "",
        rua: data.rua,
        numero: data.numero,
        complemento: data.complemento || "",
      },
    };

    try {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/clientes`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(cliente),
      });

      if (response.status === 201) {
        alert(`Cliente criado com sucesso. Sua senha é ${senha}`);
        setTimeout(() => {
          navigate("/");
        }, 3000);
      } else {
        alert("Erro ao cadastrar cliente");
      }
    } catch (error) {
      console.error("Erro na requisição:", error);
      alert("Erro de conexão com o servidor");
    }
  };

  return (
    <div className="cadastro">
      <div className="cadastro-container">
        <h1 className="cadastro-titulo">Cadastre-se</h1>

        <form className="cadastro-form" onSubmit={handleSubmit(onSubmit)}>
          <div className="cadastro-linha">
            <label className="cadastro-label">Nome*</label>
            <input
              {...register("nome")}
              className="cadastro-input"
              placeholder="Nome completo"
              required
            />
          </div>

          <div className="cadastro-linha">
            <label className="cadastro-label">CPF*</label>
            <Controller
              name="cpf"
              control={control}
              rules={{
                required: "CPF é obrigatório",
                validate: (value) =>
                  validarCPF(value) || "CPF inválido",
              }}
              render={({ field: { onChange, onBlur, value, ref } }) => (
                <IMaskInput
                  mask="000.000.000-00"
                  value={value}
                  onAccept={(val) => onChange(val)}
                  onBlur={onBlur}
                  inputRef={ref}
                  className="cadastro-input"
                  placeholder="000.000.000-00"
                  required
                />
              )}
            />
            {errors.cpf && (
              <span className="cadastro-error">{errors.cpf.message}</span>
            )}
          </div>

          <div className="cadastro-linha">
            <label className="cadastro-label">Email*</label>
            <input
              type="email"
              {...register("email", {
                required: "Email é obrigatório",
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: "Email inválido",
                },
              })}
              className="cadastro-input"
              placeholder="email@exemplo.com"
            />
            {errors.email && (
              <span className="cadastro-error">{errors.email.message}</span>
            )}
          </div>

          <div className="cadastro-linha cadastro-linha-dupla">
            <div className="cadastro-col">
              <label className="cadastro-label">CEP*</label>
              <Controller
                name="cep"
                control={control}
                render={({ field }) => (
                  <IMaskInput
                    {...field}
                    mask="00000-000"
                    className="cadastro-input"
                    placeholder="00000-000"
                    onBlur={buscarEndereco}
                    required
                  />
                )}
              />
            </div>

            <div className="cadastro-col">
              <label className="cadastro-label">Rua</label>
              <input
                {...register("rua")}
                className="cadastro-input cadastro-disabled"
                readOnly
              />
            </div>
          </div>

          <div className="cadastro-linha cadastro-linha-tripla">
            <div className="cadastro-col">
              <label className="cadastro-label">Número*</label>
              <input
                {...register("numero")}
                className="cadastro-input"
                placeholder="123"
                required
              />
            </div>

            <div className="cadastro-col">
              <label className="cadastro-label">Cidade</label>
              <input
                {...register("cidade")}
                className="cadastro-input cadastro-disabled"
                readOnly
              />
            </div>

            <div className="cadastro-col">
              <label className="cadastro-label">Estado</label>
              <input
                {...register("estado")}
                className="cadastro-input cadastro-disabled"
                readOnly
              />
            </div>
          </div>

          <div className="cadastro-linha">
            <label className="cadastro-label">Bairro</label>
            <input
              {...register("bairro")}
              className="cadastro-input"
              placeholder="Centro"
            />
          </div>

          <div className="cadastro-linha">
            <label className="cadastro-label">Complemento</label>
            <input
              {...register("complemento")}
              className="cadastro-input"
              placeholder="Apartamento, casa, etc."
            />
          </div>

          <div className="cadastro-botoes">
            <button
              type="button"
              className="cadastro-cancelar"
            >
              Cancelar
            </button>
            <button type="submit" className="cadastro-cadastrar">
              Cadastrar
            </button>
          </div>
        </form>
      </div>

      <img src={vector} alt="decorativo" className="vector-bg" />
    </div>
  );
};
