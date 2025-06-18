import tester.Tester;
import java.awt.Color;
import java.util.*;
import javalib.impworld.*;
import javalib.worldimages.*;

// represents world constants for the game
interface IWorldConstants {
  // Cell sizes
  int DEFAULT_CELL_SIZE = 30;
  int MIN_CELL_SIZE = 15;

  // Board dimensions
  int BOARD_PADDING = 50;
  int MAX_BOARD_WIDTH = 1200;
  int MAX_BOARD_HEIGHT = 700;

  // Menu dimensions
  int MENU_WIDTH = 1200;
  int MENU_HEIGHT = 900;

  // UI dimensions
  int BUTTON_WIDTH = 200;
  int BUTTON_HEIGHT = 50;
  int SLIDER_WIDTH = 300;
  int SLIDER_HEIGHT = 10;
  int UI_SPACING = 70;
  int TITLE_Y = 100;

  // Cell colors
  Color HIDDEN_COLOR = new Color(189, 189, 189);
  Color REVEALED_COLOR = new Color(215, 215, 215);
  Color MINE_COLOR = Color.BLACK;
  Color MINE_RED = new Color(255, 0, 0);
  Color FLAG_COLOR = Color.RED;
  Color FLAG_POLE = Color.BLACK;

  // Number colors (classic minesweeper)
  ArrayList<Color> numberColors = new ArrayList<>(Arrays.asList(
      null,
      new Color(0, 0, 255),
      new Color(0, 128, 0),
      new Color(255, 0, 0),
      new Color(0, 0, 128),
      new Color(128, 0, 0),
      new Color(0, 128, 128),
      new Color(0, 0, 0),
      new Color(128, 128, 128)));

  // UI colors
  Color BUTTON_COLOR = new Color(70, 130, 180);
  Color BUTTON_TEXT = Color.WHITE;
  Color SLIDER_TRACK = new Color(200, 200, 200);
  Color SLIDER_THUMB = new Color(70, 130, 180);
  Color BACKGROUND = new Color(240, 240, 240);

  // Difficulty presets
  int EASY_WIDTH = 9;
  int EASY_HEIGHT = 9;
  int EASY_MINES = 10;

  int MEDIUM_WIDTH = 16;
  int MEDIUM_HEIGHT = 16;
  int MEDIUM_MINES = 40;

  int HARD_WIDTH = 30;
  int HARD_HEIGHT = 16;
  int HARD_MINES = 99;

  Random R = new Random(123);

  // Game states
  String MENU = "MENU";
  String CUSTOM_SETUP = "CUSTOM_SETUP";
  String PLAYING = "PLAYING";
  String GAME_OVER = "GAME_OVER";
}

// represents the game board
class Board implements IWorldConstants {
  int width;
  int height;
  int mineCount;
  int cellsRevealed;
  int totalSafeCells;
  int cellSize;
  int offsetX;
  int offsetY;
  ArrayList<ArrayList<Cell>> cells;
  MineSweeper game;

  // the constructor initializes the board with given dimensions and mine count
  Board(int width, int height, int mineCount,
      MineSweeper game, int cellSize, int offsetX, int offsetY) {
    this.width = width;
    this.height = height;
    this.mineCount = mineCount;
    this.cellsRevealed = 0;
    this.totalSafeCells = width * height - mineCount;
    this.cellSize = cellSize;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.cells = new ArrayList<>();
    this.game = game;
    initializeBoard();
  }

  // initializes the board with cells and mines
  // EFFECT: Creates cells, links neighbors, places mines, and counts adjacent
  // mines
  void initializeBoard() {
    // Create cells
    for (int row = 0; row < height; row++) {
      ArrayList<Cell> rowCells = new ArrayList<>();
      for (int col = 0; col < width; col++) {
        rowCells.add(new Cell(col, row, this, cellSize, offsetX, offsetY));
      }
      cells.add(rowCells);
    }

    // Link neighbors
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        Cell cell = cells.get(row).get(col);
        linkNeighbors(cell, row, col);
      }
    }

    // Place mines
    ArrayList<Cell> allCells = new ArrayList<>();
    for (ArrayList<Cell> row : cells) {
      allCells.addAll(row);
    }

    Collections.shuffle(allCells, R);
    for (int i = 0; i < mineCount && i < allCells.size(); i++) {
      allCells.get(i).placeMine();
    }

    // Count adjacent mines
    for (ArrayList<Cell> row : cells) {
      for (Cell cell : row) {
        cell.countAdjacentMines();
      }
    }
  }

  // links a cell to its neighbors
  // EFFECT: Adds neighboring cells to the cell's neighbor list
  void linkNeighbors(Cell cell, int row, int col) {
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) {
          continue;
        }
        int newRow = row + dr;
        int newCol = col + dc;
        if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
          cell.addNeighbor(cells.get(newRow).get(newCol));
        }
      }
    }
  }

  // handles click on board
  // EFFECT: Reveals a cell or toggles flag based on mouse click
  void handleClick(Posn pos, String button) {
    int col = (pos.x - offsetX) / cellSize;
    int row = (pos.y - offsetY) / cellSize;

    if (row >= 0 && row < height && col >= 0 && col < width) {
      if (button.equals("LeftButton")) {
        cells.get(row).get(col).reveal();
      } else if (button.equals("RightButton")) {
        cells.get(row).get(col).toggleFlag();
      }
    }
  }

  // reveals all cells (for game over)
  // EFFECT: Reveals all cells, forcing mines to show
  void revealAll() {
    for (ArrayList<Cell> row : cells) {
      for (Cell cell : row) {
        cell.forceReveal();
      }
    }
  }

  // called when a cell is revealed
  // EFFECT: Updates game state based on revealed cell
  void onCellRevealed(boolean wasMine) {
    if (wasMine) {
      game.endGame(false);
    } else {
      cellsRevealed++;
      if (cellsRevealed >= totalSafeCells) {
        game.endGame(true);
      }
    }
  }

  // draws the board onto the scene
  // EFFECT: Draws the board background and all cells
  void drawOnto(WorldScene scene) {
    int boardWidth = width * cellSize;
    int boardHeight = height * cellSize;
    WorldImage boardBorder = new RectangleImage(boardWidth + 4, boardHeight + 4,
        OutlineMode.SOLID, Color.DARK_GRAY);
    WorldImage boardBg = new RectangleImage(boardWidth, boardHeight,
        OutlineMode.SOLID, Color.WHITE);
    scene.placeImageXY(boardBorder, offsetX + boardWidth / 2, offsetY + boardHeight / 2);
    scene.placeImageXY(boardBg, offsetX + boardWidth / 2, offsetY + boardHeight / 2);

    // Draw cells
    for (ArrayList<Cell> row : cells) {
      for (Cell cell : row) {
        cell.drawOnto(scene);
      }
    }
  }

  // flags a random unflagged mine
  // EFFECT: Flags a random mine that is not already flagged
  void flagRandomMine() {
    ArrayList<Cell> unflaggedMines = new ArrayList<>();

    // Collect all unflagged mines
    for (ArrayList<Cell> row : cells) {
      for (Cell cell : row) {
        cell.addToListIfUnflaggedMine(unflaggedMines);
      }
    }

    // Flag a random one if any exist
    if (!unflaggedMines.isEmpty()) {
      int randomIndex = R.nextInt(unflaggedMines.size());
      unflaggedMines.get(randomIndex).setFlag();
    }
  }

}

// represents a single cell in the game
class Cell implements IWorldConstants {
  int x;
  int y;
  boolean isMine;
  boolean isRevealed;
  boolean isFlagged;
  int adjacentMines;
  ArrayList<Cell> neighbors;
  Board board;
  int cellSize;
  int offsetX;
  int offsetY;

  // the constructor
  Cell(int x, int y, Board board, int cellSize, int offsetX, int offsetY) {
    this.x = x;
    this.y = y;
    this.board = board;
    this.cellSize = cellSize;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.isMine = false;
    this.isRevealed = false;
    this.isFlagged = false;
    this.adjacentMines = 0;
    this.neighbors = new ArrayList<>();
  }

  // adds a neighbor to this cell
  // EFFECT: Adds a neighboring cell to this cell's neighbor list
  void addNeighbor(Cell neighbor) {
    this.neighbors.add(neighbor);
  }

  // places a mine in this cell
  // EFFECT: Marks this cell as a mine
  void placeMine() {
    this.isMine = true;
  }

  // counts adjacent mines
  // EFFECT: Increments adjacent mine count for each neighboring mine
  void countAdjacentMines() {
    if (!isMine) {
      for (Cell neighbor : neighbors) {
        neighbor.addToCountIfMine(this);
      }
    }
  }

  // helper for double dispatch pattern
  // EFFECT: Increments the adjacent mine count of the asking cell if this cell is
  // a mine
  void addToCountIfMine(Cell asking) {
    if (this.isMine) {
      asking.incrementAdjacentMines();
    }
  }

  // increments adjacent mine count
  // EFFECT: Increments the count of adjacent mines for this cell
  void incrementAdjacentMines() {
    this.adjacentMines++;
  }

  // reveals this cell
  // EFFECT: Reveals the cell, updates game state, and performs flood fill if
  // necessary
  void reveal() {
    if (!isRevealed && !isFlagged) {
      isRevealed = true;
      board.onCellRevealed(isMine);

      if (!isMine && adjacentMines == 0) {
        // Flood fill
        for (Cell neighbor : neighbors) {
          neighbor.reveal();
        }
      }
    }
  }

  // force reveals (for game over)
  // EFFECT: Forces this cell to be revealed regardless of its current state
  void forceReveal() {
    isRevealed = true;
  }

  // toggles flag on this cell
  // EFFECT: Toggles the flagged state of this cell if it is not revealed
  void toggleFlag() {
    if (!isRevealed) {
      isFlagged = !isFlagged;
    }
  }

  // draws this cell onto the scene
  // EFFECT: Draws the cell with appropriate graphics based on its state
  void drawOnto(WorldScene scene) {
    int centerX = offsetX + x * cellSize + cellSize / 2;
    int centerY = offsetY + y * cellSize + cellSize / 2;

    WorldImage cellImage;
    int innerSize = cellSize - 2;

    if (!isRevealed) {
      // Simple 3D effect using overlaid rectangles
      cellImage = new RectangleImage(innerSize, innerSize,
          OutlineMode.SOLID, HIDDEN_COLOR);

      // Add highlight on top-left
      WorldImage highlight = new RectangleImage(innerSize - 4, 2,
          OutlineMode.SOLID, new Color(220, 220, 220));
      WorldImage leftHighlight = new RectangleImage(2, innerSize - 4,
          OutlineMode.SOLID, new Color(220, 220, 220));

      // Add shadow on bottom-right
      WorldImage bottomShadow = new RectangleImage(innerSize - 4, 2,
          OutlineMode.SOLID, new Color(120, 120, 120));
      WorldImage rightShadow = new RectangleImage(2, innerSize - 4,
          OutlineMode.SOLID, new Color(120, 120, 120));

      // Place highlights and shadows
      cellImage = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          highlight, 2, 2, cellImage);
      cellImage = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
          leftHighlight, 2, 2, cellImage);
      cellImage = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.BOTTOM,
          bottomShadow, -2, -2, cellImage);
      cellImage = new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.BOTTOM,
          rightShadow, -2, -2, cellImage);

      if (isFlagged) {
        // Flag graphic
        WorldImage flagPole = new RectangleImage(2, cellSize / 3, OutlineMode.SOLID, FLAG_POLE);
        WorldImage flagCloth = new TriangleImage(
            new Posn(0, 0),
            new Posn(cellSize / 4, cellSize / 8),
            new Posn(0, cellSize / 4),
            OutlineMode.SOLID, FLAG_COLOR);
        WorldImage flag = new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP,
            flagCloth, 1, 0, flagPole);
        cellImage = new OverlayImage(flag, cellImage);
      }
    } else {
      // Revealed cells - flat appearance
      cellImage = new RectangleImage(innerSize, innerSize,
          OutlineMode.SOLID, REVEALED_COLOR);

      if (isMine) {
        // Mine graphic
        WorldImage mineBody = new CircleImage(cellSize / 3, OutlineMode.SOLID, MINE_COLOR);
        WorldImage spike1 = new RectangleImage(cellSize / 2, 2, OutlineMode.SOLID, MINE_COLOR);
        WorldImage spike2 = new RotateImage(spike1, 45);
        WorldImage spike3 = new RotateImage(spike1, 90);
        WorldImage spike4 = new RotateImage(spike1, 135);

        WorldImage mine = new OverlayImage(spike1,
            new OverlayImage(spike2,
                new OverlayImage(spike3,
                    new OverlayImage(spike4, mineBody))));

        // Red background for triggered mine
        if (board.game.state.equals(GAME_OVER) && !board.game.wonGame) {
          cellImage = new RectangleImage(innerSize, innerSize,
              OutlineMode.SOLID, MINE_RED);
        }

        cellImage = new OverlayImage(mine, cellImage);
      } else if (adjacentMines > 0) {
        // Colored numbers
        int fontSize = Math.max(cellSize * 2 / 3, 12);
        Color numColor;
        if (adjacentMines < numberColors.size() && numberColors.get(adjacentMines) != null) {
          numColor = numberColors.get(adjacentMines);
        } else {
          numColor = Color.BLACK;
        }
        WorldImage number = new TextImage(String.valueOf(adjacentMines),
            fontSize, FontStyle.BOLD, numColor);
        cellImage = new OverlayImage(number, cellImage);
      }
    }

    // Add consistent border for all cells
    WorldImage border = new RectangleImage(innerSize, innerSize,
        OutlineMode.OUTLINE, Color.DARK_GRAY);
    cellImage = new OverlayImage(border, cellImage);

    scene.placeImageXY(cellImage, centerX, centerY);
  }

  void addToListIfUnflaggedMine(ArrayList<Cell> list) {
    if (isMine && !isFlagged && !isRevealed) {
      list.add(this);
    }
  }

  // sets flag on this cell (for hint system)
  void setFlag() {
    if (!isRevealed) {
      isFlagged = true;
    }
  }

}

// represents the minesweeper game world
class MineSweeper extends World implements IWorldConstants {
  String state;
  Board board;
  int worldWidth;
  int worldHeight;
  int cellSize;
  int boardOffsetX;
  int boardOffsetY;

  // Custom game settings
  Slider widthSlider;
  Slider heightSlider;
  Slider mineSlider;

  // Button positions for menu
  Button easyButton;
  Button mediumButton;
  Button hardButton;
  Button customButton;
  Button playButton;
  Button backButton;
  Button menuButton;
  Button hintButton; // NEW: hint button

  // Game over message
  boolean wonGame;

  // the constructor
  MineSweeper() {
    this.state = MENU;
    this.worldWidth = MENU_WIDTH;
    this.worldHeight = MENU_HEIGHT;
    this.cellSize = DEFAULT_CELL_SIZE;
    initializeMenu();
  }

  // calculates appropriate dimensions for the board
  // EFFECT: Sets cell size, world dimensions, and offsets based on board size
  void calculateDimensions(int cols, int rows) {
    // Start with default cell size
    cellSize = DEFAULT_CELL_SIZE;

    // Calculate board dimensions with default cell size
    int boardWidth = cols * cellSize;
    int boardHeight = rows * cellSize;

    // If board is too large, scale down the cell size
    if (boardWidth > MAX_BOARD_WIDTH || boardHeight > MAX_BOARD_HEIGHT) {
      double widthRatio = MAX_BOARD_WIDTH / (double) boardWidth;
      double heightRatio = MAX_BOARD_HEIGHT / (double) boardHeight;
      double ratio = Math.min(widthRatio, heightRatio);
      cellSize = Math.max(MIN_CELL_SIZE, (int) (cellSize * ratio));

      // Recalculate board dimensions
      boardWidth = cols * cellSize;
      boardHeight = rows * cellSize;
    }

    // Set world dimensions with padding
    worldWidth = boardWidth + 2 * BOARD_PADDING;
    worldHeight = boardHeight + 2 * BOARD_PADDING + 100; // Extra space for UI

    // Calculate offsets to center the board
    boardOffsetX = BOARD_PADDING;
    boardOffsetY = BOARD_PADDING;
  }

  // creates the world scene
  public WorldScene makeScene() {
    WorldScene scene = new WorldScene(worldWidth, worldHeight);

    if (state.equals(MENU)) {
      drawMenu(scene);
    } else if (state.equals(CUSTOM_SETUP)) {
      drawCustomSetup(scene);
    } else if (state.equals(PLAYING) || state.equals(GAME_OVER)) {
      // Draw background
      WorldImage bg = new RectangleImage(worldWidth, worldHeight,
          OutlineMode.SOLID, BACKGROUND);
      scene.placeImageXY(bg, worldWidth / 2, worldHeight / 2);

      board.drawOnto(scene);

      // Draw game info
      drawGameInfo(scene);

      // Draw hint button during play
      if (state.equals(PLAYING)) {
        drawHintButton(scene);
      }

      if (state.equals(GAME_OVER)) {
        drawGameOverOverlay(scene);
      }
    }

    return scene;
  }

  // initializes menu buttons
  // EFFECT: Creates buttons for easy, medium, hard, and custom game modes
  void initializeMenu() {
    int centerX = MENU_WIDTH / 2;
    int startY = 200;

    easyButton = new Button(centerX,
        startY, BUTTON_WIDTH, BUTTON_HEIGHT, "Easy (9x9, 10 mines)");
    mediumButton = new Button(centerX,
        startY + UI_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT, "Medium (16x16, 40 mines)");
    hardButton = new Button(centerX,
        startY + UI_SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT, "Hard (30x16, 99 mines)");
    customButton = new Button(centerX,
        startY + UI_SPACING * 3, BUTTON_WIDTH, BUTTON_HEIGHT, "Custom");
  }

  // initializes custom setup screen
  // EFFECT: Creates sliders and buttons for custom game setup
  void initializeCustomSetup() {
    int centerX = MENU_WIDTH / 2;

    widthSlider = new Slider(centerX, 200, SLIDER_WIDTH, 0, 50, 15, "Width");
    heightSlider = new Slider(centerX, 280, SLIDER_WIDTH, 5, 40, 15, "Height");
    mineSlider = new Slider(centerX, 360, SLIDER_WIDTH, 10, 500, 50, "Mines");
    playButton = new Button(centerX, 450, 150, BUTTON_HEIGHT, "Play");
    backButton = new Button(centerX, 520, 150, BUTTON_HEIGHT, "Back");
  }

  // draws game info panel
  // EFFECT: Draws the game info panel with mine count and board dimensions
  void drawGameInfo(WorldScene scene) {
    String info = "Mines: " + board.mineCount + " | Cells: " + board.width + "x" + board.height;
    WorldImage infoText = new TextImage(info, 16, FontStyle.BOLD, Color.BLACK);
    WorldImage infoBg = new RectangleImage(200, 30,
        OutlineMode.SOLID, new Color(255, 255, 255, 200));
    WorldImage infoPanel = new OverlayImage(infoText, infoBg);
    scene.placeImageXY(infoPanel, worldWidth / 2, worldHeight - 60);
  }

  // draws hint button
  // EFFECT: Creates a hint button that flags a random mine when clicked
  void drawHintButton(WorldScene scene) {
    hintButton = new Button(worldWidth / 2, worldHeight - 25, 100, 30, "Hint");
    hintButton.drawOnto(scene);
  }

  // draws menu screen
  // EFFECT: Draws the main menu with title and difficulty buttons
  void drawMenu(WorldScene scene) {
    // Background
    WorldImage bg = new RectangleImage(worldWidth, worldHeight, OutlineMode.SOLID, BACKGROUND);
    scene.placeImageXY(bg, worldWidth / 2, worldHeight / 2);

    // Title with shadow effect
    WorldImage titleShadow = new TextImage("MINESWEEPER",
        48, FontStyle.BOLD, new Color(100, 100, 100));
    WorldImage title = new TextImage("MINESWEEPER", 48, FontStyle.BOLD, Color.BLACK);
    scene.placeImageXY(titleShadow, MENU_WIDTH / 2 + 3, TITLE_Y + 3);
    scene.placeImageXY(title, MENU_WIDTH / 2, TITLE_Y);

    easyButton.drawOnto(scene);
    mediumButton.drawOnto(scene);
    hardButton.drawOnto(scene);
    customButton.drawOnto(scene);
  }

  // draws custom setup screen
  // EFFECT: Draws the custom game setup screen with sliders and buttons
  void drawCustomSetup(WorldScene scene) {
    // Background
    WorldImage bg = new RectangleImage(worldWidth, worldHeight, OutlineMode.SOLID, BACKGROUND);
    scene.placeImageXY(bg, worldWidth / 2, worldHeight / 2);

    WorldImage title = new TextImage("Custom Game Setup", 36, FontStyle.BOLD, Color.BLACK);
    scene.placeImageXY(title, MENU_WIDTH / 2, TITLE_Y);

    widthSlider.drawOnto(scene);
    heightSlider.drawOnto(scene);

    // Update mine slider max based on current dimensions
    int maxMines = widthSlider.getValue() * heightSlider.getValue() - 1;
    mineSlider.setMax(maxMines);
    mineSlider.drawOnto(scene);

    playButton.drawOnto(scene);
    backButton.drawOnto(scene);
  }

  // draws game over overlay
  // EFFECT: Draws a semi-transparent overlay with a message and button
  void drawGameOverOverlay(WorldScene scene) {
    // Semi-transparent overlay
    WorldImage overlay = new RectangleImage(worldWidth, worldHeight,
        OutlineMode.SOLID, new Color(0, 0, 0, 150));
    scene.placeImageXY(overlay, worldWidth / 2, worldHeight / 2);

    // Message with glow effect
    String message;
    Color msgColor;
    Color glowColor;

    if (wonGame) {
      message = "You Win!";
      msgColor = Color.GREEN;
      glowColor = new Color(0, 255, 0, 100);
    } else {
      message = "Game Over!";
      msgColor = Color.RED;
      glowColor = new Color(255, 0, 0, 100);
    }

    WorldImage textGlow = new TextImage(message, 66, FontStyle.BOLD, glowColor);
    WorldImage text = new TextImage(message, 64, FontStyle.BOLD, msgColor);
    scene.placeImageXY(textGlow, worldWidth / 2, worldHeight / 2 - 50);
    scene.placeImageXY(text, worldWidth / 2, worldHeight / 2 - 50);

    menuButton = new Button(worldWidth / 2, worldHeight / 2 + 50,
        BUTTON_WIDTH, BUTTON_HEIGHT, "Back to Menu");
    menuButton.drawOnto(scene);
  }

  // handles mouse clicks
  // EFFECT: Handles clicks based on the current game state
  public void onMouseClicked(Posn pos, String button) {
    if (state.equals(MENU)) {
      handleMenuClick(pos);
    } else if (state.equals(CUSTOM_SETUP)) {
      handleCustomClick(pos);
    } else if (state.equals(PLAYING)) {
      handleGameClick(pos, button);
    } else if (state.equals(GAME_OVER)) {
      handleGameOverClick(pos);
    }
  }

  // handles menu clicks
  // EFFECT: Starts a new game based on the selected difficulty or opens custom
  // setup
  void handleMenuClick(Posn pos) {
    if (easyButton.contains(pos)) {
      startGame(EASY_WIDTH, EASY_HEIGHT, EASY_MINES);
    } else if (mediumButton.contains(pos)) {
      startGame(MEDIUM_WIDTH, MEDIUM_HEIGHT, MEDIUM_MINES);
    } else if (hardButton.contains(pos)) {
      startGame(HARD_WIDTH, HARD_HEIGHT, HARD_MINES);
    } else if (customButton.contains(pos)) {
      state = CUSTOM_SETUP;
      initializeCustomSetup();
    }
  }

  // handles custom setup clicks
  // EFFECT: Updates sliders and starts game when play button is clicked
  void handleCustomClick(Posn pos) {
    widthSlider.handleClick(pos);
    heightSlider.handleClick(pos);
    mineSlider.handleClick(pos);

    if (playButton.contains(pos)) {
      int w = widthSlider.getValue();
      int h = heightSlider.getValue();
      int m = Math.min(mineSlider.getValue(), w * h - 1);
      startGame(w, h, m);
    } else if (backButton.contains(pos)) {
      state = MENU;
      worldWidth = MENU_WIDTH;
      worldHeight = MENU_HEIGHT;
    }
  }

  // handles game clicks
  // EFFECT: Reveals a cell or flags a mine based on mouse click
  void handleGameClick(Posn pos, String button) {
    // Check if hint button was clicked
    if (hintButton != null && hintButton.contains(pos) && button.equals("LeftButton")) {
      board.flagRandomMine();
    } else {
      board.handleClick(pos, button);
    }
  }

  // handles game over clicks
  // EFFECT: Returns to menu when the game over button is clicked
  void handleGameOverClick(Posn pos) {
    if (menuButton.contains(pos)) {
      state = MENU;
      worldWidth = MENU_WIDTH;
      worldHeight = MENU_HEIGHT;
    }
  }

  // starts a new game
  // EFFECT: Initializes the board and sets game state to playing
  void startGame(int cols, int rows, int mines) {
    state = PLAYING;
    calculateDimensions(cols, rows);
    board = new Board(cols, rows, mines, this, cellSize, boardOffsetX, boardOffsetY);
    wonGame = false;
  }

  // called when game ends
  // EFFECT: Sets game state to GAME_OVER and reveals all cells
  void endGame(boolean won) {
    state = GAME_OVER;
    wonGame = won;
    board.revealAll();
  }

}

// represents a clickable button
class Button implements IWorldConstants {
  int x;
  int y;
  int width;
  int height;
  String label;

  // the constructor
  Button(int x, int y, int width, int height, String label) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.label = label;
  }

  // checks if point is inside button
  boolean contains(Posn pos) {
    return pos.x >= x - width / 2 && pos.x <= x + width / 2 &&
        pos.y >= y - height / 2 && pos.y <= y + height / 2;
  }

  // draws button with 3D effect
  // EFFECT: Draws the button with a shadow, gradient effect, and text
  void drawOnto(WorldScene scene) {
    // Shadow
    WorldImage shadow = new RectangleImage(width, height,
        OutlineMode.SOLID, new Color(50, 50, 50, 50));
    scene.placeImageXY(shadow, x + 2, y + 2);

    // Button gradient effect
    WorldImage buttonBase = new RectangleImage(width, height, OutlineMode.SOLID, BUTTON_COLOR);

    // Only add gradient for larger buttons
    if (height > 30) {
      WorldImage buttonTop = new RectangleImage(width, height / 2, OutlineMode.SOLID,
          new Color(BUTTON_COLOR.getRed() + 20,
              BUTTON_COLOR.getGreen() + 20, BUTTON_COLOR.getBlue() + 20));
      scene.placeImageXY(buttonTop, x, y - height / 4);
    }

    WorldImage border = new RectangleImage(width, height, OutlineMode.OUTLINE, Color.DARK_GRAY);

    // Adjust font size for smaller buttons
    int fontSize;
    if (height > 30) {
      fontSize = 16;
    } else {
      fontSize = 14;
    }
    WorldImage text = new TextImage(label, fontSize, FontStyle.BOLD, BUTTON_TEXT);

    scene.placeImageXY(buttonBase, x, y);
    scene.placeImageXY(border, x, y);
    scene.placeImageXY(text, x, y);
  }
}

// represents a slider control
class Slider implements IWorldConstants {
  int x;
  int y;
  int width;
  int min;
  int max;
  int value;
  String label;

  // the constructor
  Slider(int x, int y, int width, int min, int max, int initial, String label) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.min = min;
    this.max = max;
    this.value = initial;
    this.label = label;
  }

  // gets current value
  int getValue() {
    return value;
  }

  // sets max value
  // EFFECT: Updates the maximum value and adjusts current value if necessary
  void setMax(int newMax) {
    this.max = newMax;
    if (value > max) {
      value = max;
    }
  }

  // handles clicks on slider
  // EFFECT: Updates the slider value based on mouse position
  void handleClick(Posn pos) {
    if (pos.y >= y - 20 && pos.y <= y + 20 &&
        pos.x >= x - width / 2 && pos.x <= x + width / 2) {
      double ratio = (pos.x - (x - (double) width / 2)) / (double) width;
      value = (int) (min + ratio * (max - min));
    }
  }

  // draws slider with enhanced graphics
  // EFFECT: Draws the slider track, thumb, and label onto the scene
  void drawOnto(WorldScene scene) {
    // Label
    WorldImage labelImg = new TextImage(label + ": " + value, 18, FontStyle.BOLD, Color.BLACK);
    scene.placeImageXY(labelImg, x, y - 40);

    // Track with groove effect
    WorldImage trackBg = new RectangleImage(width,
        SLIDER_HEIGHT + 4, OutlineMode.SOLID, Color.DARK_GRAY);
    WorldImage track = new RectangleImage(width,
        SLIDER_HEIGHT, OutlineMode.SOLID, SLIDER_TRACK);
    scene.placeImageXY(trackBg, x, y);
    scene.placeImageXY(track, x, y);

    // Thumb with 3D effect
    int thumbX = x - width / 2 + (int) ((value - min) / (double) (max - min) * width);
    WorldImage thumbShadow = new CircleImage(14, OutlineMode.SOLID, new Color(0, 0, 0, 50));
    WorldImage thumbOuter = new CircleImage(12, OutlineMode.SOLID, SLIDER_THUMB);
    WorldImage thumbInner = new CircleImage(8, OutlineMode.SOLID,
        new Color(SLIDER_THUMB.getRed() + 30,
            SLIDER_THUMB.getGreen() + 30, SLIDER_THUMB.getBlue() + 30));

    scene.placeImageXY(thumbShadow, thumbX + 1, y + 1);
    scene.placeImageXY(thumbOuter, thumbX, y);
    scene.placeImageXY(thumbInner, thumbX, y);
  }
}

// Tester Class
class Main implements IWorldConstants {
  // Test fixture fields
  MineSweeper testGame;
  Board board2x2;
  Board board3x3;
  Board emptyBoard;
  Cell cell00;
  Cell cell01;
  Cell cell10;
  Cell cell11;

  // Initializes test data
  // EFFECT: Sets up a test game and boards with cells
  void initTestData() {

    this.testGame = new MineSweeper();
    this.testGame.state = PLAYING;

    this.board2x2 = new Board(2, 2, 1, testGame, 30, 0, 0);
    this.testGame.board = this.board2x2; // Set the board in the game

    this.board3x3 = new Board(3, 3, 2, testGame, 30, 0, 0);
    this.emptyBoard = new Board(2, 2, 0, testGame, 30, 0, 0);

    this.cell00 = this.board2x2.cells.get(0).get(0);
    this.cell01 = this.board2x2.cells.get(0).get(1);
    this.cell10 = this.board2x2.cells.get(1).get(0);
    this.cell11 = this.board2x2.cells.get(1).get(1);
  }

  // Tests for initializeBoard
  boolean testInitializeBoard(Tester t) {
    this.initTestData();
    boolean test1 = t.checkExpect(this.board2x2.cells.size(), 2) &&
        t.checkExpect(this.board2x2.cells.get(0).size(), 2) &&
        t.checkExpect(this.board2x2.cells.get(1).size(), 2);

    this.initTestData();
    boolean test2 = t.checkExpect(this.board3x3.cells.size(), 3) &&
        t.checkExpect(this.board3x3.cells.get(0).size(), 3) &&
        t.checkExpect(this.board3x3.cells.get(2).size(), 3);

    this.initTestData();
    Cell c00 = this.board2x2.cells.get(0).getFirst();
    Cell c11 = this.board2x2.cells.get(1).get(1);
    boolean test3 = t.checkExpect(c00.x, 0) &&
        t.checkExpect(c00.y, 0) &&
        t.checkExpect(c11.x, 1) &&
        t.checkExpect(c11.y, 1);

    return test1 && test2 && test3;
  }

  // Tests for linkNeighbors
  boolean testLinkNeighbors(Tester t) {
    this.initTestData();
    Cell corner = this.board2x2.cells.getFirst().getFirst();
    boolean test1 = t.checkExpect(corner.neighbors.size(), 3);

    this.initTestData();
    Cell center = this.board3x3.cells.get(1).get(1);
    boolean test2 = t.checkExpect(center.neighbors.size(), 8);

    this.initTestData();
    Cell edge = this.board3x3.cells.getFirst().get(1);
    boolean test3 = t.checkExpect(edge.neighbors.size(), 5);

    return test1 && test2 && test3;
  }

  // Tests for placeMines
  boolean testPlaceMines(Tester t) {
    this.initTestData();
    int mineCount1 = 0;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isMine) {
          mineCount1++;
        }
      }
    }
    boolean test1 = t.checkExpect(mineCount1, 1);

    this.initTestData();
    int mineCount2 = 0;
    for (ArrayList<Cell> row : this.board3x3.cells) {
      for (Cell cell : row) {
        if (cell.isMine) {
          mineCount2++;
        }
      }
    }
    boolean test2 = t.checkExpect(mineCount2, 2);

    this.initTestData();
    int mineCount3 = 0;
    for (ArrayList<Cell> row : this.emptyBoard.cells) {
      for (Cell cell : row) {
        if (cell.isMine) {
          mineCount3++;
        }
      }
    }
    boolean test3 = t.checkExpect(mineCount3, 0);

    return test1 && test2 && test3;
  }

  // Tests for handleClick
  boolean testHandleClick(Tester t) {
    this.initTestData();
    this.testGame.board = this.emptyBoard; // Use empty board to avoid mine
    Cell target1 = this.emptyBoard.cells.getFirst().getFirst();
    boolean before1 = target1.isRevealed;
    this.emptyBoard.handleClick(new Posn(15, 15), "LeftButton");
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(target1.isRevealed, true);

    this.initTestData();
    Cell target2 = this.board2x2.cells.getFirst().getFirst();
    boolean before2 = target2.isFlagged;
    this.board2x2.handleClick(new Posn(15, 15), "RightButton");
    boolean test2 = t.checkExpect(before2, false) &&
        t.checkExpect(target2.isFlagged, true);

    this.initTestData();
    this.testGame.board = this.emptyBoard; // Use empty board
    this.emptyBoard.handleClick(new Posn(-10, -10), "LeftButton");
    boolean noneRevealed = true;
    for (ArrayList<Cell> row : this.emptyBoard.cells) {
      for (Cell cell : row) {
        if (cell.isRevealed) {
          noneRevealed = false;
          break;
        }
      }
    }
    boolean test3 = t.checkExpect(noneRevealed, false);

    return test1 && test2 && test3;
  }

  // Tests for revealAll
  boolean testRevealAll(Tester t) {
    this.initTestData();
    this.board2x2.revealAll();
    boolean allRevealed1 = true;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (!cell.isRevealed) {
          allRevealed1 = false;
          break;
        }
      }
    }
    boolean test1 = t.checkExpect(allRevealed1, true);

    this.initTestData();
    this.board3x3.revealAll();
    int revealedCount = 0;
    for (ArrayList<Cell> row : this.board3x3.cells) {
      for (Cell cell : row) {
        if (cell.isRevealed) {
          revealedCount++;
        }
      }
    }
    boolean test2 = t.checkExpect(revealedCount, 9);

    this.initTestData();
    Cell flagged = this.board2x2.cells.getFirst().getFirst();
    flagged.isFlagged = true;
    this.board2x2.revealAll();
    boolean test3 = t.checkExpect(flagged.isRevealed, true);

    return test1 && test2 && test3;
  }

  // Tests for onCellRevealed
  boolean testOnCellRevealed(Tester t) {
    this.initTestData();
    boolean stateBefore1 = this.testGame.state.equals(PLAYING);
    this.board2x2.onCellRevealed(true);
    boolean test1 = t.checkExpect(stateBefore1, true) &&
        t.checkExpect(this.testGame.state, GAME_OVER);

    this.initTestData();
    int countBefore = this.board2x2.cellsRevealed;
    this.board2x2.onCellRevealed(false);
    boolean test2 = t.checkExpect(countBefore, 0) &&
        t.checkExpect(this.board2x2.cellsRevealed, 1);

    this.initTestData();
    this.testGame.board = this.emptyBoard; // Use empty board for win condition
    this.emptyBoard.cellsRevealed = 3; // One away from winning
    boolean stateBefore3 = this.testGame.state.equals(PLAYING);
    this.emptyBoard.onCellRevealed(false);
    boolean test3 = t.checkExpect(stateBefore3, true) &&
        t.checkExpect(this.testGame.state, GAME_OVER) &&
        t.checkExpect(this.testGame.wonGame, true);

    return test1 && test2 && test3;
  }

  // Tests for flagRandomMine
  boolean testFlagRandomMine(Tester t) {
    this.initTestData();
    int flaggedBefore1 = 0;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flaggedBefore1++;
        }
      }
    }
    this.board2x2.flagRandomMine();
    int flaggedAfter1 = 0;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flaggedAfter1++;
        }
      }
    }
    boolean test1 = t.checkExpect(flaggedAfter1, flaggedBefore1 + 1);

    this.initTestData();
    this.emptyBoard.flagRandomMine();
    int flaggedCount2 = 0;
    for (ArrayList<Cell> row : this.emptyBoard.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flaggedCount2++;
        }
      }
    }
    boolean test2 = t.checkExpect(flaggedCount2, 0);

    this.initTestData();
    // First flag all mines manually
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isMine) {
          cell.isFlagged = true;
        }
      }
    }
    int flaggedBefore3 = 0;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flaggedBefore3++;
        }
      }
    }
    this.board2x2.flagRandomMine();
    int flaggedAfter3 = 0;
    for (ArrayList<Cell> row : this.board2x2.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flaggedAfter3++;
        }
      }
    }
    boolean test3 = t.checkExpect(flaggedAfter3, flaggedBefore3);

    return test1 && test2 && test3;
  }

  // Add these test methods to the Main class after the Board tests

  // Tests for Cell.addNeighbor
  boolean testAddNeighbor(Tester t) {
    this.initTestData();
    Cell testCell = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell neighbor1 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    int sizeBefore1 = testCell.neighbors.size();
    testCell.addNeighbor(neighbor1);
    boolean test1 = t.checkExpect(sizeBefore1, 0) &&
        t.checkExpect(testCell.neighbors.size(), 1) &&
        t.checkExpect(testCell.neighbors.contains(neighbor1), true);

    this.initTestData();
    Cell testCell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell neighbor2a = new Cell(1, 0, this.board2x2, 30, 0, 0);
    Cell neighbor2b = new Cell(0, 1, this.board2x2, 30, 0, 0);
    testCell2.addNeighbor(neighbor2a);
    testCell2.addNeighbor(neighbor2b);
    boolean test2 = t.checkExpect(testCell2.neighbors.size(), 2) &&
        t.checkExpect(testCell2.neighbors.contains(neighbor2a), true) &&
        t.checkExpect(testCell2.neighbors.contains(neighbor2b), true);

    this.initTestData();
    Cell testCell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell neighbor3 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    testCell3.addNeighbor(neighbor3);
    testCell3.addNeighbor(neighbor3);
    boolean test3 = t.checkExpect(testCell3.neighbors.size(), 2);

    return test1 && test2 && test3;
  }

  // Tests for Cell.placeMine
  boolean testPlaceMine(Tester t) {
    this.initTestData();
    Cell testCell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    boolean before1 = testCell1.isMine;
    testCell1.placeMine();
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(testCell1.isMine, true);

    this.initTestData();
    Cell testCell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    testCell2.isMine = true;
    testCell2.placeMine();
    boolean test2 = t.checkExpect(testCell2.isMine, true);

    this.initTestData();
    Cell testCell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    testCell3.placeMine();
    boolean test3 = t.checkExpect(testCell3.isRevealed, false) &&
        t.checkExpect(testCell3.isFlagged, false) &&
        t.checkExpect(testCell3.adjacentMines, 0);

    return test1 && test2 && test3;
  }

  // Tests for Cell.countAdjacentMines
  boolean testCountAdjacentMines(Tester t) {
    this.initTestData();
    Cell center1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell mine1 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    Cell safe1 = new Cell(0, 1, this.board2x2, 30, 0, 0);
    mine1.isMine = true;
    center1.addNeighbor(mine1);
    center1.addNeighbor(safe1);
    center1.countAdjacentMines();
    boolean test1 = t.checkExpect(center1.adjacentMines, 1);

    this.initTestData();
    Cell center2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell mine2a = new Cell(1, 0, this.board2x2, 30, 0, 0);
    Cell mine2b = new Cell(0, 1, this.board2x2, 30, 0, 0);
    mine2a.isMine = true;
    mine2b.isMine = true;
    center2.addNeighbor(mine2a);
    center2.addNeighbor(mine2b);
    center2.countAdjacentMines();
    boolean test2 = t.checkExpect(center2.adjacentMines, 2);

    this.initTestData();
    Cell mineCell = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell neighbor3 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    mineCell.isMine = true;
    neighbor3.isMine = true;
    mineCell.addNeighbor(neighbor3);
    mineCell.countAdjacentMines();
    boolean test3 = t.checkExpect(mineCell.adjacentMines, 0);

    return test1 && test2 && test3;
  }

  // Tests for Cell.addToCountIfMine
  boolean testAddToCountIfMine(Tester t) {
    this.initTestData();
    Cell mineCell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell askingCell1 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    mineCell1.isMine = true;
    int before1 = askingCell1.adjacentMines;
    mineCell1.addToCountIfMine(askingCell1);
    boolean test1 = t.checkExpect(before1, 0) &&
        t.checkExpect(askingCell1.adjacentMines, 1);

    this.initTestData();
    Cell safeCell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell askingCell2 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    safeCell2.isMine = false;
    int before2 = askingCell2.adjacentMines;
    safeCell2.addToCountIfMine(askingCell2);
    boolean test2 = t.checkExpect(before2, 0) &&
        t.checkExpect(askingCell2.adjacentMines, 0);

    this.initTestData();
    Cell mineCell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    Cell askingCell3 = new Cell(1, 0, this.board2x2, 30, 0, 0);
    mineCell3.isMine = true;
    mineCell3.addToCountIfMine(askingCell3);
    mineCell3.addToCountIfMine(askingCell3);
    boolean test3 = t.checkExpect(askingCell3.adjacentMines, 2);

    return test1 && test2 && test3;
  }

  // Tests for Cell.incrementAdjacentMines
  boolean testIncrementAdjacentMines(Tester t) {
    this.initTestData();
    Cell cell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    int before1 = cell1.adjacentMines;
    cell1.incrementAdjacentMines();
    boolean test1 = t.checkExpect(before1, 0) &&
        t.checkExpect(cell1.adjacentMines, 1);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.adjacentMines = 3;
    cell2.incrementAdjacentMines();
    boolean test2 = t.checkExpect(cell2.adjacentMines, 4);

    this.initTestData();
    Cell cell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell3.incrementAdjacentMines();
    cell3.incrementAdjacentMines();
    cell3.incrementAdjacentMines();
    boolean test3 = t.checkExpect(cell3.adjacentMines, 3);

    return test1 && test2 && test3;
  }

  // Tests for Cell.reveal
  boolean testReveal(Tester t) {
    this.initTestData();
    this.testGame.board = this.emptyBoard; // Use empty board to avoid mine issues
    Cell cell1 = this.emptyBoard.cells.getFirst().getFirst();
    boolean before1 = cell1.isRevealed;
    cell1.reveal();
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(cell1.isRevealed, true);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.isFlagged = true;
    cell2.reveal();
    boolean test2 = t.checkExpect(cell2.isRevealed, false) &&
        t.checkExpect(cell2.isFlagged, true);

    this.initTestData();
    this.testGame.board = this.emptyBoard;
    Cell cell3 = this.emptyBoard.cells.getFirst().getFirst();
    cell3.isRevealed = true;
    int countBefore = this.emptyBoard.cellsRevealed;
    cell3.reveal();
    boolean test3 = t.checkExpect(cell3.isRevealed, true) &&
        t.checkExpect(this.emptyBoard.cellsRevealed, countBefore);

    return test1 && test2 && test3;
  }

  // Tests for Cell.forceReveal
  boolean testForceReveal(Tester t) {
    this.initTestData();
    Cell cell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    boolean before1 = cell1.isRevealed;
    cell1.forceReveal();
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(cell1.isRevealed, true);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.isFlagged = true;
    cell2.forceReveal();
    boolean test2 = t.checkExpect(cell2.isRevealed, true) &&
        t.checkExpect(cell2.isFlagged, true);

    this.initTestData();
    Cell cell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell3.isRevealed = true;
    cell3.forceReveal();
    boolean test3 = t.checkExpect(cell3.isRevealed, true);

    return test1 && test2 && test3;
  }

  // Tests for Cell.toggleFlag
  boolean testToggleFlag(Tester t) {
    this.initTestData();
    Cell cell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    boolean before1 = cell1.isFlagged;
    cell1.toggleFlag();
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(cell1.isFlagged, true);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.isFlagged = true;
    cell2.toggleFlag();
    boolean test2 = t.checkExpect(cell2.isFlagged, false);

    this.initTestData();
    Cell cell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell3.isRevealed = true;
    cell3.toggleFlag();
    boolean test3 = t.checkExpect(cell3.isFlagged, false);

    return test1 && test2 && test3;
  }

  // Tests for Cell.addToListIfUnflaggedMine
  boolean testAddToListIfUnflaggedMine(Tester t) {
    this.initTestData();
    Cell cell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell1.isMine = true;
    ArrayList<Cell> list1 = new ArrayList<>();
    cell1.addToListIfUnflaggedMine(list1);
    boolean test1 = t.checkExpect(list1.size(), 1) &&
        t.checkExpect(list1.contains(cell1), true);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.isMine = true;
    cell2.isFlagged = true;
    ArrayList<Cell> list2 = new ArrayList<>();
    cell2.addToListIfUnflaggedMine(list2);
    boolean test2 = t.checkExpect(list2.size(), 0);

    this.initTestData();
    Cell cell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell3.isMine = false;
    ArrayList<Cell> list3 = new ArrayList<>();
    cell3.addToListIfUnflaggedMine(list3);
    boolean test3 = t.checkExpect(list3.size(), 0);

    return test1 && test2 && test3;
  }

  // Tests for Cell.setFlag
  boolean testSetFlag(Tester t) {
    this.initTestData();
    Cell cell1 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    boolean before1 = cell1.isFlagged;
    cell1.setFlag();
    boolean test1 = t.checkExpect(before1, false) &&
        t.checkExpect(cell1.isFlagged, true);

    this.initTestData();
    Cell cell2 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell2.isFlagged = true;
    cell2.setFlag();
    boolean test2 = t.checkExpect(cell2.isFlagged, true);

    this.initTestData();
    Cell cell3 = new Cell(0, 0, this.board2x2, 30, 0, 0);
    cell3.isRevealed = true;
    cell3.setFlag();
    boolean test3 = t.checkExpect(cell3.isFlagged, false);

    return test1 && test2 && test3;
  }

  // Add these test methods to the Main class after the Cell tests

  // Tests for MineSweeper.calculateDimensions
  boolean testCalculateDimensions(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.calculateDimensions(10, 10);
    boolean test1 = t.checkExpect(game1.cellSize, DEFAULT_CELL_SIZE) &&
        t.checkExpect(game1.worldWidth, 10 * DEFAULT_CELL_SIZE + 2 * BOARD_PADDING) &&
        t.checkExpect(game1.worldHeight, 10 * DEFAULT_CELL_SIZE + 2 * BOARD_PADDING + 100);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.calculateDimensions(50, 50);
    boolean test2 = t.checkExpect(game2.cellSize < DEFAULT_CELL_SIZE, true) &&
        t.checkExpect(game2.cellSize >= MIN_CELL_SIZE, true);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.calculateDimensions(15, 15);
    boolean test3 = t.checkExpect(game3.boardOffsetX, BOARD_PADDING) &&
        t.checkExpect(game3.boardOffsetY, BOARD_PADDING);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.initializeMenu
  boolean testInitializeMenu(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.initializeMenu();
    boolean test1 = t.checkExpect(game1.easyButton != null, true) &&
        t.checkExpect(game1.mediumButton != null, true) &&
        t.checkExpect(game1.hardButton != null, true) &&
        t.checkExpect(game1.customButton != null, true);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.initializeMenu();
    boolean test2 = t.checkExpect(game2.easyButton.y, 200) &&
        t.checkExpect(game2.mediumButton.y, 200 + UI_SPACING) &&
        t.checkExpect(game2.hardButton.y, 200 + UI_SPACING * 2);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.initializeMenu();
    boolean test3 = t.checkExpect(game3.easyButton.label, "Easy (9x9, 10 mines)") &&
        t.checkExpect(game3.customButton.label, "Custom");

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.initializeCustomSetup
  boolean testInitializeCustomSetup(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.initializeCustomSetup();
    boolean test1 = t.checkExpect(game1.widthSlider != null, true) &&
        t.checkExpect(game1.heightSlider != null, true) &&
        t.checkExpect(game1.mineSlider != null, true) &&
        t.checkExpect(game1.playButton != null, true) &&
        t.checkExpect(game1.backButton != null, true);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.initializeCustomSetup();
    boolean test2 = t.checkExpect(game2.widthSlider.getValue(), 15) &&
        t.checkExpect(game2.heightSlider.getValue(), 15) &&
        t.checkExpect(game2.mineSlider.getValue(), 50);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.initializeCustomSetup();
    boolean test3 = t.checkExpect(game3.widthSlider.min, 0) &&
        t.checkExpect(game3.widthSlider.max, 50) &&
        t.checkExpect(game3.heightSlider.min, 5) &&
        t.checkExpect(game3.heightSlider.max, 40);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.handleMenuClick
  boolean testHandleMenuClick(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.handleMenuClick(new Posn(MENU_WIDTH / 2, 200));
    boolean test1 = t.checkExpect(game1.state, PLAYING) &&
        t.checkExpect(game1.board.width, EASY_WIDTH) &&
        t.checkExpect(game1.board.height, EASY_HEIGHT);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.handleMenuClick(new Posn(MENU_WIDTH / 2, 200 + UI_SPACING * 3));
    boolean test2 = t.checkExpect(game2.state, CUSTOM_SETUP);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.handleMenuClick(new Posn(0, 0));
    boolean test3 = t.checkExpect(game3.state, MENU);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.handleCustomClick
  boolean testHandleCustomClick(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.state = CUSTOM_SETUP;
    game1.initializeCustomSetup();
    game1.handleCustomClick(new Posn(MENU_WIDTH / 2, 450));
    boolean test1 = t.checkExpect(game1.state, PLAYING) &&
        t.checkExpect(game1.board != null, true);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.state = CUSTOM_SETUP;
    game2.initializeCustomSetup();
    game2.handleCustomClick(new Posn(MENU_WIDTH / 2, 520));
    boolean test2 = t.checkExpect(game2.state, MENU);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.state = CUSTOM_SETUP;
    game3.initializeCustomSetup();
    int valueBefore = game3.widthSlider.getValue();
    game3.handleCustomClick(new Posn(MENU_WIDTH / 2 + 100, 200));
    boolean test3 = t.checkExpect(game3.widthSlider.getValue() > valueBefore, true);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.handleGameClick
  boolean testHandleGameClick(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.startGame(3, 3, 2);
    game1.hintButton = new Button(100, 100, 100, 30, "Hint");
    int flagsBefore = 0;
    for (ArrayList<Cell> row : game1.board.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flagsBefore++;
        }
      }
    }
    game1.handleGameClick(new Posn(100, 100), "LeftButton");
    int flagsAfter = 0;
    for (ArrayList<Cell> row : game1.board.cells) {
      for (Cell cell : row) {
        if (cell.isFlagged) {
          flagsAfter++;
        }
      }
    }
    boolean test1 = t.checkExpect(flagsAfter, flagsBefore + 1);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.startGame(3, 3, 2);
    game2.hintButton = new Button(1000, 1000, 100, 30, "Hint");
    Cell targetCell = game2.board.cells.getFirst().getFirst();
    boolean flagBefore = targetCell.isFlagged;
    game2.handleGameClick(new Posn(BOARD_PADDING + 15, BOARD_PADDING + 15), "RightButton");
    boolean test2 = t.checkExpect(flagBefore, false) &&
        t.checkExpect(targetCell.isFlagged, true);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.startGame(2, 2, 0); // No mines to avoid game over
    game3.hintButton = new Button(1000, 1000, 100, 30, "Hint");
    Cell cell = game3.board.cells.getFirst().getFirst();
    boolean revealedBefore = cell.isRevealed;
    game3.handleGameClick(new Posn(BOARD_PADDING + 15, BOARD_PADDING + 15), "LeftButton");
    boolean test3 = t.checkExpect(revealedBefore, false) &&
        t.checkExpect(cell.isRevealed, true);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.handleGameOverClick
  boolean testHandleGameOverClick(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.state = GAME_OVER;
    game1.menuButton = new Button(100, 100, 200, 50, "Back to Menu");
    game1.handleGameOverClick(new Posn(100, 100));
    boolean test1 = t.checkExpect(game1.state, MENU);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.state = GAME_OVER;
    game2.worldWidth = 500;
    game2.worldHeight = 500;
    game2.menuButton = new Button(100, 100, 200, 50, "Back to Menu");
    game2.handleGameOverClick(new Posn(100, 100));
    boolean test2 = t.checkExpect(game2.worldWidth, MENU_WIDTH) &&
        t.checkExpect(game2.worldHeight, MENU_HEIGHT);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    game3.state = GAME_OVER;
    game3.menuButton = new Button(100, 100, 200, 50, "Back to Menu");
    game3.handleGameOverClick(new Posn(500, 500));
    boolean test3 = t.checkExpect(game3.state, GAME_OVER);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.startGame
  boolean testStartGame(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.startGame(5, 5, 5);
    boolean test1 = t.checkExpect(game1.board.width, 5) &&
        t.checkExpect(game1.board.height, 5) &&
        t.checkExpect(game1.board.mineCount, 5);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.wonGame = true;
    game2.startGame(3, 3, 1);
    boolean test2 = t.checkExpect(game2.state, PLAYING) &&
        t.checkExpect(game2.wonGame, false);

    this.initTestData();
    MineSweeper game3 = new MineSweeper();
    int oldWidth = game3.worldWidth;
    game3.startGame(10, 10, 10);
    boolean test3 = t.checkExpect(game3.worldWidth != oldWidth, true) &&
        t.checkExpect(game3.boardOffsetX, BOARD_PADDING);

    return test1 && test2 && test3;
  }

  // Tests for MineSweeper.endGame
  boolean testEndGame(Tester t) {
    this.initTestData();
    MineSweeper game1 = new MineSweeper();
    game1.startGame(2, 2, 1);
    game1.endGame(true);
    boolean test1 = t.checkExpect(game1.state, GAME_OVER);

    this.initTestData();
    MineSweeper game2 = new MineSweeper();
    game2.startGame(2, 2, 1);
    game2.endGame(false);
    MineSweeper game3 = new MineSweeper();
    game3.startGame(2, 2, 1);
    game3.endGame(true);
    boolean test2 = t.checkExpect(game2.wonGame, false) &&
        t.checkExpect(game3.wonGame, true);

    this.initTestData();
    MineSweeper game4 = new MineSweeper();
    game4.startGame(2, 2, 1);
    game4.endGame(true);
    boolean allRevealed = true;
    for (ArrayList<Cell> row : game4.board.cells) {
      for (Cell cell : row) {
        if (!cell.isRevealed) {
          allRevealed = false;
          break;
        }
      }
    }
    boolean test3 = t.checkExpect(allRevealed, true);

    return test1 && test2 && test3;
  }

  // Tests for Button.contains
  boolean testButtonContains(Tester t) {
    this.initTestData();
    Button button1 = new Button(100, 100, 50, 30, "Test");
    boolean test1 = t.checkExpect(button1.contains(new Posn(100, 100)), true) &&
        t.checkExpect(button1.contains(new Posn(90, 95)), true);

    this.initTestData();
    Button button2 = new Button(100, 100, 50, 30, "Test");
    boolean test2 = t.checkExpect(button2.contains(new Posn(200, 200)), false) &&
        t.checkExpect(button2.contains(new Posn(50, 50)), false);

    this.initTestData();
    Button button3 = new Button(100, 100, 50, 30, "Test");
    boolean test3 = t.checkExpect(button3.contains(new Posn(75, 100)), true) && // left edge
        t.checkExpect(button3.contains(new Posn(125, 100)), true) && // right edge
        t.checkExpect(button3.contains(new Posn(100, 85)), true); // top edge

    return test1 && test2 && test3;
  }

  // Tests for Slider.getValue
  boolean testSliderGetValue(Tester t) {
    this.initTestData();
    Slider slider1 = new Slider(100, 100, 200, 0, 100, 50, "Test");
    boolean test1 = t.checkExpect(slider1.getValue(), 50);

    this.initTestData();
    Slider slider2 = new Slider(100, 100, 200, 0, 100, 25, "Test");
    slider2.value = 75;
    boolean test2 = t.checkExpect(slider2.getValue(), 75);

    this.initTestData();
    Slider slider3a = new Slider(100, 100, 200, 10, 90, 10, "Test");
    Slider slider3b = new Slider(100, 100, 200, 10, 90, 90, "Test");
    boolean test3 = t.checkExpect(slider3a.getValue(), 10) &&
        t.checkExpect(slider3b.getValue(), 90);

    return test1 && test2 && test3;
  }

  // Tests for Slider.setMax
  boolean testSliderSetMax(Tester t) {
    // Test 1: sets new max value
    this.initTestData();
    Slider slider1 = new Slider(100, 100, 200, 0, 100, 50, "Test");
    slider1.setMax(200);
    boolean test1 = t.checkExpect(slider1.max, 200);

    this.initTestData();
    Slider slider2 = new Slider(100, 100, 200, 0, 100, 80, "Test");
    slider2.setMax(50);
    boolean test2 = t.checkExpect(slider2.max, 50) &&
        t.checkExpect(slider2.value, 50);

    this.initTestData();
    Slider slider3 = new Slider(100, 100, 200, 0, 100, 30, "Test");
    slider3.setMax(50);
    boolean test3 = t.checkExpect(slider3.max, 50) &&
        t.checkExpect(slider3.value, 30);

    return test1 && test2 && test3;
  }

  // Tests for Slider.handleClick
  boolean testSliderHandleClick(Tester t) {
    this.initTestData();
    Slider slider1 = new Slider(100, 100, 200, 0, 100, 50, "Test");
    slider1.handleClick(new Posn(0, 100));
    boolean test1 = t.checkExpect(slider1.value, 0);

    this.initTestData();
    Slider slider2 = new Slider(100, 100, 200, 0, 100, 50, "Test");
    slider2.handleClick(new Posn(200, 100));
    boolean test2 = t.checkExpect(slider2.value, 100);

    this.initTestData();
    Slider slider3 = new Slider(100, 100, 200, 0, 100, 50, "Test");
    slider3.handleClick(new Posn(100, 200)); // too far below
    boolean test3 = t.checkExpect(slider3.value, 50);

    return test1 && test2 && test3;
  }

  // Runs the game
  boolean testBigBang(Tester t) {
    MineSweeper world = new MineSweeper();
    world.bigBang(world.worldWidth, world.worldHeight, 1 / 144.0);
    return true;
  }
}