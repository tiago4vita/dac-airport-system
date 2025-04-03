import React from "react";
import { useForm, Controller } from "react-hook-form";
import { IMaskInput } from "react-imask";
import { useNavigate } from "react-router-dom";
import vector from "./assets/vector.svg";
import "./style.css";

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
          } else {
            alert("CEP não encontrado");
          }
        });
    }
  };

  const onSubmit = (data) => {
    console.log("Cliente cadastrado:", data);
    alert("Cadastro realizado com sucesso! Verifique sua caixa de e-mail");
    navigate("/");
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
              onClick={() => navigate("/")}
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
