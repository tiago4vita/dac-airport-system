package airportsystem.msvoo.dto;

import java.time.LocalDateTime;

public class VooDTO {
    private String codigo;
    private LocalDateTime dataHora;
    private String origem;
    private String destino;
    private double precoEmReais;
    private int quantidadePoltronasTotal;
    private int quantidadePoltronasOcupadas;
    private String estado;

    // Default constructor
    public VooDTO() {
    }

    // Constructor with all fields
    public VooDTO(String codigo, LocalDateTime dataHora, String origem, String destino, 
                 double precoEmReais, int quantidadePoltronasTotal, 
                 int quantidadePoltronasOcupadas, String estado) {
        this.codigo = codigo;
        this.dataHora = dataHora;
        this.origem = origem;
        this.destino = destino;
        this.precoEmReais = precoEmReais;
        this.quantidadePoltronasTotal = quantidadePoltronasTotal;
        this.quantidadePoltronasOcupadas = quantidadePoltronasOcupadas;
        this.estado = estado;
    }

    // Getters and Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getPrecoEmReais() {
        return precoEmReais;
    }

    public void setPrecoEmReais(double precoEmReais) {
        this.precoEmReais = precoEmReais;
    }

    public int getQuantidadePoltronasTotal() {
        return quantidadePoltronasTotal;
    }

    public void setQuantidadePoltronasTotal(int quantidadePoltronasTotal) {
        this.quantidadePoltronasTotal = quantidadePoltronasTotal;
    }

    public int getQuantidadePoltronasOcupadas() {
        return quantidadePoltronasOcupadas;
    }

    public void setQuantidadePoltronasOcupadas(int quantidadePoltronasOcupadas) {
        this.quantidadePoltronasOcupadas = quantidadePoltronasOcupadas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
