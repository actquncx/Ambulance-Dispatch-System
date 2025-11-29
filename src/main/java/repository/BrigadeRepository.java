package repository;

import model.Brigade;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BrigadeRepository {
    private Connection connection;

    public BrigadeRepository() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public List<Brigade> findFreeBrigades() throws SQLException {
        List<Brigade> brigades = new ArrayList<>();
        String sql = "SELECT * FROM Brigade WHERE status = 'free'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                brigades.add(new Brigade(
                        rs.getInt("brigade_id"),
                        rs.getString("status"),
                        rs.getString("current_address")
                ));
            }
        }
        return brigades;
    }

    public void updateStatus(int brigadeId, String status) throws SQLException {
        String sql = "UPDATE Brigade SET status = ? WHERE brigade_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, brigadeId);
            pstmt.executeUpdate();
        }
    }

    public void initTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Brigade (brigade_id INTEGER PRIMARY KEY, status TEXT, current_address TEXT)");
            stmt.execute("INSERT OR IGNORE INTO Brigade (brigade_id, status, current_address) VALUES (1, 'free', 'Center')");
            stmt.execute("INSERT OR IGNORE INTO Brigade (brigade_id, status, current_address) VALUES (2, 'free', 'Station 2')");
        }
    }
}