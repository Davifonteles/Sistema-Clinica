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

    /** Compara o plano sem diferenciar maiúsculas/minúsculas nem espaços nas pontas. */
    public boolean aceitaPlano(String plano) {
        if (plano == null) return false;
        for (String p : planosAceitos) {
            if (p.trim().equalsIgnoreCase(plano.trim())) return true;
        }
        return false;
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
