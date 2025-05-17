import tester.*;

// to represent a list of Strings
interface ILoString {

  // combine all Strings in this list into one
  String combine();

  // returns true if the list is sorted
  boolean isSorted();

  // returns true if the list is sorted
  boolean isSortedHelper(String prev);

  // returns a sublist of this list from start to end
  ILoString sublist(int start, int end);

  // returns the lists interleaved
  ILoString interleave(ILoString other);

  // returns the merged sorted list
  ILoString merge(ILoString other);

  // helps return the merged sorted list
  ILoString mergeHelper(String str, ILoString other);

  // to check if the list is a doubled list
  boolean isDoubledList();

  // helper for doubled list
  boolean isDoubledListHelper(String prev);

  // to check if the list is a palindrome
  boolean isPalindromeList();

  // reversethe list
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
   * 
   * METHODS:
   * ... this.combine() ... -- String
   * ... this.isSorted() ... -- boolean
   * ... this.isSortedHelper(String) ... -- boolean
   * ... this.sublist(int, int) ... -- ILoString
   * ... this.interleave(ILoString) ... -- ILoString
   * ... this.merge(ILoString) ... -- ILoString
   * ... this.mergeHelper(ConsLoString) ... -- ILoString
   * ... this.isDoubledList() ... -- boolean
   * ... this.isDoubledListHelper(String) ... -- boolean
   * ... this.isPalindromeList() ... -- boolean
   * ... this.reverse() ... -- ILoString
   * ... this.reverseAcc(ILoString) ... -- ILoString
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

  // returns true if the list is sorted
  public boolean isSortedHelper(String prev) {
    return true;
  }

  // returns a sublist of this list from start to end
  public ILoString sublist(int start, int end) {
    return this;
  }

  // returns a new list that is the interleaving of this list and the other list
  public ILoString interleave(ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus below
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return other;
  }

  // returns the merged sorted list
  public ILoString merge(ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return other;
  }

  // helps return the merged sorted list
  public ILoString mergeHelper(String str, ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return new ConsLoString(str, other);
  }

  // to check if the list is a doubled list
  public boolean isDoubledList() {
    return true;
  }

  // helper for doubled list
  public boolean isDoubledListHelper(String prev) {
    return false;
  }

  // to check if the list is a palindrome
  public boolean isPalindromeList() {
    return true;
  }

  // reverse
  public ILoString reverse() {
    return this.reverseAcc(new MtLoString());
  }

  // reverse helper with accumulator
  public ILoString reverseAcc(ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
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
   * METHODS:
   * ... this.combine() ... -- String
   * ... this.isSorted() ... -- boolean
   * ... this.isSortedHelper(String) ... -- boolean
   * ... this.sublist(int, int) ... -- ILoString
   * ... this.interleave(ILoString) ... -- ILoString
   * ... this.merge(ILoString) ... -- ILoString
   * ... this.mergeHelper(ConsLoString) ... -- ILoString
   * ... this.isDoubledList() ... -- boolean
   * ... this.isDoubledListHelper(String) ... -- boolean
   * ... this.isPalindromeList() ... -- boolean
   * ... this.reverse() ... -- ILoString
   * ... this.reverseAcc(ILoString) ... -- ILoString
   * 
   * METHODS FOR FIELDS:
   * ... this.rest.combine() ... -- String
   * ... this.rest.isSortedHelper(String) ... -- boolean
   * ... this.rest.sublist(int, int) ... -- ILoString
   * ... this.rest.interleave(ILoString) ... -- ILoString
   * ... this.rest.merge(ILoString) ... -- ILoString
   * ... this.rest.mergeHelper(ConsLoString) ... -- ILoString
   * ... this.rest.isDoubledListHelper(String) ... -- boolean
   * ... this.rest.reverseAcc(ILoString) ... -- ILoString
   */

  // combine all Strings in this list into one
  public String combine() {
    return this.first.concat(this.rest.combine());
  }

  // returns true if the list is sorted
  public boolean isSorted() {
    return this.rest.isSortedHelper(this.first);
  }

  // returns true if the list is sorted
  public boolean isSortedHelper(String prev) {
    return this.first.compareToIgnoreCase(prev) >= 0 && this.rest.isSortedHelper(this.first);
  }

  // returns a sublist of this list from start to end
  public ILoString sublist(int start, int end) {
    if (start <= 0 && end == -1) {
      return new MtLoString();
    }
    if (start <= 0) {
      return new ConsLoString(this.first, this.rest.sublist(start, end - 1));
    }
    return this.rest.sublist(start - 1, end - 1);

  }

  // returns a new list that is the interleaving of this list and the other list
  public ILoString interleave(ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return new ConsLoString(this.first, other.interleave(this.rest));
  }

  // returns the merged sorted list
  public ILoString merge(ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for ConsLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return other.mergeHelper(this.first, this.rest);
  }

  // helps return the merged sorted list
  public ILoString mergeHelper(String str, ILoString other) {
    /*
     * METHOD TEMPLATE: everything in the class template for ConsLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */

    if (this.first.compareToIgnoreCase(str) <= 0) {
      return new ConsLoString(this.first, this.rest.mergeHelper(str, other));
    }
    return (new ConsLoString(str, other)).mergeHelper(this.first, this.rest);
  }

  // to check if the list is a doubled list
  public boolean isDoubledList() {
    return this.rest.isDoubledListHelper(this.first);
  }

  // helper for doubled list
  public boolean isDoubledListHelper(String prev) {
    if (this.first.equals(prev)) {
      return this.rest.isDoubledList();
    }
    return false;
  }

  // to check if the list is a palindrome
  public boolean isPalindromeList() {
    return this.reverse().interleave(this).isDoubledList();
  }

  // reverse this
  public ILoString reverse() {
    return this.reverseAcc(new MtLoString());
  }

  // reverse helper with accumulator
  public ILoString reverseAcc(ILoString acc) {
    /*
     * METHOD TEMPLATE: everything in the class template for MtLoString, plus
     *
     * PARAMETERS:
     * ... other ... -- ILoString
     *
     * METHODS OF PARAMETERS:
     * ... other.combine() ... -- String
     * ... other.isSorted() ... -- boolean
     * ... other.isSortedHelp(String) ... -- boolean
     * ... other.sublist(int, int) ... -- ILoString
     * ... other.interleave(ILoString) ... -- ILoString
     * ... other.merge(ILoString) ... -- ILoString
     * ... other.mergeHelper(String, ILoString) ... -- ILoString
     * ... other.isDoubledList() ... -- boolean
     * ... other.isDoubledListHelp(String) ... -- boolean
     * ... other.isPalindromeList() ... -- boolean
     * ... other.length() ... -- int
     * ... other.reverse() ... -- ILoString
     * ... other.reverseHelp() ... -- ILoString
     * 
     */
    return this.rest.reverseAcc(new ConsLoString(this.first, acc));
  }

}

// to represent examples for lists of strings
class ExamplesStrings {

  ILoString emptyList = new MtLoString();
  ILoString mary = new ConsLoString("Mary ", new ConsLoString("had ", new ConsLoString("a ",
      new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))));
  ILoString sortedA = new ConsLoString("a", new ConsLoString("b",
      new ConsLoString("c", new ConsLoString("d", new ConsLoString("e", new MtLoString())))));
  ILoString sortedB = new ConsLoString("a", new ConsLoString("c",
      new ConsLoString("c", new ConsLoString("e", new ConsLoString("e", new MtLoString())))));

  // Tests for combine method
  boolean testCombine(Tester t) {
    return t.checkExpect(this.mary.combine(), "Mary had a little lamb.") &&
        t.checkExpect(this.emptyList.combine(), "") &&
        t.checkExpect(this.sortedA.combine(), "abcde") &&
        t.checkExpect(this.sortedB.combine(), "accee");
  }

  // Tests for isSorted method
  boolean testIsSorted(Tester t) {
    return t.checkExpect(this.mary.isSorted(), false) &&
        t.checkExpect(this.sortedA.isSorted(), true) &&
        t.checkExpect(this.sortedB.isSorted(), true) &&
        t.checkExpect(this.emptyList.isSorted(), true);
  }

  // tests for isSortedHelper method
  boolean testIsSortedHelper(Tester t) {
    return t.checkExpect(this.mary.isSortedHelper("Mary "), false) &&
        t.checkExpect(this.sortedA.isSortedHelper("a"), true) &&
        t.checkExpect(this.sortedB.isSortedHelper("a"), true) &&
        t.checkExpect(this.emptyList.isSortedHelper(""), true);
  }

  // tests for sublist method
  boolean testILoStringSublist(Tester t) {
    return t.checkExpect(this.mary.sublist(1, 3), new ConsLoString("had ",
        new ConsLoString("a ", new ConsLoString("little ", new MtLoString()))))
        && t.checkExpect(this.mary.sublist(0, 1), new ConsLoString("Mary ",
            new ConsLoString("had ", new MtLoString())))
        && t.checkExpect(this.mary.sublist(2, 5), new ConsLoString("a ",
            new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString()))))
        && t.checkExpect(this.mary.sublist(0, 5), this.mary);
  }

  // tests for interleave method
  boolean testILoStringInterleave(Tester t) {
    ILoString bob = new ConsLoString("Can ", new ConsLoString("he ",
        new ConsLoString("build ", new ConsLoString("it.", new MtLoString()))));

    return t.checkExpect(this.mary.interleave(bob),
        new ConsLoString("Mary ",
            new ConsLoString("Can ", new ConsLoString("had ", new ConsLoString("he ",
                new ConsLoString("a ", new ConsLoString("build ", new ConsLoString("little ",
                    new ConsLoString("it.", new ConsLoString("lamb.", new MtLoString()))))))))));
  }

  // tests for merge
  boolean testILoStringMerge(Tester t) {
    return t
        .checkExpect(this.sortedA.merge(this.sortedB),
            new ConsLoString("a",
                new ConsLoString("a",
                    new ConsLoString("b", new ConsLoString("c", new ConsLoString("c",
                        new ConsLoString("c", new ConsLoString("d", new ConsLoString("e",
                            new ConsLoString("e", new ConsLoString("e", new MtLoString())))))))))))
        && t.checkExpect(this.sortedA.merge(this.emptyList), this.sortedA)
        && t.checkExpect(this.emptyList.merge(this.sortedA), this.sortedA)
        && t.checkExpect(this.emptyList.merge(this.emptyList),
            this.emptyList);
  }

  // tests for mergeHelper
  boolean testILoStringmergeHelper(Tester t) {
    return t.checkExpect(this.sortedA.mergeHelper("a", this.sortedB),
        new ConsLoString("a",
            new ConsLoString("a",
                new ConsLoString("a", new ConsLoString("b", new ConsLoString("c",
                    new ConsLoString("c", new ConsLoString("c", new ConsLoString("d",
                        new ConsLoString("e", new ConsLoString("e", new ConsLoString("e", new MtLoString()))))))))))))
        && t.checkExpect(this.emptyList.mergeHelper("a", this.sortedB),
            new ConsLoString("a", this.sortedB))
        && t.checkExpect(this.sortedA.mergeHelper("a", this.emptyList),
            new ConsLoString("a", this.sortedA));
  }

  // Tests for isDoubledList method
  boolean testIsDoubledList(Tester t) {
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

    return t.checkExpect(this.emptyList.isDoubledList(), true) && // Empty list edge case
        t.checkExpect(perfectDoubled.isDoubledList(), true) &&
        t.checkExpect(notDoubled.isDoubledList(), false) &&
        t.checkExpect(oddLength.isDoubledList(), false) &&
        t.checkExpect(singlePair.isDoubledList(), true);
  }

  // tests for isDoubledListHelper method
  boolean testIsDoubledListHelper(Tester t) {
    ILoString singleElement = new ConsLoString("hello", new MtLoString());
    ILoString doubled = new ConsLoString("apple",
        new ConsLoString("apple",
            new ConsLoString("banana",
                new ConsLoString("banana", new MtLoString()))));
    ILoString notDoubled = new ConsLoString("apple",
        new ConsLoString("orange", new MtLoString()));

    return t.checkExpect(singleElement.isDoubledListHelper("hello"), true) &&
        t.checkExpect(doubled.isDoubledListHelper("apple"), false) &&
        t.checkExpect(notDoubled.isDoubledListHelper("apple"), false);
  }

  // Tests for isPalindromeList method
  boolean testIsPalindromeList(Tester t) {

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

    return t.checkExpect(this.emptyList.isPalindromeList(), true) &&
        t.checkExpect(singleElement.isPalindromeList(), true) &&
        t.checkExpect(oddPalindrome.isPalindromeList(), true) &&
        t.checkExpect(evenPalindrome.isPalindromeList(), true) &&
        t.checkExpect(notPalindrome.isPalindromeList(), false);
  }

  // tests for reverse method
  boolean testReverse(Tester t) {
    return t.checkExpect(this.mary.reverse(),
        new ConsLoString("lamb.", new ConsLoString("little ",
            new ConsLoString("a ", new ConsLoString("had ",
                new ConsLoString("Mary ", new MtLoString()))))))
        && t.checkExpect(this.emptyList.reverse(), this.emptyList)
        && t.checkExpect(this.sortedA.reverse(),
            new ConsLoString("e", new ConsLoString("d", new ConsLoString("c",
                new ConsLoString("b", new ConsLoString("a", new MtLoString()))))));
  }

  // tests for reverseAcc method
  boolean testReverseAcc(Tester t) {
    return t.checkExpect(this.mary.reverseAcc(new MtLoString()),
        new ConsLoString("lamb.", new ConsLoString("little ",
            new ConsLoString("a ", new ConsLoString("had ",
                new ConsLoString("Mary ", new MtLoString()))))))
        && t.checkExpect(this.emptyList.reverseAcc(new MtLoString()), this.emptyList)
        && t.checkExpect(this.sortedA.reverseAcc(new MtLoString()),
            new ConsLoString("e", new ConsLoString("d", new ConsLoString("c",
                new ConsLoString("b", new ConsLoString("a", new MtLoString()))))));
  }
}