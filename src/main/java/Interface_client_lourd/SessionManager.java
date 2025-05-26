package Interface_client_lourd;

public class SessionManager {
    private static String adminLogin;
    public static void setAdminLogin(String login) { adminLogin = login; }
    public static String getAdminLogin() { return adminLogin; }
}
