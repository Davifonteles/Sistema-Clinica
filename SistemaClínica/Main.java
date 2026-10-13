package SistemaClínica;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;
import SistemaClínica.Model.SistemaConsultaMedica;
import SistemaClínica.View.JanelaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        SistemaConsultaMedica sistema = new SistemaConsultaMedica();

        // EXEMPLO TESTE 1
        sistema.adicionarMedico(new Medico("Davi", "Cirurgia", List.of("Unimed", "Hapvida")));
        sistema.adicionarMedico(new Medico("Ana", "Dermatologia", List.of("Hapvida")));
        sistema.adicionarMedico(new Medico("Paulo", "Clínico Geral", List.of("Unimed")));

        sistema.adicionarPaciente(new Paciente("Maria", 30, "não tenho"));
        sistema.adicionarPaciente(new Paciente("Fernanda", 22, "Hapvida"));

        SistemaController controller = new SistemaController(sistema);

        // ABRIR INTERFACE
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("swing.boldMetal", Boolean.FALSE);
        } catch (Exception ignorado) {
            // usa o visual padrão
        }
        SwingUtilities.invokeLater(() -> new JanelaPrincipal(controller).setVisible(true));
    }
}
