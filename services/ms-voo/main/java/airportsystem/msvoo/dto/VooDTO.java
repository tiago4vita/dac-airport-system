package airportsystem.msvoo.dto;

import java.math.BigDecimal;
import airportsystem.msvoo.enums.StatusVoos;
import airportsystem.msvoo.model.Voo;

public class VooDTO {
    private long id;
    private String codigoVoo;     
    private String dataHoraPartida;
    private AeroportoDTO origem;
    private AeroportoDTO destino;
    private BigDecimal valorPassagem;
    private int quantidadeAssentos;
    private int quantidadePassageiros;
    private String status;

    public VooDTO(Voo voo) {
    	this.codigoVoo = voo.getCodigoVoo();
        this.id = voo.getId();
        this.dataHoraPartida = voo.getDataHoraPartida().toString();
        this.origem = new AeroportoDTO(voo.getOrigem());
        this.destino = new AeroportoDTO(voo.getDestino());
        this.valorPassagem = voo.getValorPassagem();
        this.quantidadeAssentos = voo.getQuantidadeAssentos();
        this.quantidadePassageiros = voo.getQuantidadePassageiros();
        this.status = voo.getStatus();
    }

    public long getId() {
        return id;
    }

    public String getDataHoraPartida() {
        return dataHoraPartida;
    }

    public AeroportoDTO getOrigem() {
        return origem;
    }

    public AeroportoDTO getDestino() {
        return destino;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public int getQuantidadeAssentos() {
        return quantidadeAssentos;
    }

    public int getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public String getStatus() {
        return status;
    }

	public String getCodigoVoo() {
		return codigoVoo;
	}

	public void setCodigoVoo(String codigoVoo) {
		this.codigoVoo = codigoVoo;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDataHoraPartida(String dataHoraPartida) {
		this.dataHoraPartida = dataHoraPartida;
	}

	public void setOrigem(AeroportoDTO origem) {
		this.origem = origem;
	}

	public void setDestino(AeroportoDTO destino) {
		this.destino = destino;
	}

	public void setValorPassagem(BigDecimal valorPassagem) {
		this.valorPassagem = valorPassagem;
	}

	public void setQuantidadeAssentos(int quantidadeAssentos) {
		this.quantidadeAssentos = quantidadeAssentos;
	}

	public void setQuantidadePassageiros(int quantidadePassageiros) {
		this.quantidadePassageiros = quantidadePassageiros;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
