package airportsystem.msvoo.enums;

public enum StatusVoos {
    CONFIRMADO("Confirmado"),
    CANCELADO("Cancelado"),
    REALIZADO("Realizado");

    private final String descricao;

    StatusVoos(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusVoos fromDescricao(String descricao) {
        for (StatusVoos status : StatusVoos.values()) {
            if (status.getDescricao().equalsIgnoreCase(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + descricao);
    }
}