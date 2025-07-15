package SistemaClínica.View;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaPaciente extends JFrame {
    private final SistemaController controller;
    private final DefaultListModel<String> modeloFuturas = new DefaultListModel<>();
    private final DefaultListModel<String> modeloRealizadas = new DefaultListModel<>();
    private final JList<String> listaFuturas = new JList<>(modeloFuturas);
    private final JList<String> listaRealizadas = new JList<>(modeloRealizadas);

    private List<Consulta> futuras;
    private List<Consulta> realizadas;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public TelaPaciente(SistemaController controller) {
        this.controller = controller;
        setTitle("Área do Paciente");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        atualizarConsultas();

        // TÍTULO
        JLabel lblTitulo = new JLabel("Consultas Agendadas e Realizadas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        listaFuturas.setFont(new Font("Arial", Font.PLAIN, 14));
        listaRealizadas.setFont(new Font("Arial", Font.PLAIN, 14));

        // PAINEL PAIA
        JPanel painelListas = new JPanel(new GridLayout(1, 2, 10, 10));
        painelListas.setBorder(BorderFactory.createEmptyBorder(10, 10 , 10, 10));
        painelListas.add(new JScrollPane(listaFuturas));
        painelListas.add(new JScrollPane(listaRealizadas));
        add(painelListas, BorderLayout.CENTER);

        // BOTOES
        JButton btnAgendar = new JButton("Agendar Consulta");
        JButton btnCancelar = new JButton("Cancelar Consulta");
        JButton btnAvaliar = new JButton("Avaliar Consulta");
        JButton btnBuscar = new JButton("Buscar Médico");
        JButton btnLogout = new JButton("Logout");

        // PAINEL DE BOTOES

        Font fonteBotao = new Font("Arial", Font.BOLD, 13);
        for (JButton botao: new JButton[]{btnAgendar, btnCancelar, btnAvaliar, btnBuscar, btnLogout}){
            botao.setFont(fonteBotao);
        }


        JPanel painelBotoes = new JPanel(new GridLayout(1, 5, 10, 10));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        painelBotoes.add(btnAgendar);
        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnAvaliar);
        painelBotoes.add(btnBuscar);
        painelBotoes.add(btnLogout);
        add(painelBotoes, BorderLayout.SOUTH);

        //  AÇÕES 

        btnAgendar.addActionListener(e -> {
            new TelaAgendarConsulta(controller);
            dispose();
        });

        btnCancelar.addActionListener((ActionEvent e) -> {
            int index = listaFuturas.getSelectedIndex();
            if (index != -1) {
                Consulta consulta = futuras.get(index);
                boolean cancelado = controller.cancelarConsulta(consulta.getDataHora());
                if (cancelado) {
                    JOptionPane.showMessageDialog(this, " Consulta cancelada.");
                    atualizarConsultas();
                } else {
                    JOptionPane.showMessageDialog(this, " Erro ao cancelar.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para cancelar.");
            }
        });

        btnAvaliar.addActionListener((ActionEvent e) -> {
            int index = listaRealizadas.getSelectedIndex();
            if (index != -1) {
                Consulta consulta = realizadas.get(index);
                String notaStr = JOptionPane.showInputDialog("Nota (1 a 5):");
                String comentario = JOptionPane.showInputDialog("Comentário:");
                try {
                    int nota = Integer.parseInt(notaStr);
                    if (nota < 1 || nota > 5) throw new Exception();
                    boolean sucesso = controller.avaliarConsulta(consulta.getDataHora(), nota, comentario);
                    if (sucesso) {
                        JOptionPane.showMessageDialog(this, " Avaliação registrada.");
                        atualizarConsultas();
                    } else {
                        JOptionPane.showMessageDialog(this, " Não foi possível avaliar.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, " Nota inválida.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para avaliar.");
            }
        });

        btnBuscar.addActionListener(e -> {
            String termo = JOptionPane.showInputDialog("Digite nome ou especialidade:");
            controller.buscarMedicoPorFiltro(termo);
        });

        btnLogout.addActionListener(e -> {
            controller.logout();
            new TelaLogin(controller);
            dispose();
        });

        setVisible(true);
    }

    private void atualizarConsultas() {
        modeloFuturas.clear();
        modeloRealizadas.clear();

        futuras = controller.getSistema().getConsultasFuturasDoPaciente();
        realizadas = controller.getSistema().getConsultasRealizadasDoPaciente();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Consulta c : futuras) {
            modeloFuturas.addElement(c.getDataHora().format(formatter) + " - " + c.getMedico().getNome());
        }

        for (Consulta r : realizadas) {
            modeloRealizadas.addElement(r.getDataHora().format(formatter) + " - " + r.getMedico().getNome());

        }
        
    }
}
