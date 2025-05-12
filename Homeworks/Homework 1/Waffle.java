// to represent a waffle
interface IWaffle {
}

// to represent a plain waffle
class Plain implements IWaffle {
    String flour;

    Plain(String flour) {
        this.flour = flour;
    }
}

// to represent a toped waffle
class Topped implements IWaffle {

    IWaffle below;
    String topping;

    public Topped(IWaffle below, String topping) {
        this.below = below;
        this.topping = topping;
    }
}

class ExamplesWaffle {

    IWaffle order1 = new Topped(
            new Topped(new Topped(new Topped(new Plain("almond"), "chocolate chips"),
                    "whipped cream"), "strawberries"),
            "walnuts");

    IWaffle order2 = new Topped(new Topped(new Plain("buckwheat"), "chicken"), "gravy");
}
