package airportsystem.mscliente.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes_milhas")
public class TransacaoMilhas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_codigo", nullable = false)
    private Cliente cliente;

    @Column(name = "data_hora", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(nullable = false)
    private Long valorReais;

    @Column(nullable = false)
    private Long quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private String descricao;
    
    @Column
    private String codigoReserva;

    public enum TipoTransacao {
        ENTRADA, SAIDA;
    }

    public TransacaoMilhas() {
    }

    public TransacaoMilhas(Cliente cliente, Long quantidade, TipoTransacao tipo, String descricao) {
        this.cliente = cliente;
        this.quantidade = quantidade;
        this.valorReais = quantidade * 5;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now();
    }
    
    public TransacaoMilhas(Cliente cliente, Long quantidade, Long valorReais, TipoTransacao tipo, String descricao, String codigoReserva) {
        this.cliente = cliente;
        this.quantidade = quantidade;
        this.valorReais = valorReais;
        this.tipo = tipo;
        this.descricao = descricao;
        this.codigoReserva = codigoReserva;
        this.dataHora = LocalDateTime.now();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long id) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setValorReais(Long valorReais) { this.valorReais = valorReais; }

    public Long getValorReais() { return this.valorReais; }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getCodigoReserva() {
        return codigoReserva;
    }
    
    public void setCodigoReserva(String codigoReserva) {
        this.codigoReserva = codigoReserva;
    }
}