package projet_java;

public class Carte {
	public int couleur; // Indice de la couleur dans le tableau de couleurs
	public int numero;

	public Carte(int couleur, int numero) {
		// Vérifie que la couleur est entre 0 et 5
		if (couleur < 0 || couleur > 5) {
			throw new IllegalArgumentException("La couleur doit être entre 0 et 5");
		}
		// Vérifie que le numéro est entre 1 et 9
		if (numero < 1 || numero > 9) {
			throw new IllegalArgumentException("Le numéro doit être entre 1 et 9");
		}

		this.couleur = couleur;
		this.numero = numero;
	}

	public int getCouleur() {
		return couleur;
	}

	public int getNumero() {
		return numero;
	}
}