doc "A comparator which orders elements in decreasing order 
     according to the `Comparable` returned by the given 
     `comparable()` function."
see byIncreasing
shared Comparison? byDecreasing<Element,Value>(Value? comparable(Element e))
            (Element x, Element y)
        given Value satisfies Comparable<Value> {
    if (exists cx = comparable(x)) {
        if (exists cy = comparable(y)) {
            return cy<=>cx;
        }
    }
    return null;
}