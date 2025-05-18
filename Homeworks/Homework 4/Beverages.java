import tester.Tester;

class BubbleTea extends ABeverage {
  // TODO: Template
  // TODO: Purpose Statements

  int size;

  BubbleTea(String variety, int size, ILoString mixins) {
    super(variety, mixins);
    this.size = size;
  }

  public boolean isDecaf() {
    return this.variety.equals("Rooibos");
  }

  public String format() {
    String mixIns = this.mixins.format();

    if (mixIns.equals("")) {
      return this.size + "oz " + this.variety + " (without mixins)";
    }

    return this.size + "oz " + this.variety + " (with " + mixIns + ")";
  }
}

class Coffee extends ABeverage {
  // TODO: Template
  // TODO: Purpose Statements

  String style;

  boolean isIced;

  Coffee(String variety, String style, boolean isIced, ILoString mixins) {
    super(variety, mixins);
    this.style = style;
    this.isIced = isIced;
  }

  public boolean isDecaf() {
    return false;
  }

  public String format() {
    String mixIns = this.mixins.format();
    String iced = "Hot ";

    if (this.isIced) {
      iced = "Iced ";
    }
    if (mixIns.equals("")) {
      return iced + this.variety + " (without mixins)";
    }

    return iced + this.variety + " (with " + mixIns + ")";
  }
}

class Milkshake extends ABeverage {
  // TODO: Template
  // TODO: Purpose Statements

  String brandName;

  int size;

  Milkshake(String flavor, String brandName, int size, ILoString toppings) {
    super(flavor, toppings);
    this.brandName = brandName;
    this.size = size;
  }

  public boolean isDecaf() {
    return true;
  }

  public String format() {
    String mixIns = this.mixins.format();

    if (mixIns.equals("")) {
      return this.size + "oz " + this.brandName + " " + this.variety + " (without toppings)";
    }

    return this.size + "oz " + this.brandName + " " + this.variety + " (with " + mixIns + ")";
  }
}

interface ILoString {
  // TODO: Template
  // TODO: Purpose Statements

  boolean containsIngredient(String ingredient);

  String format();
}

class MtLoString implements ILoString {
  // TODO: Template
  // TODO: Purpose Statements

  MtLoString() {
  }

  public boolean containsIngredient(String ingredient) {
    return false;
  }

  public String format() {
    return "";
  }

}

class ConsLoString implements ILoString {
  // TODO: Template
  // TODO: Purpose Statements

  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  public boolean containsIngredient(String ingredient) {
    return this.first.equals(ingredient) || this.rest.containsIngredient(ingredient);
  }

  public String format() {
    if (this.rest.format().equals("")) {
      return this.first;
    }
    return this.first + ", " + this.rest.format();
  }
}

class ExamplesBeverages {

  IBeverage b1 = new BubbleTea("Black tea", 16, new ConsLoString("boba", new MtLoString()));
  IBeverage b2 = new Coffee("Arabica", "espresso", false, new ConsLoString("cream", new MtLoString()));
  IBeverage b3 = new Milkshake("vanilla", "Ben&Jerrys", 12, new ConsLoString("whipped cream", new MtLoString()));
  IBeverage b4 = new BubbleTea("Oolong", 20, new ConsLoString("extra sugar", new MtLoString()));
  IBeverage b5 = new Coffee("Robusta", "americano", true, new ConsLoString("sugar", new MtLoString()));
  IBeverage b6 = new Milkshake("mint-chip", "JPLicks", 16, new ConsLoString("sprinkles", new MtLoString()));

  boolean testIsDecaf(Tester t) {
    return t.checkExpect(this.b1.isDecaf(), false)
        && t.checkExpect(this.b2.isDecaf(), false)
        && t.checkExpect(this.b3.isDecaf(), true)
        && t.checkExpect(this.b4.isDecaf(), false)
        && t.checkExpect(this.b5.isDecaf(), false)
        && t.checkExpect(this.b6.isDecaf(), true);

    // TODO: include rooibos in the test cases
  }

  boolean testContainsIngredient(Tester t) {
    return t.checkExpect(this.b1.containsIngredient("boba"), true)
        && t.checkExpect(this.b2.containsIngredient("cream"), true)
        && t.checkExpect(this.b3.containsIngredient("whipped cream"), true)
        && t.checkExpect(this.b4.containsIngredient("extra sugar"), true)
        && t.checkExpect(this.b5.containsIngredient("sugar"), true)
        && t.checkExpect(this.b6.containsIngredient("sprinkles"), true);
    // TODO: include no mixins in the test cases
  }

  boolean testFormat(Tester t) {
    return t.checkExpect(this.b1.format(), "16oz Black tea (with boba)")
        && t.checkExpect(this.b2.format(), "Hot Arabica (with cream)")
        && t.checkExpect(this.b3.format(), "12oz Ben&Jerrys vanilla (with whipped cream)")
        && t.checkExpect(this.b4.format(), "20oz Oolong (with extra sugar)")
        && t.checkExpect(this.b5.format(), "Iced Robusta (with sugar)")
        && t.checkExpect(this.b6.format(), "16oz JPLicks mint-chip (with sprinkles)");
    // TODO: include no mixins in the test cases
  }
}