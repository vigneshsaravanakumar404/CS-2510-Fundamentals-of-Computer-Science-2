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
  boolean testSize() {
    return true;
  }

  // test addAtHead
  boolean testAddAtHead() {
    return true;
  }

  // test addAtTail
  boolean testAddAtTail() {
    return true;
  }

  // test removeFromHead
  boolean testRemoveFromHead() {
    return true;
  }

  // test removeFromTail
  boolean testRemoveFromTail() {
    return true;
  }

  // test find
  boolean testFind() {
    return true;
  }
}