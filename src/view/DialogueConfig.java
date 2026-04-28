package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.ModeleJeu.TypeJoueur;

/**
 * Dialogue de configuration affiché au lancement ou lors d'une nouvelle partie.
 * Permet de choisir :
 * - La taille de la grille
 * - Le type de chaque joueur (Humain / IA niveau 1, 2 ou 3)
 */
public class DialogueConfig extends JDialog {

    private boolean confirme = false;

    private final JSpinner spinLignes;
    private final JSpinner spinColonnes;
    private final JComboBox<String> comboJ1;
    private final JComboBox<String> comboJ2;

    private static final String[] TYPES = {
        "Humain",
        "IA Aléatoire (niveau 1)",
        "IA Heuristique (niveau 2)",
        "IA Minimax (niveau 3)"
    };

    public DialogueConfig(JFrame parent) {
        super(parent, "Nouvelle partie — Configuration", true);

        spinLignes   = new JSpinner(new SpinnerNumberModel(5, 2, 15, 1));
        spinColonnes = new JSpinner(new SpinnerNumberModel(6, 2, 15, 1));
        comboJ1      = new JComboBox<>(TYPES);
        comboJ2      = new JComboBox<>(TYPES);
        comboJ2.setSelectedIndex(1); // IA par défaut pour J2

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        form.add(new JLabel("Lignes :"));       form.add(spinLignes);
        form.add(new JLabel("Colonnes :"));     form.add(spinColonnes);
        form.add(new JLabel("Joueur 1 :"));     form.add(comboJ1);
        form.add(new JLabel("Joueur 2 :"));     form.add(comboJ2);

        JButton btnOK     = new JButton("Jouer !");
        JButton btnAnnuler = new JButton("Annuler");
        btnOK.addActionListener(e -> { confirme = true; dispose(); });
        btnAnnuler.addActionListener(e -> dispose());

        JPanel bas = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bas.add(btnAnnuler);
        bas.add(btnOK);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(bas,  BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public boolean estConfirme() { return confirme; }

    public int getLignes()   { return (int) spinLignes.getValue(); }
    public int getColonnes() { return (int) spinColonnes.getValue(); }

    public TypeJoueur getTypeJoueur1() { return indexVersType(comboJ1.getSelectedIndex()); }
    public TypeJoueur getTypeJoueur2() { return indexVersType(comboJ2.getSelectedIndex()); }

    private TypeJoueur indexVersType(int i) {
        return switch (i) {
            case 1  -> TypeJoueur.IA_ALEATOIRE;
            case 2  -> TypeJoueur.IA_HEURISTIQUE;
            case 3  -> TypeJoueur.IA_MINIMAX;
            default -> TypeJoueur.HUMAIN;
        };
    }
}
