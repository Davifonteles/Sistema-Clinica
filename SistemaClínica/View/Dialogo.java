package SistemaClínica.View;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Janela de confirmação/formulário no estilo do sistema (substitui os JOptionPane). */
public class Dialogo extends JDialog {
    private boolean confirmado = false;
    private final JLabel erro = Tema.rotulo(" ", Font.BOLD, 13, Tema.PERIGO);

    public Dialogo(Component pai, String titulo, String subtitulo, JComponent corpo,
                   String textoOk, boolean perigo, Supplier<String> validar) {
        super(SwingUtilities.getWindowAncestor(pai), titulo, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Tema.SUPERFICIE);
        raiz.setBorder(new EmptyBorder(22, 24, 18, 24));

        JPanel topo = Tema.titulo(titulo, subtitulo);
        ((JLabel) topo.getComponent(0)).setFont(Tema.fonte(Font.BOLD, 20));
        raiz.add(topo, BorderLayout.NORTH);
        if (corpo != null) raiz.add(corpo, BorderLayout.CENTER);

        JPanel baixo = new JPanel(new BorderLayout(0, 8));
        baixo.setOpaque(false);
        erro.setVisible(false);
        baixo.add(erro, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        Tema.Botao cancelar = Tema.Botao.secundario("Voltar");
        Tema.Botao ok = perigo ? new Tema.Botao(textoOk, Tema.PERIGO, Tema.PERIGO.darker(), Color.WHITE, null)
                               : Tema.Botao.primario(textoOk);
        cancelar.addActionListener(e -> dispose());
        ok.addActionListener(e -> {
            String msg = validar == null ? null : validar.get();
            if (msg != null) {
                erro.setText(msg);
                erro.setVisible(true);
                pack();
                return;
            }
            confirmado = true;
            dispose();
        });
        botoes.add(cancelar);
        botoes.add(ok);
        baixo.add(botoes, BorderLayout.SOUTH);
        raiz.add(baixo, BorderLayout.SOUTH);

        setContentPane(raiz);
        getRootPane().setDefaultButton(perigo ? null : ok);
        getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(pai);
    }

    /** Mostra a janela e espera a resposta. */
    public boolean perguntar() {
        setVisible(true);
        return confirmado;
    }

    public static boolean confirmar(Component pai, String titulo, String mensagem, String textoOk, boolean perigo) {
        return new Dialogo(pai, titulo, mensagem, null, textoOk, perigo, null).perguntar();
    }
}
