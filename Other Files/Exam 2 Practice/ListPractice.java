// import java.util.ArrayList;
// import java.util.Arrays;

// class ListPractice {

// public static void main(String[] args) {
// // #1
// ArrayList<Integer> l1 = new ArrayList<Integer>(Arrays.asList(11, 2, 6, 4, 10,
// 7));
// ArrayList<Integer> l2 = new ArrayList<Integer>(Arrays.asList(9, 8, 7, 6, 5));

// System.out.println(maxDiff(l1));
// System.out.println(maxDiff(l2));

// // #2
// ArrayList<String> l3 = new ArrayList<String>(Arrays.asList("a", "a", "a",
// "a")); // -> ["a"]
// ArrayList<String> l4 = new ArrayList<String>(Arrays.asList("a", "b", "c",
// "c", "a")); // -> ["a", "b", "c"]
// keepFirstUnique(l3);
// keepFirstUnique(l4);

// System.out.println(l3);
// System.out.println(l4);

// }

// public static int maxDiff(ArrayList<Integer> input) {
// int maxDiff = 0;

// for (int i = 0; i < input.size() - 1; i++) {
// for (int j = i; j < input.size(); j++) {
// if (input.get(j) - input.get(i) > maxDiff) {
// maxDiff = input.get(j) - input.get(i);
// }
// }
// }

// return maxDiff;
// }

// public static void keepFirstUnique(ArrayList<String> input) {

// // if only we could use hashsets
// for (int i = input.size() - 1; i >= 0; i--) {
// int j = i - 1;

// while (j >= 0 && !input.get(i).equals(input.get(j))) {
// j--;
// }

// if (j >= 0 && input.get(i).equals(input.get(j))) {
// input.remove(i);
// }
// }
// }

// }
import java.util.function.Predicate;
import java.util.function.BiFunction;
import java.util.Iterator;
import java.util.NoSuchElementException;

class Deque<T> {
  Sentinel<T> h;

  Deque() {
    this.h = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.h = header;
  }

  int size() {
    return this.h.size();
  }

  void addAtHead(T t) {
    this.h.addAt(t, true);
  }

  void addAtTail(T t) {
    this.h.addAt(t, false);
  }

  void reverse() {
    this.h.reverse();
  }

  T removeFromHead() {
    return this.h.removeFrom(true);
  }

  T removeFromTail() {
    return this.h.removeFrom(false);
  }

  ANode<T> find(Predicate<T> p) {
    return this.h.find(p);
  }

  void append(Deque<T> given) {
    if (given.size() != 0) {
      ANode<T> lastOfThis = this.h.prev;
      ANode<T> firstOfThat = given.h.next;
      ANode<T> lastOfThat = given.h.prev;
      this.h.prev = lastOfThat;
      lastOfThat.setNext(this.h);
      lastOfThis.setNext(firstOfThat);
      firstOfThat.setPrev(lastOfThis);
      given.h.next = given.h;
      given.h.prev = given.h;
    }
  }
}

abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  abstract int sizeHelp();

  abstract T remove();

  abstract ANode<T> check(Predicate<T> pred);

  void setNext(ANode<T> next) {
    this.next = next;
  }

  void setPrev(ANode<T> prev) {
    this.prev = prev;
  }
}

class Sentinel<T> extends ANode<T> {
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  int size() {
    return next.sizeHelp();
  }

  int sizeHelp() {
    return 0;
  }

  void addAt(T t, boolean front) {
    if (front) {
      this.next = new Node<T>(t, this.next, this);
    } else {
      this.prev = new Node<T>(t, this, this.prev);
    }
  }

  T removeFrom(boolean front) {
    if (front)
      return this.next.remove();
    return this.prev.remove();
  }

  T remove() {
    throw new RuntimeException("Cannot remove sentinel");
  }

  ANode<T> find(Predicate<T> pred) {
    return this.next.check(pred);
  }

  ANode<T> check(Predicate<T> pred) {
    return this;
  }

  void reverse() {
    if (this.next != this)
      reverseHelper(this.next);
    ANode<T> temp = this.next;
    this.next = this.prev;
    this.prev = temp;
  }

  void reverseHelper(ANode<T> node) {
    if (node == this)
      return;
    ANode<T> nextNode = node.next;
    node.next = node.prev;
    node.prev = nextNode;
    reverseHelper(nextNode);
  }
}

class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    this.data = data;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    if (next == null || prev == null)
      throw new IllegalArgumentException("Next and prev cannot be null");
    this.data = data;
    this.next = next;
    this.prev = prev;
    this.next.prev = this;
    this.prev.next = this;
  }

  int sizeHelp() {
    return this.next.sizeHelp() + 1;
  }

  T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return this.data;
  }

  ANode<T> check(Predicate<T> pred) {
    if (pred.test(this.data))
      return this;
    return this.next.check(pred);
  }
}

class ScanLeftIterator<T, V> implements Iterator<T> {
  Iterator<V> source;
  T base;
  BiFunction<T, V, T> func;

  ScanLeftIterator(Iterator<V> source, BiFunction<T, V, T> func, T base) {
    this.source = source;
    this.func = func;
    this.base = base;
  }

  public boolean hasNext() {
    return this.source.hasNext();
  }

  public T next() {
    if (!this.hasNext())
      throw new NoSuchElementException("No more items");
    V answer = this.source.next();
    this.base = func.apply(this.base, answer);
    return this.base;

  }
}