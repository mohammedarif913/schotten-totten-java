package projet_java;

import javax.swing.*;
import java.awt.*;

public class CardView extends JPanel {
    private static final Color HIGHLIGHT_COLOR = new Color(255, 215, 0, 100);
    private static final int CORNER_RADIUS = 15;
    private static final int CARD_WIDTH = 55;  
    private static final int CARD_HEIGHT =62; 
    
    private Carte carte;
    private boolean isHighlighted;

    public CardView(Carte carte) {
        this.carte = carte;
        this.isHighlighted = false;
        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);  // Ajoutez cet appel
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rectangle de fond
        g2d.setColor(new Color(54, 57, 63));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (carte != null) {
            // Carte
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, CORNER_RADIUS, CORNER_RADIUS);
            
            g2d.setColor(getColorForCard(carte.getCouleur()));
            g2d.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, CORNER_RADIUS, CORNER_RADIUS);
            
            // Numéro
            drawCardNumber(g2d);
        }
    }

    private void drawCardNumber(Graphics2D g2d) {
        String number = String.valueOf(carte.getNumero());
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(number)) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2d.drawString(number, x, y);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(number, 5, 15);
        g2d.drawString(number, getWidth()-15, getHeight()-5);
    }

    private Color getColorForCard(int colorIndex) {
        return switch(colorIndex) {
            case 0 -> new Color(34, 139, 34);    // Vert forêt
            case 1 -> new Color(0, 0, 139);      // Bleu foncé
            case 2 -> new Color(178, 34, 34);    // Rouge foncé
            case 3 -> new Color(218, 165, 32);   // Jaune doré
            case 4 -> new Color(148, 0, 211);    // Violet
            case 5 -> new Color(139, 69, 19);    // Marron
            default -> Color.GRAY;
        };
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
        repaint();
    }

    public Carte getCarte() {
        return carte;
    }

    public void setHighlighted(boolean highlighted) {
        this.isHighlighted = highlighted;
        repaint();
    }
}