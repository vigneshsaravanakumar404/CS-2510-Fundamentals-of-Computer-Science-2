import java.util.function.Function;

// represents a shape
interface IShape {

  <R> R accept(IShapeVisitor<R> v);
}

// represents a circle
class Circle implements IShape {
  int radius;

  Circle(int r) {
    this.radius = r;
  }

  // NEW:
  public <R> R accept(IShapeVisitor<R> v) {
    return v.visitCircle(this);
  }
}

// represents a rectangle
class Rectangle implements IShape {
  int length;
  int width;

  Rectangle(int l, int w) {
    this.length = l;
    this.width = w;
  }

  // NEW:
  public <R> R accept(IShapeVisitor<R> v) {
    return v.visitRectangle(this);
  }

}

// represents a shape visitor, to implement a function over Shape objects
// returning a value of type R
interface IShapeVisitor<R> extends Function<IShape, R> {

  // NEW:
  R visitCircle(Circle c);

  // NEW:
  R visitRectangle(Rectangle r);
}

class ShapePerimeter implements IShapeVisitor<Double> {
  public Double visitCircle(Circle c) {
    return 2 * Math.PI * c.radius;
  }

  public Double visitRectangle(Rectangle r) {
    return 2.0 * (r.length + r.width);
  }

  // NEW:
  public Double apply(IShape t) {
    return t.accept(this);
  }
}
