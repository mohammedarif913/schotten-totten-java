package projet_java;

public class ComparateurCombinaison {

	// Méthode pour comparer les combinaisons de trois cartes entre deux joueurs
	// dans une rangée spécifique
	public int comparerCombinaison(Joueur joueur1, Joueur joueur2, int rangee) {
		Tapis tapis = joueur1.tapis;

		int valeurJoueur1 = calculerValeurCombinaison(0, rangee, tapis); // indexJoueur = 0 pour joueur1
		int valeurJoueur2 = calculerValeurCombinaison(1, rangee, tapis); // indexJoueur = 1 pour joueur2

		int sommeJoueur1 = calculerSommeCombinaison(0, rangee, tapis); // indexJoueur = 0 pour joueur1
		int sommeJoueur2 = calculerSommeCombinaison(1, rangee, tapis); // indexJoueur = 1 pour joueur2

		// Compare les valeurs des combinaisons
		if (valeurJoueur1 > valeurJoueur2) {
			joueur1.incrementerScore();
			joueur1.rangees_gagnes[rangee] = 1;
			return 1; // joueur1 gagne
		}
		if (valeurJoueur1 < valeurJoueur2) {
			joueur2.incrementerScore();
			joueur2.rangees_gagnes[rangee] = 1;
			return -1; // joueur2 gagne
		}

		// Si les valeurs sont égales, compare les sommes des numéros
		if (sommeJoueur1 > sommeJoueur2) {
			joueur1.incrementerScore();
			joueur1.rangees_gagnes[rangee] = 1;
			return 1; // joueur1 gagne
		}
		if (sommeJoueur2 > sommeJoueur1) {
			joueur2.incrementerScore();
			joueur2.rangees_gagnes[rangee] = 1;
			return -1; // joueur2 gagne
		}

		// Si les sommes sont également égales, compare le temps de fin de combinaison
		Float tempsJoueur1 = obtenirTempsFinCombinaison(joueur1, rangee, 0);
		Float tempsJoueur2 = obtenirTempsFinCombinaison(joueur2, rangee, 1);

		if (tempsJoueur1 != null && tempsJoueur2 != null) {
			if (tempsJoueur1 > tempsJoueur2) {
				joueur2.incrementerScore();
				joueur2.rangees_gagnes[rangee] = 1;
				return -1; // joueur2 gagne
			}
			joueur1.incrementerScore();
			joueur1.rangees_gagnes[rangee] = 1;
			return 1; // joueur1 gagne
		}

		return 0; // Égalité
	}

	// Méthode pour récupérer le temps de fin de combinaison
	private Float obtenirTempsFinCombinaison(Joueur joueur, int rangee, int indexJoueur) {
		try {
			// Vérifie si le temps est défini pour le joueur
			float temps = joueur.tapis.tempsFin[rangee][indexJoueur];
			return (temps >= 0) ? temps : null;
		} catch (Exception e) {
			return null;
		}
	}

	// Méthode pour calculer la valeur de la combinaison d'un joueur dans une rangée
	// donnée
	private int calculerValeurCombinaison(int indexJoueur, int rangee, Tapis tapis) {
		Carte carte1 = tapis.cartesPosees[rangee][indexJoueur][0];
		Carte carte2 = tapis.cartesPosees[rangee][indexJoueur][1];
		Carte carte3 = tapis.cartesPosees[rangee][indexJoueur][2];

		int valeur = 0;
		if (carte1 == null || carte2 == null || carte3 == null) {
			return valeur;
		}
		if (carte1.getCouleur() == carte2.getCouleur() && carte2.getCouleur() == carte3.getCouleur()) {
			if (carte2.getNumero() == carte1.getNumero() + 1 && carte3.getNumero() == carte2.getNumero() + 1) {
				valeur = 4; // Suite Couleur
			} else {
				valeur = 2; // Couleur
			}
		} else if (carte1.getNumero() == carte2.getNumero() && carte2.getNumero() == carte3.getNumero()) {
			valeur = 3; // Brelan
		} else if (carte2.getNumero() == carte1.getNumero() + 1 && carte3.getNumero() == carte2.getNumero() + 1) {
			valeur = 1; // Suite
		}
		return valeur;
	}

	// Méthode pour calculer la somme des valeurs de la combinaison d'un joueur dans
	// une rangée donnée
	private int calculerSommeCombinaison(int indexJoueur, int rangee, Tapis tapis) {
		int somme = 0;
		for (int i = 0; i < 3; i++) { // Limite à 3 car il y a trois cartes par joueur dans la rangée
			Carte carte = tapis.cartesPosees[rangee][indexJoueur][i];
			if (carte != null) {
				somme += carte.getNumero();
			}
		}
		return somme;
	}
}
