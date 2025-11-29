package repository;

import model.Patient;
import java.sql.*;

public class PatientRepository {
    private Connection connection;

    public PatientRepository(Connection connection) {
        this.connection = connection;
    }

    public int save(Patient patient) throws SQLException {
        String sql = "INSERT INTO Patient (name, surname, phone, age, address) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patient.getName());
            pstmt.setString(2, patient.getSurname());
            pstmt.setLong(3, patient.getPhone());
            pstmt.setInt(4, patient.getAge());
            pstmt.setString(5, patient.getAddress());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                    return patient.getId();
                }
            }
        }
        return -1;
    }
}