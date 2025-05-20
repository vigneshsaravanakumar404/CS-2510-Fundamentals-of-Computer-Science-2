import tester.Tester;

// represents a Person
interface IAT {

  // returns if the person contains the name
  boolean containsName(String name);

  boolean duplicateNames();
}

// represents an Unknown Person
class Unknown implements IAT {

  // returns if the person contains the name
  public boolean containsName(String name) {
    return false;
  }

  public boolean duplicateNames() {
    return false;
  }

}

// represents
class Person implements IAT {
  String name;
  IAT dad, mom;

  Person(String name, IAT dad, IAT mom) {
    this.name = name;
    this.dad = dad;
    this.mom = mom;
  }

  // returns if the person contains the name
  public boolean containsName(String n) {
    return this.name.equals(n) || this.mom.containsName(n) || this.dad.containsName(n);
  }

  public boolean duplicateNames() {
    return this.dad.duplicateNames() || this.mom.duplicateNames()
        || (this.dad.containsName(this.name) || this.mom.containsName(this.name));
  }

}

class Number2 {
  IAT davisSr = new Person("Davis", new Unknown(), new Unknown());
  IAT edna = new Person("Edna", new Unknown(), new Unknown());
  IAT davisJr = new Person("Davis", davisSr, edna);
  IAT carl = new Person("Carl", new Unknown(), new Unknown());
  IAT candace = new Person("Candace", davisJr, new Unknown());
  IAT claire = new Person("Claire", new Unknown(), new Unknown());
  IAT bill = new Person("Bill", carl, candace);
  IAT bree = new Person("Bree", new Unknown(), claire);
  IAT anthony = new Person("Anthony", bill, bree);

  boolean testContainsName(Tester t) {
    return t.checkExpect(this.davisSr.containsName("Davis"), true)
        && t.checkExpect(this.candace.containsName("Davis"), true)
        && t.checkExpect(this.anthony.containsName("Edna"), true)
        && t.checkExpect(this.davisSr.containsName("Edna"), false);
  }

  boolean testduplicateName(Tester t) {
    return t.checkExpect(this.davisSr.duplicateNames(), false)
        && t.checkExpect(this.candace.duplicateNames(), true)
        && t.checkExpect(this.anthony.duplicateNames(), true);
  }
}