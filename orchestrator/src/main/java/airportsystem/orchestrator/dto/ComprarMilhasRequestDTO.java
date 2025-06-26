package airportsystem.orchestrator.dto;

public class ComprarMilhasRequestDTO {
    private Long quantidade;

    public ComprarMilhasRequestDTO() {
    }

    public ComprarMilhasRequestDTO(Long quantidade) {
        this.quantidade = quantidade;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
} 