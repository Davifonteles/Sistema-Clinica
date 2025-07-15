package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroPaciente extends JFrame {
    public TelaCadastroPaciente(SistemaController controller, TelaLogin telaLogin) {
        setTitle("Cadastro de Paciente");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel lblNome = new JLabel("Nome:");
        JTextField txtNome = new JTextField();

        JLabel lblIdade = new JLabel("Idade:");
        JTextField txtIdade = new JTextField();

        JLabel lblPlano = new JLabel("Plano de Saúde:");
        JTextField txtPlano = new JTextField();

        JButton btnCadastrar = new JButton("Cadastrar");

        btnCadastrar.addActionListener(e -> {
            String nome = txtNome.getText();
            int idade = Integer.parseInt(txtIdade.getText());
            String plano = txtPlano.getText();
            controller.getSistema().adicionarPaciente(new SistemaClínica.Model.Paciente(nome, idade, plano));

            JOptionPane.showMessageDialog(this, "Paciente cadastrado com sucesso!");
            telaLogin.atualizarListaPacientes();
            telaLogin.setVisible(true);
            dispose();
        });

        add(lblNome);
        add(txtNome);
        add(lblIdade);
        add(txtIdade);
        add(lblPlano);
        add(txtPlano);
        add(new JLabel());
        add(btnCadastrar);

        setVisible(true);
    }
}