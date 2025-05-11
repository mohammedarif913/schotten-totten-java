package projet_java;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame implements GameObserver {
   private static final Color BACKGROUND_COLOR = new Color(44, 47, 51);
   private static final Color BORDER_COLOR = new Color(114, 137, 218);
   private static final Color TEXT_COLOR = new Color(255, 255, 255);
   private static final Color PANEL_COLOR = new Color(54, 57, 63);
   private static final Color HEADER_COLOR = new Color(35, 39, 42);
   private static final int SPACING = 15;

   private GameController controller;
   private JPanel playerHandPanel;
   private JPanel bornesPanel;
   private JLabel statusLabel;
   private CardView[] playerHandCards;
   private CardView selectedCard;
   private JLabel[] scoreLabels;
   private CardView[][][] borneCards;

   public GameWindow(String player1Name, String player2Name) {
       controller = new GameController(player1Name, player2Name);
       controller.addObserver(this);
       
       initializeWindow();
       initializeComponents(player1Name, player2Name);
       buildLayout();
       
       pack();
       setLocationRelativeTo(null);
       updateDisplay();
   }

   private void initializeWindow() {
       setTitle("Battle Line");
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setLayout(new BorderLayout(SPACING, SPACING));
       getContentPane().setBackground(BACKGROUND_COLOR);
       setMinimumSize(new Dimension(1200, 800));
   }

   private void initializeComponents(String player1Name, String player2Name) {
       scoreLabels = new JLabel[2];
       playerHandCards = new CardView[6];
       borneCards = new CardView[9][2][3];
   }

   private void buildLayout() {
       JPanel headerPanel = createHeaderPanel();
       add(headerPanel, BorderLayout.NORTH);

       bornesPanel = createBornesPanel();
       add(bornesPanel, BorderLayout.CENTER);

       playerHandPanel = createPlayerHandPanel();
       add(playerHandPanel, BorderLayout.SOUTH);

       ((JPanel)getContentPane()).setBorder(
           BorderFactory.createEmptyBorder(SPACING, SPACING, SPACING, SPACING)
       );
   }

   private JPanel createHeaderPanel() {
       JPanel panel = new JPanel();
       panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
       panel.setBackground(HEADER_COLOR);
       panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
       
       scoreLabels[0] = createStylizedLabel(controller.getJoueur1().getNom() + ": 0");
       statusLabel = createStylizedLabel("Tour de " + controller.getJoueur1().getNom());
       scoreLabels[1] = createStylizedLabel(controller.getJoueur2().getNom() + ": 0");

       panel.add(Box.createHorizontalGlue());
       panel.add(scoreLabels[0]); 
       panel.add(Box.createHorizontalGlue());
       panel.add(statusLabel);
       panel.add(Box.createHorizontalGlue());
       panel.add(scoreLabels[1]);
       panel.add(Box.createHorizontalGlue());

       return panel;
   }

   private JLabel createStylizedLabel(String text) {
       JLabel label = new JLabel(text);
       label.setForeground(TEXT_COLOR);
       label.setFont(new Font("Arial", Font.BOLD, 18));
       return label;
   }

   private JPanel createBornesPanel() {
       JPanel panel = new JPanel(new GridLayout(3, 3, SPACING, SPACING));
       panel.setBackground(BACKGROUND_COLOR);
       panel.setOpaque(true);

       for (int i = 0; i < 9; i++) {
           panel.add(createBornePanel(i));
       }

       return panel;
   }
   
   private TitledBorder createBorneBorder(int index) {
       TitledBorder border = BorderFactory.createTitledBorder(
           BorderFactory.createLineBorder(BORDER_COLOR, 2),
           "Borne " + (index + 1),
           TitledBorder.CENTER,
           TitledBorder.DEFAULT_POSITION,
           new Font("Arial", Font.BOLD, 14),
           TEXT_COLOR
       );
       return border;
   }

   private JPanel createBornePanel(int borneIndex) {
       JPanel panel = new JPanel(new GridLayout(2, 1, 5, 20));
       panel.setBackground(PANEL_COLOR);
       panel.setBorder(createBorneBorder(borneIndex));
       
       panel.add(createPlayerCardsPanel(borneIndex, 0));
       panel.add(createPlayerCardsPanel(borneIndex, 1));
       
       panel.addMouseListener(new BornePanelClickListener(borneIndex));
       
       return panel;
   }

   private JPanel createPlayerCardsPanel(int borneIndex, int playerIndex) {
       JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
       panel.setBackground(PANEL_COLOR);
       panel.setPreferredSize(new Dimension(150, 60));

       for (int i = 0; i < 3; i++) {
           CardView cardView = new CardView(null);
           borneCards[borneIndex][playerIndex][i] = cardView;
           panel.add(cardView);
       }
       
       return panel;
   }

   private JPanel createPlayerHandPanel() {
       JPanel panel = new JPanel();
       panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
       panel.setBackground(PANEL_COLOR);
       panel.setBorder(BorderFactory.createTitledBorder(
           BorderFactory.createLineBorder(BORDER_COLOR, 2),
           "Votre Main",
           TitledBorder.CENTER, 
           TitledBorder.DEFAULT_POSITION,
           new Font("Arial", Font.BOLD, 14),
           TEXT_COLOR
       ));

       JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING, SPACING));
       cardsPanel.setBackground(PANEL_COLOR);

       for (int i = 0; i < 6; i++) {
           CardView cardView = new CardView(null);
           cardView.addMouseListener(new CardClickListener(cardView));
           playerHandCards[i] = cardView;
           cardsPanel.add(cardView);
       }

       panel.add(Box.createVerticalStrut(10));
       panel.add(cardsPanel);
       panel.add(Box.createVerticalStrut(10));

       return panel;
   }

   private class BornePanelClickListener extends MouseAdapter {
       private final int borneIndex;

       BornePanelClickListener(int borneIndex) {
           this.borneIndex = borneIndex;
       }

       @Override
       public void mouseClicked(MouseEvent e) {
           if (selectedCard != null) {
               int cardIndex = getSelectedCardIndex();
               if (cardIndex != -1) {
                   controller.playCard(cardIndex, borneIndex);
                   selectedCard.setHighlighted(false);
                   selectedCard = null;
                   updateDisplay();
               }
           }
       }
   }

   private class CardClickListener extends MouseAdapter {
       private final CardView cardView;

       CardClickListener(CardView cardView) {
           this.cardView = cardView;
       }

       @Override
       public void mouseClicked(MouseEvent e) {
           if (selectedCard != null) {
               selectedCard.setHighlighted(false);
           }
           selectedCard = cardView;
           selectedCard.setHighlighted(true);
       }
   }

   private int getSelectedCardIndex() {
       for (int i = 0; i < playerHandCards.length; i++) {
           if (playerHandCards[i] == selectedCard) {
               return i;
           }
       }
       return -1;
   }

   @Override
   public void onCardPlayed(Joueur player, int row, Carte card) {
       int playerIndex = (player == controller.getJoueur1()) ? 0 : 1;
       
       for (int i = 0; i < 3; i++) {
           if (borneCards[row][playerIndex][i].getCarte() == null) {
               borneCards[row][playerIndex][i].setCarte(card);
               break;
           }
       }
       
       SwingUtilities.invokeLater(this::updateDisplay);
   }

   @Override
   public void onScoreUpdated(Joueur player) {
       SwingUtilities.invokeLater(() -> {
           int playerIndex = (player == controller.getJoueur1()) ? 0 : 1;
           scoreLabels[playerIndex].setText(player.getNom() + ": " + player.getScore());
       });
   }

   @Override
   public void onGameWon(Joueur player) {
       SwingUtilities.invokeLater(() -> {
           JOptionPane.showMessageDialog(this,
               player.getNom() + " gagne la partie!",
               "Fin de la partie",
               JOptionPane.INFORMATION_MESSAGE);
           dispose();
       });
   }

// Dans GameWindow.java, modifions la méthode updateDisplay

   private void updateDisplay() {
	    // Mise à jour de l'affichage du plateau
	    for (int borne = 0; borne < 9; borne++) {
	        for (int joueur = 0; joueur < 2; joueur++) {
	            for (int i = 0; i < 3; i++) {
	                Carte carte = controller.getTapis().cartesPosees[borne][joueur][i];
	                if (borneCards[borne][joueur][i] != null) {
	                    borneCards[borne][joueur][i].setCarte(carte);
	                    borneCards[borne][joueur][i].repaint();
	                }
	            }
	        }
	    }

	    // Afficher les cartes du joueur courant si c'est un humain
	    Joueur currentPlayer = controller.getCurrentPlayer();
	    if (!(currentPlayer instanceof JoueurRobot)) {
	        for (int i = 0; i < 6; i++) {
	            if (playerHandCards[i] != null) {
	                playerHandCards[i].setCarte(currentPlayer.cartes[i]);
	                playerHandCards[i].repaint();
	            }
	        }
	    }

	    statusLabel.setText("Tour de : " + currentPlayer.getNom());
	    
	    revalidate();
	    repaint();
	}
}