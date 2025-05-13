import tester.*;

// to represent a list of Strings
interface ILoString {

  // combine all Strings in this list into one
  String combine();

  // returns true if the list is sorted
  boolean isSorted();

  // returns a sublist of this list from start to end
  ILoString sublist(int start, int end);

  //
  ILoString interleave(ILoString other);
}

// to represent an empty list of Strings
class MtLoString implements ILoString {

  // constructor
  MtLoString() {
  }

  /*
   * TEMPLATE
   * FIELDS:
   * ... this ... -- ILoString
   * 
   * METHODS
   * ... this.combine() ... -- String
   * ... this.isSorted() ... -- boolean
   * ... this.sublist(int, int) ... -- ILoString
   * ... this.interleave(ILoString) ... -- ILoString
   * 
   * METHODS FOR FIELDS
   * 
   */

  // combine all Strings in this list into one
  public String combine() {
    return "";
  }

  // returns true if the list is sorted
  public boolean isSorted() {
    return true;
  }

  // returns a sublist of this list from start to end
  public ILoString sublist(int start, int end) {
    return this;
  }

  // returns a new list that is the interleaving of this list and the other list
  public ILoString interleave(ILoString other) {
    return other;
  }
}

// to represent a nonempty list of Strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  // constructor
  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE
   * FIELDS:
   * ... this.first ... -- String
   * ... this.rest ... -- ILoString
   * 
   * METHODS
   * ... this.combine() ... -- String
   * ... this.isSorted() ... -- boolean
   * ... this.sublist(int, int) ... -- ILoString
   * ... this.interleave(ILoString) ... -- ILoString
   * 
   * METHODS FOR FIELDS
   * ... this.first.concat(String) ... -- String
   * ... this.first.compareTo(String) ... -- int
   * ... this.rest.combine() ... -- String
   * 
   */

  // combine all Strings in this list into one
  public String combine() {
    return this.first.concat(this.rest.combine());
  }

  // returns true if the list is sorted
  public boolean isSorted() {
    return this.rest.isSorted() && this.first.compareTo(this.rest.combine()) <= 0;
  }

  // returns a sublist of this list from start to end
  public ILoString sublist(int start, int end) {
    if (start <= 0 && end > 0) {
      return new ConsLoString(this.first, this.rest.sublist(0, end - 1));
    }
    if (start > 0 && end > 0) {
      return this.rest.sublist(start - 1, end - 1);
    }

    return new MtLoString();
  }

  // returns a new list that is the interleaving of this list and the other list
  public ILoString interleave(ILoString other) {
    return new ConsLoString(this.first, other.interleave(this.rest));
  }

}

// to represent examples for lists of strings
class ExamplesStrings {

  ILoString mary = new ConsLoString("Mary ", new ConsLoString("had ", new ConsLoString("a ",
      new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))));

  ILoString bob = new ConsLoString("Can ", new ConsLoString("he ",
      new ConsLoString("build ", new ConsLoString("it.", new MtLoString()))));

  ILoString sortedA = new ConsLoString("a", new ConsLoString("b",
      new ConsLoString("c", new ConsLoString("d", new ConsLoString("e", new MtLoString())))));
  ILoString sortedB = new ConsLoString("a", new ConsLoString("c",
      new ConsLoString("c", new ConsLoString("e", new ConsLoString("e", new MtLoString())))));

  // test the method combine for the lists of Strings
  boolean testCombine(Tester t) {
    return t.checkExpect(this.mary.combine(), "Mary had a little lamb.");
  }

  boolean testILoStringSublist(Tester t) {
    return t.checkExpect(mary.sublist(1, 3), new ConsLoString("had ",
        new ConsLoString("a ", new ConsLoString("little ", new MtLoString()))));
  }

  boolean testILoStringInterleave(Tester t) {
    return t.checkExpect(mary.interleave(bob),
        new ConsLoString("Mary ",
            new ConsLoString("Can ", new ConsLoString("had ", new ConsLoString("he ",
                new ConsLoString("a ", new ConsLoString("build ", new ConsLoString("little ",
                    new ConsLoString("it.", new ConsLoString("lamb.", new MtLoString()))))))))));
  }

  // boolean testILoStringMerge(Tester t) {
  // return t
  // .checkExpect(sortedA.merge(sortedB),
  // new ConsLoString("a",
  // new ConsLoString("a",
  // new ConsLoString("b", new ConsLoString("c", new ConsLoString("c",
  // new ConsLoString("c", new ConsLoString("d", new ConsLoString("e",
  // new ConsLoString("e", new ConsLoString("e", new MtLoString())))))))))));
  // }

  // boolean testILoStringIsDoubledList(Tester t) {
  // return t.checkExpect(sortedA.isDoubledList(sortedA), true)
  // && t.checkExpect(sortedA.isDoubledList(bob), false);
  // }

}