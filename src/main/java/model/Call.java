package model;

import java.time.LocalDateTime;

public class Call {
    private int id;
    private int patientId;
    private Integer brigadeId;
    private String diagnosis;
    private String address;
    private LocalDateTime callDate;

    // Приватний конструктор (використовується Білдером)
    private Call(Builder builder) {
        this.patientId = builder.patientId;
        this.address = builder.address;
        this.diagnosis = builder.diagnosis;
        this.brigadeId = builder.brigadeId;
        this.callDate = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public Integer getBrigadeId() { return brigadeId; }
    public void setBrigadeId(Integer brigadeId) { this.brigadeId = brigadeId; }
    public String getDiagnosis() { return diagnosis; }
    public String getAddress() { return address; }
    public LocalDateTime getCallDate() { return callDate; }

    // Внутрішній клас Builder
    public static class Builder {
        private int patientId;
        private String address;
        private String diagnosis;
        private Integer brigadeId;

        public Builder setPatientId(int patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setDiagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
            return this;
        }

        public Call build() {
            return new Call(this);
        }
    }
}