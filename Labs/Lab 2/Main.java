import tester.Tester;

// to represent a pet owner
class Person {

  String name;
  IPet pet;
  int age; // in years

  // the constructor
  Person(String name, IPet pet, int age) {
    this.name = name;
    this.pet = pet;
    this.age = age;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.name ... -- String
   * ... this.pet ... -- IPet
   * ... this.age ... -- int
   * 
   * Methods:
   * ... this.isOlder(Person) ... -- boolean
   * ... this.sameNamePet(String) ... -- boolean
   * ... this.perish() ... -- Person
   * 
   * METHODS FOR FIELDS:
   * 
   */

  // check if is this Person older than the given Person?
  boolean isOlder(Person other) {
    return this.age > other.age;
  }

  // check if the pet has the same name as the given name?
  boolean sameNamePet(String other) {
    return this.pet.sameNamePet(other);
  }

  // return a new person who's pet has perished
  Person perish() {
    return new Person(this.name, new NoPet(), this.age);
  }
}

// to represent a pet
interface IPet {

  // check if the pet has the same name as the given name?
  boolean sameNamePet(String other);

}

class NoPet implements IPet {

  // constructor
  NoPet() {

  }

  /* TEMPLATE: */

  // check if the pet has the same name as the given name?
  public boolean sameNamePet(String other) {
    return false;
  }
}

// to represent a pet cat
class Cat implements IPet {
  String name;
  String kind;
  boolean longhaired;

  // the constructor
  Cat(String name, String kind, boolean longhaired) {
    this.name = name;
    this.kind = kind;
    this.longhaired = longhaired;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.name ... -- String
   * ... this.kind ... -- String
   * ... this.longhaired ... -- boolean
   */

  // check if the pet has the same name as the given name?
  public boolean sameNamePet(String other) {
    return this.name.equals(other);
  }
}

// to represent a pet dog
class Dog implements IPet {
  String name;
  String kind;
  boolean male;

  Dog(String name, String kind, boolean male) {
    this.name = name;
    this.kind = kind;
    this.male = male;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.name ... -- String
   * ... this.kind ... -- String
   * ... this.male ... -- boolean
   */

  // check if the pet has the same name as the given name?
  public boolean sameNamePet(String other) {
    return this.name.equals(other);
  }
}

class ExamplesOwners {
  // Pets
  Cat bob = new Cat("Bob", "Siamese cat", false);
  Cat benjamin = new Cat("Benjamin", "Abyssinian", true);
  Dog brandon = new Dog("Brandon", "Terrier", true);
  Dog michael = new Dog("Michael", "Golden Retriever", false);
  NoPet none = new NoPet();

  // Person
  Person vignesh = new Person("Vignesh", bob, 1);
  Person shaan = new Person("Shaan", benjamin, 2);
  Person ivan = new Person("Ivan", brandon, 4);
  Person andrew = new Person("Anderw", michael, 4);
  Person anotherPerson = new Person("Anderw", none, 4);

  boolean testSameNamePet(Tester t) {
    return t.checkExpect(this.vignesh.sameNamePet("Bob"), true) &&
        t.checkExpect(this.shaan.sameNamePet("Ben"), false) &&
        t.checkExpect(this.anotherPerson.sameNamePet("Bob"), false);
  }

  boolean testIsOlder(Tester t) {
    return t.checkExpect(this.andrew.isOlder(vignesh), true) &&
        t.checkExpect(this.andrew.isOlder(ivan), false) &&
        t.checkExpect(this.anotherPerson.isOlder(anotherPerson), false);
  }

  boolean testPerish(Tester t) {
    return t.checkExpect(this.andrew.perish(), new Person("Anderw", this.none, 4)) &&
        t.checkExpect(this.ivan.perish(), new Person("Ivan", this.none, 4)) &&
        t.checkExpect(this.shaan.perish(), new Person("Shaan", this.none, 2));
  }

}
