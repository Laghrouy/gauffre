package ai;

import model.Coup;
import model.ModeleJeu;

/**
 * Interface commune à toutes les stratégies IA.
 * Chaque niveau implémente cette interface.
 */
public interface StrategieIA {
    /**
     * Choisit le meilleur coup pour le joueur courant dans l'état donné.
     * Retourne null si aucun coup n'est possible (ne devrait pas arriver).
     */
    Coup choisirCoup(ModeleJeu modele);
}
