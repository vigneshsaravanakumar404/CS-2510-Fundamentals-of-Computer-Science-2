import tester.Tester;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// represents a code that permutes the alphabet
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
  PermutationCode(long seed) {
    this.rand.setSeed(seed);
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

  // returns the encoded string using the code
  String encode(String source) {
    return transform(source, this.alphabet, this.code);
  }

  // returns the decoded string using the code
  String decode(String code) {
    return transform(code, this.code, this.alphabet);
  }

  // transforms the text from one character set to another
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

class ExamplesPermutationCode {

  ArrayList<Character> alphabet = new ArrayList<Character>(Arrays.asList(
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
      't', 'u', 'v', 'w', 'x', 'y', 'z'));
  ArrayList<Character> reverseCode = new ArrayList<Character>(Arrays.asList(
      'z', 'y', 'x', 'w', 'v', 'u', 't', 's', 'r', 'q',
      'p', 'o', 'n', 'm', 'l', 'k', 'j', 'i', 'h',
      'g', 'f', 'e', 'd', 'c', 'b', 'a'));
  ArrayList<Character> shiftCode = new ArrayList<Character>(Arrays.asList(
      'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
      'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', 'a'));
  ArrayList<Character> customCode = new ArrayList<Character>(Arrays.asList(
      'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
      'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
      'z', 'x', 'c', 'v', 'b', 'n', 'm'));
  
  boolean testInitEncoder(Tester t) {
    PermutationCode pc1 = new PermutationCode(12345L);
    PermutationCode pc2 = new PermutationCode(67890L);
    ArrayList<Character> code2 = pc2.code;

    boolean hasAllLetters = true;
    for (Character c : this.alphabet) {
      if (!code2.contains(c)) {
        hasAllLetters = false;
      }
    }

    PermutationCode pc3a = new PermutationCode(11111L);
    PermutationCode pc3b = new PermutationCode(11111L);

    PermutationCode pc4a = new PermutationCode(22222L);
    PermutationCode pc4b = new PermutationCode(33333L);

    return t.checkExpect(pc1.initEncoder().size() == 26, true) &&
        t.checkExpect(hasAllLetters && code2.size() == 26, true) &&
        t.checkExpect(pc3a.code.equals(pc3b.code), true) &&
        t.checkExpect(!pc4a.code.equals(pc4b.code), true);
  }

  boolean testEncode(Tester t) {
    PermutationCode pc1 = new PermutationCode(this.reverseCode);
    PermutationCode pc2 = new PermutationCode(this.reverseCode);
    PermutationCode pc3 = new PermutationCode(this.shiftCode);
    PermutationCode pc4 = new PermutationCode(this.customCode);

    return t.checkExpect(pc1.encode("a"), "z") &&
        t.checkExpect(pc2.encode("hello"), "svool") &&
        t.checkExpect(pc3.encode("xyz"), "yza") &&
        t.checkExpect(pc4.encode(""), "");
  }

  boolean testDecode(Tester t) {
    PermutationCode pc1 = new PermutationCode(this.reverseCode);
    PermutationCode pc2 = new PermutationCode(this.reverseCode);
    PermutationCode pc3 = new PermutationCode(this.customCode);
    String original3 = "testing";
    String encoded3 = pc3.encode(original3);
    PermutationCode pc4 = new PermutationCode(this.shiftCode);

    return t.checkExpect(pc1.decode("z"), "a") &&
        t.checkExpect(pc2.decode("svool"), "hello") &&
        t.checkExpect(pc3.decode(encoded3), original3) &&
        t.checkExpect(pc4.decode("bcd"), "abc");
  }

  boolean testTransform(Tester t) {
    PermutationCode pc = new PermutationCode();

    ArrayList<Character> from = new ArrayList<Character>(Arrays.asList('a', 'b', 'c'));
    ArrayList<Character> to = new ArrayList<Character>(Arrays.asList('x', 'y', 'z'));

    return t.checkExpect(pc.transform("abc", this.alphabet, this.alphabet), "abc") &&
        t.checkExpect(pc.transform("abc", this.alphabet, this.reverseCode), "zyx") &&
        t.checkExpect(pc.transform("", this.alphabet, this.reverseCode), "") &&
        t.checkExpect(pc.transform("abcabc", from, to), "xyzxyz");
  }

  boolean testIntegration(Tester t) {
    PermutationCode pc1 = new PermutationCode(54321L);
    String original1 = "thequickbrownfox";

    PermutationCode pc2 = new PermutationCode(this.reverseCode);
    String allLetters = "abcdefghijklmnopqrstuvwxyz";

    PermutationCode pc3a = new PermutationCode(this.shiftCode);
    PermutationCode pc3b = new PermutationCode(this.reverseCode);
    String message3 = "secret";
    String firstEncode = pc3a.encode(message3);
    String firstDecode = pc3b.decode(pc3b.encode(firstEncode));

    PermutationCode pc4 = new PermutationCode(this.customCode);
    String encoded4 = pc4.encode("aaa");

    return t.checkExpect(pc1.decode(pc1.encode(original1)), original1) &&
        t.checkExpect(pc2.decode(pc2.encode(allLetters)), allLetters) &&
        t.checkExpect(pc3a.decode(firstDecode), message3) &&
        t.checkExpect(encoded4.charAt(0) == encoded4.charAt(1) &&
            encoded4.charAt(1) == encoded4.charAt(2), true)
        &&
        t.checkExpect(encoded4, "qqq");
  }

  boolean testEdgeCases(Tester t) {
    PermutationCode pc1 = new PermutationCode(99999L);
    boolean allCharactersWork = true;
    for (char c : this.alphabet) {
      String original = String.valueOf(c);
      if (!pc1.decode(pc1.encode(original)).equals(original)) {
        allCharactersWork = false;
        break;
      }
    }

    PermutationCode pc2 = new PermutationCode(this.reverseCode);
    String longString = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";

    PermutationCode pc3 = new PermutationCode(shiftCode);

    PermutationCode pc4 = new PermutationCode(12345L);
    boolean isValidPermutation = pc4.code.size() == 26;
    for (char c : this.alphabet) {
      if (!pc4.code.contains(c)) {
        isValidPermutation = false;
        break;
      }
    }

    return t.checkExpect(allCharactersWork, true) &&
        t.checkExpect(pc2.decode(pc2.encode(longString)), longString) &&
        t.checkExpect(pc3.encode("aaabbbccc"), "bbbcccddd") &&
        t.checkExpect(isValidPermutation, true);
  }
}