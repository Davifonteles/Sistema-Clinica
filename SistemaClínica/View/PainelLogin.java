package SistemaClínica.View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;

/** Tela inicial: entrar como paciente ou médico, ou fazer um cadastro novo. */
public class PainelLogin extends JPanel {
    private static final String OPCAO_SEM_PLANO = "Não tenho (particular)";

    private final SistemaController controller;
    private final JanelaPrincipal janela;

    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudoCartao = new JPanel(cartas);

    private boolean modoMedico = false;
    private final Tema.Coluna listaPessoas = new Tema.Coluna();
    private final List<LinhaPessoa> linhas = new ArrayList<>();
    private LinhaPessoa selecionada;
    private final JLabel mensagem = Tema.rotulo(" ", Font.BOLD, 13, Tema.PERIGO);
    private final JLabel tituloLista = Tema.rotulo("", Font.BOLD, 13, Tema.TEXTO);
    private final Tema.Botao botaoCadastrar = new Tema.Botao("", new Color(0, 0, 0, 0), Tema.PRIMARIA_CLARA,
                                                              Tema.PRIMARIA_ESCURA, null);
    private final Segmento segmento = new Segmento();

    public PainelLogin(SistemaController controller, JanelaPrincipal janela) {
        super(new BorderLayout());
        this.controller = controller;
        this.janela = janela;
        setBackground(Tema.FUNDO);

        add(new Apresentacao(), BorderLayout.WEST);

        conteudoCartao.setOpaque(false);
        conteudoCartao.add(cartaoEntrar(), "entrar");

        Tema.Cartao cartao = new Tema.Cartao(new BorderLayout());
        cartao.setBorder(new EmptyBorder(28, 30, 26, 30));
        cartao.add(conteudoCartao);
        cartao.setPreferredSize(new Dimension(470, 600));

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(24, 24, 24, 24));
        centro.add(cartao);
        add(centro, BorderLayout.CENTER);

        trocarModo(false);
    }

    // =================================================================== entrar

    private JComponent cartaoEntrar() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setOpaque(false);

        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        JPanel t = Tema.titulo("Entrar", "Escolha quem está usando o sistema.");
        t.setAlignmentX(0);
        topo.add(t);
        topo.add(Box.createVerticalStrut(18));
        segmento.setAlignmentX(0);
        topo.add(segmento);
        topo.add(Box.createVerticalStrut(16));
        tituloLista.setAlignmentX(0);
        topo.add(tituloLista);
        p.add(topo, BorderLayout.NORTH);

        JScrollPane rolagem = Tema.rolagem(listaPessoas);
        rolagem.setBorder(BorderFactory.createLineBorder(Tema.BORDA, 1, true));
        rolagem.getViewport().setOpaque(true);
        rolagem.getViewport().setBackground(Tema.SUPERFICIE);
        p.add(rolagem, BorderLayout.CENTER);

        JPanel baixo = new JPanel();
        baixo.setOpaque(false);
        baixo.setLayout(new BoxLayout(baixo, BoxLayout.Y_AXIS));
        mensagem.setAlignmentX(0);
        baixo.add(mensagem);
        baixo.add(Box.createVerticalStrut(8));
        Tema.Botao entrar = Tema.Botao.primario("Entrar").grande();
        entrar.setAlignmentX(0);
        entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        entrar.addActionListener(e -> entrar());
        baixo.add(entrar);
        baixo.add(Box.createVerticalStrut(14));

        JPanel cadastro = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        cadastro.setOpaque(false);
        cadastro.setAlignmentX(0);
        cadastro.add(Tema.rotulo("Não encontrou seu nome?", Font.PLAIN, 13, Tema.SUAVE));
        botaoCadastrar.pequeno();
        botaoCadastrar.addActionListener(e -> abrirCadastro());
        cadastro.add(botaoCadastrar);
        baixo.add(cadastro);
        p.add(baixo, BorderLayout.SOUTH);
        return p;
    }

    private void trocarModo(boolean medico) {
        modoMedico = medico;
        segmento.selecionar(medico);
        tituloLista.setText(medico ? "Médicos cadastrados" : "Pacientes cadastrados");
        botaoCadastrar.setText(medico ? "Cadastrar médico" : "Cadastrar paciente");
        mensagem.setText(" ");
        recarregarLista(null);
    }

    private void recarregarLista(String selecionarNome) {
        listaPessoas.removeAll();
        linhas.clear();
        selecionada = null;
        if (modoMedico) {
            for (Medico m : controller.listarMedicos()) {
                String planos = m.getPlanosAceitos().isEmpty() ? "Só particular" : String.join(", ", m.getPlanosAceitos());
                linhas.add(new LinhaPessoa(m.getNome(), m.getEspecialidade() + " · " + planos));
            }
        } else {
            for (Paciente pa : controller.listarPacientes()) {
                linhas.add(new LinhaPessoa(pa.getNome(), pa.getIdade() + " anos · " + InfoClinica.nomePlano(pa.getPlanoSaude())));
            }
        }
        for (LinhaPessoa l : linhas) listaPessoas.adicionar(l);
        if (linhas.isEmpty()) {
            listaPessoas.adicionar(Tema.vazio("Ninguém cadastrado ainda", "Use o cadastro abaixo para começar.", null));
        }
        LinhaPessoa alvo = linhas.isEmpty() ? null : linhas.get(0);
        for (LinhaPessoa l : linhas) if (l.nome.equals(selecionarNome)) alvo = l;
        if (alvo != null) selecionar(alvo);
        listaPessoas.revalidate();
        listaPessoas.repaint();
    }

    private void selecionar(LinhaPessoa l) {
        if (selecionada != null) selecionada.setSelecionada(false);
        selecionada = l;
        l.setSelecionada(true);
        l.scrollRectToVisible(new Rectangle(0, 0, l.getWidth(), l.getHeight()));
    }

    private void entrar() {
        if (selecionada == null) {
            mensagem.setForeground(Tema.PERIGO);
            mensagem.setText("Selecione um nome na lista.");
            return;
        }
        if (modoMedico) {
            if (controller.loginMedico(selecionada.nome)) janela.mostrarAreaMedico();
        } else {
            if (controller.loginPaciente(selecionada.nome)) janela.mostrarAreaPaciente();
        }
    }

    // =================================================================== cadastros

    private void abrirCadastro() {
        String id = modoMedico ? "cadMedico" : "cadPaciente";
        conteudoCartao.add(modoMedico ? cartaoCadastroMedico() : cartaoCadastroPaciente(), id);
        cartas.show(conteudoCartao, id);
    }

    private void voltar(String selecionarNome, String aviso) {
        cartas.show(conteudoCartao, "entrar");
        recarregarLista(selecionarNome);
        if (aviso != null) {
            mensagem.setForeground(Tema.OK);
            mensagem.setText(aviso);
        }
    }

    private Set<String> planosConhecidos() {
        Set<String> planos = new LinkedHashSet<>();
        for (Medico m : controller.listarMedicos()) planos.addAll(m.getPlanosAceitos());
        for (Paciente p : controller.listarPacientes()) {
            if (!InfoClinica.semPlano(p)) planos.add(p.getPlanoSaude());
        }
        return planos;
    }

    private JComponent formulario(String titulo, String subtitulo, JComponent[] campos, Runnable salvar, JLabel erro) {
        JPanel p = new JPanel(new BorderLayout(0, 18));
        p.setOpaque(false);
        p.add(Tema.titulo(titulo, subtitulo), BorderLayout.NORTH);

        Tema.Coluna col = new Tema.Coluna();
        for (JComponent c : campos) {
            col.adicionar(c);
            col.add(Box.createVerticalStrut(14));
        }
        p.add(Tema.rolagem(col), BorderLayout.CENTER);

        JPanel baixo = new JPanel(new BorderLayout(0, 10));
        baixo.setOpaque(false);
        baixo.add(erro, BorderLayout.NORTH);
        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));
        botoes.setOpaque(false);
        Tema.Botao voltar = Tema.Botao.secundario("Voltar").grande();
        voltar.addActionListener(e -> voltar(null, null));
        Tema.Botao ok = Tema.Botao.primario("Cadastrar").grande();
        ok.addActionListener(e -> salvar.run());
        botoes.add(voltar);
        botoes.add(ok);
        baixo.add(botoes, BorderLayout.SOUTH);
        p.add(baixo, BorderLayout.SOUTH);
        return p;
    }

    private JComponent cartaoCadastroPaciente() {
        JTextField nome = Tema.campo();
        JSpinner idade = new JSpinner(new SpinnerNumberModel(30, 0, 120, 1));
        Tema.estilizar(idade);

        List<String> opcoes = new ArrayList<>();
        opcoes.add(OPCAO_SEM_PLANO);
        opcoes.addAll(planosConhecidos());
        JComboBox<String> plano = new JComboBox<>(opcoes.toArray(new String[0]));
        plano.setEditable(true);
        Tema.estilizar(plano);

        JLabel dica = Tema.rotulo("Escolha da lista ou digite o nome do seu plano.", Font.PLAIN, 12, Tema.SUAVE);
        JPanel campoPlano = new JPanel(new BorderLayout(0, 6));
        campoPlano.setOpaque(false);
        campoPlano.add(Tema.campoComRotulo("Plano de saúde", plano), BorderLayout.CENTER);
        campoPlano.add(dica, BorderLayout.SOUTH);

        JLabel erro = Tema.rotulo(" ", Font.BOLD, 13, Tema.PERIGO);
        return formulario("Novo paciente", "Os dados ficam disponíveis para agendar consultas.",
            new JComponent[]{Tema.campoComRotulo("Nome", nome), Tema.campoComRotulo("Idade", idade), campoPlano},
            () -> {
                String n = nome.getText().trim();
                String pl = String.valueOf(plano.getEditor().getItem()).trim();
                if (n.isEmpty()) { erro.setText("Digite o nome do paciente."); nome.requestFocus(); return; }
                for (Paciente x : controller.listarPacientes()) {
                    if (x.getNome().equalsIgnoreCase(n)) { erro.setText("Já existe um paciente com esse nome."); return; }
                }
                if (pl.isEmpty()) { erro.setText("Informe o plano de saúde ou escolha \"Não tenho\"."); return; }
                if (pl.equals(OPCAO_SEM_PLANO) || pl.equalsIgnoreCase(InfoClinica.SEM_PLANO)) pl = InfoClinica.SEM_PLANO;
                controller.getSistema().adicionarPaciente(new Paciente(n, (Integer) idade.getValue(), pl));
                voltar(n, "Paciente " + n + " cadastrado. Agora é só entrar.");
            }, erro);
    }

    private JComponent cartaoCadastroMedico() {
        JTextField nome = Tema.campo();
        Set<String> especialidades = new LinkedHashSet<>(Arrays.asList(
            "Clínico Geral", "Cardiologia", "Dermatologia", "Cirurgia", "Pediatria", "Ortopedia"));
        for (Medico m : controller.listarMedicos()) especialidades.add(m.getEspecialidade());
        JComboBox<String> esp = new JComboBox<>(especialidades.toArray(new String[0]));
        esp.setEditable(true);
        Tema.estilizar(esp);

        JPanel caixas = new JPanel(new GridLayout(0, 2, 8, 4));
        caixas.setOpaque(false);
        List<JCheckBox> marcados = new ArrayList<>();
        for (String pl : planosConhecidos()) {
            JCheckBox cb = new JCheckBox(pl);
            cb.setOpaque(false);
            cb.setFont(Tema.fonte(Font.PLAIN, 14));
            marcados.add(cb);
            caixas.add(cb);
        }
        JTextField outros = Tema.campo();
        JPanel planos = new JPanel(new BorderLayout(0, 8));
        planos.setOpaque(false);
        planos.add(Tema.rotulo("Planos aceitos", Font.BOLD, 13, Tema.TEXTO), BorderLayout.NORTH);
        planos.add(caixas, BorderLayout.CENTER);
        JPanel outrosP = new JPanel(new BorderLayout(0, 4));
        outrosP.setOpaque(false);
        outrosP.add(Tema.rotulo("Outros planos (separados por vírgula)", Font.PLAIN, 12, Tema.SUAVE), BorderLayout.NORTH);
        outrosP.add(outros, BorderLayout.CENTER);
        planos.add(outrosP, BorderLayout.SOUTH);

        JLabel erro = Tema.rotulo(" ", Font.BOLD, 13, Tema.PERIGO);
        return formulario("Novo médico", "Pacientes sem plano são atendidos por todos os médicos.",
            new JComponent[]{Tema.campoComRotulo("Nome", nome), Tema.campoComRotulo("Especialidade", esp), planos},
            () -> {
                String n = nome.getText().trim();
                String e = String.valueOf(esp.getEditor().getItem()).trim();
                if (n.isEmpty()) { erro.setText("Digite o nome do médico."); nome.requestFocus(); return; }
                for (Medico x : controller.listarMedicos()) {
                    if (x.getNome().equalsIgnoreCase(n)) { erro.setText("Já existe um médico com esse nome."); return; }
                }
                if (e.isEmpty()) { erro.setText("Informe a especialidade."); return; }
                List<String> lista = new ArrayList<>();
                for (JCheckBox cb : marcados) if (cb.isSelected()) lista.add(cb.getText());
                for (String o : outros.getText().split("\\s*,\\s*")) {
                    if (!o.isBlank() && !lista.contains(o.trim())) lista.add(o.trim());
                }
                controller.getSistema().adicionarMedico(new Medico(n, e, lista));
                voltar(n, "Médico " + n + " cadastrado. Agora é só entrar.");
            }, erro);
    }

    // =================================================================== componentes

    /** Linha selecionável com avatar, nome e detalhe. */
    private class LinhaPessoa extends JPanel {
        final String nome;
        private boolean marcada;

        LinhaPessoa(String nome, String detalhe) {
            super(new BorderLayout(12, 0));
            this.nome = nome;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 12, 10, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(new JLabel(Tema.avatar(nome, 36)), BorderLayout.WEST);
            JPanel textos = new JPanel(new GridLayout(2, 1));
            textos.setOpaque(false);
            textos.add(Tema.rotulo(nome, Font.BOLD, 14, Tema.TEXTO));
            textos.add(Tema.rotulo(detalhe, Font.PLAIN, 12, Tema.SUAVE));
            add(textos, BorderLayout.CENTER);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    selecionar(LinhaPessoa.this);
                    if (e.getClickCount() == 2) entrar();
                }
            });
        }

        void setSelecionada(boolean s) {
            marcada = s;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (marcada) {
                g2.setColor(Tema.PRIMARIA_CLARA);
                g2.fillRoundRect(4, 3, getWidth() - 8, getHeight() - 6, 12, 12);
                g2.setColor(Tema.PRIMARIA);
                g2.drawRoundRect(4, 3, getWidth() - 9, getHeight() - 7, 12, 12);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Escolha entre Paciente e Médico. */
    private class Segmento extends JPanel {
        private final JLabel paciente = opcao("Sou paciente", false);
        private final JLabel medico = opcao("Sou médico", true);
        private boolean medicoSel;

        Segmento() {
            super(new GridLayout(1, 2, 4, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(4, 4, 4, 4));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            add(paciente);
            add(medico);
        }

        private JLabel opcao(String texto, boolean ehMedico) {
            JLabel l = new JLabel(texto, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    if (medicoSel == ehMedico) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Tema.SUPERFICIE);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                        g2.setColor(Tema.BORDA);
                        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                        g2.dispose();
                    }
                    super.paintComponent(g);
                }
            };
            l.setFont(Tema.fonte(Font.BOLD, 14));
            l.setBorder(new EmptyBorder(8, 8, 8, 8));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            l.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { trocarModo(ehMedico); }
            });
            return l;
        }

        void selecionar(boolean m) {
            medicoSel = m;
            paciente.setForeground(m ? Tema.SUAVE : Tema.TEXTO);
            medico.setForeground(m ? Tema.TEXTO : Tema.SUAVE);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xE9EFED));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Painel colorido à esquerda com o nome do sistema. */
    private static class Apresentacao extends JPanel {
        Apresentacao() {
            super(new GridBagLayout());
            setPreferredSize(new Dimension(440, 10));
            setBorder(new EmptyBorder(0, 56, 0, 40));
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            JLabel logo = new JLabel(JanelaPrincipal.logo(56));
            logo.setAlignmentX(0);
            col.add(logo);
            col.add(Box.createVerticalStrut(22));
            JLabel t = Tema.rotulo("Sistema Clínica", Font.BOLD, 34, Color.WHITE);
            t.setAlignmentX(0);
            col.add(t);
            col.add(Box.createVerticalStrut(8));
            JLabel s = Tema.rotulo("<html><div style='width:290px'>Consultas, atendimentos e avaliações num só lugar.</div></html>",
                Font.PLAIN, 16, new Color(0xD6EEEA));
            s.setAlignmentX(0);
            col.add(s);
            col.add(Box.createVerticalStrut(30));
            for (String item : new String[]{
                "Agende com médicos que aceitam seu plano",
                "Lista de espera automática quando o dia lota",
                "Avalie o atendimento depois da consulta"}) {
                JLabel l = Tema.rotulo(item, Font.PLAIN, 14, Color.WHITE);
                l.setIcon(marca());
                l.setIconTextGap(10);
                l.setAlignmentX(0);
                col.add(l);
                col.add(Box.createVerticalStrut(12));
            }
            GridBagConstraints g = new GridBagConstraints();
            g.anchor = GridBagConstraints.WEST;
            g.weightx = 1;
            add(col, g);
        }

        private static Icon marca() {
            return new Icon() {
                @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillOval(x, y, 20, 20);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawPolyline(new int[]{x + 6, x + 9, x + 14}, new int[]{y + 10, y + 13, y + 7}, 3);
                    g2.dispose();
                }
                @Override public int getIconWidth() { return 20; }
                @Override public int getIconHeight() { return 20; }
            };
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, new Color(0x0F7F76), getWidth(), getHeight(), new Color(0x0A4F4A)));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // círculos suaves de fundo
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillOval(getWidth() - 220, -120, 380, 380);
            g2.fillOval(-160, getHeight() - 220, 360, 360);
            g2.dispose();
        }
    }
}
