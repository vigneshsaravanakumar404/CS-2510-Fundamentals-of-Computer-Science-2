import tester.Tester;
import java.awt.Color;
import javalib.worldimages.*;

//represents a group of tasks
class Group {
  String title;
  ILoTask tasks;

  // the constructor
  Group(String t, ILoTask tasks) {
    this.title = t;
    this.tasks = tasks;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.title ... -- String
   * ... this.tasks ... -- ILoTask
   * 
   * Methods:
   * ... this.rotate() ... -- Group
   * ... this.flip() ... -- Group
   * ... this.draw() ... -- WorldImage
   * 
   * METHODS FOR FIELDS:
   * ... this.tasks.flip() ... -- Group
   * ... this.tasks.draw() ... -- Group
   * ... this.tasks.checkBox() ... -- WorldImage
   * ... this.tasks.addToEnd() ... -- ILoTask
   */

  // rotate the list of tasks in this group
  Group rotate() {
    return new Group(this.title, this.tasks.rotate());
  }

  // flip the first task to done or not done
  Group flip() {
    return new Group(this.title, this.tasks.flip());
  }

  // draw the current state of this group of tasks
  WorldImage draw() {
    WorldImage tasksImage = this.tasks.draw();
    WorldImage titleImage = new TextImage(this.title, 25, Color.darkGray); // title
    return new OverlayOffsetImage(titleImage, 0, 80, tasksImage);
  }
}

// represents a task to be checked off a to-do list
class Task {
  String description;
  boolean isDone;

  Task(String d, boolean isDone) {
    this.description = d;
    this.isDone = isDone;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.description ... -- String
   * ... this.isDone ... -- boolean
   * 
   * Methods:
   * ... this.flip() ... -- Task
   * ... this.draw() ... -- WorldImage
   * ... this.drawCheckbox() ... -- WorldImage
   */

  // flip the completeness of this task
  Task flip() {
    return new Task(this.description, !this.isDone);
  }

  // draw this task as text
  WorldImage draw() {
    WorldImage bg = new RectangleImage(300, 200, "solid", Color.cyan);
    bg = new OverlayImage(new TextImage(this.description, 20, Color.black), bg);
    bg = new OverlayOffsetImage(this.drawCheckbox(), 0, -60, bg);
    return bg;
  }

  // draw the check box for this task
  WorldImage drawCheckbox() {
    if (this.isDone) {
      return new RectangleImage(20, 20, "solid", Color.black);
    }
    return new RectangleImage(20, 20, "outline", Color.black);
  }
}

// represents a list of tasks
interface ILoTask {
  // draw the first task's description as text or "No tasks to do" for this
  // ILoTask
  WorldImage draw();

  // rotate this list of tasks (moving the first to the end)
  ILoTask rotate();

  // flip the first task's completion status
  ILoTask flip();

  // add a task to the end of this list
  ILoTask addToEnd(Task t);
}

// represents an empty list of tasks
class MtLoTask implements ILoTask {

  /*
   * TEMPLATE:
   * Fields:
   * 
   * Methods:
   * ... this.draw() ... -- WorldImage
   * ... this.rotate() ... -- ILoTask
   * ... this.flip() ... -- ILoTask
   * ... this.addToEnd() ... -- ILoTask
   */

  // render that there are no tasks to do since this list is empty
  public WorldImage draw() {
    return new TextImage("No tasks to do", 30, Color.black);
  }

  // rotating an empty list gives an empty list
  public ILoTask rotate() {
    return this;
  }

  // can't flip a task in an empty list
  public ILoTask flip() {
    return this;
  }

  // add a task to the end of an empty list
  public ILoTask addToEnd(Task t) {
    return new ConsLoTask(t, this);
  }
}

// represents a non-empty list of tasks
class ConsLoTask implements ILoTask {
  Task first;
  ILoTask rest;

  // the constructor
  ConsLoTask(Task first, ILoTask rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * TEMPLATE:
   * Fields:
   * ... this.first ... -- Task
   * ... this.rest ... -- ILoTask
   * 
   * Methods:
   * ... this.draw() ... -- WorldImage
   * ... this.rotate() ... -- ILoTask
   * ... this.flip() ... -- ILoTask
   * ... this.addToEnd() ... -- ILoTask
   * 
   * METHODS FOR FIELDS:
   * ... this.tasks.flip() ... -- Group
   * ... this.tasks.draw() ... -- Group
   * ... this.tasks.checkBox() ... -- WorldImage
   * ... this.tasks.addToEnd() ... -- ILoTask
   * ... this.rest.draw() ... -- WorldImage
   * ... this.rest.rotate() ... -- ILoTask
   * ... this.rest.flip() ... -- ILoTask
   * ... this.rest.addToEnd() ... -- ILoTask
   */

  // draw the first task to do
  public WorldImage draw() {
    return this.first.draw();
  }

  // rotate by putting the first task at the end
  public ILoTask rotate() {
    return this.rest.addToEnd(this.first);
  }

  // flip the first task's completion status
  public ILoTask flip() {
    return new ConsLoTask(this.first.flip(), this.rest);
  }

  // add a task to the end of this list
  public ILoTask addToEnd(Task t) {
    return new ConsLoTask(this.first, this.rest.addToEnd(t));
  }
}

class Examples {
  Task clean = new Task("clean room", true);
  Task laundry = new Task("do a load of laundry", false);
  Task hw = new Task("work on fundies 2 assignment", false);
  Task lab = new Task("do lab exercises", true);
  Task sleep = new Task("sleep 8 hours", true);
  Task eat = new Task("breakfast", true);

  Task dd = new Task("Data Definitions", true);
  Task templ = new Task("Templates", false);
  Task examples = new Task("Examples", false);
  Task methodStubs = new Task("Method Stubs", false);
  Task checkExpects = new Task("Tests", false);
  Task code = new Task("Implement the methods", false);

  ILoTask mt = new MtLoTask();
  ILoTask cleaning = new ConsLoTask(this.clean, new ConsLoTask(this.laundry, this.mt));
  ILoTask cleaningRotated = new ConsLoTask(this.laundry, new ConsLoTask(this.clean, this.mt));
  ILoTask school = new ConsLoTask(this.hw, new ConsLoTask(this.lab, this.mt));
  ILoTask health = new ConsLoTask(this.sleep, new ConsLoTask(this.eat, this.mt));

  ILoTask coding = new ConsLoTask(this.checkExpects, new ConsLoTask(this.code, this.mt));
  ILoTask methods = new ConsLoTask(this.examples, new ConsLoTask(this.methodStubs, this.coding));

  ILoTask data = new ConsLoTask(this.dd, new ConsLoTask(this.templ, this.methods));

  Group houseWork = new Group("chores", this.cleaning);
  Group schoolWork = new Group("school work", this.school);
  Group living = new Group("health", this.health);
  Group design = new Group("design recipe", this.data);

  // calls big bang to launch the task manager
  boolean testBigBang(Tester t) {
    TaskWorld world = new TaskWorld(this.design);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .1;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }

  // Tests for the methods we implemented
  boolean testRotate(Tester t) {
    return t.checkExpect(this.houseWork.rotate(),
        new Group("chores", this.cleaningRotated)) &&
        t.checkExpect(new Group("empty", this.mt).rotate(),
            new Group("empty", this.mt))
        &&
        t.checkExpect(new Group("empty", this.mt).rotate().rotate(),
            new Group("empty", this.mt));
  }

  // tests for flip
  boolean testFlip(Tester t) {
    return t.checkExpect(this.houseWork.flip(),
        new Group("chores",
            new ConsLoTask(new Task("clean room", false),
                new ConsLoTask(this.laundry, this.mt))))
        &&
        t.checkExpect(new Group("empty", this.mt).flip(),
            new Group("empty", this.mt))
        &&
        t.checkExpect(new Group("empty", this.mt).flip().flip(),
            new Group("empty", this.mt));
  }

  // tests for fliping a task
  boolean testTaskFlip(Tester t) {
    return t.checkExpect(this.clean.flip(), new Task("clean room", false)) &&
        t.checkExpect(this.laundry.flip(), new Task("do a load of laundry", true)) &&
        t.checkExpect(this.laundry.flip().flip(), new Task("do a load of laundry", false));
  }

  // launch big bang
  boolean testBigBang(Tester t) {
    TaskWorld world = new TaskWorld(this.design);
    int worldWidth = 600;
    int worldHeight = 400;
    double tickRate = .1;
    return world.bigBang(worldWidth, worldHeight, tickRate);
  }

}