package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TelaLogin extends JFrame {
    private JComboBox<String> comboPacientes;
    private JComboBox<String> comboMedicos;
    private final SistemaController controller;

    public TelaLogin(SistemaController controller) {
        this.controller = controller;
        setTitle("Tela de Login");
        setSize(420, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 1, 10, 10));

        // Paciente
        comboPacientes = new JComboBox<>();
        atualizarListaPacientes();
        JButton btnLoginPaciente = new JButton("Login como Paciente");
        JButton btnCadastrarPaciente = new JButton("Cadastrar Paciente");

        // Médico
        comboMedicos = new JComboBox<>();
        atualizarListaMedicos();
        JButton btnLoginMedico = new JButton("Login como Médico");
        JButton btnCadastrarMedico = new JButton("Cadastrar Médico");

        // Ações de login
        btnLoginPaciente.addActionListener((ActionEvent e) -> {
            String nome = (String) comboPacientes.getSelectedItem();
            if (nome != null && controller.loginPaciente(nome)) {
                JOptionPane.showMessageDialog(this, "Login como paciente realizado. ");
                new TelaPaciente(controller);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, " Paciente não encontrado.");
            }
        });

        btnLoginMedico.addActionListener((ActionEvent e) -> {
            String nome = (String) comboMedicos.getSelectedItem();
            if (nome != null && controller.loginMedico(nome)) {
                JOptionPane.showMessageDialog(this, " Login como médico realizado!");
                new TelaMedico(controller);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, " Médico não encontrado.");
            }
        });

        // Ações de cadastro
        btnCadastrarPaciente.addActionListener(e -> {
            new TelaCadastroPaciente(controller, this);
        });

        btnCadastrarMedico.addActionListener(e -> {
            new TelaCadastroMedico(controller, this);
        });

        // Painéis
        JPanel painelPacientes = new JPanel();
        painelPacientes.add(new JLabel("Paciente:"));
        painelPacientes.add(comboPacientes);
        painelPacientes.add(btnLoginPaciente);

        JPanel painelCadastrarPac = new JPanel();
        painelCadastrarPac.add(btnCadastrarPaciente);

        JPanel painelMedicos = new JPanel();
        painelMedicos.add(new JLabel("Médico:"));
        painelMedicos.add(comboMedicos);
        painelMedicos.add(btnLoginMedico);

        JPanel painelCadastrarMed = new JPanel();
        painelCadastrarMed.add(btnCadastrarMedico);

        add(painelPacientes);
        add(painelCadastrarPac);
        add(painelMedicos);
        add(painelCadastrarMed);
        setVisible(true);
    }

    public void atualizarListaPacientes() {
        comboPacientes.removeAllItems();
        List<Paciente> pacientes = controller.listarPacientes();
        for (Paciente p : pacientes) {
            comboPacientes.addItem(p.getNome());
        }
    }

    public void atualizarListaMedicos() {
        comboMedicos.removeAllItems();
        List<Medico> medicos = controller.listarMedicos();
        for (Medico m : medicos) {
            comboMedicos.addItem(m.getNome());
        }
    }
}
