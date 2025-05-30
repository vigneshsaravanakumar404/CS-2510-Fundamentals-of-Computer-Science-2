import tester.Tester;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

abstract class ABST<T> {
  Comparator<T> order;

  ABST(Comparator<T> order) {
    this.order = order;
  }

  abstract ABST<T> insert(T t);

  abstract boolean present(T t);

  abstract T getLeftmost();

  abstract T getLeftMostHelper(T currentLeftMost);

  abstract ABST<T> getRight();

  abstract ABST<T> getRightHelper(Node<T> currentLeftMost);

  abstract boolean sameTree(ABST<T> other);

  boolean sameTreeLeafHelper(Leaf<T> leaf) {
    return false;
  }

  boolean sameTreeNodeHelper(Node<T> node) {
    return false;
  }

  boolean sameData(ABST<T> other) {
    return this.buildList().sameList(other.buildList());
  }

  abstract IList<T> buildList();

  abstract IList<T> buildListHelper(IList<T> accumulator);
}

class Leaf<T> extends ABST<T> {
  Leaf(Comparator<T> order) {
    super(order);
  }

  ABST<T> insert(T t) {
    return new Node<T>(order, t, this, this);
  }

  T getLeftmost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  T getLeftMostHelper(T currentLeftMost) {
    return currentLeftMost;
  }

  boolean present(T t) {
    return false;
  }

  ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  ABST<T> getRightHelper(Node<T> currentLeftMost) {
    return this;
  }

  boolean sameTree(ABST<T> other) {
    return other.sameTreeLeafHelper(this);
  }

  boolean sameTreeLeafHelper(Leaf<T> leaf) {
    return true;
  }

  IList<T> buildList() {
    return new MtList<T>();
  }

  IList<T> buildListHelper(IList<T> accumulator) {
    return accumulator;
  }

}

class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  Node(Comparator<T> order, T value, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = value;
    this.left = left;
    this.right = right;
  }

  ABST<T> insert(T t) {
    if (this.order.compare(this.data, t) > 0) {
      return new Node<T>(this.order, this.data, this.left.insert(t), this.right);
    }

    return new Node<T>(this.order, this.data, this.left, this.right.insert(t));
  }

  T getLeftmost() {
    return this.left.getLeftMostHelper(this.data);
  }

  T getLeftMostHelper(T currentLeftMost) {
    return this.left.getLeftMostHelper(this.data);
  }

  boolean present(T t) {
    if (this.order.compare(this.data, t) == 0) {
      return true;
    }
    if (this.order.compare(this.data, t) < 0) {
      return this.right.present(t);
    }

    return this.left.present(t);
  }

  ABST<T> getRight() {
    return this.left.getRightHelper(this);
  }

  ABST<T> getRightHelper(Node<T> currentLeftMost) {
    return new Node<T>(this.order, currentLeftMost.data, this.left.getRightHelper(this), this.right);
  }

  boolean sameTree(ABST<T> other) {
    return other.sameTreeNodeHelper(this);
  }

  boolean sameTreeNodeHelper(Node<T> node) {
    return this.data.equals(node.data)
        && this.left.sameTree(node.left)
        && this.right.sameTree(node.right);
  }

  IList<T> buildList() {
    return this.buildListHelper(new MtList<T>());
  }

  IList<T> buildListHelper(IList<T> accumulator) {
    return this.left.buildListHelper(new ConsList<T>(this.data, this.right.buildListHelper(accumulator)));
  }

}

class Book {
  String title;
  String author;
  int price;

  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }
}

class BooksByTitle implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.title.compareTo(b2.title);
  }
}

class BooksByAuthor implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.author.compareTo(b2.author);
  }
}

class BooksByPrice implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.price - b2.price;
  }
}

class ExampleABST {
  // Create example data

  boolean testGetLeftMost(Tester t) {
    return true;
  }
}

// represents a generic list
interface IList<T> {
  // filter this list using the given predicate
  IList<T> filter(Predicate<T> pred);

  // map a given function onto every member of this list and return a list of the
  // results
  <U> IList<U> map(Function<T, U> converter);

  // combine the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> converter, U initial);

  // Check if this list equals another list
  boolean sameList(IList<T> other);

  // Helper methods for checking equality with empty and non-empty lists
  boolean sameListMtHelper(MtList<T> mt);

  // Helper methods for checking equality with non-empty lists
  boolean sameListConsHelper(ConsList<T> cons);

}

// represents a generic empty list
class MtList<T> implements IList<T> {

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

  // Check if this empty list equals another list
  public boolean sameList(IList<T> other) {
    return other.sameListMtHelper(this);
  }

  // Helper when comparing with an empty list
  public boolean sameListMtHelper(MtList<T> mt) {
    return true; // two empty lists are equal
  }

  // Helper when comparing with a non-empty list
  public boolean sameListConsHelper(ConsList<T> cons) {
    return false; // empty list is not equal to non-empty list
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

  // returns true if this non-empty list is equal to another object
  public boolean sameList(IList<T> other) {
    return other.sameListConsHelper(this);
  }

  // Helper when comparing with an empty list
  public boolean sameListMtHelper(MtList<T> mt) {
    return false; // non-empty list is not equal to empty list
  }

  // Helper when comparing with a non-empty list
  public boolean sameListConsHelper(ConsList<T> cons) {
    return this.first.equals(cons.first) && this.rest.sameList(cons.rest);
  }

}

class ExamplesRegistrar {

  ABST<Book> b1 = new Leaf<Book>(new BooksByTitle());
  ABST<Book> b2 = new Leaf<Book>(new BooksByTitle());
  IList<Book> lb1 = b1.buildList();
  IList<Book> lb2 = b2.buildList();

  boolean testSameTree(Tester t) {
    return t.checkExpect(b1.sameData(b2), true);
  }
}