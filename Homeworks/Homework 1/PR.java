// represents a resource
interface IResource {
}

// represents a denial resource
class Denial implements IResource {
    String subject;
    int believability;

    // The constructor
    Denial(String subject, int believability) {
        this.subject = subject;
        this.believability = believability;
    }
}

// represents a bribe
class Bribe implements IResource {
    String target;
    int amount;

    // The constructor
    Bribe(String target, int amount) {
        this.target = target;
        this.amount = amount;
    }
}

// represents an apology
class Apology implements IResource {
    String excuse;
    boolean reusable;

    // The constructor
    Apology(String excuse, boolean reusable) {
        this.excuse = excuse;
        this.reusable = reusable;
    }
}

// represents an action
interface IAction {
}

// represents the purchase action
class Purchase implements IAction {
    int cost;
    IResource item;

    // The constructor
    Purchase(int cost, IResource item) {
        this.cost = cost;
        this.item = item;
    }
}

// represents a swap action
class Swap implements IAction {
    IResource consumed;
    IResource received;

    // The constructor
    Swap(IResource consumed, IResource received) {
        this.consumed = consumed;
        this.received = received;
    }
}

class ExamplesGame {
    // Resources
    IResource iDidntKnow = new Denial("knowledge", 51);
    IResource dueTomorrow = new Denial("god knows what", 60);

    IResource witness = new Bribe("innocent witness", 49);
    IResource highBribe = new Bribe("media", 50);

    IResource iWontDoItAgain = new Apology("I won't do it again", false);
    IResource weakExcuse = new Apology("It was a mistake", false);
    IResource strongExcuse = new Apology("It was system error", true);

    // Actions
    IAction purchase1 = new Purchase(50, iDidntKnow);
    IAction purchase2 = new Purchase(60, strongExcuse);

    IAction swap1 = new Swap(witness, highBribe); // 49 to 52 valid
    IAction swap2 = new Swap(iWontDoItAgain, weakExcuse); // 50 to 50 valid
}