// Represents a drink you might get at a coffee shop
interface IBeverage {

  // returns if a drink is decaffeinated
  boolean isDecaf();

  // returns true if the drink contains the given ingredient
  boolean containsIngredient(String ingredient);

  // returns a string representation of the drink
  String format();

}