abstract class ABeverage implements IBeverage {
  // TODO: Template
  // TODO: Purpose Statements

  String variety;

  ILoString mixins;

  ABeverage(String variety, ILoString mixins) {
    this.variety = variety;
    this.mixins = mixins;
  }

  public abstract boolean isDecaf();

  public boolean containsIngredient(String ingredient) {
    return mixins.containsIngredient(ingredient);
  }

  public abstract String format();

}