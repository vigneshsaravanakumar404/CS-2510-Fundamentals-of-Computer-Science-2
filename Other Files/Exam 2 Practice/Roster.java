import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import tester.Tester;

class RosterIterator implements Iterator<String> {
  ArrayList<String> roster;
  int index = 0;

  RosterIterator(ArrayList<String> roster) {
    this.roster = roster;
  }

  public boolean hasNext() {
    return index < roster.size();
  }

  @Override
  public String next() {
    index++;
    return roster.get(index - 1);
  }

}

// Represents a roster of students for a sports team
class Roster implements Iterable<String> {
  // names of the students
  ArrayList<String> names;

  // create a roster from a given list of names
  Roster(ArrayList<String> names) {
    this.names = names;
  }

  // adds a name to this Roster's list of names
  void addName(String name) {
    this.names.add(name);
  }

  // returns the list of students in this Roster
  ArrayList<String> getNames() {
    return this.names;
  }

  public Iterator<String> iterator() {
    return new RosterIterator(this.names);
  }
}

// examples class
class ExamplesRoster {
  ArrayList<String> defaultRoster;
  Roster volleyball;
  Roster tennis;
  Roster swimming;
  Roster figureSkating;

  void initData() {
    this.defaultRoster = new ArrayList<String>(Arrays.asList("Alice"));

    this.volleyball = new Roster(this.defaultRoster);
    this.tennis = new Roster(this.defaultRoster);
    this.swimming = new Roster(this.defaultRoster);
    this.figureSkating = new Roster(this.defaultRoster);

  }

  // tests the addName method
  void testAddName(Tester t) {
    this.initData();

    t.checkExpect(this.volleyball.names.size(), 1);
    t.checkExpect(this.volleyball.names, new ArrayList<String>(Arrays.asList("Alice")));
    t.checkExpect(this.tennis.names.size(), 1);
    t.checkExpect(this.tennis.names, new ArrayList<String>(Arrays.asList("Alice")));

    this.volleyball.addName("Bob");

    t.checkExpect(this.volleyball.names.size(), 2);
    t.checkExpect(this.volleyball.names, new ArrayList<String>(Arrays.asList("Alice", "Bob")));

    // why do the two tests below fail?
    t.checkExpect(this.tennis.names.size(), 2); // should be 2
    t.checkExpect(this.tennis.names, new ArrayList<String>(Arrays.asList("Alice", "Bob"))); // should include bob
  }

  // tests the getNames method
  void testGetNames(Tester t) {
    this.initData();

    t.checkExpect(this.figureSkating.getNames().size(), 1);
    t.checkExpect(this.figureSkating.getNames(), new ArrayList<String>(Arrays.asList("Alice")));
    t.checkExpect(this.swimming.getNames().size(), 1);
    t.checkExpect(this.swimming.getNames(), new ArrayList<String>(Arrays.asList("Alice")));

    ArrayList<String> fgList = this.figureSkating.getNames();
    fgList.add("Bob");

    t.checkExpect(fgList.size(), 2);
    t.checkExpect(fgList, new ArrayList<String>(Arrays.asList("Alice", "Bob")));

    // why do the three tests below fail?
    // fglist points to the arraylist in figureSkating.names which is the
    // defaultRoster
    t.checkExpect(this.figureSkating.getNames(), new ArrayList<String>(Arrays.asList("Alice", "Bob")));
    t.checkExpect(this.swimming.getNames().size(), 2);
    t.checkExpect(this.swimming.getNames(), new ArrayList<String>(Arrays.asList("Alice", "Bob")));

  }
}
