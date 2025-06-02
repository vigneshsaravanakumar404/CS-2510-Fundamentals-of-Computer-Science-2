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

  Function<Double, Double> neg = x -> -x;
  Function<Double, Double> sqr = x -> x * x;
  BiFunction<Double, Double, Double> plus = (x, y) -> x + y;
  BiFunction<Double, Double, Double> minus = (x, y) -> x - y;
  BiFunction<Double, Double, Double> mul = (x, y) -> x * y;
  BiFunction<Double, Double, Double> div = (x, y) -> x / y;

  IArith const1 = new Const(1.0);
  IArith const2 = new Const(2.0);
  IArith const3 = new Const(1.5);
  IArith const4 = new Const(4.0);

  // 1.0 + 2.0
  IArith plusExpr = new BinaryFormula(plus, "plus", const1, const2);

  // neg 1.5
  IArith negExpr = new UnaryFormula(neg, "neg", const3);

  // 1.0 + 2.0 / neg 1.5
  IArith complexExpr = new BinaryFormula(div, "div", plusExpr, negExpr);

  // sqrt(4.0)
  IArith sqrExpr = new UnaryFormula(sqr, "sqr", const4);

}