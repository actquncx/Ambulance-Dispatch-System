package service;

import model.Call;
import model.Patient;
import model.Brigade;
import repository.BrigadeRepository;
import repository.CallRepository;
import repository.PatientRepository;

import java.sql.SQLException;
import java.util.List;

public class AmbulanceService {
    private PatientRepository patientRepo;
    private CallRepository callRepo;
    private BrigadeRepository brigadeRepo;
    private BrigadeSelectionStrategy strategy;
    // 1. Метод для Таблиці 1: Валідація даних
    public boolean validateCallData(String name, String phoneStr, int age, String address) {
        if (name == null || name.trim().isEmpty()) return false;
        if (address == null || address.trim().isEmpty()) return false;
        if (age < 0 || age > 120) return false;

        // Перевірка телефону (має бути 10-12 цифр)
        if (phoneStr == null || !phoneStr.matches("\\d{10,12}")) return false;

        return true;
    }

    // 2. Метод для Таблиці 2: Доступність бригади
    // status: "free", "busy", "lunch"
    // shiftEndHour: о котрій закінчується зміна (наприклад, 20)
    // currentHour: поточна година (наприклад, 18)
    public boolean isBrigadeAvailable(String status, int shiftEndHour, int currentHour) {
        if (!"free".equalsIgnoreCase(status)) return false; // Якщо не вільна - відмова
        if (currentHour >= shiftEndHour) return false;      // Якщо зміна закінчилась - відмова
        return true;
    }

    // 3. Метод для Таблиці 3: Пріоритет виклику
    // diagnosis: ключові слова ("heart", "accident", "flu")
    // age: вік пацієнта
    public String calculatePriority(String diagnosis, int age) {
        if (diagnosis == null) return "LOW";
        String diag = diagnosis.toLowerCase();

        if (diag.contains("heart") || diag.contains("infarct") || diag.contains("stroke")) {
            return "CRITICAL";
        }
        if (diag.contains("accident") || (age < 3)) { // Аварія або немовля
            return "HIGH";
        }
        if (age > 80 || diag.contains("flu")) {
            return "MEDIUM";
        }
        return "LOW";
    }
    public AmbulanceService(PatientRepository pRepo, CallRepository cRepo, BrigadeRepository bRepo, BrigadeSelectionStrategy strategy) {
        this.patientRepo = pRepo;
        this.callRepo = cRepo;
        this.brigadeRepo = bRepo;
        this.strategy = strategy;
    }

    public String registerNewCall(String name, String surname, long phone, int age, String address, String diagnosis) {
        try {
            // 1. Зберігаємо пацієнта
            Patient patient = new Patient(name, surname, phone, age, address);
            int patientId = patientRepo.save(patient);

            // 2. Створюємо виклик через BUILDER (Паттерн Builder)
            Call call = new Call.Builder()
                    .setPatientId(patientId)
                    .setAddress(address)
                    .setDiagnosis(diagnosis)
                    .build();

            int callId = callRepo.save(call);

            // 3. Шукаємо бригаду
            List<Brigade> freeBrigades = brigadeRepo.findFreeBrigades();
            Brigade selectedBrigade = strategy.selectBrigade(freeBrigades, address);

            if (selectedBrigade != null) {
                // 4. Призначаємо бригаду
                callRepo.updateBrigade(callId, selectedBrigade.getId());
                brigadeRepo.updateStatus(selectedBrigade.getId(), "in_transit");
                return "Виклик #" + callId + " створено. Призначено бригаду #" + selectedBrigade.getId();
            } else {
                return "Виклик #" + callId + " створено. Вільних бригад немає, виклик у черзі.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Помилка системи: " + e.getMessage();
        }
    }
}