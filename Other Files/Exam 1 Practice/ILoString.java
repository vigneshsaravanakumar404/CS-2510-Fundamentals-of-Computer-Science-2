interface ILoString {

  int length();

  ILoString keepEveryOther();

  ILoString keepEveryN(int i, int n);

  ILoString keepEveryThird();

  ILoString reverse();

  ILoString moveToEnd(String element);

  ILoString reverse2();

  ILoString reverse2Accumulator(ILoString accum);
}

class MtLoString implements ILoString {

  MtLoString() {
  }

  public int length() {
    return 0;
  }

  public ILoString keepEveryOther() {
    return this;
  }

  public ILoString keepEveryN(int i, int n) {
    return this;
  }

  public ILoString keepEveryThird() {
    return this;
  }

  // Interface
  public ILoString reverse() {
    return this;
  }

  public ILoString moveToEnd(String element) {
    return new ConsLoString(element, this);
  }

  public ILoString reverse2() {
    return this;
  }

  public ILoString reverse2Accumulator(ILoString accum) {
    return accum;
  }

}

class ConsLoString implements ILoString {

  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  public int length() {
    return 1 + this.rest.length();
  }

  public ILoString keepEveryOther() {
    return new ConsLoString(this.first, this.rest.keepEveryN(1, 2));
  }

  public ILoString keepEveryN(int i, int n) {
    if (i % n == 0) {
      return this.keepEveryOther();
    }

    return this.rest.keepEveryN(i + 1, n);
  }

  public ILoString keepEveryThird() {
    return new ConsLoString(this.first, this.rest.keepEveryN(1, 3));
  }

  // ConsLoString
  public ILoString reverse() {
    return this.rest.reverse().moveToEnd(first);
  }

  public ILoString moveToEnd(String element) {
    return new ConsLoString(this.first, this.rest.moveToEnd(element));
  }

  public ILoString revers2() {
    return this.rest.reverse2Accumulator(new ConsLoString(first, new MtLoString()));
  }

  public ILoString reverse2AccumuLoString(ILoString accum){
    return 
  }

}
