package SistemaClínica.Model;

public class Paciente {
    private String nome;
    private int idade;
    private String planoSaude;

    public Paciente(String nome, int idade, String planoSaude) {
        this.nome = nome;
        this.idade = idade;
        this.planoSaude = planoSaude;
    }

    public String getNome() {
        return nome;
    }

    public int getIdade() {
        return idade;
    }

    public String getPlanoSaude() {
        return planoSaude;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setPlanoSaude(String planoSaude) {
        this.planoSaude = planoSaude;
    }

    @Override
    public String toString() {
        return nome + " - Idade: " + idade + " - Plano: " + planoSaude;
    }
}
