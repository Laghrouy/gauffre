package controller;

import java.io.IOException;
import javax.swing.*;
import model.ModeleJeu;
import util.GestionnairePartie;
import view.DialogueConfig;
import view.MenuPrincipal;
import view.VueGrille;
import view.VuePrincipale;

/**
 * Classe utilitaire de lancement : gère la navigation entre
 * le menu principal, la configuration d'une nouvelle partie
 * et le chargement d'une partie existante.
 *
 * Accessible depuis Main (package par défaut) et depuis ControleurJeu.
 */
public class Lanceur {

    private Lanceur() {}

    // ── Affiche le menu principal ──────────────────────────────────────────────
    public static void afficherMenu() {
        MenuPrincipal menu = new MenuPrincipal();

        menu.onNouvellePartie(e -> {
            menu.dispose();
            lancerNouvellePartie(null);
        });

        menu.onCharger(e -> {
            menu.dispose();
            chargerPartie(null);
        });

        menu.onQuitter(e -> System.exit(0));

        menu.setVisible(true);
    }

    // ── Lance une nouvelle partie après configuration ──────────────────────────
    public static void lancerNouvellePartie(JFrame parent) {
        DialogueConfig config = new DialogueConfig(parent);
        config.setVisible(true);

        if (!config.estConfirme()) {
            // Retour au menu si l'utilisateur annule
            afficherMenu();
            return;
        }

        ModeleJeu modele = new ModeleJeu(
            config.getLignes(),
            config.getColonnes(),
            config.getTypeJoueur1(),
            config.getTypeJoueur2()
        );

        VueGrille grille = new VueGrille(modele);
        VuePrincipale vue = new VuePrincipale(modele, grille);
        new ControleurJeu(modele, vue);
        vue.setVisible(true);
    }

    // ── Ouvre un sélecteur de fichier et charge une partie ────────────────────
    public static void chargerPartie (JFrame parent) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Charger une partie");
        if (fc.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            afficherMenu();
            return;
        }
        try {
            ModeleJeu modele = GestionnairePartie.charger(
                fc.getSelectedFile().getPath());
            VueGrille grille = new VueGrille(modele);
            VuePrincipale vue = new VuePrincipale(modele, grille);
            new ControleurJeu(modele, vue);
            vue.setVisible(true);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                "Impossible de charger la partie : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            afficherMenu();
        }
    }

    public static void chargerPartieModele(ModeleJeu modele) {
        if (modele == null) {
            afficherMenu();
            return;
        }

        VueGrille grille = new VueGrille(modele);
        VuePrincipale vue = new VuePrincipale(modele, grille);
        new ControleurJeu(modele, vue);
        vue.setVisible(true);
    }
}
