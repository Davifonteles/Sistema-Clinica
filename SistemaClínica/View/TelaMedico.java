package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaMedico extends JFrame {
    private final SistemaController controller;
    private final DefaultListModel<String> modeloConsultas = new DefaultListModel<>();
    private final JList<String> listaConsultas = new JList<>(modeloConsultas);
    private final List<Consulta> consultasPendentes;

    public TelaMedico(SistemaController controller) {
        this.controller = controller;
        this.consultasPendentes = controller.getConsultasPendentesDoMedico();

        setTitle("Área do Médico");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel painelPrincipal = new JPanel();
        painelPrincipal.setLayout(new BoxLayout(painelPrincipal, BoxLayout.Y_AXIS));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(painelPrincipal);

        // Título
        JLabel lblTitulo = new JLabel("Consultas Pendentes", SwingConstants.CENTER);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelPrincipal.add(lblTitulo);

        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));

        // Lista
        carregarConsultas();
        JScrollPane scroll = new JScrollPane(listaConsultas);
        scroll.setPreferredSize(new Dimension(400, 150));
        painelPrincipal.add(scroll);

        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 15)));

        // Botões
        JButton btnRealizar = new JButton("Realizar Consulta");
        JButton btnAvaliacoes = new JButton("Ver Avaliações Recebidas");
        JButton btnLogout = new JButton("Logout");

        btnRealizar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAvaliacoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelPrincipal.add(btnRealizar);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 8)));
        painelPrincipal.add(btnAvaliacoes);
        painelPrincipal.add(Box.createRigidArea(new Dimension(0, 8)));
        painelPrincipal.add(btnLogout);

        // AÇÕES
        btnRealizar.addActionListener(evt -> {
            int index = listaConsultas.getSelectedIndex();
            if (index != -1) {
                Consulta selecionada = consultasPendentes.get(index);

                String descricao = JOptionPane.showInputDialog(
                        this,
                        "Digite a descrição da consulta:",
                        "Realizar Consulta",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (descricao != null && !descricao.trim().isEmpty()) {
                    boolean sucesso = controller.realizarConsulta(selecionada.getDataHora(), descricao);
                    if (sucesso) {
                        JOptionPane.showMessageDialog(this, " Consulta realizada com sucesso!");
                        atualizarLista();
                    } else {
                        JOptionPane.showMessageDialog(this, " Erro ao realizar consulta.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta da lista.");
            }
        });

        btnAvaliacoes.addActionListener(evt -> {
            List<Consulta> avaliacoes = controller.getAvaliacoesRecebidas();
            if (avaliacoes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhuma avaliação recebida.");
            } else {
                JTextArea area = new JTextArea();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (Consulta c: controller.getAvaliacoesRecebidas()){
                    String dataFormatada = c.getDataHora().format(formatter);
                    area.append(dataFormatada +
                    "| Paciente: " + c.getPaciente().getNome() +
                    "- Idade: " + c.getPaciente().getIdade() +
                    "- Plano: " + c.getPaciente().getPlanoSaude() +
                    "| Nota: " + c.getAvaliacao() +
                    "| Comentário: " + c.getComentario() + "\n");
                }
                area.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(area);
                scrollPane.setPreferredSize(new Dimension(400, 200));
                JOptionPane.showMessageDialog(this, scrollPane, "Avaliações Recebidas", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnLogout.addActionListener(evt -> {
            controller.logout();
            new TelaLogin(controller);
            dispose();
        });

        setVisible(true);
    }

    private void carregarConsultas() {
        modeloConsultas.clear();
        for (Consulta c : consultasPendentes) {
            if (!c.isRealizada()) {
                modeloConsultas.addElement(
                        c.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " - " + c.getPaciente().getNome()
                );
            }
        }
    }

    private void atualizarLista() {
        consultasPendentes.removeIf(Consulta::isRealizada);
        carregarConsultas();
    }
}
