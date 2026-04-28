package util;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Bibliothèque d'icônes vectorielles pour le jeu Gaufre Empoisonnée.
 *
 * Chaque icône est un rendu Java2D équivalent au fichier SVG correspondant
 * dans res/icons/. Toutes les coordonnées sont exprimées dans un
 * espace 32×32, puis mis à l'échelle à la taille demandée via AffineTransform.
 *
 * Usage :
 *   Icon ico = Icones.crane(32);
 *   ico.paintIcon(composant, g, x, y);
 *   // ou
 *   ImageIcon img = Icones.toImageIcon(Icones.trophee(24));
 *   label.setIcon(img);
 */
public final class Icones {

    private Icones() {}

    // ════════════════════════════════════════════════════════════════════════
    // Factory
    // ════════════════════════════════════════════════════════════════════════

    /** Grille 4×4 dorée — res/icons/gaufre.svg */
    public static Icon gaufre(int taille)      { return new GaufreIcon(taille); }

    /** Tête de mort — res/icons/crane.svg */
    public static Icon crane(int taille)       { return new CraneIcon(taille); }

    /** Trophée doré — res/icons/trophee.svg */
    public static Icon trophee(int taille)     { return new TropheeIcon(taille); }

    /** Bouton play vert — res/icons/jouer.svg */
    public static Icon jouer(int taille)       { return new JouerIcon(taille); }

    /** Bouton plus bleu — res/icons/nouveau.svg */
    public static Icon nouveau(int taille)     { return new NouveauIcon(taille); }

    /** Dossier jaune — res/icons/dossier.svg */
    public static Icon dossier(int taille)     { return new DossierIcon(taille); }

    /** Disquette bleue — res/icons/sauvegarder.svg */
    public static Icon sauvegarder(int taille) { return new SauvegarderIcon(taille); }

    /** Porte + flèche rouge — res/icons/sortie.svg */
    public static Icon sortie(int taille)      { return new SortieIcon(taille); }

    /**
     * Convertit n'importe quel Icon vectoriel en ImageIcon (pour JLabel, etc.)
     */
    public static ImageIcon toImageIcon(Icon icon) {
        BufferedImage img = new BufferedImage(
            icon.getIconWidth(), icon.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();
        return new ImageIcon(img);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Classe de base — coordonnées normalisées en 32×32
    // ════════════════════════════════════════════════════════════════════════

    private abstract static class VectorIcon implements Icon {
        protected final int taille;

        VectorIcon(int taille) { this.taille = taille; }

        @Override public int getIconWidth()  { return taille; }
        @Override public int getIconHeight() { return taille; }

        @Override
        public final void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                                RenderingHints.VALUE_STROKE_PURE);
            g2.translate(x, y);
            // Mise à l'échelle depuis l'espace 32×32
            double s = taille / 32.0;
            g2.scale(s, s);
            dessiner(g2);
            g2.dispose();
        }

        /** Dessine l'icône dans un espace de coordonnées 32×32. */
        protected abstract void dessiner(Graphics2D g2);
    }

    // ════════════════════════════════════════════════════════════════════════
    // Gaufre — grille 4×4 de carrés dorés (res/icons/gaufre.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class GaufreIcon extends VectorIcon {
        GaufreIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Cellule : 7px, gap : 1px → 4*(7+1) = 32
            double cellW = 7.0, gap = 1.0, r = 1.5;

            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    double x = col * (cellW + gap) + 0.5;
                    double y = row * (cellW + gap) + 0.5;

                    // Ombre légère
                    g2.setColor(new Color(180, 130, 20, 80));
                    g2.fill(new RoundRectangle2D.Double(x + 0.6, y + 0.6, cellW, cellW, r, r));

                    // Remplissage doré
                    g2.setColor(new Color(255, 200, 50));
                    g2.fill(new RoundRectangle2D.Double(x, y, cellW, cellW, r, r));

                    // Bordure
                    g2.setColor(new Color(200, 150, 20));
                    g2.setStroke(new BasicStroke(0.6f));
                    g2.draw(new RoundRectangle2D.Double(x, y, cellW, cellW, r, r));
                }
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Crane / Skull (res/icons/crane.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class CraneIcon extends VectorIcon {
        CraneIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Tête (ellipse)
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Double(4, 1, 24, 22));

            // Mâchoire
            Path2D jaw = new Path2D.Double();
            jaw.moveTo(8, 19);  jaw.lineTo(24, 19);
            jaw.lineTo(24, 27); jaw.curveTo(24, 30, 20, 30, 20, 30);
            jaw.lineTo(12, 30); jaw.curveTo(8, 30, 8, 27, 8, 27);
            jaw.closePath();
            g2.fill(jaw);

            // Contour
            g2.setColor(new Color(60, 40, 40));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Ellipse2D.Double(4, 1, 24, 22));
            g2.draw(jaw);

            // Orbites (yeux)
            g2.setColor(new Color(40, 20, 20));
            g2.fill(new Ellipse2D.Double(6.5, 8, 7, 7.5));    // gauche
            g2.fill(new Ellipse2D.Double(18.5, 8, 7, 7.5));   // droite

            // Cavité nasale
            Path2D nose = new Path2D.Double();
            nose.moveTo(14, 19); nose.lineTo(16, 15.5); nose.lineTo(18, 19);
            nose.closePath();
            g2.fill(nose);

            // Lignes des dents
            g2.setColor(new Color(60, 40, 40));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawLine(12, 19, 12, 27);
            g2.drawLine(16, 19, 16, 29);
            g2.drawLine(20, 19, 20, 27);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Trophée (res/icons/trophee.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class TropheeIcon extends VectorIcon {
        TropheeIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Corps de la coupe
            Path2D cup = new Path2D.Double();
            cup.moveTo(8, 2);   cup.lineTo(24, 2);
            cup.lineTo(21, 19); cup.curveTo(21, 22, 11, 22, 11, 19);
            cup.closePath();

            g2.setColor(new Color(255, 200, 50));
            g2.fill(cup);

            // Reflet
            g2.setColor(new Color(255, 235, 130, 140));
            g2.fill(new Ellipse2D.Double(11, 4, 5, 11));

            // Anses
            g2.setColor(new Color(220, 170, 30));
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Double(2, 5, 9, 11, 90, -180, Arc2D.OPEN));
            g2.draw(new Arc2D.Double(21, 5, 9, 11, 90, 180, Arc2D.OPEN));

            // Contour coupe
            g2.setColor(new Color(180, 140, 10));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(cup);

            // Pied
            g2.setColor(new Color(255, 200, 50));
            g2.fill(new Rectangle2D.Double(14, 21, 4, 5));
            g2.setColor(new Color(180, 140, 10));
            g2.setStroke(new BasicStroke(0.8f));
            g2.draw(new Rectangle2D.Double(14, 21, 4, 5));

            // Base
            g2.setColor(new Color(255, 200, 50));
            g2.fill(new RoundRectangle2D.Double(8, 26, 16, 4.5, 2, 2));
            g2.setColor(new Color(180, 140, 10));
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Double(8, 26, 16, 4.5, 2, 2));

            // Étoile dans la coupe
            g2.setColor(new Color(255, 235, 100));
            g2.setFont(new Font("SansSerif", Font.BOLD, 9));
            g2.drawString("★", 13, 16);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Jouer — cercle vert + triangle play (res/icons/jouer.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class JouerIcon extends VectorIcon {
        JouerIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            g2.setColor(new Color(60, 160, 60));
            g2.fill(new Ellipse2D.Double(1, 1, 30, 30));
            g2.setColor(new Color(30, 100, 30));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new Ellipse2D.Double(1, 1, 30, 30));

            // Triangle play
            Path2D tri = new Path2D.Double();
            tri.moveTo(12, 8); tri.lineTo(26, 16); tri.lineTo(12, 24);
            tri.closePath();
            g2.setColor(Color.WHITE);
            g2.fill(tri);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Nouveau — cercle bleu + croix (res/icons/nouveau.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class NouveauIcon extends VectorIcon {
        NouveauIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            g2.setColor(new Color(50, 130, 220));
            g2.fill(new Ellipse2D.Double(1, 1, 30, 30));
            g2.setColor(new Color(20, 80, 160));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new Ellipse2D.Double(1, 1, 30, 30));

            // Croix +
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(16, 8, 16, 24);
            g2.drawLine(8, 16, 24, 16);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Dossier (res/icons/dossier.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class DossierIcon extends VectorIcon {
        DossierIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Panneau arrière
            g2.setColor(new Color(200, 150, 20));
            g2.fill(new RoundRectangle2D.Double(1, 9, 30, 21, 3, 3));

            // Onglet
            Path2D tab = new Path2D.Double();
            tab.moveTo(1, 9);  tab.lineTo(1, 4);
            tab.curveTo(1, 3, 2, 2, 3, 2);
            tab.lineTo(14, 2);
            tab.curveTo(15, 2, 15, 3, 15, 4);
            tab.lineTo(15, 9); tab.closePath();
            g2.setColor(new Color(220, 170, 40));
            g2.fill(tab);

            // Panneau avant (plus clair)
            g2.setColor(new Color(240, 190, 50));
            g2.fill(new RoundRectangle2D.Double(1, 12, 30, 18, 3, 3));

            // Lignes intérieures (fichiers)
            g2.setColor(new Color(160, 120, 10, 120));
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(5, 18, 27, 18);
            g2.drawLine(5, 22, 22, 22);

            // Contour
            g2.setColor(new Color(160, 120, 10));
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Double(1, 9, 30, 21, 3, 3));
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Sauvegarder — disquette (res/icons/sauvegarder.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class SauvegarderIcon extends VectorIcon {
        SauvegarderIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Corps de la disquette
            Path2D body = new Path2D.Double();
            body.moveTo(2, 2); body.lineTo(24, 2);
            body.lineTo(30, 8); body.lineTo(30, 30);
            body.lineTo(2, 30); body.closePath();

            g2.setColor(new Color(70, 118, 190));
            g2.fill(body);

            // Zone étiquette
            g2.setColor(new Color(220, 230, 255));
            g2.fill(new Rectangle2D.Double(5, 3, 16, 10));

            // Encoche coins
            g2.setColor(new Color(106, 144, 210));
            Path2D notch = new Path2D.Double();
            notch.moveTo(24, 2); notch.lineTo(30, 8); notch.lineTo(24, 8);
            notch.closePath();
            g2.fill(notch);

            // Lignes étiquette
            g2.setColor(new Color(140, 168, 220));
            g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(7, 7, 16, 7);
            g2.drawLine(7, 10, 13, 10);

            // Fenêtre lecture/écriture
            g2.setColor(new Color(30, 60, 140));
            g2.fill(new RoundRectangle2D.Double(6, 18, 20, 10, 2, 2));

            // Volet argenté
            g2.setColor(new Color(160, 180, 222));
            g2.fill(new Rectangle2D.Double(13, 18, 6, 10));

            // Contour
            g2.setColor(new Color(30, 60, 130));
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(body);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Sortie — porte + flèche (res/icons/sortie.svg)
    // ════════════════════════════════════════════════════════════════════════

    private static final class SortieIcon extends VectorIcon {
        SortieIcon(int t) { super(t); }

        @Override
        protected void dessiner(Graphics2D g2) {
            // Cadre de porte
            g2.setColor(new Color(140, 90, 40));
            g2.fill(new RoundRectangle2D.Double(1, 2, 18, 28, 2, 2));

            // Panneau intérieur
            g2.setColor(new Color(180, 120, 60));
            g2.fill(new RoundRectangle2D.Double(3, 4, 14, 24, 2, 2));

            // Poignée
            g2.setColor(new Color(230, 180, 50));
            g2.fill(new Ellipse2D.Double(14, 14, 3.5, 3.5));

            // Contour porte
            g2.setColor(new Color(80, 50, 15));
            g2.setStroke(new BasicStroke(1.0f));
            g2.draw(new RoundRectangle2D.Double(1, 2, 18, 28, 2, 2));

            // Corps de la flèche
            g2.setColor(new Color(200, 50, 50));
            g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(21, 16, 30, 16);

            // Pointe de flèche
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(26, 11); arrow.lineTo(31, 16); arrow.lineTo(26, 21);
            g2.draw(arrow);
        }
    }
}
