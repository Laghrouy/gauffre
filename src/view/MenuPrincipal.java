package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import util.Icones;

/**
 * Écran de menu principal affiché au lancement du jeu.
 *
 * Propose trois actions :
 * - Nouvelle partie (ouvre DialogueConfig)
 * - Charger une partie
 * - Quitter
 */
public class MenuPrincipal extends JFrame {

    // ─── Actions possibles ────────────────────────────────────────────────────
    public enum Action { NOUVELLE_PARTIE, CHARGER, QUITTER }

    private Action actionChoisie = Action.QUITTER;

    // ─── Boutons ─────────────────────────────────────────────────────────────
    private final JButton btnNouveaux;
    private final JButton btnCharger;
    private final JButton btnQuitter;

    // ─── Thème (cohérent avec VuePrincipale) ─────────────────────────────────
    private static final Color FOND          = new Color(30, 18, 3);
    private static final Color COULEUR_TITRE = new Color(255, 200, 50);
    private static final Color COULEUR_BTN   = new Color(120, 80, 20);
    private static final Color COULEUR_HOVER = new Color(160, 110, 30);
    private static final Color TEXTE_BTN     = Color.WHITE;

    public MenuPrincipal() {
        super("Gaufre Empoisonnée");
        // Icône de la fenêtre (SVG gaufre 32px)
        setIconImage(Icones.toImageIcon(Icones.gaufre(32)).getImage());

        btnNouveaux = creerBouton("Nouvelle partie",  Icones.jouer(20));
        btnCharger  = creerBouton("Charger une partie", Icones.dossier(20));
        btnQuitter  = creerBouton("Quitter",           Icones.sortie(20));
        btnQuitter.setBackground(new Color(130, 35, 35));

        construireUI();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    // ─── Construction UI ──────────────────────────────────────────────────────

    private void construireUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FOND);

        root.add(construireTitre(),  BorderLayout.NORTH);
        root.add(construireBoutons(), BorderLayout.CENTER);
        root.add(construireCredits(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel construireTitre() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FOND);
        panel.setBorder(new EmptyBorder(40, 60, 20, 60));

        // Icône gaufre SVG (80px)
        JLabel iconLabel = new JLabel(Icones.toImageIcon(Icones.gaufre(80)),
                                       SwingConstants.CENTER);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titre = new JLabel("Gaufre Empoisonnée", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 28));
        titre.setForeground(COULEUR_TITRE);
        titre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel(
            "Qui mangera la case empoisonnée ?",
            SwingConstants.CENTER);
        sousTitre.setFont(new Font("SansSerif", Font.ITALIC, 13));
        sousTitre.setForeground(new Color(200, 170, 100));
        sousTitre.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(titre);
        panel.add(Box.createVerticalStrut(6));
        panel.add(sousTitre);
        return panel;
    }

    private JPanel construireBoutons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(FOND);
        panel.setBorder(new EmptyBorder(20, 80, 20, 80));

        btnNouveaux.setAlignmentX(CENTER_ALIGNMENT);
        btnCharger.setAlignmentX(CENTER_ALIGNMENT);
        btnQuitter.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(btnNouveaux);
        panel.add(Box.createVerticalStrut(14));
        panel.add(btnCharger);
        panel.add(Box.createVerticalStrut(14));
        panel.add(btnQuitter);
        return panel;
    }

    private JPanel construireCredits() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(FOND);
        panel.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel credits = new JLabel("Projet PROG6 — Groupe 9");
        credits.setFont(new Font("SansSerif", Font.PLAIN, 11));
        credits.setForeground(new Color(120, 100, 60));
        panel.add(credits);
        return panel;
    }

    // ─── Enregistrement des listeners ─────────────────────────────────────────

    public void onNouvellePartie(ActionListener l) { btnNouveaux.addActionListener(l); }
    public void onCharger(ActionListener l)        { btnCharger.addActionListener(l); }
    public void onQuitter(ActionListener l)        { btnQuitter.addActionListener(l); }

    // ─── Helper bouton ────────────────────────────────────────────────────────

    private JButton creerBouton(String texte, javax.swing.Icon icone) {
        JButton btn = new JButton(texte, icone);
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setBackground(COULEUR_BTN);
        btn.setForeground(TEXTE_BTN);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setIconTextGap(10);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(260, 46));
        btn.setMaximumSize(new Dimension(260, 46));

        // Effet de survol
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color fondOriginal = btn.getBackground();
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(fondOriginal.brighter());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(fondOriginal);
            }
        });
        return btn;
    }
}
