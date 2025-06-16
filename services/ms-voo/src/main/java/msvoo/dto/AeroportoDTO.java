package airportsystem.msvoo;

import airportsystem.msvoo.model.Aeroporto;

public class AeroportoDTO {
    private String codigo;
    private String nome;
    private String cidade;
    private String estado;
    private String pais;

    public AeroportoDTO(Aeroporto aeroporto) {
        this.codigo = aeroporto.getCodigo();
        this.nome = aeroporto.getNome();
        this.cidade = aeroporto.getCidade();
        this.estado = aeroporto.getEstado();
        this.pais = aeroporto.getPais();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
    
}
