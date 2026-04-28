package model;

/**
 * Représente un coup joué : la case (ligne, colonne) choisie par un joueur.
 */
public class Coup {
    private final int ligne;
    private final int colonne;

    public Coup(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }

    public int getLigne() { return ligne; }
    public int getColonne() { return colonne; }

    @Override
    public String toString() {
        return "(" + ligne + ", " + colonne + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Coup)) return false;
        Coup other = (Coup) obj;
        return this.ligne == other.ligne && this.colonne == other.colonne;
    }

    @Override
    public int hashCode() {
        return 31 * ligne + colonne;
    }
}
