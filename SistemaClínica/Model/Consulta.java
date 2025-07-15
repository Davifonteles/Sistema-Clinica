package SistemaClínica.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consulta {
    private String descricao;
    private String comentario;
    private Double valorCobrado = null;
    private Medico medico;
    private Paciente paciente;
    private LocalDateTime dataHora;
    private boolean realizada = false;
    private Integer avaliacao = null;

    public Consulta(Medico medico, Paciente paciente, LocalDateTime dataHora) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataHora = dataHora;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isRealizada() {
        return realizada;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public Double getValorCobrado() {
        return valorCobrado;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getComentario(){
        return comentario;
    }

    public void realizarConsulta(String descricao, double valor) {
        this.realizada = true;
        this.descricao = descricao;

        if (!paciente.getPlanoSaude().equalsIgnoreCase("não tenho")) {
            this.valorCobrado = 0.0;
        } else {
            this.valorCobrado = valor;
        }
    }

    public void avaliar(int nota, String comentario) {
        if (nota >= 1 && nota <= 5) {
            this.avaliacao = nota;
            this.comentario = comentario;
        }
    }

    @Override
public String toString() {
    return "Consulta em " + dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        + " | Médico: " + medico.getNome()
        + " | Paciente: " + paciente.getNome()
        + " | Realizada: " + realizada
        + (realizada ? " | Valor: R$" + String.format("%.2f", valorCobrado) : "")
        + (avaliacao != null ? " | Avaliação: " + avaliacao + "/5 - Comentário: " + comentario : "");
}

}
