import controller.Lanceur;
import javax.swing.SwingUtilities;

/**
 * Point d'entrée principal du jeu Gaufre Empoisonnée.
 * Délègue toute la logique de navigation à Lanceur.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Lanceur::afficherMenu);
    }
}
