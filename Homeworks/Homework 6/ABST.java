import tester.Tester;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

// represents a binary search tree with generic type T
abstract class ABST<T> {
  Comparator<T> order;

  // constructor
  ABST(Comparator<T> order) {
    this.order = order;
  }

  // returns new tree with t inserted
  abstract ABST<T> insert(T t);

  // returns true if t is present in this tree
  abstract boolean present(T t);

  // returns the leftmost item in this tree
  abstract T getLeftmost();

  // returns the leftmost item in this tree
  abstract T getLeftMostHelper(T currentLeft);

  // returns the right subtree of this tree
  abstract ABST<T> getRight();

  // returns the right subtree of this tree, given the node data and right subtree
  abstract ABST<T> getRightHelper(T nodeData, ABST<T> nodeRight, Comparator<T> order);

  // checks if this tree is the same as another tree
  abstract boolean sameTree(ABST<T> other);

  // builds a list from this tree
  abstract IList<T> buildList();

  // builds a list from this tree, using an accumulator
  abstract IList<T> buildListHelper(IList<T> accumulator);

  // checks if this tree is the same as another leaf
  boolean sameTreeLeafHelper(Leaf<T> leaf) {
    return false;
  }

  // checks if this tree is the same as another node
  boolean sameTreeNodeHelper(Node<T> node) {
    return false;
  }

  // builds a list from this tree and checks if it is the same as the list built
  boolean sameData(ABST<T> other) {
    return this.buildList().sameList(other.buildList());
  }
}

// represents a leaf in the binary search tree
class Leaf<T> extends ABST<T> {

  // the constructor
  Leaf(Comparator<T> order) {
    super(order);
  }

  // returns new tree with t inserted
  ABST<T> insert(T t) {
    return new Node<T>(order, t, this, this);
  }

  // returns the leftmost item in this tree
  T getLeftmost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  // returns the leftmost item in this tree
  T getLeftMostHelper(T currentLeftMost) {
    return currentLeftMost;
  }

  // returns true if t is present in this tree
  boolean present(T t) {
    return false;
  }

  // returns the right subtree of this tree
  ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  // returns the right subtree of this tree, given the node data and right subtree
  ABST<T> getRightHelper(T nodeData, ABST<T> nodeRight, Comparator<T> order) {
    return nodeRight;
  }

  // checks if this tree is the same as another tree
  boolean sameTree(ABST<T> other) {
    return other.sameTreeLeafHelper(this);
  }

  // checks if this tree is the same as another leaf
  boolean sameTreeLeafHelper(Leaf<T> leaf) {
    return true;
  }

  // builds a list from this tree
  IList<T> buildList() {
    return new MtList<T>();
  }

  // builds a list from this tree, using an accumulator
  IList<T> buildListHelper(IList<T> accumulator) {
    return accumulator;
  }
}

// represents a node in the binary search tree
class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  // constructor
  Node(Comparator<T> order, T value, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = value;
    this.left = left;
    this.right = right;
  }

  // returns new tree with t inserted
  ABST<T> insert(T t) {
    if (this.order.compare(this.data, t) > 0) {
      return new Node<T>(this.order, this.data, this.left.insert(t), this.right);
    }
    return new Node<T>(this.order, this.data, this.left, this.right.insert(t));
  }

  // returns the leftmost item in this tree
  T getLeftmost() {
    return this.left.getLeftMostHelper(this.data);
  }

  // returns the leftmost item in this tree
  T getLeftMostHelper(T currentLeftMost) {
    return this.left.getLeftMostHelper(this.data);
  }

  // returns true if t is present in this tree
  boolean present(T t) {
    if (this.order.compare(this.data, t) == 0) {
      return true;
    }
    if (this.order.compare(this.data, t) < 0) {
      return this.right.present(t);
    }
    return this.left.present(t);
  }

  // returns the right subtree of this tree
  ABST<T> getRight() {
    return this.left.getRightHelper(this.data, this.right, this.order);
  }

  // returns the right subtree of this tree, given the node data and right subtree
  ABST<T> getRightHelper(T nD, ABST<T> nR, Comparator<T> order) {
    return new Node<T>(order, nD, this.left.getRightHelper(this.data, this.right, order), nR);
  }

  // checks if this tree is the same as another tree
  boolean sameTree(ABST<T> other) {
    return other.sameTreeNodeHelper(this);
  }

  // checks if this tree is the same as another node
  boolean sameTreeNodeHelper(Node<T> node) {
    return this.data.equals(node.data)
        && this.left.sameTree(node.left)
        && this.right.sameTree(node.right);
  }

  // builds a list from this tree
  IList<T> buildList() {
    return this.buildListHelper(new MtList<T>());
  }

  // builds a list from this tree, using an accumulator
  IList<T> buildListHelper(IList<T> acc) {
    return this.left.buildListHelper(new ConsList<T>(this.data, this.right.buildListHelper(acc)));
  }
}

// represents a book
class Book {
  String title;
  String author;
  int price;

  // constructor
  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }
}

// represents a comparator for books based title
class BooksByTitle implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.title.compareTo(b2.title);
  }
}

// represents a comparator for books based on author
class BooksByAuthor implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.author.compareTo(b2.author);
  }
}

// represents a comparator for books based on price
class BooksByPrice implements Comparator<Book> {
  public int compare(Book b1, Book b2) {
    return b1.price - b2.price;
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

  // constructor
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

class ExampleABST {
  // Book examples
  Book book1 = new Book("Alice in Wonderland", "Lewis Carroll", 15);
  Book book2 = new Book("The Great Gatsby", "F Scott Fitzgerald", 12);
  Book book3 = new Book("1984", "George Orwell", 14);
  Book book4 = new Book("Pride and Prejudice", "Jane Austen", 10);
  Book book5 = new Book("To Kill a Mockingbird", "Harper Lee", 13);
  Book book6 = new Book("The Catcher in the Rye", "J.D. Salinger", 11);
  Book book7 = new Book("Brave New World", "Aldous Huxley", 16);
  Book book8 = new Book("Animal Farm", "George Orwell", 9);

  // Binary Search Trees sorted by Title
  // Empty tree sorted by title
  ABST<Book> emptyByTitle = new Leaf<Book>(new BooksByTitle());

  // Single node tree sorted by title
  ABST<Book> singleByTitle = new Node<Book>(new BooksByTitle(), book1,
      new Leaf<Book>(new BooksByTitle()), new Leaf<Book>(new BooksByTitle()));

  // Balanced tree sorted by title (following the pattern from assignment)
  ABST<Book> balancedByTitle = new Node<Book>(new BooksByTitle(), book2,
      new Node<Book>(new BooksByTitle(), book1,
          new Leaf<Book>(new BooksByTitle()),
          new Leaf<Book>(new BooksByTitle())),
      new Node<Book>(new BooksByTitle(), book4,
          new Leaf<Book>(new BooksByTitle()),
          new Leaf<Book>(new BooksByTitle())));

  // Left-heavy tree sorted by title
  ABST<Book> leftHeavyByTitle = new Node<Book>(new BooksByTitle(), book2,
      new Node<Book>(new BooksByTitle(), book3,
          new Node<Book>(new BooksByTitle(), book1,
              new Leaf<Book>(new BooksByTitle()),
              new Leaf<Book>(new BooksByTitle())),
          new Leaf<Book>(new BooksByTitle())),
      new Leaf<Book>(new BooksByTitle()));

  // Binary Search Trees sorted by Author
  // Empty tree sorted by author
  ABST<Book> emptyByAuthor = new Leaf<Book>(new BooksByAuthor());

  // Single node tree sorted by author
  ABST<Book> singleByAuthor = new Node<Book>(new BooksByAuthor(), book5,
      new Leaf<Book>(new BooksByAuthor()), new Leaf<Book>(new BooksByAuthor()));

  // Tree with multiple books by same author
  ABST<Book> multiAuthorTree = new Node<Book>(new BooksByAuthor(), book3,
      new Node<Book>(new BooksByAuthor(), book2,
          new Leaf<Book>(new BooksByAuthor()),
          new Leaf<Book>(new BooksByAuthor())),
      new Node<Book>(new BooksByAuthor(), book8,
          new Node<Book>(new BooksByAuthor(), book5,
              new Leaf<Book>(new BooksByAuthor()),
              new Leaf<Book>(new BooksByAuthor())),
          new Leaf<Book>(new BooksByAuthor())));

  // Right-heavy tree sorted by author
  ABST<Book> rightHeavyByAuthor = new Node<Book>(new BooksByAuthor(), book7,
      new Leaf<Book>(new BooksByAuthor()),
      new Node<Book>(new BooksByAuthor(), book6,
          new Leaf<Book>(new BooksByAuthor()),
          new Node<Book>(new BooksByAuthor(), book4,
              new Leaf<Book>(new BooksByAuthor()),
              new Node<Book>(new BooksByAuthor(), book1,
                  new Leaf<Book>(new BooksByAuthor()),
                  new Leaf<Book>(new BooksByAuthor())))));

  // Binary Search Trees sorted by Price
  // Empty tree sorted by price
  ABST<Book> emptyByPrice = new Leaf<Book>(new BooksByPrice());

  // Single node tree sorted by price
  ABST<Book> singleByPrice = new Node<Book>(new BooksByPrice(), book3,
      new Leaf<Book>(new BooksByPrice()), new Leaf<Book>(new BooksByPrice()));

  // Balanced tree sorted by price
  ABST<Book> balancedByPrice = new Node<Book>(new BooksByPrice(), book2,
      new Node<Book>(new BooksByPrice(), book4,
          new Node<Book>(new BooksByPrice(), book8,
              new Leaf<Book>(new BooksByPrice()),
              new Leaf<Book>(new BooksByPrice())),
          new Node<Book>(new BooksByPrice(), book6,
              new Leaf<Book>(new BooksByPrice()),
              new Leaf<Book>(new BooksByPrice()))),
      new Node<Book>(new BooksByPrice(), book3,
          new Node<Book>(new BooksByPrice(), book5,
              new Leaf<Book>(new BooksByPrice()),
              new Leaf<Book>(new BooksByPrice())),
          new Node<Book>(new BooksByPrice(), book1,
              new Leaf<Book>(new BooksByPrice()),
              new Node<Book>(new BooksByPrice(), book7,
                  new Leaf<Book>(new BooksByPrice()),
                  new Leaf<Book>(new BooksByPrice())))));

  // Tree with duplicate prices (using right subtree for duplicates)
  ABST<Book> duplicatePriceTree = new Node<Book>(new BooksByPrice(), book3,
      new Node<Book>(new BooksByPrice(), book5,
          new Leaf<Book>(new BooksByPrice()),
          new Leaf<Book>(new BooksByPrice())),
      new Node<Book>(new BooksByPrice(),
          new Book("Another 14 Dollar Book", "Some Author", 14),
          new Leaf<Book>(new BooksByPrice()),
          new Leaf<Book>(new BooksByPrice())));

  // IList<Book> examples matching the BSTs
  // Empty list matching empty tree
  IList<Book> emptyBookList = new MtList<Book>();

  // List with single book matching singleByTitle tree
  IList<Book> singleBookList = new ConsList<Book>(book1, new MtList<Book>());

  // List matching balancedByTitle tree (in-order traversal: book1, book2, book4)
  IList<Book> balancedTitleList = new ConsList<Book>(book1,
      new ConsList<Book>(book2,
          new ConsList<Book>(book4, new MtList<Book>())));

  // List matching balancedByPrice tree (in-order traversal by price)
  IList<Book> balancedPriceList = new ConsList<Book>(book8,
      new ConsList<Book>(book4,
          new ConsList<Book>(book6,
              new ConsList<Book>(book2,
                  new ConsList<Book>(book5,
                      new ConsList<Book>(book3,
                          new ConsList<Book>(book1,
                              new ConsList<Book>(book7,
                                  new MtList<Book>()))))))));

  boolean testInsert(Tester t) {
    return t
        .checkExpect(this.emptyByTitle.insert(book1)
            .sameTree(new Node<Book>(new BooksByTitle(), book1, new Leaf<Book>(new BooksByTitle()),
                new Leaf<Book>(new BooksByTitle()))),
            true)
        && t.checkExpect(this.singleByTitle.insert(book2)
            .sameTree(new Node<Book>(new BooksByTitle(), book1, new Leaf<Book>(new BooksByTitle()),
                new Node<Book>(new BooksByTitle(), book2, new Leaf<Book>(new BooksByTitle()),
                    new Leaf<Book>(new BooksByTitle())))),
            true)
        && t.checkExpect(
            this.emptyByPrice.insert(book3).insert(book4)
                .sameTree(new Node<Book>(
                    new BooksByPrice(), book3, new Node<Book>(new BooksByPrice(), book4,
                        new Leaf<Book>(new BooksByPrice()), new Leaf<Book>(new BooksByPrice())),
                    new Leaf<Book>(new BooksByPrice()))),
            true);
  }

  boolean testPresent(Tester t) {
    return t.checkExpect(this.emptyByTitle.present(book1), false)
        && t.checkExpect(this.singleByTitle.present(book1), true)
        && t.checkExpect(this.balancedByPrice.present(book3), true);
  }

  boolean testGetLeftmost(Tester t) {
    return t.checkException(new RuntimeException(
        "No leftmost item of an empty tree"), this.emptyByTitle, "getLeftmost")
        && t.checkExpect(this.singleByTitle.getLeftmost(), book1)
        && t.checkExpect(this.balancedByPrice.getLeftmost(), book8);
  }

  boolean testGetLeftMostHelper(Tester t) {
    return t.checkExpect(this.emptyByTitle.getLeftMostHelper(book1), book1)
        && t.checkExpect(this.singleByTitle.getLeftMostHelper(book2), book1)
        && t.checkExpect(this.balancedByPrice.getLeftMostHelper(book5), book8);
  }

  boolean testGetRight(Tester t) {
    return t.checkException(new RuntimeException(
        "No right of an empty tree"), this.emptyByTitle, "getRight")
        && t.checkExpect(this.singleByTitle.getRight().sameTree(
            new Leaf<Book>(new BooksByTitle())), true)
        && t.checkExpect(
            this.leftHeavyByTitle.getRight()
                .sameTree(new Node<Book>(
                    new BooksByTitle(), book2, new Node<Book>(new BooksByTitle(), book3,
                        new Leaf<Book>(new BooksByTitle()), new Leaf<Book>(new BooksByTitle())),
                    new Leaf<Book>(new BooksByTitle()))),
            true);
  }

  boolean testGetRightHelper(Tester t) {
    return t
        .checkExpect(this.emptyByTitle.getRightHelper(book1, this.singleByTitle, new BooksByTitle())
            .sameTree(this.singleByTitle), true)
        && t.checkExpect(this.singleByTitle.getRightHelper(
            book2, this.emptyByTitle, new BooksByTitle())
            .sameTree(new Node<Book>(new BooksByTitle(), book2, new Leaf<Book>(new BooksByTitle()),
                new Leaf<Book>(new BooksByTitle()))),
            true)
        && t.checkExpect(this.emptyByTitle.getRightHelper(
            book3, this.emptyByTitle, new BooksByTitle())
            .sameTree(new Leaf<Book>(new BooksByTitle())), true);
  }

  boolean testSameTree(Tester t) {
    return t.checkExpect(this.emptyByTitle.sameTree(new Leaf<Book>(new BooksByTitle())), true)
        && t.checkExpect(this.singleByTitle.sameTree(new Node<Book>(new BooksByTitle(), book1,
            new Leaf<Book>(new BooksByTitle()), new Leaf<Book>(new BooksByTitle()))), true)
        && t.checkExpect(this.balancedByTitle.sameTree(this.leftHeavyByTitle), false);
  }

  boolean testSameTreeLeafHelper(Tester t) {
    return t.checkExpect(this.emptyByTitle.sameTreeLeafHelper(
        new Leaf<Book>(new BooksByTitle())), true)
        && t.checkExpect(this.singleByTitle.sameTreeLeafHelper(
            new Leaf<Book>(new BooksByTitle())), false)
        && t.checkExpect(this.balancedByPrice.sameTreeLeafHelper(
            new Leaf<Book>(new BooksByPrice())), false);
  }

  boolean testSameTreeNodeHelper(Tester t) {
    return t
        .checkExpect(this.emptyByTitle.sameTreeNodeHelper(new Node<Book>(new BooksByTitle(), book1,
            new Leaf<Book>(new BooksByTitle()), new Leaf<Book>(new BooksByTitle()))), false)
        && t.checkExpect(this.singleByTitle.sameTreeNodeHelper(
            new Node<Book>(new BooksByTitle(), book1, new Leaf<Book>(new BooksByTitle()),
                new Leaf<Book>(new BooksByTitle()))),
            true)
        && t.checkExpect(this.balancedByTitle.sameTreeNodeHelper(
            new Node<Book>(new BooksByTitle(), book2,
                new Node<Book>(new BooksByTitle(), book1, new Leaf<Book>(new BooksByTitle()),
                    new Leaf<Book>(new BooksByTitle())),
                new Node<Book>(new BooksByTitle(), book4, new Leaf<Book>(new BooksByTitle()),
                    new Leaf<Book>(new BooksByTitle())))),
            true);
  }

  boolean testBuildList(Tester t) {
    return t.checkExpect(this.emptyByTitle.buildList().sameList(this.emptyBookList), true)
        && t.checkExpect(this.singleByTitle.buildList().sameList(this.singleBookList), true)
        && t.checkExpect(this.balancedByTitle.buildList().sameList(this.balancedTitleList), true);
  }

  boolean testBuildListHelper(Tester t) {
    return t
        .checkExpect(this.emptyByTitle.buildListHelper(
            new ConsList<Book>(book1, new MtList<Book>()))
            .sameList(new ConsList<Book>(book1, new MtList<Book>())), true)
        && t.checkExpect(this.singleByTitle.buildListHelper(
            new MtList<Book>()).sameList(this.singleBookList), true)
        && t.checkExpect(this.balancedByTitle.buildListHelper(
            new MtList<Book>()).sameList(this.balancedTitleList), true);
  }

  boolean testSameData(Tester t) {
    return t.checkExpect(this.emptyByTitle.sameData(this.emptyByAuthor), true)
        && t.checkExpect(this.singleByTitle.sameData(new Node<Book>(new BooksByAuthor(), book1,
            new Leaf<Book>(new BooksByAuthor()), new Leaf<Book>(new BooksByAuthor()))), true)
        && t.checkExpect(this.balancedByTitle.sameData(this.leftHeavyByTitle), false);
  }
}