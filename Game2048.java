package asd;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
public class Game2048 extends JPanel {
  enum State {
    start, won, running, over
  }
  final Color[] colorTable = {
      new Color(0xFFFF00), new Color(0x7FFF00), new Color(0xFFFF00)};
  final static int target = 2048;
  static int highest;
  static int score;
  private Color gridColor = new Color(0x006400);
  private Color emptyColor = new Color(0xD9D9D9);
  private Color startColor = new Color(0x32CD32F);
  private Color stringColor= new Color(0xFFFFFF);
  private Random rand = new Random();
  private Tile[][] tiles;
  private int side = 5;
  private State gamestate = State.start;
  private boolean checkingAvailableMoves;
  public Game2048() {
    setPreferredSize(new Dimension(1000, 750));
    setBackground(new Color(0xFFFAF0));
    setFont(new Font("SansSerif", Font.BOLD, 48));
    setFocusable(true);
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        startGame();
        repaint();
      }
    });
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            moveUp();
            break;
          case KeyEvent.VK_DOWN:
            moveDown();
            break;
          case KeyEvent.VK_LEFT:
            moveLeft();
            break;
          case KeyEvent.VK_RIGHT:
            moveRight();
            break;
        }
        repaint();
      }
    });
  }
  @Override
  public void paintComponent(Graphics gg) {
    super.paintComponent(gg);
    Graphics2D g = (Graphics2D) gg;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g);
  }
  void startGame() {
    if (gamestate != State.running) {
      score = 0;
      highest = 0;
      gamestate = State.running;
      tiles = new Tile[side][side];
      addRandomTile();
      addRandomTile();
    }
  }
  void drawGrid(Graphics2D g) {
    g.setColor(emptyColor);
    g.fillRoundRect(200, 100, 615, 615, 15, 15);
    if (gamestate == State.running) {
      g.setColor(startColor);
    g.fillRoundRect(200, 100, 615, 615, 15, 15);
      for (int r = 0; r < side; r++) {
        for (int c = 0; c < side; c++) {
          if (tiles[r][c] == null) {
            g.setColor(emptyColor);
            g.fillRoundRect(215 + c * 121, 115 + r * 121, 90, 90, 7, 7);
          } else {
            drawTile(g, r, c);
          }
        }
      }
    } else {
      g.setColor(startColor);
      g.fillRoundRect(215, 115, 585, 585, 7, 7);
      g.setColor(stringColor);
      g.setFont(new Font("Cooper Black", Font.BOLD, 128));
      g.drawString("2048", 355, 270);
      g.setFont(new Font("»ªÎÄÐÐ¿¬", Font.ITALIC, 40));
      g.drawString("welcome to my greenland",361,350);
      g.setFont(new Font("SansSerif", Font.ITALIC, 25));
      if (gamestate == State.won) {
        g.drawString("you made it!", 450, 430);
      } else if (gamestate == State.over)
        g.drawString("Game Over", 450, 430);
      g.setColor(stringColor);
      g.setFont(new Font("SansSerif", Font.ITALIC, 20));
      g.drawString("Click to Start a New Game", 395, 530);
      g.drawString("(use arrow keys to move tiles)", 380, 590);
    }
  }
  void drawTile(Graphics2D g, int r, int c) {
    int value = tiles[r][c].getValue();
    g.setColor(colorTable[1]);
    g.fillRoundRect(215+ c * 121, 115 + r * 121, 90, 90, 7, 7);
    String s = String.valueOf(value);
    g.setColor(value < 128 ? colorTable[0]:colorTable[2]);
    FontMetrics fm = g.getFontMetrics();
    int asc = fm.getAscent();
    int dec = fm.getDescent();
    int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2;
    int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);
    g.drawString(s, x, y);
  }

 private void addRandomTile() {
    int pos = rand.nextInt(side * side);
    int row, col;
    do {
      pos = (pos + 1) % (side * side);
      row = pos / side;
      col = pos % side;
    } while (tiles[row][col] != null);
    int val = rand.nextInt(10) == 0 ? 4 : 2;
    tiles[row][col] = new Tile(val);
  }
  private boolean move(int countDownFrom, int yIncr, int xIncr) {
    boolean moved = false;
    for (int i = 0; i < side * side; i++) {
      int j = Math.abs(countDownFrom - i);
      int r = j / side;
      int c = j % side;
      if (tiles[r][c] == null)
        continue;
      int nextR = r + yIncr;
      int nextC = c + xIncr;
      while (nextR >= 0 && nextR < side && nextC >= 0 && nextC < side) {
        Tile next = tiles[nextR][nextC];
        Tile curr = tiles[r][c];
        if (next == null) {
          if (checkingAvailableMoves)
            return true;
          tiles[nextR][nextC] = curr;
          tiles[r][c] = null;
          r = nextR;
          c = nextC;
          nextR += yIncr;
          nextC += xIncr;
          moved = true;
        } else if (next.canMergeWith(curr)) {
          if (checkingAvailableMoves)
            return true;
          int value = next.mergeWith(curr);
          if (value > highest)
            highest = value;
          score += value;
          tiles[r][c] = null;
          moved = true;
          break;
        } else
          break;
      }
    }
    if (moved) {
      if (highest < target) {
        clearMerged();
        addRandomTile();
        if (!movesAvailable()) {
          gamestate = State.over;
        }
      } else if (highest == target)
        gamestate = State.won;
    }
    return moved;
  }
  boolean moveUp() {
    return move(0, -1, 0);
  }
  boolean moveDown() {
    return move(side * side - 1, 1, 0);
  }
  boolean moveLeft() {
    return move(0, 0, -1);
  }
  boolean moveRight() {
    return move(side * side - 1, 0, 1);
  }
  void clearMerged() {
    for (Tile[] row : tiles)
      for (Tile tile : row)
        if (tile != null)
          tile.setMerged(false);
  }
  boolean movesAvailable() {
    checkingAvailableMoves = true;
    boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
    checkingAvailableMoves = false;
    return hasMoves;
  }
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.setTitle("2048");
      f.setResizable(true);
      f.add(new Game2048(), BorderLayout.CENTER);
      f.pack();
      f.setLocationRelativeTo(null);
      f.setVisible(true);
    });
  }
}
class Tile {
  private boolean merged;
  private int value;
  Tile(int val) {
    value = val;
  }
  int getValue() {
    return value;
  }
  void setMerged(boolean m) {
    merged = m;
  }
  boolean canMergeWith(Tile other) {
    return !merged && other != null && !other.merged && value == other.getValue();
  }
  int mergeWith(Tile other) {
    if (canMergeWith(other)) {
      value *= 2;
      merged = true;
      return value;
    }
    return -1;
  }
}