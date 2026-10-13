package SistemaClínica.View;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import SistemaClínica.Controller.SistemaController;
import SistemaClínica.Model.Consulta;
import SistemaClínica.Model.Medico;
import SistemaClínica.Model.Paciente;
import SistemaClínica.Model.ResultadoAgendamento;

/** Área do paciente: minhas consultas e agendamento. */
public class AreaPaciente extends AreaBase {
    private final Paciente paciente;

    // estado da página de agendamento
    private Medico medicoEscolhido;
    private LocalDateTime horarioEscolhido;

    public AreaPaciente(SistemaController controller, JanelaPrincipal janela) {
        super(controller, janela, controller.getPacienteLogado().getNome(),
              InfoClinica.nomePlano(controller.getPacienteLogado().getPlanoSaude()), "Paciente");
        this.paciente = controller.getPacienteLogado();
        pagina("consultas", "Minhas consultas", this::paginaConsultas);
        pagina("agendar", "Agendar consulta", this::paginaAgendar);
        abrir("consultas");
    }

    // =================================================================== minhas consultas

    private JComponent paginaConsultas() {
        List<Consulta> futuras = InfoClinica.ordenar(controller.getSistema().getConsultasFuturasDoPaciente(), false);
        List<Consulta> realizadas = InfoClinica.ordenar(controller.getSistema().getConsultasRealizadasDoPaciente(), true);
        long semNota = realizadas.stream().filter(c -> c.getAvaliacao() == null).count();

        Tema.Coluna col = new Tema.Coluna();

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        String primeiroNome = paciente.getNome().split("\\s+")[0];
        topo.add(Tema.titulo("Olá, " + primeiroNome, "Acompanhe suas consultas e avalie os atendimentos."), BorderLayout.CENTER);
        Tema.Botao agendar = Tema.Botao.primario("+ Agendar consulta");
        agendar.addActionListener(e -> abrir("agendar"));
        JPanel b = new JPanel(new GridBagLayout());
        b.setOpaque(false);
        b.add(agendar);
        topo.add(b, BorderLayout.EAST);
        col.adicionar(topo);
        col.add(Box.createVerticalStrut(18));

        JPanel resumo = new JPanel(new GridLayout(1, 3, 12, 0));
        resumo.setOpaque(false);
        resumo.add(numero("Próximas", futuras.size(), "consultas agendadas"));
        resumo.add(numero("Realizadas", realizadas.size(), "atendimentos concluídos"));
        resumo.add(numero("Para avaliar", (int) semNota, semNota == 1 ? "consulta sem nota" : "consultas sem nota"));
        col.adicionar(resumo);

        col.adicionar(Tema.secao("Próximas consultas"));
        if (futuras.isEmpty()) {
            Tema.Botao a = Tema.Botao.leve("Agendar agora");
            a.addActionListener(e -> abrir("agendar"));
            col.adicionar(Tema.vazio("Nenhuma consulta agendada", "Escolha um médico e um horário para marcar.", a));
        }
        for (Consulta c : futuras) {
            col.adicionar(linhaFutura(c));
            col.add(Box.createVerticalStrut(10));
        }

        List<Consulta> espera = InfoClinica.ordenar(controller.getListaEsperaDoPaciente(), false);
        if (!espera.isEmpty()) {
            col.adicionar(Tema.secao("Na lista de espera"));
            for (Consulta c : espera) {
                col.adicionar(linhaEspera(c));
                col.add(Box.createVerticalStrut(10));
            }
        }

        col.adicionar(Tema.secao("Consultas realizadas"));
        if (realizadas.isEmpty()) {
            col.adicionar(Tema.vazio("Nenhuma consulta realizada ainda",
                "Depois do atendimento, ela aparece aqui para você avaliar.", null));
        }
        for (Consulta c : realizadas) {
            col.adicionar(linhaRealizada(c));
            col.add(Box.createVerticalStrut(10));
        }
        return Tema.rolagem(col);
    }

    private JComponent numero(String rotulo, int valor, String detalhe) {
        Tema.Cartao c = new Tema.Cartao(new BorderLayout(0, 2));
        c.add(Tema.rotulo(rotulo.toUpperCase(), Font.BOLD, 11, Tema.SUAVE), BorderLayout.NORTH);
        c.add(Tema.rotulo(String.valueOf(valor), Font.BOLD, 28, Tema.TEXTO), BorderLayout.CENTER);
        c.add(Tema.rotulo(detalhe, Font.PLAIN, 12, Tema.SUAVE), BorderLayout.SOUTH);
        return c;
    }

    private JComponent linhaFutura(Consulta c) {
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(12, 12, 12, 18));
        card.add(Tema.blocoData(c.getDataHora(), true), BorderLayout.WEST);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        Medico m = c.getMedico();
        textos.add(Tema.rotulo(c.getDataHora().format(Tema.HORA) + "  ·  " + m.getNome(), Font.BOLD, 16, Tema.TEXTO));
        textos.add(Tema.rotulo(m.getEspecialidade() + "  ·  " + Tema.diaLongo(c.getDataHora()), Font.PLAIN, 13, Tema.SUAVE));
        card.add(textos, BorderLayout.CENTER);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(InfoClinica.semPlano(paciente)
            ? Tema.Chip.neutro("Particular · " + Tema.dinheiro(InfoClinica.valorParticular(m)))
            : Tema.Chip.ok("Pelo plano " + paciente.getPlanoSaude()));
        Tema.Botao cancelar = Tema.Botao.perigo("Cancelar").pequeno();
        cancelar.addActionListener(e -> cancelar(c));
        direita.add(cancelar);
        card.add(centralizar(direita), BorderLayout.EAST);
        return card;
    }

    private JComponent linhaEspera(Consulta c) {
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(16, 0));
        card.cores(Tema.ESPERA_CLARO, new Color(0xEBCB93));
        card.setBorder(new EmptyBorder(12, 12, 12, 18));
        card.add(Tema.blocoData(c.getDataHora(), false), BorderLayout.WEST);
        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(Tema.rotulo(c.getMedico().getNome() + "  ·  " + c.getMedico().getEspecialidade(), Font.BOLD, 15, Tema.TEXTO));
        textos.add(Tema.rotulo("Pediu " + c.getDataHora().format(Tema.HORA) + ". Se alguém cancelar nesse dia, a vaga é sua.",
            Font.PLAIN, 13, Tema.ESPERA));
        card.add(textos, BorderLayout.CENTER);
        card.add(centralizar(Tema.Chip.espera("Aguardando vaga")), BorderLayout.EAST);
        return card;
    }

    private JComponent linhaRealizada(Consulta c) {
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(12, 12, 12, 18));
        card.add(Tema.blocoData(c.getDataHora(), false), BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel t = Tema.rotulo(c.getMedico().getNome() + "  ·  " + c.getMedico().getEspecialidade(), Font.BOLD, 15, Tema.TEXTO);
        String desc = c.getDescricao() == null || c.getDescricao().isBlank() ? "Sem descrição" : c.getDescricao();
        JLabel d = Tema.rotulo("<html>" + escapar(desc) + "</html>", Font.PLAIN, 13, Tema.SUAVE);
        double valor = c.getValorCobrado() == null ? 0 : c.getValorCobrado();
        JPanel chips = Tema.linha(6, valor > 0 ? Tema.Chip.neutro("Pago " + Tema.dinheiro(valor)) : Tema.Chip.ok("Coberto pelo plano"));
        for (JComponent x : new JComponent[]{t, d, chips}) {
            x.setAlignmentX(0);
            textos.add(x);
            textos.add(Box.createVerticalStrut(4));
        }
        card.add(textos, BorderLayout.CENTER);

        JPanel direita = new JPanel();
        direita.setOpaque(false);
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));
        if (c.getAvaliacao() != null) {
            Tema.Estrelas e = new Tema.Estrelas(c.getAvaliacao(), 18, false);
            e.setAlignmentX(1f);
            direita.add(e);
            if (c.getComentario() != null && !c.getComentario().isBlank()) {
                direita.add(Box.createVerticalStrut(6));
                JLabel com = Tema.rotulo("“" + resumir(c.getComentario(), 34) + "”", Font.ITALIC, 12, Tema.SUAVE);
                com.setToolTipText(c.getComentario());
                com.setAlignmentX(1f);
                direita.add(com);
            }
        } else {
            Tema.Botao avaliar = Tema.Botao.leve("Avaliar atendimento").pequeno();
            avaliar.setAlignmentX(1f);
            avaliar.addActionListener(e -> avaliar(c));
            direita.add(avaliar);
        }
        card.add(centralizar(direita), BorderLayout.EAST);
        return card;
    }

    private void cancelar(Consulta c) {
        String quando = Tema.diaLongo(c.getDataHora()) + " às " + c.getDataHora().format(Tema.HORA);
        boolean ok = Dialogo.confirmar(this, "Cancelar consulta?",
            c.getMedico().getNome() + " · " + quando, "Cancelar consulta", true);
        if (!ok) return;
        if (controller.cancelarConsulta(c.getDataHora())) {
            avisar("Consulta de " + quando + " cancelada.", TipoAviso.OK);
        } else {
            avisar("Não foi possível cancelar essa consulta.", TipoAviso.ERRO);
        }
        atualizar();
    }

    private void avaliar(Consulta c) {
        Tema.Estrelas estrelas = new Tema.Estrelas(0, 34, true);
        JTextArea comentario = new JTextArea(4, 30);
        comentario.setLineWrap(true);
        comentario.setWrapStyleWord(true);
        comentario.setFont(Tema.fonte(Font.PLAIN, 14));
        comentario.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane sc = new JScrollPane(comentario);
        sc.setBorder(BorderFactory.createLineBorder(Tema.BORDA, 1, true));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        JLabel l1 = Tema.rotulo("Sua nota", Font.BOLD, 13, Tema.TEXTO);
        JLabel l2 = Tema.rotulo("Comentário (opcional)", Font.BOLD, 13, Tema.TEXTO);
        for (JComponent x : new JComponent[]{l1, estrelas, l2, sc}) x.setAlignmentX(0);
        corpo.add(l1);
        corpo.add(Box.createVerticalStrut(8));
        corpo.add(estrelas);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(l2);
        corpo.add(Box.createVerticalStrut(6));
        corpo.add(sc);

        Dialogo d = new Dialogo(this, "Avaliar atendimento",
            c.getMedico().getNome() + " · " + c.getDataHora().format(Tema.DATA_HORA),
            corpo, "Enviar avaliação", false,
            () -> estrelas.getNota() < 1 ? "Escolha de 1 a 5 estrelas." : null);
        if (!d.perguntar()) return;

        String texto = comentario.getText().trim();
        if (controller.avaliarConsulta(c.getDataHora(), estrelas.getNota(), texto.isEmpty() ? null : texto)) {
            avisar("Obrigado! Sua avaliação de " + estrelas.getNota() + " estrela(s) foi registrada.", TipoAviso.OK);
        } else {
            avisar("Não foi possível registrar a avaliação.", TipoAviso.ERRO);
        }
        atualizar();
    }

    // =================================================================== agendar

    private JComponent paginaAgendar() {
        JPanel p = new JPanel(new BorderLayout(20, 0));
        p.setOpaque(false);

        // ---- lista de médicos
        JPanel esquerda = new JPanel(new BorderLayout(0, 12));
        esquerda.setOpaque(false);
        esquerda.setPreferredSize(new Dimension(370, 10));
        JPanel topo = new JPanel(new BorderLayout(0, 14));
        topo.setOpaque(false);
        topo.add(Tema.titulo("Agendar consulta", "Escolha o médico e depois o horário."), BorderLayout.NORTH);
        JTextField busca = Tema.campo();
        busca.putClientProperty("JTextField.placeholderText", "Buscar por nome ou especialidade");
        busca.setToolTipText("Buscar por nome ou especialidade");
        topo.add(Tema.campoComRotulo("Buscar por nome ou especialidade", busca), BorderLayout.SOUTH);
        esquerda.add(topo, BorderLayout.NORTH);

        Tema.Coluna lista = new Tema.Coluna();
        esquerda.add(Tema.rolagem(lista), BorderLayout.CENTER);
        p.add(esquerda, BorderLayout.WEST);

        // ---- detalhe + horários
        JPanel direita = new JPanel(new BorderLayout());
        direita.setOpaque(false);
        p.add(direita, BorderLayout.CENTER);

        if (medicoEscolhido == null || !InfoClinica.atende(medicoEscolhido, paciente)) {
            medicoEscolhido = controller.listarMedicos().stream()
                .filter(m -> InfoClinica.atende(m, paciente)).findFirst().orElse(null);
            horarioEscolhido = null;
        }

        Runnable[] desenhar = new Runnable[1];
        desenhar[0] = () -> {
            preencherLista(lista, busca.getText(), desenhar[0]);
            direita.removeAll();
            direita.add(detalheMedico(desenhar[0]), BorderLayout.CENTER);
            direita.revalidate();
            direita.repaint();
        };
        busca.getDocument().addDocumentListener(new DocumentListener() {
            void mudou() { preencherLista(lista, busca.getText(), desenhar[0]); }
            @Override public void insertUpdate(DocumentEvent e) { mudou(); }
            @Override public void removeUpdate(DocumentEvent e) { mudou(); }
            @Override public void changedUpdate(DocumentEvent e) { mudou(); }
        });
        desenhar[0].run();
        return p;
    }

    private void preencherLista(Tema.Coluna lista, String filtro, Runnable redesenhar) {
        lista.removeAll();
        String termo = filtro.trim().toLowerCase();
        List<Medico> compativeis = new ArrayList<>();
        List<Medico> outros = new ArrayList<>();
        for (Medico m : controller.listarMedicos()) {
            boolean combina = termo.isEmpty() || m.getNome().toLowerCase().contains(termo)
                || m.getEspecialidade().toLowerCase().contains(termo);
            if (!combina) continue;
            (InfoClinica.atende(m, paciente) ? compativeis : outros).add(m);
        }
        for (Medico m : compativeis) {
            lista.adicionar(cartaoMedico(m, true, redesenhar));
            lista.add(Box.createVerticalStrut(8));
        }
        if (!outros.isEmpty()) {
            lista.adicionar(Tema.secao("Não aceitam " + paciente.getPlanoSaude()));
            for (Medico m : outros) {
                lista.adicionar(cartaoMedico(m, false, redesenhar));
                lista.add(Box.createVerticalStrut(8));
            }
        }
        if (compativeis.isEmpty() && outros.isEmpty()) {
            lista.adicionar(Tema.vazio("Nenhum médico encontrado", "Tente outro nome ou especialidade.", null));
        }
        lista.revalidate();
        lista.repaint();
    }

    private JComponent cartaoMedico(Medico m, boolean disponivel, Runnable redesenhar) {
        boolean escolhido = m == medicoEscolhido;
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(12, 0));
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        if (escolhido) card.cores(Tema.PRIMARIA_CLARA, Tema.PRIMARIA);
        else if (!disponivel) card.cores(Tema.SUPERFICIE_2, Tema.BORDA);

        card.add(new JLabel(Tema.avatar(m.getNome(), 42)), BorderLayout.WEST);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel nome = Tema.rotulo(m.getNome(), Font.BOLD, 15, disponivel ? Tema.TEXTO : Tema.SUAVE);
        JLabel esp = Tema.rotulo(m.getEspecialidade(), Font.PLAIN, 13, Tema.SUAVE);

        List<Consulta> avaliadas = InfoClinica.avaliacoesDe(controller, m);
        JPanel notas = avaliadas.isEmpty()
            ? Tema.linha(6, Tema.rotulo("Sem avaliações", Font.PLAIN, 12, Tema.SUAVE))
            : Tema.linha(6, new Tema.Estrelas(InfoClinica.media(avaliadas), 13, false),
                Tema.rotulo(String.format(Tema.PT_BR, "%.1f (%d)", InfoClinica.media(avaliadas), avaliadas.size()),
                    Font.BOLD, 12, Tema.TEXTO));
        JComponent chip = !disponivel ? Tema.Chip.perigo("Não aceita " + paciente.getPlanoSaude())
            : InfoClinica.semPlano(paciente) ? Tema.Chip.neutro("Particular · " + Tema.dinheiro(InfoClinica.valorParticular(m)))
            : Tema.Chip.ok("Aceita " + paciente.getPlanoSaude());
        JPanel chipP = Tema.linha(0, chip);
        for (JComponent x : new JComponent[]{nome, esp, notas, chipP}) {
            x.setAlignmentX(0);
            textos.add(x);
            textos.add(Box.createVerticalStrut(4));
        }
        card.add(textos, BorderLayout.CENTER);

        if (disponivel) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (medicoEscolhido != m) {
                        medicoEscolhido = m;
                        horarioEscolhido = null;
                        redesenhar.run();
                    }
                }
            });
        } else {
            card.setToolTipText("Este médico não atende o seu plano de saúde.");
        }
        return card;
    }

    private JComponent detalheMedico(Runnable redesenhar) {
        Medico m = medicoEscolhido;
        if (m == null) {
            return Tema.vazio("Nenhum médico disponível",
                "Não há médicos cadastrados que aceitem o seu plano.", null);
        }
        Tema.Coluna col = new Tema.Coluna();
        Tema.Cartao card = new Tema.Cartao(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        // cabeçalho do médico
        JPanel cab = new JPanel(new BorderLayout(14, 0));
        cab.setOpaque(false);
        cab.add(new JLabel(Tema.avatar(m.getNome(), 56)), BorderLayout.WEST);
        JPanel t = new JPanel(new GridLayout(3, 1, 0, 2));
        t.setOpaque(false);
        t.add(Tema.rotulo(m.getNome(), Font.BOLD, 20, Tema.TEXTO));
        t.add(Tema.rotulo(m.getEspecialidade(), Font.PLAIN, 14, Tema.SUAVE));
        List<JComponent> chipsPlanos = new ArrayList<>();
        if (m.getPlanosAceitos().isEmpty()) chipsPlanos.add(Tema.Chip.neutro("Só particular"));
        for (String pl : m.getPlanosAceitos()) chipsPlanos.add(Tema.Chip.primario(pl));
        JPanel planos = Tema.linha(6, chipsPlanos.toArray(new JComponent[0]));
        t.add(planos);
        cab.add(t, BorderLayout.CENTER);

        List<Consulta> avaliadas = InfoClinica.ordenar(InfoClinica.avaliacoesDe(controller, m), true);
        JPanel nota = new JPanel();
        nota.setOpaque(false);
        nota.setLayout(new BoxLayout(nota, BoxLayout.Y_AXIS));
        if (!avaliadas.isEmpty()) {
            JLabel media = Tema.rotulo(String.format(Tema.PT_BR, "%.1f", InfoClinica.media(avaliadas)), Font.BOLD, 26, Tema.TEXTO);
            media.setAlignmentX(1f);
            Tema.Estrelas e = new Tema.Estrelas(InfoClinica.media(avaliadas), 14, false);
            e.setAlignmentX(1f);
            JLabel qt = Tema.rotulo(avaliadas.size() + (avaliadas.size() == 1 ? " avaliação" : " avaliações"), Font.PLAIN, 12, Tema.SUAVE);
            qt.setAlignmentX(1f);
            nota.add(media);
            nota.add(e);
            nota.add(Box.createVerticalStrut(4));
            nota.add(qt);
        }
        cab.add(nota, BorderLayout.EAST);
        card.add(cab, BorderLayout.NORTH);

        // últimas avaliações (como em listarMedicosPorFiltro: as 3 mais recentes)
        JPanel meio = new JPanel();
        meio.setOpaque(false);
        meio.setLayout(new BoxLayout(meio, BoxLayout.Y_AXIS));
        if (!avaliadas.isEmpty()) {
            JLabel tit = Tema.rotulo("ÚLTIMAS AVALIAÇÕES", Font.BOLD, 11, Tema.SUAVE);
            tit.setAlignmentX(0);
            meio.add(tit);
            meio.add(Box.createVerticalStrut(6));
            for (Consulta a : avaliadas.subList(0, Math.min(3, avaliadas.size()))) {
                String com = a.getComentario() == null || a.getComentario().isBlank() ? "(sem comentário)" : "“" + resumir(a.getComentario(), 60) + "”";
                JPanel linha = Tema.linha(8, new Tema.Estrelas(a.getAvaliacao(), 12, false),
                    Tema.rotulo(com, Font.PLAIN, 13, Tema.TEXTO));
                linha.setAlignmentX(0);
                linha.setBorder(new EmptyBorder(2, 0, 2, 0));
                meio.add(linha);
            }
            meio.add(Box.createVerticalStrut(14));
        }
        JLabel tituloHor = Tema.rotulo("Escolha um horário", Font.BOLD, 16, Tema.TEXTO);
        tituloHor.setAlignmentX(0);
        meio.add(tituloHor);
        meio.add(Box.createVerticalStrut(10));
        JComponent grade = gradeHorarios(m, redesenhar);
        grade.setAlignmentX(0);
        meio.add(grade);
        meio.add(Box.createVerticalStrut(10));
        JPanel legenda = Tema.linha(8, Tema.Chip.primario("Livre"),
            Tema.Chip.espera("Dia lotado: lista de espera"), Tema.Chip.neutro("Ocupado"));
        legenda.setAlignmentX(0);
        meio.add(legenda);
        card.add(meio, BorderLayout.CENTER);

        // resumo e confirmação
        JPanel rodape = new JPanel(new BorderLayout(12, 0));
        rodape.setOpaque(false);
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDA), new EmptyBorder(14, 0, 0, 0)));
        JPanel resumo = new JPanel(new GridLayout(2, 1, 0, 2));
        resumo.setOpaque(false);
        Tema.Botao confirmar = Tema.Botao.primario("Confirmar agendamento").grande();
        if (horarioEscolhido == null) {
            resumo.add(Tema.rotulo("Nenhum horário escolhido", Font.BOLD, 15, Tema.SUAVE));
            resumo.add(Tema.rotulo("Clique em um horário livre acima.", Font.PLAIN, 13, Tema.SUAVE));
            confirmar.setEnabled(false);
        } else {
            boolean lotado = InfoClinica.consultasNoDia(controller, m, horarioEscolhido.toLocalDate()) >= InfoClinica.LIMITE_POR_DIA;
            resumo.add(Tema.rotulo(Tema.diaLongo(horarioEscolhido) + " às " + horarioEscolhido.format(Tema.HORA),
                Font.BOLD, 15, Tema.TEXTO));
            String valor = InfoClinica.semPlano(paciente)
                ? "Particular: " + Tema.dinheiro(InfoClinica.valorParticular(m))
                : "Coberto pelo plano " + paciente.getPlanoSaude();
            resumo.add(lotado
                ? Tema.rotulo("Dia lotado: você entrará na lista de espera.", Font.BOLD, 13, Tema.ESPERA)
                : Tema.rotulo(valor, Font.PLAIN, 13, Tema.SUAVE));
            if (lotado) confirmar.setText("Entrar na lista de espera");
        }
        confirmar.addActionListener(e -> confirmarAgendamento());
        rodape.add(resumo, BorderLayout.CENTER);
        rodape.add(centralizar(confirmar), BorderLayout.EAST);
        card.add(rodape, BorderLayout.SOUTH);

        col.adicionar(card);
        return Tema.rolagem(col);
    }

    private JComponent gradeHorarios(Medico m, Runnable redesenhar) {
        List<LocalDate> dias = InfoClinica.proximosDiasUteis();
        JPanel grade = new JPanel(new GridLayout(1, dias.size(), 10, 0));
        grade.setOpaque(false);
        for (LocalDate dia : dias) {
            JPanel coluna = new JPanel(new GridLayout(InfoClinica.HORAS.length + 1, 1, 0, 6));
            coluna.setOpaque(false);
            LocalDateTime ref = dia.atStartOfDay();
            JPanel cab = new JPanel(new GridLayout(2, 1));
            cab.setOpaque(false);
            JLabel sem = Tema.rotulo(Tema.diaSemanaCurto(ref), Font.BOLD, 13, Tema.TEXTO);
            JLabel data = Tema.rotulo(ref.format(Tema.DIA_MES), Font.PLAIN, 12, Tema.SUAVE);
            sem.setHorizontalAlignment(SwingConstants.CENTER);
            data.setHorizontalAlignment(SwingConstants.CENTER);
            cab.add(sem);
            cab.add(data);
            coluna.add(cab);

            boolean lotado = InfoClinica.consultasNoDia(controller, m, dia) >= InfoClinica.LIMITE_POR_DIA;
            for (int h : InfoClinica.HORAS) {
                LocalDateTime horario = InfoClinica.horario(dia, h);
                boolean doPaciente = InfoClinica.pacienteOcupado(controller, paciente, horario);
                boolean ocupado = doPaciente || InfoClinica.ocupado(controller, m, horario);
                Horario botao = new Horario(horario.format(Tema.HORA), ocupado, lotado, horario.equals(horarioEscolhido));
                if (doPaciente) botao.setToolTipText("Você já tem uma consulta neste horário");
                if (!ocupado) {
                    botao.addActionListener(e -> {
                        horarioEscolhido = horario;
                        redesenhar.run();
                    });
                }
                coluna.add(botao);
            }
            grade.add(coluna);
        }
        return grade;
    }

    private void confirmarAgendamento() {
        if (medicoEscolhido == null || horarioEscolhido == null) return;
        ResultadoAgendamento r = controller.agendarConsulta(medicoEscolhido.getNome(), horarioEscolhido);
        String quando = Tema.diaLongo(horarioEscolhido) + " às " + horarioEscolhido.format(Tema.HORA);
        if (!r.deuCerto()) {
            avisar(r.getMensagem(), TipoAviso.ERRO);
            horarioEscolhido = null;
            atualizar();
        } else if (r == ResultadoAgendamento.AGENDADA) {
            avisar("Consulta com " + medicoEscolhido.getNome() + " agendada para " + quando + ".", TipoAviso.OK);
            horarioEscolhido = null;
            abrir("consultas");
        } else {
            avisar("O dia está lotado: você entrou na lista de espera de " + medicoEscolhido.getNome()
                + ". Se alguém cancelar nesse dia, a vaga é sua.", TipoAviso.ESPERA);
            horarioEscolhido = null;
            atualizar();
        }
    }

    // =================================================================== auxiliares

    private static JComponent centralizar(JComponent c) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private static String resumir(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /** Botão de horário na grade. */
    private static class Horario extends JButton {
        private final boolean ocupado, lotado, escolhido;
        private boolean sobre;

        Horario(String texto, boolean ocupado, boolean lotado, boolean escolhido) {
            super(ocupado ? texto : texto);
            this.ocupado = ocupado;
            this.lotado = lotado;
            this.escolhido = escolhido;
            setFont(Tema.fonte(Font.BOLD, 14));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setEnabled(!ocupado);
            setPreferredSize(new Dimension(84, 38));
            setCursor(Cursor.getPredefinedCursor(ocupado ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
            if (ocupado) setToolTipText("Horário já ocupado");
            else if (lotado) setToolTipText("Este dia já tem 3 consultas: o agendamento vai para a lista de espera");
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { sobre = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { sobre = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fundo, borda, texto;
            if (ocupado) { fundo = Tema.SUPERFICIE_2; borda = Tema.SUPERFICIE_2; texto = new Color(0xA9B6B3); }
            else if (escolhido) { fundo = lotado ? Tema.ESPERA : Tema.PRIMARIA; borda = fundo; texto = Color.WHITE; }
            else if (lotado) { fundo = sobre ? new Color(0xF8E3C0) : Tema.ESPERA_CLARO; borda = new Color(0xEBCB93); texto = Tema.ESPERA; }
            else { fundo = sobre ? Tema.PRIMARIA_CLARA : Tema.SUPERFICIE; borda = sobre ? Tema.PRIMARIA : Tema.BORDA; texto = Tema.PRIMARIA_ESCURA; }
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(borda);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.setColor(texto);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String t = getText();
            int x = (getWidth() - fm.stringWidth(t)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(t, x, y);
            if (ocupado) {
                g2.drawLine(x - 2, y - fm.getAscent() / 3, x + fm.stringWidth(t) + 2, y - fm.getAscent() / 3);
            }
            g2.dispose();
        }
    }
}
