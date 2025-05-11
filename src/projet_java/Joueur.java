package projet_java;

import java.util.List;
import java.util.ArrayList;

public class Joueur {
	public String nom;
	public Carte[] cartes; // Main du joueur
	private int score; // Score du joueur
	public int[] rangees_gagnes; // Rangées gagnées
	public Tapis tapis; // Référence au tapis partagé

	// Constructeur
	public Joueur(String nom, Tapis tapis) {
		if (tapis == null) {
			throw new IllegalArgumentException("Le tapis ne peut pas être null.");
		}
		this.nom = nom;
		this.cartes = new Carte[6];
		this.score = 0;
		this.rangees_gagnes = new int[9];
		this.tapis = tapis; // Attribution du tapis partagé
	}

	// Getter pour le nom
	public String getNom() {
		return nom;
	}

	// Getter pour le score
	public int getScore() {
		return score;
	}

	// Incrémentation du score
	public void incrementerScore() {
		this.score++;
	}

	// Méthode pour poser une carte sur le tapis
	public boolean poserCarte(int rangee, Carte carte, int indexJoueur) {
		if (tapis == null || tapis.cartesPosees == null) {
			System.out.println("Erreur : le tapis ou les cartes posées ne sont pas initialisés.");
			return false;
		}

		if (rangee < 0 || rangee >= tapis.cartesPosees.length) {
			System.out.println("Erreur : la rangée doit être entre 0 et " + (tapis.cartesPosees.length - 1) + ".");
			return false;
		}

		// Parcourt les trois emplacements de la rangée pour trouver un emplacement
		// libre
		for (int i = 0; i < 3; i++) {
			if (tapis.cartesPosees[rangee][indexJoueur][i] == null) {
				tapis.cartesPosees[rangee][indexJoueur][i] = carte;
				return true;
			}
		}

		System.out.println("Erreur : tous les emplacements de cette rangée sont occupés.");
		return false;
	}

	// Définir le temps de fin de combinaison dans le tapis
	public void definirTempsFinCombinaison(int rangee, int indexJoueur) {
		if (tapis == null || tapis.tempsFin == null) {
			throw new IllegalStateException("Erreur : le tapis ou le tableau tempsFin n'est pas initialisé.");
		}

		if (rangee < 0 || rangee >= tapis.tempsFin.length) {
			throw new IndexOutOfBoundsException(
					"Erreur : la rangée doit être entre 0 et " + (tapis.tempsFin.length - 1) + ".");
		}

		if (indexJoueur < 0 || indexJoueur >= tapis.tempsFin[rangee].length) {
			throw new IndexOutOfBoundsException(
					"Erreur : l'indice du joueur doit être entre 0 et " + (tapis.tempsFin[rangee].length - 1) + ".");
		}

		// Mettre à jour le temps pour cette rangée et ce joueur
		tapis.tempsFin[rangee][indexJoueur] = System.currentTimeMillis() / 1000.0f;
	}

	// Afficher les cartes du joueur
	public void afficherCartes(String[] couleurs) {
		System.out.println("Cartes de " + nom + ":");
		for (int i = 0; i < cartes.length; i++) {
			Carte carte = cartes[i];
			if (carte != null) {
				System.out.println(i + ": Couleur: " + couleurs[carte.getCouleur()] + ", Numéro: " + carte.getNumero());
			} else {
				System.out.println(i + ": [vide]");
			}
		}
	}

	public boolean RevendiquerBorne(int indexJoueur, int rangee, Carte[][][] cartesPosees, List<Carte> pioche) {
		if (cartesPosees[rangee][indexJoueur][0] == null || cartesPosees[rangee][indexJoueur][1] == null
				|| cartesPosees[rangee][indexJoueur][2] == null) {
			System.out.println("Le joueur n'a pas encore fini sa combinaison");
			return false;
		}

		int vide = 0;
		for (int i = 0; i < 3; i++) {
			if (cartesPosees[rangee][1 - indexJoueur][i] == null) {
				vide++;
			}
		}

		ComparateurCombinaison comparateur = new ComparateurCombinaison();

		// Use temporary `Joueur` objects for simulations
		Tapis simulationTapis = new Tapis();
		simulationTapis.cartesPosees = cloneCartesPosees(cartesPosees); // Copy current state
		simulationTapis.pioche = new ArrayList<>(pioche); // Copy the pioche

		if (vide == 1) {
			for (Carte e : simulationTapis.pioche) {
				// Assign the hypothetical combination
				simulationTapis.cartesPosees[rangee][1 - indexJoueur][2] = e;

				// Create temporary players
				Joueur joueurTemp = createTempJoueur(simulationTapis.cartesPosees[rangee][indexJoueur],
						simulationTapis);
				Joueur adversaireTemp = createTempJoueur(simulationTapis.cartesPosees[rangee][1 - indexJoueur],
						simulationTapis);

				// Compare the combinations
				if (comparateur.comparerCombinaison(joueurTemp, adversaireTemp, rangee) <= 0) {
					return false;
				}
			}
		} else if (vide == 2) {
			for (Carte e1 : simulationTapis.pioche) {
				for (Carte e2 : simulationTapis.pioche) {
					if (e1 != e2) {
						simulationTapis.cartesPosees[rangee][1 - indexJoueur][1] = e1;
						simulationTapis.cartesPosees[rangee][1 - indexJoueur][2] = e2;

						Joueur joueurTemp = createTempJoueur(simulationTapis.cartesPosees[rangee][indexJoueur],
								simulationTapis);
						Joueur adversaireTemp = createTempJoueur(simulationTapis.cartesPosees[rangee][1 - indexJoueur],
								simulationTapis);

						if (comparateur.comparerCombinaison(joueurTemp, adversaireTemp, rangee) <= 0) {
							return false;
						}
					}
				}
			}
		} else if (vide == 3) {
			for (Carte e1 : simulationTapis.pioche) {
				for (Carte e2 : simulationTapis.pioche) {
					for (Carte e3 : simulationTapis.pioche) {
						if (e1 != e2 && e1 != e3 && e2 != e3) {
							simulationTapis.cartesPosees[rangee][1 - indexJoueur][0] = e1;
							simulationTapis.cartesPosees[rangee][1 - indexJoueur][1] = e2;
							simulationTapis.cartesPosees[rangee][1 - indexJoueur][2] = e3;

							Joueur joueurTemp = createTempJoueur(simulationTapis.cartesPosees[rangee][indexJoueur],
									simulationTapis);
							Joueur adversaireTemp = createTempJoueur(
									simulationTapis.cartesPosees[rangee][1 - indexJoueur], simulationTapis);

							if (comparateur.comparerCombinaison(joueurTemp, adversaireTemp, rangee) <= 0) {
								return false;
							}
						}
					}
				}
			}
		}

		this.rangees_gagnes[rangee] = 1;
		incrementerScore();
		return true;
	}

	private Joueur createTempJoueur(Carte[] cartes, Tapis tapis) {
		Joueur tempJoueur = new Joueur("Temp", tapis);
		tempJoueur.cartes = cartes.clone(); // Clone the combination to avoid overwriting
		return tempJoueur;
	}

	private Carte[][][] cloneCartesPosees(Carte[][][] cartesPosees) {
		// Initialize a new array with the same dimensions as the original
		Carte[][][] clone = new Carte[cartesPosees.length][][];
		for (int i = 0; i < cartesPosees.length; i++) {
			clone[i] = new Carte[cartesPosees[i].length][];
			for (int j = 0; j < cartesPosees[i].length; j++) {
				// Clone each row of the array
				if (cartesPosees[i][j] != null) {
					clone[i][j] = cartesPosees[i][j].clone();
				} else {
					clone[i][j] = null;
				}
			}
		}
		return clone;
	}

}