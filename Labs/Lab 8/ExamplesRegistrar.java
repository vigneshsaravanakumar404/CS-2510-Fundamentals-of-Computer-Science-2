import tester.*;

// represents a generic list of items
interface IList<T> {

  // returns if the item is in the list
  boolean contains(T item);
}

// represents an empty list
class MtList<T> implements IList<T> {

  // returns if the item is in the list
  public boolean contains(T item) {
    return false;
  }
}

// represents a non-empty list
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // the constructor for a non-empty list
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns if the item is in the list
  public boolean contains(T item) {
    return this.first.equals(item) || this.rest.contains(item);
  }
}

// Course class
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  // the constructor
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();
    this.prof.addCourse(this);
  }

  // Check if the given student is enrolled in this course
  boolean hasStudent(Student s) {
    return this.students.contains(s);
  }
}

// Instructor class
class Instructor {
  String name;
  IList<Course> courses;

  // the constructor
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // EFFECT: adds the course to this instructor's list of courses
  void addCourse(Course c) {
    if (c.prof != this) {
      throw new RuntimeException("Course is not taught by this instructor");
    }
    this.courses = new ConsList<Course>(c, this.courses);
  }

  // Determines whether the given student is in more than one of this instructor's
  // courses
  boolean dejavu(Student s) {
    return this.countCoursesWithStudent(this.courses, s) > 1;
  }

  // Helper: count courses that contain the student
  int countCoursesWithStudent(IList<Course> courses, Student s) {
    if (courses instanceof MtList) {
      return 0;
    }
    ConsList<Course> cons = (ConsList<Course>) courses;
    if (cons.first.hasStudent(s)) {
      return 1 + countCoursesWithStudent(cons.rest, s);
    } else {
      return countCoursesWithStudent(cons.rest, s);
    }
  }
}

// Student class
class Student {
  String name;
  int id;
  IList<Course> courses;

  // the constructor
  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  // EFFECT: enrolls this student in the given course
  void enroll(Course c) {
    if (this.courses.contains(c)) {
      throw new RuntimeException("Already enrolled in this course");
    }
    this.courses = new ConsList<Course>(c, this.courses);
    c.students = new ConsList<Student>(this, c.students);
  }

  // Determines whether the given student is in any of the same classes as this
  boolean classmates(Student s) {
    if (this == s) {
      return false;
    }
    return this.anySharedCourse(this.courses, s);
  }

  // returns if this student shares any course with the given student
  boolean anySharedCourse(IList<Course> courses, Student s) {
    if (courses instanceof MtList) {
      return false;
    }
    ConsList<Course> cons = (ConsList<Course>) courses;
    return cons.first.hasStudent(s) || anySharedCourse(cons.rest, s);
  }
}

// Examples and tests
class ExamplesRegistrar {

  Instructor profSmith;
  Instructor profJones;

  Course cs2510;
  Course cs3500;
  Course cs4400;
  Course cs4500;

  // Students
  Student alice;
  Student bob;
  Student charlie;
  Student diana;
  Student eve;

  // Initialize test data
  void initTestConditions() {
    // Create instructors
    this.profSmith = new Instructor("Prof. Smith");
    this.profJones = new Instructor("Prof. Jones");

    // Create courses with their instructors
    this.cs2510 = new Course("Fundies 2", this.profSmith);
    this.cs3500 = new Course("OOD", this.profSmith);
    this.cs4400 = new Course("Programming Languages", this.profJones);
    this.cs4500 = new Course("Software Dev", this.profJones);

    // Create students
    this.alice = new Student("Alice", 1001);
    this.bob = new Student("Bob", 1002);
    this.charlie = new Student("Charlie", 1003);
    this.diana = new Student("Diana", 1004);
    this.eve = new Student("Eve", 1005);
  }

  // Test the enroll method
  void testEnroll(Tester t) {
    this.initTestConditions();

    // Check initial conditions
    t.checkExpect(this.alice.courses.contains(this.cs2510), false);
    t.checkExpect(this.cs2510.students.contains(this.alice), false);

    // Enroll Alice in CS2510
    this.alice.enroll(this.cs2510);

    // Check that enrollment worked both ways
    t.checkExpect(this.alice.courses.contains(this.cs2510), true);
    t.checkExpect(this.cs2510.students.contains(this.alice), true);

    // Enroll more students
    this.bob.enroll(this.cs2510);
    this.bob.enroll(this.cs3500);
    this.charlie.enroll(this.cs2510);
    this.diana.enroll(this.cs3500);
    this.diana.enroll(this.cs4400);

    // Check multiple enrollments
    t.checkExpect(this.bob.courses.contains(this.cs2510), true);
    t.checkExpect(this.bob.courses.contains(this.cs3500), true);
    t.checkExpect(this.cs2510.students.contains(this.bob), true);
    t.checkExpect(this.cs3500.students.contains(this.bob), true);

    // Test that we can't enroll twice in the same course
    t.checkException(
        new RuntimeException("Already enrolled in this course"),
        this.alice, "enroll", this.cs2510);
  }

  // Test the classmates method
  void testClassmates(Tester t) {
    this.initTestConditions();

    // Initially no one is classmates
    t.checkExpect(this.alice.classmates(this.bob), false);

    // Enroll students in courses
    this.alice.enroll(this.cs2510);
    this.alice.enroll(this.cs3500);
    this.bob.enroll(this.cs2510);
    this.charlie.enroll(this.cs3500);
    this.diana.enroll(this.cs4400);
    this.eve.enroll(this.cs4500);

    // Test classmates
    t.checkExpect(this.alice.classmates(this.bob), true); // Both in CS2510
    t.checkExpect(this.alice.classmates(this.charlie), true); // Both in CS3500
    t.checkExpect(this.alice.classmates(this.diana), false); // No common courses
    t.checkExpect(this.alice.classmates(this.eve), false); // No common courses
    t.checkExpect(this.bob.classmates(this.charlie), false); // No common courses

    // A student is not their own classmate
    t.checkExpect(this.alice.classmates(this.alice), false);

    // Test symmetric property
    t.checkExpect(this.bob.classmates(this.alice), true);
  }

  // Test the dejavu method
  void testDejavu(Tester t) {
    this.initTestConditions();

    // Initially no dejavu
    t.checkExpect(this.profSmith.dejavu(this.alice), false);

    // Enroll Alice in one course with Prof Smith
    this.alice.enroll(this.cs2510);
    t.checkExpect(this.profSmith.dejavu(this.alice), false);

    // Enroll Alice in another course with Prof Smith
    this.alice.enroll(this.cs3500);
    t.checkExpect(this.profSmith.dejavu(this.alice), true);

    // Bob in only one course with Prof Smith
    this.bob.enroll(this.cs2510);
    t.checkExpect(this.profSmith.dejavu(this.bob), false);

    // Charlie in both courses with Prof Jones
    this.charlie.enroll(this.cs4400);
    this.charlie.enroll(this.cs4500);
    t.checkExpect(this.profJones.dejavu(this.charlie), true);
    t.checkExpect(this.profSmith.dejavu(this.charlie), false);

    // Diana in one course with each professor
    this.diana.enroll(this.cs2510);
    this.diana.enroll(this.cs4400);
    t.checkExpect(this.profSmith.dejavu(this.diana), false);
    t.checkExpect(this.profJones.dejavu(this.diana), false);
  }

  // Additional test for complex scenarios
  void testComplexEnrollments(Tester t) {
    this.initTestConditions();

    // Create a complex enrollment scenario
    this.alice.enroll(this.cs2510);
    this.alice.enroll(this.cs3500);
    this.alice.enroll(this.cs4400);

    this.bob.enroll(this.cs2510);
    this.bob.enroll(this.cs3500);
    this.bob.enroll(this.cs4500);

    this.charlie.enroll(this.cs2510);
    this.charlie.enroll(this.cs4400);
    this.charlie.enroll(this.cs4500);

    // Test multiple classmate relationships
    t.checkExpect(this.alice.classmates(this.bob), true); // CS2510 & CS3500
    t.checkExpect(this.alice.classmates(this.charlie), true); // CS2510 & CS4400
    t.checkExpect(this.bob.classmates(this.charlie), true); // CS2510 & CS4500

    // Test dejavu with multiple students
    t.checkExpect(this.profSmith.dejavu(this.alice), true); // CS2510 & CS3500
    t.checkExpect(this.profSmith.dejavu(this.bob), true); // CS2510 & CS3500
    t.checkExpect(this.profSmith.dejavu(this.charlie), false); // Only CS2510
    t.checkExpect(this.profJones.dejavu(this.charlie), true); // CS4400 & CS4500
  }
}