package airportsystem.msvoo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Reserva {
    @Column(name = "reserva_id")
    private Long reservaId;
    
    @Column(name = "quantidade")
    private Integer quantidade;
    
    @Column(name = "status")
    private String status;
    
    public Reserva() {}
    
    public Reserva(Long reservaId, Integer quantidade, String status) {
        this.reservaId = reservaId;
        this.quantidade = quantidade;
        this.status = status;
    }
    
    public Long getReservaId() {
        return reservaId;
    }
    
    public void setReservaId(Long reservaId) {
        this.reservaId = reservaId;
    }
    
    public Integer getQuantidade() {
        return quantidade;
    }
    
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}