package projet_java;

import java.util.*;

public class JoueurRobot extends Joueur {
    private static final double THRESHOLD_VICTORY = 0.75;
    private final int playerIndex;

    public JoueurRobot(Tapis tapis, boolean isFirstPlayer) {
        super("Robot", tapis);
        this.playerIndex = isFirstPlayer ? 0 : 1;
    }

    public boolean isValidMove(Move move) {
        if (cartes[move.cardIndex] == null) {
            return false;
        }
        
        int spaceIndex = -1;
        for (int i = 0; i < 3; i++) {
            if (tapis.cartesPosees[move.row][playerIndex][i] == null) {
                spaceIndex = i;
                break;
            }
        }
        
        return spaceIndex != -1;
    }

    public Move calculateBestMove() {
        List<Move> possibleMoves = generateAllPossibleMoves();
        
        if (possibleMoves.isEmpty()) {
            
            return null;
        }
        
        Move bestValidMove = null;
        double bestScore = Double.NEGATIVE_INFINITY;

       
        
        for (Move move : possibleMoves) {
            if (!isValidMove(move)) {
                continue;
            }
            
            try {
                double moveScore = evaluateMove(move);
                
                
                if (moveScore > bestScore) {
                    bestScore = moveScore;
                    bestValidMove = move;
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'évaluation du mouvement: " + e.getMessage());
            }
        }

        if (bestValidMove == null && !possibleMoves.isEmpty()) {
            bestValidMove = possibleMoves.get(0);
        }

        return bestValidMove;
    }

    private boolean hasSpaceInRow(int row) {
        int cardsInRow = 0;
        
        for (int i = 0; i < 3; i++) {
            if (tapis.cartesPosees[row][playerIndex][i] != null) {
                cardsInRow++;
            }
        }
        
        
        
        boolean hasSpace = cardsInRow < 3;
        
        
        
        return hasSpace;
    }

    private List<Move> generateAllPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        
        
        
        for (int cardIndex = 0; cardIndex < 6; cardIndex++) {
            Carte card = cartes[cardIndex];
            if (card == null) continue;
            
            for (int row = 0; row < 9; row++) {
                if (hasSpaceInRow(row)) {
                    moves.add(new Move(cardIndex, card, row));
                    
                }
            }
        }
        
        
        return moves;
    }

    private double evaluateMove(Move move) {
        double score = 0.0;
        
        score += evaluatePotentialCombination(move) * 2.0;
        score += evaluatePosition(move.row) * 1.5;
        score += evaluateBlockingPotential(move) * 1.2;
        score += evaluateRemainingCards(move) * 1.0;
        
        return score;
    }

    private double evaluatePotentialCombination(Move move) {
        Carte[] currentCombo = getCurrentRowCards(move.row);
        List<Carte> potentialCombo = new ArrayList<>();
        
        for (Carte c : currentCombo) {
            if (c != null) potentialCombo.add(c);
        }
        potentialCombo.add(move.card);
        
        double suiteProbability = calculateSuiteProbability(potentialCombo);
        double colorProbability = calculateColorProbability(potentialCombo);
        double brelanProbability = calculateBrelanProbability(potentialCombo);
        
        return Math.max(Math.max(suiteProbability, colorProbability), brelanProbability);
    }

    private double calculateSuiteProbability(List<Carte> combo) {
        if (combo.size() == 3) {
            return isValidSuite(combo) ? 1.0 : 0.0;
        }

        List<Integer> numbers = combo.stream()
            .map(Carte::getNumero)
            .sorted()
            .toList();
        
        if (numbers.size() < 2) return 0.5;
        
        int gaps = numbers.get(numbers.size() - 1) - numbers.get(0) - (numbers.size() - 1);
        int possibleCards = countRemainingCardsInRange(numbers.get(0), numbers.get(numbers.size() - 1));
        
        return gaps == 0 ? 1.0 : (double)possibleCards / countRemainingCards();
    }

    private double calculateColorProbability(List<Carte> combo) {
        if (combo.isEmpty()) return 1.0;
        
        int targetColor = combo.get(0).getCouleur();
        long sameColorCount = combo.stream()
            .filter(c -> c.getCouleur() == targetColor)
            .count();
            
        if (sameColorCount == combo.size()) {
            int remainingNeeded = 3 - combo.size();
            int remainingSameColor = countRemainingCardsOfColor(targetColor);
            return remainingNeeded == 0 ? 1.0 : 
                   (double)remainingSameColor / countRemainingCards();
        }
        
        return 0.0;
    }

    private double calculateBrelanProbability(List<Carte> combo) {
        if (combo.isEmpty()) return 1.0;
        
        Map<Integer, Integer> numberCounts = new HashMap<>();
        combo.forEach(c -> numberCounts.merge(c.getNumero(), 1, Integer::sum));
        
        Optional<Map.Entry<Integer, Integer>> mostCommon = numberCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue());
            
        if (mostCommon.isPresent()) {
            int number = mostCommon.get().getKey();
            int count = mostCommon.get().getValue();
            int remainingNeeded = 3 - count;
            int remainingSameNumber = countRemainingCardsOfNumber(number);
            
            return remainingNeeded == 0 ? 1.0 : 
                   (double)remainingSameNumber / countRemainingCards();
        }
        
        return 0.0;
    }

    private double evaluatePosition(int row) {
        double positionScore = 0.0;
        
        for (int i = Math.max(0, row - 2); i <= Math.min(8, row + 2); i++) {
            if (rangees_gagnes[i] == 1) {
                positionScore += 0.3;
            }
        }
        
        positionScore += (4.0 - Math.abs(4 - row)) / 4.0 * 0.2;
        
        return positionScore;
    }

    private double evaluateBlockingPotential(Move move) {
        Carte[] adversaryCards = getAdversaryRowCards(move.row);
        if (adversaryCards == null || adversaryCards.length == 0) return 0.0;
        
        List<Carte> adversaryCombo = new ArrayList<>();
        for (Carte c : adversaryCards) {
            if (c != null) adversaryCombo.add(c);
        }
        
        if (adversaryCombo.isEmpty()) return 0.0;
        
        double adversaryStrength = Math.max(
            Math.max(
                calculateSuiteProbability(adversaryCombo),
                calculateColorProbability(adversaryCombo)
            ),
            calculateBrelanProbability(adversaryCombo)
        );
        
        return adversaryStrength * 0.8;
    }

    private double evaluateRemainingCards(Move move) {
        int remainingCards = countRemainingCards();
        if (remainingCards == 0) return 0.0;
        
        double handValue = 0.0;
        int cardCount = 0;
        
        for (Carte c : cartes) {
            if (c != null && c != move.card) {
                handValue += evaluateCardStrength(c);
                cardCount++;
            }
        }
        
        return cardCount > 0 ? handValue / cardCount : 0.0;
    }

    private Carte[] getCurrentRowCards(int row) {
        return tapis.cartesPosees[row][playerIndex];
    }

    private Carte[] getAdversaryRowCards(int row) {
        return tapis.cartesPosees[row][playerIndex == 0 ? 1 : 0];
    }

    private int countRemainingCards() {
        return tapis.pioche.size();
    }

    private int countRemainingCardsOfColor(int color) {
        return (int) tapis.pioche.stream()
            .filter(c -> c.getCouleur() == color)
            .count();
    }

    private int countRemainingCardsOfNumber(int number) {
        return (int) tapis.pioche.stream()
            .filter(c -> c.getNumero() == number)
            .count();
    }

    private int countRemainingCardsInRange(int min, int max) {
        return (int) tapis.pioche.stream()
            .filter(c -> c.getNumero() >= min && c.getNumero() <= max)
            .count();
    }

    private double evaluateCardStrength(Carte card) {
        return (card.getNumero() / 9.0) * 0.7 + (Math.min(card.getCouleur(), 5) / 5.0) * 0.3;
    }

    private boolean isValidSuite(List<Carte> cards) {
        if (cards.size() != 3) return false;
        
        List<Integer> numbers = cards.stream()
            .map(Carte::getNumero)
            .sorted()
            .toList();
            
        return numbers.get(1) == numbers.get(0) + 1 && 
               numbers.get(2) == numbers.get(1) + 1;
    }

    @Override
    public void afficherCartes(String[] couleurs) {
        // Méthode volontairement vide pour masquer les cartes du robot
    }
}