import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import tester.Tester;

class PersonIterator implements Iterator<Person> {
  private ArrayList<Person> children;

  PersonIterator(ArrayList<Person> children) {
    this.children = children;
  }

  public boolean hasNext() {
    return !children.isEmpty();
  }

  public Person next() {
    if (!hasNext()) {
      throw new NoSuchElementException("No more people in this family tree");
    }
    Person current = worklist.remove(0);
    worklist.addAll(current.listOfChildren);
    return current;
  }
}

// to represent a person with a name
// and their list of children (for a family tree)
class Person implements Iterable<Person> {
  String name;
  ArrayList<Person> listOfChildren;

  // standard constructor
  Person(String name, ArrayList<Person> children) {
    this.name = name;
    this.listOfChildren = children;
  }

  // convenience constructor
  Person(String name) {
    this.name = name;

  public Iterator<Person> iterator() {
    return new PersonIterator(this.listOfChildren);
  }
}

// to represent examples of Person
class ExamplesPerson {

  ExamplesPerson() {
  }

  // simple example
  Person c;
  Person b;
  Person a;

  // complex example
  Person len;
  Person kim;
  Person jan;
  Person hank;
  Person gabi;
  Person fay;
  Person ed;
  Person dan;
  Person cole;
  Person bob;
  Person ann;

  // initializes the data and the family trees
  void initData() {
    // simple example

    /*
     * A
     * |
     * (B C)
     * 
     */

    this.c = new Person("C");
    this.b = new Person("B");
    this.a = new Person("A", new ArrayList<Person>(Arrays.asList(this.b, this.c)));

    // complex example

    /*
     * Ann
     * |
     * (Bob Cole Dan)
     * | | |
     * (Ed Fay Gabi Hank) (Jan Kim) ()
     * |
     * (Len)
     * 
     * 
     * Ann's children: Bob, Cole, and Dan
     * Bob's children: Ed, Fay, Gabi, and Hank
     * Cole's children: Jan and Kim
     * Fay's children: Len
     * 
     */

    this.len = new Person("Len");
    this.kim = new Person("Kim");
    this.jan = new Person("Jan");
    this.hank = new Person("Hank");
    this.gabi = new Person("Gabi");
    ArrayList<Person> fayChildren = new ArrayList<Person>(
        Arrays.asList(this.len));
    this.fay = new Person("Fay", fayChildren);
    this.ed = new Person("Ed");
    this.dan = new Person("Dan");
    ArrayList<Person> coleChildren = new ArrayList<Person>(
        Arrays.asList(this.jan, this.kim));
    this.cole = new Person("Cole", coleChildren);
    ArrayList<Person> bobChildren = new ArrayList<Person>(
        Arrays.asList(this.ed, this.fay, this.gabi, this.hank));
    this.bob = new Person("Bob", bobChildren);
    ArrayList<Person> annChildren = new ArrayList<Person>(
        Arrays.asList(this.bob, this.cole, this.dan));
    this.ann = new Person("Ann", annChildren);
  }

  void testForEachLoopForSimpleExample(Tester t) {
    this.initData();

    ArrayList<Person> aPeople = new ArrayList<Person>(
        Arrays.asList(this.a, this.b, this.c));
    ArrayList<Person> aTest = new ArrayList<Person>();

    int testIndex = 0;
    for (Person p : this.a) {
      t.checkExpect(p, aPeople.get(testIndex));
      aTest.add(p);
      testIndex = testIndex + 1;
      // System.out.println(p.name);
    }

    // order should be A -> B -> C
    t.checkExpect(aTest, aPeople);
  }

  void testForEachLoopForComplexExample(Tester t) {
    this.initData();

    ArrayList<Person> annPeople = new ArrayList<Person>(
        Arrays.asList(this.ann, this.bob, this.cole,
            this.dan, this.ed, this.fay, this.gabi, this.hank,
            this.jan, this.kim, this.len));
    ArrayList<Person> annTest = new ArrayList<Person>();

    int testIndex = 0;
    for (Person p : this.ann) {
      t.checkExpect(p, annPeople.get(testIndex));
      annTest.add(p);
      testIndex = testIndex + 1;
      // System.out.println(p.name);
    }

    // order should be Ann -> Bob -> Cole -> Dan -> Ed -> Fay
    // -> Gabi -> Hank -> Jan -> Kim -> Len
    t.checkExpect(annTest, annPeople);
  }

  void testHasNextAndNextForSimpleExample(Tester t) {
    this.initData();

    Iterator<Person> famIter = this.a.iterator();

    ArrayList<Person> aPeople = new ArrayList<Person>(
        Arrays.asList(this.a, this.b, this.c));
    int testIndex = 0;
    while (famIter.hasNext()) {
      t.checkExpect(famIter.next(), aPeople.get(testIndex));
      testIndex = testIndex + 1;
    }

    t.checkExpect(famIter.hasNext(), false);
    t.checkException(new NoSuchElementException("No more people in this family tree"),
        famIter, "next");
  }

  void testHasNextAndNextForComplexExample(Tester t) {
    this.initData();

    Iterator<Person> famIter = this.ann.iterator();

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.ann);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.bob);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.cole);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.dan);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.ed);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.fay);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.gabi);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.hank);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.jan);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.kim);

    t.checkExpect(famIter.hasNext(), true);
    t.checkExpect(famIter.next(), this.len);

    t.checkExpect(famIter.hasNext(), false);
    t.checkException(new NoSuchElementException("No more people in this family tree"),
        famIter, "next");
  }
}