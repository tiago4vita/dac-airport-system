package airportsystem.msvoo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import airportsystem.msvoo.enums.StatusVoos;

@Entity
@Table(name = "voos")
public class Voo {



	public Voo() {
		super();
	}



	public Voo(Long id, String codigoVoo, LocalDateTime dataHoraPartida, Aeroporto origem, Aeroporto destino,
			BigDecimal valorPassagem, int quantidadeAssentos, int quantidadePassageiros, StatusVoos status) {
		super();
		this.id = id;
		this.codigoVoo = codigoVoo;
		this.dataHoraPartida = dataHoraPartida;
		this.origem = origem;
		this.destino = destino;
		this.valorPassagem = valorPassagem;
		this.quantidadeAssentos = quantidadeAssentos;
		this.quantidadePassageiros = quantidadePassageiros;
		this.status = status;
	}



	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_voo")
    private String codigoVoo;
    
    @Column(name = "data_hora_partida")
    private LocalDateTime dataHoraPartida;

    @ManyToOne
    @JoinColumn(name = "aeroporto_origem_id")
    private Aeroporto origem;

    @ManyToOne
    @JoinColumn(name = "aeroporto_destino_id")
    private Aeroporto destino;

    @Column(name = "valor_passagem")
    private BigDecimal valorPassagem;

    @Column(name = "quantidade_assentos")
    private int quantidadeAssentos;

    @Column(name = "quantidade_passageiros")
    private int quantidadePassageiros;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusVoos status = StatusVoos.CONFIRMADO; 
    
    @ElementCollection
    @CollectionTable(
        name = "voo_reservas_tracking",
        joinColumns = @JoinColumn(name = "voo_id")
    )
    private List<Reserva> reservasTracking = new ArrayList<>();


    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHoraPartida() {
        return dataHoraPartida;
    }

    public void setDataHoraPartida(LocalDateTime dataHoraPartida) {
        this.dataHoraPartida = dataHoraPartida;
    }

    public Aeroporto getOrigem() {
        return origem;
    }

    public void setOrigem(Aeroporto origem) {
        this.origem = origem;
    }

    public Aeroporto getDestino() {
        return destino;
    }

    public void setDestino(Aeroporto destino) {
        this.destino = destino;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public int getQuantidadeAssentos() {
        return quantidadeAssentos;
    }

    public void setQuantidadeAssentos(int quantidadeAssentos) {
        this.quantidadeAssentos = quantidadeAssentos;
    }

    public int getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public void setQuantidadePassageiros(int quantidadePassageiros) {
        this.quantidadePassageiros = quantidadePassageiros;
    }
    
    public void setStatus(StatusVoos status) {
        this.status = status;
    }

    public String getStatus() {
        return status != null ? status.getDescricao() : null;
    }

    public void setStatus(String statusStr) {
        this.status = StatusVoos.fromDescricao(statusStr);
    }

	public String getCodigoVoo() {
		return codigoVoo;
	}
	
	public StatusVoos getStatusEnum() {
	    return this.status;
	}


	public void setCodigoVoo(String codigoVoo) {
		this.codigoVoo = codigoVoo;
	}
	
	public List<Reserva> getReservasTracking() {
        return reservasTracking;
    }

    public void setReservasTracking(List<Reserva> reservasTracking) {
        this.reservasTracking = reservasTracking;
        updatePassengerCount();
    }
    
    public void addReserva(Long reservaId, Integer quantidade, String status) {
        if (!hasAvailableSeats(quantidade)) {
            throw new IllegalStateException("Não há assentos suficientes disponíveis");
        }
        
        Reserva tracking = new Reserva(reservaId, quantidade, status);
        reservasTracking.add(tracking);
        updatePassengerCount();
    }

    public void updateReservaStatus(Long reservaId, String newStatus) {
        reservasTracking.stream()
            .filter(r -> r.getReservaId().equals(reservaId))
            .findFirst()
            .ifPresent(r -> {
                r.setStatus(newStatus);
                updatePassengerCount();
            });
    }

    public boolean hasAvailableSeats(int requestedSeats) {
        int occupiedSeats = reservasTracking.stream()
            .filter(r -> !"CANCELADA".equals(r.getStatus()))
            .mapToInt(Reserva::getQuantidade)
            .sum();
            
        return (quantidadeAssentos - occupiedSeats) >= requestedSeats;
    }
    
    public void updatePassengerCount() {
        this.quantidadePassageiros = reservasTracking.stream()
            .filter(r -> !"CANCELADA".equals(r.getStatus()))
            .mapToInt(Reserva::getQuantidade)
            .sum();
    }
}
    
