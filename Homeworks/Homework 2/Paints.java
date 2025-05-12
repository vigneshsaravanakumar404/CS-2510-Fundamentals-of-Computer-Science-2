import java.awt.Color;
import tester.Tester;

interface IPaint {

  Color getFinalColor();

  // IPaint invert();

  int countPaints();

  int countMixes();

  int formulaDepth();

  String mixingFormula(int depth);
}

class Solid implements IPaint {
  String name;
  Color color;

  Solid(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  public Color getFinalColor() {
    return this.color;
  }

  public int countPaints() {
    return 1;
  }

  public int countMixes() {
    return 0;
  }

  public int formulaDepth() {
    return 0;
  }

  public String mixingFormula(int depth) {
    return this.name;
  }

  // public IPaint invert() {
  // return this;
  // }

}

// *

class Combo implements IPaint {
  String name;
  IMixture operation;

  Combo(String name, IMixture operation) {
    this.name = name;
    this.operation = operation;
  }

  public Color getFinalColor() {
    return this.operation.getFinalColor();
  }

  public int countPaints() {
    return this.operation.countPaints();
  }

  public int countMixes() {
    return this.operation.countMixes();
  }

  public int formulaDepth() {
    return this.operation.formulaDepth();
  }

  public String mixingFormula(int depth) {
    if (depth == 0) {
      return this.name;
    }

    return this.operation.mixingFormula(depth);
  }

  // public IPaint invert() {
  // return new Combo(this.name, this.operation.invert());
  // }
}

interface IMixture {

  Color getFinalColor();

  // IPaint invert();

  int countPaints();

  int countMixes();

  int formulaDepth();

  String mixingFormula(int depth);

}

class Darken implements IMixture {
  IPaint paint;

  Darken(IPaint paint) {
    this.paint = paint;
  }

  public Color getFinalColor() {
    return this.paint.getFinalColor().darker();
  }

  public int countPaints() {
    return 1 + this.paint.countPaints();
  }

  public int countMixes() {
    return 1 + this.paint.countMixes();
  }

  public int formulaDepth() {
    return 1 + this.paint.formulaDepth();
  }

  public String mixingFormula(int depth) {
    return "darken(" + this.paint.mixingFormula(depth - 1) + ")";
  }

  // public IPaint invert() {
  // return new Brighten(this.paint);
  // }

}

class Brighten implements IMixture {
  IPaint paint;

  Brighten(IPaint paint) {
    this.paint = paint;
  }

  public Color getFinalColor() {
    return this.paint.getFinalColor().brighter();
  }

  public int countPaints() {
    return 1 + this.paint.countPaints();
  }

  public int countMixes() {
    return 1 + this.paint.countMixes();
  }

  public int formulaDepth() {
    return 1 + this.paint.formulaDepth();
  }

  public String mixingFormula(int depth) {
    return "brighten(" + this.paint.mixingFormula(depth - 1) + ")";
  }

  // public Color invert() {
  // return new Darken(this.paint).getFinalColor();
  // }

}

class Blend implements IMixture {
  IPaint paint1;
  IPaint paint2;

  Blend(IPaint paint1, IPaint paint2) {
    this.paint1 = paint1;
    this.paint2 = paint2;
  }

  public Color getFinalColor() {
    Color color1 = this.paint1.getFinalColor();
    Color color2 = this.paint2.getFinalColor();
    return new Color(
        ((color1.getRed() + color2.getRed()) / 2),
        (color1.getGreen() + color2.getGreen()) / 2,
        (color1.getBlue() + color2.getBlue()) / 2);
  }

  public int countPaints() {
    return this.paint1.countPaints() + this.paint2.countPaints();
  }

  public int countMixes() {
    return 1 + this.paint1.countMixes() + this.paint2.countMixes();
  }

  public int formulaDepth() {
    return 1 + Math.max(this.paint1.formulaDepth(), this.paint2.formulaDepth());
  }

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

  IPaint darkGreen = new Combo("dark green", new Darken(green));
  IPaint brown = new Combo("brown", new Blend(darkGreen, red));
  IPaint bronze = new Combo("bronze", new Brighten(brown));
  IMixture darkPurpleM = new Darken(purple);
  IMixture brightOrangeM = new Brighten(bronze);
  IMixture coralM = new Blend(pink, khaki);
  IPaint brightOrange = new Combo("bright orange", new Brighten(bronze));

  // Test getFinalColor
  boolean testGetFinalColor(Tester t) {
    return t.checkExpect(pink.getFinalColor(), new Color(181, 90, 90));
  }

  // test the method countMixes for the interface IPaint
  boolean testIPaintCountMixes(Tester t) {
    return t.checkExpect(this.darkPurple.countMixes(), 2)
        && t.checkExpect(this.brightOrange.countMixes(), 4)
        && t.checkExpect(this.pink.countMixes(), 4);
  }

  // test the method countMixes for the interface IMixture
  boolean testIMixtureCountMixes(Tester t) {
    return t.checkExpect(this.darkPurpleM.countMixes(), 2)
        && t.checkExpect(this.brightOrangeM.countMixes(), 4)
        && t.checkExpect(this.coralM.countMixes(), 6);
  }
}
