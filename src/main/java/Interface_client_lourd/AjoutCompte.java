package Interface_client_lourd; 

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mindrot.jbcrypt.BCrypt; 

public class AjoutCompte extends JFrame { 
    private static final long serialVersionUID = 318966351394263965L; 

    //  Champs du formulaire 
    private final JTextField nameField     = new JTextField(20);     
    private final JTextField prenomField   = new JTextField(20);     
    private final JTextField mailField     = new JTextField(20);     
    private final JPasswordField pwdField  = new JPasswordField(20); 
    private final JCheckBox eluCheck       = new JCheckBox("Élu");    
    private final JCheckBox acteurCheck    = new JCheckBox("Acteur sportif"); 
    private final JTextField filePathField = new JTextField(20);     
    private File selectedFile;

    // -Paramètres JDBC
    private static final String HOST     = "localhost";   // Adresse du serveur MySQL
    private static final String DB_NAME  = "clubs_sport"; // Nom de la base de données
    private static final String LOGIN    = "root";        // Nom d'utilisateur de la BDD
    private static final String PASSWORD = "root";        // Mot de passe de la BDD

    public AjoutCompte() {                      
        super("Ajout d'un compte");             
        initUI();                               
    }

    private void initUI() {                     
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 500);                         
        setLocationRelativeTo(null);              
        setLayout(new BorderLayout(10,10));       

        // PANEL central : grille 7 lignes x 2 colonnes, espacement de 5px
        JPanel center = new JPanel(new GridLayout(7, 2, 5, 5)); 
        center.add(new JLabel("Nom :"));           
        center.add(nameField);                     
        center.add(new JLabel("Prénom :"));        
        center.add(prenomField);                   
        center.add(new JLabel("Mail :"));          
        center.add(mailField);                     
        center.add(new JLabel("Mot de passe :"));  
        center.add(pwdField);                      
        center.add(new JLabel("Élu :"));           
        center.add(eluCheck);                      
        center.add(new JLabel("Acteur sportif :")); 
        center.add(acteurCheck);                   

        // Section Justificatif (PDF) : label + champ + bouton
        center.add(new JLabel("Justificatif (PDF) :"));   
        JPanel justifPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        filePathField.setEditable(false);                  
        JButton browseBtn = new JButton("Parcourir…");     
        browseBtn.addActionListener(e -> {                
            JFileChooser chooser = new JFileChooser();     
            chooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf")); 
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
                selectedFile = chooser.getSelectedFile();  
                filePathField.setText(selectedFile.getAbsolutePath()); 
            }
        });
        justifPanel.add(filePathField);                     
        justifPanel.add(Box.createHorizontalStrut(5));     
        justifPanel.add(browseBtn);                        
        center.add(justifPanel);                          

        add(center, BorderLayout.CENTER); 

        // PANEL bas : boutons « Valider » et « Annuler »
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        JButton submit = new JButton("Valider");   
        JButton cancel = new JButton("Annuler");   
        south.add(cancel);                         
        south.add(submit);                         
        add(south, BorderLayout.SOUTH);           

        cancel.addActionListener(e -> dispose()); 
        submit.addActionListener(e -> {
			try {
				handleSubmit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}); 
    }

    private void handleSubmit() throws FileNotFoundException, IOException { 
        String nom    = nameField.getText().trim();      
        String prenom = prenomField.getText().trim();    
        String mail   = mailField.getText().trim();      
        String pwd    = new String(pwdField.getPassword());
        String pwdHash = BCrypt.hashpw(pwd, BCrypt.gensalt(12));
        boolean isElu = eluCheck.isSelected();           
        boolean isAct = acteurCheck.isSelected();        
                                  

        // Vérification minimale des champs obligatoires
        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty() || pwd.isEmpty()) {
            JOptionPane.showMessageDialog(this,         
                "Veuillez remplir tous les champs obligatoires.",
                "Champs manquants",
                JOptionPane.WARNING_MESSAGE);
            return; 
        }

        // Requête SQL d’insertion avec paramètres (PreparedStatement)
        String sql = """
            INSERT INTO compte
              (Nom, Prénom, Mail, Mot_passe, Elu, Acteur_sport, Justificatif)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (
            Connection conn = getConnection();                
            PreparedStatement ps = conn.prepareStatement(sql) ;
        	// si on a selectionné un fichier j'instancie un flux binaire sur ce fichier 
            FileInputStream fis = (selectedFile != null)	  // crée un FileInputStream si un fichier a été choisi
            					  ? new FileInputStream(selectedFile)
            					  : null
        ) {
            ps.setString(1, nom);      
            ps.setString(2, prenom);   
            ps.setString(3, mail);     
            ps.setString(4, pwdHash);      
            ps.setBoolean(5, isElu);   
            ps.setBoolean(6, isAct);   
            
            //Bind du PDF ou Null
            if(fis != null) {
            	//cette ligne de  code sert à lier le contenu binaire du fichier pdf a la 7ieme variable 
            	ps.setBinaryStream(7, fis, (int) selectedFile.length());
            }else {
            	//cette ligne de code sert de, pour le paramètre 7, mettre null dans la colonne et préciser que c'est un blob 
            	ps.setNull(7, Types.BLOB);
            }
            ps.executeUpdate();         

            JOptionPane.showMessageDialog(this,   
                "Compte ajouté avec succès !",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            AuditLogger.logAction("INSERT", "compte", -1, "Ajout compte : mail=" + mail);
            dispose(); 

        } catch (SQLException ex) { 
            ex.printStackTrace();    
            JOptionPane.showMessageDialog(this,             
                "Erreur lors de l'insertion en base : " + ex.getMessage(),
                "Erreur BDD",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Connection getConnection() throws SQLException { 
        String url = "jdbc:mysql://" + HOST + "/" + DB_NAME   
                   + "?characterEncoding=UTF-8&serverTimezone=UTC"; 
        return DriverManager.getConnection(url, LOGIN, PASSWORD); 
    }

    public static void main(String[] args) {              // Point d’entrée de l’application
        SwingUtilities.invokeLater(() -> {                // S’assure que la création UI se fait sur l’EDT
            new AjoutCompte().setVisible(true);          // Crée et affiche la fenêtre
        });
    }
}
