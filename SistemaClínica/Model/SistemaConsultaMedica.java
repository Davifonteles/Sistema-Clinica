package SistemaClínica.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SistemaConsultaMedica {
    public List<Consulta> getConsultasRealizadas() {
    return consultas;
    }
    private List<Consulta> listaEspera = new ArrayList<>();
    private List<Medico> medicos = new ArrayList<>();
    private List<Paciente> pacientes = new ArrayList<>();
    private List<Consulta> consultas = new ArrayList<>();

    private Medico medicoLogado = null;
    private Paciente pacienteLogado = null;

    //  AUXILIARES

    private List<Integer> getAvaliacoesMedico(Medico medico) {
        List<Integer> avaliacoes = new ArrayList<>();
        for (Consulta c : consultas) {
            if (c.getMedico().equals(medico) && c.getAvaliacao() != null) {
                avaliacoes.add(c.getAvaliacao());
            }
        }
        return avaliacoes;
    }

    private long contarConsultasDoMedicoNoDia(Medico medico, LocalDateTime data) {
        return consultas.stream()
                .filter(c -> c.getMedico().equals(medico) &&
                        c.getDataHora().toLocalDate().equals(data.toLocalDate()))
                .count();
    }

    // LOGIN 

    public boolean loginMedico(String nome) {
        for (Medico m : medicos) {
            if (m.getNome().equalsIgnoreCase(nome)) {
                medicoLogado = m;
                pacienteLogado = null;
                return true;
            }
        }
        return false;
    }

    public boolean loginPaciente(String nome) {
        for (Paciente p : pacientes) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                pacienteLogado = p;
                medicoLogado = null;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        medicoLogado = null;
        pacienteLogado = null;
    }

    public void adicionarMedico(Medico medico) {
        medicos.add(medico);
    }

    public void adicionarPaciente(Paciente paciente) {
        pacientes.add(paciente);
    }


    public void listarMedicosPorFiltro(String filtro) {
        if (pacienteLogado == null) {
            System.out.println("Você precisa estar logado como paciente.");
            return;
        }

        String planoPaciente = pacienteLogado.getPlanoSaude().toLowerCase();
        String termo = filtro.toLowerCase();

        for (Medico m : medicos) {
            boolean nomeCombina = m.getNome().toLowerCase().contains(termo);
            boolean especialidadeCombina = m.getEspecialidade().toLowerCase().contains(termo);

            boolean planoAceito = planoPaciente.equals("não tenho") ||
                    m.getPlanosAceitos().stream()
                            .map(String::toLowerCase)
                            .anyMatch(p -> p.equals(planoPaciente));

            if ((nomeCombina || especialidadeCombina) && planoAceito) {
                List<Integer> avaliacoes = getAvaliacoesMedico(m);
                double media = 0;
                if (!avaliacoes.isEmpty()) {
                    int soma = 0;
                    for (int nota : avaliacoes) soma += nota;
                    media = (double) soma / avaliacoes.size();
                }

                System.out.println("Dr. " + m.getNome() +
                        " | Especialidade: " + m.getEspecialidade() +
                        " | Média de estrelas: " + (avaliacoes.isEmpty() ? "Sem avaliações" : String.format("%.1f", media)));

                if (!avaliacoes.isEmpty()) {
                    System.out.print("Últimas avaliações: ");
                    for (int i = Math.max(avaliacoes.size() - 3, 0); i < avaliacoes.size(); i++) {
                        System.out.print(avaliacoes.get(i) + " ");
                    }
                    System.out.println();
                }
                System.out.println();
            }
        }
    }

    // AGENDAR CONSULTA 

    public boolean agendarConsulta(String nomeMedico, LocalDateTime dataHora) {
        if (pacienteLogado == null) {
            System.out.println("Você precisa estar logado como paciente.");
            return false;
        }

        Medico medicoSelecionado = null;
        for (Medico m : medicos) {
            if (m.getNome().equalsIgnoreCase(nomeMedico)) {
                medicoSelecionado = m;
                break;
            }
        }

        if (medicoSelecionado == null) {
            System.out.println("Médico não encontrado.");
            return false;
        }

        String plano = pacienteLogado.getPlanoSaude();
        if (!plano.equalsIgnoreCase("não tenho") &&
                !medicoSelecionado.getPlanosAceitos().contains(plano)) {
            System.out.println("Este médico não aceita seu plano de saúde.");
            return false;
        }

        long consultasNoDia = contarConsultasDoMedicoNoDia(medicoSelecionado, dataHora);
        Consulta novaConsulta = new Consulta(medicoSelecionado, pacienteLogado, dataHora);

        if (consultasNoDia < 3) {
            consultas.add(novaConsulta);
            System.out.println("Consulta agendada com sucesso.");
        } else {
            listaEspera.add(novaConsulta);
            System.out.println("Horários do médico estão cheios neste dia.");
            System.out.println("Você foi adicionado à lista de espera.");
        }

        return true;
    }

    // CANCELAR CONSULTA 

    public boolean cancelarConsulta(LocalDateTime dataHora) {
    if (pacienteLogado == null) {
        System.out.println("Você precisa estar logado como paciente.");
        return false;
    }

    for (Consulta c : consultas) {
        if (c.getPaciente().equals(pacienteLogado) && c.getDataHora().equals(dataHora)) {
            if (c.isRealizada()) {
                System.out.println("Consulta já realizada. Não pode ser cancelada.");
                return false;
            }

            Medico medico = c.getMedico();
            LocalDateTime horarioCancelado = c.getDataHora();

            consultas.remove(c);
            System.out.println("Consulta cancelada.");

            // Verifica se há alguém na lista de espera para esse médico e dia
            for (Consulta espera : listaEspera) {
                if (espera.getMedico().equals(medico) &&
                    espera.getDataHora().toLocalDate().equals(horarioCancelado.toLocalDate())) {

                    // Remarca para o mesmo horário do cancelamento
                    Consulta novaConsulta = new Consulta(medico, espera.getPaciente(), horarioCancelado);
                    consultas.add(novaConsulta);
                    listaEspera.remove(espera);

                    System.out.println("Paciente " + espera.getPaciente().getNome() +
                                       " foi retirado da lista de espera e teve a consulta agendada para " +
                                       horarioCancelado);
                    break;
                }
            }

            return true;
        }
    }

    System.out.println("Consulta não encontrada.");
    return false;
}


    //  REALIZAR CONSULTA 

    public boolean realizarConsulta(LocalDateTime dataHora, String descricao) {
    if (medicoLogado == null) {
        System.out.println("Você precisa estar logado como médico.");
        return false;
    }

    for (Consulta c : consultas) {
        if (c.getMedico().equals(medicoLogado) && c.getDataHora().equals(dataHora)) {
            if (c.isRealizada()) {
                System.out.println("Consulta já foi realizada.");
                return false;
            }

            double valor = 0.0;
            if (c.getPaciente().getPlanoSaude().equalsIgnoreCase("não tenho")) {
                String esp = medicoLogado.getEspecialidade().toLowerCase();
                switch (esp) {
                    case "cardiologia" -> valor = 300.00;
                    case "dermatologia" -> valor = 200.00;
                    case "clínico geral" -> valor = 150.00;
                    default -> valor = 250.00;
                }
            }

            c.realizarConsulta(descricao, valor);
            System.out.println("Consulta realizada com sucesso.");
            if (valor > 0) {
                System.out.printf("O paciente deverá pagar: R$ %.2f\n", valor);
            } else {
                System.out.println("Consulta coberta pelo plano de saúde.");
            }

            return true;
        }
    }

    System.out.println("Consulta não encontrada.");
    return false;
}



    //  AVALIAR CONSULTA 

    public boolean avaliarConsulta(LocalDateTime dataHora, int nota, String comentario) {
    if (pacienteLogado == null) {
        System.out.println("Você precisa estar logado como paciente.");
        return false;
    }

    for (Consulta c : consultas) {
        if (c.getPaciente().equals(pacienteLogado) && c.getDataHora().equals(dataHora)) {
            if (!c.isRealizada()) {
                System.out.println("A consulta ainda não foi realizada.");
                return false;
            }

            c.avaliar(nota, comentario);
            System.out.println(" Avaliação registrada.");
            return true;
        }
    }

    System.out.println("Consulta não encontrada.");
    return false;
}


    //  LISTAGENS 

    public void listarConsultas() {
        for (Consulta c : consultas) {
            System.out.println(c);
        }
    }

    public void listarMedicos() {
        for (Medico m : medicos) {
            System.out.println(m);
        }
    }

    public void listarPacientes() {
        for (Paciente p : pacientes) {
            System.out.println(p);
        }
    }
    public void cadastrarMedico(Scanner scanner) {
        System.out.print("Nome do médico: ");
        String nome = scanner.nextLine();

        System.out.print("Especialidade: ");
        String especialidade = scanner.nextLine();

        System.out.print("Planos aceitos (separados por vírgula): ");
        String planosStr = scanner.nextLine();
        List<String> planos = Arrays.asList(planosStr.split("\\s*,\\s*"));

        Medico novoMedico = new Medico(nome, especialidade, planos);
        medicos.add(novoMedico);
        System.out.println(" Médico cadastrado com sucesso!");
    }

    public void cadastrarPaciente(Scanner scanner) {
        System.out.print("Nome do paciente: ");
        String nome = scanner.nextLine();

        System.out.print("Idade: ");
        int idade = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Plano de saúde (ou digite 'não tenho'): ");
        String plano = scanner.nextLine();

        Paciente novoPaciente = new Paciente(nome, idade, plano);
        pacientes.add(novoPaciente);
        System.out.println(" Paciente cadastrado com sucesso!");


    }

    public List<Medico> getMedicos(){
        return medicos;
    }

    public List<Paciente> getPacientes(){
        return pacientes;
    }

    public  Medico getMedicoLogado(){
        return medicoLogado;
    }

    public Paciente getPacienteLogado(){
        return pacienteLogado;
    }

    // RETORNAR CONSULTAS FUTURAS
public List<Consulta> getConsultasFuturasDoPaciente() {
    List<Consulta> futuras = new ArrayList<>();
    if (pacienteLogado == null) return futuras;

    for (Consulta c : consultas) {
        if (c.getPaciente().equals(pacienteLogado) && !c.isRealizada()) {
            futuras.add(c);
        }
    }
    return futuras;
}

// RETORNAR CONSULTAS REALIZADAS
public List<Consulta> getConsultasRealizadasDoPaciente() {
    List<Consulta> realizadas = new ArrayList<>();
    if (pacienteLogado == null) return realizadas;

    for (Consulta c : consultas) {
        if (c.getPaciente().equals(pacienteLogado) && c.isRealizada()) {
            realizadas.add(c);
        }
    }
    return realizadas;
}



    public List<Consulta> getConsultasPendentesDoMedico() {
    List<Consulta> pendentes = new ArrayList<>();
    if (medicoLogado == null) return pendentes;

    for (Consulta c : consultas) {
        if (c.getMedico().equals(medicoLogado) && !c.isRealizada()) {
            pendentes.add(c);
        }
    }
    return pendentes;
}

public List<Consulta> getAvaliacoesRecebidas() {
    List<Consulta> avaliadas = new ArrayList<>();
    if (medicoLogado == null) return avaliadas;

    for (Consulta c : consultas) {
        if (c.getMedico().equals(medicoLogado) && c.isRealizada() && c.getAvaliacao() != null) {
            avaliadas.add(c);
        }
    }
    return avaliadas;
}

}

