package Interface_client_lourd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditLogger {
    public static void logAction(String actionType, String tableName, int targetId, String details) {
        String admin = SessionManager.getAdminLogin();
        String sql = "INSERT INTO audit_log (admin_login, action_type, table_name, target_id, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/clubs_sport?characterEncoding=UTF-8", "root", "root");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin != null ? admin : "ANONYMOUS");
            ps.setString(2, actionType);
            ps.setString(3, tableName);
            ps.setInt(4, targetId);
            ps.setString(5, details);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
