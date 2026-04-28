package ai;

import model.ModeleJeu.TypeJoueur;

/**
 * Fabrique d'IA : retourne la bonne stratégie selon le TypeJoueur.
 */
public class IAFactory {

    private IAFactory() {}  // Classe utilitaire, non instanciable

    public static StrategieIA creer(TypeJoueur type) {
        return switch (type) {
            case IA_ALEATOIRE   -> new IaAleatoire();
            case IA_HEURISTIQUE -> new IaHeuristique();
            case IA_MINIMAX     -> new IaMinimax(4);  // Profondeur limitée à 4
            default -> throw new IllegalArgumentException("Pas d'IA pour " + type);
        };
    }
}
