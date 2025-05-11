package projet_java;

public interface GameObserver {
    // Appelé quand une carte est jouée
    void onCardPlayed(Joueur player, int row, Carte card);
    
    // Appelé quand un score est mis à jour
    void onScoreUpdated(Joueur player);
    
    // Appelé quand un joueur gagne la partie
    void onGameWon(Joueur player);
}
