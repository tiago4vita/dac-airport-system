package airportsystem.msauth.dto;

import airportsystem.msauth.model.Usuario;
import jakarta.validation.constraints.NotBlank;

public class UsuarioDTO {

    private String codigo;

    @NotBlank(message = "Login é obrigatório")
    private String login;

    @NotBlank(message = "Senha é obrigatório")
    private String senha;

    private Usuario.TipoUsuario tipo;

    private Boolean ativo;

    public UsuarioDTO() {}

    public UsuarioDTO(String codigo, String login, String senha) {
        this.codigo = codigo;
        this.login = login;
        this.senha = senha;
    }

    public String getCodigo() { return codigo; }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Usuario.TipoUsuario getTipo() { return tipo; }

    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(o == null || getClass() != o.getClass()) return false;

        UsuarioDTO usuarioDTO = (UsuarioDTO) o;

        if (login != null ? !login.equals(usuarioDTO.login) : usuarioDTO.login != null) return false;
        return senha != null ? senha.equals(usuarioDTO.senha) : usuarioDTO.senha == null;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (senha != null ? senha.hashCode() : 0);
        return result;
    }

}
