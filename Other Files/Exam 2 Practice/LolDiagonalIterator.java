import java.util.ArrayList;

class LolDiagonalIterator {

  public static void main(String[] args) {

  }
}

interface Iterator<T> {
  boolean hasNext();

  T next();

}

interface Iterable<T> {
  java.util.Iterator<Person> Iterator();
}

class LolDiagonalIteratorIterator<T> implements Iterator<T> {
  int index;
  ArrayList<ArrayList<T>> data;

  LolDiagonalIteratorIterator(ArrayList<ArrayList<T>> data) {
    this.data = data;
    index = 0;
  }

  public boolean hasNext() {
    return index < data.size() && index < data.get(index).size();
  }

  public T next() {
    index++;
    return data.get(index - 1).get(index - 1);
  }
}

class TwoDArrayList<T> implements Iterable<T> {
  ArrayList<ArrayList<T>> list;

  TwoDArrayList(ArrayList<ArrayList<T>> list) {
    this.list = list;
  }

  public Iterator<T> Iterator() {
    return new LolDiagonalIteratorIterator<T>(this.list);
  }
}