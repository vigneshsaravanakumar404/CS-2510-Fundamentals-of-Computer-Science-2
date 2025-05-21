import tester.Tester;

interface IExamQuestion {

  boolean isCorrect(String givenAns);

  boolean sameQuestion(IExamQuestion other);

  boolean sameTF(String q, String a, int p);

  boolean sameFC(String q, String a, int p, String A, String B, String C, String D);

  boolean sameFR(String q, String a, int p);

}

abstract class AExamQuestion implements IExamQuestion {
  String question;
  String answer;
  int points;

  AExamQuestion(String question, String answer, int points) {
    if (0 >= points && points >= 100) {
      throw new IllegalArgumentException("Points is not between 0 and 100 inclusive");
    }

    this.question = question;
    this.answer = answer;
    this.points = points;
  }

  public boolean isCorrect(String givenAns) {
    return answer.equals(givenAns);
  }

  public boolean sameTF(String q, String a, int p) {
    return false;
  }

  public boolean sameFC(String q, String a, int p, String A, String B, String C, String D) {
    return false;
  }

  public boolean sameFR(String q, String a, int p) {
    return false;
  }

}

class TrueFalse extends AExamQuestion {

  TrueFalse(String question, String answer, int points) {
    super(question, answer, points);
  }

  public boolean sameQuestion(IExamQuestion other) {
    return other.sameTF(this.question, this.answer, this.points);
  }

  public boolean sameTF(String q, String a, int p) {
    return this.question.equals(q) && this.answer.equals(a) && this.points == p;
  }
}

class FourChoice extends AExamQuestion {
  String choiceA;
  String choiceB;
  String choiceC;
  String choiceD;

  FourChoice(String question, String answer, String choiceA, String choiceB, String choiceC, String choiceD,
      int points) {
    super(question, answer, points);
    this.choiceA = choiceA;
    this.choiceB = choiceB;
    this.choiceC = choiceC;
    this.choiceD = choiceD;

  }

  public boolean sameQuestion(IExamQuestion other) {
    return other.sameFC(this.question, this.answer, this.points, this.choiceA, this.choiceB, this.choiceC,
        this.choiceD);
  }

  public boolean sameFC(String q, String a, int p, String A, String B, String C, String D) {
    return this.question.equals(q) && this.answer.equals(a) && this.choiceA.equals(A) && this.choiceB.equals(B)
        && this.choiceC.equals(C) && this.choiceD.equals(D) && points == p;
  }

}

class FreeResponse extends AExamQuestion {

  FreeResponse(String question, String answer, int points) {
    super(question, answer, points);
  }

  public boolean sameQuestion(IExamQuestion other) {
    return other.sameFR(this.question, this.answer, this.points);
  }

  public boolean sameFR(String q, String a, int p) {
    return this.question.equals(q) && this.answer.equals(a) && this.points == p;
  }

}

// include tests and purpose statements

class ExamplesExam {
  IExamQuestion question1 = new TrueFalse("1 + 1 = 2", "true", 3);
  IExamQuestion question2 = new TrueFalse("A triangle has 5 sides.", "false", 5);
  IExamQuestion question3 = new FourChoice("Boston is the capitial of what state?", "Massachusetts", "Rhode Island",
      "New Jersey", "Massachusetts", "Maine", 15);
  IExamQuestion question4 = new FourChoice("What season is May in?", "Spring", "Summer", "Spring", "Winter", "Fall",
      10);
  IExamQuestion question5 = new FreeResponse("What's the difference between an int and a double?",
      "An int is a whole number, while a double have decimal points.", 25);
  IExamQuestion question6 = new FreeResponse("What's the difference between a Dtring and a char?",
      "A char is a singular character, while a String is an array of characters.", 20);

  // NOTE: Only includes tests for main methods, should be testing your helpers
  // thoroughly as well
  // Think of the different test cases these tests cover!

  boolean testIsCorrect(Tester t) {
    return t.checkExpect(this.question1.isCorrect("false"), false, "False case for TrueFalse")
        && t.checkExpect(this.question1.isCorrect("true"), true, "True case for TrueFalse")
        && t.checkExpect(this.question2.isCorrect("false"), true, "Another true case for TrueFalse")
        && t.checkExpect(this.question3.isCorrect("Maine"), false, "False case for FourChoice")
        && t.checkExpect(this.question3.isCorrect("Massachusetts"), true, "True case for FourChoice")
        && t.checkExpect(this.question5.isCorrect("ermmm"), false, "False case for FreeResponse")
        && t.checkExpect(this.question5.isCorrect("An int is a whole number, while a double have decimal points."),
            true, "True case for FreeResponse");
  }

  boolean testSameQuestion(Tester t) {
    return t.checkExpect(this.question1.sameQuestion(this.question1), true, "True case: two same TrueFalse questions")
        && t.checkExpect(this.question2.sameQuestion(this.question1), false,
            "False case: two different TrueFalse questions")
        && t.checkExpect(this.question3.sameQuestion(this.question3), true, "True case: two same FourChoice questions")
        && t.checkExpect(this.question3.sameQuestion(this.question4), false,
            "False case: two different FourChoice questions")
        && t.checkExpect(this.question1.sameQuestion(this.question5), false, "False case: a TrueFalse and FreeResponse")
        && t.checkExpect(this.question3.sameQuestion(this.question1), false, "False case: a FourChoice and TrueFalse")
        && t.checkExpect(this.question5.sameQuestion(this.question3), false,
            "False case: a FreeResponse and FourChoice")
        && t.checkExpect(this.question6.sameQuestion(this.question5), false,
            "False case: two different FreeResponse questions")
        && t.checkExpect(this.question6.sameQuestion(this.question6), true,
            "True case: two same FreeResponse questions");
  }
}
