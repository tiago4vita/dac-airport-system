package airportsystem.msauth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


@Document(collection = "usuarios")
public class Usuario {

    @Id
    @Indexed(unique = true)
    private String codigo;

    @Indexed(unique = true)
    @Email(message = "Email inválido")
    @NotBlank(message = "Email não pode ser vazio")
    private String login;

    @NotBlank(message = "Senha não pode ser vazia")
    private String senha;// Stores SHA256+SALT hash from API Gateway

    private TipoUsuario tipo;

    public enum TipoUsuario {
        CLIENTE, FUNCIONARIO
    }

    private boolean ativo;

    // Default constructor
    public Usuario() {
        this.ativo = true;
    }
    // Constructor with all fields
    public Usuario(String codigo, String login, String senha, TipoUsuario tipo, boolean ativo) {
        this.codigo = codigo;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    // Constructor with default ativo = true
    public Usuario(String login, String senha, TipoUsuario tipo) {
        this(null, login, senha, tipo, true);
    }
    // Getters
    public String getCodigo() {
        return codigo;
    }
    public String getLogin() {
        return login;
    }
    public String getSenha() {
        return senha;
    }
    public TipoUsuario getTipo() {
        return tipo;
    }
    public boolean isAtivo() {
        return ativo;
    }
    // Setters
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    // Business methods
    public boolean isEmailValid(String email) {
        if (email == null || email.isBlank()) return false;
        long atCount = email.chars().filter(ch -> ch == '@').count();
        if (atCount != 1) return false;
        String[] parts = email.split("@");
        if (parts.length != 2) return false;
        String localPart = parts[0];
        String domainPart = parts[1];
        if (localPart.isEmpty() || domainPart.isEmpty()) return false;
        if (!domainPart.contains(".")) return false;
        List<String> domainExtensions = Arrays.asList("com", "org", "net", "br", "io", "co");
        int lastDotIndex = domainPart.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == domainPart.length() - 1) return false;
        String extension = domainPart.substring(lastDotIndex + 1);
        if (extension.length() < 2 || !domainExtensions.contains(extension.toLowerCase())) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
    public Usuario ativar() {
        return new Usuario(this.codigo, this.login, this.senha, this.tipo, true);
    }
    public Usuario desativar() {
        return new Usuario(this.codigo, this.login, this.senha, this.tipo, false);
    }
    // toString method
    @Override
    public String toString() {
        return "Usuario{" +
                "id='" + codigo + '\'' +
                ", email='" + login + '\'' +
                ", senha='[PROTECTED]'" +
                ", tipo=" + tipo +
                ", ativo=" + ativo +
                '}';
    }
    // equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return ativo == usuario.ativo &&
                Objects.equals(codigo, usuario.codigo) &&
                Objects.equals(login, usuario.login) &&
                Objects.equals(senha, usuario.senha) &&
                tipo == usuario.tipo;
    }
    // hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(codigo, login, senha, tipo, ativo);
    }
}