import tester.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

// represents a utils class
class Utils {

  // returns a new ArrayList containing the elements of arr that pass the
  // predicate pred
  <T> ArrayList<T> customFilter(ArrayList<T> arr, Predicate<T> pred, boolean keepPassing) {
    ArrayList<T> result = new ArrayList<>();
    for (T item : arr) {
      if (pred.test(item) == keepPassing) {
        result.add(item);
      }
    }
    return result;
  }

  // returns a new ArrayList containing the elements of arr that pass the
  <T> ArrayList<T> filter(ArrayList<T> arr, Predicate<T> pred) {
    return customFilter(arr, pred, true);
  }

  // returns a new ArrayList containing the elements of arr that do not pass
  <T> ArrayList<T> filterNot(ArrayList<T> arr, Predicate<T> pred) {
    return customFilter(arr, pred, false);
  }

  // removes elements from arr that do not pass the predicate pred
  <T> void removeFailing(ArrayList<T> arr, Predicate<T> pred) {
    customRemove(arr, pred, true);
  }

  // removes all elements from arr that pass the predicate pred
  <T> void removePassing(ArrayList<T> arr, Predicate<T> pred) {
    customRemove(arr, pred, false);
  }

  // removes elements from arr that do not pass the predicate pred
  <T> void customRemove(ArrayList<T> arr, Predicate<T> pred, boolean keepPassing) {
    for (int i = arr.size() - 1; i >= 0; i--) {
      if (pred.test(arr.get(i)) != keepPassing) {
        arr.remove(i);
      }
    }
  }

  // returns an interweaved ArrayList of arr1 and arr2
  <T> ArrayList<T> interweave(ArrayList<T> arr1, ArrayList<T> arr2) {
    ArrayList<T> result = new ArrayList<>();
    int size = Math.min(arr1.size(), arr2.size());
    for (int i = 0; i < size; i++) {
      result.add(arr1.get(i));
      result.add(arr2.get(i));
    }

    if (arr1.size() > size) {
      result.addAll(arr1.subList(size, arr1.size()));
    } else if (arr2.size() > size) {
      result.addAll(arr2.subList(size, arr2.size()));
    }
    return result;
  }

  // returns an interweaved ArrayList of arr1 and arr2, taking up to
  // getFrom1 elements from arr1 and getFrom2 elements from arr2 at a time
  <T> ArrayList<T> customInterweave(ArrayList<T> arr1, ArrayList<T> arr2,
      int getFrom1, int getFrom2) {
    ArrayList<T> result = new ArrayList<>();
    int i1 = 0;
    int i2 = 0;

    // Continue while at least one list has elements remaining
    while (i1 < arr1.size() || i2 < arr2.size()) {
      // Take up to getFrom1 elements from arr1
      for (int i = 0; i < getFrom1 && i1 < arr1.size(); i++) {
        result.add(arr1.get(i1));
        i1++;
      }

      // Take up to getFrom2 elements from arr2
      for (int i = 0; i < getFrom2 && i2 < arr2.size(); i++) {
        result.add(arr2.get(i2));
        i2++;
      }
    }

    return result;
  }

}

class ExampleUtils {

  Utils utils = new Utils();
  ArrayList<Integer> a1;
  ArrayList<String> a2;
  ArrayList<Character> a3;
  ArrayList<Double> a4;
  ArrayList<Boolean> a5;

  void initializeData() {
    a1 = new ArrayList<>();
    a2 = new ArrayList<>();
    a3 = new ArrayList<>();
    a4 = new ArrayList<>();
    a5 = new ArrayList<>();

    a1.add(1);
    a1.add(2);
    a1.add(3);
    a1.add(4);
    a1.add(5);

    a2.add("abc");
    a2.add("bde");
    a2.add("cde");
    a2.add("def");
    a2.add("efg");

    a3.add('x');
    a3.add('y');
    a3.add('z');
    a3.add('w');
    a3.add('v');

    a4.add(1.1);
    a4.add(2.2);
    a4.add(3.3);
    a4.add(4.4);
    a4.add(5.5);

    a5.add(true);
    a5.add(false);
    a5.add(true);
    a5.add(false);
    a5.add(true);
  }

  // tests for custom filter
  boolean testcustomFilter(Tester t) {
    initializeData();
    ArrayList<Integer> filtered = utils.filter(a1, x -> x > 2);
    return t.checkExpect(utils.customFilter(a1, x -> x > 2, true), new ArrayList<Integer>(Arrays.asList(3, 4, 5)))
        && t.checkExpect(utils.customFilter(a1, x -> x > 2, false), new ArrayList<Integer>(Arrays.asList(1, 2)))
        && t.checkExpect(utils.customFilter(a2, x -> x.startsWith("a"), true),
            new ArrayList<String>(Arrays.asList("abc")));
  }

  // Tests for filter
  boolean testFilter(Tester t) {
    initializeData();
    return t.checkExpect(utils.filter(a1, x -> x > 2), new ArrayList<Integer>(Arrays.asList(3, 4, 5)))
        && t.checkExpect(utils.filter(a2, x -> x.startsWith("a")), new ArrayList<String>(Arrays.asList("abc")))
        && t.checkExpect(utils.filter(a3, x -> x < 'y'), new ArrayList<Character>(Arrays.asList('x', 'w', 'v')))
        && t.checkExpect(utils.filter(a4, x -> x < 3.0), new ArrayList<Double>(Arrays.asList(1.1, 2.2)))
        && t.checkExpect(utils.filter(a5, x -> !x), new ArrayList<Boolean>(Arrays.asList(false, false)));
  }

  // Tests for filterNot
  boolean testFilterNot(Tester t) {
    initializeData();
    return t.checkExpect(utils.filterNot(a1, x -> x > 2), new ArrayList<Integer>(Arrays.asList(1, 2)))
        && t.checkExpect(utils.filterNot(a2, x -> x.startsWith("a")),
            new ArrayList<String>(Arrays.asList("bde", "cde", "def", "efg")))
        && t.checkExpect(utils.filterNot(a3, x -> x < 'y'), new ArrayList<Character>(Arrays.asList('y', 'z')))
        && t.checkExpect(utils.filterNot(a4, x -> x < 3.0), new ArrayList<Double>(Arrays.asList(3.3, 4.4, 5.5)))
        && t.checkExpect(utils.filterNot(a5, x -> !x), new ArrayList<Boolean>(Arrays.asList(true, true, true)));
  }

  // tests for removeFailing
  void testRemoveFailing(Tester t) {
    initializeData();
    utils.removeFailing(a1, x -> x > 2);
    t.checkExpect(a1, new ArrayList<Integer>(Arrays.asList(3, 4, 5)));
    utils.removeFailing(a2, x -> x.startsWith("a"));
    t.checkExpect(a2, new ArrayList<String>(Arrays.asList("abc")));
    utils.removeFailing(a3, x -> x < 'y');
    t.checkExpect(a3, new ArrayList<Character>(Arrays.asList('x', 'w', 'v')));
  }

  // tests for removePassing
  void testRemovePassing(Tester t) {
    initializeData();
    utils.removePassing(a1, x -> x > 2);
    t.checkExpect(a1, new ArrayList<Integer>(Arrays.asList(1, 2)));
    utils.removePassing(a2, x -> x.startsWith("a"));
    t.checkExpect(a2, new ArrayList<String>(Arrays.asList("bde", "cde", "def", "efg")));
    utils.removePassing(a3, x -> x < 'y');
    t.checkExpect(a3, new ArrayList<Character>(Arrays.asList('y', 'z')));
  }

  // tests for customRemove
  void testCustomRemove(Tester t) {
    initializeData();
    utils.customRemove(a1, x -> x > 2, true);
    t.checkExpect(a1, new ArrayList<Integer>(Arrays.asList(3, 4, 5)));
    utils.customRemove(a2, x -> x.startsWith("a"), true);
    t.checkExpect(a2, new ArrayList<String>(Arrays.asList("abc")));
    utils.customRemove(a3, x -> x < 'y', true);
    t.checkExpect(a3, new ArrayList<Character>(Arrays.asList('x', 'w', 'v')));
  }

  // tests for interweave
  boolean testInterweave(Tester t) {
    initializeData();
    ArrayList<Integer> interweaved1 = utils.interweave(a1, a1);
    ArrayList<String> interweaved2 = utils.interweave(a2, a2);
    ArrayList<Character> interweaved3 = utils.interweave(a3, a3);
    ArrayList<Double> interweaved4 = utils.interweave(a4, a4);
    ArrayList<Boolean> interweaved5 = utils.interweave(a5, a5);

    return t.checkExpect(interweaved1,
        new ArrayList<Integer>(Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4, 5, 5)))
        && t.checkExpect(interweaved2,
            new ArrayList<String>(Arrays.asList("abc", "abc", "bde", "bde", "cde", "cde", "def", "def", "efg", "efg")))
        && t.checkExpect(interweaved3,
            new ArrayList<Character>(Arrays.asList('x', 'x', 'y', 'y', 'z', 'z', 'w', 'w', 'v', 'v')))
        && t.checkExpect(interweaved4,
            new ArrayList<Double>(Arrays.asList(1.1, 1.1, 2.2, 2.2, 3.3, 3.3, 4.4, 4.4, 5.5, 5.5)))
        && t.checkExpect(interweaved5,
            new ArrayList<Boolean>(Arrays.asList(true, true, false, false, true, true, false, false, true, true)));
  }

  // tests for customInterweave
  boolean testCustomInterweave(Tester t) {
    initializeData();
    ArrayList<Integer> customInterweaved1 = utils.customInterweave(a1, a1, 2, 3);
    ArrayList<String> customInterweaved2 = utils.customInterweave(a2, a2, 1, 2);
    ArrayList<Character> customInterweaved3 = utils.customInterweave(a3, a3, 3, 1);
    ArrayList<Double> customInterweaved4 = utils.customInterweave(a4, a4, 2, 2);
    ArrayList<Boolean> customInterweaved5 = utils.customInterweave(a5, a5, 1, 3);

    return t.checkExpect(customInterweaved1,
        new ArrayList<Integer>(Arrays.asList(1, 2, 1, 2, 3, 3, 4, 4, 5, 5)))
        && t.checkExpect(customInterweaved3,
            new ArrayList<Character>(Arrays.asList('x', 'y', 'z', 'x', 'w', 'v', 'y', 'z', 'w', 'v')))
        && t.checkExpect(customInterweaved5,
            new ArrayList<Boolean>(Arrays.asList(true, true, false, true, false, false, true, true, false, true)));
  }

}