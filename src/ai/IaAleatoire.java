package ai;

import java.util.List;
import java.util.Random;
import model.Coup;
import model.ModeleJeu;

/**
 * IA Niveau 1 — Aléatoire.
 *
 * Joue un coup valide choisi uniformément au hasard parmi
 * tous les coups disponibles.
 */
public class IaAleatoire implements StrategieIA {

    private final Random rng = new Random();

    @Override
    public Coup choisirCoup(ModeleJeu modele) {
        List<Coup> coups = modele.getCoupsDisponibles();
        if (coups.isEmpty()) return null;
        return coups.get(rng.nextInt(coups.size()));
    }
}
