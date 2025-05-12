// a representation of a Dog
class Dog {

    String name;
    String breed;
    int yob;
    String state;
    boolean hypoallergenic;

    // constructor
    Dog(String name, String breed, int yob, String state, boolean hypoallergenic) {
        this.name = name;
        this.breed = breed;
        this.yob = yob;
        this.state = state;
        this.hypoallergenic = hypoallergenic;
    }
}

class ExamplesDog {
    Dog huffle = new Dog("Hufflepuff", "Wheaten Terrier", 2020, "TX", true);
    Dog pearl = new Dog("Pearl", "Labrador Retriever", 2019, "MA", false);
    Dog nova = new Dog("Nova", "Border Collie", 2021, "CA", true);
    Dog baxter = new Dog("Baxter", "Golden Retriever", 2018, "CO", false);
    Dog luna = new Dog("Luna", "Australian Shepherd", 2022, "WA", true);
}