import java.awt.Color;
import tester.Tester;

// to represent paint objects that can be used in mixing formulas
interface IPaint {
  // returns the final color of this paint
  // Example: redPaint.getFinalColor() -> Color object with RGB values for red
  Color getFinalColor();

  // counts the total number of solid paint objects in this paint's formula
  // Example: redPaint.countPaints() -> 1, mixedPaint.countPaints() -> 2
  int countPaints();

  // counts the number of mix operations used to create this paint
  // Example: redPaint.countMixes() -> 0, (red + blue).countMixes() -> 1
  int countMixes();

  // returns the maximum depth of this paint's mixing formula
  // Example: redPaint.formulaDepth() -> 0, (red + blue).formulaDepth() -> 1
  int formulaDepth();

  // returns a string representation of this paint's mixing formula with the given
  // maximum depth
  // Example: redPaint.mixingFormula(0) -> "Red", mixedPaint.mixingFormula(1) ->
  // "Red + Blue"
  String mixingFormula(int depth);

  // TODO:
  // IPaint invert();
}

// to represent a solid paint color
class Solid implements IPaint {
  String name;
  Color color;

  // constructs a solid paint with the given name and color
  Solid(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.name ... -- String
   * ... this.color ... -- Color
   * 
   * Methods:
   * ... this.getFinalColor() ... -- Color
   * ... this.countPaints() ... -- int
   * ... this.countMixes() ... -- int
   * ... this.formulaDepth() ... -- int
   * ... this.mixingFormula(int) ... -- String
   */

  // returns the color of this paint
  public Color getFinalColor() {
    return this.color;
  }

  // counts the number of solid paints in this paint
  public int countPaints() {
    return 1;
  }

  // counts the number of mixtures in this paint
  public int countMixes() {
    return 0;
  }

  // determines the maximum depth of the formula for this paint
  public int formulaDepth() {
    return 0;
  }

  // produces a string representation of the mixing formula to the given depth
  public String mixingFormula(int depth) {
    return this.name;
  }

  // commented out method left as is
  // public IPaint invert() {
  // return this;
  // }
}

// to represent a named combination of paint mixtures
class Combo implements IPaint {
  String name;
  IMixture operation;

  // constructs a combo paint with the given name and mixture operation
  Combo(String name, IMixture operation) {
    this.name = name;
    this.operation = operation;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.name ... -- String
   * ... this.operation ... -- IMixture
   * 
   * Methods:
   * ... this.getFinalColor() ... -- Color
   * ... this.countPaints() ... -- int
   * ... this.countMixes() ... -- int
   * ... this.formulaDepth() ... -- int
   * ... this.mixingFormula(int) ... -- String
   */

  // returns the final color of this combo paint
  public Color getFinalColor() {
    return this.operation.getFinalColor();
  }

  // counts the number of solid paints in this combo
  public int countPaints() {
    return this.operation.countPaints();
  }

  // counts the number of mixtures in this combo
  public int countMixes() {
    return this.operation.countMixes();
  }

  // determines the maximum depth of the formula for this combo
  public int formulaDepth() {
    return this.operation.formulaDepth();
  }

  // produces a string representation of the mixing formula to the given depth
  public String mixingFormula(int depth) {
    if (depth == 0) {
      return this.name;
    }

    return this.operation.mixingFormula(depth);
  }

  // commented out method left as is
  // public IPaint invert() {
  // return new Combo(this.name, this.operation.invert());
  // }
}

// to represent paint mixtures with operations that can be performed on them
interface IMixture {
  // returns the final color resulting from this mixture
  // Example: redBlue.getFinalColor() -> Color object representing purple
  Color getFinalColor();

  // counts the number of solid paints used in this mixture
  // Example: redBlue.countPaints() -> 2
  int countPaints();

  // counts the number of mix operations in this mixture
  // Example: redBlue.countMixes() -> 1, (redBlue + yellow).countMixes() -> 2
  int countMixes();

  // determines the maximum depth of the formula for this mixture
  // Example: redBlue.formulaDepth() -> 1, (redBlue + yellow).formulaDepth() -> 2
  int formulaDepth();

  // produces a string representation of the mixing formula to the given depth
  // Example: redBlue.mixingFormula(1) -> "Red + Blue", redBlue.mixingFormula(0)
  // -> "Purple"
  String mixingFormula(int depth);

  // commented out method left as is
  // IPaint invert();
}

// to represent a darkening operation on a paint
class Darken implements IMixture {
  IPaint paint;

  // constructs a darkening operation applied to the given paint
  Darken(IPaint paint) {
    this.paint = paint;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.paint ... -- IPaint
   * 
   * Methods:
   * ... this.getFinalColor() ... -- Color
   * ... this.countPaints() ... -- int
   * ... this.countMixes() ... -- int
   * ... this.formulaDepth() ... -- int
   * ... this.mixingFormula(int) ... -- String
   */

  // returns the darkened color of the paint
  public Color getFinalColor() {
    return this.paint.getFinalColor().darker();
  }

  // counts the total number of solid paints in this operation
  public int countPaints() {
    return 1 + this.paint.countPaints();
  }

  // counts the number of mixing operations including this darkening
  public int countMixes() {
    return 1 + this.paint.countMixes();
  }

  // determines the maximum depth of the formula including this darkening
  public int formulaDepth() {
    return 1 + this.paint.formulaDepth();
  }

  // produces a string representation of the darkening formula to the given depth
  public String mixingFormula(int depth) {
    return "darken(" + this.paint.mixingFormula(depth - 1) + ")";
  }

  // commented out method left as is
  // public IPaint invert() {
  // return new Brighten(this.paint);
  // }
}

// to represent a brightening operation on a paint
class Brighten implements IMixture {
  IPaint paint;

  // constructs a brightening operation applied to the given paint
  Brighten(IPaint paint) {
    this.paint = paint;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.paint ... -- IPaint
   * 
   * Methods:
   * ... this.getFinalColor() ... -- Color
   * ... this.countPaints() ... -- int
   * ... this.countMixes() ... -- int
   * ... this.formulaDepth() ... -- int
   * ... this.mixingFormula(int) ... -- String
   */

  // returns the brightened color of the paint
  public Color getFinalColor() {
    return this.paint.getFinalColor().brighter();
  }

  // counts the total number of solid paints in this operation
  public int countPaints() {
    return 1 + this.paint.countPaints();
  }

  // counts the number of mixing operations including this brightening
  public int countMixes() {
    return 1 + this.paint.countMixes();
  }

  // determines the maximum depth of the formula including this brightening
  public int formulaDepth() {
    return 1 + this.paint.formulaDepth();
  }

  // produces a string representation of the brightening formula to the given
  // depth
  public String mixingFormula(int depth) {
    return "brighten(" + this.paint.mixingFormula(depth - 1) + ")";
  }

  // commented out method left as is
  // public Color invert() {
  // return new Darken(this.paint).getFinalColor();
  // }
}

// to represent a blending operation between two paints
class Blend implements IMixture {
  IPaint paint1;
  IPaint paint2;

  // constructs a blend operation between the two given paints
  Blend(IPaint paint1, IPaint paint2) {
    this.paint1 = paint1;
    this.paint2 = paint2;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.paint1 ... -- IPaint
   * ... this.paint2 ... -- IPaint
   * 
   * Methods:
   * ... this.getFinalColor() ... -- Color
   * ... this.countPaints() ... -- int
   * ... this.countMixes() ... -- int
   * ... this.formulaDepth() ... -- int
   * ... this.mixingFormula(int) ... -- String
   */

  // returns the color that results from blending the two paints
  public Color getFinalColor() {
    return new Color((paint1.getFinalColor().getRed() / 2) + (paint2.getFinalColor().getRed() / 2),
        (paint1.getFinalColor().getGreen() / 2) + (paint2.getFinalColor().getGreen() / 2),
        (paint1.getFinalColor().getBlue() / 2) + (paint2.getFinalColor().getBlue() / 2));
  }

  // counts the total number of solid paints in this blend
  public int countPaints() {
    return this.paint1.countPaints() + this.paint2.countPaints();
  }

  // counts the number of mixing operations including this blend
  public int countMixes() {
    return 1 + this.paint1.countMixes() + this.paint2.countMixes();
  }

  // determines the maximum depth of the formula including this blend
  public int formulaDepth() {
    return 1 + Math.max(this.paint1.formulaDepth(), this.paint2.formulaDepth());
  }

  // produces a string representation of the blending formula to the given depth
  public String mixingFormula(int depth) {
    return "blend(" + this.paint1.mixingFormula(depth - 1) + ", "
        + this.paint2.mixingFormula(depth - 1) + ")";
  }
}

class ExamplesPaint {

  // Solids
  IPaint red = new Solid("red", new Color(255, 0, 0));
  IPaint green = new Solid("green", new Color(0, 255, 0));
  IPaint blue = new Solid("blue", new Color(0, 0, 255));

  // Combos
  IPaint purple = new Combo("purple", new Blend(red, blue));
  IPaint khaki = new Combo("khaki", new Blend(red, green));
  IPaint darkPurple = new Combo("dark purple", new Darken(purple));
  IPaint yellow = new Combo("yellow", new Brighten(khaki));
  IPaint mauve = new Combo("mauve", new Blend(purple, khaki));
  IPaint pink = new Combo("pink", new Brighten(mauve));
  IPaint coral = new Combo("coral", new Blend(pink, khaki));

  // Additional examples for each mixture type
  IPaint teal = new Combo("teal", new Blend(green, blue));
  IPaint darkKhaki = new Combo("dark khaki", new Darken(khaki));
  IPaint lightPurple = new Combo("light purple", new Brighten(purple));

  // Test getFinalColor()
  boolean testGetFinalColor(Tester t) {
    // Test a solid color
    return t.checkExpect(red.getFinalColor(), new Color(255, 0, 0))

        // Test simple blend (Purple is blend of red and blue)
        && t.checkExpect(purple.getFinalColor(), new Color(127, 0, 127))

        // Test darkened color
        && t.checkExpect(darkPurple.getFinalColor(), purple.getFinalColor().darker())

        // Test brightened color
        && t.checkExpect(yellow.getFinalColor(), khaki.getFinalColor().brighter())

        // Test complex blend (mauve is blend of purple and khaki)
        && t.checkExpect(mauve.getFinalColor(), new Color(126, 63, 63))

        // Test brightened complex blend (pink is brightened mauve)
        && t.checkExpect(pink.getFinalColor(), mauve.getFinalColor().brighter())

        // Test deeply nested combination (coral is blend of pink and khaki)
        && t.checkExpect(coral.getFinalColor(),
            new Color((pink.getFinalColor().getRed() + khaki.getFinalColor().getRed()) / 2,
                (pink.getFinalColor().getGreen() + khaki.getFinalColor().getGreen()) / 2,
                (pink.getFinalColor().getBlue() + khaki.getFinalColor().getBlue()) / 2));
  }

  boolean testCountPaints(Tester t) {
    // Test solid paints (each should count as 1)
    return t.checkExpect(red.countPaints(), 1)

        // Test simple blends (purple is blend of red and blue: 1+1=2)
        && t.checkExpect(purple.countPaints(), 2)
        && t.checkExpect(khaki.countPaints(), 2)

        // Test darkened (darkPurple is purple + black: 2+1=3)
        && t.checkExpect(darkPurple.countPaints(), 3)

        // Test brightened (yellow is khaki + white: 2+1=3)
        && t.checkExpect(yellow.countPaints(), 3)
        && t.checkExpect(mauve.countPaints(), 4)

        // Test brightened complex blend (pink is brightened mauve: 4+1=5)
        && t.checkExpect(pink.countPaints(), 5)

        // Test deeply nested combinations (coral is blend of pink and khaki: 5+2=7)
        && t.checkExpect(coral.countPaints(), 7);
  }

  boolean testCountMixes(Tester t) {
    // Test solid paints (each should have 0 mixes)
    return t.checkExpect(red.countMixes(), 0)

        // Test simple blends (purple is blend of red and blue: 1 mix)
        && t.checkExpect(purple.countMixes(), 1)
        && t.checkExpect(khaki.countMixes(), 1)

        // purple has 1 mix + 1 for darkening = 2 total
        && t.checkExpect(darkPurple.countMixes(), 2)

        // khaki has 1 mix + 1 for brightening = 2 total
        && t.checkExpect(yellow.countMixes(), 2)

        // purple has 1 mix + khaki has 1 mix + 1 for blending them = 3 total
        && t.checkExpect(mauve.countMixes(), 3)

        // mauve has 3 mixes + 1 for brightening = 4 total
        && t.checkExpect(pink.countMixes(), 4)

        // pink has 4 mixes + khaki has 1 mix + 1 for blending them = 6 total
        && t.checkExpect(coral.countMixes(), 6);
  }

  boolean testFormulaDepth(Tester t) {
    // Test solid paints (each should have depth 0)
    return t.checkExpect(red.formulaDepth(), 0)

        // Test simple blends (purple is blend of red and blue: depth 1)
        && t.checkExpect(purple.formulaDepth(), 1)
        && t.checkExpect(khaki.formulaDepth(), 1)

        // purple has depth 1, and darkening adds 1 level = depth 2
        && t.checkExpect(darkPurple.formulaDepth(), 2)

        // khaki has depth 1, and brightening adds 1 level = depth 2
        && t.checkExpect(yellow.formulaDepth(), 2)
        && t.checkExpect(mauve.formulaDepth(), 2)
        && t.checkExpect(pink.formulaDepth(), 3)
        && t.checkExpect(coral.formulaDepth(), 4);
  }

  boolean testMixingFormula(Tester t) {
    // Test solid paints at different depths
    return t.checkExpect(red.mixingFormula(0), "red")
        && t.checkExpect(red.mixingFormula(1), "red")
        && t.checkExpect(red.mixingFormula(5), "red")

        && t.checkExpect(purple.mixingFormula(0), "purple")
        && t.checkExpect(purple.mixingFormula(1), "blend(red, blue)")
        && t.checkExpect(purple.mixingFormula(2), "blend(red, blue)")

        && t.checkExpect(coral.mixingFormula(0), "coral")
        && t.checkExpect(coral.mixingFormula(1), "blend(pink, khaki)")
        && t.checkExpect(coral.mixingFormula(2), "blend(brighten(mauve), blend(red, green))")
        && t.checkExpect(coral.mixingFormula(3), "blend(brighten(blend(purple, khaki)), blend(red, green))")
        && t.checkExpect(coral.mixingFormula(4),
            "blend(brighten(blend(blend(red, blue), blend(red, green))), blend(red, green))");
  }
}