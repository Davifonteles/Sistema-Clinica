package SistemaClínica.Controller;

import SistemaClínica.Model.SistemaConsultaMedica;
import SistemaClínica.Model.ArquivoUtils;
import SistemaClínica.Model.Consulta;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SistemaController {

    private final SistemaConsultaMedica sistema;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public SistemaController(SistemaConsultaMedica sistema){
        this.sistema = sistema;
    }

    // login
    public boolean loginMedico(String nome) {
        return sistema.loginMedico(nome);
    }

    public boolean loginPaciente(String nome) {
        return sistema.loginPaciente(nome);
    }

    public void logout() {
        sistema.logout();
    }

    // Operações do paciente
    public boolean agendarConsulta(String nomeMedico, LocalDateTime dataHora) {
        return sistema.agendarConsulta(nomeMedico, dataHora);
    }

    public boolean cancelarConsulta(LocalDateTime dataHora) {
        return sistema.cancelarConsulta(dataHora);
    }

    public boolean avaliarConsulta(LocalDateTime dataHora, int nota, String comentario){
        return sistema.avaliarConsulta(dataHora, nota, comentario);
    }

    public void buscarMedicoPorFiltro(String filtro){
        sistema.listarMedicosPorFiltro(filtro);
    }

    // Operações do médico
    public boolean realizarConsulta(LocalDateTime dataHora, String descricao) {
    return sistema.realizarConsulta(dataHora, descricao);
}

    // Exportação
    public void exportarConsultas(String caminho) {
        ArquivoUtils.salvarConsultasRealizadas(sistema.getConsultasRealizadas(), caminho);
    }

    // Getters para listar dados
    public List<Consulta> getConsultasRealizadas(){
        return sistema.getConsultasRealizadas();
    }

    public List<Medico> listarMedicos() {
        return sistema.getMedicos();
    }

    public List<Paciente> listarPacientes() {
        return sistema.getPacientes();
    }

    public SistemaConsultaMedica getSistema() {
        return sistema;
    }

    public Medico getMedicoLogado(){
        return sistema.getMedicoLogado();
    }

    public Paciente getPacienteLogado(){
        return sistema.getPacienteLogado(); 
    }

    public List<Consulta> getConsultasPendentesDoMedico() {
    return sistema.getConsultasPendentesDoMedico();
    }

    public List<Consulta> getAvaliacoesRecebidas() {
    return sistema.getAvaliacoesRecebidas();
}

}
