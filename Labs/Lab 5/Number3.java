interface ILoString {

  ILoString add(String str);
}

class MtLoString implements ILoString {

  MtLoString() {
  }

  public ILoString add(String str) {
    return new ConsLoString(str, this);
  }

}

class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  public ILoString add(String str) {
    return new ConsLoString(str, this);
  }

}

class PairOfLists {
  ILoString first, second;

  PairOfLists(ILoString first, ILoString second) {
    this.first = first;
    this.second = second;
  }

  // Produces a new pair of lists, with the given String added to
  // the front of the first list of this pair
  PairOfLists addToFirst(String first) {
    return new PairOfLists(this.first.add(first), this.second);
  }

  // Produces a new pair of lists, with the given String added to
  // the front of the second list of this pair
  PairOfLists addToSecond(String second) {
    return new PairOfLists(this.first, this.second.add(second));
  }

  PairOfLists unzip(ILoString list) {
    return unzip(list, true);
  }

  PairOfLists unzip(ILoString list, boolean flip) {

  }

}
