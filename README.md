# 🎮 Falling 2048

A Java-based puzzle game that combines the classic 2048 tile-merging mechanic with Tetris-style falling gameplay. Built using Java Swing, this project demonstrates real-time game logic, grid-based mechanics, and interactive UI handling.

---

## 🧩 Game Overview

In **Falling 2048**, tiles with values like 2, 4, and 8 fall from the top of the screen.  
Your goal is to **stack and merge matching numbers** to form higher values and increase your score.  
Use arrow keys to control the tile's movement and make strategic merges.

🟫 **Game ends when there's no room to spawn a new tile.**  
⚡ **Each 1000 points = new level + faster tile drops.**

---

## 🎮 Controls

| Key | Action            |
|-----|-------------------|
| ← / A | Move tile left     |
| → / D | Move tile right    |
| ↓ / S | Drop tile instantly |
| R     | Restart the game   |

---

## 🛠️ Built With

- **Java**
- **Java Swing (GUI)**
- **2D Arrays** (Game grid)
- **KeyListener & Timer** (Real-time input and game loop)
- **Custom Drawing** with `Graphics2D`

---

## 💡 Features

- Dynamic tile **falling animation**
- **Auto-merging** logic for horizontal and vertical matches
- **Gravity system** for tile collapse after merge
- **Score and level tracking**
- Color-coded tile values
- Clean and responsive GUI
- Smooth restart and game-over handling

---

## 🧠 Concepts Applied

- Grid-based movement & simulation
- Real-time rendering and input handling
- Recursion-free merge logic using loops
- Game state management
- UI synchronization with backend logic

---

## 📸 Screenshot

<img width="509" height="814" alt="image" src="https://github.com/user-attachments/assets/758f8b43-c536-43dc-b352-f79d2c48f32c" />
---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/falling-2048.git
   cd falling-2048
