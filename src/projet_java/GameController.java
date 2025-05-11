package projet_java;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;

public class GameController {
    private List<GameObserver> observers = new ArrayList<>();
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur currentPlayer;
    private Tapis tapis;
    private ComparateurCombinaison comparateur;

    public GameController(String player1Name, String player2Name) {
        tapis = new Tapis();
        
        // Création des joueurs en fonction des noms fournis
        if (player1Name.equalsIgnoreCase("robot")) {
            joueur1 = new JoueurRobot(tapis, true);  // true car premier joueur
        } else {
            joueur1 = new Joueur(player1Name, tapis);
        }
        
        if (player2Name.equalsIgnoreCase("robot")) {
            joueur2 = new JoueurRobot(tapis, false); // false car deuxième joueur
        } else {
            joueur2 = new Joueur(player2Name, tapis);
        }
        
        currentPlayer = joueur1;
        comparateur = new ComparateurCombinaison();
        
        // Distribution initiale des cartes
        distributionInitiale();
        
        // Si le joueur 1 est un robot, déclencher son premier tour
        if (currentPlayer instanceof JoueurRobot) {
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500);
                    playRobotTurn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private void distributionInitiale() {
        for (int i = 0; i < 6; i++) {
            joueur1.cartes[i] = tapis.piocherCarte();
            joueur2.cartes[i] = tapis.piocherCarte();
        }
    }

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void playCard(int cardIndex, int row) {
        if (!isValidPlay(cardIndex, row)) {
            
            return;
        }

        Carte card = currentPlayer.cartes[cardIndex];
        int playerIndex = currentPlayer == joueur1 ? 0 : 1;
        
        if (currentPlayer.poserCarte(row, card, playerIndex)) {
            
            if (!tapis.estPiocheVide()) {
                Carte newCard = tapis.piocherCarte();
                currentPlayer.cartes[cardIndex] = newCard;
            } else {
                // Si la pioche est vide, on met juste la carte à null
                currentPlayer.cartes[cardIndex] = null;
            }
            
            notifyCardPlayed(currentPlayer, row, card);
            
            if (isRowComplete(row)) {
                int result = comparateur.comparerCombinaison(joueur1, joueur2, row);
                if (result != 0) {
                    notifyScoreUpdated(result == 1 ? joueur1 : joueur2);
                    if (isGameOver()) {
                        checkVictoryCondition();
                        return;
                    }
                }
            }
            
            switchPlayer();
            
            if (!isGameOver() && currentPlayer instanceof JoueurRobot) {
                SwingUtilities.invokeLater(this::playRobotTurn);
            }
        }
    }

    private boolean isValidPlay(int cardIndex, int row) {
        if (cardIndex < 0 || cardIndex >= 6 || row < 0 || row >= 9) {
            return false;
        }
        
        if (currentPlayer.cartes[cardIndex] == null) {
            return false;
        }
        
        int playerIndex = currentPlayer == joueur1 ? 0 : 1;
        int cardsInRow = 0;
        for (int i = 0; i < 3; i++) {
            if (tapis.cartesPosees[row][playerIndex][i] != null) {
                cardsInRow++;
            }
        }
        
        return cardsInRow < 3;
    }

    private boolean isRowComplete(int row) {
        boolean joueur1Complete = true;
        boolean joueur2Complete = true;
        
        for (int i = 0; i < 3; i++) {
            if (tapis.cartesPosees[row][0][i] == null) joueur1Complete = false;
            if (tapis.cartesPosees[row][1][i] == null) joueur2Complete = false;
        }
        
        return joueur1Complete && joueur2Complete;
    }

    public void playRobotTurn() {
        if (!(currentPlayer instanceof JoueurRobot)) return;

        try {
           
            Thread.sleep(1000); 
            
            JoueurRobot robot = (JoueurRobot) currentPlayer;
            Move bestMove = robot.calculateBestMove();
            
            while (bestMove != null) {
                if (isValidPlay(bestMove.cardIndex, bestMove.row)) {
                    
                    
                    playCard(bestMove.cardIndex, bestMove.row);
                    return; // Le robot joue et finit son tour.
                } else {
                    bestMove = robot.calculateBestMove(); // Cherche un autre coup.
                }
            }
            
            System.out.println("Le robot ne peut pas jouer.");
            if (isGameOver()) {
                endGame();
            } else {
                switchPlayer(); // Passe au joueur suivant si possible.
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == joueur1) ? joueur2 : joueur1;
    }

    private void notifyCardPlayed(Joueur player, int row, Carte card) {
        for (GameObserver observer : observers) {
            observer.onCardPlayed(player, row, card);
        }
    }

    private void notifyScoreUpdated(Joueur player) {
        for (GameObserver observer : observers) {
            observer.onScoreUpdated(player);
        }
    }

    private void checkVictoryCondition() {
        if (isGameOver()) {
            Joueur winner = determineWinner();
            for (GameObserver observer : observers) {
                observer.onGameWon(winner);
            }
        }
    }

    private boolean isGameOver() {
        // Victoire par score ou rangées consécutives
        if (joueur1.getScore() >= 5 || joueur2.getScore() >= 5 ||
            hasThreeConsecutiveRows(joueur1) || hasThreeConsecutiveRows(joueur2)) {
            return true;
        }
        
        // Vérifie si les deux joueurs ne peuvent plus jouer
        boolean joueur1PeutJouer = false;
        boolean joueur2PeutJouer = false;
        
        // Sauvegarde le joueur actuel
        Joueur joueurTemp = currentPlayer;
        
        // Vérifie joueur 1
        currentPlayer = joueur1;
        joueur1PeutJouer = canPlayAnyCard();
        
        // Vérifie joueur 2
        currentPlayer = joueur2;
        joueur2PeutJouer = canPlayAnyCard();
        
        // Restore le joueur actuel
        currentPlayer = joueurTemp;
        
        // La partie est finie si aucun joueur ne peut jouer
        return !joueur1PeutJouer && !joueur2PeutJouer;
    }

    private boolean canPlayAnyCard() {
        // Vérifie si le joueur actuel peut jouer une carte
        boolean hasValidMove = false;
        for (int cardIndex = 0; cardIndex < 6; cardIndex++) {
            if (currentPlayer.cartes[cardIndex] != null) {  // Vérifie que la carte existe
                for (int row = 0; row < 9; row++) {
                    if (isValidPlay(cardIndex, row)) {
                        hasValidMove = true;
                        break;
                    }
                }
            }
        }
        return hasValidMove;
    }

    private Joueur determineWinner() {
        if (hasThreeConsecutiveRows(joueur1)) return joueur1;
        if (hasThreeConsecutiveRows(joueur2)) return joueur2;
        
        if (joueur1.getScore() > joueur2.getScore()) return joueur1;
        if (joueur2.getScore() > joueur1.getScore()) return joueur2;
        
        // En cas d'égalité, le joueur avec le plus de rangées gagnées l'emporte
        int rangees1 = countWonRows(joueur1);
        int rangees2 = countWonRows(joueur2);
        
        if (rangees1 > rangees2) return joueur1;
        if (rangees2 > rangees1) return joueur2;
        
        // Si toujours égalité, retourne joueur1 par défaut
        return joueur1;
    }

    private int countWonRows(Joueur player) {
        int count = 0;
        for (int row = 0; row < 9; row++) {
            if (player.rangees_gagnes[row] == 1) {
                count++;
            }
        }
        return count;
    }

    private boolean hasThreeConsecutiveRows(Joueur player) {
        for (int i = 0; i <= 6; i++) {
            if (player.rangees_gagnes[i] == 1 && 
                player.rangees_gagnes[i + 1] == 1 && 
                player.rangees_gagnes[i + 2] == 1) {
                return true;
            }
        }
        return false;
    }

    public void endGame() {
        // Vérifier d'abord les conditions de victoire
        if (joueur1.getScore() >= 5 || joueur2.getScore() >= 5 ||
            hasThreeConsecutiveRows(joueur1) || hasThreeConsecutiveRows(joueur2)) {
            Joueur winner = determineWinner();
            for (GameObserver observer : observers) {
                observer.onGameWon(winner);
            }
        }
        // Si la pioche est vide mais qu'il reste des coups possibles, 
        // la partie continue
        else if (tapis.estPiocheVide() && canPlayAnyCard()) {
            return; // Continue la partie
        }
        // Sinon fin de partie par blocage
        else {
            Joueur winner = determineWinner();
            for (GameObserver observer : observers) {
                observer.onGameWon(winner);
            }
        }
    }

    public void printBoardState() {
        System.out.println("\n=== État du plateau ===");
        for (int row = 0; row < 9; row++) {
            System.out.println("Rangée " + row + ":");
            for (int joueur = 0; joueur < 2; joueur++) {
                System.out.print("  Joueur " + (joueur + 1) + ": ");
                for (int card = 0; card < 3; card++) {
                    if (tapis.cartesPosees[row][joueur][card] != null) {
                        Carte c = tapis.cartesPosees[row][joueur][card];
                        System.out.print("[" + c.getNumero() + "] ");
                    } else {
                        System.out.print("[ ] ");
                    }
                }
                System.out.println();
            }
        }
    }

    // Getters
    public Joueur getCurrentPlayer() {
        return currentPlayer;
    }

    public Joueur getJoueur1() {
        return joueur1;
    }

    public Joueur getJoueur2() {
        return joueur2;
    }

    public Tapis getTapis() {
        return tapis;
    }
}