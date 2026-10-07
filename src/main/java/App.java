import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;

public class App {

    public static void main(String[] args) {
        // Monta o plano semanal de treinos
        Map<DayOfWeek, String> plano = new EnumMap<>(DayOfWeek.class);
        plano.put(DayOfWeek.MONDAY, "Peito e Tríceps");
        plano.put(DayOfWeek.TUESDAY, "Costas, Bíceps e Antebraço");
        plano.put(DayOfWeek.WEDNESDAY, "Perna compelto. (Quadríceps, Posterior e Panurrilha");
        plano.put(DayOfWeek.THURSDAY, "Ombros e Trapézio");
        plano.put(DayOfWeek.FRIDAY, "Descanso");
        plano.put(DayOfWeek.SATURDAY, "Peito e Trìceps");
        plano.put(DayOfWeek.SUNDAY, "Costas, Bíceps e Antebraço");

        // Usa o horário do Brasil, porque o GitHub roda em horário UTC
        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        DayOfWeek diaDaSemana = hoje.getDayOfWeek();

        String treinoDeHoje = plano.get(diaDaSemana);

        System.out.println("=== TREINO AUTOMATIZADO ===");
        System.out.println("Data: " + hoje);
        System.out.println("Dia: " + diaDaSemana);
        System.out.println("Treino de hoje: " + treinoDeHoje);
    }
}
