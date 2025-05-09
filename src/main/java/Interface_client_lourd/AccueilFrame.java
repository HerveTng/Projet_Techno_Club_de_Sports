package Interface_client_lourd;
 

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
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
import java.io.*;  // pour File, FileOutputStream, IOException, etc.


import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

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
    
 // ==== Composants et modèle pour l’onglet Recherche de clubs ====  
    private JTextField clubNameField;        // 1) Champ où l’utilisateur tape le nom du club  
    private JTextField communeField;         // 2) Champ pour la commune  
    private JTextField deptField;            // 3) Champ pour le département  
    private JTextField regionField;          // 4) Champ pour la région  
    private JButton    searchButton;         // 5) Bouton pour lancer la recherche  

    private JTable               searchTable;      // 6) Tableau Swing pour afficher le résultat  
    private DefaultTableModel    searchTableModel; // 7) Modèle de données du tableau  

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
        JPanel panel = new JPanel(new BorderLayout(5,5));  
        // 8) Panel principal en BorderLayout pour avoir :  
        //    - inputs en haut (NORTH)  
        //    - tableau au centre (CENTER)

        // -- 9) On crée d’abord un sous-panel pour les champs de recherche --
        JPanel inputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        //    FlowLayout à gauche, 10px de marge horizontale et verticale

        /*inputs.add(new JLabel("Nom du club :"));            
        // 10) Label “Nom du club :”
        clubNameField = new JTextField(10);                  
        // 11) Champ texte sur 10 colonnes pour taper le nom
        inputs.add(clubNameField); */                         

        inputs.add(new JLabel("Commune :"));                 
        // 12) Label “Commune :”
        communeField = new JTextField(10);                   
        // 13) Champ texte pour la commune
        inputs.add(communeField);                           

        inputs.add(new JLabel("Département :"));              
        // 14) Label “Département :”
        deptField = new JTextField(5);                       
        // 15) Champ plus court pour le code département
        inputs.add(deptField);                              

        inputs.add(new JLabel("Région :"));                   
        // 16) Label “Région :”
        regionField = new JTextField(10);                    
        // 17) Champ texte pour la région
        inputs.add(regionField);                            

        // -- 18) Bouton “Rechercher” --
        searchButton = new JButton("Rechercher");            
        // 19) Création du bouton
        searchButton.addActionListener(e -> handleSearchClubs());  
        // 20) Quand on clique, on appelle handleSearchClubs()
        inputs.add(searchButton);                            

        panel.add(inputs, BorderLayout.NORTH);  
        // 21) On place tous ces champs en haut du panel principal

        // -- 22) Maintenant on crée le tableau de résultats --
        String[] cols = {
            "ID", "Code postale", "Commune", "Code QPV", "Nom QPV",
            "Département", "Région", "Statut géo",
            "Code fédération", "Fédération",
            "Nb clubs", "EPA", "Total"
        };
        // 23) Liste des entêtes de colonnes (exactement comme en base)

        searchTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;  
                // 24) Interdit à l’utilisateur de modifier directement les cellules
            }
        };

        searchTable = new JTable(searchTableModel);  
        // 25) On lie le modèle au JTable pour afficher les données
        panel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        // 26) On met le tableau dans un JScrollPane (barres de défilement auto)

        return panel;  
        // 27) Retourne l’onglet complet pour l’ajouter à centerPanel
    }

    private void handleSearchClubs() {
        // -- 28) Vider l’ancien contenu du tableau --
        searchTableModel.setRowCount(0);

        // -- 29) Construire dynamiquement la requête SQL --
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

        // -- 31) Même logique pour la Commune --
        if (!communeField.getText().trim().isEmpty()) {
            sql.append(" AND Commune LIKE ?");
            params.add("%" + communeField.getText().trim() + "%");
        }

        // -- 32) Et pour le Département --
        if (!deptField.getText().trim().isEmpty()) {
            sql.append(" AND Département LIKE ?");
            params.add("%" + deptField.getText().trim() + "%");
        }

        // -- 33) Et pour la Région --
        if (!regionField.getText().trim().isEmpty()) {
            sql.append(" AND Région LIKE ?");
            params.add("%" + regionField.getText().trim() + "%");
        }

        // -- 34) Préparer et exécuter la requête sécurisée --
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            // 35) Binder chaque paramètre dans l’ordre
            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i + 1, params.get(i));
            }

            // 36) Exécuter la requête et récupérer le résultat
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // 37) Pour chaque ligne, on lit les 13 colonnes
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
            // 39) En cas d’erreur SQL, on loggue la stack trace dans la console
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la recherche des clubs",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            // 40) Et on prévient l'utilisateur via une boîte d'alerte
        }
    }


    /**
     * Crée le panneau pour la demande d'ajout de club
     */
    private JPanel createDemandePanel() {
    	JPanel panel = new JPanel(new BorderLayout(5,5));   
    	//Définition des colonnes du tableau 
    	String[] cols = { "Nom", "Prénom", "Mail", "Mot de passe", "Élus", "Acteur Sport", "Justificatif","Action" };
    	
    	//model de tableau + interdiction d'editer les cellules 
    	demandeTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 7 ;
            }
        };
     // Création du JTable à partir du modèle
        demandeTable = new JTable(demandeTableModel);
     // Ajout dans un JScrollPane pour avoir des barres de défilement
        panel.add(new JScrollPane(demandeTable), BorderLayout.CENTER);
        
     // 1) Récupère la 8ᵉ colonne (index 7) qui correspond à “Action”
        TableColumn actionColumn = demandeTable.getColumnModel().getColumn(7);
        // 2) Assigne à cette colonne un renderer qui dessine un JButton
        actionColumn.setCellRenderer(new ButtonRenderer());
        // 3) Assigne un editor qui gère le clic sur ce JButton
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
 
       loadDemandesData();
     
     return panel;
    }
    
    /**
     * 2) Charge les données depuis la table 'demande' en BDD
     *    et remplit le modèle du tableau des demandes
     */
    private void loadDemandesData() {
        // 1) On vide d’abord toutes les lignes présentes dans le modèle
        demandeTableModel.setRowCount(0);

        // 2) On prépare la requête SQL pour récupérer toutes les colonnes utiles
        String sql = "SELECT id, Nom, Prénom, Mail, Elus, Acteur_sport, Justificatif FROM demande";

        // 3) Try-with-resources : ouvre la connexion, le statement et le resultSet
        try (Connection conn = getConnection();                      // 3a) Ouvre la connexion à MySQL
             Statement stmt = conn.createStatement();                // 3b) Crée un Statement pour exécuter du SQL simple
             ResultSet rs = stmt.executeQuery(sql)) {                // 3c) Exécute la requête et récupère les résultats

            // 4) Tant qu’il y a des lignes dans le ResultSet…
            while (rs.next()) {
                // 5) On lit chaque colonne en utilisant le nom exact de la colonne SQL
                int    id            = rs.getInt("id");               // 5a) Colonne id (clé primaire)
                String nom           = rs.getString("Nom");           // 5b) Colonne Nom
                String prenom        = rs.getString("Prénom");        // 5c) Colonne Prénom
                String mail          = rs.getString("Mail");          // 5d) Colonne Mail
                boolean elu          = rs.getBoolean("Elus");          // 5e) Colonne Elu (true/false)
                boolean acteurSport  = rs.getBoolean("Acteur_sport"); // 5f) Colonne Acteur_sport (true/false)
                byte[] justificatif  = rs.getBytes("Justificatif");   // 5g) Colonne Justificatif (blob binaire)

                // 6) On place ces valeurs dans un tableau d’Object pour la ligne
                Object[] row = {
                    id,
                    nom,
                    prenom,
                    mail,
                    elu,
                    acteurSport,
                    justificatif    // on stocke le blob pour l'afficher ou l'ouvrir plus tard
                };

                // 7) On ajoute cette ligne au modèle, ce qui met à jour automatiquement le JTable
                demandeTableModel.addRow(row);
            }

        } catch (SQLException ex) {
            // 8) Si une erreur survient, on imprime la stack trace pour debug
            ex.printStackTrace();

            // 9) Et on affiche un message d’erreur à l’utilisateur
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des demandes",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
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
    /** 4) Renderer : dessine un vrai bouton dans chaque cellule “Action” */
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);                // 4a) Assure que le fond est peint
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Voir justificatif");   // 4b) Le texte du bouton
            return this;                    // 4c) On renvoie ce JButton pour l’affichage
        }
    }

    /** 5) Editor : gère le clic sur le bouton “Voir justificatif” */
    private class ButtonEditor extends DefaultCellEditor {
        private final JButton btn;         // 5a) Le bouton qu’on va afficher
        private int currentRow;            // 5b) La ligne sur laquelle on a cliqué

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);              // 5c) Parent a besoin d’un JCheckBox, on l’ignore
            btn = new JButton();          // 5d) On crée le vrai JButton
            btn.setOpaque(true);          // 5e) Fond opaque pour le rendu
            // 5f) Quand on clique sur ce bouton :
            btn.addActionListener(e -> {
                // 5g) On récupère l’ID de la demande (colonne 0) sur la ligne courante
                int id = (Integer) demandeTableModel.getValueAt(currentRow, 0);
                // 5h) On appelle la popup pour afficher le justificatif
                showJustificatifDialog(id);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;             // 5i) On mémorise la ligne sélectionnée
            btn.setText("Voir justificatif"); // 5j) On fixe le texte du bouton
            return btn;                   // 5k) On renvoie le bouton comme éditeur
        }

        @Override
        public Object getCellEditorValue() {
            return "";                    // 5l) Valeur renvoyée (inutile ici)
        }
    }
    /**
     * Affiche le justificatif d’une demande dans une fenêtre modale
     * et propose d’« Accepter » ou de « Refuser » la demande.
     */
    private void showJustificatifDialog(int demandeId) {
        // 1) Variables pour capturer les données hors de la portée de ResultSet
        final byte[][]   blobHolder   = new byte[1][];   // pour stocker le blob PDF/Word
        final String[]   nomHolder    = new String[1];   // pour stocker le nom
        final String[]   prenomHolder = new String[1];   // pour stocker le prénom
        final String[]   mailHolder   = new String[1];   // pour stocker le mail
        final boolean[]  eluHolder    = new boolean[1];  // pour stocker le flag Elus
        final boolean[]  acteurHolder = new boolean[1];  // pour stocker le flag Acteur_sport

        // 2) Prépare la requête SQL pour récupérer le justificatif et les infos
        String sql = 
            "SELECT Justificatif, Nom, Prénom, Mail, Elus, Acteur_sport " +
            "FROM demande WHERE id = ?";

        // 3) Exécute la requête et remplit les holders
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, demandeId);                    // 3a) on bind l’ID de la demande
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {                          // 3b) si on a trouvé la demande
                    blobHolder[0]   = rs.getBytes("Justificatif");     // 3c) récupère le blob
                    nomHolder[0]    = rs.getString("Nom");            // 3d) récupère le nom
                    prenomHolder[0] = rs.getString("Prénom");         // 3e) récupère le prénom
                    mailHolder[0]   = rs.getString("Mail");           // 3f) récupère le mail
                    eluHolder[0]    = rs.getBoolean("Elus");          // 3g) récupère Elus
                    acteurHolder[0] = rs.getBoolean("Acteur_sport");  // 3h) récupère Acteur_sport
                } else {
                    // 3i) si pas de ligne, on prévient et on quitte
                    JOptionPane.showMessageDialog(this,
                        "Demande introuvable en base",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this,
                "Erreur SQL lors de la récupération du justificatif",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4) Écrit le blob dans un fichier temporaire pour l’ouvrir
        java.io.File tempFile;
        try {
            tempFile = java.io.File.createTempFile("justif_" + demandeId + "_", ".pdf");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile)) {
                fos.write(blobHolder[0]);               // 4a) copie les octets dans le fichier
            }
            tempFile.deleteOnExit();                   // 4b) supprime le fichier quand la JVM se termine
        } catch (java.io.IOException ioex) {
            ioex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l’écriture du fichier temporaire",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 5) Ouvre le fichier temporaire dans l’application par défaut
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(tempFile);    // 5a) lance le viewer externe
            } catch (IOException ioe) {
                ioe.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Impossible d’ouvrir le justificatif",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        // 6) Création du dialog modal pour accepter ou refuser
        JDialog dialog = new JDialog(this, "Traitement de la demande", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        // 7) Message explicatif centré
        JLabel msg = new JLabel(
            "Accepter ou refuser la demande n°" + demandeId,
            SwingConstants.CENTER
        );
        dialog.add(msg, BorderLayout.CENTER);

        // 8) Panneau pour les boutons en bas à droite
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refuseBtn = new JButton("Refuser");
        JButton acceptBtn = new JButton("Accepter");
        btnPanel.add(refuseBtn);
        btnPanel.add(acceptBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        // 9) Listener pour le bouton “Accepter”
        acceptBtn.addActionListener(e -> {
            // 9a) Récupère les données stockées dans les holders
            String nom       = nomHolder[0];
            String prenom    = prenomHolder[0];
            String mail      = mailHolder[0];
            boolean elu      = eluHolder[0];
            boolean acteur   = acteurHolder[0];
            byte[] justif    = blobHolder[0];

            // 9b) Prépare l’INSERT dans la table compte
            String insertSql = 
                "INSERT INTO compte(Nom, Prénom, Mail, Elu, Acteur_sport, Justificatif) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
            try (Connection conn2 = getConnection();
                 PreparedStatement ins = conn2.prepareStatement(insertSql)) {
                ins.setString(1, nom);                  // 9c) nom
                ins.setString(2, prenom);               // 9d) prénom
                ins.setString(3, mail);                 // 9e) mail
                ins.setBoolean(4, elu);                 // 9f) elu
                ins.setBoolean(5, acteur);              // 9g) acteur_sport
                ins.setBytes(6, justif);                // 9h) blob du justificatif
                ins.executeUpdate();                    // 9i) exécute l’insertion
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l’insertion dans compte",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 9j) Supprime la demande désormais traitée
            String delSql = "DELETE FROM demande WHERE id = ?";
            try (Connection conn3 = getConnection();
                 PreparedStatement del = conn3.prepareStatement(delSql)) {
                del.setInt(1, demandeId);             // bind id
                del.executeUpdate();                  // exécute la suppression
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la suppression de la demande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 9k) Rafraîchit la liste des demandes et ferme la fenêtre
            loadDemandesData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Demande acceptée et compte créé.",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        });

        // 10) Listener pour le bouton “Refuser”
        refuseBtn.addActionListener(e -> {
            // 10a) Supprime la demande directement
            String delSql = "DELETE FROM demande WHERE id = ?";
            try (Connection conn4 = getConnection();
                 PreparedStatement del = conn4.prepareStatement(delSql)) {
                del.setInt(1, demandeId);
                del.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la suppression de la demande",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 10b) Rafraîchit la liste et ferme
            loadDemandesData();
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                "Demande refusée et supprimée.",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
        });

        // 11) Affiche le dialog de manière bloquante
        dialog.setVisible(true);
    }

}

