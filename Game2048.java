import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
/** 
 * Game2048
 * @author 吴文瀚 张子瑜 彭小珏
 * @version 11.0.11
 * @see Game2048
 * @see Tile
 * @since 2021-5-31
 */

  public class Game2048 extends JPanel {
  enum State {
    start, running, won, over
  }
  /*enumerate 4 states
  *start is for the beginning page
  *running is for the playing page
  *won is for the page that you have once succeeded in getting score of 2048
  *over is for the page that you can not move any tile any more
  */

  final Color[] colorTable = {
      new Color(0xFFFF00), new Color(0x7FFF00), new Color(0xFFFF00)};
  final static int target = 2048;
  //when the user reach the target he/she would win the game 

  static int highest;
  //find the highest score among all tiles

  static int score;
  //every tile has a score

  static int scores;

  private int Numbers[][];
  // 存放数据的数组
  private int BackUp[][] = new int[4][4];//用于备份数组，供回退时使用
  private int BackUp2[][] = new int[4][4];//用于备份数组，供起死回生时使用
  public JLabel lb;   //分数标签
  int tempscore, tempscore2;//记录回退的分数值
  public JButton bt, about, back;
  public JCheckBox isSoundBox;
  //是否胜利,true:胜利，false：失败
  private boolean isWin = false;
  //是否复活，true：使用复活，false:不使用复活
  private boolean relive = false;
  //是否可以回退，true：不可回退，false：可以回退  (是否已经进行过一次回退了)
  private boolean hasBack = false;
  //是否播放音乐，true:播放音效，false：不播放音效
  private boolean isSound = true;
  //事件
  private ActionEvent e;

  private Color emptyColor = new Color(0xD9D9D9);
  //emptyColor is used for the color of tile

  private Color startColor = new Color(0x32CD32F);
  //startColor is mostly used for the color of the base of the table

  private Color stringColor= new Color(0xFFFFFF);
  /*stringColor is used for the color of some string
  *like "Welcome to Our Greenland !" below
  */

  private Random rand = new Random();
  //prepare for creating a new tile randomly

  private Tile[][] tiles;
  private int side = 5;
  //side determines that the column and row both has 5 tiles each.

  private State gamestate = State.start;
  //everytime the user open the game the first state page is the gamestate,state.start
  private boolean checkingAvailableMoves;
  //to check whether one tile is available to move or not.

  public void ComponentListener( int[][] Numbers, JLabel lb, JButton bt, JButton about, JButton back, JCheckBox isSoundBox) {
    this.Numbers = Numbers;
    this.lb = lb;
    this.bt = bt;
    this.about = about;
    this.back = back;
    this.isSoundBox = isSoundBox;
}

  public Game2048() {
    setPreferredSize(new Dimension(1000, 750));
    setBackground(new Color(0xFFFAF0));
    setFont(new Font("SansSerif", Font.BOLD, 48));
    setFocusable(true);
    //set the color,size and typeface of the Fond

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        startGame();
        //press mouse to start game
        repaint();
      }
    });
    //add mouseListener

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            moveUp();
            break;
          /*define the case VK_UP 
          *when the uses tpye the pgUp
          */  
          case KeyEvent.VK_DOWN:
            moveDown();
            break;
          /*define the case VK_DOWN 
          *when the uses tpye the pgDn
          */   
          case KeyEvent.VK_LEFT:
            moveLeft();
            break;
          /*define the case VK_LEFT 
          *when the uses tpye the Home
          */   
          case KeyEvent.VK_RIGHT:
            moveRight();
            break;
          /*define the case VK_RIGHT 
          *when the uses tpye the End
          */ 
        }

        repaint();

      }
    });
    //add keyListener
  }
  @Override

  //below is about drawing graphic
  public void paintComponent(Graphics gg) {
    super.paintComponent(gg);
    Graphics2D g = (Graphics2D) gg;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g);
  }
  //GUI:draw the GUI Component called g


  void startGame() {
    if (gamestate != State.running) {
      scores = 0;
      highest = 0;
      //when not running the score and highest is fixed as 0
      gamestate = State.running;
      tiles = new Tile[side][side];
      addRandomTile();
      addRandomTile();
      // when start to run there will add two tiles randomly
      for (int i = 0; i < 4; i++){
                  for (int j = 0; j < 4; j++){
                      Numbers[i][j] = 0;
                      //游戏开始，分数为0
                      scores = 0;
                      lb.setText("分数：" + score);
                      //生成4个0-3之间的随机数
                      int r1 = rand.nextInt(4);
                      int r2 = rand.nextInt(4);
                      int c1 = rand.nextInt(4);
                      int c2 = rand.nextInt(4);
                      //由r1,c1;r2,c2组成两个初始值，所以初始值的坐标不能重复
                      while (r1 == r2 && c1 == c2) {
                          r2 = rand.nextInt(4);
                          c2 = rand.nextInt(4);
                      }
                    }
                    }

    }
  }


  void drawGrid(Graphics2D g) {
    g.setColor(emptyColor);
    /*all pages' base color is emptyColor
    *which means gray here
    */
    g.fillRoundRect(200, 100, 615, 615, 15, 15);

    if (gamestate == State.running) {
      g.setColor(startColor);
      /*but when the state is running
      *the running page's base color is startColor
      *which means green here
      */

    g.fillRoundRect(200, 100, 615, 615, 15, 15);
      for (int r = 0; r < side; r++) {
        for (int c = 0; c < side; c++) {
          if (tiles[r][c] == null) {
            g.setColor(emptyColor);
            /*if there is no tile
            *the place will be filled with emptyColor
            */
            g.fillRoundRect(215 + c * 121, 115 + r * 121, 90, 90, 7, 7);
          } else {
            drawTile(g, r, c);
          }//if else
        }//for
      }//for
    } else {
      g.setColor(startColor);
      g.fillRoundRect(215, 115, 585, 585, 7, 7);
      /*In the center of every page except running page
      *use the startColor,the green to cover up the base color.the gray
      *which can lead to the image of graphic with a gray frame
      */
      
      g.setColor(stringColor);
      g.setFont(new Font("Cooper Black", Font.BOLD, 128));
      g.drawString("2048", 355, 270);
      //set fond,size and site of string "2048"

      g.setFont(new Font("华文行楷", Font.ITALIC, 40));
      g.drawString("Welcome to Our Greenland !",361,350);
      //set fond,size and site of string "Welcome to Our Greenland !"

      g.setFont(new Font("SansSerif", Font.ITALIC, 25));
      if (gamestate == State.won) {
        g.drawString("You Made It!", 450, 430);
        /*set fond,size and site of string "You Made It!"
        *when the state is won
        */

      } else if (gamestate == State.over)
        g.drawString("Game Over", 450, 430);
        g.setFont(new Font("华文行楷", Font.ITALIC, 40));
      System.out.println(scores);
        /*set fond,size and site of string "Game Over"
        *when the state is over
        */

      g.setColor(stringColor);
      g.setFont(new Font("SansSerif", Font.ITALIC, 20));
      g.drawString("Click to Start a New Game", 395, 530);
      g.drawString("(use arrow keys to move tiles)", 380, 590);
       /*set fond,size and site of string
       * "Click to Start a New Game" 
       * "(use arrow keys to move tiles)"
       */
    }
  }//method drawGrid


  void drawTile(Graphics2D g, int r, int c) {
    int value = tiles[r][c].getValue();
    //get value for every tile
    g.setColor(colorTable[1]);
    //set color of tile
    g.fillRoundRect(215+ c * 121, 115 + r * 121, 90, 90, 7, 7);
    //set site and size of tile
    String s = String.valueOf(value);
    //let the value 'int' change into value 'string' as s

    g.setColor(value < 128 ? colorTable[0]:colorTable[2]);
    /*considering limited kinds of color in our colorTable
    *this code makes no difference
    *but if you want to add more color
    *you can change color according to the value of tile by changing this
    */

    FontMetrics fm = g.getFontMetrics();
    int asc = fm.getAscent();
    int dec = fm.getDescent();
    int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2;
    int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);
    //set site of s

    g.drawString(s, x, y);
    //draw string s
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
  }//the method of adding a new tile randomly 


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
        //if else
      }//while
    }//for

    if (moved) {
      if (highest < target) {
        clearMerged();
        addRandomTile();
        /*when not reaching the target
        *everng time you merge two tile
        *it will clear one of them and also add new one ramdonly
        */
        if (!movesAvailable()) {
          gamestate = State.over;
        }
        //if all the tiles can not move any more game is over
      } else if (highest == target)
        gamestate = State.won;
        /*when you reach target 2048 you won the game
        * also you can change target score into what you like
        */

    }//if else
    return moved;
  }//method move


  boolean moveUp() {
    return move(0, -1, 0);
  }//method moveUp


  boolean moveDown() {
    return move(side * side - 1, 1, 0);
  }//method moveDown


  boolean moveLeft() {
    return move(0, 0, -1);
  }//method moveLeft


  boolean moveRight() {
    return move(side * side - 1, 0, 1);
  }//method moveRight


  void clearMerged() {
    for (Tile[] row : tiles)
      for (Tile tile : row)
        if (tile != null)
          tile.setMerged(false);
  }//mehod clearMerged


  boolean movesAvailable() {
    checkingAvailableMoves = true;
    boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
    checkingAvailableMoves = false;
    return hasMoves;
  }


  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame f = new JFrame();
      //codes that sets up and shows the frame
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //make the program exit when the Close button is clicked
      f.setTitle("2048");
      //set the title "2048"
      f.setResizable(true);
      f.add(new Game2048(), BorderLayout.CENTER);
      f.pack();
      //Adjust the size of the window
      f.setLocationRelativeTo(null);
      f.setVisible(true);
    });
  }//method main
}//class Game2048


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
    /*to determine whether two tiles can merge together or not 
    *by test whether they have same value
    */
  }
  int mergeWith(Tile other) {
    if (canMergeWith(other)) {
      value *= 2;
      merged = true;
      return value;
      /*if two tiles can merge together
      *the value becomes two times
      */
    }
    return -1;
  }//method mergeWith
}//class Tile