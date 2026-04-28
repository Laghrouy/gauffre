package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Coup;
import model.ModeleJeu;

/**
 * IA Niveau 2 — Heuristique.
 *
 * Stratégie :
 * 1. Si un coup gagnant immédiat existe (force l'adversaire à perdre), le jouer.
 * 2. Éviter de prendre la case empoisonnée (0,0) sauf si c'est le seul coup.
 * 3. Parmi les coups "sûrs", choisir au hasard (peut être amélioré).
 *
 * Un coup est "gagnant immédiat" si, après ce coup, l'adversaire
 * n'a plus que la case (0,0) disponible.
 */
public class IaHeuristique implements StrategieIA {

    private final Random rng = new Random();

    @Override
    public Coup choisirCoup(ModeleJeu modele) {
        List<Coup> disponibles = modele.getCoupsDisponibles();
        if (disponibles.isEmpty()) return null;

        // ── 1. Chercher un coup gagnant immédiat ──────────────────────────
        for (Coup coup : disponibles) {
            if (estCoupGagnant(modele, coup)) {
                return coup;
            }
        }

        // ── 2. Éliminer la case empoisonnée si d'autres coups existent ────
        List<Coup> coupsSurs = new ArrayList<>();
        for (Coup coup : disponibles) {
            if (!(coup.getLigne() == 0 && coup.getColonne() == 0)) {
                coupsSurs.add(coup);
            }
        }

        if (coupsSurs.isEmpty()) {
            // Seul la case empoisonnée reste : on est forcé de perdre
            return disponibles.get(0);
        }

        // ── 3. Choisir un coup sûr au hasard ─────────────────────────────
        return coupsSurs.get(rng.nextInt(coupsSurs.size()));
    }

    /**
     * Vérifie si jouer ce coup force l'adversaire à ne prendre
     * que la case (0,0) à son prochain tour.
     */
    private boolean estCoupGagnant(ModeleJeu modele, Coup coup) {
        ModeleJeu copie = modele.copier();
        copie.jouer(coup);
        // Si après ce coup il ne reste que (0,0), l'adversaire perd
        List<Coup> restants = copie.getCoupsDisponibles();
        return restants.size() == 1
            && restants.get(0).getLigne() == 0
            && restants.get(0).getColonne() == 0;
    }
}
