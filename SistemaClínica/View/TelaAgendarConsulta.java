package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;
import SistemaClínica.Model.Medico;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaAgendarConsulta extends JFrame {
    private final SistemaController controller;

    private JComboBox<String> comboMedicos;
    private JComboBox<String> comboDias;
    private JComboBox<String> comboHorarios;
    private JButton btnAgendar;
    private JButton btnVoltar;

    public TelaAgendarConsulta(SistemaController controller) {
        this.controller = controller;
        setTitle("Agendar Consulta");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));

        comboMedicos = new JComboBox<>();
        comboHorarios = new JComboBox<>();
        btnAgendar = new JButton("Agendar");
        btnVoltar = new JButton("Voltar");

        // Preenche médicos
        List<Medico> medicos = controller.listarMedicos();
        for (Medico m : medicos) {
            comboMedicos.addItem(m.getNome());
        }

        // Atualiza horários ao escolher médico
        comboMedicos.addActionListener(e -> atualizarHorarios());

        btnAgendar.addActionListener(e -> {
    String nomeMedico = (String) comboMedicos.getSelectedItem();
    String horarioStr = (String) comboHorarios.getSelectedItem();

    if (nomeMedico == null || horarioStr == null) {
        JOptionPane.showMessageDialog(this, "Selecione um médico e um horário.");
        return;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    LocalDateTime dataHora = LocalDateTime.parse(horarioStr, formatter);

    boolean sucesso = controller.agendarConsulta(nomeMedico, dataHora);
    if (sucesso) {
        JOptionPane.showMessageDialog(this, "Consulta agendada com sucesso!");
        new TelaPaciente(controller);
        dispose();
    } else {
        JOptionPane.showMessageDialog(this, " Não foi possível agendar.");
    }
});

        btnVoltar.addActionListener(e -> dispose());

        add(new JLabel("Selecione o médico:"));
        add(comboMedicos);
        add(new JLabel("Horários disponíveis:"));
        add(comboHorarios);
        add(btnAgendar);
        add(btnVoltar);

        atualizarHorarios();
        setVisible(true);
    }

    private void atualizarHorarios() {
    comboHorarios.removeAllItems();
    String nomeMedico = (String) comboMedicos.getSelectedItem();

    if (nomeMedico == null) return;

    List<Consulta> consultas = controller.getConsultasRealizadas();

    // HORARIOS PARA OS PROXIMOS 5 DIAS
    LocalDate hoje = LocalDate.now();
    int diasGerados = 0;

    while (diasGerados < 5) {
        hoje = hoje.plusDays(1);
        if (hoje.getDayOfWeek().getValue() >= 6) continue; //PULAR FIM DE SEMANA

        for (int i = 9; i <= 17; i += 2) { 
            LocalDateTime horario = LocalDateTime.of(hoje, LocalTime.of(i, 0));
            boolean ocupado = consultas.stream()
                .anyMatch(c -> c.getMedico().getNome().equalsIgnoreCase(nomeMedico)
                        && c.getDataHora().equals(horario));
            if (!ocupado) {
                comboHorarios.addItem(horario.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
        }

        diasGerados++;
    }
}
}