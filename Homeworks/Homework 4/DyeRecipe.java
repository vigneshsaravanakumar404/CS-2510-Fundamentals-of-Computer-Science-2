import tester.Tester;

class Utils {

  // the constructor
  Utils() {
  }

  // Check if the color percentage is valid
  double checkRedPercentage(double red) {
    if (red >= 0) {
      return red;
    }
    throw new IllegalArgumentException("Red must be greater than 0");
  }

  // Check if the color percentage is valid
  double checkBluePercentage(double blue) {
    if (blue >= 0) {
      return blue;
    }
    throw new IllegalArgumentException("Blue must be greater than 0");
  }

  // Check if the color percentage is valid
  double checkYellowPercentage(double yellow, double blue) {
    if (yellow < 0) {
      throw new IllegalArgumentException("Yellow must be greater than 0");
    }
    if (yellow > 0 && blue > (yellow * 0.1)) {
      throw new IllegalArgumentException("If yellow present, blue must <= 1/10th of yellow");
    }

    return yellow;
  }

  // Check if the color percentage is valid
  double checkBlackPercentage(double red, double yellow, double blue, double black) {
    double otherColors = red + yellow + blue;

    if (black < 0) {
      throw new IllegalArgumentException("Black must be greater than 0");
    }
    if (!((black <= 0.05 * otherColors) || (otherColors <= 0.1 * black))) {
      throw new IllegalArgumentException("Black ratio constraint violated");
    }

    return black;
  }

  // Check if two double values are within a tolerance
  boolean withInTolerance(double a, double b) {
    return Math.abs(a - b) < 0.001;
  }

  double normalizeBlack(double red, double yellow, double blue, double black) {
    return Math.min(black / 2.0, 0.05 * ((red / 2.0) + (yellow / 2.0) + (blue / 2.0)));
  }
}

// represents a dye recipe with 4 colors
class DyeRecipe {
  double red;
  double yellow;
  double blue;
  double black;

  Utils utils = new Utils(); // should be made private but we can't use that :(

  // Constructor for 4 color recipe
  DyeRecipe(double red, double yellow, double blue, double black) {
    double r = this.utils.checkRedPercentage(red);
    double y = this.utils.checkYellowPercentage(yellow, blue);
    double b = this.utils.checkBluePercentage(blue);
    double l = this.utils.checkBlackPercentage(red, yellow, blue, black);
    double total = r + y + b + l;

    this.red = r / total;
    this.yellow = y / total;
    this.blue = b / total;
    this.black = l / total;
  }

  // Constructor for 3 color recipe
  DyeRecipe(double r, double y, double b) {
    this(r, y, Math.min(b, 0.1 * y), (r + y + Math.min(b, 0.1 * y)) * 0.05);
  }

  // Constructor for 2 color recipe
  DyeRecipe(DyeRecipe d1, DyeRecipe d2) {
    this((d1.red + d2.red) / 2.0, (d1.yellow + d2.yellow) / 2.0, (d1.blue + d2.blue) / 2.0,
        new Utils().normalizeBlack(d1.red + d2.red, d1.yellow + d2.yellow,
            d1.blue + d2.blue, d1.black + d2.black));
  }

  /*
   * TEMPLATE
   * FIELDS:
   * ... this.red ... -- double
   * ... this.yellow ... -- double
   * ... this.blue ... -- double
   * ... this.black ... -- double
   * METHODS:
   * ... this.sameRecipe(DyeRecipe) ... -- boolean
   * ... this.sameRecipe(double, double, double, double) ... -- boolean
   */

  // check if two recipes are the same
  boolean sameRecipe(DyeRecipe other) {
    /*
     * TEMPLATE
     * FIELDS:
     * ... this.red ... -- double
     * ... this.yellow ... -- double
     * ... this.blue ... -- double
     * ... this.black ... -- double
     * METHODS:
     * ... this.sameRecipe(DyeRecipe) ... -- boolean
     * ... this.sameRecipe(double, double, double, double) ... -- boolean
     */
    return other.sameRecipe(this.red, this.yellow, this.blue, this.black);
  }

  // check if a recipe is the same as the given values
  boolean sameRecipe(double red, double yellow, double blue, double black) {
    return this.utils.withInTolerance(this.red, red)
        && this.utils.withInTolerance(this.yellow, yellow)
        && this.utils.withInTolerance(this.blue, blue)
        && this.utils.withInTolerance(this.black, black);
  }
}

// for testing purposes

class DyeRecipeTest {

  DyeRecipe recipe1 = new DyeRecipe(10.0, 20.0, 1.0, 1.0);
  DyeRecipe recipe2 = new DyeRecipe(5.0, 10.0, 0.5);
  DyeRecipe recipe3 = new DyeRecipe(5.0, 10.0, 2.0);
  DyeRecipe recipe4 = new DyeRecipe(4.0, 8.0, 0.4, 0.5);
  DyeRecipe recipe5 = new DyeRecipe(6.0, 4.0, 0.4, 0.2);
  DyeRecipe recipe6 = new DyeRecipe(this.recipe1, new DyeRecipe(5.0, 10.0, 0.5, 0.5));

  // Test the 4-color constructor with valid inputs
  boolean testFourColorConstructorValid(Tester t) {
    return t.checkExpect(this.recipe1.red, 0.3125)
        && t.checkExpect(this.recipe1.yellow, 0.625)
        && t.checkExpect(this.recipe1.blue, 0.03125)
        && t.checkExpect(this.recipe1.black, 0.03125);
  }

  // Test the 4-color constructor with various invalid values
  boolean testFourColorConstructorInvalidInputs(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException("Red must be greater than 0"),
        "DyeRecipe", -1.0, 10.0, 1.0, 0.5)
        && t.checkConstructorException(
            new IllegalArgumentException("Yellow must be greater than 0"),
            "DyeRecipe", 10.0, -1.0, 1.0, 0.5)
        && t.checkConstructorException(
            new IllegalArgumentException("If yellow present, blue must <= 1/10th of yellow"),
            "DyeRecipe", 10.0, 5.0, 1.0, 0.5)
        && t.checkConstructorException(
            new IllegalArgumentException("Blue must be greater than 0"),
            "DyeRecipe", 10.0, 10.0, -1.0, 0.5)
        && t.checkConstructorException(
            new IllegalArgumentException("Black ratio constraint violated"),
            "DyeRecipe", 10.0, 10.0, 1.0, 5.0);
  }

  // Test the 3-color constructor (with and without blue cap)
  boolean testThreeColorConstructorCombined(Tester t) {
    return t.checkExpect(this.recipe2.red, 5.0 / 16.275)
        && t.checkExpect(this.recipe2.yellow, 10.0 / 16.275)
        && t.checkExpect(this.recipe2.blue, 0.5 / 16.275)
        && t.checkExpect(this.recipe2.black, 0.775 / 16.275)
        && t.checkExpect(this.recipe3.red, 5.0 / 16.8)
        && t.checkExpect(this.recipe3.yellow, 10.0 / 16.8)
        && t.checkExpect(this.recipe3.blue, 1.0 / 16.8)
        && t.checkExpect(this.recipe3.black, 0.8 / 16.8);
  }

  // Test the 2-color (mixing) constructor
  boolean testMixingConstructor(Tester t) {
    return t.checkExpect(this.recipe6.red, 7.5 / 24.0)
        && t.checkExpect(this.recipe6.yellow, 15.0 / 24.0)
        && t.checkExpect(this.recipe6.blue, 0.75 / 24.0)
        && t.checkExpect(this.recipe6.black, 0.75 / 24.0);
  }

  // Test the sameRecipe(DyeRecipe)
  boolean testSameRecipeObjectSame(Tester t) {
    double total = 4.0 + 8.0 + 0.4 + 0.5;
    return t.checkExpect(this.recipe4.sameRecipe(this.recipe4), true)
        && t.checkExpect(this.recipe4.sameRecipe(this.recipe5), false)
        && t.checkExpect(this.recipe4.sameRecipe(new DyeRecipe(4.0, 8.0, 0.4, 0.3)), false)
        && t.checkExpect(this.recipe4.sameRecipe(new DyeRecipe(4.0, 8.0, 0.4, 0.6)), false)
        && t.checkExpect(this.recipe4.sameRecipe(new DyeRecipe(4.0, 8.0, 0.4, 0.5)), true);
  }
}