import tester.Tester;
import java.util.function.*;

// represents an arithmetic expressions
interface IArith {
  // Accept a visitor and return the result of visiting this IArith
  <R> R accept(IArithVisitor<R> visitor);
}

// represents a constant
class Const implements IArith {
  double num;

  // the constructor
  Const(double num) {
    this.num = num;
  }

  // returns the result of the visitor visiting this constant
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitConst(this);
  }
}

// represents a unary operation on an IArith
class UnaryFormula implements IArith {
  Function<Double, Double> func;
  String name;
  IArith child;

  // the constructor
  UnaryFormula(Function<Double, Double> func, String name, IArith child) {
    this.func = func;
    this.name = name;
    this.child = child;
  }

  // returns the result of the visitor visiting this UnaryFormula
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitUnaryFormula(this);
  }
}

// represents a binary operation on two IArith expressions
class BinaryFormula implements IArith {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith left;
  IArith right;

  // the constructor
  BinaryFormula(BiFunction<Double, Double, Double> func, String name, IArith left, IArith right) {
    this.func = func;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  // returns the result of the visitor visiting this BinaryFormula
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitBinaryFormula(this);
  }
}

// represents a visitor for IArith expressions
interface IArithVisitor<R> extends Function<IArith, R> {

  // visits a constant
  R visitConst(Const c);

  // visits a unary formula
  R visitUnaryFormula(UnaryFormula u);

  // visits a binary formula
  R visitBinaryFormula(BinaryFormula b);

  // applies the visitor to an IArith expression
  R apply(IArith arith);
}

// represents a visitor that evaluates arithmetic expressions
class EvalVisitor implements IArithVisitor<Double> {

  // Evaluates a constant
  public Double visitConst(Const c) {
    return c.num;
  }

  // Evaluates a unary formula by applying the function to the child's value
  public Double visitUnaryFormula(UnaryFormula u) {
    Double childValue = u.child.accept(this);
    return u.func.apply(childValue);
  }

  // Evaluates a binary formula by applying the function to the left and right
  // child's values
  public Double visitBinaryFormula(BinaryFormula b) {
    Double leftValue = b.left.accept(this);
    Double rightValue = b.right.accept(this);
    return b.func.apply(leftValue, rightValue);
  }

  // Applies the visitor to an IArith expression
  public Double apply(IArith arith) {
    return arith.accept(this);
  }
}

// PrintVisitor - produces a String in Racket-like prefix notation
class PrintVisitor implements IArithVisitor<String> {

  // Visits a constant and returns its string representation
  public String visitConst(Const c) {
    return Double.toString(c.num);
  }

  // Visits a unary formula and returns its string representation in prefix
  // notation
  public String visitUnaryFormula(UnaryFormula u) {
    return "(" + u.name + " " + u.child.accept(this) + ")";
  }

  // Visits a binary formula and returns its string representation in prefix
  public String visitBinaryFormula(BinaryFormula b) {
    return "(" + b.name + " " + b.left.accept(this) + " " + b.right.accept(this) + ")";
  }

  // Applies the visitor to an IArith expression and returns its string
  public String apply(IArith arith) {
    return arith.accept(this);
  }
}

// MirrorVisitor - swaps left and right in all BinaryFormulas
class MirrorVisitor implements IArithVisitor<IArith> {

  // Visits a constant and returns a new constant with the same value
  public IArith visitConst(Const c) {
    return new Const(c.num);
  }

  // Visits a unary formula and returns a new unary formula with the child
  public IArith visitUnaryFormula(UnaryFormula u) {
    return new UnaryFormula(u.func, u.name, u.child.accept(this));
  }

  // Visits a binary formula and returns a new binary formula with left and right
  public IArith visitBinaryFormula(BinaryFormula b) {
    return new BinaryFormula(b.func, b.name, b.right.accept(this), b.left.accept(this));
  }

  // Applies the visitor to an IArith expression and returns the mirrored version
  public IArith apply(IArith arith) {
    return arith.accept(this);
  }
}

// AllEvenVisitor - checks if all constants are even
class AllEvenVisitor implements IArithVisitor<Boolean> {

  // Visits a constant and checks if it is even
  public Boolean visitConst(Const c) {
    return c.num % 2 == 0;
  }

  // Visits a unary formula and checks if the child is even
  public Boolean visitUnaryFormula(UnaryFormula u) {
    return u.child.accept(this);
  }

  // Visits a binary formula and checks if both left and right children are even
  public Boolean visitBinaryFormula(BinaryFormula b) {
    return b.left.accept(this) && b.right.accept(this);
  }

  // Applies the visitor to an IArith expression and checks if all constants are
  // even
  public Boolean apply(IArith arith) {
    return arith.accept(this);
  }
}

// ExamplesVisitors class with test cases
class ExamplesVisitors {

  // Constants
  IArith c0 = new Const(0.0);
  IArith c1 = new Const(1.0);
  IArith c2 = new Const(2.0);
  IArith c3 = new Const(3.0);
  IArith c4 = new Const(4.0);
  IArith c5 = new Const(5.0);
  IArith const6 = new Const(6.0);
  IArith cN2 = new Const(-2.0);

  // Functions
  Function<Double, Double> neg = x -> -x;
  Function<Double, Double> sqr = x -> x * x;
  BiFunction<Double, Double, Double> plus = (x, y) -> x + y;
  BiFunction<Double, Double, Double> minus = (x, y) -> x - y;
  BiFunction<Double, Double, Double> mul = (x, y) -> x * y;
  BiFunction<Double, Double, Double> div = (x, y) -> x / y;

  // Arithmetic expressions
  IArith plusExpr = new BinaryFormula(plus, "plus", c1, c2);
  IArith minusExpr = new BinaryFormula(minus, "minus", c5, c3);
  IArith mulExpr = new BinaryFormula(mul, "mul", c4, c3);
  IArith divExpr = new BinaryFormula(div, "div", const6, c2);
  IArith negExpr = new UnaryFormula(neg, "neg", c3);
  IArith sqrExpr = new UnaryFormula(sqr, "sqr", c4);
  IArith allEvenExpr = new BinaryFormula(plus, "plus", c2, c2);
  IArith mixedExpr = new BinaryFormula(mul, "mul", c2, c3);
  IArith allOddExpr = new BinaryFormula(minus, "minus", c3, c5);

  // Nested expressions
  IArith nestedExpr1 = new BinaryFormula(div, "div", plusExpr, negExpr);
  IArith nestedExpr2 = new UnaryFormula(sqr, "sqr", minusExpr);
  IArith deeplyNested = new BinaryFormula(mul, "mul",
      new UnaryFormula(neg, "neg", plusExpr),
      new BinaryFormula(minus, "minus", sqrExpr, mulExpr));
  IArith nestedEvenExpr = new BinaryFormula(plus, "plus",
      new UnaryFormula(neg, "neg", c2),
      new BinaryFormula(mul, "mul", c2, const6));

  // Visitors
  AllEvenVisitor allEven = new AllEvenVisitor();
  MirrorVisitor mirror = new MirrorVisitor();
  EvalVisitor eval = new EvalVisitor();
  PrintVisitor print = new PrintVisitor();

  // tests for EvalVisitor
  boolean testEvalVisitor(Tester t) {
    return t.checkInexact(this.eval.apply(this.plusExpr), 3.0, 0.001)
        && t.checkInexact(this.eval.apply(this.nestedExpr1), -1.0, 0.001)
        && t.checkInexact(this.eval.apply(this.deeplyNested), -12.0, 0.001);
  }

  // tests for PrintVisitor
  boolean testPrintVisitor(Tester t) {
    return t.checkExpect(this.print.apply(this.c4), "4.0")
        && t.checkExpect(this.print.apply(this.sqrExpr), "(sqr 4.0)")
        && t.checkExpect(this.print.apply(this.nestedExpr1), "(div (plus 1.0 2.0) (neg 3.0))");
  }

  // tests for MirrorVisitor with EvalVisitor to verify correctness
  boolean testMirrorVisitor(Tester t) {
    return t.checkInexact(this.eval.apply(this.mirror.apply(this.minusExpr)), -2.0, 0.001)
        && t.checkInexact(this.eval.apply(this.mirror.apply(this.nestedExpr1)), -1.0, 0.001)
        && t.checkInexact(this.eval.apply(this.mirror.apply(this.deeplyNested)), 12.0, 0.001);
  }

  // tests for AllEvenVisitor
  boolean testAllEvenVisitor(Tester t) {
    return t.checkExpect(this.allEven.apply(this.allEvenExpr), true)
        && t.checkExpect(this.allEven.apply(this.mixedExpr), false)
        && t.checkExpect(this.allEven.apply(this.nestedEvenExpr), true);
  }

  // Additional tests for unary operations with EvalVisitor
  boolean testUnaryOperations(Tester t) {
    return t.checkInexact(this.eval.apply(new UnaryFormula(this.neg, "neg", this.c0)), 0.0, 0.001)
        && t.checkInexact(this.eval.apply(new UnaryFormula(this.sqr, "sqr", this.cN2)), 4.0, 0.001)
        && t.checkInexact(this.eval.apply(
            new UnaryFormula(this.neg, "neg", this.negExpr)), 3.0, 0.001);
  }

  // Additional tests for binary operations with EvalVisitor
  boolean testBinaryOperations(Tester t) {
    return t.checkInexact(this.eval.apply(
        new BinaryFormula(this.div, "div", this.c5, this.c1)), 5.0, 0.001)
        && t.checkInexact(this.eval.apply(
            new BinaryFormula(this.mul, "mul", this.c4, this.c0)), 0.0, 0.001)
        && t.checkInexact(this.eval.apply(
            new BinaryFormula(this.plus, "plus", this.c3, this.cN2)), 1.0, 0.001);
  }

  // Additional tests for PrintVisitor with complex expressions
  boolean testPrintVisitorComplex(Tester t) {
    IArith expr1 = new BinaryFormula(this.mul, "mul", this.c0, this.negExpr);
    IArith expr2 = new UnaryFormula(this.neg, "neg", this.divExpr);
    IArith expr3 = new BinaryFormula(this.plus, "plus", this.sqrExpr,
        new UnaryFormula(neg, "neg", this.sqrExpr));

    return t.checkExpect(this.print.apply(expr1), "(mul 0.0 (neg 3.0))")
        && t.checkExpect(this.print.apply(expr2), "(neg (div 6.0 2.0))")
        && t.checkExpect(this.print.apply(expr3), "(plus (sqr 4.0) (neg (sqr 4.0)))");
  }

  // Additional tests for MirrorVisitor with PrintVisitor to see structure
  boolean testMirrorVisitorStructure(Tester t) {
    IArith expr1 = new BinaryFormula(this.div, "div", this.const6, this.c3);
    IArith expr2 = new BinaryFormula(this.minus, "minus", this.plusExpr, this.mulExpr);
    IArith expr3 = new UnaryFormula(this.sqr, "sqr", this.minusExpr);

    return t.checkExpect(this.print.apply(this.mirror.apply(expr1)), "(div 3.0 6.0)")
        && t.checkExpect(this.print.apply(this.mirror.apply(expr2)),
            "(minus (mul 3.0 4.0) (plus 2.0 1.0))")
        && t.checkExpect(this.print.apply(this.mirror.apply(expr3)), "(sqr (minus 3.0 5.0))");
  }

  // Additional tests for AllEvenVisitor with edge cases
  boolean testAllEvenVisitorEdgeCases(Tester t) {
    IArith zeroExpr = new UnaryFormula(this.neg, "neg", this.c0);
    IArith negEven = new UnaryFormula(this.neg, "neg", this.c2);
    IArith complexMixed = new BinaryFormula(this.plus, "plus",
        new BinaryFormula(this.mul, "mul", this.c2, this.c2),
        new UnaryFormula(this.sqr, "sqr", this.c3));

    return t.checkExpect(this.allEven.apply(zeroExpr), true)
        && t.checkExpect(this.allEven.apply(negEven), true)
        && t.checkExpect(this.allEven.apply(complexMixed), false);
  }
}