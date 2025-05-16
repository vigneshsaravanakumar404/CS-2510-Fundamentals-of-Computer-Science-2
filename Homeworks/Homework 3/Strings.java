import tester.*;

// to represent a list of Strings
interface ILoString {

  // combine all Strings in this list into one
  String combine();

  // returns true if the list is sorted
  boolean isSorted();

  // returns a sublist of this list from start to end
  ILoString sublist(int start, int end);

  // returns the lists interleaved
  ILoString interleave(ILoString other);

  // merges this list with another
  ILoString merge(ILoString other);

  ILoString mergeHelper(ConsLoString other);

  // to check if the list is a doubled list
  boolean isDoubledList();

  // helper for doubled list
  boolean isDoubledListHelper(String prev);

  // to check if the list is a palindrome
  boolean isPalindromeList();

  // reverse
  ILoString reverse();

  // reverse helper with accumulator
  ILoString reverseAcc(ILoString acc);

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

  // merging with empty list returns the other list
  public ILoString merge(ILoString other) {
    return other;
  }

  // merging a nonempty list with empty list returns the nonempty list
  public ILoString mergeHelper(ConsLoString other) {
    return other;
  }

  // to check if the list is a doubled list
  public boolean isDoubledList() {
    return true;
  }

  // helper for doubled list
  public boolean isDoubledListHelper(String prev) {
    return false;
  }

  public boolean isPalindromeList() {
    return true;
  }

  // reverse
  public ILoString reverse() {
    return this.reverseAcc(new MtLoString());
  }

  // reverse helper with accumulator
  public ILoString reverseAcc(ILoString acc) {
    return acc;
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
    if (start == 0 && end == -1) {
      return new MtLoString();
    }
    if (start == 0) {
      return new ConsLoString(this.first, this.rest.sublist(start, end - 1));
    }
    return this.rest.sublist(start - 1, end - 1);

  }

  // returns a new list that is the interleaving of this list and the other list
  public ILoString interleave(ILoString other) {
    return new ConsLoString(this.first, other.interleave(this.rest));
  }

  // merges this non-empty list with another
  public ILoString merge(ILoString other) {
    return other.mergeHelper(this); // double dispatch
  }

  // merges a non-empty list with another non-empty list
  public ILoString mergeHelper(ConsLoString that) {
    if (this.first.toLowerCase().compareTo(that.first.toLowerCase()) <= 0) {
      return new ConsLoString(this.first, this.rest.merge(that));
    } else {
      return new ConsLoString(that.first, this.merge(that.rest));
    }
  }

  public boolean isDoubledList() {
    return this.rest.isDoubledListHelper(this.first);
  }

  public boolean isDoubledListHelper(String prev) {
    if (this.first.equals(prev)) {
      return this.rest.isDoubledList();
    }
    return false;
  }

  public boolean isPalindromeList() {
    return this.reverse().interleave(this).isDoubledList();
  }

  // reverse this
  public ILoString reverse() {
    return this.reverseAcc(new MtLoString());
  }

  // reverse helper with accumulator
  public ILoString reverseAcc(ILoString acc) {
    return this.rest.reverseAcc(new ConsLoString(this.first, acc));
  }

}

// to represent examples for lists of strings
class ExamplesStrings {

  ILoString emptyList = new MtLoString();
  ILoString mary = new ConsLoString("Mary ", new ConsLoString("had ", new ConsLoString("a ",
      new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))));
  ILoString bob = new ConsLoString("Can ", new ConsLoString("he ",
      new ConsLoString("build ", new ConsLoString("it.", new MtLoString()))));
  ILoString sortedA = new ConsLoString("a", new ConsLoString("b",
      new ConsLoString("c", new ConsLoString("d", new ConsLoString("e", new MtLoString())))));
  ILoString sortedB = new ConsLoString("a", new ConsLoString("c",
      new ConsLoString("c", new ConsLoString("e", new ConsLoString("e", new MtLoString())))));
  ILoString singleElement = new ConsLoString("hello", new MtLoString());
  ILoString oddPalindrome = new ConsLoString("first",
      new ConsLoString("middle",
          new ConsLoString("first", new MtLoString())));
  ILoString evenPalindrome = new ConsLoString("start",
      new ConsLoString("middle",
          new ConsLoString("middle",
              new ConsLoString("start", new MtLoString()))));
  ILoString notPalindrome = new ConsLoString("first",
      new ConsLoString("second",
          new ConsLoString("third", new MtLoString())));
  ILoString perfectDoubled = new ConsLoString("apple",
      new ConsLoString("apple",
          new ConsLoString("banana",
              new ConsLoString("banana",
                  new ConsLoString("cherry",
                      new ConsLoString("cherry", new MtLoString()))))));
  ILoString notDoubled = new ConsLoString("apple",
      new ConsLoString("orange",
          new ConsLoString("banana",
              new ConsLoString("grape", new MtLoString()))));
  ILoString oddLength = new ConsLoString("apple",
      new ConsLoString("apple",
          new ConsLoString("banana", new MtLoString())));
  ILoString singlePair = new ConsLoString("hello",
      new ConsLoString("hello", new MtLoString()));

  boolean testCombine(Tester t) {
    return t.checkExpect(this.mary.combine(), "Mary had a little lamb.");
  }

  boolean testILoStringSublist(Tester t) {
    return t.checkExpect(this.mary.sublist(1, 3), new ConsLoString("had ",
        new ConsLoString("a ", new ConsLoString("little ", new MtLoString()))));
  }

  boolean testILoStringInterleave(Tester t) {
    return t.checkExpect(this.mary.interleave(bob),
        new ConsLoString("Mary ",
            new ConsLoString("Can ", new ConsLoString("had ", new ConsLoString("he ",
                new ConsLoString("a ", new ConsLoString("build ", new ConsLoString("little ",
                    new ConsLoString("it.", new ConsLoString("lamb.", new MtLoString()))))))))));
  }

  boolean testILoStringMerge(Tester t) {
    return t
        .checkExpect(sortedA.merge(sortedB),
            new ConsLoString("a",
                new ConsLoString("a",
                    new ConsLoString("b", new ConsLoString("c", new ConsLoString("c",
                        new ConsLoString("c", new ConsLoString("d", new ConsLoString("e",
                            new ConsLoString("e", new ConsLoString("e", new MtLoString())))))))))));
  }

  // Tests for isDoubledList method
  boolean testIsDoubledList(Tester t) {
    return t.checkExpect(this.emptyList.isDoubledList(), true) && // Empty list edge case
        t.checkExpect(this.perfectDoubled.isDoubledList(), true) &&
        t.checkExpect(this.notDoubled.isDoubledList(), false) &&
        t.checkExpect(this.oddLength.isDoubledList(), false) &&
        t.checkExpect(this.singlePair.isDoubledList(), true);
  }

  // Tests for isPalindromeList method
  boolean testIsPalindromeList(Tester t) {
    return t.checkExpect(this.emptyList.isPalindromeList(), true) &&
        t.checkExpect(this.singleElement.isPalindromeList(), true) &&
        t.checkExpect(this.oddPalindrome.isPalindromeList(), true) &&
        t.checkExpect(this.evenPalindrome.isPalindromeList(), true) &&
        t.checkExpect(this.notPalindrome.isPalindromeList(), false);
  }

}