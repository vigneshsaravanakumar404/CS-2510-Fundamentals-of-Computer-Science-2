import tester.Tester;

// Represents a drink you might get at a coffee shop
interface IBeverage {

  // returns if a drink is decaffeinated
  boolean isDecaf();

  // returns true if the drink contains the given ingredient
  boolean containsIngredient(String ingredient);

  // returns a string representation of the drink
  String format();

}

// Represents a drink you might get at a coffee shop
abstract class ABeverage implements IBeverage {

  String variety;
  ILoString mixins;

  // the constructor
  ABeverage(String variety, ILoString mixins) {
    this.variety = variety;
    this.mixins = mixins;
  }

  /*
   * TEMPLATE
   * 
   * FIELDS:
   * ... this.variety ... -- String
   * ... this.mixins ... -- ILoString
   * 
   * METHODS:
   * ... this.isDecaf() ... -- boolean
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * ... this.formatHelp(String, String) ... -- String
   * 
   * METHODS FOR FIELDS:
   * ... this.mixins.containsString() ... -- boolean
   * ... this.mixins.format() ... -- String
   * ... this.mixins.formatHelp() ... -- String
   */

  // return if the beverage is decaf
  public abstract boolean isDecaf();

  // return if the beverage contains the ingredient
  public boolean containsIngredient(String ingredient) {
    return this.mixins.containsIngredient(ingredient);
  }

  // return the string representation of the beverage
  public abstract String format();

}

// Represents a beverage
class BubbleTea extends ABeverage {

  int size;

  // the constructor
  BubbleTea(String variety, ILoString mixins, int size) {
    super(variety, mixins);
    this.size = size;
  }

  /*
   * TEMPLATE
   * 
   * FIELDS:
   * ... this.variety ... -- String
   * ... this.mixins ... -- ILoString
   * ... this.size ... -- int
   * 
   * METHODS:
   * ... this.isDecaf() ... -- boolean
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * ... this.formatHelp(String, String) ... -- String
   * 
   */

  // return if the beverage is decaf
  public boolean isDecaf() {
    return this.variety.equals("Rooibos");
  }

  // return if the beverage contains the ingredient
  public String format() {
    String mixIns = this.mixins.format();

    if (mixIns.equals("")) {
      return this.size + "oz " + this.variety + " (without mixins)";
    }

    return this.size + "oz " + this.variety + " (with " + mixIns + ")";
  }
}

// represents a coffee beverage
class Coffee extends ABeverage {

  String style;
  boolean isIced;

  // the constructor
  Coffee(String variety, ILoString mixins, String style, boolean isIced) {
    super(variety, mixins);
    this.style = style;
    this.isIced = isIced;
  }

  /*
   * TEMPLATE
   * 
   * FIELDS:
   * ... this.variety ... -- String
   * ... this.mixins ... -- ILoString
   * ... this.style ... -- String
   * ... this.isIced ... -- boolean
   * 
   * METHODS:
   * ... this.isDecaf() ... -- boolean
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * ... this.formatHelp(String, String) ... -- String
   * 
   */

  // return if the beverage is decaf
  public boolean isDecaf() {
    return false;
  }

  // return if the beverage contains the ingredient
  public String format() {
    String mixIns = this.mixins.format();
    String iced = "Hot ";

    if (this.isIced) {
      iced = "Iced ";
    }
    if (mixIns.equals("")) {
      return iced + this.variety + " " + this.style + " (without mixins)";
    }

    return iced + this.variety + " " + this.style + " (with " + mixIns + ")";
  }
}

// Represents a beverage
class Milkshake extends ABeverage {

  String brandName;
  int size;

  // the constructor
  Milkshake(String flavor, ILoString toppings, String brandName, int size) {
    super(flavor, toppings);
    this.brandName = brandName;
    this.size = size;
  }

  /*
   * TEMPLATE
   * 
   * FIELDS:
   * ... this.variety ... -- String
   * ... this.mixins ... -- ILoString
   * ... this.brandName ... -- String
   * ... this.size ... -- int
   * 
   * METHODS:
   * ... this.isDecaf() ... -- boolean
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * ... this.formatHelp(String, String) ... -- String
   * 
   */

  // return if the beverage is decaf
  public boolean isDecaf() {
    return true;
  }

  // return if the beverage contains the ingredient
  public String format() {
    String mixIns = this.mixins.format();

    if (mixIns.equals("")) {
      return this.size + "oz " + this.brandName + " " + this.variety + " (without mixins)";
    }

    return this.size + "oz " + this.brandName + " " + this.variety + " (with " + mixIns + ")";
  }
}

// represents a list of strings
interface ILoString {

  // return if the beverage contains the ingredient
  boolean containsIngredient(String ingredient);

  // return the string representation of the beverage
  String format();
}

// represents an empty list of strings
class MtLoString implements ILoString {

  // the constructor
  MtLoString() {
  }

  /*
   * TEMPLATE
   * 
   * METHODS:
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * 
   */

  // return if the beverage is decaf
  public boolean containsIngredient(String ingredient) {
    return false;
  }

  // return if the beverage contains the ingredient
  public String format() {
    return "";
  }

}

// represents a non-empty list of strings
class ConsLoString implements ILoString {

  String first;
  ILoString rest;

  // the constructor
  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE
   * 
   * FIELDS:
   * ... this.first ... -- String
   * ... this.rest ... -- ILoString
   * 
   * 
   * METHODS:
   * ... this.containsIngredient(String) ... -- boolean
   * ... this.format() ... -- String
   * 
   * METHODS FOR FIELDS:
   * ... this.rest.containsIngredient() ... -- boolean
   * ... this.rest.format() ... -- String
   * 
   */

  // return if the beverage is decaf
  public boolean containsIngredient(String ingredient) {
    return this.first.equals(ingredient) || this.rest.containsIngredient(ingredient);
  }

  // return if the beverage contains the ingredient
  public String format() {
    if (this.rest.format().equals("")) {
      return this.first;
    }
    return this.first + ", " + this.rest.format();
  }
}

// Examples and tests for the beverages
class ExamplesBeverages {

  IBeverage b1 = new BubbleTea("Black tea", new ConsLoString("boba",
      new MtLoString()), 16);
  IBeverage b2 = new Coffee("Arabica", new ConsLoString("cream",
      new MtLoString()), "espresso", false);
  IBeverage b3 = new Milkshake("vanilla", new ConsLoString("whipped cream",
      new MtLoString()), "Ben&Jerrys", 12);
  IBeverage b4 = new BubbleTea("Oolong", new ConsLoString("extra sugar",
      new MtLoString()), 20);
  IBeverage b5 = new Coffee("Robusta", new ConsLoString("sugar",
      new MtLoString()), "americano", true);
  IBeverage b6 = new Milkshake("mint-chip", new ConsLoString("sprinkles",
      new MtLoString()), "JPLicks", 16);
  IBeverage b7 = new BubbleTea("Rooibos", new MtLoString(), 16);
  IBeverage b8 = new Milkshake("chocolate", new ConsLoString("whipped cream",
      new ConsLoString("cherry",
          new ConsLoString("sprinkles", new MtLoString()))),
      "HaagenDazs", 20);

  // test cases for the isDecaf method
  boolean testIsDecaf(Tester t) {
    return t.checkExpect(this.b1.isDecaf(), false)
        && t.checkExpect(this.b2.isDecaf(), false)
        && t.checkExpect(this.b3.isDecaf(), true)
        && t.checkExpect(this.b4.isDecaf(), false)
        && t.checkExpect(this.b5.isDecaf(), false)
        && t.checkExpect(this.b6.isDecaf(), true)
        && t.checkExpect(this.b7.isDecaf(), true)
        && t.checkExpect(this.b8.isDecaf(), true);
  }

  // test cases for the containsIngredient method
  boolean testContainsIngredient(Tester t) {
    return t.checkExpect(this.b1.containsIngredient("boba"), true)
        && t.checkExpect(this.b2.containsIngredient("cream"), true)
        && t.checkExpect(this.b3.containsIngredient("whipped cream"), true)
        && t.checkExpect(this.b4.containsIngredient("extra sugar"), true)
        && t.checkExpect(this.b5.containsIngredient("sugar"), true)
        && t.checkExpect(this.b6.containsIngredient("sprinkles"), true)
        && t.checkExpect(this.b7.containsIngredient("boba"), false)
        && t.checkExpect(this.b8.containsIngredient("cherry"), true);
  }

  // test cases for the format method
  boolean testFormat(Tester t) {
    return t.checkExpect(this.b1.format(), "16oz Black tea (with boba)")
        && t.checkExpect(this.b2.format(), "Hot Arabica espresso (with cream)")
        && t.checkExpect(this.b3.format(), "12oz Ben&Jerrys vanilla (with whipped cream)")
        && t.checkExpect(this.b4.format(), "20oz Oolong (with extra sugar)")
        && t.checkExpect(this.b5.format(), "Iced Robusta americano (with sugar)")
        && t.checkExpect(this.b6.format(), "16oz JPLicks mint-chip (with sprinkles)")
        && t.checkExpect(this.b7.format(), "16oz Rooibos (without mixins)")
        && t.checkExpect(this.b8.format(),
            "20oz HaagenDazs chocolate (with whipped cream, cherry, sprinkles)");
  }
}