"A possibly-empty, immutable sequence of values. The type 
 `Sequential<Element>` may be abbreviated `[Element*]` or 
 `Element[]`. 
 
 `Sequential` has two enumerated subtypes:
 
 - [[Empty]], abbreviated `[]`, represents an empty sequence, 
   and
 - [[Sequence]]`<Element>`, abbreviated `[Element+]` represents 
   a non-empty sequence, and has the very important 
   subclass [[Tuple]]."
see (`class Tuple`)
shared interface Sequential<out Element>
        of []|[Element+]
        satisfies List<Element> & 
                  Ranged<Integer,Element,Element[]> {
    
    "This sequence."
    shared actual default Element[] sequence() => this;
    
    "The rest of the sequence, without the first element."
    shared actual formal Element[] rest;
    
    "A sequence containing all indexes of this sequence."
    shared actual default Integer[] keys => 0:size;
    
    shared actual formal Element[] reversed;
    
    shared actual formal Element[] reverse();
    
    "Returns a sequence formed by repeating the elements of 
     this sequence the given number of times, or an empty 
     sequence if `times<=0`."
    shared actual default Element[] repeat(Integer times)
            => cycle(times).sequence();
    
    "Select the first elements of this sequence, returning 
     a sequence no longer than the given length. If this 
     sequence is shorter than the given length, return this 
     sequence. Otherwise return a sequence of the given 
     length."
    shared actual default Element[] initial(Integer length)
            => this[0:length];
    
    "Select the last elements of the sequence, returning a 
     sequence no longer than the given length. If this 
     sequence is shorter than the given length, return this 
     sequence. Otherwise return a sequence of the given 
     length."
    shared actual default Element[] terminal(Integer length) {
        if (exists l = lastIndex, length>0) {
            return this[l-length+1..l];
        }
        else {
            return [];
        }
    }
    
    "Trim the elements satisfying the given predicate
     function from the start and end of this sequence, 
     returning a sequence no longer than this sequence."
    shared actual default Element[] trim(
        Boolean trimming(Element&Object elem))
            => super.trim(trimming).sequence(); //TODO: inefficient?
    
    "Trim the elements satisfying the given predicate
     function from the start of this sequence, returning 
     a sequence no longer than this sequence."
    shared actual default Element[] trimLeading(
        Boolean trimming(Element&Object elem))
            => super.trimLeading(trimming).sequence(); //TODO: inefficient?
    
    "Trim the elements satisfying the given predicate
     function from the end of this sequence, returning a 
     sequence no longer than this sequence."
    shared actual default Element[] trimTrailing(
        Boolean trimming(Element&Object elem))
            => super.trimTrailing(trimming).sequence(); //TODO: inefficient?
    
    "Return two sequences, the first containing the elements
     that occur before the given [[index]], the second with
     the elements that occur after the given `index`. If the
     given `index` is outside the range of indices of this
     list, one of the returned sequences will be empty."
    shared actual default [Element[],Element[]] slice(Integer index)
            => [this[...index-1], this[index...]];
        
    "This sequence."
    shared actual default Element[] clone() => this;
    
    "A string of form `\"[ x, y, z ]\"` where `x`, `y`, and 
     `z` are the `string` representations of the elements of 
     this collection, as produced by the iterator of the 
     collection, or the string `\"{}\"` if this collection 
     is empty. If the collection iterator produces the value 
     `null`, the string representation contains the string 
     `\"null\"`."
    shared actual default String string => 
            empty then "[]" else "[``commaList(this)``]";
    
}
