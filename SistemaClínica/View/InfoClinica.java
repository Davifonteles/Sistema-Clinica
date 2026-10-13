package SistemaClínica.View;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;

/**
 * Consultas de leitura que as telas precisam (médias, horários, compatibilidade de plano).
 * Não muda nada no sistema: só lê o que o Controller já expõe.
 */
public final class InfoClinica {
    private InfoClinica() {}

    public static final String SEM_PLANO = "não tenho";
    /** Horários oferecidos por dia, como na tela original: 9h às 17h, de 2 em 2 horas. */
    public static final int[] HORAS = {9, 11, 13, 15, 17};
    /** Limite de consultas por médico por dia antes de ir para a lista de espera (regra do Model). */
    public static final int LIMITE_POR_DIA = 3;

    public static boolean semPlano(Paciente p) {
        return p.getPlanoSaude().equalsIgnoreCase(SEM_PLANO);
    }

    public static String nomePlano(String plano) {
        return plano.equalsIgnoreCase(SEM_PLANO) ? "Particular" : plano;
    }

    /** Mesma verificação feita em SistemaConsultaMedica.agendarConsulta. */
    public static boolean atende(Medico m, Paciente p) {
        return semPlano(p) || m.getPlanosAceitos().contains(p.getPlanoSaude());
    }

    /** Valor cobrado de quem não tem plano (mesma tabela de SistemaConsultaMedica.realizarConsulta). */
    public static double valorParticular(Medico m) {
        return switch (m.getEspecialidade().toLowerCase()) {
            case "cardiologia" -> 300.00;
            case "dermatologia" -> 200.00;
            case "clínico geral" -> 150.00;
            default -> 250.00;
        };
    }

    public static List<Consulta> todas(SistemaController c) {
        return c.getConsultasRealizadas(); // no Model, esse método devolve todas as consultas
    }

    public static List<Consulta> avaliacoesDe(SistemaController c, Medico m) {
        List<Consulta> lista = new ArrayList<>();
        for (Consulta x : todas(c)) {
            if (x.getMedico() == m && x.getAvaliacao() != null) lista.add(x);
        }
        return lista;
    }

    public static double media(List<Consulta> avaliadas) {
        if (avaliadas.isEmpty()) return 0;
        double soma = 0;
        for (Consulta x : avaliadas) soma += x.getAvaliacao();
        return soma / avaliadas.size();
    }

    public static boolean ocupado(SistemaController c, Medico m, LocalDateTime horario) {
        for (Consulta x : todas(c)) {
            if (x.getMedico() == m && x.getDataHora().equals(horario)) return true;
        }
        return false;
    }

    public static long consultasNoDia(SistemaController c, Medico m, LocalDate dia) {
        return todas(c).stream()
            .filter(x -> x.getMedico() == m && x.getDataHora().toLocalDate().equals(dia))
            .count();
    }

    /** Próximos 5 dias úteis a partir de amanhã (igual à tela original). */
    public static List<LocalDate> proximosDiasUteis() {
        List<LocalDate> dias = new ArrayList<>();
        LocalDate d = LocalDate.now();
        while (dias.size() < 5) {
            d = d.plusDays(1);
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) dias.add(d);
        }
        return dias;
    }

    public static LocalDateTime horario(LocalDate dia, int hora) {
        return LocalDateTime.of(dia, LocalTime.of(hora, 0));
    }

    public static List<Consulta> ordenar(List<Consulta> lista, boolean maisRecentePrimeiro) {
        List<Consulta> copia = new ArrayList<>(lista);
        Comparator<Consulta> cmp = Comparator.comparing(Consulta::getDataHora);
        copia.sort(maisRecentePrimeiro ? cmp.reversed() : cmp);
        return copia;
    }
}
