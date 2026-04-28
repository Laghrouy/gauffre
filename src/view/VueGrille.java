package view;

import java.awt.*;
import javax.swing.*;
import model.ModeleJeu;
import util.Icones;

/**
 * Panneau graphique qui dessine la grille de la gaufre.
 *
 * Conventions visuelles :
 * - Case normale : dorée (255, 200, 50)
 * - Case empoisonnée (0,0) non mangée : vert foncé
 * - Case mangée : blanche (vide)
 * - Survol de la souris : coloration bleutée
 */
public class VueGrille extends JComponent {

    private final ModeleJeu modele;

    // Case sous le curseur (pour le survol)
    private int survolLigne  = -1;
    private int survolColonne = -1;

    // Couleurs
    private static final Color COULEUR_CASE       = new Color(255, 200, 50);
    private static final Color COULEUR_EMPOISONNEE = new Color(180, 30, 30);
    private static final Color COULEUR_MANGEE      = new Color(245, 245, 245);
    private static final Color COULEUR_SURVOL      = new Color(150, 210, 255);
    private static final Color COULEUR_SURVOL_DANG = new Color(255, 120, 120);
    private static final Color COULEUR_GRILLE      = new Color(180, 140, 20);

    public VueGrille(ModeleJeu modele) {
        this.modele = modele;
        setPreferredSize(new Dimension(600, 500));
    }

    // ─── Calcul de la taille d'une case ──────────────────────────────────────

    public int getTailleCase() {
        int w = getWidth()  / modele.getColonnes();
        int h = getHeight() / modele.getLignes();
        return Math.max(10, Math.min(w, h));
    }

    // ─── Conversion pixel → case ─────────────────────────────────────────────

    public int pixelVersLigne(int y)   { return y / getTailleCase(); }
    public int pixelVersColonne(int x) { return x / getTailleCase(); }

    // ─── Survol ───────────────────────────────────────────────────────────────

    public void setSurvol(int ligne, int colonne) {
        this.survolLigne   = ligne;
        this.survolColonne = colonne;
    }

    // ─── Dessin ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int t = getTailleCase();
        int pad = 2; // écart entre cases

        for (int l = 0; l < modele.getLignes(); l++) {
            for (int c = 0; c < modele.getColonnes(); c++) {
                int x = c * t;
                int y = l * t;

                // ── Choisir la couleur de fond ────────────────────────────
                Color fond;
                if (modele.estMangee(l, c)) {
                    fond = COULEUR_MANGEE;
                } else if (l == 0 && c == 0) {
                    fond = COULEUR_EMPOISONNEE;
                } else if (estDansSurvol(l, c)) {
                    fond = (survolLigne == 0 && survolColonne == 0)
                           ? COULEUR_SURVOL_DANG : COULEUR_SURVOL;
                } else {
                    fond = COULEUR_CASE;
                }

                // ── Remplissage ───────────────────────────────────────────
                g2.setColor(fond);
                g2.fillRoundRect(x + pad, y + pad,
                                 t - pad * 2, t - pad * 2, 6, 6);

                // ── Bordure ───────────────────────────────────────────────
                if (!modele.estMangee(l, c)) {
                    g2.setColor(COULEUR_GRILLE);
                    g2.drawRoundRect(x + pad, y + pad,
                                     t - pad * 2, t - pad * 2, 6, 6);
                }

                // ── Icône SVG crâne sur la case empoisonnée ───────────────────────
                if (l == 0 && c == 0 && !modele.estMangee(l, c)) {
                    int iconSize = Math.max(10, t - pad * 6);
                    Icon skull = Icones.crane(iconSize);
                    int ix = x + (t - iconSize) / 2;
                    int iy = y + (t - iconSize) / 2;
                    skull.paintIcon(this, g2, ix, iy);
                }
            }
        }
    }

    /**
     * Renvoie true si la case (l,c) doit être colorée en survol.
     * Le survol affecte le rectangle [survolLigne..fin][survolColonne..fin].
     */
    private boolean estDansSurvol(int l, int c) {
        if (survolLigne < 0 || survolColonne < 0) return false;
        return l >= survolLigne && c >= survolColonne;
    }
}
