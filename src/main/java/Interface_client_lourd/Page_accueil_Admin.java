package Interface_client_lourd;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public class Page_accueil_Admin extends JFrame {
	
	private static final long serialVersionUID = -2453026692979619200L;

	public Page_accueil_Admin() {
		// TODO Auto-generated constructor stub
		super("Accueil");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(barreNavigation(), BorderLayout.NORTH);
		
	}
	public JToolBar barreNavigation() {
		JToolBar toolBar = new JToolBar();
		
		JButton btnConnexionPassword = new JButton("connexion et mot de passe");
		toolBar.add(btnConnexionPassword);
		
		JButton btnHistorique = new JButton("Historique des connexion");
		toolBar.add(btnHistorique);
		
		JButton btnRecherche = new JButton("Rechercher des clubs");
		toolBar.add(btnRecherche);
		
		return toolBar;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Page_accueil_Admin accueil = new Page_accueil_Admin();
		accueil.setVisible(true);
	}

}
