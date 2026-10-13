package SistemaClínica.View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/** Cores, fontes e componentes visuais usados por todas as telas. */
public final class Tema {
    private Tema() {}

    // ------------------------------------------------------------- cores
    public static final Color FUNDO = new Color(0xF2F5F4);
    public static final Color SUPERFICIE = Color.WHITE;
    public static final Color SUPERFICIE_2 = new Color(0xF7FAF9);
    public static final Color BORDA = new Color(0xDCE4E1);
    public static final Color TEXTO = new Color(0x17302C);
    public static final Color SUAVE = new Color(0x627C77);
    public static final Color PRIMARIA = new Color(0x0E7C74);
    public static final Color PRIMARIA_ESCURA = new Color(0x0A5E58);
    public static final Color PRIMARIA_CLARA = new Color(0xE2F1EE);
    public static final Color PERIGO = new Color(0xB9443B);
    public static final Color PERIGO_CLARO = new Color(0xFBEAE8);
    public static final Color ESPERA = new Color(0xA86A12);
    public static final Color ESPERA_CLARO = new Color(0xFCF1DE);
    public static final Color OK = new Color(0x2B7A4B);
    public static final Color OK_CLARO = new Color(0xE4F3E9);
    public static final Color ESTRELA = new Color(0xE5A11A);

    private static final Color[] CORES_AVATAR = {
        new Color(0x0E7C74), new Color(0x3B6FB6), new Color(0x8A4FB0),
        new Color(0xC0613A), new Color(0x2F8F5B), new Color(0xB2476E)
    };

    // ------------------------------------------------------------- datas em português
    public static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    public static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DIA_MES = DateTimeFormatter.ofPattern("dd/MM");
    public static final DateTimeFormatter DIA_LONGO = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", PT_BR);

    public static String diaSemanaCurto(LocalDateTime d) {
        String s = d.format(DateTimeFormatter.ofPattern("EEE", PT_BR)).replace(".", "");
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String mesCurto(LocalDateTime d) {
        return d.format(DateTimeFormatter.ofPattern("MMM", PT_BR)).replace(".", "").toUpperCase();
    }

    public static String diaLongo(LocalDateTime d) {
        String s = d.format(DIA_LONGO);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String dinheiro(double v) {
        return String.format(PT_BR, "R$ %,.2f", v);
    }

    // ------------------------------------------------------------- fontes e textos
    public static Font fonte(int estilo, float tamanho) {
        return new Font(Font.SANS_SERIF, estilo, Math.round(tamanho));
    }

    public static JLabel rotulo(String texto, int estilo, float tamanho, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(estilo, tamanho));
        l.setForeground(cor);
        return l;
    }

    /** Título de seção com subtítulo opcional. */
    public static JPanel titulo(String titulo, String subtitulo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = rotulo(titulo, Font.BOLD, 24, TEXTO);
        t.setAlignmentX(0);
        p.add(t);
        if (subtitulo != null) {
            p.add(Box.createVerticalStrut(4));
            JLabel s = rotulo(subtitulo, Font.PLAIN, 14, SUAVE);
            s.setAlignmentX(0);
            p.add(s);
        }
        return p;
    }

    public static JLabel secao(String texto) {
        JLabel l = rotulo(texto.toUpperCase(), Font.BOLD, 12, SUAVE);
        l.setBorder(new EmptyBorder(18, 2, 8, 0));
        return l;
    }

    public static Color corPara(String nome) {
        return CORES_AVATAR[Math.abs(nome.toLowerCase().hashCode()) % CORES_AVATAR.length];
    }

    public static String iniciais(String nome) {
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 0 || partes[0].isEmpty()) return "?";
        String i = partes[0].substring(0, 1);
        if (partes.length > 1) i += partes[partes.length - 1].substring(0, 1);
        return i.toUpperCase();
    }

    // ------------------------------------------------------------- campos
    public static Border bordaCampo() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDA, 1, true), new EmptyBorder(8, 10, 8, 10));
    }

    public static JTextField campo() {
        JTextField c = new JTextField();
        c.setFont(fonte(Font.PLAIN, 14));
        c.setBorder(bordaCampo());
        return c;
    }

    public static JPanel campoComRotulo(String rotulo, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(rotulo(rotulo, Font.BOLD, 13, TEXTO), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        p.setAlignmentX(0);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height));
        return p;
    }

    /** Linha horizontal sem recuo à esquerda, com espaço fixo entre os itens. */
    public static JPanel linha(int espaco, JComponent... itens) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        for (int i = 0; i < itens.length; i++) {
            if (i > 0) p.add(Box.createHorizontalStrut(espaco));
            itens[i].setAlignmentY(0.5f);
            p.add(itens[i]);
        }
        p.add(Box.createHorizontalGlue());
        return p;
    }

    /** Deixa combos e spinners com a mesma borda dos campos de texto. */
    public static void estilizar(JComponent c) {
        c.setFont(fonte(Font.PLAIN, 14));
        c.setBackground(SUPERFICIE);
        c.setBorder(BorderFactory.createLineBorder(BORDA, 1, true));
        if (c instanceof JComboBox<?> combo && combo.getEditor().getEditorComponent() instanceof JTextField tf) {
            tf.setBorder(new EmptyBorder(7, 9, 7, 9));
            tf.setBackground(SUPERFICIE);
        }
        if (c instanceof JSpinner sp && sp.getEditor() instanceof JSpinner.DefaultEditor ed) {
            ed.getTextField().setBorder(new EmptyBorder(7, 9, 7, 9));
            ed.getTextField().setHorizontalAlignment(JTextField.LEFT);
        }
    }

    public static JScrollPane rolagem(JComponent c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(null);
        s.setOpaque(false);
        s.getViewport().setOpaque(false);
        s.getVerticalScrollBar().setUnitIncrement(18);
        s.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return s;
    }

    // ------------------------------------------------------------- componentes

    /** Painel branco com cantos arredondados. */
    public static class Cartao extends JPanel {
        private Color fundo = SUPERFICIE;
        private Color borda = BORDA;

        public Cartao(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(16, 18, 16, 18));
        }

        public Cartao cores(Color fundo, Color borda) {
            this.fundo = fundo;
            this.borda = borda;
            repaint();
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            if (borda != null) {
                g2.setColor(borda);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Botão arredondado. */
    public static class Botao extends JButton {
        private final Color fundo;
        private final Color fundoSobre;
        private final Color corTexto;
        private final Color contorno;
        private boolean sobre;

        public Botao(String texto, Color fundo, Color fundoSobre, Color corTexto, Color contorno) {
            super(texto);
            this.fundo = fundo;
            this.fundoSobre = fundoSobre;
            this.corTexto = corTexto;
            this.contorno = contorno;
            setFont(fonte(Font.BOLD, 14));
            setForeground(corTexto);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(9, 16, 9, 16));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
            });
        }

        public static Botao primario(String t) {
            return new Botao(t, PRIMARIA, PRIMARIA_ESCURA, Color.WHITE, null);
        }

        public static Botao secundario(String t) {
            return new Botao(t, SUPERFICIE, SUPERFICIE_2, TEXTO, BORDA);
        }

        public static Botao perigo(String t) {
            return new Botao(t, PERIGO_CLARO, new Color(0xF6D5D1), PERIGO, null);
        }

        public static Botao leve(String t) {
            return new Botao(t, PRIMARIA_CLARA, new Color(0xD2EAE5), PRIMARIA_ESCURA, null);
        }

        public Botao pequeno() {
            setFont(fonte(Font.BOLD, 13));
            setBorder(new EmptyBorder(6, 12, 6, 12));
            return this;
        }

        public Botao grande() {
            setFont(fonte(Font.BOLD, 15));
            setBorder(new EmptyBorder(12, 20, 12, 20));
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = !isEnabled() ? new Color(0xE3E8E6) : (sobre || getModel().isPressed() ? fundoSobre : fundo);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            if (contorno != null) {
                g2.setColor(contorno);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
            if (isFocusOwner()) {
                g2.setColor(new Color(PRIMARIA.getRed(), PRIMARIA.getGreen(), PRIMARIA.getBlue(), 110));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            }
            g2.dispose();
            setForeground(isEnabled() ? corTexto : new Color(0x98A8A4));
            super.paintComponent(g);
        }
    }

    /** Etiqueta arredondada (ex.: plano de saúde, status). */
    public static class Chip extends JLabel {
        private final Color fundo;

        public Chip(String texto, Color fundo, Color cor) {
            super(texto);
            this.fundo = fundo;
            setForeground(cor);
            setFont(fonte(Font.BOLD, 12));
            setBorder(new EmptyBorder(3, 9, 3, 9));
        }

        public static Chip neutro(String t) { return new Chip(t, SUPERFICIE_2, SUAVE); }
        public static Chip primario(String t) { return new Chip(t, PRIMARIA_CLARA, PRIMARIA_ESCURA); }
        public static Chip ok(String t) { return new Chip(t, OK_CLARO, OK); }
        public static Chip espera(String t) { return new Chip(t, ESPERA_CLARO, ESPERA); }
        public static Chip perigo(String t) { return new Chip(t, PERIGO_CLARO, PERIGO); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Círculo com as iniciais da pessoa. */
    public static Icon avatar(String nome, int tam) {
        Color cor = corPara(nome);
        String ini = iniciais(nome);
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.fillOval(x, y, tam, tam);
                g2.setColor(Color.WHITE);
                g2.setFont(fonte(Font.BOLD, tam * 0.4f));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(ini, x + (tam - fm.stringWidth(ini)) / 2, y + (tam + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
            @Override public int getIconWidth() { return tam; }
            @Override public int getIconHeight() { return tam; }
        };
    }

    /** Estrelas de 0 a 5. Se editável, o clique define a nota. */
    public static class Estrelas extends JComponent {
        private double valor;
        private final int tam;
        private final boolean editavel;
        private int sobre = 0;

        public Estrelas(double valor, int tam, boolean editavel) {
            this.valor = valor;
            this.tam = tam;
            this.editavel = editavel;
            Dimension d = new Dimension(tam * 5 + (tam / 4) * 4, tam);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            if (editavel) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setFocusable(true);
                MouseAdapter m = new MouseAdapter() {
                    @Override public void mouseMoved(MouseEvent e) { sobre = indice(e.getX()); repaint(); }
                    @Override public void mouseExited(MouseEvent e) { sobre = 0; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) {
                        double antigo = Estrelas.this.valor;
                        Estrelas.this.valor = indice(e.getX());
                        firePropertyChange("valor", antigo, Estrelas.this.valor);
                        repaint();
                    }
                };
                addMouseListener(m);
                addMouseMotionListener(m);
            }
        }

        private int indice(int x) {
            int passo = tam + tam / 4;
            return Math.max(1, Math.min(5, x / passo + 1));
        }

        public int getNota() { return (int) Math.round(valor); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double mostrar = sobre > 0 ? sobre : valor;
            int passo = tam + tam / 4;
            for (int i = 0; i < 5; i++) {
                Shape estrela = estrela(i * passo + tam / 2.0, tam / 2.0, tam / 2.0, tam / 4.6);
                g2.setColor(new Color(0xDCE2E0));
                g2.fill(estrela);
                double preenchido = Math.max(0, Math.min(1, mostrar - i));
                if (preenchido > 0) {
                    Shape antigo = g2.getClip();
                    g2.clipRect(i * passo, 0, (int) Math.round(tam * preenchido), tam);
                    g2.setColor(ESTRELA);
                    g2.fill(estrela);
                    g2.setClip(antigo);
                }
            }
            g2.dispose();
        }

        private static Shape estrela(double cx, double cy, double rExt, double rInt) {
            Polygon p = new Polygon();
            for (int i = 0; i < 10; i++) {
                double r = i % 2 == 0 ? rExt : rInt;
                double a = -Math.PI / 2 + i * Math.PI / 5;
                p.addPoint((int) Math.round(cx + Math.cos(a) * r), (int) Math.round(cy + Math.sin(a) * r));
            }
            return p;
        }
    }

    /** Bloco de data (dia grande e mês) usado nas listas de consultas. */
    public static JComponent blocoData(LocalDateTime d, boolean destaque) {
        Cartao c = new Cartao(new GridBagLayout());
        c.cores(destaque ? PRIMARIA_CLARA : SUPERFICIE_2, null);
        c.setBorder(new EmptyBorder(6, 10, 6, 10));
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        JLabel sem = rotulo(diaSemanaCurto(d).toUpperCase(), Font.BOLD, 10, destaque ? PRIMARIA_ESCURA : SUAVE);
        JLabel dia = rotulo(d.format(DateTimeFormatter.ofPattern("dd")), Font.BOLD, 22, destaque ? PRIMARIA_ESCURA : TEXTO);
        JLabel mes = rotulo(mesCurto(d), Font.BOLD, 10, destaque ? PRIMARIA_ESCURA : SUAVE);
        for (JLabel l : new JLabel[]{sem, dia, mes}) {
            l.setAlignmentX(0.5f);
            col.add(l);
        }
        c.add(col);
        c.setPreferredSize(new Dimension(64, 70));
        return c;
    }

    /** Coluna vertical que ocupa toda a largura (para listas dentro de JScrollPane). */
    public static class Coluna extends JPanel implements Scrollable {
        public Coluna() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        public void adicionar(JComponent c) {
            c.setAlignmentX(0);
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
            add(c);
        }

        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 18; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return r.height - 40; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }

    /** Mensagem para quando uma lista está vazia. */
    public static JComponent vazio(String titulo, String texto, JButton acao) {
        Cartao c = new Cartao(new GridBagLayout());
        c.cores(SUPERFICIE_2, BORDA);
        c.setBorder(new EmptyBorder(26, 20, 26, 20));
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        JLabel t = rotulo(titulo, Font.BOLD, 15, TEXTO);
        JLabel s = rotulo(texto, Font.PLAIN, 13, SUAVE);
        t.setAlignmentX(0.5f);
        s.setAlignmentX(0.5f);
        col.add(t);
        col.add(Box.createVerticalStrut(4));
        col.add(s);
        if (acao != null) {
            col.add(Box.createVerticalStrut(12));
            acao.setAlignmentX(0.5f);
            col.add(acao);
        }
        c.add(col);
        return c;
    }
}
