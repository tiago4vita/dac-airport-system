package airportsystem.orchestrator.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class VooDTO {

    private OffsetDateTime data;
    private BigDecimal valorPassagem;
    private int quantidadePoltronasTotal;
    private int quantidadePoltronasOcupadas;
    private String codigoAeroportoOrigem;
    private String codigoAeroportoDestino;

    // Getters e Setters

    public OffsetDateTime getData() {
        return data;
    }

    public void setData(OffsetDateTime data) {
        this.data = data;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
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

    public String getCodigoAeroportoOrigem() {
        return codigoAeroportoOrigem;
    }

    public void setCodigoAeroportoOrigem(String codigoAeroportoOrigem) {
        this.codigoAeroportoOrigem = codigoAeroportoOrigem;
    }

    public String getCodigoAeroportoDestino() {
        return codigoAeroportoDestino;
    }

    public void setCodigoAeroportoDestino(String codigoAeroportoDestino) {
        this.codigoAeroportoDestino = codigoAeroportoDestino;
    }
}
