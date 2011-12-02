doc "Abstraction of integral numeric types. That is, types 
     with no fractional part, including `Natural` and `Integer`. 
     The division operation for integral numeric types 
     results in a remainder. Therefore, integral numeric 
     types have an operation to determine the remainder of 
     any division operation."
see (Natural, Integer)
by "Gavin"
shared interface Integral<Other> of Other
        satisfies Numeric<Other> & Ordinal<Other>
        given Other satisfies Integral<Other> {

    doc "The remainder, after dividing this number by the 
         given number."
    see (divided)
    shared formal Other remainder(Other other);

    doc "Determine if the number is zero."
    shared formal Boolean zero;
    
    doc "Determine if the number is one."
    shared formal Boolean unit;
    
}