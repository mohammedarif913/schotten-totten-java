package projet_java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tapis {
	List<Carte> pioche; // Pioche pour les cartes classiques
	
	public Carte[][][] cartesPosees; // 9 rangées, 2 joueurs, 3 cartes
	public float[][] tempsFin; // 9 rangées, 2 joueurs (temps par joueur)

	public Tapis() {
		// Initialisation de la pioche classique
		pioche = new ArrayList<>();
		for (int couleurIndex = 0; couleurIndex < 6; couleurIndex++) {
			for (int numero = 1; numero <= 9; numero++) {
				pioche.add(new Carte(couleurIndex, numero));
			}
		}
		Collections.shuffle(pioche);


		// Initialisation de cartesPosees
		this.cartesPosees = new Carte[9][2][3]; // 9 rangées, 2 joueurs, 3 cartes

		// Initialisation de tempsFin
		this.tempsFin = new float[9][2]; // 9 rangées, 2 joueurs
		for (int rangee = 0; rangee < 9; rangee++) {
			for (int joueur = 0; joueur < 2; joueur++) {
				this.tempsFin[rangee][joueur] = -1.0f; // Temps par défaut : -1 (aucun temps défini)
			}
		}
	}


	// Méthode pour vérifier si la pioche classique est vide
	public boolean estPiocheVide() {
		return pioche.isEmpty();
	 }

	// Méthode pour piocher une carte classique
	public Carte piocherCarte() {
		if (!estPiocheVide()) {
			return pioche.remove(0);
		}
		System.out.println("Erreur : la pioche classique est vide.");
		return null;
		}
}	
	