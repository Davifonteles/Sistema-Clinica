package SistemaClínica.View;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;

/** Área do médico: agenda, atendimentos realizados e avaliações recebidas. */
public class AreaMedico extends AreaBase {
    private final Medico medico;

    public AreaMedico(SistemaController controller, JanelaPrincipal janela) {
        super(controller, janela, controller.getMedicoLogado().getNome(),
              controller.getMedicoLogado().getEspecialidade(), "Médico");
        this.medico = controller.getMedicoLogado();
        pagina("agenda", "Agenda", this::paginaAgenda);
        pagina("atendimentos", "Atendimentos", this::paginaAtendimentos);
        pagina("avaliacoes", "Avaliações", this::paginaAvaliacoes);
        abrir("agenda");
    }

    private List<Consulta> realizadasDoMedico() {
        List<Consulta> lista = new ArrayList<>();
        for (Consulta c : InfoClinica.todas(controller)) {
            if (c.getMedico() == medico && c.isRealizada()) lista.add(c);
        }
        return InfoClinica.ordenar(lista, true);
    }

    // =================================================================== agenda

    private JComponent paginaAgenda() {
        List<Consulta> pendentes = InfoClinica.ordenar(controller.getConsultasPendentesDoMedico(), false);
        List<Consulta> avaliadas = controller.getAvaliacoesRecebidas();
        long hoje = pendentes.stream().filter(c -> c.getDataHora().toLocalDate().equals(LocalDate.now())).count();

        Tema.Coluna col = new Tema.Coluna();
        col.adicionar(Tema.titulo("Agenda", "Consultas marcadas com você que ainda não foram realizadas."));
        col.add(Box.createVerticalStrut(18));

        JPanel resumo = new JPanel(new GridLayout(1, 3, 12, 0));
        resumo.setOpaque(false);
        resumo.add(numero("Pendentes", String.valueOf(pendentes.size()), "consultas a realizar"));
        resumo.add(numero("Hoje", String.valueOf(hoje), hoje == 1 ? "consulta hoje" : "consultas hoje"));
        resumo.add(numero("Sua nota", avaliadas.isEmpty() ? "–" : String.format(Tema.PT_BR, "%.1f", InfoClinica.media(avaliadas)),
            avaliadas.isEmpty() ? "sem avaliações ainda" : avaliadas.size() + " avaliação(ões)"));
        col.adicionar(resumo);

        if (pendentes.isEmpty()) {
            col.add(Box.createVerticalStrut(18));
            col.adicionar(Tema.vazio("Agenda livre", "Nenhuma consulta pendente no momento.", null));
        }
        LocalDate diaAtual = null;
        for (Consulta c : pendentes) {
            LocalDate d = c.getDataHora().toLocalDate();
            if (!d.equals(diaAtual)) {
                diaAtual = d;
                col.adicionar(Tema.secao(Tema.diaLongo(c.getDataHora())));
            }
            col.adicionar(linhaAgenda(c));
            col.add(Box.createVerticalStrut(10));
        }
        return Tema.rolagem(col);
    }

    private JComponent numero(String rotulo, String valor, String detalhe) {
        Tema.Cartao c = new Tema.Cartao(new BorderLayout(0, 2));
        c.add(Tema.rotulo(rotulo.toUpperCase(), Font.BOLD, 11, Tema.SUAVE), BorderLayout.NORTH);
        c.add(Tema.rotulo(valor, Font.BOLD, 28, Tema.TEXTO), BorderLayout.CENTER);
        c.add(Tema.rotulo(detalhe, Font.PLAIN, 12, Tema.SUAVE), BorderLayout.SOUTH);
        return c;
    }

    private JComponent linhaAgenda(Consulta c) {
        Paciente p = c.getPaciente();
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(12, 16, 12, 18));

        Tema.Cartao hora = new Tema.Cartao(new GridBagLayout());
        hora.cores(Tema.PRIMARIA_CLARA, null);
        hora.setBorder(new EmptyBorder(10, 12, 10, 12));
        hora.add(Tema.rotulo(c.getDataHora().format(Tema.HORA), Font.BOLD, 18, Tema.PRIMARIA_ESCURA));
        card.add(Tema.linha(14, hora, new JLabel(Tema.avatar(p.getNome(), 40))), BorderLayout.WEST);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 4));
        textos.setOpaque(false);
        textos.add(Tema.rotulo(p.getNome(), Font.BOLD, 16, Tema.TEXTO));
        JPanel det = Tema.linha(8, Tema.rotulo(p.getIdade() + " anos", Font.PLAIN, 13, Tema.SUAVE),
            InfoClinica.semPlano(p)
                ? Tema.Chip.neutro("Particular · " + Tema.dinheiro(InfoClinica.valorParticular(medico)))
                : Tema.Chip.ok("Plano " + p.getPlanoSaude()));
        textos.add(det);
        card.add(textos, BorderLayout.CENTER);

        Tema.Botao realizar = Tema.Botao.primario("Realizar consulta").pequeno();
        realizar.addActionListener(e -> realizar(c));
        JPanel d = new JPanel(new GridBagLayout());
        d.setOpaque(false);
        d.add(realizar);
        card.add(d, BorderLayout.EAST);
        return card;
    }

    private void realizar(Consulta c) {
        Paciente p = c.getPaciente();
        JTextArea descricao = new JTextArea(5, 34);
        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setFont(Tema.fonte(Font.PLAIN, 14));
        descricao.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane sc = new JScrollPane(descricao);
        sc.setBorder(BorderFactory.createLineBorder(Tema.BORDA, 1, true));

        double valor = InfoClinica.semPlano(p) ? InfoClinica.valorParticular(medico) : 0;
        JPanel corpo = new JPanel(new BorderLayout(0, 12));
        corpo.setOpaque(false);
        corpo.add(Tema.campoComRotulo("Descrição do atendimento", sc), BorderLayout.CENTER);
        Tema.Cartao cobranca = new Tema.Cartao(new BorderLayout());
        cobranca.setBorder(new EmptyBorder(10, 14, 10, 14));
        if (valor > 0) {
            cobranca.cores(Tema.ESPERA_CLARO, null);
            cobranca.add(Tema.rotulo("Paciente particular: cobrar " + Tema.dinheiro(valor), Font.BOLD, 14, Tema.ESPERA));
        } else {
            cobranca.cores(Tema.OK_CLARO, null);
            cobranca.add(Tema.rotulo("Coberto pelo plano " + p.getPlanoSaude(), Font.BOLD, 14, Tema.OK));
        }
        corpo.add(cobranca, BorderLayout.SOUTH);

        Dialogo d = new Dialogo(this, "Realizar consulta",
            p.getNome() + " · " + c.getDataHora().format(Tema.DATA_HORA), corpo, "Concluir atendimento", false,
            () -> descricao.getText().isBlank() ? "Escreva uma descrição do atendimento." : null);
        if (!d.perguntar()) return;

        if (controller.realizarConsulta(c.getDataHora(), descricao.getText().trim())) {
            avisar(valor > 0
                ? "Consulta de " + p.getNome() + " concluída. Valor a cobrar: " + Tema.dinheiro(valor) + "."
                : "Consulta de " + p.getNome() + " concluída. Coberta pelo plano " + p.getPlanoSaude() + ".", TipoAviso.OK);
        } else {
            avisar("Não foi possível concluir a consulta.", TipoAviso.ERRO);
        }
        atualizar();
    }

    // =================================================================== atendimentos

    private JComponent paginaAtendimentos() {
        List<Consulta> feitas = realizadasDoMedico();
        double total = feitas.stream().mapToDouble(c -> c.getValorCobrado() == null ? 0 : c.getValorCobrado()).sum();
        long porPlano = feitas.stream().filter(c -> c.getValorCobrado() == null || c.getValorCobrado() == 0).count();

        Tema.Coluna col = new Tema.Coluna();
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Tema.titulo("Atendimentos", "Consultas que você já realizou."), BorderLayout.CENTER);
        Tema.Botao exportar = Tema.Botao.secundario("Exportar CSV");
        exportar.setToolTipText("Salva todas as consultas realizadas da clínica num arquivo .csv");
        exportar.addActionListener(e -> exportar());
        JPanel b = new JPanel(new GridBagLayout());
        b.setOpaque(false);
        b.add(exportar);
        topo.add(b, BorderLayout.EAST);
        col.adicionar(topo);
        col.add(Box.createVerticalStrut(18));

        JPanel resumo = new JPanel(new GridLayout(1, 3, 12, 0));
        resumo.setOpaque(false);
        resumo.add(numero("Realizadas", String.valueOf(feitas.size()), "atendimentos no total"));
        resumo.add(numero("Particulares", Tema.dinheiro(total), "valor cobrado de quem não tem plano"));
        resumo.add(numero("Pelo plano", String.valueOf(porPlano), "atendimentos cobertos"));
        col.adicionar(resumo);
        col.add(Box.createVerticalStrut(18));

        if (feitas.isEmpty()) {
            col.adicionar(Tema.vazio("Nenhum atendimento ainda", "As consultas realizadas aparecem aqui.", null));
            return Tema.rolagem(col);
        }

        // tabela simples
        Tema.Cartao tabela = new Tema.Cartao(new BorderLayout());
        tabela.setBorder(new EmptyBorder(6, 0, 6, 0));
        Tema.Coluna linhas = new Tema.Coluna();
        linhas.adicionar(linhaTabela(true, "Data", "Paciente", "Descrição", "Valor", null));
        for (Consulta c : feitas) {
            double v = c.getValorCobrado() == null ? 0 : c.getValorCobrado();
            linhas.adicionar(linhaTabela(false, c.getDataHora().format(Tema.DATA_HORA), c.getPaciente().getNome(),
                c.getDescricao() == null ? "" : c.getDescricao(), v > 0 ? Tema.dinheiro(v) : "Plano", c.getAvaliacao()));
        }
        tabela.add(linhas);
        col.adicionar(tabela);
        return Tema.rolagem(col);
    }

    private JComponent linhaTabela(boolean cabecalho, String data, String paciente, String desc, String valor, Integer nota) {
        JPanel l = new JPanel(new GridBagLayout());
        l.setOpaque(false);
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, cabecalho ? Tema.BORDA : new Color(0xEEF2F1)),
            new EmptyBorder(10, 18, 10, 18)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        int estilo = cabecalho ? Font.BOLD : Font.PLAIN;
        Color cor = cabecalho ? Tema.SUAVE : Tema.TEXTO;
        float tam = cabecalho ? 12 : 14;

        Object[][] cols = {
            {cabecalho ? data.toUpperCase() : data, 0.0, 140},
            {cabecalho ? paciente.toUpperCase() : paciente, 0.0, 150},
            {cabecalho ? desc.toUpperCase() : desc, 1.0, 120},
            {cabecalho ? valor.toUpperCase() : valor, 0.0, 110},
        };
        for (Object[] c : cols) {
            JLabel lab = Tema.rotulo((String) c[0], estilo, tam, cor);
            lab.setPreferredSize(new Dimension((Integer) c[2], lab.getPreferredSize().height));
            lab.setToolTipText(cabecalho ? null : (String) c[0]);
            g.weightx = (Double) c[1];
            g.insets = new Insets(0, 0, 0, 12);
            l.add(lab, g);
        }
        g.weightx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        JComponent ultima;
        if (cabecalho) ultima = Tema.rotulo("NOTA", Font.BOLD, 12, Tema.SUAVE);
        else if (nota != null) ultima = new Tema.Estrelas(nota, 13, false);
        else ultima = Tema.rotulo("sem nota", Font.PLAIN, 12, Tema.SUAVE);
        JPanel caixa = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        caixa.setOpaque(false);
        caixa.setPreferredSize(new Dimension(84, ultima.getPreferredSize().height));
        caixa.add(ultima);
        l.add(caixa, g);
        return l;
    }

    private void exportar() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Salvar consultas realizadas");
        fc.setSelectedFile(new File("consultas_realizadas.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) f = new File(f.getParentFile(), f.getName() + ".csv");
        controller.exportarConsultas(f.getAbsolutePath());
        if (f.exists()) avisar("Arquivo salvo em " + f.getAbsolutePath(), TipoAviso.OK);
        else avisar("Não foi possível salvar o arquivo.", TipoAviso.ERRO);
    }

    // =================================================================== avaliações

    private JComponent paginaAvaliacoes() {
        List<Consulta> avaliadas = InfoClinica.ordenar(controller.getAvaliacoesRecebidas(), true);

        Tema.Coluna col = new Tema.Coluna();
        col.adicionar(Tema.titulo("Avaliações", "O que os pacientes acharam dos seus atendimentos."));
        col.add(Box.createVerticalStrut(18));

        if (avaliadas.isEmpty()) {
            col.adicionar(Tema.vazio("Nenhuma avaliação recebida",
                "Os pacientes podem avaliar depois que a consulta é realizada.", null));
            return Tema.rolagem(col);
        }

        // resumo: média + distribuição das notas
        Tema.Cartao resumo = new Tema.Cartao(new BorderLayout(30, 0));
        resumo.setBorder(new EmptyBorder(20, 24, 20, 24));
        JPanel media = new JPanel();
        media.setOpaque(false);
        media.setLayout(new BoxLayout(media, BoxLayout.Y_AXIS));
        double m = InfoClinica.media(avaliadas);
        JLabel num = Tema.rotulo(String.format(Tema.PT_BR, "%.1f", m), Font.BOLD, 44, Tema.TEXTO);
        Tema.Estrelas est = new Tema.Estrelas(m, 20, false);
        JLabel qt = Tema.rotulo(avaliadas.size() + (avaliadas.size() == 1 ? " avaliação" : " avaliações"), Font.PLAIN, 13, Tema.SUAVE);
        for (JComponent x : new JComponent[]{num, est, qt}) {
            x.setAlignmentX(0);
            media.add(x);
            media.add(Box.createVerticalStrut(4));
        }
        resumo.add(media, BorderLayout.WEST);

        JPanel barras = new JPanel(new GridLayout(5, 1, 0, 6));
        barras.setOpaque(false);
        for (int nota = 5; nota >= 1; nota--) {
            final int n = nota;
            long qtd = avaliadas.stream().filter(c -> c.getAvaliacao() == n).count();
            barras.add(barra(nota, qtd, avaliadas.size()));
        }
        resumo.add(barras, BorderLayout.CENTER);
        col.adicionar(resumo);

        col.adicionar(Tema.secao("Comentários"));
        for (Consulta c : avaliadas) {
            Tema.Cartao card = new Tema.Cartao(new BorderLayout(14, 0));
            card.setBorder(new EmptyBorder(14, 16, 14, 18));
            card.add(new JLabel(Tema.avatar(c.getPaciente().getNome(), 40)), BorderLayout.WEST);
            JPanel t = new JPanel();
            t.setOpaque(false);
            t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
            JPanel linha1 = Tema.linha(10, Tema.rotulo(c.getPaciente().getNome(), Font.BOLD, 15, Tema.TEXTO),
                new Tema.Estrelas(c.getAvaliacao(), 14, false),
                Tema.rotulo(c.getDataHora().format(Tema.DATA_HORA), Font.PLAIN, 12, Tema.SUAVE));
            String com = c.getComentario() == null || c.getComentario().isBlank() ? "Sem comentário." : c.getComentario();
            JLabel texto = Tema.rotulo("<html>" + com.replace("<", "&lt;") + "</html>", Font.PLAIN, 14,
                c.getComentario() == null ? Tema.SUAVE : Tema.TEXTO);
            linha1.setAlignmentX(0);
            texto.setAlignmentX(0);
            t.add(linha1);
            t.add(Box.createVerticalStrut(6));
            t.add(texto);
            card.add(t, BorderLayout.CENTER);
            col.adicionar(card);
            col.add(Box.createVerticalStrut(10));
        }
        return Tema.rolagem(col);
    }

    private JComponent barra(int nota, long qtd, int total) {
        JPanel l = new JPanel(new BorderLayout(10, 0));
        l.setOpaque(false);
        JLabel rot = Tema.rotulo(nota + " ★", Font.BOLD, 13, Tema.TEXTO);
        rot.setPreferredSize(new Dimension(34, 16));
        l.add(rot, BorderLayout.WEST);
        JComponent trilho = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int h = 8, y = (getHeight() - h) / 2;
                g2.setColor(new Color(0xE8EEEC));
                g2.fillRoundRect(0, y, getWidth(), h, h, h);
                int w = total == 0 ? 0 : (int) Math.round(getWidth() * (double) qtd / total);
                if (w > 0) {
                    g2.setColor(Tema.ESTRELA);
                    g2.fillRoundRect(0, y, Math.max(h, w), h, h, h);
                }
                g2.dispose();
            }
        };
        trilho.setPreferredSize(new Dimension(200, 16));
        l.add(trilho, BorderLayout.CENTER);
        JLabel q = Tema.rotulo(String.valueOf(qtd), Font.PLAIN, 13, Tema.SUAVE);
        q.setPreferredSize(new Dimension(28, 16));
        q.setHorizontalAlignment(SwingConstants.RIGHT);
        l.add(q, BorderLayout.EAST);
        return l;
    }
}
