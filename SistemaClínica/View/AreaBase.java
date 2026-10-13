package SistemaClínica.View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import SistemaClínica.Controller.SistemaController;

/**
 * Estrutura comum das áreas logadas: barra do topo, menu lateral e conteúdo.
 * Cada página é recriada ao ser aberta, então sempre mostra os dados atuais.
 */
public abstract class AreaBase extends JPanel {

    public enum TipoAviso { OK, ESPERA, ERRO }

    protected final SistemaController controller;
    protected final JanelaPrincipal janela;

    private final Map<String, Supplier<JComponent>> paginas = new LinkedHashMap<>();
    private final Map<String, ItemMenu> itens = new LinkedHashMap<>();
    private final JPanel menu = new JPanel();
    private final JPanel conteudo = new JPanel(new BorderLayout());
    private final JPanel aviso = new JPanel(new BorderLayout());
    private final JLabel textoAviso = Tema.rotulo("", Font.BOLD, 14, Tema.TEXTO);
    private Timer timerAviso;
    private String atual;

    protected AreaBase(SistemaController controller, JanelaPrincipal janela,
                       String nome, String detalhe, String papel) {
        super(new BorderLayout());
        this.controller = controller;
        this.janela = janela;
        setBackground(Tema.FUNDO);

        add(barraTopo(nome, detalhe, papel), BorderLayout.NORTH);

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Tema.SUPERFICIE);
        menu.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 1, Tema.BORDA), new EmptyBorder(18, 12, 18, 12)));
        menu.setPreferredSize(new Dimension(230, 10));
        add(menu, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(22, 28, 22, 28));

        aviso.setOpaque(false);
        Tema.Cartao caixa = new Tema.Cartao(new BorderLayout());
        caixa.setBorder(new EmptyBorder(11, 16, 11, 16));
        caixa.add(textoAviso, BorderLayout.CENTER);
        aviso.add(caixa);
        aviso.setVisible(false);
        centro.add(aviso, BorderLayout.NORTH);

        conteudo.setOpaque(false);
        centro.add(conteudo, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private JComponent barraTopo(String nome, String detalhe, String papel) {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.SUPERFICIE);
        barra.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Tema.BORDA), new EmptyBorder(12, 20, 12, 20)));

        JLabel marca = Tema.rotulo("Sistema Clínica", Font.BOLD, 18, Tema.TEXTO);
        marca.setIcon(JanelaPrincipal.logo(28));
        marca.setIconTextGap(10);
        barra.add(marca, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        direita.setOpaque(false);
        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel n = Tema.rotulo(nome, Font.BOLD, 14, Tema.TEXTO);
        n.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel d = Tema.rotulo(papel + " · " + detalhe, Font.PLAIN, 12, Tema.SUAVE);
        d.setHorizontalAlignment(SwingConstants.RIGHT);
        textos.add(n);
        textos.add(d);
        direita.add(textos);
        direita.add(new JLabel(Tema.avatar(nome, 38)));
        Tema.Botao sair = Tema.Botao.secundario("Sair").pequeno();
        sair.addActionListener(e -> {
            controller.logout();
            janela.mostrarLogin();
        });
        direita.add(sair);
        barra.add(direita, BorderLayout.EAST);
        return barra;
    }

    /** Registra uma página no menu lateral. */
    protected void pagina(String id, String rotulo, Supplier<JComponent> fabrica) {
        paginas.put(id, fabrica);
        ItemMenu item = new ItemMenu(rotulo, () -> abrir(id));
        itens.put(id, item);
        item.setAlignmentX(0);
        menu.add(item);
        menu.add(Box.createVerticalStrut(4));
    }

    public void abrir(String id) {
        atual = id;
        itens.forEach((k, v) -> v.setSelecionado(k.equals(id)));
        conteudo.removeAll();
        conteudo.add(paginas.get(id).get(), BorderLayout.CENTER);
        conteudo.revalidate();
        conteudo.repaint();
    }

    /** Recria a página aberta (depois de agendar, cancelar etc.). */
    public void atualizar() {
        if (atual != null) abrir(atual);
    }

    public void avisar(String mensagem, TipoAviso tipo) {
        Tema.Cartao caixa = (Tema.Cartao) aviso.getComponent(0);
        switch (tipo) {
            case OK -> { caixa.cores(Tema.OK_CLARO, null); textoAviso.setForeground(Tema.OK); }
            case ESPERA -> { caixa.cores(Tema.ESPERA_CLARO, null); textoAviso.setForeground(Tema.ESPERA); }
            case ERRO -> { caixa.cores(Tema.PERIGO_CLARO, null); textoAviso.setForeground(Tema.PERIGO); }
        }
        textoAviso.setText(mensagem);
        aviso.setVisible(true);
        revalidate();
        if (timerAviso != null) timerAviso.stop();
        timerAviso = new Timer(6000, e -> aviso.setVisible(false));
        timerAviso.setRepeats(false);
        timerAviso.start();
    }

    /** Item clicável do menu lateral. */
    private static class ItemMenu extends JLabel {
        private boolean selecionado;
        private boolean sobre;

        ItemMenu(String texto, Runnable acao) {
            super(texto);
            setFont(Tema.fonte(Font.BOLD, 14));
            setBorder(new EmptyBorder(11, 14, 11, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFocusable(true);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { acao.run(); }
                @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
            });
            getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "abrir");
            getActionMap().put("abrir", new AbstractAction() {
                @Override public void actionPerformed(java.awt.event.ActionEvent e) { acao.run(); }
            });
        }

        void setSelecionado(boolean s) {
            selecionado = s;
            setForeground(s ? Tema.PRIMARIA_ESCURA : Tema.SUAVE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (selecionado || sobre) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(selecionado ? Tema.PRIMARIA_CLARA : Tema.SUPERFICIE_2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (selecionado) {
                    g2.setColor(Tema.PRIMARIA);
                    g2.fillRoundRect(0, 10, 4, getHeight() - 20, 4, 4);
                }
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
