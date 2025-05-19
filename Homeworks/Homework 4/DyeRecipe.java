// represents a dye recipe with 4 colors
public class DyeRecipe {
  double red;
  double yellow;
  double blue;
  double black;

  private Utils utils = new Utils();

  // Constructor for 4 color recipe
  public DyeRecipe(double red, double yellow, double blue, double black) {

    double r = this.utils.checkRedPercentage(red);
    double y = this.utils.checkYellowPercentage(yellow, blue);
    double b = this.utils.checkBluePercentage(blue);
    double l = this.utils.checkBlackPercentage(red, yellow, blue, black);

    normalizeColors(r, y, b, l);
  }

  // Constructor for 3 color recipe
  public DyeRecipe(double red, double yellow, double blue) {
    double b = Math.min(blue, 0.1 * yellow);

    normalizeColors(red, yellow, b, (red + yellow + b) * 0.05);
  }

  // Constructor for 2 color recipe
  public DyeRecipe(DyeRecipe d1, DyeRecipe d2) {
    double totalRed = (d1.red + d2.red) / 2.0;
    double totalYellow = (d1.yellow + d2.yellow) / 2.0;
    double totalBlue = (d1.blue + d2.blue) / 2.0;
    double otherColors = totalRed + totalYellow + totalBlue;
    double totalBlack = Math.min((d1.black + d2.black) / 2.0, 0.05 * otherColors);

    normalizeColors(totalRed, totalYellow, totalBlue, totalBlack);
  }

  // check if two recipes are the same
  boolean sameRecipe(DyeRecipe other) {
    return other.sameRecipe(this.red, this.yellow, this.blue, this.black);
  }

  // check if two recipes are the same
  boolean sameRecipe(double red, double yellow, double blue, double black) {
    return this.utils.withInTolerance(this.red, red)
        && this.utils.withInTolerance(this.yellow, yellow)
        && this.utils.withInTolerance(this.blue, blue)
        && this.utils.withInTolerance(this.black, black);
  }

  // normalize the color percentages
  private void normalizeColors(double red, double yellow, double blue, double black) {
    double total = red + yellow + blue + black;

    this.red = red / total;
    this.yellow = yellow / total;
    this.blue = blue / total;
    this.black = black / total;
  }

}

// for testing purposes
class Utils {

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
}