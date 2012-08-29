interface SpreadTest {
    shared formal String x();
}
class Spread1() satisfies SpreadTest {
    shared actual String x() { return "S1"; }
}
class Spread2() satisfies SpreadTest {
    shared actual String x() { return "S2"; }
}

void operators() {
    String? maybe = "hello";
    String? maybeNot = null;
    check(exists maybe?.uppercased, "?.");
    check(!exists maybeNot?.uppercased, "?.");
    check(maybe?"goodbye"=="hello", "?");
    check(maybeNot?"goodbye"=="goodbye", "?");
    check(exists maybe?[0], "?[]");
    check(exists maybe?[4], "?[]");
    check(!exists maybe?[10], "?[]");
    check(!exists maybeNot?[0], "?[]");
    check(!exists maybeNot?[10], "?[]");
    check(!nonempty maybeNot, "nonempty null");

    String[] empty = {};
    String[] full = { "hello", "world" };
    check(!nonempty empty[].uppercased, "spread 1");
    check(nonempty full[].uppercased, "spread 2");
    value spread1 = full[].uppercased;
    value spread2 = full[].item(1);
	if (exists s1s=spread1[0]) {
        check(s1s == "HELLO", "spread 3");
    } else { fail("spread 3"); }
    if (exists s1s=spread1[1]) {
        check(s1s == "WORLD", "spread 4");
    } else { fail("spread 4"); }
    check(spread1.size == 2, "spread 5");
    check(spread2.size == 2, "spread 6");
    if (exists s2s=spread2[0]) {
        check(s2s == `e`, "spread 7");
    } else { fail("spread 7"); }
    if (exists s2s=spread2[1]) {
        check(s2s == `o`, "spread 8");
    } else { fail("spread 8"); }
    /*
    Character?[] spread3(Integer x) = full[].item;
    //Callable<Character?[], Integer> spread3 = full[].item;
    value spread4 = spread3(1);
    check(spread4.size == 2, "spread 10");
    if (exists s4s=spread4[0]) {
        check(s4s == `e`, "spread 11");
    } else { fail("spread 11"); }
    if (exists s4s=spread4[1]) {
        check(s4s == `o`, "spread 12");
    } else { fail("spread 12"); }
    */
    value spreadList = { Spread1(), Spread2() };
    value spread13 = spreadList[].x();
    check(spread13.size == 2, "spread 13 size");
    check(is String spread13[0], "spread 13 item 0");
    if (is String s13_1 = spread13[1]) {
        check(s13_1 == "S2", "spread 13 item 1");
    } else { fail("spread 13 item 1"); }
    /*
    function spread14() = spreadList[].x;
    check(spread14().size == 2, "spread 14 size");
    check(is String spread14()[0], "spread 14 item 0");
    if (is String s14_1 = spread14()[1]) {
        check(s14_1 == "S2", "spread 14 item 1");
    } else { fail("spread 14 item 1");}
    */

    check("hello" in "hello world", "in 1");
    check("world" in "hello world", "in 2");

    Correspondence<Integer, String> c1 = {};
    check(!exists c1[0], "empty correspondence");
    
    Ranged<Integer,String[]> sequence = {"foo", "bar"};
    String[] subrange = sequence[1..2];
    check(subrange.size==1, "subrange size");
    check(nonempty subrange, "subrange nonempty");
    check(sequence[1...].size==1, "open subrange size 1");
    check(sequence[0...].size==2, "open subrange size 2");
    check(nonempty sequence[1...], "open subrange nonempty");
    check(!nonempty sequence[2...], "open subrange empty");
                                
    Float x = 0.5;
    check(exists (x>0.0 then x), "then not null");
    check(!exists (x<0.0 then x), "then null");
    check((x<0.0 then x else 1.0) == 1.0, "then else");
    check((x>0.0 then x else 1.0) == 0.5, "then");
    
    check((maybe else "goodbye")=="hello", "else");
    check((maybeNot else "goodbye")=="goodbye", "else");
    
    class X() {}
    X? xx = X();
    Object? obj(Object? x) { return x; }
    check(is X obj(xx else X()), "something");
    check(is X obj(true then X()), "something");
    check(is X obj(true then X() else X()), "something");

}
