import javalib.funworld.World;
import javalib.funworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
import java.util.Random;

// represents world constants for the game
interface IWorldConstants {
  int WORLD_WIDTH = 1200, WORLD_HEIGHT = 800, SPEED_BOOST_SIZE = 25, LARGE_FISH_SIZE = 110;
  int SF_COUNT = 25, MF_COUNT = 10, LF_COUNT = 4, BOOST_COUNT = 3;
  double BASE_MASS = 10.0, MASS_PER_VALUE = 2.0, SPEED_BOOST_DURATION = 300.0;
  double DRAG_COEFFICIENT = 0.95, TARGET_THRESHOLD = 10.0, TICK_RATE = 1.0 / 144;
  Random R = new Random(123);
  String f1 = "f1.png", f2 = "f2.png", f3 = "f3.png", f4 = "f4.png";
  WorldImage F1 = new FromFileImage(f1), F2 = new FromFileImage(f2);
  WorldImage F3 = new FromFileImage(f3), F4 = new FromFileImage(f4);
}

// represents a vector in 2D space
class Vector<T> {
  T x, y;

  Vector(T x, T y) {
    this.x = x;
    this.y = y;
  }
}

// represents a list of fish
interface IListFish {

  // draws the fish in the scene
  WorldScene drawFish(WorldScene scene);

  // moves all fish in the list
  IListFish moveAll();

  // returns the number of fish in the list
  int count();

  // counts the number of each type of fish in the list
  FishCount countTypes();

  // checks for collisions between the player and the fish
  CollisionResult checkCollisions(PlayerFish player, int smallEaten, int mediumEaten,
      boolean playerEaten);
}

// represents a list of boost
interface IListBoost extends IWorldConstants {

  // returns an image of the rendered boosts
  WorldScene drawBoosts(WorldScene scene);

  // returns a boost collision result
  BoostCollisionResult checkBoostCollisions(PlayerFish player);

  // returns the count of boosts
  int count();
}

// represents an empty list of fish
class MtListFish implements IListFish {

  // Returns a WorldScene with no fish drawn
  public WorldScene drawFish(WorldScene scene) {
    return scene;
  }

  // Returns an empty list of fish
  public IListFish moveAll() {
    return this;
  }

  // Returns 0 as there are no fish in the list
  public int count() {
    return 0;
  }

  // Returns a FishCount with all counts set to 0
  public FishCount countTypes() {
    return new FishCount(0, 0, 0);
  }

  // checks for collisions with the player
  public CollisionResult checkCollisions(PlayerFish player, int smallEaten, int mediumEaten,
      boolean playerEaten) {
    return new CollisionResult(player, this, smallEaten, mediumEaten, playerEaten);
  }
}

// represents a non-empty list of fish
class ConsListFish implements IListFish {
  IFish first;
  IListFish rest;

  // Constructor for ConsListFish
  ConsListFish(IFish first, IListFish rest) {
    this.first = first;
    this.rest = rest;
  }

  // Draws the fish in the scene
  public WorldScene drawFish(WorldScene scene) {
    Fish f = (Fish) this.first;
    return this.rest.drawFish(scene.placeImageXY(this.first.draw(), f.position.x, f.position.y));
  }

  // Moves all fish in the list
  public IListFish moveAll() {
    return new ConsListFish(this.first.move(), this.rest.moveAll());
  }

  // Returns the number of fish in the list
  public int count() {
    return 1 + this.rest.count();
  }

  // returns the count of each type of fish
  public FishCount countTypes() {
    FishCount r = this.rest.countTypes();
    return new FishCount(r.smallCount + this.first.countAsSmall(),
        r.mediumCount + this.first.countAsMedium(), r.largeCount + this.first.countAsLarge());
  }

  // returns the result of a collision
  public CollisionResult checkCollisions(PlayerFish player, int smallEaten, int mediumEaten,
      boolean playerEaten) {
    if (playerEaten)
      return new CollisionResult(player, this, smallEaten, mediumEaten, true);

    if (this.first.collidesWith(player)) {
      if (this.first.canBeEatenBy(player)) {
        return this.rest.checkCollisions(player, smallEaten + this.first.countAsSmall(),
            mediumEaten + this.first.countAsMedium(), false);
      }
      if (this.first.canEat(player)) {
        return new CollisionResult(player, this, smallEaten, mediumEaten, true);
      }
    }

    CollisionResult r = this.rest.checkCollisions(player, smallEaten, mediumEaten, false);
    return new CollisionResult(r.player, new ConsListFish(this.first, r.fishList), r.smallEaten,
        r.mediumEaten, r.playerEaten);
  }
}

// represents an empty list
class MtListBoost implements IListBoost {

  // Returns a WorldScene with no boosts drawn
  public WorldScene drawBoosts(WorldScene scene) {
    return scene;
  }

  // Returns a BoostCollisionResult with no collision detected
  public BoostCollisionResult checkBoostCollisions(PlayerFish player) {
    return new BoostCollisionResult(player, this, false);
  }

  // Returns 0 as there are no boosts in the list
  public int count() {
    return 0;
  }
}

// represents a non-empty list of boosts
class ConsListBoost implements IListBoost {
  SpeedBoost first;
  IListBoost rest;

  // Constructor for ConsListBoost
  ConsListBoost(SpeedBoost first, IListBoost rest) {
    this.first = first;
    this.rest = rest;
  }

  // Draws all boosts in the list to the scene
  public WorldScene drawBoosts(WorldScene scene) {
    SpeedBoost animated = this.first.move();
    WorldImage boostImage = animated.draw();
    return this.rest
        .drawBoosts(scene.placeImageXY(boostImage, animated.position.x, animated.position.y));
  }

  // Checks for collisions between the player and boosts in the list
  public BoostCollisionResult checkBoostCollisions(PlayerFish player) {
    if (this.first.collidesWith(player)) {
      return new BoostCollisionResult(player, this.rest, true);
    }

    BoostCollisionResult restResult = this.rest.checkBoostCollisions(player);
    return new BoostCollisionResult(restResult.player,
        new ConsListBoost(this.first, restResult.boostList), restResult.boostCollected);
  }

  // Returns the number of boosts in the list
  public int count() {
    return 1 + this.rest.count();
  }
}

// represents a fish in the game
interface IFish extends IWorldConstants {

  // returns the image of the fish
  WorldImage draw();

  // returns the current position of the fish
  IFish move();

  // returns the current position of the fish clamped to the world
  boolean collidesWith(PlayerFish player);

  // returns if the fish can be eaten by the player fish
  boolean canBeEatenBy(PlayerFish player);

  // returns if the fish can eat the player fish
  boolean canEat(PlayerFish player);

  // returns if the fish is a small fish
  int countAsSmall();

  // returns if the fish is a medium fish
  int countAsMedium();

  // returns if the fish is a large fish
  int countAsLarge();
}

// represents a fish in the game
abstract class Fish implements IFish {
  Vector<Double> velocity;
  Vector<Integer> position;
  int size;
  boolean direction;
  String imagePath;

  // the constructor for the fish
  Fish(int x, int y, int size, boolean direction, String imagePath, double vx, double vy) {
    this.position = new Vector<>(x, y);
    this.size = size;
    this.direction = direction;
    this.imagePath = imagePath;
    this.velocity = new Vector<>(vx, vy);
  }

  // returns the Image of the fish
  public WorldImage draw() {
    WorldImage baseImage;

    // Select the appropriate image based on imagePath
    if (this.imagePath.equals(f1)) {
      baseImage = F1;
    } else if (this.imagePath.equals(f2)) {
      baseImage = F2;
    } else if (this.imagePath.equals(f3)) {
      baseImage = F3;
    } else if (this.imagePath.equals(f4)) {
      baseImage = F4;
    } else {
      // Fallback to loading from file if path doesn't match
      baseImage = new FromFileImage(this.imagePath);
    }

    double scale = this.size / 500.0;
    if (this.direction) {
      return new ScaleImageXY(baseImage, -scale, scale);
    }
    return new ScaleImage(baseImage, scale);
  }

  // returns if the fish is colliding with the player fish
  public boolean collidesWith(PlayerFish player) {
    double dx = this.position.x - player.position.x;
    double dy = this.position.y - player.position.y;
    return Math.sqrt(dx * dx + dy * dy) < (this.size * 0.25 + player.size * 0.25);
  }

  // returns if the fish can be eaten by the player fish
  public boolean canBeEatenBy(PlayerFish player) {
    return player.size > this.size;
  }

  // returns if the fish can eat the player fish
  public boolean canEat(PlayerFish player) {
    return this.size > player.size;
  }

  // returns the count of the fish as small
  public int countAsSmall() {
    return 0;
  }

  // returns the count of the fish as medium
  public int countAsMedium() {
    return 0;
  }

  // returns the count of the fish as large
  public int countAsLarge() {
    return 0;
  }

  // Rest of the methods remain the same...
  // wraps the x coordinate around the world boundaries
  int wrapX(int x) {
    if (x < -size) {
      return WORLD_WIDTH + size;
    }
    if (x > WORLD_WIDTH + size) {
      return -size;
    }
    return x;
  } // Tested

  // returns the current position of the fish clamped to the world
  int clampY(int y) {
    return Math.max(size / 2, Math.min(WORLD_HEIGHT - size / 2, y));
  } // Tested
}

// represents a fish that moves in the background
class BackgroundFish extends Fish {
  Vector<Integer> target;
  double speed, actualX, actualY;

  // constructor for the background fish
  BackgroundFish(int x, int y, int size, String imagePath, double speed) {
    super(x, y, size, false, imagePath, speed, speed);
    this.speed = speed;
    this.actualX = x;
    this.actualY = y;
    this.target = new Vector<>(R.nextInt(WORLD_WIDTH), R.nextInt(WORLD_HEIGHT));
  }

  // constructor for the background fish with velocity and direction
  BackgroundFish(double actualX, double actualY, int size, String imagePath, double speed,
      Vector<Integer> target, boolean direction) {
    super((int) actualX, (int) actualY, size, direction, imagePath, speed, speed);
    this.speed = speed;
    this.actualX = actualX;
    this.actualY = actualY;
    this.target = target;
  }

  // moves the background fish towards its target and returns a new fish instance
  public BackgroundFish move() {
    Vector<Integer> currentTarget;
    if (Math.hypot(target.x - actualX, target.y - actualY) < TARGET_THRESHOLD) {
      currentTarget = new Vector<>(R.nextInt(WORLD_WIDTH), R.nextInt(WORLD_HEIGHT));
    } else {
      currentTarget = target;
    }

    double angle = Math.atan2(currentTarget.y - actualY, currentTarget.x - actualX);
    double newActualX = actualX + speed * Math.cos(angle);
    double newActualY = actualY + speed * Math.sin(angle);

    if (newActualX < -size)
      newActualX = WORLD_WIDTH + size;
    else if (newActualX > WORLD_WIDTH + size)
      newActualX = -size;

    newActualY = Math.max(size / 2, Math.min(WORLD_HEIGHT - size / 2, newActualY));

    return new BackgroundFish(newActualX, newActualY, size, imagePath, speed, currentTarget,
        Math.cos(angle) > 0);
  }

  // returns the number of background fish
  public int countAsSmall() {
    return 1;
  }
}

// represents a fish that moves in a wave pattern
abstract class WaveFish extends Fish {
  double baseSpeed, waveAmplitude, waveFrequency, time, periodOffset;
  int baseY;

  // constructor for the wave fish
  WaveFish(int x, int y, int size, String imagePath, double speed, boolean direction,
      double waveAmplitude, double waveFrequency) {
    super(x, y, size, direction, imagePath, direction ? speed : -speed, 0.0);
    this.baseSpeed = speed;
    this.waveAmplitude = waveAmplitude;
    this.waveFrequency = waveFrequency;
    this.time = R.nextDouble() * Math.PI * 2;
    this.baseY = y;
    this.periodOffset = R.nextDouble() * Math.PI * 2;
  }

  // constructor for the wave fish with velocity and direction
  WaveFish(int x, int y, int size, String imagePath, double speed, boolean direction,
      double waveAmplitude, double waveFrequency, double time, int baseY, double periodOffset) {
    super(x, y, size, direction, imagePath, direction ? speed : -speed, 0.0);
    this.baseSpeed = speed;
    this.waveAmplitude = waveAmplitude;
    this.waveFrequency = waveFrequency;
    this.time = time;
    this.baseY = baseY;
    this.periodOffset = periodOffset;
  }
}

// represents a medium fish that moves in a wave pattern
class MediumFish extends WaveFish {

  // the constructor
  MediumFish(int x, int y, int size, String imagePath, double speed, boolean direction) {
    super(x, y, size, imagePath, speed, direction, 20.0, 0.4);
  }

  // convenience constructor
  MediumFish(int x, int y, int size, String imagePath, double speed, boolean direction,
      double waveAmplitude, double waveFrequency, double time, int baseY, double periodOffset) {
    super(x, y, size, imagePath, speed, direction, waveAmplitude, waveFrequency, time, baseY,
        periodOffset);
  }

  // moves the fish and returns its pos
  public MediumFish move() {
    int newX = wrapX(position.x + velocity.x.intValue());
    double newTime = time + 0.12;
    int newY = clampY(
        baseY + (int) (waveAmplitude * Math.sin(newTime * waveFrequency + periodOffset)));
    return new MediumFish(newX, newY, size, imagePath, baseSpeed, direction, waveAmplitude,
        waveFrequency, newTime, baseY, periodOffset);
  }

  // returns the number of medium fish
  public int countAsMedium() {
    return 1;
  }
}

// represents a large fish that moves in a wave pattern
class LargeFish extends WaveFish {

  // constructor
  LargeFish(int x, int y, int size, String imagePath, double speed, boolean direction) {
    super(x, y, size, imagePath, speed, direction, 30.0, 0.1);
  }

  // convenience constructor
  LargeFish(int x, int y, int size, String imagePath, double speed, boolean direction,
      double waveAmplitude, double waveFrequency, double time, int baseY, double periodOffset) {
    super(x, y, size, imagePath, speed, direction, waveAmplitude, waveFrequency, time, baseY,
        periodOffset);
  }

  // moves the fish and returns its position
  public LargeFish move() {
    int newX = wrapX(position.x + velocity.x.intValue());
    double newTime = time + 0.12;
    int newY = clampY(
        baseY + (int) (waveAmplitude * Math.sin(newTime * waveFrequency + periodOffset)));
    return new LargeFish(newX, newY, size, imagePath, baseSpeed, direction, waveAmplitude,
        waveFrequency, newTime, baseY, periodOffset);
  }

  // returns the number of large fish here
  public int countAsLarge() {
    return 1;
  }
}

// represents a player fish that can be controlled by the user
class PlayerFish extends Fish {
  double movementForce, mass, actualX, actualY;
  boolean upPressed, downPressed, leftPressed, rightPressed;
  double MAX_SPEED = 8.0;

  // constructor for the player fish
  PlayerFish(int x, int y, int size, double movementForce, double mass, String imagePath) {
    super(x, y, size, true, imagePath, 0.0, 0.0);
    this.movementForce = movementForce;
    this.mass = mass;
    this.actualX = x;
    this.actualY = y;
  }

  // constructor for the player fish with velocity and direction
  PlayerFish(double actualX, double actualY, int size, double movementForce, double mass,
      String imagePath, double vx, double vy, boolean direction, boolean upPressed,
      boolean downPressed, boolean leftPressed, boolean rightPressed) {
    super((int) actualX, (int) actualY, size, direction, imagePath, vx, vy);
    this.movementForce = movementForce;
    this.mass = mass;
    this.actualX = actualX;
    this.actualY = actualY;
    this.upPressed = upPressed;
    this.downPressed = downPressed;
    this.leftPressed = leftPressed;
    this.rightPressed = rightPressed;
  }

  // updates the key pressed state and returns a new player fish instance
  public PlayerFish setKeyPressed(String key, boolean pressed) {
    boolean newUpPressed = upPressed;
    boolean newDownPressed = downPressed;
    boolean newLeftPressed = leftPressed;
    boolean newRightPressed = rightPressed;

    if (key.equals("up")) {
      newUpPressed = pressed;
    } else if (key.equals("down")) {
      newDownPressed = pressed;
    } else if (key.equals("left")) {
      newLeftPressed = pressed;
    } else if (key.equals("right")) {
      newRightPressed = pressed;
    }

    return new PlayerFish(actualX, actualY, size, movementForce, mass, imagePath, velocity.x,
        velocity.y, direction, newUpPressed, newDownPressed, newLeftPressed, newRightPressed);
  }

  // moves the player fish without speed boost
  public PlayerFish move() {
    return moveWithBoost(1.0);
  }

  // moves the player fish with a speed boost multiplier
  public PlayerFish moveWithBoost(double speedMultiplier) {
    double leftForce = 0, rightForce = 0, upForce = 0, downForce = 0;
    if (leftPressed) {
      leftForce = -1;
    }
    if (rightPressed) {
      rightForce = 1;
    }
    if (upPressed) {
      upForce = -1;
    }
    if (downPressed) {
      downForce = 1;
    }

    double forceX = (leftForce + rightForce) * movementForce * speedMultiplier;
    double forceY = (upForce + downForce) * movementForce * speedMultiplier;

    double newVx = (velocity.x + forceX / mass * 3) * DRAG_COEFFICIENT;
    double newVy = (velocity.y + forceY / mass * 3) * DRAG_COEFFICIENT;

    double speed = Math.hypot(newVx, newVy);
    double maxSpeed = MAX_SPEED * speedMultiplier;
    if (speed > maxSpeed) {
      newVx = (newVx / speed) * maxSpeed;
      newVy = (newVy / speed) * maxSpeed;
    }

    double newActualX = actualX + newVx;
    double newActualY = clampY((int) (actualY + newVy));

    if (newActualX < -size) {
      newActualX = WORLD_WIDTH + size;
    } else if (newActualX > WORLD_WIDTH + size) {
      newActualX = -size;
    }

    boolean newDirection;
    if (Math.abs(newVx) > 0.1) {
      newDirection = newVx > 0;
    } else {
      newDirection = direction;
    }

    return new PlayerFish(newActualX, newActualY, size, movementForce, mass, imagePath, newVx,
        newVy, newDirection, upPressed, downPressed, leftPressed, rightPressed);
  }

  // updates the player fish size and mass and returns a new instance
  public PlayerFish updateSizeAndMass(int newSize, double newMass) {
    return new PlayerFish(actualX, actualY, newSize, movementForce, newMass, imagePath, velocity.x,
        velocity.y, direction, upPressed, downPressed, leftPressed, rightPressed);
  }
}

// represents the feeding frenzy game world
class FeedingFrenzy extends World implements IWorldConstants {
  PlayerFish player;
  IListFish allFish;
  IListBoost allBoosts;
  int score, lives, RESPAWN_DELAY = 120, BOOST_RESPAWN_DELAY = 180;
  int smallFishEaten, mediumFishEaten, mediumRespawnTimer, largeRespawnTimer, boostRespawnTimer;
  double totalFishValue, speedBoostTimer;
  boolean gameWon, gameLost;

  // creates a new game with default starting values
  FeedingFrenzy() {
    this.player = new PlayerFish(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 50, 5.0, BASE_MASS,
        f1);
    this.allFish = createLF(0, LF_COUNT,
        createMF(0, MF_COUNT, createSF(0, SF_COUNT, new MtListFish())));
    this.allBoosts = createBoostsHelper(0, BOOST_COUNT, new MtListBoost());
    this.score = 0;
    this.lives = 3;
    this.smallFishEaten = 0;
    this.mediumFishEaten = 0;
    this.totalFishValue = 0.0;
    this.gameWon = false;
    this.gameLost = false;
    this.mediumRespawnTimer = 0;
    this.largeRespawnTimer = 0;
    this.boostRespawnTimer = 0;
    this.speedBoostTimer = 0.0;
  }

  // creates a game with specified state values
  FeedingFrenzy(PlayerFish player, IListFish allFish, IListBoost allBoosts, int score, int lives,
      int smallFishEaten, int mediumFishEaten, double totalFishValue, boolean gameWon,
      boolean gameLost, int mediumRespawnTimer, int largeRespawnTimer, int boostRespawnTimer,
      double speedBoostTimer) {
    this.player = player;
    this.allFish = allFish;
    this.allBoosts = allBoosts;
    this.score = score;
    this.lives = lives;
    this.smallFishEaten = smallFishEaten;
    this.mediumFishEaten = mediumFishEaten;
    this.totalFishValue = totalFishValue;
    this.gameWon = gameWon;
    this.gameLost = gameLost;
    this.mediumRespawnTimer = mediumRespawnTimer;
    this.largeRespawnTimer = largeRespawnTimer;
    this.boostRespawnTimer = boostRespawnTimer;
    this.speedBoostTimer = speedBoostTimer;
  }

  // helper method to recursively create boosts
  IListBoost createBoostsHelper(int current, int total, IListBoost acc) {
    if (current >= total) {
      return acc;
    }
    SpeedBoost boost = new SpeedBoost(100 + R.nextInt(WORLD_WIDTH - 200),
        100 + R.nextInt(WORLD_HEIGHT - 200));
    return createBoostsHelper(current + 1, total, new ConsListBoost(boost, acc));
  } // tested

  // helper method to recursively create small fish
  IListFish createSF(int current, int total, IListFish acc) {
    if (current >= total) {
      return acc;
    }
    BackgroundFish small = new BackgroundFish(R.nextInt(WORLD_WIDTH), R.nextInt(WORLD_HEIGHT), 30,
        f4, 2.0 + R.nextDouble());
    return createSF(current + 1, total, new ConsListFish(small, acc));
  } // tested

  // helper method to recursively create medium fish
  IListFish createMF(int current, int total, IListFish acc) {
    if (current >= total) {
      return acc;
    }
    MediumFish medium = new MediumFish(R.nextInt(WORLD_WIDTH), R.nextInt(WORLD_HEIGHT), 70,
        f2, 3.0 + R.nextDouble(), R.nextBoolean());
    return createMF(current + 1, total, new ConsListFish(medium, acc));
  } // tested

  // helper method to recursively create large fish
  IListFish createLF(int current, int total, IListFish acc) {
    if (current >= total) {
      return acc;
    }
    LargeFish large = new LargeFish(R.nextInt(WORLD_WIDTH), 50 + R.nextInt(WORLD_HEIGHT - 100),
        LARGE_FISH_SIZE, f3, 1.5 + R.nextDouble() * 0.5, R.nextBoolean());
    return createLF(current + 1, total, new ConsListFish(large, acc));
  } // tested

  // renders the current game scene
  public WorldScene makeScene() {
    if (this.gameWon) {
      return this.makeEndScene("You Win! You're the apex predator!");
    } else if (this.gameLost) {
      return this.makeEndScene("Game Over! You got eaten!");
    }

    WorldScene scene = this.allFish.drawFish(this.allBoosts.drawBoosts(this.getEmptyScene()));

    WorldImage playerImage;
    if (this.speedBoostTimer > 0) {
      WorldImage glow = new CircleImage(this.player.size + 10, OutlineMode.SOLID,
          new Color(100, 200, 255, 50));
      WorldImage normalPlayer = this.player.draw();
      playerImage = new OverlayImage(normalPlayer, glow);
    } else {
      playerImage = this.player.draw();
    }
    scene = scene.placeImageXY(playerImage, this.player.position.x, this.player.position.y);

    WorldImage scoreBg = new RectangleImage(200, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage scoreText = new TextImage("Score: " + this.score, 24, FontStyle.BOLD, Color.WHITE);
    WorldImage scoreDisplay = new OverlayImage(scoreText, scoreBg);
    scene = scene.placeImageXY(scoreDisplay, 120, 30);

    WorldImage sizeBg = new RectangleImage(150, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage sizeText = new TextImage("Size: " + this.player.size, 20, FontStyle.BOLD,
        Color.WHITE);
    WorldImage sizeDisplay = new OverlayImage(sizeText, sizeBg);
    scene = scene.placeImageXY(sizeDisplay, 120, 80);

    WorldImage livesBg = new RectangleImage(150, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage livesDisplay = drawLivesHelper(0, this.lives, new EmptyImage());

    WorldImage livesWithBg = new OverlayImage(livesDisplay, livesBg);
    scene = scene.placeImageXY(livesWithBg, WORLD_WIDTH - 100, 30);

    if (this.speedBoostTimer > 0) {
      WorldImage boostBg = new RectangleImage(200, 30, OutlineMode.SOLID,
          new Color(0, 100, 200, 180));
      int percentage = (int) ((this.speedBoostTimer / SPEED_BOOST_DURATION) * 100);
      WorldImage boostText = new TextImage("BOOST: " + percentage + "%", 18, FontStyle.BOLD,
          Color.YELLOW);
      WorldImage boostDisplay = new OverlayImage(boostText, boostBg);
      scene = scene.placeImageXY(boostDisplay, WORLD_WIDTH / 2, 30);
    }

    return scene;
  }

  // helper method to recursively draw lives (hearts)
  WorldImage drawLivesHelper(int current, int total, WorldImage acc) {
    if (current >= total) {
      return acc;
    }
    WorldImage heart = new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.RED);
    WorldImage heartBottom = new CircleImage(7, OutlineMode.SOLID, Color.RED);
    WorldImage heartShape = new OverlayOffsetImage(heart, 0, -3,
        new BesideImage(heartBottom, heartBottom));
    return drawLivesHelper(current + 1, total, new BesideImage(acc, heartShape));
  }

  // returns the end scene
  public WorldScene makeEndScene(String message) {
    WorldScene scene = this.getEmptyScene();

    WorldImage background = new RectangleImage(WORLD_WIDTH, WORLD_HEIGHT, OutlineMode.SOLID,
        new Color(0, 0, 50));
    scene = scene.placeImageXY(background, WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

    WorldImage messageImage = new TextImage(message, 40, FontStyle.BOLD, Color.WHITE);
    scene = scene.placeImageXY(messageImage, WORLD_WIDTH / 2, WORLD_HEIGHT / 2);

    WorldImage scoreImage = new TextImage("Final Score: " + this.score, 30, FontStyle.BOLD,
        Color.YELLOW);
    scene = scene.placeImageXY(scoreImage, WORLD_WIDTH / 2, WORLD_HEIGHT / 2 + 60);

    return scene;
  }

  // updates the world on tick
  public World onTick() {
    if (this.gameWon || this.gameLost) {
      return this;
    }

    IListFish newFish = this.allFish.moveAll();

    PlayerFish newPlayer;
    if (this.speedBoostTimer > 0) {
      newPlayer = this.player.moveWithBoost(2.0);
    } else {
      newPlayer = this.player.move();
    }

    CollisionResult result = this.checkCollisions(newPlayer, newFish);

    BoostCollisionResult boostResult = this.allBoosts.checkBoostCollisions(result.player);

    int newScore = this.score + (result.smallEaten * 10) + (result.mediumEaten * 25);

    double newTotalValue = this.totalFishValue + result.smallEaten + (result.mediumEaten * 2);

    PlayerFish sizedPlayer = boostResult.player.updateSizeAndMass(
        50 + (int) (15 * Math.log1p(newTotalValue)), BASE_MASS + (newTotalValue * MASS_PER_VALUE));

    boolean won = sizedPlayer.size >= LARGE_FISH_SIZE;

    int newLives = this.lives;
    PlayerFish respawnedPlayer = sizedPlayer;
    double respawnedTotalValue = newTotalValue;
    int respawnedSmallEaten = this.smallFishEaten + result.smallEaten;
    int respawnedMediumEaten = this.mediumFishEaten + result.mediumEaten;

    if (result.playerEaten) {
      newLives = this.lives - 1;
      if (newLives > 0) {
        respawnedPlayer = new PlayerFish(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 50, 5.0, BASE_MASS,
            f1);
        newScore = Math.max(0, newScore - 50);
        respawnedTotalValue = newTotalValue * 0.5;
        respawnedSmallEaten = (int) (respawnedSmallEaten * 0.5);
        respawnedMediumEaten = (int) (respawnedMediumEaten * 0.5);
      }
    }

    boolean lost = newLives <= 0;

    FishCount counts = result.fishList.countTypes();

    int newMediumTimer;
    if (this.mediumRespawnTimer > 0) {
      newMediumTimer = this.mediumRespawnTimer - 1;
    } else {
      newMediumTimer = 0;
    }

    int newLargeTimer;
    if (this.largeRespawnTimer > 0) {
      newLargeTimer = this.largeRespawnTimer - 1;
    } else {
      newLargeTimer = 0;
    }

    int newBoostTimer;
    if (this.boostRespawnTimer > 0) {
      newBoostTimer = this.boostRespawnTimer - 1;
    } else {
      newBoostTimer = 0;
    }

    double newSpeedBoostTimer = Math.max(0, this.speedBoostTimer - 1);

    if (boostResult.boostCollected) {
      newSpeedBoostTimer = SPEED_BOOST_DURATION;
    }

    IListFish updatedFishList = result.fishList;

    if (counts.mediumCount < MF_COUNT && newMediumTimer == 0) {
      boolean fromLeft = R.nextBoolean();
      int xPosition = WORLD_WIDTH + 60;
      if (fromLeft) {
        xPosition = -60;
      }

      MediumFish newMedium = new MediumFish(xPosition, 50 + R.nextInt(WORLD_HEIGHT - 100), 70,
          f2, 3.0 + R.nextDouble(), fromLeft);
      updatedFishList = new ConsListFish(newMedium, updatedFishList);
      newMediumTimer = RESPAWN_DELAY;
    }

    if (counts.largeCount < LF_COUNT && newLargeTimer == 0) {
      boolean fromLeft = R.nextBoolean();
      int xPosition;
      if (fromLeft) {
        xPosition = -100;
      } else {
        xPosition = WORLD_WIDTH + 100;
      }
      int yPosition = 50 + R.nextInt(WORLD_HEIGHT - 100);

      LargeFish newLarge = new LargeFish(xPosition, yPosition, LARGE_FISH_SIZE, f3,
          1.5 + R.nextDouble() * 0.5, fromLeft);
      updatedFishList = new ConsListFish(newLarge, updatedFishList);
      newLargeTimer = RESPAWN_DELAY;
    }

    IListBoost updatedBoostList = boostResult.boostList;
    int boostCount = boostResult.boostList.count();
    if (boostCount < BOOST_COUNT && newBoostTimer == 0) {
      SpeedBoost newBoost = new SpeedBoost(100 + R.nextInt(WORLD_WIDTH - 200),
          100 + R.nextInt(WORLD_HEIGHT - 200));
      updatedBoostList = new ConsListBoost(newBoost, updatedBoostList);
      newBoostTimer = BOOST_RESPAWN_DELAY;
    }

    return new FeedingFrenzy(respawnedPlayer, updatedFishList, updatedBoostList, newScore, newLives,
        respawnedSmallEaten, respawnedMediumEaten, respawnedTotalValue, won, lost, newMediumTimer,
        newLargeTimer, newBoostTimer, newSpeedBoostTimer);
  }

  // handles collision with fish
  CollisionResult checkCollisions(PlayerFish newPlayer, IListFish newFish) {
    return newFish.checkCollisions(newPlayer, 0, 0, false);
  }

  // handles key events
  public World onKeyEvent(String key) {
    if (this.gameWon || this.gameLost) {
      return this;
    }

    PlayerFish newPlayer = this.player;

    if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      newPlayer = this.player.setKeyPressed(key, true);
    }

    return new FeedingFrenzy(newPlayer, this.allFish, this.allBoosts, this.score, this.lives,
        this.smallFishEaten, this.mediumFishEaten, this.totalFishValue, this.gameWon, this.gameLost,
        this.mediumRespawnTimer, this.largeRespawnTimer, this.boostRespawnTimer,
        this.speedBoostTimer);
  }

  // handles key releases
  public World onKeyReleased(String key) {
    if (this.gameWon || this.gameLost) {
      return this;
    }

    PlayerFish newPlayer = this.player;

    if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      newPlayer = this.player.setKeyPressed(key, false);
    }

    return new FeedingFrenzy(newPlayer, this.allFish, this.allBoosts, this.score, this.lives,
        this.smallFishEaten, this.mediumFishEaten, this.totalFishValue, this.gameWon, this.gameLost,
        this.mediumRespawnTimer, this.largeRespawnTimer, this.boostRespawnTimer,
        this.speedBoostTimer);
  }
}

// represents a collision result
class CollisionResult {
  PlayerFish player;
  IListFish fishList;
  int smallEaten, mediumEaten;
  boolean playerEaten;

  // Constructor for CollisionResult
  CollisionResult(PlayerFish player, IListFish fishList, int smallEaten, int mediumEaten,
      boolean playerEaten) {
    this.player = player;
    this.fishList = fishList;
    this.smallEaten = smallEaten;
    this.mediumEaten = mediumEaten;
    this.playerEaten = playerEaten;
  }
}

// represents a collision with a speed boost
class BoostCollisionResult {
  PlayerFish player;
  IListBoost boostList;
  boolean boostCollected;

  // Constructor for BoostCollisionResult
  BoostCollisionResult(PlayerFish player, IListBoost boostList, boolean boostCollected) {
    this.player = player;
    this.boostList = boostList;
    this.boostCollected = boostCollected;
  }
}

// represents the count of different fish types
class FishCount {
  int smallCount, mediumCount, largeCount;

  // Constructor for FishCount
  FishCount(int smallCount, int mediumCount, int largeCount) {
    this.smallCount = smallCount;
    this.mediumCount = mediumCount;
    this.largeCount = largeCount;
  }
}

class SpeedBoost implements IWorldConstants {
  Vector<Integer> position;
  double pulseTime;

  // creates a speed boost at the given position with random pulse time
  SpeedBoost(int x, int y) {
    this.position = new Vector<>(x, y);
    this.pulseTime = R.nextDouble() * Math.PI * 2;
  }

  // creates a speed boost at the given position with specified pulse time
  SpeedBoost(int x, int y, double pulseTime) {
    this.position = new Vector<>(x, y);
    this.pulseTime = pulseTime;
  }

  // draws the speed boost with a pulsing animation effect
  WorldImage draw() {
    double scale = 1.0 + 0.2 * Math.sin(pulseTime);
    int currentSize = (int) (SPEED_BOOST_SIZE * scale);

    WorldImage outer = new CircleImage(currentSize + 5, OutlineMode.SOLID,
        new Color(100, 200, 255, 100));
    WorldImage middle = new CircleImage(currentSize, OutlineMode.SOLID,
        new Color(150, 220, 255, 150));
    WorldImage inner = new CircleImage(currentSize - 5, OutlineMode.SOLID,
        new Color(200, 240, 255, 200));
    WorldImage star = new StarImage(currentSize - 8, OutlineMode.SOLID, Color.WHITE);

    return new OverlayImage(star, new OverlayImage(inner, new OverlayImage(middle, outer)));
  } // tested

  // updates the boost's pulse animation
  SpeedBoost move() {
    return new SpeedBoost(this.position.x, this.position.y, this.pulseTime + 0.1);
  } // tested

  // checks if this boost collides with the given player
  boolean collidesWith(PlayerFish player) {
    double distance = Math.sqrt(Math.pow(this.position.x - player.position.x, 2)
        + Math.pow(this.position.y - player.position.y, 2));

    return distance < (SPEED_BOOST_SIZE + player.size * 0.25);
  } // tested
}

// Tester Class
class Main implements IWorldConstants {

  BackgroundFish small1 = new BackgroundFish(50, 50, 30, f4, 2.0);
  BackgroundFish small2 = new BackgroundFish(100, 100, 30, f4, 2.0);
  MediumFish medium = new MediumFish(150, 150, 60, f2, 3.0, true);
  LargeFish large = new LargeFish(200, 200, 100, f3, 2.0, true);
  IListFish list1 = new ConsListFish(this.small1, new MtListFish());
  IListFish list2 = new ConsListFish(this.medium, new ConsListFish(this.large, new MtListFish()));
  IListFish list3 = new ConsListFish(this.small1,
      new ConsListFish(this.small2, new ConsListFish(this.medium, new MtListFish())));

  // SpeedBoost tests
  boolean testSpeedBoostDraw(Tester t) {
    SpeedBoost boost1 = new SpeedBoost(100, 100, 0.0);
    SpeedBoost boost2 = new SpeedBoost(200, 200, Math.PI);
    SpeedBoost boost3 = new SpeedBoost(50, 50, Math.PI / 2);
    return t.checkExpect(boost1.draw() != null, true) && t.checkExpect(boost2.draw() != null, true)
        && t.checkExpect(boost3.draw() != null, true);
  }

  boolean testSpeedBoostMove(Tester t) {
    SpeedBoost boost1 = new SpeedBoost(100, 100, 1.0);
    SpeedBoost boost2 = new SpeedBoost(50, 75, 0.0);
    SpeedBoost boost3 = new SpeedBoost(200, 300, 5.5);
    return t.checkExpect(boost1.move().pulseTime, 1.1)
        && t.checkExpect(boost2.move().position.x, 50)
        && t.checkExpect(boost2.move().position.y, 75)
        && t.checkExpect(boost3.move().pulseTime, 5.6);
  }

  boolean testSpeedBoostCollidesWith(Tester t) {
    SpeedBoost boost = new SpeedBoost(100, 100);
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(200, 200, 50, 5.0, 10.0, f1);
    PlayerFish player3 = new PlayerFish(125, 100, 50, 5.0, 10.0, f1);
    return t.checkExpect(boost.collidesWith(player1), true)
        && t.checkExpect(boost.collidesWith(player2), false)
        && t.checkExpect(boost.collidesWith(player3), true);
  }

  // Fish tests
  boolean testFishWrapX(Tester t) {
    BackgroundFish fish = new BackgroundFish(50, 50, 30, f4, 2.0);
    return t.checkExpect(fish.wrapX(-50), WORLD_WIDTH + 30)
        && t.checkExpect(fish.wrapX(WORLD_WIDTH + 50), -30) && t.checkExpect(fish.wrapX(600), 600);
  }

  boolean testFishClampY(Tester t) {
    BackgroundFish fish = new BackgroundFish(50, 50, 30, f4, 2.0);
    return t.checkExpect(fish.clampY(10), 15)
        && t.checkExpect(fish.clampY(WORLD_HEIGHT - 10), WORLD_HEIGHT - 15)
        && t.checkExpect(fish.clampY(400), 400);
  }

  boolean testFishCollidesWith(Tester t) {
    BackgroundFish fish1 = new BackgroundFish(100, 100, 30, f4, 2.0);
    BackgroundFish fish2 = new BackgroundFish(100, 100, 40, f4, 2.0);
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(200, 200, 50, 5.0, 10.0, f1);
    PlayerFish player3 = new PlayerFish(120, 100, 40, 5.0, 10.0, f1);
    return t.checkExpect(fish1.collidesWith(player1), true)
        && t.checkExpect(fish1.collidesWith(player2), false)
        && t.checkExpect(fish2.collidesWith(player3), false);
  }

  boolean testFishCanBeEatenBy(Tester t) {
    BackgroundFish smallFish = new BackgroundFish(100, 100, 30, f4, 2.0);
    PlayerFish bigPlayer = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish smallPlayer = new PlayerFish(100, 100, 20, 5.0, 10.0, f1);
    PlayerFish equalPlayer = new PlayerFish(100, 100, 30, 5.0, 10.0, f1);
    return t.checkExpect(smallFish.canBeEatenBy(bigPlayer), true)
        && t.checkExpect(smallFish.canBeEatenBy(smallPlayer), false)
        && t.checkExpect(smallFish.canBeEatenBy(equalPlayer), false);
  }

  boolean testFishCanEat(Tester t) {
    LargeFish largeFish = new LargeFish(100, 100, 100, f3, 2.0, true);
    MediumFish mediumFish = new MediumFish(100, 100, 60, f2, 3.0, true);
    PlayerFish smallPlayer = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish bigPlayer = new PlayerFish(100, 100, 150, 5.0, 10.0, f1);
    PlayerFish tinyPlayer = new PlayerFish(100, 100, 40, 5.0, 10.0, f1);
    return t.checkExpect(largeFish.canEat(smallPlayer), true)
        && t.checkExpect(largeFish.canEat(bigPlayer), false)
        && t.checkExpect(mediumFish.canEat(tinyPlayer), true);
  }

  // BackgroundFish tests
  boolean testBackgroundFishCountAsSmall(Tester t) {
    BackgroundFish fish1 = new BackgroundFish(100, 100, 30, f4, 2.0);
    BackgroundFish fish2 = new BackgroundFish(50, 50, 20, f4, 3.0);
    BackgroundFish fish3 = new BackgroundFish(200, 200, 40, f4, 1.5);
    return t.checkExpect(fish1.countAsSmall(), 1) && t.checkExpect(fish2.countAsMedium(), 0)
        && t.checkExpect(fish3.countAsLarge(), 0);
  }

  // MediumFish tests
  boolean testMediumFishCountAsMedium(Tester t) {
    MediumFish fish1 = new MediumFish(100, 100, 60, f2, 3.0, true);
    MediumFish fish2 = new MediumFish(150, 150, 70, f2, 2.5, false);
    MediumFish fish3 = new MediumFish(200, 200, 80, f2, 3.5, true);
    return t.checkExpect(fish1.countAsMedium(), 1) && t.checkExpect(fish2.countAsSmall(), 0)
        && t.checkExpect(fish3.countAsLarge(), 0);
  }

  // LargeFish tests
  boolean testLargeFishCountAsLarge(Tester t) {
    LargeFish fish1 = new LargeFish(100, 100, 100, f3, 2.0, true);
    LargeFish fish2 = new LargeFish(150, 150, 120, f3, 1.5, false);
    LargeFish fish3 = new LargeFish(200, 200, 110, f3, 1.8, true);
    return t.checkExpect(fish1.countAsLarge(), 1) && t.checkExpect(fish2.countAsSmall(), 0)
        && t.checkExpect(fish3.countAsMedium(), 0);
  }

  // PlayerFish tests
  boolean testPlayerFishSetKeyPressed(Tester t) {
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish upPressed = player.setKeyPressed("up", true);
    PlayerFish leftPressed = player.setKeyPressed("left", true);
    PlayerFish leftReleased = leftPressed.setKeyPressed("left", false);
    PlayerFish multiPressed = player.setKeyPressed("right", true).setKeyPressed("down", true);
    return t.checkExpect(upPressed.upPressed, true)
        && t.checkExpect(leftReleased.leftPressed, false)
        && t.checkExpect(multiPressed.rightPressed, true)
        && t.checkExpect(multiPressed.downPressed, true);
  }

  boolean testPlayerFishMove(Tester t) {
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(100.0, 100.0, 50, 5.0, 10.0, f1, 2.0, 0.0,
        true, false, false, false, false);
    PlayerFish player3 = new PlayerFish(WORLD_WIDTH + 60, 100.0, 50, 5.0, 10.0, f1,
        0.0, 0.0, true, false, false, false, false);
    return t.checkExpect(player1.move().position.x, 100)
        && t.checkExpect(player1.move().position.y, 100)
        && t.checkExpect(player2.move().actualX > 100.0, true)
        && t.checkExpect(player3.move().actualX < 0, true);
  }

  boolean testPlayerFishMoveWithBoost(Tester t) {
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(100.0, 100.0, 50, 5.0, 10.0, f1, 0.0, 0.0,
        true, false, false, false, true);
    PlayerFish player3 = new PlayerFish(100.0, 100.0, 50, 5.0, 10.0, f1, 0.0, 0.0,
        true, true, false, false, false);
    return t.checkExpect(player1.moveWithBoost(2.0).position.x, 100)
        && t.checkExpect(player1.moveWithBoost(2.0).position.y, 100)
        && t.checkExpect(player2.moveWithBoost(2.0).velocity.x > 0, true)
        && t.checkExpect(player3.moveWithBoost(3.0).velocity.y < 0, true);
  }

  boolean testPlayerFishUpdateSizeAndMass(Tester t) {
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(200, 300, 60, 6.0, 12.0, f1);
    PlayerFish player3 = new PlayerFish(150, 150, 40, 4.0, 8.0, f1);
    PlayerFish updated1 = player1.updateSizeAndMass(75, 15.0);
    PlayerFish updated2 = player2.updateSizeAndMass(100, 20.0);
    PlayerFish updated3 = player3.updateSizeAndMass(30, 5.0);
    return t.checkExpect(updated1.size, 75) && t.checkExpect(updated1.mass, 15.0)
        && t.checkExpect(updated2.position.x, 200) && t.checkExpect(updated2.position.y, 300)
        && t.checkExpect(updated3.movementForce, 4.0);
  }

  // MtListFish tests
  boolean testMtListFishCount(Tester t) {
    MtListFish empty1 = new MtListFish();
    IListFish empty2 = new MtListFish();
    MtListFish empty3 = new MtListFish();
    return t.checkExpect(empty1.count(), 0) && t.checkExpect(empty2.count(), 0)
        && t.checkExpect(empty3.count() + 5, 5);
  }

  boolean testMtListFishCountTypes(Tester t) {
    MtListFish empty = new MtListFish();
    FishCount counts = empty.countTypes();
    return t.checkExpect(counts.smallCount, 0) && t.checkExpect(counts.mediumCount, 0)
        && t.checkExpect(counts.largeCount, 0);
  }

  boolean testMtListFishCheckCollisions(Tester t) {
    MtListFish empty = new MtListFish();
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    CollisionResult result1 = empty.checkCollisions(player, 0, 0, false);
    CollisionResult result2 = empty.checkCollisions(player, 5, 3, false);
    CollisionResult result3 = empty.checkCollisions(player, 0, 0, true);
    return t.checkExpect(result1.smallEaten, 0) && t.checkExpect(result2.smallEaten, 5)
        && t.checkExpect(result2.mediumEaten, 3) && t.checkExpect(result3.playerEaten, true);
  }

  // ConsListFish tests
  boolean testConsListFishCount(Tester t) {
    return t.checkExpect(this.list1.count(), 1) && t.checkExpect(this.list2.count(), 2)
        && t.checkExpect(this.list3.count(), 3);
  }

  boolean testConsListFishCountTypes(Tester t) {
    FishCount counts1 = this.list1.countTypes();
    FishCount counts2 = this.list2.countTypes();
    FishCount counts3 = this.list3.countTypes();
    return t.checkExpect(counts1.smallCount, 1) && t.checkExpect(counts2.mediumCount, 1)
        && t.checkExpect(counts2.largeCount, 1) && t.checkExpect(counts3.smallCount, 2)
        && t.checkExpect(counts3.mediumCount, 1);
  }

  // MtListBoost tests
  boolean testMtListBoostCount(Tester t) {
    MtListBoost empty1 = new MtListBoost();
    IListBoost empty2 = new MtListBoost();
    MtListBoost empty3 = new MtListBoost();
    return t.checkExpect(empty1.count(), 0) && t.checkExpect(empty2.count(), 0)
        && t.checkExpect(empty3.count() + 10, 10);
  }

  boolean testMtListBoostCheckBoostCollisions(Tester t) {
    MtListBoost empty = new MtListBoost();
    PlayerFish player1 = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish player2 = new PlayerFish(200, 200, 60, 6.0, 12.0, f1);
    PlayerFish player3 = new PlayerFish(300, 300, 70, 7.0, 14.0, f1);
    BoostCollisionResult result1 = empty.checkBoostCollisions(player1);
    BoostCollisionResult result2 = empty.checkBoostCollisions(player2);
    BoostCollisionResult result3 = empty.checkBoostCollisions(player3);
    return t.checkExpect(result1.boostCollected, false) && t.checkExpect(result2.player, player2)
        && t.checkExpect(result3.boostList, empty);
  }

  // ConsListBoost tests
  boolean testConsListBoostCount(Tester t) {
    SpeedBoost boost1 = new SpeedBoost(100, 100);
    SpeedBoost boost2 = new SpeedBoost(200, 200);
    SpeedBoost boost3 = new SpeedBoost(300, 300);
    IListBoost list1 = new ConsListBoost(boost1, new MtListBoost());
    IListBoost list2 = new ConsListBoost(boost1, new ConsListBoost(boost2, new MtListBoost()));
    IListBoost list3 = new ConsListBoost(boost1,
        new ConsListBoost(boost2, new ConsListBoost(boost3, new MtListBoost())));
    return t.checkExpect(list1.count(), 1) && t.checkExpect(list2.count(), 2)
        && t.checkExpect(list3.count(), 3);
  }

  boolean testConsListBoostCheckBoostCollisions(Tester t) {
    SpeedBoost boost = new SpeedBoost(100, 100);
    IListBoost list = new ConsListBoost(boost, new MtListBoost());
    PlayerFish playerOn = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish playerOff = new PlayerFish(300, 300, 50, 5.0, 10.0, f1);
    BoostCollisionResult resultOn = list.checkBoostCollisions(playerOn);
    BoostCollisionResult resultOff = list.checkBoostCollisions(playerOff);
    return t.checkExpect(resultOn.boostCollected, true)
        && t.checkExpect(resultOff.boostCollected, false)
        && t.checkExpect(resultOn.boostList.count(), 0);
  }

  // FeedingFrenzy tests
  boolean testFeedingFrenzyCreateBoostsHelper(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    IListBoost boosts0 = game.createBoostsHelper(0, 0, new MtListBoost());
    IListBoost boosts1 = game.createBoostsHelper(0, 1, new MtListBoost());
    IListBoost boosts3 = game.createBoostsHelper(0, 3, new MtListBoost());
    return t.checkExpect(boosts0.count(), 0) && t.checkExpect(boosts1.count(), 1)
        && t.checkExpect(boosts3.count(), 3);
  }

  boolean testFeedingFrenzyCreateSF(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    IListFish fish0 = game.createSF(0, 0, new MtListFish());
    IListFish fish1 = game.createSF(0, 1, new MtListFish());
    IListFish fish5 = game.createSF(0, 5, new MtListFish());
    FishCount counts5 = fish5.countTypes();
    return t.checkExpect(fish0.count(), 0) && t.checkExpect(fish1.count(), 1)
        && t.checkExpect(counts5.smallCount, 5);
  }

  boolean testFeedingFrenzyCreateMF(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    IListFish fish0 = game.createMF(0, 0, new MtListFish());
    IListFish fish2 = game.createMF(0, 2, new MtListFish());
    IListFish fish4 = game.createMF(0, 4, new MtListFish());
    FishCount counts4 = fish4.countTypes();
    return t.checkExpect(fish0.count(), 0) && t.checkExpect(fish2.count(), 2)
        && t.checkExpect(counts4.mediumCount, 4);
  }

  boolean testFeedingFrenzyCreateLF(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    IListFish fish0 = game.createLF(0, 0, new MtListFish());
    IListFish fish2 = game.createLF(0, 2, new MtListFish());
    IListFish fish3 = game.createLF(0, 3, new MtListFish());
    FishCount counts3 = fish3.countTypes();
    return t.checkExpect(fish0.count(), 0) && t.checkExpect(fish2.count(), 2)
        && t.checkExpect(counts3.largeCount, 3);
  }

  boolean testFeedingFrenzyCheckCollisions(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    BackgroundFish small = new BackgroundFish(100, 100, 30, f4, 2.0);
    LargeFish large = new LargeFish(100, 100, 100, f3, 2.0, true);
    IListFish empty = new MtListFish();
    IListFish withSmall = new ConsListFish(small, new MtListFish());
    IListFish withLarge = new ConsListFish(large, new MtListFish());
    CollisionResult result1 = game.checkCollisions(player, empty);
    CollisionResult result2 = game.checkCollisions(player, withSmall);
    CollisionResult result3 = game.checkCollisions(player, withLarge);
    return t.checkExpect(result1.smallEaten, 0) && t.checkExpect(result2.smallEaten, 1)
        && t.checkExpect(result3.playerEaten, true);
  }

  // Fish draw() tests
  boolean testFishDraw(Tester t) {
    BackgroundFish fish1 = new BackgroundFish(100, 100, 30, f4, 2.0);
    MediumFish fish2 = new MediumFish(150, 150, 60, f2, 3.0, true);
    LargeFish fish3 = new LargeFish(200, 200, 100, f3, 2.0, false);

    WorldImage expected1 = new ScaleImage(F4, 30 / 500.0);
    WorldImage expected2 = new ScaleImageXY(F2, -60 / 500.0, 60 / 500.0);
    WorldImage expected3 = new ScaleImage(F3, 100 / 500.0);

    return t.checkExpect(fish1.draw(), expected1) && t.checkExpect(fish2.draw(), expected2)
        && t.checkExpect(fish3.draw(), expected3);
  }

  // IListFish drawFish() tests
  boolean testDrawFish(Tester t) {
    WorldScene emptyScene = new WorldScene(1200, 800);
    MtListFish empty = new MtListFish();
    BackgroundFish fish1 = new BackgroundFish(100, 100, 30, f4, 2.0);
    MediumFish fish2 = new MediumFish(150, 150, 60, f2, 3.0, true);
    ConsListFish list1 = new ConsListFish(fish1, new MtListFish());
    ConsListFish list2 = new ConsListFish(fish1, new ConsListFish(fish2, new MtListFish()));

    WorldScene scene1 = emptyScene.placeImageXY(fish1.draw(), 100, 100);
    WorldScene scene2 = scene1.placeImageXY(fish2.draw(), 150, 150);

    return t.checkExpect(empty.drawFish(emptyScene), emptyScene)
        && t.checkExpect(list1.drawFish(emptyScene), scene1)
        && t.checkExpect(list2.drawFish(emptyScene), scene2);
  }

  // IListFish moveAll() tests
  boolean testMoveAll(Tester t) {
    MtListFish empty = new MtListFish();
    BackgroundFish fish1 = new BackgroundFish(100, 100, 30, f4, 2.0);
    MediumFish fish2 = new MediumFish(150, 150, 60, f2, 3.0, true);
    ConsListFish list1 = new ConsListFish(fish1, new MtListFish());
    ConsListFish list2 = new ConsListFish(fish1, new ConsListFish(fish2, new MtListFish()));

    IListFish movedList1 = new ConsListFish(fish1.move(), new MtListFish());
    IListFish movedList2 = new ConsListFish(fish1.move(),
        new ConsListFish(fish2.move(), new MtListFish()));

    return t.checkExpect(empty.moveAll(), empty) && t.checkExpect(list1.moveAll(), movedList1)
        && t.checkExpect(list2.moveAll(), movedList2);
  }

  // IListFish countTypes() tests
  boolean testCountTypes(Tester t) {
    MtListFish empty = new MtListFish();
    BackgroundFish small1 = new BackgroundFish(50, 50, 30, f4, 2.0);
    BackgroundFish small2 = new BackgroundFish(100, 100, 30, f4, 2.0);
    MediumFish medium = new MediumFish(150, 150, 60, f2, 3.0, true);
    LargeFish large = new LargeFish(200, 200, 100, f3, 2.0, true);
    ConsListFish list1 = new ConsListFish(small1, new ConsListFish(medium, new MtListFish()));
    ConsListFish list2 = new ConsListFish(small1,
        new ConsListFish(small2, new ConsListFish(large, new MtListFish())));

    return t.checkExpect(empty.countTypes(), new FishCount(0, 0, 0))
        && t.checkExpect(list1.countTypes(), new FishCount(1, 1, 0))
        && t.checkExpect(list2.countTypes(), new FishCount(2, 0, 1));
  }

  // IListFish checkCollisions() tests
  boolean testCheckCollisions(Tester t) {
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    MtListFish empty = new MtListFish();
    BackgroundFish small = new BackgroundFish(100, 100, 30, f4, 2.0);
    LargeFish large = new LargeFish(100, 100, 100, f3, 2.0, true);
    ConsListFish list1 = new ConsListFish(small, new MtListFish());
    ConsListFish list2 = new ConsListFish(large, new MtListFish());

    return t.checkExpect(empty.checkCollisions(player, 2, 1, false),
        new CollisionResult(player, empty, 2, 1, false))
        && t.checkExpect(list1.checkCollisions(player, 0, 0, false),
            new CollisionResult(player, new MtListFish(), 1, 0, false))
        && t.checkExpect(list2.checkCollisions(player, 0, 0, false),
            new CollisionResult(player, list2, 0, 0, true));
  }

  // IListBoost drawBoosts() tests
  boolean testDrawBoosts(Tester t) {
    WorldScene emptyScene = new WorldScene(1200, 800);
    MtListBoost empty = new MtListBoost();
    SpeedBoost boost1 = new SpeedBoost(100, 100, 0.0);
    SpeedBoost boost2 = new SpeedBoost(200, 200, 0.0);
    ConsListBoost list1 = new ConsListBoost(boost1, new MtListBoost());
    ConsListBoost list2 = new ConsListBoost(boost1, new ConsListBoost(boost2, new MtListBoost()));

    WorldScene scene1 = emptyScene.placeImageXY(boost1.move().draw(), 100, 100);
    WorldScene scene2 = scene1.placeImageXY(boost2.move().draw(), 200, 200);

    return t.checkExpect(empty.drawBoosts(emptyScene), emptyScene)
        && t.checkExpect(list1.drawBoosts(emptyScene), scene1)
        && t.checkExpect(list2.drawBoosts(emptyScene), scene2);
  }

  // FeedingFrenzy makeScene() tests
  boolean testMakeScene(Tester t) {
    PlayerFish player = new PlayerFish(300, 300, 50, 5.0, 10.0, f1);
    BackgroundFish small = new BackgroundFish(100, 100, 30, f4, 2.0);
    MediumFish medium = new MediumFish(200, 200, 60, f2, 3.0, true);
    LargeFish large = new LargeFish(400, 400, 100, f3, 2.0, true);
    IListFish fishList = new ConsListFish(small,
        new ConsListFish(medium, new ConsListFish(large, new MtListFish())));
    FeedingFrenzy game1 = new FeedingFrenzy(player, fishList, new MtListBoost(), 100, 2, 1, 1, 3.0,
        false, false, 0, 0, 0, 0.0);

    WorldScene expectedScene = game1.getEmptyScene();
    expectedScene = expectedScene.placeImageXY(small.draw(), 100, 100);
    expectedScene = expectedScene.placeImageXY(medium.draw(), 200, 200);
    expectedScene = expectedScene.placeImageXY(large.draw(), 400, 400);
    expectedScene = expectedScene.placeImageXY(player.draw(), 300, 300);

    WorldImage scoreBg = new RectangleImage(200, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage scoreText = new TextImage("Score: 100", 24, FontStyle.BOLD, Color.WHITE);
    WorldImage scoreDisplay = new OverlayImage(scoreText, scoreBg);
    expectedScene = expectedScene.placeImageXY(scoreDisplay, 120, 30);

    WorldImage sizeBg = new RectangleImage(150, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage sizeText = new TextImage("Size: 50", 20, FontStyle.BOLD, Color.WHITE);
    WorldImage sizeDisplay = new OverlayImage(sizeText, sizeBg);
    expectedScene = expectedScene.placeImageXY(sizeDisplay, 120, 80);

    WorldImage livesBg = new RectangleImage(150, 40, OutlineMode.SOLID, new Color(0, 0, 0, 180));
    WorldImage livesDisplay = game1.drawLivesHelper(0, 2, new EmptyImage());
    WorldImage livesWithBg = new OverlayImage(livesDisplay, livesBg);
    expectedScene = expectedScene.placeImageXY(livesWithBg, 1100, 30);

    FeedingFrenzy gameWon = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 500, 3,
        0, 0, 0.0, true, false, 0, 0, 0, 0.0);
    FeedingFrenzy gameLost = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 200, 0,
        0, 0, 0.0, false, true, 0, 0, 0, 0.0);

    return t.checkExpect(game1.makeScene(), expectedScene)
        && t.checkExpect(gameWon.makeScene(),
            gameWon.makeEndScene("You Win! You're the apex predator!"))
        && t.checkExpect(gameLost.makeScene(), gameLost.makeEndScene("Game Over! You got eaten!"));
  }

  // FeedingFrenzy makeEndScene() tests
  boolean testMakeEndScene(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    WorldScene scene1 = game.getEmptyScene();
    WorldImage background = new RectangleImage(1200, 800, OutlineMode.SOLID, new Color(0, 0, 50));
    scene1 = scene1.placeImageXY(background, 600, 400);
    scene1 = scene1.placeImageXY(new TextImage("You Win!", 40, FontStyle.BOLD, Color.WHITE), 600,
        400);
    scene1 = scene1.placeImageXY(new TextImage("Final Score: 0", 30, FontStyle.BOLD, Color.YELLOW),
        600, 460);

    WorldScene scene2 = game.getEmptyScene();
    scene2 = scene2.placeImageXY(background, 600, 400);
    scene2 = scene2.placeImageXY(new TextImage("Game Over!", 40, FontStyle.BOLD, Color.WHITE), 600,
        400);
    scene2 = scene2.placeImageXY(new TextImage("Final Score: 0", 30, FontStyle.BOLD, Color.YELLOW),
        600, 460);

    WorldScene scene3 = game.getEmptyScene();
    scene3 = scene3.placeImageXY(background, 600, 400);
    scene3 = scene3.placeImageXY(new TextImage("Test", 40, FontStyle.BOLD, Color.WHITE), 600, 400);
    scene3 = scene3.placeImageXY(new TextImage("Final Score: 0", 30, FontStyle.BOLD, Color.YELLOW),
        600, 460);

    return t.checkExpect(game.makeEndScene("You Win!"), scene1)
        && t.checkExpect(game.makeEndScene("Game Over!"), scene2)
        && t.checkExpect(game.makeEndScene("Test"), scene3);
  }

  // FeedingFrenzy onTick() tests
  boolean testOnTick(Tester t) {
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    FeedingFrenzy gameWon = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 0, 3, 0,
        0, 0.0, true, false, 0, 0, 0, 0.0);
    FeedingFrenzy gameLost = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 0, 0, 0,
        0, 0.0, false, true, 0, 0, 0, 0.0);
    FeedingFrenzy normalGame = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 0, 3,
        0, 0, 0.0, false, false, 0, 0, 0, 0.0);

    FeedingFrenzy tickedGame = (FeedingFrenzy) normalGame.onTick();

    return t.checkExpect(gameWon.onTick(), gameWon) && t.checkExpect(gameLost.onTick(), gameLost)
        && t.checkExpect(tickedGame.mediumRespawnTimer, 120)
        && t.checkExpect(tickedGame.largeRespawnTimer, 120)
        && t.checkExpect(tickedGame.boostRespawnTimer, 180);
  }

  // FeedingFrenzy onKeyEvent() tests
  boolean testOnKeyEvent(Tester t) {
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    FeedingFrenzy game = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 0, 3, 0, 0,
        0.0, false, false, 0, 0, 0, 0.0);
    FeedingFrenzy gameWon = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 100, 3,
        0, 0, 0.0, true, false, 0, 0, 0, 0.0);

    PlayerFish upPlayer = player.setKeyPressed("up", true);
    PlayerFish leftPlayer = player.setKeyPressed("left", true);
    FeedingFrenzy expectedUp = new FeedingFrenzy(upPlayer, new MtListFish(), new MtListBoost(), 0,
        3, 0, 0, 0.0, false, false, 0, 0, 0, 0.0);
    FeedingFrenzy expectedLeft = new FeedingFrenzy(leftPlayer, new MtListFish(), new MtListBoost(),
        0, 3, 0, 0, 0.0, false, false, 0, 0, 0, 0.0);

    return t.checkExpect(game.onKeyEvent("up"), expectedUp)
        && t.checkExpect(game.onKeyEvent("left"), expectedLeft)
        && t.checkExpect(gameWon.onKeyEvent("up"), gameWon);
  }

  // FeedingFrenzy onKeyReleased() tests
  boolean testOnKeyReleased(Tester t) {
    PlayerFish player = new PlayerFish(100, 100, 50, 5.0, 10.0, f1);
    PlayerFish downPressed = player.setKeyPressed("down", true);
    PlayerFish rightPressed = player.setKeyPressed("right", true);
    FeedingFrenzy game1 = new FeedingFrenzy(downPressed, new MtListFish(), new MtListBoost(), 0, 3,
        0, 0, 0.0, false, false, 0, 0, 0, 0.0);
    FeedingFrenzy game2 = new FeedingFrenzy(rightPressed, new MtListFish(), new MtListBoost(), 0, 3,
        0, 0, 0.0, false, false, 0, 0, 0, 0.0);
    FeedingFrenzy gameLost = new FeedingFrenzy(player, new MtListFish(), new MtListBoost(), 200, 0,
        0, 0, 0.0, false, true, 0, 0, 0, 0.0);

    PlayerFish downReleased = downPressed.setKeyPressed("down", false);
    PlayerFish rightReleased = rightPressed.setKeyPressed("right", false);
    FeedingFrenzy expected1 = new FeedingFrenzy(downReleased, new MtListFish(), new MtListBoost(),
        0, 3, 0, 0, 0.0, false, false, 0, 0, 0, 0.0);
    FeedingFrenzy expected2 = new FeedingFrenzy(rightReleased, new MtListFish(), new MtListBoost(),
        0, 3, 0, 0, 0.0, false, false, 0, 0, 0, 0.0);

    return t.checkExpect(game1.onKeyReleased("down"), expected1)
        && t.checkExpect(game2.onKeyReleased("right"), expected2)
        && t.checkExpect(gameLost.onKeyReleased("down"), gameLost);
  }

  // FeedingFrenzy drawLivesHelper() tests
  boolean testDrawLivesHelper(Tester t) {
    FeedingFrenzy game = new FeedingFrenzy();
    WorldImage heart = new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.RED);
    WorldImage heartBottom = new CircleImage(7, OutlineMode.SOLID, Color.RED);
    WorldImage heartShape = new OverlayOffsetImage(heart, 0, -3,
        new BesideImage(heartBottom, heartBottom));

    WorldImage oneHeart = new BesideImage(new EmptyImage(), heartShape);
    WorldImage twoHearts = new BesideImage(oneHeart, heartShape);
    WorldImage threeHearts = new BesideImage(twoHearts, heartShape);

    return t.checkExpect(game.drawLivesHelper(0, 0, new EmptyImage()), new EmptyImage())
        && t.checkExpect(game.drawLivesHelper(0, 1, new EmptyImage()), oneHeart)
        && t.checkExpect(game.drawLivesHelper(0, 3, new EmptyImage()), threeHearts);
  }

  // Runs the game
  boolean testBigBang(Tester t) {
    FeedingFrenzy world = new FeedingFrenzy();
    return world.bigBang(WORLD_WIDTH, WORLD_HEIGHT, TICK_RATE);
  }
}