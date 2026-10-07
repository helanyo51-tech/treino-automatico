import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class App {

    public static void main(String[] args) throws IOException {
        // 1) Plano semanal de treinos
        Map<DayOfWeek, String> plano = new EnumMap<>(DayOfWeek.class);
        plano.put(DayOfWeek.MONDAY, "Peito e Tríceps");
        plano.put(DayOfWeek.TUESDAY, "Costas e Bíceps");
        plano.put(DayOfWeek.WEDNESDAY, "Pernas");
        plano.put(DayOfWeek.THURSDAY, "Ombros e Abdômen");
        plano.put(DayOfWeek.FRIDAY, "Treino completo (full body)");
        plano.put(DayOfWeek.SATURDAY, "Cardio leve");
        plano.put(DayOfWeek.SUNDAY, "Descanso");

        // 2) Data de hoje no horário do Brasil
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        DayOfWeek diaSemana = hoje.getDayOfWeek();
        String treinoHoje = plano.get(diaSemana);

        System.out.println("=== TREINO AUTOMATIZADO ===");
        System.out.println("Data: " + hoje);
        System.out.println("Treino de hoje: " + treinoHoje);

        // 3) Lê os registros de treino (dados/registros.csv)
        List<String[]> registros = lerRegistros(Path.of("dados/registros.csv"));

        // 4) Gera o painel em docs/index.html
        String html = gerarHtml(plano, hoje, diaSemana, treinoHoje, registros);
        Files.createDirectories(Path.of("docs"));
        Files.writeString(Path.of("docs/index.html"), html);
        System.out.println("Painel gerado em docs/index.html");
    }

    private static List<String[]> lerRegistros(Path arquivo) throws IOException {
        List<String[]> lista = new ArrayList<>();
        if (!Files.exists(arquivo)) {
            return lista;
        }
        List<String> linhas = Files.readAllLines(arquivo);
        for (int i = 1; i < linhas.size(); i++) { // pula o cabeçalho
            String linha = linhas.get(i).trim();
            if (linha.isEmpty()) {
                continue;
            }
            String[] partes = linha.split(",");
            if (partes.length >= 4) {
                lista.add(partes);
            }
        }
        return lista;
    }

    private static String nomeDia(DayOfWeek d) {
        String nome = d.getDisplayName(TextStyle.FULL, Locale.of("pt", "BR"));
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }

    private static String gerarHtml(Map<DayOfWeek, String> plano, LocalDate hoje,
                                    DayOfWeek diaSemana, String treinoHoje,
                                    List<String[]> registros) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"pt-BR\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
        sb.append("<title>Treino Automatizado</title>\n");
        sb.append("<script src=\"https://cdn.jsdelivr.net/npm/chart.js@4\"></script>\n");
        sb.append("<style>\n");
        sb.append("body{font-family:Arial,sans-serif;background:#0f172a;color:#e2e8f0;margin:0;padding:20px}\n");
        sb.append(".container{max-width:900px;margin:auto}\n");
        sb.append("h1{margin-bottom:4px}\n");
        sb.append(".sub{color:#94a3b8;margin-top:0}\n");
        sb.append(".card{background:#1e293b;border-radius:12px;padding:20px;margin:16px 0}\n");
        sb.append(".hoje{border-left:6px solid #22c55e}\n");
        sb.append(".hoje h2{margin:0 0 8px 0}\n");
        sb.append(".treino{font-size:28px;font-weight:bold;color:#22c55e}\n");
        sb.append("table{width:100%;border-collapse:collapse}\n");
        sb.append("td,th{padding:10px;border-bottom:1px solid #334155;text-align:left}\n");
        sb.append("tr.atual td{background:#14532d;font-weight:bold}\n");
        sb.append("footer{color:#64748b;font-size:13px;text-align:center;margin-top:30px}\n");
        sb.append("</style>\n</head>\n<body>\n<div class=\"container\">\n");
        sb.append("<h1>🏋️ Treino Automatizado</h1>\n");
        sb.append("<p class=\"sub\">Painel gerado automaticamente com Java + GitHub Actions</p>\n");

        // Cartão do treino de hoje
        sb.append("<div class=\"card hoje\">\n");
        sb.append("<h2>Hoje, ").append(nomeDia(diaSemana)).append(" (").append(hoje).append(")</h2>\n");
        sb.append("<div class=\"treino\">").append(treinoHoje).append("</div>\n</div>\n");

        // Plano semanal
        sb.append("<div class=\"card\">\n<h2>Plano da semana</h2>\n<table>\n");
        sb.append("<tr><th>Dia</th><th>Treino</th></tr>\n");
        for (DayOfWeek d : DayOfWeek.values()) {
            sb.append(d == diaSemana ? "<tr class=\"atual\">" : "<tr>");
            sb.append("<td>").append(nomeDia(d)).append("</td>");
            sb.append("<td>").append(plano.get(d)).append("</td></tr>\n");
        }
        sb.append("</table>\n</div>\n");

        // Gráfico de evolução das cargas
        sb.append("<div class=\"card\">\n<h2>Evolução das cargas</h2>\n");
        if (registros.isEmpty()) {
            sb.append("<p>Nenhum treino registrado ainda.</p>\n");
        } else {
            sb.append("<canvas id=\"grafico\" height=\"120\"></canvas>\n");
        }
        sb.append("</div>\n");

        // Tabela de registros
        sb.append("<div class=\"card\">\n<h2>Últimos registros</h2>\n<table>\n");
        sb.append("<tr><th>Data</th><th>Exercício</th><th>Carga (kg)</th><th>Repetições</th></tr>\n");
        for (int i = registros.size() - 1; i >= 0 && i >= registros.size() - 10; i--) {
            String[] r = registros.get(i);
            sb.append("<tr><td>").append(r[0]).append("</td><td>").append(r[1])
              .append("</td><td>").append(r[2]).append("</td><td>").append(r[3]).append("</td></tr>\n");
        }
        sb.append("</table>\n</div>\n");

        sb.append("<footer>Atualizado em ").append(hoje).append(" • helanyo51-tech</footer>\n");
        sb.append("</div>\n");

        // Script do gráfico (uma linha por exercício)
        if (!registros.isEmpty()) {
            sb.append("<script>\n");
            sb.append("const dados = [\n");
            for (String[] r : registros) {
                sb.append("  {data:\"").append(r[0].trim()).append("\", exercicio:\"")
                  .append(r[1].trim().replace("\"", "")).append("\", carga:")
                  .append(r[2].trim()).append("},\n");
            }
            sb.append("];\n");
            sb.append("const datas = [...new Set(dados.map(d => d.data))].sort();\n");
            sb.append("const exercicios = [...new Set(dados.map(d => d.exercicio))];\n");
            sb.append("const cores = ['#22c55e','#38bdf8','#f59e0b','#f43f5e','#a78bfa'];\n");
            sb.append("const datasets = exercicios.map((ex, i) => ({\n");
            sb.append("  label: ex,\n");
            sb.append("  data: datas.map(dt => { const f = dados.find(d => d.data === dt && d.exercicio === ex); return f ? f.carga : null; }),\n");
            sb.append("  borderColor: cores[i % cores.length],\n");
            sb.append("  backgroundColor: cores[i % cores.length],\n");
            sb.append("  spanGaps: true,\n");
            sb.append("  tension: 0.2\n");
            sb.append("}));\n");
            sb.append("new Chart(document.getElementById('grafico'), {\n");
            sb.append("  type: 'line',\n");
            sb.append("  data: { labels: datas, datasets: datasets },\n");
            sb.append("  options: { plugins: { legend: { labels: { color: '#e2e8f0' } } },\n");
            sb.append("    scales: { x: { ticks: { color: '#94a3b8' } }, y: { ticks: { color: '#94a3b8' }, title: { display: true, text: 'kg', color: '#94a3b8' } } } }\n");
            sb.append("});\n");
            sb.append("</script>\n");
        }

        sb.append("</body>\n</html>\n");
        return sb.toString();
    }
}
