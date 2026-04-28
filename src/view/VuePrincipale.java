package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.EtatPartie;
import model.ModeleJeu;
import model.ModeleJeu.TypeJoueur;
import util.Icones;

/**
 * Fenêtre principale du jeu.
 *
 * Contient :
 * - VueGrille (centre) : affichage de la gaufre
 * - Panneau d'informations (haut) : joueur courant, état
 * - Panneau de contrôle (bas) : boutons Nouvelle partie, Sauvegarder, Charger
 */
public class VuePrincipale extends JFrame {

    private final ModeleJeu modele;
    private final VueGrille vueGrille;

    // ─── Composants UI ────────────────────────────────────────────────────────
    private final JLabel labelStatut;
    private final JLabel labelTour;
    private final JLabel labelHistorique;
    private final JButton btnNouvelle;
    private final JButton btnSauvegarder;
    private final JButton btnCharger;
    private final JButton btnQuitter;
    private final JButton btnHistorique;


    // ─── Éléments du menu ────────────────────────────────────────────────────
    private final JMenuItem menuNouvelle;
    private final JMenuItem menuCharger;
    private final JMenuItem menuSauvegarder;
    private final JMenuItem menuQuitter;

    // ─── Couleurs thème ───────────────────────────────────────────────────────
    private static final Color FOND_HEADER  = new Color(60, 40, 10);
    private static final Color FOND_PANEL   = new Color(80, 55, 15);
    private static final Color TEXTE_CLAIR  = new Color(255, 230, 150);
    private static final Color FOND_FENETRE = new Color(45, 30, 5);

    public VuePrincipale(ModeleJeu modele, VueGrille vueGrille) {
        super("Gaufre Empoisonnée");
        setIconImage(Icones.toImageIcon(Icones.gaufre(32)).getImage());
        this.modele   = modele;
        this.vueGrille = vueGrille;

        // ── Labels ─────────────────────────────────────────────────────────────
        labelStatut    = creerLabel("", 16, Font.BOLD);
        labelTour      = creerLabel("", 14, Font.PLAIN);
        labelHistorique = creerLabel("", 12, Font.ITALIC);

        // ── Boutons (texte + icône SVG) ──────────────────────────────────────
        btnNouvelle   = creerBouton("Nouvelle partie",  Icones.nouveau(18));
        btnSauvegarder = creerBouton("Sauvegarder",     Icones.sauvegarder(18));
        btnCharger    = creerBouton("Charger",          Icones.dossier(18));
        btnQuitter    = creerBouton("Quitter",          Icones.sortie(18));
        btnHistorique = creerBouton("Annuler le coup", Icones.dossier(18));
        btnQuitter.setBackground(new Color(150, 40, 40));

        // ── Items de menu (avec icônes SVG) ─────────────────────────────────
        menuNouvelle   = creerMenuItem("Nouvelle partie",       Icones.nouveau(16));
        menuCharger    = creerMenuItem("Charger une partie...",  Icones.dossier(16));
        menuSauvegarder = creerMenuItem("Sauvegarder la partie...", Icones.sauvegarder(16));
        menuQuitter    = creerMenuItem("Quitter",               Icones.sortie(16));

        construireUI();
        actualiser();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 350));
    }

    // ─── Construction UI ──────────────────────────────────────────────────────

    private void construireUI() {
        JPanel root = new JPanel(new BorderLayout(5, 5));
        root.setBackground(FOND_FENETRE);
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        root.add(construireHeader(), BorderLayout.NORTH);
        root.add(vueGrille,          BorderLayout.CENTER);
        root.add(construirePanneauBas(), BorderLayout.SOUTH);

        setContentPane(root);
        setJMenuBar(construireMenuBar());
    }

    private JMenuBar construireMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ── Menu "Partie" ────────────────────────────────────────────────
        JMenu menuPartie = new JMenu("Partie");
        menuPartie.setMnemonic('P');

        menuNouvelle.setAccelerator(
            javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_N,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuSauvegarder.setAccelerator(
            javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuCharger.setAccelerator(
            javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuQuitter.setAccelerator(
            javax.swing.KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));

        menuPartie.add(menuNouvelle);
        menuPartie.addSeparator();
        menuPartie.add(menuSauvegarder);
        menuPartie.add(menuCharger);
        menuPartie.addSeparator();
        menuPartie.add(menuQuitter);

        menuBar.add(menuPartie);
        return menuBar;
    }

    private JPanel construireHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FOND_HEADER);
        panel.setBorder(new EmptyBorder(8, 12, 8, 12));

        labelStatut.setAlignmentX(CENTER_ALIGNMENT);
        labelTour.setAlignmentX(CENTER_ALIGNMENT);
        labelHistorique.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(labelStatut);
        panel.add(Box.createVerticalStrut(4));
        panel.add(labelTour);
        panel.add(Box.createVerticalStrut(2));
        panel.add(labelHistorique);
        return panel;
    }

    private JPanel construirePanneauBas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        panel.setBackground(FOND_PANEL);
        panel.add(btnNouvelle);
        panel.add(btnSauvegarder);
        panel.add(btnCharger);
        panel.add(btnQuitter);
        panel.add(btnHistorique);
        return panel;
    }

    // ─── Méthodes publiques ───────────────────────────────────────────────────

    /**
     * Met à jour tous les labels en fonction de l'état du modèle.
     */
    public void actualiser() {
        if (modele.getEtat() == EtatPartie.TERMINE) {
            labelStatut.setText("Joueur " + modele.getGagnant() + " a gagné !");
            labelStatut.setForeground(new Color(100, 255, 100));
            labelStatut.setIcon(Icones.toImageIcon(Icones.trophee(24)));
            labelStatut.setIconTextGap(8);
            labelTour.setText("La partie est terminée.");
        } else {
            int j = modele.getJoueurActuel();
            String typeStr = typeToString(modele.getTypeJoueur(j));
            labelStatut.setText("Tour du Joueur " + j + " (" + typeStr + ")");
            labelStatut.setForeground(TEXTE_CLAIR);
            labelStatut.setIcon(null);
            labelTour.setText(modele.getCoupsDisponibles().size() + " coup(s) disponible(s)");
        }

        // Dernier coup joué
        var hist = modele.getHistorique();
        if (!hist.isEmpty()) {
            var dernier = hist.get(hist.size() - 1);
            labelHistorique.setText("Dernier coup : " + dernier);
        } else {
            labelHistorique.setText("Aucun coup joué");
        }

        vueGrille.repaint();
    }

    /**
     * Affiche une boîte de dialogue d'erreur.
     */
    public void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Coup invalide",
                                      JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Affiche la boîte de fin de partie et demande si l'on rejoue.
     */
    public boolean demanderRejouer(int gagnant) {
        Object[] options = {"OUI", "NON"};
        int rep = JOptionPane.showOptionDialog(
            this,
            "Joueur " + gagnant + " a gagné ! Rejouer ?",
            "Fin de partie",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);
        return rep == 0;
    }

    // ─── Enregistrement des listeners (depuis le Controller) ─────────────────

    public void onNouvellePartie(ActionListener l) {
        btnNouvelle.addActionListener(l);
        menuNouvelle.addActionListener(l);
    }
    public void onSauvegarder(ActionListener l) {
        btnSauvegarder.addActionListener(l);
        menuSauvegarder.addActionListener(l);
    }
    public void onCharger(ActionListener l) {
        btnCharger.addActionListener(l);
        menuCharger.addActionListener(l);
    }
    public void onQuitter(ActionListener l) {
        btnQuitter.addActionListener(l);
        menuQuitter.addActionListener(l);
    }

    public void onAnnulerCoup(ActionListener l) {
        btnHistorique.addActionListener(l);
    }
    // ─── Accès aux sous-composants ────────────────────────────────────────────

    public VueGrille getVueGrille() { return vueGrille; }

    // ─── Helpers privés ───────────────────────────────────────────────────────

    private JLabel creerLabel(String texte, int taille, int style) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", style, taille));
        label.setForeground(TEXTE_CLAIR);
        return label;
    }

    private JButton creerBouton(String texte, Icon icone) {
        JButton btn = new JButton(texte, icone);
        btn.setBackground(new Color(120, 80, 20));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setIconTextGap(8);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JMenuItem creerMenuItem(String texte, Icon icone) {
        JMenuItem item = new JMenuItem(texte, icone);
        return item;
    }

    private String typeToString(TypeJoueur type) {
        return switch (type) {
            case HUMAIN         -> "Humain";
            case IA_ALEATOIRE   -> "IA Aléatoire";
            case IA_HEURISTIQUE -> "IA Heuristique";
            case IA_MINIMAX     -> "IA Minimax";
        };
    }
}
