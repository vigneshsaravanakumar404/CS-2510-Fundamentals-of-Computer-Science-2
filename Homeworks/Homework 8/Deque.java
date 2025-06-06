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

  // helper methods for double delegation
  abstract void updateNext(ANode<T> newNext);

  abstract void updatePrev(ANode<T> newPrev);

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

  // updates the next pointer
  void updateNext(ANode<T> newNext) {
    this.next = newNext;
  }

  // updates the prev pointer
  void updatePrev(ANode<T> newPrev) {
    this.prev = newPrev;
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

  // returns the data of the removed node using double delegation
  T remove() {
    // First delegation: ask prev to update its next pointer
    this.prev.updateNext(this.next);
    // Second delegation: ask next to update its prev pointer
    this.next.updatePrev(this.prev);
    return this.data;
  }

  // updates the next pointer
  void updateNext(ANode<T> newNext) {
    this.next = newNext;
  }

  // updates the prev pointer
  void updatePrev(ANode<T> newPrev) {
    this.prev = newPrev;
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

  // Test size() method for Sentinel
  boolean testSentinelSize(Tester t) {
    Sentinel<String> sentinel1 = new Sentinel<String>();
    Sentinel<Integer> sentinel2 = new Sentinel<Integer>();

    return t.checkExpect(sentinel1.size(), 0) &&
        t.checkExpect(sentinel2.size(), 0);
  }

  // Test size() method for Node
  boolean testNodeSize(Tester t) {
    // Create a sentinel for testing
    Sentinel<String> header = new Sentinel<String>();

    // Single node pointing to sentinel
    Node<String> single = new Node<String>("solo", header, header);

    // Chain of nodes
    Node<String> first = new Node<String>("first", header, header);
    Node<String> second = new Node<String>("second", header, first);
    Node<String> third = new Node<String>("third", header, second);

    return t.checkExpect(single.size(), 1) &&
        t.checkExpect(first.size(), 3) &&
        t.checkExpect(second.size(), 2) &&
        t.checkExpect(third.size(), 1);
  }

  // Test remove() method for Sentinel
  boolean testSentinelRemove(Tester t) {
    Sentinel<String> sentinel = new Sentinel<String>();

    return t.checkException(new RuntimeException("Cannot remove from an empty list"),
        sentinel, "remove");
  }

  // Test remove() method for Node
  boolean testNodeRemove(Tester t) {
    // Test 1: Remove a single node
    Sentinel<String> header1 = new Sentinel<String>();
    Node<String> single = new Node<String>("alone", header1, header1);
    String removed1 = single.remove();
    boolean check1 = header1.next == header1 && header1.prev == header1;

    // Test 2: Remove middle node from chain
    Sentinel<String> header2 = new Sentinel<String>();
    Node<String> n1 = new Node<String>("A", header2, header2);
    Node<String> n2 = new Node<String>("B", header2, n1);
    Node<String> n3 = new Node<String>("C", header2, n2);
    String removed2 = n2.remove();
    boolean check2 = n1.next == n3 && n3.prev == n1;

    // Test 3: Remove first node
    Sentinel<String> header3 = new Sentinel<String>();
    Node<String> first = new Node<String>("first", header3, header3);
    Node<String> second = new Node<String>("second", header3, first);
    String removed3 = first.remove();
    boolean check3 = header3.next == second && second.prev == header3;

    // Test 4: Remove last node
    Sentinel<String> header4 = new Sentinel<String>();
    Node<String> n4_1 = new Node<String>("X", header4, header4);
    Node<String> n4_2 = new Node<String>("Y", header4, n4_1);
    String removed4 = n4_2.remove();
    boolean check4 = n4_1.next == header4 && header4.prev == n4_1;

    return t.checkExpect(removed1, "alone") && check1 &&
        t.checkExpect(removed2, "B") && check2 &&
        t.checkExpect(removed3, "first") && check3 &&
        t.checkExpect(removed4, "Y") && check4;
  }

  // Test find() method for Sentinel
  boolean testSentinelFind(Tester t) {
    Sentinel<String> sentinel1 = new Sentinel<String>();
    Sentinel<Integer> sentinel2 = new Sentinel<Integer>();

    // Sentinel always returns itself
    return t.checkExpect(sentinel1.find(s -> true), sentinel1) &&
        t.checkExpect(sentinel1.find(s -> false), sentinel1) &&
        t.checkExpect(sentinel2.find(n -> n > 0), sentinel2);
  }

  // Test find() method for Node
  boolean testNodeFind(Tester t) {
    // Setup test structure
    Sentinel<String> header = new Sentinel<String>();
    Node<String> n1 = new Node<String>("apple", header, header);
    Node<String> n2 = new Node<String>("banana", header, n1);
    Node<String> n3 = new Node<String>("cherry", header, n2);
    Node<String> n4 = new Node<String>("date", header, n3);

    // Test finding existing elements
    ANode<String> found1 = n1.find(s -> s.equals("apple"));
    ANode<String> found2 = n1.find(s -> s.equals("cherry"));
    ANode<String> found3 = n2.find(s -> s.startsWith("d"));

    // Test not finding elements
    ANode<String> notFound1 = n1.find(s -> s.equals("grape"));
    ANode<String> notFound2 = n3.find(s -> s.length() > 10);

    // Test finding with different predicates
    ANode<String> firstWithA = n1.find(s -> s.contains("a"));
    ANode<String> firstWith6Chars = n1.find(s -> s.length() == 6);

    return t.checkExpect(found1, n1) &&
        t.checkExpect(found2, n3) &&
        t.checkExpect(found3, n4) &&
        t.checkExpect(notFound1, header) &&
        t.checkExpect(notFound2, header) &&
        t.checkExpect(firstWithA, n1) &&
        t.checkExpect(firstWith6Chars, n2);
  }

  // Test updateNext() and updatePrev() for proper delegation
  boolean testUpdateMethods(Tester t) {
    // Test Sentinel update methods
    Sentinel<String> sentinel = new Sentinel<String>();
    Node<String> node = new Node<String>("test");
    sentinel.updateNext(node);
    sentinel.updatePrev(node);
    boolean sentinelCheck = sentinel.next == node && sentinel.prev == node;

    // Test Node update methods
    Node<String> n1 = new Node<String>("n1");
    Node<String> n2 = new Node<String>("n2");
    Node<String> n3 = new Node<String>("n3");

    n1.updateNext(n2);
    n1.updatePrev(n3);
    boolean nodeCheck = n1.next == n2 && n1.prev == n3;

    return t.checkExpect(sentinelCheck, true) &&
        t.checkExpect(nodeCheck, true);
  }

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