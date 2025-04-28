package Interface_client_lourd;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Page_connexion extends JFrame {
	
	private static final long serialVersionUID = -2079224152752168191L;


	public Page_connexion() {
		super("connexion");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridLayout(3,1));
		 
		contentPane.add(username());
		contentPane.add(password());
		contentPane.add(btnConnect());
	
		
		// TODO Auto-generated constructor stub
	}

	public JPanel username() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		JLabel lblStatus1 = new JLabel("Username");
		lblStatus1.setPreferredSize(new Dimension(100, 30));
		content.add(lblStatus1);
		
		JTextField  txtConnexion = new JTextField("");
		txtConnexion.setPreferredSize(new Dimension(200,30));
		content.add(txtConnexion);
		
		return content;
		
	}
	
	public JPanel password() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		JLabel lblStatus1 = new JLabel("Password");
		lblStatus1.setPreferredSize(new Dimension(100, 30));
		content.add(lblStatus1);
		
		JTextField  txtConnexion = new JTextField("");
		txtConnexion.setPreferredSize(new Dimension(200,30));
		content.add(txtConnexion);
		
		return content;
		
	}
	
	public JPanel btnConnect() {
		JPanel content = new JPanel();
		content.setLayout(new FlowLayout());
		
		JButton btnConnexion = new JButton("Connexion");
		btnConnexion.setPreferredSize(new Dimension(100,30));
		content.add(btnConnexion);
		
	    return content;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Page_connexion connexion = new Page_connexion();
		connexion.setVisible(true);
	}

}
