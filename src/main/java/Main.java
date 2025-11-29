import repository.*;
import service.*;
import service.MedicalSystemSwingApp;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Підключення до БД (Використовуємо відносний шлях для Git)
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:testemergency.db";
            Connection conn = DriverManager.getConnection(url);

            System.out.println("БД підключено.");

            // 2. Ініціалізація Репозиторіїв (Dependency Injection)
            PatientRepository patientRepo = new PatientRepository(conn);
            BrigadeRepository brigadeRepo = new BrigadeRepository(conn);
            CallRepository callRepo = new CallRepository(conn);

            // Створення таблиць, якщо їх немає (для першого запуску)
            brigadeRepo.initTable();
            callRepo.initTable();

            // 3. Налаштування Сервісу
            BrigadeSelectionStrategy strategy = new FirstAvailableStrategy();
            AmbulanceService service = new AmbulanceService(patientRepo, callRepo, brigadeRepo, strategy);

            // 4. Запуск UI
            SwingUtilities.invokeLater(() -> {
                new MedicalSystemSwingApp(service).setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}