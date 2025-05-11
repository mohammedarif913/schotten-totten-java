Schotten Totten - Java Implementation
A digital implementation of the popular card game Schotten Totten, developed in Java with Swing GUI.
📋 Table of Contents

About
Game Rules
Features
Screenshots
Installation
How to Play
Project Structure
AI Implementation
Future Improvements
Credits

🎮 About
Schotten Totten is a strategic card game originally designed by Reiner Knizia. This Java implementation allows you to play the game on your computer against another player or challenge the AI.
📜 Game Rules
Objective
Win by claiming 5 boundary stones (bornes) or 3 adjacent stones, or have the most stones when the deck is empty.
Setup

54 cards (6 colors, values 1-9 in each color)
9 boundary stones between players
Each player starts with a hand of 6 cards

Gameplay

On your turn, play a card on your side of any boundary stone (max 3 cards per stone per player)
Draw a card to replace it (if possible)
Check if any boundary stones can be claimed
Pass turn to the opponent

Winning Combinations (from strongest to weakest)

Color Run (4 points): 3 consecutive cards of the same color
Three of a Kind (3 points): 3 cards of the same value
Color (2 points): 3 cards of the same color
Run (1 point): 3 consecutive cards of any color
Highest Sum: If combinations are equal, highest sum wins
First Completed: If sums are equal, player who completed their combination first wins

✨ Features

Complete implementation of Schotten Totten rules
Play against another human or AI opponent
Intuitive drag-and-drop interface
Automatic calculation of combinations and winners
Visual feedback for card placement and stone claiming
Automated AI with strategic decision-making

📸 Screenshots
[Screenshots would be added here]
💻 Installation
Requirements

Java JDK 17 or higher
Any operating system that supports Java

Running the Game

Clone this repository
git clone https://github.com/yourusername/schotten-totten-java.git

Navigate to the project directory
cd schotten-totten-java

Compile the source code
javac -d bin src/projet_java/*.java

Run the game
java -cp bin projet_java.MainGame


Alternatively, you can import the project into an IDE such as Eclipse, IntelliJ IDEA, or NetBeans and run it from there.
🎯 How to Play

Start the game and choose player modes (Human vs Human or Human vs AI)
Enter player names when prompted
The game board shows 9 boundary stones in the center
Your hand is displayed at the bottom of the screen
Click on a card in your hand to select it
Click on a boundary stone to place the selected card
The game automatically checks for winning conditions
Continue until a player wins or the deck is empty

🧱 Project Structure
projet_java/
├── Carte.java             # Card representation
├── CardView.java          # Visual card component
├── ComparateurCombinaison.java  # Combination evaluator
├── GameController.java    # Game logic controller
├── GameObserver.java      # Observer interface
├── GameWindow.java        # Main game window
├── Joueur.java            # Player class
├── JoueurRobot.java       # AI player implementation
├── MainGame.java          # Entry point
├── Move.java              # Move representation
└── Tapis.java             # Game board representation
🤖 AI Implementation
The AI player (JoueurRobot.java) uses a strategic evaluation algorithm to determine optimal moves:

Evaluates potential combinations that could be formed
Considers blocking opponent's strong combinations
Prioritizes completing winning patterns
Uses weighted scoring for different aspects of the game state
Makes decisions based on remaining cards and game progress

🚀 Future Improvements

Enhanced AI difficulty levels
Network multiplayer
Game statistics and scoring history
Undo/redo functionality
Custom card deck themes
Tutorial mode for new players

🙏 Credits

Original game concept by Reiner Knizia
Java implementation by [Your Name]
Card graphics inspired by classic card designs


Feel free to contribute to this project by submitting issues or pull requests!
