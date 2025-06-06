import tester.Tester;
import java.util.function.Predicate;

// represents an abstract node in the deque
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  // returns the size of the deque starting from this node
  abstract int size();

  // returns the data of the removed node (will be overridden)
  abstract T remove();

  // returns the first node that matches the predicate, or itself if none match
  abstract ANode<T> find(Predicate<T> pred);
}

// represents a sentinel node in the deque
class Sentinel<T> extends ANode<T> {

  // the constructor
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // returns 0 since sentinel does not hold data
  int size() {
    return 0;
  }

  // returns an exception since sentinel cannot be removed
  T remove() {
    throw new RuntimeException("Cannot remove from an empty list");
  }

  // returns itself since sentinel does not hold data
  ANode<T> find(Predicate<T> pred) {
    return this;
  }
}

// represents a node in the deque
class Node<T> extends ANode<T> {
  T data;

  // the constructor
  Node(T data) {
    this.data = data;
  }

  // the constructor
  Node(T data, ANode<T> next, ANode<T> prev) {
    this.data = data;
    this.next = next;
    this.prev = prev;

    if (next == null || prev == null) {
      throw new IllegalArgumentException("Next and prev nodes cannot be null");
    }

    next.prev = this;
    prev.next = this;
  }

  // returns the size of the deque starting from this node
  int size() {
    return 1 + this.next.size();
  }

  // returns the data of the removed node
  T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return this.data;
  }

  // returns the first node that matches the predicate, or itself if none match
  ANode<T> find(Predicate<T> pred) {
    if (pred.test(this.data)) {
      return this;
    } else {
      return this.next.find(pred);
    }
  }
}

// represents a deque
class Deque<T> {
  Sentinel<T> header;

  // the constructor
  Deque() {
    this.header = new Sentinel<T>();
  }

  // the constructor
  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // counts the number of nodes in the deque
  int size() {
    return this.header.next.size();
  }

  // adds a value at the head of the deque
  void addAtHead(T value) {
    new Node<T>(value, this.header.next, this.header);
  }

  // adds a value at the tail of the deque
  void addAtTail(T value) {
    new Node<T>(value, this.header, this.header.prev);
  }

  // removes and returns the first node's data
  T removeFromHead() {
    return this.header.next.remove();
  }

  // removes and returns the last node's data
  T removeFromTail() {
    return this.header.prev.remove();
  }

  // finds the first node matching the predicate
  ANode<T> find(Predicate<T> pred) {
    return this.header.next.find(pred);
  }
}

class ExamplesDeque {

  // Example 1: Empty list
  Deque<String> deque1 = new Deque<String>();

  // Example 2: List with "abc", "bcd", "cde", "def" in order
  Deque<String> deque2 = new Deque<String>();
  Node<String> node1 = new Node<String>("abc", this.deque2.header, this.deque2.header);
  Node<String> node2 = new Node<String>("bcd", this.deque2.header, this.node1);
  Node<String> node3 = new Node<String>("cde", this.deque2.header, this.node2);
  Node<String> node4 = new Node<String>("def", this.deque2.header, this.node3);

  // Example 3: List with at least four values NOT in lexicographic order
  Deque<String> deque3 = new Deque<String>();
  Node<String> nodeA = new Node<String>("zebra", this.deque3.header, this.deque3.header);
  Node<String> nodeB = new Node<String>("apple", this.deque3.header, this.nodeA);
  Node<String> nodeC = new Node<String>("mango", this.deque3.header, this.nodeB);
  Node<String> nodeD = new Node<String>("banana", this.deque3.header, this.nodeC);
  Node<String> nodeE = new Node<String>("kiwi", this.deque3.header, this.nodeD);

  // test size
  boolean testSize(Tester t) {
    Deque<String> test2 = new Deque<String>();
    test2.addAtHead("single");

    Deque<String> test3 = new Deque<String>();
    test3.addAtTail("first");
    test3.addAtTail("second");
    test3.addAtTail("third");

    Deque<Integer> test4 = new Deque<Integer>();
    test4.addAtHead(1);
    test4.addAtTail(2);

    return t.checkExpect(new Deque<String>().size(), 0) &&
        t.checkExpect(test2.size(), 1) &&
        t.checkExpect(test3.size(), 3) &&
        t.checkExpect(test4.size(), 2);
  }

  // test addAtHead
  boolean testAddAtHead(Tester t) {
    Deque<String> test1 = new Deque<String>();
    test1.addAtHead("solo");
    String head1 = test1.removeFromHead();

    Deque<String> test2 = new Deque<String>();
    test2.addAtHead("first");
    test2.addAtHead("second");
    test2.addAtHead("third");
    String head2 = test2.removeFromHead();

    Deque<String> test3 = new Deque<String>();
    test3.addAtTail("tail");
    test3.addAtHead("head");
    String tail3 = test3.removeFromTail();

    Deque<String> test4 = new Deque<String>();
    test4.addAtHead("a");
    test4.addAtHead("b");
    test4.addAtHead("c");
    String tail4 = test4.removeFromTail();

    return t.checkExpect(head1, "solo") &&
        t.checkExpect(head2, "third") &&
        t.checkExpect(tail3, "tail") &&
        t.checkExpect(tail4, "a");
  }

  // test addAtTail
  boolean testAddAtTail(Tester t) {
    Deque<String> test1 = new Deque<String>();
    test1.addAtTail("only");
    String removed1 = test1.removeFromTail();

    Deque<String> test2 = new Deque<String>();
    test2.addAtTail("x");
    test2.addAtTail("y");
    test2.addAtTail("z");
    String tail2 = test2.removeFromTail();

    Deque<String> test3 = new Deque<String>();
    test3.addAtHead("front");
    test3.addAtTail("back");
    String head3 = test3.removeFromHead();

    Deque<String> test4 = new Deque<String>();
    test4.addAtTail("1");
    test4.addAtTail("2");
    test4.addAtTail("3");
    String head4 = test4.removeFromHead();

    return t.checkExpect(removed1, "only") &&
        t.checkExpect(tail2, "z") &&
        t.checkExpect(head3, "front") &&
        t.checkExpect(head4, "1");
  }

  // test removeFromHead
  boolean testRemoveFromHead(Tester t) {
    Deque<String> test1 = new Deque<String>();
    test1.addAtHead("single");
    String removed1 = test1.removeFromHead();
    int size1 = test1.size();

    Deque<String> test2 = new Deque<String>();
    test2.addAtTail("a");
    test2.addAtTail("b");
    test2.addAtTail("c");
    String first = test2.removeFromHead();
    String second = test2.removeFromHead();

    Deque<Integer> test3 = new Deque<Integer>();
    test3.addAtHead(1);
    test3.addAtTail(2);
    test3.addAtHead(3);
    Integer removed3 = test3.removeFromHead();

    Deque<String> test4 = new Deque<String>();

    return t.checkExpect(removed1, "single") &&
        t.checkExpect(size1, 0) &&
        t.checkExpect(first, "a") &&
        t.checkExpect(second, "b") &&
        t.checkExpect(removed3, 3) &&
        t.checkException(new RuntimeException("Cannot remove from an empty list"),
            test4, "removeFromHead");
  }

  // test removeFromTail
  boolean testRemoveFromTail(Tester t) {
    Deque<String> test1 = new Deque<String>();
    test1.addAtTail("alone");
    String removed1 = test1.removeFromTail();
    int size1 = test1.size();

    Deque<String> test2 = new Deque<String>();
    test2.addAtHead("x");
    test2.addAtHead("y");
    test2.addAtHead("z");
    String last = test2.removeFromTail();
    String secondLast = test2.removeFromTail();

    Deque<Integer> test3 = new Deque<Integer>();
    test3.addAtTail(10);
    test3.addAtHead(20);
    test3.addAtTail(30);
    Integer removed3 = test3.removeFromTail();

    // Test 4: Exception on empty deque
    Deque<String> test4 = new Deque<String>();

    return t.checkExpect(removed1, "alone") &&
        t.checkExpect(size1, 0) &&
        t.checkExpect(last, "x") &&
        t.checkExpect(secondLast, "y") &&
        t.checkExpect(removed3, 30) &&
        t.checkException(new RuntimeException("Cannot remove from an empty list"),
            test4, "removeFromTail");
  }

  // test find
  boolean testFind(Tester t) {
    ANode<String> found1 = this.deque2.find(s -> s.equals("cde"));
    ANode<String> notFound = this.deque2.find(s -> s.equals("xyz"));
    ANode<String> emptyFind = this.deque1.find(s -> true);
    ANode<String> firstMatch = this.deque3.find(s -> s.contains("a"));

    return t.checkExpect(found1, this.node3) &&
        t.checkExpect(notFound, this.deque2.header) &&
        t.checkExpect(emptyFind, this.deque1.header) &&
        t.checkExpect(firstMatch, this.nodeA);
  }
}