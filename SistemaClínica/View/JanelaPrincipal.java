package SistemaClínica.View;

import java.awt.*;
import javax.swing.*;

import SistemaClínica.Controller.SistemaController;

/** Janela única do sistema: troca entre login, área do paciente e área do médico. */
public class JanelaPrincipal extends JFrame {
    private final SistemaController controller;

    public JanelaPrincipal(SistemaController controller) {
        super("Sistema Clínica");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 680));
        setSize(1240, 800);
        setLocationRelativeTo(null);
        mostrarLogin();
    }

    public void mostrarLogin() {
        trocar(new PainelLogin(controller, this));
    }

    public void mostrarAreaPaciente() {
        trocar(new AreaPaciente(controller, this));
    }

    public void mostrarAreaMedico() {
        trocar(new AreaMedico(controller, this));
    }

    private void trocar(JComponent tela) {
        setContentPane(tela);
        revalidate();
        repaint();
    }

    /** Símbolo da clínica: cruz branca num quadrado verde-azulado. */
    public static Icon logo(int tam) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.PRIMARIA);
                g2.fillRoundRect(x, y, tam, tam, tam / 3, tam / 3);
                g2.setColor(Color.WHITE);
                int b = Math.max(3, tam / 5), l = tam * 3 / 5;
                g2.fillRoundRect(x + (tam - b) / 2, y + (tam - l) / 2, b, l, b / 2, b / 2);
                g2.fillRoundRect(x + (tam - l) / 2, y + (tam - b) / 2, l, b, b / 2, b / 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return tam; }
            @Override public int getIconHeight() { return tam; }
        };
    }
}
