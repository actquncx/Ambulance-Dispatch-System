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

    public AmbulanceService(PatientRepository pRepo, CallRepository cRepo, BrigadeRepository bRepo, BrigadeSelectionStrategy strategy) {
        this.patientRepo = pRepo;
        this.callRepo = cRepo;
        this.brigadeRepo = bRepo;
        this.strategy = strategy;
    }

    // Головний метод, який замінює "спагеті-код" з Register
    public String registerNewCall(String name, String surname, long phone, int age, String address, String diagnosis) {
        try {
            // 1. Зберігаємо пацієнта
            Patient patient = new Patient(name, surname, phone, age, address);
            int patientId = patientRepo.save(patient);

            // 2. Створюємо виклик
            Call call = new Call(patientId, address, diagnosis);
            int callId = callRepo.save(call);

            // 3. Шукаємо бригаду (Стратегія)
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