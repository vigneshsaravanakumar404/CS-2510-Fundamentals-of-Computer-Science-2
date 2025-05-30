import tester.Tester;

// First, the IFunc interface from lectures
interface IFunc<Arg, Ret> {

  // Represents a function that takes an argument of type Arg and returns a value
  // of type Ret
  Ret apply(Arg arg);
}

// represents a function that returns a square of a Doubles
class SquareNum implements IFunc<Double, Double> {

  // returns the square of its argument
  public Double apply(Double d) {
    return d * d;
  }
}

// represents a function that returns the sine of a Double
class SineNum implements IFunc<Double, Double> {

  // returns the sine of its argument
  public Double apply(Double d) {
    return Math.sin(d);
  }
}

// represents a function that returns its argument unchanged
class Identity<T> implements IFunc<T, T> {

  // returns its argument unchanged
  public T apply(T t) {
    return t;
  }
}

// represents a function that adds n to its argument
class PlusN implements IFunc<Double, Double> {
  double n;

  // the constructor
  PlusN(double n) {
    this.n = n;
  }

  // returns the sum of its argument and n
  public Double apply(Double d) {
    return d + this.n;
  }
}

// represents a function that composes two functions
class FunctionComposition<Arg, Mid, Ret> implements IFunc<Arg, Ret> {
  IFunc<Arg, Mid> f;
  IFunc<Mid, Ret> g;

  // the constructor
  FunctionComposition(IFunc<Arg, Mid> f, IFunc<Mid, Ret> g) {
    this.f = f;
    this.g = g;
  }

  // applies the composition of f and g to its argument
  public Ret apply(Arg arg) {
    return g.apply(f.apply(arg));
  }
}

// represents a function that takes two arguments and returns a result
interface IFunc2<Arg1, Arg2, Ret> {
  Ret apply(Arg1 arg1, Arg2 arg2);
}

/*
 * What ComposeFunctions does:
 * - takes two functions and returns their composition
 * ComposeFunctions is a function that takes two functions as
 * arguments and returns a new function as its result. It's a higher-order
 * function that produces functions
 *
 * How it differs from FunctionComposition:
 * - FunctionComposition IS a function (implements IFunc) that applies
 * composition when called
 * - ComposeFunctions IS a two-argument function that PRODUCES a composed
 * function
 * They are related:
 * - ComposeFunctions.apply returns what FunctionComposition
 * represents
 */
// represents a function that composes two functions
class ComposeFunctions<Arg, Mid, Ret>
    implements IFunc2<IFunc<Arg, Mid>, IFunc<Mid, Ret>, IFunc<Arg, Ret>> {

  // applies the composition of two functions
  public IFunc<Arg, Ret> apply(IFunc<Arg, Mid> f1, IFunc<Mid, Ret> f2) {
    return new FunctionComposition<Arg, Mid, Ret>(f1, f2);
  }
}

// represents a list of T
interface IList<T> {
  // Generic foldl with the most general signature
  <U> U foldl(IFunc2<U, T, U> f, U base);
}

// represents an empty list of T
class MtList<T> implements IList<T> {

  // returns the result of foldl
  public <U> U foldl(IFunc2<U, T, U> f, U base) {
    return base;
  }
}

// represents a non-empty list of T
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // the constructor
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns the result of foldl
  public <U> U foldl(IFunc2<U, T, U> f, U base) {
    return this.rest.foldl(f, f.apply(base, this.first));
  }
}

// Examples and tests
class ExamplesFunctionObjects {

  SineNum sine = new SineNum();
  PlusN plus1 = new PlusN(1.0);
  SquareNum square = new SquareNum();

  // Test the function f(x) = (sin(x) + 1)²
  IFunc<Double, Double> sinPlus1 = new FunctionComposition<Double, Double, Double>(
      sine, plus1);
  IFunc<Double, Double> fComposed = new FunctionComposition<Double, Double, Double>(
      sinPlus1, square);

  // list of functions represening f(x) = (sin(x) + 1)^2
  IList<IFunc<Double, Double>> functionList = new ConsList<IFunc<Double, Double>>(sine,
      new ConsList<IFunc<Double, Double>>(plus1,
          new ConsList<IFunc<Double, Double>>(square,
              new MtList<IFunc<Double, Double>>())));
  IFunc<Double, Double> fFromList = functionList.foldl(
      new ComposeFunctions<Double, Double, Double>(), new Identity<Double>());

  // tests for square num function
  boolean testSquareNum(Tester t) {
    return t.checkInexact(square.apply(3.0), 9.0, 0.001)
        && t.checkInexact(square.apply(-2.0), 4.0, 0.001)
        && t.checkInexact(square.apply(0.0), 0.0, 0.001);
  }

  // tests for sine num function
  boolean testSineNum(Tester t) {
    return t.checkInexact(sine.apply(0.0), 0.0, 0.001)
        && t.checkInexact(sine.apply(Math.PI / 2), 1.0, 0.001)
        && t.checkInexact(sine.apply(Math.PI), 0.0, 0.001);
  }

  // tests for identity function
  boolean testIdentity(Tester t) {
    Identity<Double> idDouble = new Identity<Double>();
    Identity<String> idString = new Identity<String>();
    Identity<Integer> idInteger = new Identity<Integer>();

    return t.checkExpect(idDouble.apply(5.0), 5.0)
        && t.checkExpect(idString.apply("hello"), "hello")
        && t.checkExpect(idInteger.apply(42), 42);
  }

  // tests for plus n function
  boolean testPlusN(Tester t) {
    PlusN plus5 = new PlusN(5.0);

    return t.checkExpect(this.plus1.apply(3.0), 4.0)
        && t.checkExpect(plus5.apply(2.0), 7.0)
        && t.checkExpect(this.plus1.apply(-1.0), 0.0);
  }

  // REQUIRED TEST FROM THE ASSIGNMENT
  boolean testComposition(Tester t) {
    return t.checkInexact(fComposed.apply(0.0), 1.0, 0.001)
        && t.checkInexact(fComposed.apply(Math.PI / 2), 4.0, 0.001)
        && t.checkInexact(fComposed.apply(Math.PI), 1.0, 0.001);
  }

  // tests for function composition using foldl
  boolean testFoldlComposition(Tester t) {
    return t.checkInexact(fFromList.apply(0.0), 1.0, 0.001)
        && t.checkInexact(fFromList.apply(Math.PI / 2), 4.0, 0.001)
        && t.checkInexact(fFromList.apply(Math.PI), 1.0, 0.001);
  }

  // REQUIRED TEST FROM THE ASSIGNMENT
  boolean testFunctionEquality(Tester t) {
    boolean sameObject = fComposed.equals(fFromList);
    boolean sameResults = Math.abs(fComposed.apply(1.0) - fFromList.apply(1.0)) == 0;

    return t.checkExpect(sameObject, false) &&
        t.checkExpect(sameResults, true);

    // Explanation: checkExpect uses extensional equality (checking field values in
    // addition) for objects. Since function objects can have different
    // internal fields and still calculate the same result, the checkExpect will
    // find them different.
  }
}