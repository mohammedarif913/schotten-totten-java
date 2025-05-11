package projet_java;

public class Move {
    public final int cardIndex;
    public final Carte card;
    public final int row;
    
    public Move(int cardIndex, Carte card, int row) {
        this.cardIndex = cardIndex;
        this.card = card;
        this.row = row;
    }
}