package airportsystem.msvoo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import airportsystem.msvoo.enums.StatusVoos;

public class UpdateVooDTO {
    private LocalDateTime dataHoraPartida;
    private String codigoOrigem;
    private String codigoDestino;
    private BigDecimal valorPassagem;
    private Integer quantidadeAssentos;
    private Integer quantidadePassageiros;
    private StatusVoos status;

    public UpdateVooDTO() {
        this.dataHoraPartida = getDataHoraPartida();
        this.codigoOrigem = getCodigoOrigem();
        this.codigoDestino = getCodigoDestino();
        this.valorPassagem = getValorPassagem();
        this.quantidadeAssentos = getQuantidadeAssentos();
        this.quantidadePassageiros = getQuantidadePassageiros();
        this.status = getStatus();
    }

    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    public String getCodigoOrigem() {
        return codigoOrigem;
    }

    public void setCodigoOrigem(String codigoOrigem) {
        this.codigoOrigem = codigoOrigem;
    }

    public String getCodigoDestino() {
        return codigoDestino;
    }

    public void setCodigoDestino(String codigoDestino) {
        this.codigoDestino = codigoDestino;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public Integer getQuantidadeAssentos() {
        return quantidadeAssentos;
    }

    public void setQuantidadeAssentos(Integer quantidadeAssentos) {
        this.quantidadeAssentos = quantidadeAssentos;
    }

    public Integer getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public void setQuantidadePassageiros(Integer quantidadePassageiros) {
        this.quantidadePassageiros = quantidadePassageiros;
    }

    public StatusVoos getStatus() {
        return status;
    }

    public void setStatus(StatusVoos status) {
        this.status = status;
    }
}
