package Interface_client_lourd;
 

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
// pour File, FileOutputStream, IOException, etc.
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.mindrot.jbcrypt.BCrypt;             

/**
 * Fenêtre principale avec une toolbar simulant des onglets
 */
public class AccueilFrame extends JFrame {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 638363932619892452L;
	// -------- Constantes pour identifier les cartes --------
    private static final String CARD_CONNEXION  = "connexion et mot de passe";     // Clé pour la carte Connexion
    private static final String CARD_HISTORIQUE = "Historique des connexions";    // Clé pour la carte Historique
    private static final String CARD_RECHERCHE  = "Rechercher des clubs";         // Clé pour la carte Recherche
    private static final String CARD_DEMANDE    = "Demande d'ajout"; // Clé pour la carte Demande
    // ===== Composants et modèles pour l’onglet Connexion et mot de passe =====
    private JTable               compteTable;          // Le tableau qui affichera les comptes
    private DefaultTableModel    tableModel;           // Le modèle de données du tableau
    private JButton              resetButton;          // Le bouton pour réinitialiser le mot de passe
    private JButton 			 addButton; 		   // le bouton pour ajouter un compte 
    private JButton				 editButton;           // le bouton pour modifier un compte 
    private JButton				 deleteButton;		   // le bouton pour supprimer un compte 
    // -------- Attributs Swing --------
    private final CardLayout cardLayout = new CardLayout();                       // Layout permettant de basculer entre plusieurs panneaux
    private final JPanel centerPanel = new JPanel(cardLayout);                    // Panneau central qui contient les différentes cartes
    
 // ==== Composants et modèle pour l’onglet Recherche de clubs ====  
   // private JTextField clubNameField;        //  Champ où l’utilisateur tape le nom du club  
    private JTextField communeField;         //  Champ pour la commune  
    private JTextField deptField;            //  Champ pour le département  
    private JTextField regionField;			//  Champ pour la région 
    private JTextField federationField;     // champ pour la fédération 
    private JButton    searchButton;         //  Bouton pour lancer la recherche  

    private JTable               searchTable;      //  Tableau Swing pour afficher le résultat  
    private DefaultTableModel    searchTableModel; //  Modèle de données du tableau  
    
  // ==== Composants et modèles pour l'onglet historique de connexion =====
    private JTextArea logsTextArea; //zone de texte partagé pour afficher le contenu des logs 
    
    private static final String LOG_FILE_PATH = "C:/Users/Utilisateur/log/mtt.log" ;

 // ==== Composants et modèles pour l'onglet Demande d'ajout =====
    private JTable  demandeTable; 
    private DefaultTableModel demandeTableModel; 
    

 // nom de la machine hôte qui héberge le SGBD Mysql
 	final static String host = "localhost";
 	// nom de la BDD sur le serveur Mysql
 	final static String nomBase = "clubs_sport";
 	// login de la BDD
 	final static String login = "root";
 	// mot de passe
 	final static String motDePasse = "root";
 	
 	
    /**
     * Constructeur : initialise la fenêtre
     */
    public AccueilFrame() {
        super("Accueil");                   // Appelle le constructeur parent (JFrame) en passant le titre de la fenêtre
        initUI();                            // Methode interne pour construire et agencer tous les composants
    }

    /**
     * Configure l'interface utilisateur (UI)
     */
    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // Ferme l'application entière lorsque l'utilisateur ferme cette fenêtre
        setSize(900, 500);                        // Définit une taille initiale de 900px de large par 500px de haut
        setLocationRelativeTo(null);              // Centre la fenêtre sur l'écran quel que soit le moniteur
        setLayout(new BorderLayout());            // Définit le layout principal en BorderLayout (North, South, East, West, Center)

        // --- Création de la barre d'outils (toolbar) ---
        JToolBar toolBar = new JToolBar();        // Instancie un nouvel objet JToolBar
        toolBar.setFloatable(false);              // Désactive la possibilité de détacher la toolbar comme une fenêtre flottante

        // Groupe pour que les boutons-onglets soient mutuellement exclusifs
        ButtonGroup group = new ButtonGroup();    // Chaque bouton Toggle ajouté appartiendra à ce groupe

        // Création d'un bouton-onglet pour "Connexion"
        JToggleButton btnConnexion = createToggle(CARD_CONNEXION, group);
        // Création d'un bouton-onglet pour l'historique
        JToggleButton btnHist      = createToggle(CARD_HISTORIQUE, group);
        // Création d'un bouton-onglet pour la recherche de clubs
        JToggleButton btnRech      = createToggle(CARD_RECHERCHE, group);
        // Création d'un bouton-onglet pour la demande d'ajout
        JToggleButton btnDemande   = createToggle(CARD_DEMANDE, group);

        // Ajout des boutons à la toolbar avec des séparateurs visuels
        toolBar.add(btnConnexion);                // Ajoute le bouton de connexion
        toolBar.addSeparator();                   // Ajoute un espace visuel
        toolBar.add(btnHist);                     // Ajoute le bouton historique
        toolBar.addSeparator();                   // Séparateur
        toolBar.add(btnRech);                     // Bouton recherche
        toolBar.addSeparator();                   // Séparateur
        toolBar.add(btnDemande);                  // Bouton demande d'ajout

        add(toolBar, BorderLayout.NORTH);         // Ajoute la toolbar en haut (North) de la fenêtre
        //rafraichissement des pages connexion et demande 
        btnConnexion.addActionListener(e -> loadCompteData());
        btnDemande.addActionListener(e-> loadDemandesData());

        // --- Construction des panneaux correspondants à chaque onglet ---
        centerPanel.add(createConnexionPanel(),  CARD_CONNEXION);    // Ajoute le panneau connexion sous la clé CARD_CONNEXION
        centerPanel.add(createHistoriquePanel(), CARD_HISTORIQUE);   // Ajoute le panneau historique
        centerPanel.add(createRecherchePanel(),  CARD_RECHERCHE);    // Ajoute le panneau recherche
        centerPanel.add(createDemandePanel(),    CARD_DEMANDE);      // Ajoute le panneau demande d'ajout

        add(centerPanel, BorderLayout.CENTER);     // Place le panneau central (cards) au centre

        // Sélection du bouton par défaut et affichage de la première carte
        btnConnexion.setSelected(true);             // Marque le bouton Connexion comme sélectionné
        cardLayout.show(centerPanel, CARD_CONNEXION); // Affiche la carte Connexion en premier
    }

    /**
     * Méthode utilitaire qui crée un JToggleButton pour changer de carte
     * @param title la clé du CardLayout et le texte du bouton
     * @param group le ButtonGroup auquel ajouter le bouton
     * @return un JToggleButton configuré
     */
    private JToggleButton createToggle(String title, ButtonGroup group) {
        // Création d'une action anonyme qui change la carte affichée
        AbstractAction action = new AbstractAction(title) {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Affiche le panneau identifié par 'title'
                cardLayout.show(centerPanel, title);
            }
        };

        JToggleButton btn = new JToggleButton(action); // Instancie le bouton avec l'action définie
        btn.setFocusable(false);                       // Retire le focus visuel au clic pour un look plus propre
        group.add(btn);                                // Ajoute ce bouton au groupe pour l'exclusion mutuelle
        return btn;                                    // Retourne le bouton prêt à l'emploi
    }

    // ===== Méthodes de création des panneaux pour chaque onglet =====

    /**  
     *  Construction de l’onglet “Connexion et mot de passe” :  
     *     - Un tableau pour afficher tous les comptes  
     *     - Un bouton pour réinitialiser le mot de passe  
     */
    private JPanel createConnexionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));  //  Panel principal en BorderLayout

        //  Définition des colonnes du tableau
        String[] cols = { "Nom", "Prénom", "Mail", "Mot de passe", "Élu", "Acteur Sport" };
        //  Modèle de tableau + interdiction d’éditer les cellules
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // 24) Aucune cellule ne pourra être modifiée à la main
            }
        };

        //  Création du JTable à partir du modèle
        compteTable = new JTable(tableModel);
        //  Ajout dans un JScrollPane pour avoir des barres de défilement
        panel.add(new JScrollPane(compteTable), BorderLayout.CENTER);

        loadCompteData(); //  J’interroge la BDD pour remplir les lignes du tableau

        //  Je crée le bouton de réinitialisation
        resetButton = new JButton("Réinitialiser mot de passe");
        //  Quand on clique dessus, on appelle handleResetPassword()
        resetButton.addActionListener(e -> handleResetPassword());

        // je crée le bouton d' ajout 
        addButton = new JButton("Ajout d'un compte");
        // Quand on clique dessus on appelle handleAddUser 
        addButton.addActionListener(e -> handleAddUser() );
        
        //je crée le boutton modifier un compte 
        editButton = new JButton("modifier un compte");
        //quand on clique dessus on appelle handleEditUser 
        editButton.addActionListener(e -> handleEditUser());
        
        //je crée le bouton pour supprimer un compte 
        deleteButton = new JButton("supprimer un compte");
        //quand on clique dessus on appelle handleExitUser 
        deleteButton.addActionListener(e -> handleExitUser());
        
        
        //  Je place le bouton dans un petit panneau en bas à droite
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(resetButton);
        south.add(addButton);
        south.add(editButton);
        south.add(deleteButton);
        panel.add(south, BorderLayout.SOUTH);

        return panel; //  Je renvoie l’onglet complet
    }
    
    /**  
     *  Charge les données depuis la table 'compte' en BDD  
     *     et remplit le tableModel  
     */
    private void loadCompteData() {
        tableModel.setRowCount(0); // 33) Vide d’abord toutes les lignes existantes

        String sql = "SELECT Nom, Prénom, Mail, Mot_passe, Elu, Acteur_sport FROM compte";
        try (Connection conn = getConnection();                     //  Ouvre la connexion
             Statement stmt = conn.createStatement();               //  Crée un statement simple
             ResultSet rs = stmt.executeQuery(sql)) {               //  Exécute la requête
            while (rs.next()) {                                     //  Tant qu’il y a une ligne…
                Object[] row = {                                  //  Je récupère chaque colonne
                    rs.getString("Nom"),
                    rs.getString("Prénom"),
                    rs.getString("Mail"),
                    rs.getString("Mot_passe"),
                    rs.getBoolean("Elu"),
                    rs.getBoolean("Acteur_sport")
                };
                tableModel.addRow(row);                            //  J’ajoute la ligne au modèle
            }
        } catch (SQLException ex) {                                 //  Si ça plante, j’affiche l’erreur
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des comptes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**  
     * Réinitialise le mot de passe du compte sélectionné  
     *     et met à jour la BDD et le tableau à l’écran  
     */
    private void handleResetPassword() {
        int row = compteTable.getSelectedRow();                    //  Récupère l’indice de la ligne sélectionnée
        if (row < 0) {                                             //  Si rien n’est sélectionné…
            JOptionPane.showMessageDialog(this,
                "Sélectionnez d’abord un compte dans le tableau",
                "Aucun compte sélectionné",
                JOptionPane.WARNING_MESSAGE);
            return;                                                //  On arrête là
        }

        //  On prend le mail (clé unique) pour savoir quel compte mettre à jour
        String mail = (String) tableModel.getValueAt(row, 2);

        //  Génère un nouveau mot de passe aléatoire de 8 caractères
        String newPwd = generateRandomPassword(8);
        //  On le hache avec BCrypt pour la sécurité
        String newHash = BCrypt.hashpw(newPwd, BCrypt.gensalt(12));

        // Prépare la requête UPDATE pour changer le hash en BDD
        String sql = "UPDATE compte SET Mot_passe = ? WHERE Mail = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);                          //  Place le nouveau hash
            stmt.setString(2, mail);                             //  Place le mail du compte
            int updated = stmt.executeUpdate();                  //  Exécute la mise à jour et retourne le nombre de ligne modifié 

            if (updated == 1) {   //  Si une ligne a bien été modifiée
                loadCompteData();
            	//tableModel.setValueAt(newHash, row, 3);          //  Met à jour le hash dans le tableau
                // Affiche la boîte de dialogue avec le nouveau mot de passe en clair
                JOptionPane.showMessageDialog(this,
                    "Le mot de passe de \"" + mail + "\" a été réinitialisé.\n"
                    + "Nouveau mot de passe : " + newPwd,
                    "Réinitialisation réussie",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Si rien n’a bougé, on informe l’utilisateur
                JOptionPane.showMessageDialog(this,
                    "Échec de la mise à jour en base.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {                                //  En cas d’erreur SQL
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur SQL durant la réinitialisation",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void handleAddUser() {
    	//invokeLater(Runnable r) place votre bloc de code dans la file d’événements de Swing, 
    			//pour qu’il soit exécuté après que tous les événements en cours (clics, rafraîchissements, etc.) 
    			//soient traités, et toujours sur l’EDT.
    			SwingUtilities.invokeLater(() -> { // expression lambda pour instancier un runnable 
    		         new AjoutCompte().setVisible(true);
    			});
    		
    }
    
    
    private void handleEditUser() {
        int row = compteTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Sélectionnez d’abord un compte dans le tableau",
                "Aucun compte sélectionné",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Clé unique pour identifier le compte : l'email
        String mailOriginal = (String) tableModel.getValueAt(row, 2);

        // Récupération complète du compte en base
        final String[] nomHolder    = new String[1];
        final String[] prenomHolder = new String[1];
        final boolean[] eluHolder   = new boolean[1];
        final boolean[] actHolder   = new boolean[1];
        final byte[][] blobHolder   = new byte[1][];

        String selectSql = 
            "SELECT Nom, Prénom, Elu, Acteur_sport, Justificatif FROM compte WHERE Mail = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, mailOriginal);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        "Compte introuvable en base",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                nomHolder[0]    = rs.getString("Nom");
                prenomHolder[0] = rs.getString("Prénom");
                eluHolder[0]    = rs.getBoolean("Elu");
                actHolder[0]    = rs.getBoolean("Acteur_sport");
                blobHolder[0]   = rs.getBytes("Justificatif");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur SQL lors de la récupération du compte",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construction de la boîte de dialogue d’édition
        JDialog dialog = new JDialog(this, "Modifier le compte " + mailOriginal, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0,2,5,5));
        JTextField nomField    = new JTextField(nomHolder[0]);
        JTextField prenomField = new JTextField(prenomHolder[0]);
        JTextField mailField   = new JTextField(mailOriginal);
        JCheckBox eluCheck     = new JCheckBox("Élu", eluHolder[0]);
        JCheckBox actCheck     = new JCheckBox("Acteur sport", actHolder[0]);

        form.add(new JLabel("Nom :"));       form.add(nomField);
        form.add(new JLabel("Prénom :"));    form.add(prenomField);
        form.add(new JLabel("Mail :"));      form.add(mailField);
        form.add(new JLabel("Statut :"));    // panel vide
        JPanel statutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statutPanel.add(eluCheck);
        statutPanel.add(actCheck);
        form.add(new JLabel(""));            form.add(statutPanel);

        // Bouton pour changer le justificatif
        final byte[][] newBlobHolder = new byte[1][];
        JButton changeJustifBtn = new JButton("Changer justificatif (PDF)");
        changeJustifBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF", "pdf"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    newBlobHolder[0] = java.nio.file.Files.readAllBytes(chooser.getSelectedFile().toPath());
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                        "Impossible de lire le fichier sélectionné",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        form.add(new JLabel("Justificatif :")); form.add(changeJustifBtn);

        dialog.add(form, BorderLayout.CENTER);

        // Panel des boutons Enregistrer / Annuler
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn   = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");
        btns.add(cancelBtn);
        btns.add(saveBtn);
        dialog.add(btns, BorderLayout.SOUTH);

        // Action du bouton Annuler
        cancelBtn.addActionListener(e -> dialog.dispose());

        // Action du bouton Enregistrer
        saveBtn.addActionListener(e -> {
            String updateSql = 
                "UPDATE compte SET Nom = ?, Prénom = ?, Mail = ?, Elu = ?, Acteur_sport = ?, Justificatif = ? " +
                "WHERE Mail = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, nomField.getText().trim());
                ps.setString(2, prenomField.getText().trim());
                ps.setString(3, mailField.getText().trim());
                ps.setBoolean(4, eluCheck.isSelected());
                ps.setBoolean(5, actCheck.isSelected());
                // si l'utilisateur a choisi un nouveau PDF, on l'utilise, sinon on remet l'ancien blob
                ps.setBytes(6, newBlobHolder[0] != null ? newBlobHolder[0] : blobHolder[0]);
                ps.setString(7, mailOriginal);
                int updated = ps.executeUpdate();
                if (updated == 1) {
                    loadCompteData();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Compte mis à jour avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Aucune ligne modifiée en base",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur SQL lors de la mise à jour",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    private void handleExitUser() {
    	int row = compteTable.getSelectedRow();
    	
    	if(row < 0) {
    		JOptionPane.showMessageDialog(this,
    				"veuillez sélectionner un compte",
    				"Erreur de selection",
    				JOptionPane.ERROR_MESSAGE);
    		return; 
    	}
    	String email = (String) compteTable.getValueAt(row, 2);
    	String delSql = "DELETE FROM compte WHERE Mail = ?";
    	
    	try(Connection conn = getConnection();
    		PreparedStatement ps = conn.prepareStatement(delSql)){
    		ps.setString(1, email);
    		ps.executeUpdate();
    	}catch(SQLException ex) {
    		ex.printStackTrace();
    		JOptionPane.showMessageDialog(this,
    				"Erreur lors de la suppression en table",
    				"Erreur de Suppression",
    				JOptionPane.ERROR_MESSAGE);
    	}
    	loadCompteData();
    	JOptionPane.showMessageDialog(this,
    			"compte supprimé avec succés ",
    			"suppresion réussi",
    			JOptionPane.INFORMATION_MESSAGE);
    	
    
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                       "abcdefghijklmnopqrstuvwxyz" +
                       "0123456789";                          //  Tous les caractères possibles
        SecureRandom rnd = new SecureRandom();                 // Moteur de hasard sécurisé
        StringBuilder sb = new StringBuilder(length);          // Pour construire le mot de passe
        for (int i = 0; i < length; i++) {                     // On fait length boucles
            int idx = rnd.nextInt(chars.length());             // Choisit un index aléatoire
            sb.append(chars.charAt(idx));                      // Ajoute le caractère correspondant
        }
        return sb.toString();                                  //  Retourne le mot de passe complet
    }
    
  //Etablir la connexion avec MySQL
  	private Connection getConnection()throws SQLException{
  		return DriverManager.getConnection("jdbc:mysql://" + host + "/" + nomBase + "?characterEncoding=UTF-8",
  				login, motDePasse);
  	}

    /**
     * Crée le panneau affichant l'historique des connexions
     */
    private JPanel createHistoriquePanel() {
    	 JPanel p = new JPanel(new BorderLayout());                // panneau principal en BorderLayout
         JLabel header = new JLabel(                               
             "Historique des connexions",                          // texte du titre
             SwingConstants.CENTER                                 // centré horizontalement
         );
         p.add(header, BorderLayout.NORTH);                         // ajoute le titre en haut

         logsTextArea = new JTextArea();                            // instancie la zone de texte
         logsTextArea.setEditable(false);                           // rend la zone non éditable
         JScrollPane scroll = new JScrollPane(logsTextArea);        // ajoute un scroll automatique
         p.add(scroll, BorderLayout.CENTER);                        // place le scroll au centre

         // Lorsque ce panneau devient visible, on charge les logs
         p.addComponentListener(new ComponentAdapter() {
             @Override
             public void componentShown(ComponentEvent e) {
                 loadLogs();                                        // appel à la méthode de lecture
             }
         });

         return p; 
    }
    
    /**
     * Lit le fichier de logs et affiche chaque ligne dans logsTextArea
     */
    private void loadLogs() {
        Path logPath = Paths.get(LOG_FILE_PATH);                   // construit le chemin vers le fichier
        if (!Files.exists(logPath)) {                              // vérifie l’existence du fichier
            logsTextArea.setText(
                "Fichier de logs non trouvé : " + LOG_FILE_PATH   // message si introuvable
            );
            return;                                                // sort de la méthode
        }

        try {
            // Lecture de toutes les lignes avec l’encodage UTF-8
            java.util.List<String> lines = Files.readAllLines(
                logPath, StandardCharsets.UTF_8
            );
            // Affiche tout le contenu, séparé par des sauts de ligne
            logsTextArea.setText(String.join("\n", lines));
            logsTextArea.setCaretPosition(0);                      // replace le curseur en début
        } catch (IOException ex) {                                  // capture les erreurs d’I/O
            ex.printStackTrace();                                  // logs en console pour debug
            logsTextArea.setText(
                "Erreur lecture logs : " + ex.getMessage()        // message d’erreur dans l’UI
            );
        }
    }

    /**
     * Crée le panneau pour la recherche de clubs
     */
    private JPanel createRecherchePanel() {                 
        JPanel panel = new JPanel(new BorderLayout(5,5));  
        //  Panel principal en BorderLayout pour avoir :  
        //    - inputs en haut (NORTH)  
        //    - tableau au centre (CENTER)

        // --  On crée d’abord un sous-panel pour les champs de recherche --
        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        //    FlowLayout à gauche, 10px de marge horizontale et verticale

        /*inputs.add(new JLabel("Nom du club :"));            
        // 10) Label “Nom du club :”
        clubNameField = new JTextField(10);                  
        // 11) Champ texte sur 10 colonnes pour taper le nom
        inputs.add(clubNameField); */                         

        inputs.add(new JLabel("Commune :"));                 
        //  Label “Commune :”
        communeField = new JTextField(10);                   
        // Champ texte pour la commune
        inputs.add(communeField);                           

        inputs.add(new JLabel("Département :"));              
        //  Label “Département :”
        deptField = new JTextField(5);                       
        //  Champ plus court pour le code département
        inputs.add(deptField);                              

        inputs.add(new JLabel("Région :"));                   
        //  Label “Région :”
        regionField = new JTextField(10);                    
        //  Champ texte pour la région
        inputs.add(regionField); 
        
       inputs.add(new JLabel("Fédération :"));
       //label Fédération
       federationField = new JTextField(10);
       //champ texte pour la federation 
       inputs.add(federationField);

        // --  Bouton “Rechercher” --
        searchButton = new JButton("Rechercher");            
        //  Création du bouton
        searchButton.addActionListener(e -> handleSearchClubs());  
        //  Quand on clique, on appelle handleSearchClubs()
        inputs.add(searchButton);                            

        panel.add(inputs, BorderLayout.NORTH);  
        //  On place tous ces champs en haut du panel principal

        // --  Maintenant on crée le tableau de résultats --
        String[] cols = {
            "ID", "Code postale", "Commune", "Code QPV", "Nom QPV",
            "Département", "Région", "Statut géo",
            "Code fédération", "Fédération",
            "Nb clubs", "EPA", "Total"
        };
        //  Liste des entêtes de colonnes (exactement comme en base)

        searchTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;  
                // Interdit à l’utilisateur de modifier directement les cellules
            }
        };

        searchTable = new JTable(searchTableModel);  
        //  On lie le modèle au JTable pour afficher les données
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        //  On met le tableau dans un JScrollPane (barres de défilement auto)

        return panel;  
        //  Retourne l’onglet complet pour l’ajouter à centerPanel
    }

    private void handleSearchClubs() {
        // -- Vider l’ancien contenu du tableau --
        searchTableModel.setRowCount(0);

        // --  Construire dynamiquement la requête SQL --
        StringBuilder sql = new StringBuilder(
            "SELECT id, Code_postale, Commune, Code_QPV, Nom_QPV, Département," +
            " Région, Statu_géo, Code_fédération, Fédération, Nb_cluds, EPA, Total" +
            " FROM clubs WHERE 1=1"
        );
        List<String> params = new ArrayList<>();
        //    Le “WHERE 1=1” permet d’enchainer proprement plusieurs AND sans gestion spéciale

        // -- 30) Si le champ Nom du club n’est pas vide, on ajoute une condition --
       /* if (!clubNameField.getText().trim().isEmpty()) {
            sql.append(" AND Fédération LIKE ?");
            params.add("%" + clubNameField.getText().trim() + "%");
            //    On utilise LIKE + %...% pour pouvoir chercher n’importe où dans le texte
        }*/

        // --  Même logique pour la Commune --
        if (!communeField.getText().trim().isEmpty()) {
            sql.append(" AND Commune LIKE ?");
            params.add("%" + communeField.getText().trim() + "%");
        }

        // --  Et pour le Département --
        if (!deptField.getText().trim().isEmpty()) {
            sql.append(" AND Département LIKE ?");
            params.add("%" + deptField.getText().trim() + "%");
        }

        // --  Et pour la Région --
        if (!regionField.getText().trim().isEmpty()) {
            sql.append(" AND Région LIKE ?");
            params.add("%" + regionField.getText().trim() + "%");
        }
        
        // pour la fédération 
        if (!federationField.getText().trim().isEmpty()) {
            sql.append(" AND `Fédération` LIKE ?");
            params.add("%" + federationField.getText().trim() + "%");
        }


        // --  Préparer et exécuter la requête sécurisée --
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            //  Binder chaque paramètre dans l’ordre
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            //  Exécuter la requête et récupérer le résultat
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Pour chaque ligne, on lit les 13 colonnes
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("Code_postale"),
                        rs.getString("Commune"),
                        rs.getString("Code_QPV"),
                        rs.getString("Nom_QPV"),
                        rs.getString("Département"),
                        rs.getString("Région"),
                        rs.getString("Statu_géo"),
                        rs.getString("Code_fédération"),
                        rs.getString("Fédération"),
                        rs.getInt("Nb_cluds"),
                        rs.getString("EPA"),
                        rs.getInt("Total")
                    };
                    // 38) On ajoute cette ligne dans le modèle du tableau
                    searchTableModel.addRow(row);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();  
            //  En cas d’erreur SQL, on loggue la stack trace dans la console
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche des clubs",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            // Et on prévient l'utilisateur via une boîte d'alerte
        }
    }


    /**
     * Crée le panneau pour la demande d'ajout de club
     */
    private JPanel createDemandePanel() {
        //  Panel principal en BorderLayout
        JPanel panel = new JPanel(new BorderLayout(5,5));

        //  Définition des colonnes
        String[] cols = {
            "ID", "Nom", "Prénom", "Mail",
            "Elus", "Acteur_sport", "Justificatif"
        };

        //  Modèle en lecture seule
        demandeTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        //  JTable + JScrollPane
        demandeTable = new JTable(demandeTableModel);
        panel.add(new JScrollPane(demandeTable), BorderLayout.CENTER);

        //  Chargement initial des données
        loadDemandesData();

        //  Sous-panel pour le bouton
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton voirBtn = new JButton("Voir justificatif");
        south.add(voirBtn);
        panel.add(south, BorderLayout.SOUTH);

        // Listener du bouton (déplacé en Étape 4)
        voirBtn.addActionListener(e -> { 
            int row = demandeTable.getSelectedRow();  
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                    "Sélectionnez d'abord une demande dans le tableau.",
                    "Aucune demande sélectionnée",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            // On récupère l’ID (colonne 0)
            int id = (Integer) demandeTableModel.getValueAt(row, 0);
            // On appelle la methode pour visualiser le fichier pdf et afficher la boite de dialoque 
            showJustificatifDialog(id);
        });

        return panel;
    }
    private void loadDemandesData() {
        //  Vide d’abord le modèle
        demandeTableModel.setRowCount(0);

        //  Requête SQL
        String sql = "SELECT id, Nom, Prénom, Mail, Elus, Acteur_sport, Justificatif "
                   + "FROM demande";

        //  Exécution et boucle sur le ResultSet
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("Nom"),
                    rs.getString("Prénom"),
                    rs.getString("Mail"),
                    rs.getBoolean("Elus"),
                    rs.getBoolean("Acteur_sport"),
                    rs.getBytes("Justificatif")
                };
                demandeTableModel.addRow(row);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des demandes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    // cette fonction est faite en plusieurs etape 

    private void showJustificatifDialog(int demandeId) {
        // etape 1 :  Holders pour capturer hors du ResultSet
    	//stocke des valeurs recupérer en base tout en permettant leur accés 
    	//dans des fonctions anonyme 
    	//astuce JAVA pour contourner le fait que des variables doivent être final 
    	//on met des tableau de taille 1 pour modifier 
    	//le contenu sans changer la référence du tableau lui même
        final byte[][]   blobHolder   = new byte[1][];
        final String[]   nomHolder    = new String[1];
        final String[]   prenomHolder = new String[1];
        final String[]   mailHolder   = new String[1];
        final String[]   password     = new String[1];
        final boolean[]  eluHolder    = new boolean[1];
        final boolean[]  acteurHolder = new boolean[1];

        // etape 2 :  Récupération en base
        // on écrit la requête qui va recupérer exactement la ligne de la table demande
        //corresppondant à l'id 
        String sql = "SELECT Justificatif, Nom, Prénom, Mail, Elus, Acteur_sport, Mdp "
                   + "FROM demande WHERE id = ?";
        
        //on ouvre la connexion mySQL
        //On prépare un preparedStatement pour eviter toute injection SQL 
        //On bind l'id de la demande dans le ? de la requête 
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, demandeId);
            
            //on lance la requête 
            //rs.next place le curceur sur la première ligne, si elle n'existe pas  
            //on affiche un JOptionPane d'erreur et on quitte la méthode 
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this,
                        "Demande introuvable",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                //on lit chacune des colonnes de la ligne et on les stocke dans nors holder 
                blobHolder[0]    = rs.getBytes("Justificatif");
                
                nomHolder[0]    = rs.getString("Nom");
                prenomHolder[0] = rs.getString("Prénom");
                mailHolder[0]   = rs.getString("Mail");
                eluHolder[0]    = rs.getBoolean("Elus");
                acteurHolder[0] = rs.getBoolean("Acteur_sport");
                password[0]     = rs.getString("Mdp");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur SQL lors de la récupération du justificatif",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // etape 3 : Écrire le blob dans un fichier temporaire
        //creation d'un fichier temporaire sur le disque suffixé en .pdf 
        //on ouvre un FileOutputStream  pour écrire les octets du blob dans ce fichier 
        //deleteOnExit garantit que le fichier sera nettoyé à la fermeture de la JVM 
        //En cas d'erreur d'E/S on l'affiche et on quitte 
        
      //declaration d'une variable tempfile qui ne référence encore aucun fichier 
        java.io.File tempFile; 
        try {
        	//crée physiquement un nouveau fichier vide dans le repertoire temporaire du système 
            tempFile = java.io.File.createTempFile("justif_" + demandeId + "_", ".pdf");
            //new FileOutputStream(tempFile) ouvre un flux binaire pour écrire dans le fichier temporaire
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
            	//copie tous les octet de blobHolder dans le fichier sur le disque en ecrasant tout contenu antérieure
                fos.write(blobHolder[0]);
            }
            //Cette méthode marque le fichier pour être automatiquement supprimé 
            //lors de la fermeture de la machine virtuelle Java
            tempFile.deleteOnExit();
        } catch (java.io.IOException ioex) {
            ioex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur écriture fichier temporaire",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // etape 4 :  Ouvre le fichier externe
        //si le desktop est supporté on demande au système d'ouvrir le fichier
        //avec le programme par défaut  en cas d'echec on prévient 
        
        /**
         * Desktop est une API Java qui permet d’interagir avec l’environnement de bureau
         *  (lancer des applications externes, ouvrir des fichiers, etc.)
         *  
         *  isDesktopSupported() renvoie true 
         *  si la plateforme (Windows, macOS, Linux) prend en charge cette API
         *  
         * Si ce n’est pas supporté, on n’essaie pas d’ouvrir le fichier pour éviter une exception 
         */
        if (Desktop.isDesktopSupported()) {
            try {
            	//tentative d'ouverture du fichier 
                Desktop.getDesktop().open(tempFile);
            } catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Impossible d’ouvrir le justificatif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        // etape 5 :  Création de la fenêtre modale
        //on crée un JDialog modal (bloque l'accés à la femêtre principal tant qu'il est  ouvert 
        //on choisit un BorderLayout; cntré par rapport à la fenêtre parente de taille fixe 
        //on ajoute un JLabel central avec un message
        JDialog dialog = new JDialog(this, "Traitement de la demande", true);
        dialog.setLayout(new BorderLayout(10,10));
        dialog.setSize(400,150);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JLabel(
            "Accepter ou refuser la demande n°" + demandeId,
            SwingConstants.CENTER
        ), BorderLayout.CENTER);

        // etape 6 :  Panneau des boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refuseBtn = new JButton("Refuser");
        JButton acceptBtn = new JButton("Accepter");
        btnPanel.add(refuseBtn);
        btnPanel.add(acceptBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        // etape 7: ajout d'un action listener au bouton refuserBtn 
        refuseBtn.addActionListener(e -> {
            String delSql = "DELETE FROM demande WHERE id = ?";
            try (Connection c = getConnection();
                 PreparedStatement ps = c.prepareStatement(delSql)) {
                ps.setInt(1, demandeId);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la suppression de la demande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadDemandesData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Demande refusée et supprimée.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        });

        //etape 8 : ajout d'un action listener au bouton acceptBtn 
        acceptBtn.addActionListener(e -> {
            String nom    = nomHolder[0];
            String prenom = prenomHolder[0];
            String mail   = mailHolder[0];
            boolean elu   = eluHolder[0];
            boolean act   = acteurHolder[0];
            byte[] justif = blobHolder[0];
            String newPassword = password[0];
            //  INSERT dans compte
            String insSql = 
              "INSERT INTO compte(Nom, Prénom, Mail, Mot_passe, Elu, Acteur_sport, Justificatif) " +
              "VALUES(?, ?, ?, ?, ?, ?, ?)";
            try (Connection c = getConnection();
                 PreparedStatement ps = c.prepareStatement(insSql)) {
                ps.setString(1, nom);
                ps.setString(2, prenom);
                ps.setString(3, mail);
                ps.setString(4, newPassword);
                ps.setBoolean(5, elu);
                ps.setBoolean(6, act);
                ps.setBytes(7, justif);
                ps.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l’insertion dans compte",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            //  DELETE de la demande
            String delSql = "DELETE FROM demande WHERE id = ?";
            try (Connection c2 = getConnection();
                 PreparedStatement ps2 = c2.prepareStatement(delSql)) {
                ps2.setInt(1, demandeId);
                ps2.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la suppression de la demande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            //  Rafraîchissement + fermeture
            loadDemandesData();
            loadCompteData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Demande acceptée et compte créé.",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        });


        dialog.setVisible(true);
    } 

    

    /**
     * Point d'entrée pour lancer cette fenêtre indépendamment
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {      // Assure que l'UI est créée sur l'EDT (Event Dispatch Thread)
            AccueilFrame frame = new AccueilFrame(); // Instancie la fenêtre principale
            frame.setVisible(true);               // Rendre la fenêtre visible à l'écran
        });
    }
    

}

