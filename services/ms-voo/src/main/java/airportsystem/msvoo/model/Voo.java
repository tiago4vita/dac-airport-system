package airportsystem.msvoo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
public class Voo {

    @Id
    private String codigo;
    
    private LocalDateTime dataHora;

    private String origem;

    private String destino;

    private double precoEmReais;

    private int quantidadePoltronasTotal;

    private int quantidadePoltronasOcupadas;

    private EstadoVoo estado;

    public enum EstadoVoo {
        CONFIRMADO,
        CANCELADO,
        REALIZADO
    }

    public Voo() {
        this.estado = EstadoVoo.CONFIRMADO;
        this.quantidadePoltronasOcupadas = 0;
    }

    public Voo(LocalDateTime dataHora, String origem, String destino, 
               double precoEmReais, int quantidadePoltronasTotal, int quantidadePoltronasOcupadas) {
        this.codigo = gerarCodigo();
        this.dataHora = dataHora;
        this.origem = origem; 
        this.destino = destino;
        this.precoEmReais = precoEmReais;
        this.quantidadePoltronasTotal = quantidadePoltronasTotal;
        this.quantidadePoltronasOcupadas = quantidadePoltronasOcupadas;
        this.estado = EstadoVoo.CONFIRMADO;
    }

    private String gerarCodigo() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // Generate 4 random uppercase letters
        for (int i = 0; i < 4; i++) {
            char randomLetter = (char) (random.nextInt(26) + 'A');
            sb.append(randomLetter);
        }
        // Generate 4 random numbers
        for (int i = 0; i < 4; i++) {
            int randomDigit = random.nextInt(10);
            sb.append(randomDigit);
        }
        return sb.toString();
    }

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
        return this.estado.toString();
    }

    public void setEstado(EstadoVoo estado) {
        this.estado = estado;
    }
    
}
