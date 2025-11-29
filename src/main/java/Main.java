import repository.*;
import service.*;
import view.MedicalSystemSwingApp;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Репозиторії (з'єднання береться автоматично через Singleton)
            PatientRepository patientRepo = new PatientRepository();
            BrigadeRepository brigadeRepo = new BrigadeRepository();
            CallRepository callRepo = new CallRepository();

            // Ініціалізація таблиць
            brigadeRepo.initTable();
            callRepo.initTable();

            // 2. Сервіс
            BrigadeSelectionStrategy strategy = new FirstAvailableStrategy();
            AmbulanceService service = new AmbulanceService(patientRepo, callRepo, brigadeRepo, strategy);

            // 3. UI
            SwingUtilities.invokeLater(() -> {
                new MedicalSystemSwingApp(service).setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}