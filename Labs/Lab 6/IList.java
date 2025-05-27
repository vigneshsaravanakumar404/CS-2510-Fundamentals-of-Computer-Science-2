import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import tester.Tester;

//represents a generic list
interface IList<T> {
  // filter this list using the given predicate
  IList<T> filter(Predicate<T> pred);

  // map a given function onto every member of this list and return a list of the
  // results
  <U> IList<U> map(Function<T, U> converter);

  // combine the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> converter, U initial);
}

// represents a generic empty list
class MtList<T> implements IList<T> {

  MtList() {
  }

  /*
   * TEMPLATE
   * 
   * METHODS:
   * ... this.filter(Predicate<T> pred) ... -- IList<T>
   * ... this.map(Function<T, U> converter) ... -- IList<U>
   * ... this.fold(BiFunction<T, U, U> converter, U initial) ... -- U
   */

  // filter this empty list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    return new MtList<T>();
  }

  // map a given function onto every member of this list and return a list of the
  // results
  public <U> IList<U> map(Function<T, U> converter) {
    return new MtList<U>();
  }

  // combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return initial;
  }

}

// represents a generic non-empty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * CLASS TEMPLATE
   * 
   * FIELDS:
   * ... this.first ... -- T
   * ... this.rest ... -- IList<T>
   * 
   * METHODS:
   * ... this.filter(Predicate<T> pred) ... -- IList<T>
   * ... this.map(Function<T, U> converter) ... -- IList<U>
   * ... this.fold(BiFunction<T, U, U> converter, U initial) ... -- U
   * 
   * 
   * METHODS ON/OF/FOR FIELDS:
   * ... this.rest.filter(Predicate<T> pred) ... -- IList<T>
   * ... this.rest.map(Function<T, U> converter) ... -- IList<U>
   * ... this.rest.fold(BiFunction<T, U, U> converter, U initial) ... -- U
   */

  // filter this non-empty list using the given predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    } else {
      return this.rest.filter(pred);
    }
  }

  // map a given function onto every member of this list and return a list of the
  // results
  public <U> IList<U> map(Function<T, U> converter) {
    return new ConsList<U>(converter.apply(this.first), this.rest.map(converter));
  }

  // combine the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> converter, U initial) {
    return converter.apply(this.first, this.rest.fold(converter, initial));
  }
}

class ExamplesLists {

  ExamplesLists() {
  }

  // Months
  IList<String> empty = new MtList<String>();
  IList<String> january = new ConsList<String>("January", this.empty);
  IList<String> february = new ConsList<String>("February", this.january);
  IList<String> march = new ConsList<String>("March", this.february);
  IList<String> april = new ConsList<String>("April", this.march);
  IList<String> may = new ConsList<String>("May", this.april);
  IList<String> june = new ConsList<String>("June", this.may);
  IList<String> july = new ConsList<String>("July", this.june);
  IList<String> august = new ConsList<String>("August", this.july);
  IList<String> september = new ConsList<String>("September", this.august);
  IList<String> october = new ConsList<String>("October", this.september);
  IList<String> november = new ConsList<String>("November", this.october);
  IList<String> december = new ConsList<String>("December", this.november);

  boolean teststartingWithJ(Tester t) {
    return t.checkExpect(this.december.filter(s -> s.startsWith("J")),
        new ConsList<String>("July",
            new ConsList<String>("June",
                new ConsList<String>("January", new MtList<String>()))))
        && t.checkExpect(this.december.filter(s -> s.startsWith("M")),
            new ConsList<String>("May",
                new ConsList<String>("March",
                    new MtList<String>())))
        && t.checkExpect(this.december.filter(s -> s.startsWith("Z")),
            new MtList<String>());
  }

  boolean testfindMonthsCountEndingWithER(Tester t) {
    return t.checkExpect(this.december.filter(s -> s.endsWith("er"))
        .fold((s, count) -> count + 1, 0), 4)
        && t.checkExpect(this.december.filter(s -> s.endsWith("y"))
            .fold((s, count) -> count + 1, 0), 4)
        && t.checkExpect(this.december.filter(s -> s.endsWith("z"))
            .fold((s, count) -> count + 1, 0), 0);
  }

  boolean testcreateThreeLetterAbbreviations(Tester t) {
    return t.checkExpect(this.december.map(s -> s.substring(0, 3).toUpperCase()),
        new ConsList<String>("DEC",
            new ConsList<String>("NOV",
                new ConsList<String>("OCT",
                    new ConsList<String>("SEP",
                        new ConsList<String>("AUG",
                            new ConsList<String>("JUL",
                                new ConsList<String>("JUN",
                                    new ConsList<String>("MAY",
                                        new ConsList<String>("APR",
                                            new ConsList<String>("MAR",
                                                new ConsList<String>("FEB",
                                                    new ConsList<String>("JAN", this.empty)))))))))))))
        && t.checkExpect(this.december.map(s -> s.length()),
            new ConsList<Integer>(8,
                new ConsList<Integer>(8,
                    new ConsList<Integer>(7,
                        new ConsList<Integer>(9,
                            new ConsList<Integer>(6,
                                new ConsList<Integer>(4,
                                    new ConsList<Integer>(4,
                                        new ConsList<Integer>(3,
                                            new ConsList<Integer>(5,
                                                new ConsList<Integer>(5,
                                                    new ConsList<Integer>(8,
                                                        new ConsList<Integer>(7, new MtList<Integer>())))))))))))))
        && t.checkExpect(this.december.map(s -> s.toLowerCase()),
            new ConsList<String>("december",
                new ConsList<String>("november",
                    new ConsList<String>("october",
                        new ConsList<String>("september",
                            new ConsList<String>("august",
                                new ConsList<String>("july",
                                    new ConsList<String>("june",
                                        new ConsList<String>("may",
                                            new ConsList<String>("april",
                                                new ConsList<String>("march",
                                                    new ConsList<String>("february",
                                                        new ConsList<String>("january", this.empty)))))))))))));
  }

}