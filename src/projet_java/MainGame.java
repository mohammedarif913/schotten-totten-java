package projet_java;

import javax.swing.*;
import java.awt.*;

public class MainGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Création d'un panneau personnalisé pour le choix du premier joueur
            JPanel firstPlayerPanel = new JPanel();
            firstPlayerPanel.setLayout(new GridLayout(2, 1, 5, 5));
            firstPlayerPanel.add(new JLabel("Choisir le type du Joueur 1:"));
            JComboBox<String> player1Type = new JComboBox<>(new String[]{"Humain", "Robot"});
            firstPlayerPanel.add(player1Type);

            // Demande le type du premier joueur
            int result = JOptionPane.showConfirmDialog(
                null,
                firstPlayerPanel,
                "Configuration du Joueur 1",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String player1Name;
                String player2Name;
                
                // Configuration du Joueur 1
                if (player1Type.getSelectedItem().equals("Robot")) {
                    player1Name = "robot";
                    // Si Joueur 1 est un robot, Joueur 2 doit être humain
                    player2Name = JOptionPane.showInputDialog(
                        null,
                        "Entrer le nom du Joueur 2 (Humain):",
                        "Configuration du Joueur 2",
                        JOptionPane.QUESTION_MESSAGE
                    );
                } else {
                    // Si Joueur 1 est humain, demander son nom
                    player1Name = JOptionPane.showInputDialog(
                        null,
                        "Entrer le nom du Joueur 1:",
                        "Configuration du Joueur 1",
                        JOptionPane.QUESTION_MESSAGE
                    );
                    
                    // Pour le Joueur 2, proposer le choix entre humain et robot
                    Object[] options = {"Humain", "Robot"};
                    int player2Choice = JOptionPane.showOptionDialog(
                        null,
                        "Choisir le type du Joueur 2:",
                        "Configuration du Joueur 2",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    
                    if (player2Choice == 0) {
                        // Joueur 2 est humain
                        player2Name = JOptionPane.showInputDialog(
                            null,
                            "Entrer le nom du Joueur 2:",
                            "Configuration du Joueur 2",
                            JOptionPane.QUESTION_MESSAGE
                        );
                    } else {
                        // Joueur 2 est un robot
                        player2Name = "robot";
                    }
                }
                
                // Vérification que les deux noms ont été fournis
                if (player1Name != null && player2Name != null) {
                    new GameWindow(player1Name, player2Name).setVisible(true);
                }
            }
        });
    }
}