class ExamplesStrings {
  ILoString list1 = makeList("a", "b", "c");
  ILoString list2 = makeList("x", "y", "z");

  boolean testInterleaveAndMerge(Tester t) {
    return t.checkExpect(list1.interleave(list2), makeList("a", "x", "b", "y", "c", "z"))
        && t.checkExpect(list1.merge(list2), makeList("a", "b", "c", "x", "y", "z"));
  }
}