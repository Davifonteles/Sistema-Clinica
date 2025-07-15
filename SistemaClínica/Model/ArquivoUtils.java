package SistemaClínica.Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ArquivoUtils {

    public static void salvarConsultasRealizadas(List<Consulta> consultas, String caminhoArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write("DataHora,Medico,Paciente,Valor,Avaliacao,Comentario\n");

            for (Consulta c : consultas) {
                if (c.isRealizada()) {
                    String data = c.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    String medico = c.getMedico().getNome();
                    String paciente = c.getPaciente().getNome();
                    Double valor = c.getValorCobrado() != null ? c.getValorCobrado() : 0.0;
                    String avaliacao = (c.getAvaliacao() != null) ? c.getAvaliacao().toString() : "Sem nota";
                    String comentario = (c.getComentario()) != null ? c.getComentario() : "Sem comentário";

                    writer.write(String.format("%s,%s,%s,%.2f,%s,%s\n",
                            data, medico, paciente, valor, avaliacao, comentario));
                }
            }

            System.out.println("Consultas salvas com sucesso em: " + caminhoArquivo);

        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo: " + e.getMessage());
        }
    }
}
