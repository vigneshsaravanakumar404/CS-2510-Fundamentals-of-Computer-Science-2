import tester.Tester;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// represents a simple permutation code that can encode and decode messages
class PermutationCode {
  ArrayList<Character> alphabet = new ArrayList<Character>(Arrays.asList(
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
      't', 'u', 'v', 'w', 'x', 'y', 'z'));
  ArrayList<Character> code = new ArrayList<Character>(26);
  Random rand = new Random();

  // the constructor
  PermutationCode() {
    this.code = this.initEncoder();
  }

  // the constructor
  PermutationCode(ArrayList<Character> code) {
    this.code = code;
  }

  // initializes the encoder with a random permutation of the alphabet
  ArrayList<Character> initEncoder() {
    ArrayList<Character> alphabetCopy = new ArrayList<Character>(this.alphabet);
    ArrayList<Character> encoder = new ArrayList<Character>(26);

    while (!alphabetCopy.isEmpty()) {
      int randomIndex = rand.nextInt(alphabetCopy.size());
      encoder.add(alphabetCopy.remove(randomIndex));
    }

    return encoder;
  }

  // returns the code used for encoding
  String encode(String source) {
    return transform(source, this.alphabet, this.code);
  }

  // returns the code used for decoding
  String decode(String code) {
    return transform(code, this.code, this.alphabet);
  }

  // transforms the text using the provided fromList and toList
  String transform(String text, ArrayList<Character> fromList, ArrayList<Character> toList) {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < text.length(); i++) {
      char currentChar = text.charAt(i);
      int index = fromList.indexOf(currentChar);
      result.append(toList.get(index));
    }

    return result.toString();
  }
}