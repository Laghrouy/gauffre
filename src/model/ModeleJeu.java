package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle principal du jeu Gaufre empoisonnée.
 *
 * Règles :
 * - La grille représente une gaufre rectangulaire.
 * - La case (0,0) est empoisonnée.
 * - À chaque tour, le joueur choisit une case : toutes les cases
 *   dans le rectangle [ligne..fin][colonne..fin] sont "mangées".
 * - Celui qui mange la case (0,0) perd la partie.
 *
 * Architecture : ce modèle est indépendant de l'IHM (pattern MVC).
 * Il notifie les observateurs via le pattern Observer.
 */
public class ModeleJeu {

    // ─── Données de la grille ────────────────────────────────────────────────
    private final int lignes;
    private final int colonnes;
    private boolean[][] mangee;   // true = case supprimée

    // ─── État de la partie ───────────────────────────────────────────────────
    public int joueurActuel;     // 1 ou 2
    private int gagnant;          // 0 = pas encore, 1 ou 2 = gagnant
    public int nbCouptJoue;
    private EtatPartie etat;

    // ─── Historique des coups ────────────────────────────────────────────────
    private final List<Coup> historique;
    public List<boolean[][]> historiqueEtat;

    // ─── Observateurs (pattern Observer simplifié) ───────────────────────────
    private final List<Runnable> observateurs;

    // ─── Mode de jeu ─────────────────────────────────────────────────────────
    /** Type des deux joueurs : HUMAIN ou IA (1/2/3) */
    public enum TypeJoueur { HUMAIN, IA_ALEATOIRE, IA_HEURISTIQUE, IA_MINIMAX }
    public TypeJoueur typeJoueur1;
    public TypeJoueur typeJoueur2;

    // ════════════════════════════════════════════════════════════════════════
    // Constructeur
    // ════════════════════════════════════════════════════════════════════════

    public ModeleJeu(int lignes, int colonnes,
                     TypeJoueur typeJ1, TypeJoueur typeJ2) {
        if (lignes < 2 || colonnes < 2) {
            throw new IllegalArgumentException(
                "La grille doit être d'au moins 2x2.");
        }
        this.lignes   = lignes;
        this.colonnes = colonnes;
        this.typeJoueur1 = typeJ1;
        this.typeJoueur2 = typeJ2;

        this.historique  = new ArrayList<>();
        this.observateurs = new ArrayList<>();
        this.historiqueEtat = new ArrayList<>();
        reinitialiser();
    }

    /** Constructeur avec valeurs par défaut (deux humains). */
    public ModeleJeu(int lignes, int colonnes) {
        this(lignes, colonnes, TypeJoueur.HUMAIN, TypeJoueur.HUMAIN);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Logique du jeu
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Remet la partie à zéro (même taille, mêmes types de joueurs).
     */
    public void reinitialiser() {
        this.mangee      = new boolean[lignes][colonnes];
        this.joueurActuel = 1;
        this.gagnant      = 0;
        this.etat         = EtatPartie.EN_COURS;
        this.historique.clear();
        this.historiqueEtat.clear();
        notifierObservateurs();
    }

    /**
     * Joue le coup (ligne, colonne) pour le joueur courant.
     * Retourne false si le coup est invalide.
     */
    public boolean jouer(int ligne, int colonne) {
        if (!estCoupValide(ligne, colonne)) return false;

        // Cas spécial : case empoisonnée (0,0)
        if (ligne == 0 && colonne == 0) {
            mangee[0][0] = true;
            gagnant = (joueurActuel == 1) ? 2 : 1;
            etat = EtatPartie.TERMINE;
            historique.add(new Coup(ligne, colonne));
            notifierObservateurs();
            return true;
        }
        ModeleJeu modelCopie = copier();
        historiqueEtat.add(modelCopie.mangee);
        // Manger toutes les cases dans le rectangle (ligne..fin) x (colonne..fin)
        for (int l = ligne; l < lignes; l++) {
            for (int c = colonne; c < colonnes; c++) {
                mangee[l][c] = true;
            }
        }

        historique.add(new Coup(ligne, colonne));
        joueurActuel = (joueurActuel == 1) ? 2 : 1;
        notifierObservateurs();
        return true;
    }

    /**
     * Joue un coup encapsulé dans un objet Coup.
     */
    public boolean jouer(Coup coup) {
        return jouer(coup.getLigne(), coup.getColonne());
    }

    /**
     * Vérifie si un coup est valide :
     * - dans les bornes de la grille
     * - case non encore mangée
     * - partie en cours
     */
    public boolean estCoupValide(int ligne, int colonne) {
        if (etat != EtatPartie.EN_COURS) return false;
        if (ligne < 0 || ligne >= lignes) return false;
        if (colonne < 0 || colonne >= colonnes) return false;
        return !mangee[ligne][colonne];
    }

    /**
     * Retourne la liste de tous les coups valides disponibles.
     */
    public List<Coup> getCoupsDisponibles() {
        List<Coup> coups = new ArrayList<>();
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                if (!mangee[l][c]) {
                    coups.add(new Coup(l, c));
                }
            }
        }
        return coups;
    }

    /**
     * Retourne une copie profonde du modèle (utile pour l'IA Minimax).
     */
    public ModeleJeu copier() {
        ModeleJeu copie = new ModeleJeu(lignes, colonnes, typeJoueur1, typeJoueur2);
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                copie.mangee[l][c] = this.mangee[l][c];
            }
        }
        copie.joueurActuel = this.joueurActuel;
        copie.gagnant      = this.gagnant;
        copie.etat         = this.etat;
        // Pas besoin de copier l'historique pour l'IA
        return copie;
    }


    // ════════════════════════════════════════════════════════════════════════
    // Pattern Observateur
    // ════════════════════════════════════════════════════════════════════════

    public void ajouterObservateur(Runnable r) {
        observateurs.add(r);
    }

    private void notifierObservateurs() {
        for (Runnable r : observateurs) r.run();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Etat de la partie
    // ════════════════════════════════════════════════════════════════════════

    public int getLignes()        { return lignes; }
    public int getColonnes()      { return colonnes; }
    public int getJoueurActuel()  { return joueurActuel; }
    public int getGagnant()       { return gagnant; }
    public EtatPartie getEtat()   { return etat; }
    public List<Coup> getHistorique() { return List.copyOf(historique); }

    public boolean estMangee(int ligne, int colonne) {
        return mangee[ligne][colonne];
    }

    public TypeJoueur getTypeJoueur(int numero) {
        return (numero == 1) ? typeJoueur1 : typeJoueur2;
    }

    public void setTypeJoueur(int numero, TypeJoueur type) {
        if (numero == 1) typeJoueur1 = type;
        else             typeJoueur2 = type;
    }

    /**
     * Restaure directement l'état interne de la grille (utilisé par le chargement).
     * Bypasse la logique de jeu pour poser l'état exact sauvegardé.
     *
     * @param grilleMangee  tableau [lignes][colonnes] des cases mangées
     * @param joueur        joueur dont c'est le tour (1 ou 2)
     */
    public void chargerEtat(boolean[][] grilleMangee, int joueur) {
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                this.mangee[l][c] = grilleMangee[l][c];
            }
        }
        this.joueurActuel = joueur;
        this.gagnant      = 0;
        this.etat         = EtatPartie.EN_COURS;
        this.historique.clear();
        notifierObservateurs();
    }

    public boolean annulerDernierCoup() {
        if (historique.isEmpty()) return false;
        
        historique.remove(historique.size() - 1);
        
        if (historiqueEtat.isEmpty()) {
            reinitialiser();
            return true;
        }
        
        boolean[][] etatPrecedent = historiqueEtat.remove(historiqueEtat.size() - 1);
        for (int l = 0; l < lignes; l++) {
            for (int c = 0; c < colonnes; c++) {
                mangee[l][c] = etatPrecedent[l][c];
            }
        }
        
        joueurActuel = (joueurActuel == 1) ? 2 : 1;
        
        if (etat == EtatPartie.TERMINE) {
            etat = EtatPartie.EN_COURS;
            gagnant = 0;
        }
        
        notifierObservateurs();
        return true;
    }
}
