package repository;

import model.Call;
import java.sql.*;

public class CallRepository {
    private Connection connection;

    public CallRepository(Connection connection) {
        this.connection = connection;
    }

    public int save(Call call) throws SQLException {
        // Спрощена схема для прикладу, адаптована з вашого коду
        String sql = "INSERT INTO Call (patient_id, address, diagnosis_details, call_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, call.getPatientId());
            pstmt.setString(2, call.getAddress());
            pstmt.setString(3, call.getDiagnosis());
            pstmt.setString(4, call.getCallDate().toString());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    call.setId(rs.getInt(1));
                    return call.getId();
                }
            }
        }
        return -1;
    }

    public void updateBrigade(int callId, int brigadeId) throws SQLException {
        String sql = "UPDATE Call SET brigade_id = ? WHERE call_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, brigadeId);
            pstmt.setInt(2, callId);
            pstmt.executeUpdate();
        }
    }

    public void initTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Patient (patient_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, surname TEXT, phone INTEGER, age INTEGER, address TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Call (call_id INTEGER PRIMARY KEY AUTOINCREMENT, patient_id INTEGER, brigade_id INTEGER, address TEXT, diagnosis_details TEXT, call_date TEXT)");
        }
    }
}