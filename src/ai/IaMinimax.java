package ai;

import java.util.List;
import model.Coup;
import model.ModeleJeu;

/**
 * IA Niveau 3 — Minimax avec profondeur limitée.
 *
 * Principe Minimax :
 * - Le joueur MAX cherche à maximiser son score.
 * - Le joueur MIN cherche à minimiser le score de MAX.
 * - On évalue récursivement jusqu'à la profondeur limite.
 *
 * Évaluation terminale :
 * - +1  : le joueur MAX gagne (l'adversaire a pris la case empoisonnée)
 * - -1  : le joueur MIN gagne (MAX a pris la case empoisonnée)
 * - 0   : match nul / profondeur atteinte (évaluation heuristique)
 *
 * Note : dans la Gaufre empoisonnée, il existe une théorie mathématique
 * qui prouve que le premier joueur a toujours une stratégie gagnante
 * (sauf grille 1×1). Le Minimax la retrouve naturellement.
 */
public class IaMinimax implements StrategieIA {

    private final int profondeurMax;

    public IaMinimax(int profondeurMax) {
        this.profondeurMax = profondeurMax;
    }

    @Override
    public Coup choisirCoup(ModeleJeu modele) {
        List<Coup> coups = modele.getCoupsDisponibles();
        if (coups.isEmpty()) return null;

        Coup meilleurCoup = null;
        int  meilleurScore = Integer.MIN_VALUE;
        int  joueurIA = modele.getJoueurActuel();

        for (Coup coup : coups) {
            ModeleJeu copie = modele.copier();
            copie.jouer(coup);

            int score = minimax(copie, profondeurMax - 1, false, joueurIA,
                                Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup  = coup;
            }
        }

        return meilleurCoup;
    }

    /**
     * Algorithme Minimax avec élagage Alpha-Bêta.
     *
     * @param modele    État courant du jeu
     * @param prof      Profondeur restante
     * @param estMax    true si c'est le tour du joueur maximisant
     * @param joueurIA  Numéro du joueur IA (MAX)
     * @param alpha     Valeur Alpha pour l'élagage
     * @param beta      Valeur Bêta pour l'élagage
     */
    private int minimax(ModeleJeu modele, int prof, boolean estMax,
                        int joueurIA, int alpha, int beta) {

        // ── Cas terminal : partie terminée ───────────────────────────────
        if (modele.getEtat() == model.EtatPartie.TERMINE) {
            // Le gagnant est celui qui n'a PAS pris la case empoisonnée
            return (modele.getGagnant() == joueurIA) ? +10 : -10;
        }

        // ── Cas terminal : profondeur atteinte ───────────────────────────
        if (prof == 0) {
            return evaluerHeuristique(modele, joueurIA);
        }

        List<Coup> coups = modele.getCoupsDisponibles();

        if (estMax) {
            int valeur = Integer.MIN_VALUE;
            for (Coup coup : coups) {
                ModeleJeu copie = modele.copier();
                copie.jouer(coup);
                valeur = Math.max(valeur,
                    minimax(copie, prof - 1, false, joueurIA, alpha, beta));
                alpha = Math.max(alpha, valeur);
                if (beta <= alpha) break; // Élagage Bêta
            }
            return valeur;
        } else {
            int valeur = Integer.MAX_VALUE;
            for (Coup coup : coups) {
                ModeleJeu copie = modele.copier();
                copie.jouer(coup);
                valeur = Math.min(valeur,
                    minimax(copie, prof - 1, true, joueurIA, alpha, beta));
                beta = Math.min(beta, valeur);
                if (beta <= alpha) break; // Élagage Alpha
            }
            return valeur;
        }
    }

    /**
     * Évaluation heuristique lorsque la profondeur limite est atteinte.
     *
     * Heuristique simple : plus il reste de cases, mieux c'est pour celui qui
     * les possède. On favorise les positions où l'adversaire a peu de coups.
     */
    private int evaluerHeuristique(ModeleJeu modele, int joueurIA) {
        // Nombre de coups disponibles pour le joueur courant
        int nbCoups = modele.getCoupsDisponibles().size();
        // Si c'est le tour de l'adversaire de l'IA, beaucoup de coups = mauvais pour l'IA
        boolean tourAdversaire = (modele.getJoueurActuel() != joueurIA);
        return tourAdversaire ? -nbCoups : nbCoups;
    }
}
