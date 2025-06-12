import java.util.ArrayList;

class Main {

  public static void main(String[] args) {
    ArrayList<Integer> a = new ArrayList<Integer>(java.util.Arrays.asList(1, 2,
        3, 4, 5));
    ArrayList<Integer> b = new ArrayList<Integer>(java.util.Arrays.asList());
    System.out.println(containsSequence(a, b));
  }

  static boolean containsSequence(ArrayList<Integer> source, ArrayList<Integer> sequence) {
    for (int i = 0; i < source.size() - sequence.size(); i++) {

      boolean allEqual = true;
      for (int j = 0; j < sequence.size(); j++) {
        if (!sequence.get(j).equals(source.get(i + j))) {
          allEqual = false;
        }
      }
      if (allEqual) {
        return true;
      }
    }

    return false;
  }

}

// interface IList<T> {
// // EFFECT: this list has the items of that list added to the end
// void append(IList<T> that);

// // helps append by returning a new rest of this list
// IList<T> appendHelp(IList<T> that);

// // computes the size of this list
// int length();
// }

// class ConsList<T> implements IList<T> {
// T first;
// IList<T> rest;

// ConsList(T first, IList<T> rest) {
// this.first = first;
// this.rest = rest;
// }

// // EFFECT: this list has the items of that list added to the end
// public void append(IList<T> that) {
// this.rest = this.rest.appendHelp(that);
// }

// // helps append by returning a new rest of this non-empty list
// public IList<T> appendHelp(IList<T> that) {
// this.rest = this.rest.appendHelp(that);
// return this;
// }

// // computes the size of this non-empty list
// public int length() {
// return 1 + this.rest.length();
// }
// }

// class MtList<T> implements IList<T> {
// // doesn’t make any changes to the empty list
// public void append(IList<T> that) {
// return;
// }

// // helps append by returning a new rest
// public IList<T> appendHelp(IList<T> that) {
// return that;
// }

// // computes the length of this empty list
// public int length() {
// return 0;
// }
// }

// class Main {

// public static void main(String[] args) {
// IList<Integer> mt = new MtList<Integer>();
// IList<Integer> ints1 = new ConsList<Integer>(1, mt);
// IList<Integer> ints2 = new ConsList<Integer>(2, new ConsList<Integer>(3,
// mt));
// IList<Integer> ints3 = new ConsList<Integer>(4, new ConsList<Integer>(5,
// mt));
// IList<Integer> ints4 = new ConsList<Integer>(6, new ConsList<Integer>(7,
// mt));

// System.out.println(ints1.length());
// System.out.println(ints2.length());
// System.out.println(ints3.length());
// ints1.append(ints2);
// System.out.println(ints1.length());
// ints2.append(ints3);
// System.out.println(ints1.length());
// ints2.append(ints4);
// System.out.println(ints2.length());
// System.out.println(ints3.length());
// // ints4.append(ints4);
// // System.out.println(ints4.length());
// }
// }

// class Deque<T> {
// Sentinel<T> header;

// // Returns the number of items in this Deque (excluding the sentinel)
// int size();

// // EFFECT: inserts the given value at the front of the Deque
// void addToHead(T value);

// // EFFECT: inserts the given value at the back of the Deque
// void addToTail(T value);

// // Returns the current first item of the Deque
// // EFFECT: removes the current first item of the Deque
// T removeFromHead();

// // Returns the current last item of the Deque
// // EFFECT: removes the current last item of the Deque
// T removeFromTail();
// }

// class ANode<T> {
// ANode<T> next;
// ANode<T> prev;
// }

// class Sentinel<T> extends ANode<T> {
// }

// class Node<T> extends ANode<T> {
// T data;
// }ArrayLists:

// class ArrayList<T> {
// // returns the number of elements in the list
// int size();

// // EFFECT: Adds the given value at the end of the list
// boolean add(T value);

// // Returns the value at the given index
// T get(int index);

// // Returns the current value at the given index
// // EFFECT: Updates the value at the given index to the given new value
// T set(int index, T newValue);

// // Returns the element that is removed
// // EFFECT: Removes the element at the specified position in this list.
// T remove(int index)
// }