package Interface_client_lourd;
 // Définit le namespace (package) pour organiser les classes en modules

import javax.swing.*;                       // Importe les composants Swing (JFrame, JPanel, JButton, etc.)
import java.awt.*;                          // Importe les gestionnaires de positionnement AWT (BorderLayout, CardLayout, etc.)
import java.awt.event.ActionEvent; // Importe la classe pour gérer les événements d'action (clics de bouton)
import javax.swing.table.DefaultTableModel;
import java.security.SecureRandom;
import java.sql.*;
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

    // -------- Attributs Swing --------
    private final CardLayout cardLayout = new CardLayout();                       // Layout permettant de basculer entre plusieurs panneaux
    private final JPanel centerPanel = new JPanel(cardLayout);                    // Panneau central qui contient les différentes cartes

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
     * 20) Construction de l’onglet “Connexion et mot de passe” :  
     *     - Un tableau pour afficher tous les comptes  
     *     - Un bouton pour réinitialiser le mot de passe  
     */
    private JPanel createConnexionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));  // 21) Panel principal en BorderLayout

        // 22) Définition des colonnes du tableau
        String[] cols = { "Nom", "Prénom", "Mail", "Mot de passe", "Élu", "Acteur Sport" };
        // 23) Modèle de tableau + interdiction d’éditer les cellules
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // 24) Aucune cellule ne pourra être modifiée à la main
            }
        };

        // 25) Création du JTable à partir du modèle
        compteTable = new JTable(tableModel);
        // 26) Ajout dans un JScrollPane pour avoir des barres de défilement
        panel.add(new JScrollPane(compteTable), BorderLayout.CENTER);

        loadCompteData(); // 27) J’interroge la BDD pour remplir les lignes du tableau

        // 28) Je crée le bouton de réinitialisation
        resetButton = new JButton("Réinitialiser mot de passe");
        // 29) Quand on clique dessus, on appelle handleResetPassword()
        resetButton.addActionListener(e -> handleResetPassword());

        // 30) Je place le bouton dans un petit panneau en bas à droite
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(resetButton);
        panel.add(south, BorderLayout.SOUTH);

        return panel; // 31) Je renvoie l’onglet complet
    }
    
    /**  
     * 32) Charge les données depuis la table 'compte' en BDD  
     *     et remplit le tableModel  
     */
    private void loadCompteData() {
        tableModel.setRowCount(0); // 33) Vide d’abord toutes les lignes existantes

        String sql = "SELECT Nom, Prénom, Mail, Mot_passe, Elu, Acteur_sport FROM compte";
        try (Connection conn = getConnection();                     // 34) Ouvre la connexion
             Statement stmt = conn.createStatement();               // 35) Crée un statement simple
             ResultSet rs = stmt.executeQuery(sql)) {               // 36) Exécute la requête
            while (rs.next()) {                                     // 37) Tant qu’il y a une ligne…
                Object[] row = {                                  // 38) Je récupère chaque colonne
                    rs.getString("Nom"),
                    rs.getString("Prénom"),
                    rs.getString("Mail"),
                    rs.getString("Mot_passe"),
                    rs.getBoolean("Elu"),
                    rs.getBoolean("Acteur_sport")
                };
                tableModel.addRow(row);                            // 39) J’ajoute la ligne au modèle
            }
        } catch (SQLException ex) {                                 // 40) Si ça plante, j’affiche l’erreur
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des comptes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**  
     * 41) Réinitialise le mot de passe du compte sélectionné  
     *     et met à jour la BDD et le tableau à l’écran  
     */
    private void handleResetPassword() {
        int row = compteTable.getSelectedRow();                    // 42) Récupère l’indice de la ligne sélectionnée
        if (row < 0) {                                             // 43) Si rien n’est sélectionné…
            JOptionPane.showMessageDialog(this,
                "Sélectionnez d’abord un compte dans le tableau",
                "Aucun compte sélectionné",
                JOptionPane.WARNING_MESSAGE);
            return;                                                // 44) On arrête là
        }

        // 45) On prend le mail (clé unique) pour savoir quel compte mettre à jour
        String mail = (String) tableModel.getValueAt(row, 2);

        // 46) Génère un nouveau mot de passe aléatoire de 8 caractères
        String newPwd = generateRandomPassword(8);
        // 47) On le hache avec BCrypt pour la sécurité
        String newHash = BCrypt.hashpw(newPwd, BCrypt.gensalt(12));

        // 48) Prépare la requête UPDATE pour changer le hash en BDD
        String sql = "UPDATE compte SET Mot_passe = ? WHERE Mail = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newHash);                          // 49) Place le nouveau hash
            stmt.setString(2, mail);                             // 50) Place le mail du compte
            int updated = stmt.executeUpdate();                  // 51) Exécute la mise à jour

            if (updated == 1) {                                  // 52) Si une ligne a bien été modifiée
                tableModel.setValueAt(newHash, row, 3);          // 53) Met à jour le hash dans le tableau
                // 54) Affiche la boîte de dialogue avec le nouveau mot de passe en clair
                JOptionPane.showMessageDialog(this,
                    "Le mot de passe de \"" + mail + "\" a été réinitialisé.\n"
                    + "Nouveau mot de passe : " + newPwd,
                    "Réinitialisation réussie",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // 55) Si rien n’a bougé, on informe l’utilisateur
                JOptionPane.showMessageDialog(this,
                    "Échec de la mise à jour en base.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {                                // 56) En cas d’erreur SQL
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur SQL durant la réinitialisation",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**  
     * 57) Génère une chaîne aléatoire de lettres et chiffres  
     *     pour servir de nouveau mot de passe  
     */
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                       "abcdefghijklmnopqrstuvwxyz" +
                       "0123456789";                          // 58) Tous les caractères possibles
        SecureRandom rnd = new SecureRandom();                 // 59) Moteur de hasard sécurisé
        StringBuilder sb = new StringBuilder(length);          // 60) Pour construire le mot de passe
        for (int i = 0; i < length; i++) {                     // 61) On fait length boucles
            int idx = rnd.nextInt(chars.length());             // 62) Choisit un index aléatoire
            sb.append(chars.charAt(idx));                      // 63) Ajoute le caractère correspondant
        }
        return sb.toString();                                  // 64) Retourne le mot de passe complet
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
        JPanel p = new JPanel(new BorderLayout());      // BorderLayout pour un header + zone défilante
        JLabel header = new JLabel("Historique des connexions", SwingConstants.CENTER); // Titre centré en haut
        p.add(header, BorderLayout.NORTH);               // Place le titre en haut

        JTextArea ta = new JTextArea("Ici vous verrez la liste des connexions..."); // Zone de texte
        ta.setEditable(false);                           // Rend la zone non modifiable
        JScrollPane scroll = new JScrollPane(ta);         // Ajoute un ascenseur si nécessaire
        p.add(scroll, BorderLayout.CENTER);               // Place la zone défilante au centre
        return p;                                        // Retourne le panneau historial
    }

    /**
     * Crée le panneau pour la recherche de clubs
     */
    private JPanel createRecherchePanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout avec marges horizontales et verticales
        p.add(new JLabel("Rechercher des clubs :"));                    // Label d'invite
        p.add(new JTextField(20));                                       // Champ texte sur 20 colonnes
        p.add(new JButton("Rechercher"));                              // Bouton de validation
        return p;                                                       // Retourne le panneau recherche
    }

    /**
     * Crée le panneau pour la demande d'ajout de club
     */
    private JPanel createDemandePanel() {
        JPanel p = new JPanel(new GridBagLayout());                    // GridBagLayout pour centrer le formulaire
        JLabel titre = new JLabel("Formulaire de demande d'ajout de club"); // Label explicatif
        p.add(titre);                                                   // Ajoute le label au centre
        return p;                                                      // Retourne le panneau demande
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

