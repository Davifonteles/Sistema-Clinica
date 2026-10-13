package SistemaClínica.Model;

/** O que aconteceu ao tentar agendar uma consulta. */
public enum ResultadoAgendamento {
    AGENDADA("Consulta agendada com sucesso."),
    LISTA_DE_ESPERA("Horários do médico estão cheios neste dia. Você foi adicionado à lista de espera."),
    NAO_LOGADO("Você precisa estar logado como paciente."),
    MEDICO_NAO_ENCONTRADO("Médico não encontrado."),
    PLANO_NAO_ACEITO("Este médico não aceita seu plano de saúde."),
    HORARIO_OCUPADO("Este horário do médico já está ocupado."),
    PACIENTE_OCUPADO("Você já tem outra consulta neste mesmo horário.");

    private final String mensagem;

    ResultadoAgendamento(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    /** true quando a consulta foi marcada ou o paciente entrou na lista de espera. */
    public boolean deuCerto() {
        return this == AGENDADA || this == LISTA_DE_ESPERA;
    }
}
