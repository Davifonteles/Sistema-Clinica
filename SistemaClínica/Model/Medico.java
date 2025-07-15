package SistemaClínica.Model;

import java.util.List;

public class Medico {
    private String nome;
    private String especialidade;
    private List<String> planosAceitos;

    public Medico(String nome, String especialidade, List<String> planosAceitos) {
        this.nome = nome;
        this.especialidade = especialidade;
        this.planosAceitos = planosAceitos;
    }

    public String getNome() {
        return nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public List<String> getPlanosAceitos() {
        return planosAceitos;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public void setPlanosAceitos(List<String> planosAceitos) {
        this.planosAceitos = planosAceitos;
    }

    @Override
    public String toString() {
        return "Dr. " + nome + " - " + especialidade + " - Planos: " + planosAceitos;
    }
}
