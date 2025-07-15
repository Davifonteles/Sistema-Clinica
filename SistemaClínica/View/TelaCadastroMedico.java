package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class TelaCadastroMedico extends JFrame {
    public TelaCadastroMedico(SistemaController controller, TelaLogin telaLogin) {
        setTitle("Cadastro de Médico");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField();

        JLabel lblEsp = new JLabel("Especialidade:");
        JTextField txtEsp = new JTextField();

        JLabel lblPlanos = new JLabel("Planos aceitos (separados por vírgula):");
        JTextField txtPlanos = new JTextField();

        JButton btnCadastrar = new JButton("Cadastrar");

        btnCadastrar.addActionListener(e -> {
            String nome = txtNome.getText();
            String esp = txtEsp.getText();
            var planos = Arrays.asList(txtPlanos.getText().split("\s*,\s*"));
            controller.getSistema().adicionarMedico(new SistemaClínica.Model.Medico(nome, esp, planos));

            JOptionPane.showMessageDialog(this, " Médico cadastrado com sucesso!");
            telaLogin.atualizarListaMedicos();
            telaLogin.setVisible(true);
            dispose();
        });

        add(lblNome);
        add(txtNome);
        add(lblEsp);
        add(txtEsp);
        add(lblPlanos);
        add(txtPlanos);
        add(new JLabel());
        add(btnCadastrar);

        setVisible(true);
    }
}