package controller;

import ai.IAFactory;
import ai.StrategieIA;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import model.Coup;
import model.EtatPartie;
import model.ModeleJeu;
import model.ModeleJeu.TypeJoueur;
import util.GestionnairePartie;
import view.VueGrille;
import view.VuePrincipale;

/**
 * Contrôleur principal du jeu (MVC).
 *
 * Responsabilités :
 * - Écoute les clics sur la grille et les boutons
 * - Transmet les coups au modèle
 * - Déclenche les tours de l'IA si nécessaire
 * - Orchestre la sauvegarde / chargement
 * - Gère la fin de partie et le "rejouer"
 */
public class ControleurJeu {

    private ModeleJeu modele;
    private VuePrincipale vue;

    public ControleurJeu(ModeleJeu modele, VuePrincipale vue) {
        this.modele = modele;
        this.vue    = vue;

        // Abonner la vue aux changements du modèle
        modele.ajouterObservateur(this::actualiserVue);

        // Enregistrer les listeners des boutons
        vue.onNouvellePartie(e -> demanderNouvellePartie());
        vue.onSauvegarder(e -> sauvegarder());
        vue.onCharger(e -> charger());
        vue.onQuitter(e -> System.exit(0));
        vue.onAnnulerCoup(e -> annulerCoup());

        // Enregistrer les listeners de la grille
        attacherEcouteursGrille(vue.getVueGrille());

        // Si J1 est une IA (ex: IA vs IA), déclencher son premier coup
        planifierTourIA();
    }

    // ════════════════════════════════════════════════════════════════════════
    // Gestion des clics sur la grille
    // ════════════════════════════════════════════════════════════════════════

    private void attacherEcouteursGrille(VueGrille grille) {
        grille.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gererClic(e.getX(), e.getY());
            }
        });

        grille.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int l = grille.pixelVersLigne(e.getY());
                int c = grille.pixelVersColonne(e.getX());
                if (modele.estCoupValide(l, c)) {
                    grille.setSurvol(l, c);
                } else {
                    grille.setSurvol(-1, -1);
                }
                grille.repaint();
            }
        });
    }

    private void gererClic(int px, int py) {
        // Ignorer si partie terminée ou si c'est le tour de l'IA
        if (modele.getEtat() == EtatPartie.TERMINE) return;
        TypeJoueur type = modele.getTypeJoueur(modele.getJoueurActuel());
        if (type != TypeJoueur.HUMAIN) return;

        VueGrille grille = vue.getVueGrille();
        int ligne   = grille.pixelVersLigne(py);
        int colonne = grille.pixelVersColonne(px);

        if (!modele.estCoupValide(ligne, colonne)) {
            vue.afficherErreur("Coup invalide ! Choisissez une case disponible.");
            return;
        }

        modele.jouer(ligne, colonne);

        // Si la partie continue, vérifier si le prochain joueur est une IA
        if (modele.getEtat() == EtatPartie.EN_COURS) {
            planifierTourIA();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Tours de l'IA
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Si le joueur courant est une IA, planifie son coup avec un léger délai
     * pour laisser l'UI se rafraîchir.
     */
    private void planifierTourIA() {
        TypeJoueur type = modele.getTypeJoueur(modele.getJoueurActuel());
        if (type == TypeJoueur.HUMAIN) return;

        // Délai de 600ms pour que l'utilisateur voie le coup précédent
        Timer timer = new Timer(600, e -> jouerTourIA());
        timer.setRepeats(false);
        timer.start();
    }

    private void jouerTourIA() {
        if (modele.getEtat() != EtatPartie.EN_COURS) return;

        TypeJoueur type = modele.getTypeJoueur(modele.getJoueurActuel());
        StrategieIA ia = IAFactory.creer(type);
        Coup coup = ia.choisirCoup(modele);

        if (coup != null) {
            modele.jouer(coup);
            // Si l'IA suivante doit aussi jouer, enchaîner
            if (modele.getEtat() == EtatPartie.EN_COURS) {
                planifierTourIA();
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Mise à jour de la vue
    // ════════════════════════════════════════════════════════════════════════

    private void actualiserVue() {
        vue.actualiser();

        if (modele.getEtat() == EtatPartie.TERMINE) {
            // Léger délai avant d'afficher la boîte de fin
            Timer t = new Timer(400, e -> gererFinDePartie());
            t.setRepeats(false);
            t.start();
        }
    }

    private void gererFinDePartie() {
        boolean rejouer = vue.demanderRejouer(modele.getGagnant());
        if (rejouer) {
            modele.reinitialiser();
            planifierTourIA(); // Si J1 est une IA, elle joue en premier
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Nouvelle partie
    // ════════════════════════════════════════════════════════════════════════

    private void demanderNouvellePartie() {
        vue.dispose();
        Lanceur.lancerNouvellePartie(null);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Sauvegarde / Chargement
    // ════════════════════════════════════════════════════════════════════════

    private void sauvegarder() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Sauvegarder la partie");
        fc.setSelectedFile(new File("gaufre_sauvegarde.txt"));
        if (fc.showSaveDialog(vue) != JFileChooser.APPROVE_OPTION) return;

        try {
            GestionnairePartie.sauvegarder(modele, fc.getSelectedFile().getPath());
            JOptionPane.showMessageDialog(vue, "Partie sauvegardée avec succès.",
                                          "Sauvegarde", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            vue.afficherErreur("Erreur lors de la sauvegarde : " + ex.getMessage());
        }
    }

    private void charger() {
        vue.dispose();
        Lanceur.chargerPartie(null);
    }

    private void annulerCoup() {
    if (modele.annulerDernierCoup()) {
        if (modele.joueurActuel == 1) {
            if (modele.getTypeJoueur(1) == ModeleJeu.TypeJoueur.IA_ALEATOIRE || modele.getTypeJoueur(1) == ModeleJeu.TypeJoueur.IA_HEURISTIQUE || modele.getTypeJoueur(1) == ModeleJeu.TypeJoueur.IA_MINIMAX){
                planifierTourIA(); // Si IA a joué avant et doit rejouer
            }
        }
        if (modele.joueurActuel == 2) {
            if (modele.getTypeJoueur(2) == ModeleJeu.TypeJoueur.IA_ALEATOIRE || modele.getTypeJoueur(2) == ModeleJeu.TypeJoueur.IA_HEURISTIQUE || modele.getTypeJoueur(2) == ModeleJeu.TypeJoueur.IA_MINIMAX){
                planifierTourIA(); // Si IA a joué avant et doit rejouer
            }
        }
    } else {
        vue.afficherErreur("Aucun coup à annuler !");
    }
}
}
