package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import model.ModeleJeu;
import model.ModeleJeu.TypeJoueur;

/**
 * Gestion de la sauvegarde et du chargement de parties.
 *
 * Format du fichier (texte simple) :
 * ─────────────────────────────────
 * GAUFRE_SAVE_V1
 * lignes=5
 * colonnes=6
 * joueurActuel=1
 * typeJ1=HUMAIN
 * typeJ2=IA_ALEATOIRE
 * grille=
 * 0 0 0 0 0 0
 * 0 0 1 1 1 1
 * 0 0 1 1 1 1
 * ...
 * ─────────────────────────────────
 * 0 = case disponible, 1 = case mangée
 */
public class GestionnairePartie {

    private static final String HEADER = "GAUFRE_SAVE_V1";

    private GestionnairePartie() {}

    // ════════════════════════════════════════════════════════════════════════
    // Sauvegarde
    // ════════════════════════════════════════════════════════════════════════

    public static void sauvegarder(ModeleJeu modele, String chemin) throws IOException {
        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(chemin), StandardCharsets.UTF_8))) {

            w.write(HEADER); w.newLine();
            w.write("lignes=" + modele.getLignes()); w.newLine();
            w.write("colonnes=" + modele.getColonnes()); w.newLine();
            w.write("joueurActuel=" + modele.getJoueurActuel()); w.newLine();
            w.write("typeJ1=" + modele.getTypeJoueur(1).name()); w.newLine();
            w.write("typeJ2=" + modele.getTypeJoueur(2).name()); w.newLine();
            w.write("grille="); w.newLine();

            for (int l = 0; l < modele.getLignes(); l++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < modele.getColonnes(); c++) {
                    if (c > 0) sb.append(' ');
                    sb.append(modele.estMangee(l, c) ? '1' : '0');
                }
                w.write(sb.toString());
                w.newLine();
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Chargement
    // ════════════════════════════════════════════════════════════════════════

    public static ModeleJeu charger(String chemin) throws IOException {
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(new FileInputStream(chemin), StandardCharsets.UTF_8))) {

            String header = r.readLine();
            if (!HEADER.equals(header)) {
                throw new IOException("Fichier de sauvegarde invalide ou corrompu.");
            }

            int lignes       = Integer.parseInt(lireValeur(r, "lignes"));
            int colonnes     = Integer.parseInt(lireValeur(r, "colonnes"));
            int joueurActuel = Integer.parseInt(lireValeur(r, "joueurActuel"));
            TypeJoueur tj1   = TypeJoueur.valueOf(lireValeur(r, "typeJ1"));
            TypeJoueur tj2   = TypeJoueur.valueOf(lireValeur(r, "typeJ2"));

            // Lire l'entête "grille="
            r.readLine();

            // Reconstruire la grille
            boolean[][] mangee = new boolean[lignes][colonnes];
            for (int l = 0; l < lignes; l++) {
                String[] vals = r.readLine().trim().split("\\s+");
                for (int c = 0; c < colonnes; c++) {
                    mangee[l][c] = vals[c].equals("1");
                }
            }

            // Reconstruire le modèle
            ModeleJeu modele = new ModeleJeu(lignes, colonnes, tj1, tj2);
            // Rejouer les coups pour reconstruire l'état
            // Alternative plus directe : accès package privé via classe interne
            // Ici on utilise une méthode de reconstruction directe
            restaurerEtat(modele, mangee, joueurActuel);
            return modele;
        }
    }

    /**
     * Restaure l'état interne du modèle directement depuis les données lues,
     * sans rejouer les coups (approche robuste via chargerEtat).
     */
    private static void restaurerEtat(ModeleJeu modele,
                                       boolean[][] mangee, int joueurActuel) {
        modele.chargerEtat(mangee, joueurActuel);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static String lireValeur(BufferedReader r, String cle) throws IOException {
        String ligne = r.readLine();
        if (ligne == null || !ligne.startsWith(cle + "=")) {
            throw new IOException("Champ manquant : " + cle);
        }
        return ligne.substring(cle.length() + 1).trim();
    }
}
