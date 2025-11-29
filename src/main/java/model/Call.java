package model;

import java.time.LocalDateTime;

public class Call {
    private int id;
    private int patientId;
    private Integer brigadeId; // Може бути null, якщо бригада ще не призначена
    private String diagnosis;
    private String address;
    private LocalDateTime callDate;

    public Call(int patientId, String address, String diagnosis) {
        this.patientId = patientId;
        this.address = address;
        this.diagnosis = diagnosis;
        this.callDate = LocalDateTime.now();
    }

    // Геттери та Сеттери
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public Integer getBrigadeId() { return brigadeId; }
    public void setBrigadeId(Integer brigadeId) { this.brigadeId = brigadeId; }
    public String getDiagnosis() { return diagnosis; }
    public String getAddress() { return address; }
    public LocalDateTime getCallDate() { return callDate; }
}