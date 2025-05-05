package Interface_client_lourd;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.mindrot.jbcrypt.BCrypt;
public class Page_connexion extends JFrame {
	
	private static final long serialVersionUID = -2079224152752168191L;
	private JTextField  txtLogin = new JTextField("");
	private JPasswordField  txtConnexion = new JPasswordField("");
	private JButton btnConnexion = new JButton("Connexion");
	// nom de la machine hôte qui héberge le SGBD Mysql
	final static String host = "localhost";
	// nom de la BDD sur le serveur Mysql
	final static String nomBase = "clubs_sport";
	// login de la BDD
	final static String login = "root";
	// mot de passe
	final static String motDePasse = "root";
	
	public Page_connexion() {
		super("connexion");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridLayout(3,1));
		 
		contentPane.add(username());
		contentPane.add(password());
		contentPane.add(btnConnect());
		
		btnConnexion.addActionListener(e -> priseEnMainLogin());
	
		
		// TODO Auto-generated constructor stub
	}

	public JPanel username() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		JLabel lblStatus1 = new JLabel("Username");
		lblStatus1.setPreferredSize(new Dimension(100, 30));
		content.add(lblStatus1);
		
		
		txtLogin.setPreferredSize(new Dimension(200,30));
		content.add(txtLogin);
		
		return content;
		
	}
	
	public JPanel password() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		JLabel lblStatus1 = new JLabel("Password");
		lblStatus1.setPreferredSize(new Dimension(100, 30));
		content.add(lblStatus1);
		
		
		txtConnexion.setPreferredSize(new Dimension(200,30));
		content.add(txtConnexion);
		
		return content;
		
	}
	
	public JPanel btnConnect() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		
		btnConnexion.setPreferredSize(new Dimension(100,30));
		content.add(btnConnexion);
		
	    return content;
	}
	
	//Etablir la connexion avec MySQL
	private Connection getConnection()throws SQLException{
		return DriverManager.getConnection("jdbc:mysql://" + host + "/" + nomBase + "?characterEncoding=UTF-8",
				login, motDePasse);
	}
	
	private boolean authenticateHashed(String login, String pwd) {
	    String sql = "SELECT COUNT(*) FROM admin WHERE Log = ? AND Mot_de_passe = ?";
	    try (Connection conn = getConnection();// ouvre la connexion à la JDBC
	         PreparedStatement stmt = conn.prepareStatement(sql)) {//crée un preparedStatement prêt à éxécuter la requête
	        stmt.setString(1, login); // remplace le premier ? par la valeur de la variable login
	        stmt.setString(2, pwd); // remplace le deuxième ? par la valeur de pwd
	        //cela protège contre l'injection sql
	        try (ResultSet rs = stmt.executeQuery()) {//execute la requête et renvoie un ResultSet contenant une seule ligne et une seule colonne
	            rs.next();//deplace le curseur sur la première ligne du resultat
	            return rs.getInt(1) == 1;//on compare la valeur obtenu à 1
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        return false;
	    }
	}

	
	 //authenticateHashed sert à vérifier, de manière sécurisée, que le mot de passe saisi par l’utilisateur correspond bien au mot de passe stocké dans la base de données
	/*private boolean authenticateHashed(String login, String pwd) {
		String sql ="SELECT Mot_de_passe FROM admin WHERE Log = ?";
		try(Connection conn = getConnection();
			PreparedStatement stmt=conn.prepareStatement(sql)){
			stmt.setString(1, login);
			try(ResultSet rs = stmt.executeQuery()){ // lance la requête sql en le plaçant dans un try on aura un ResultSet automatiquement fermé 
				if(!rs.next()) {// rs.next() avance le cursueur du ResultSet sur la premiere ligne
					return false;//return false s'il ny a aucune ligne 
				}
				String storedHash = rs.getString("Mot_de_passe"); // lis la valeur de la première colonne 
				return BCrypt.checkpw(pwd, storedHash);//applique l’algorithme BCrypt au mot de passe saisi (pwd) en utilisant le sel incorporé dans storedHash, puis compare les deux
			}
		}catch(SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}*/
	
	private void priseEnMainLogin() {
		String login = txtLogin.getText().trim();// on récupère le login 
		String pwd = new String(txtConnexion.getPassword());
		
		//txtConnexion.getPassword() renvoie un tableau de char[] contenant le mot de passe saisi
		//Arrays.fill(..., '0') parcourt ce tableau et remplace chaque caractère par '0'
		Arrays.fill(txtConnexion.getPassword(), '0');
		
		if(login.isEmpty() || pwd.isEmpty()) {
			showError("veuillez renseignez tous les champs");
			return;
		}
		
		//choisissones la méthode d'identification 
		boolean ok = authenticateHashed(login, pwd);
		if(ok) {
			ouvrirPageAccueil();
			
		}else {
			showError("Identifiants incorrects");
		}
	}
	
	private void ouvrirPageAccueil() {
		//invokeLater(Runnable r) place votre bloc de code dans la file d’événements de Swing, 
		//pour qu’il soit exécuté après que tous les événements en cours (clics, rafraîchissements, etc.) 
		//soient traités, et toujours sur l’EDT.
		SwingUtilities.invokeLater(() -> { // expression lambda pour instancier un runnable 
	        AccueilFrame accueil = new AccueilFrame();
	        accueil.setVisible(true);
	        // Fermer ou masquer la fenêtre de login
	        this.dispose();
	    });
		
	}
	
	private void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Page_connexion connexion = new Page_connexion();
		
	}

}
