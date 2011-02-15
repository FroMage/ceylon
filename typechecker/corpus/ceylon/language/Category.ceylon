shared interface Category {
    
    doc "Determine if the given object belongs to the category.
         Return |true| iff the given object belongs to the 
         category."
    shared formal Boolean contains(Object obj);

    doc "Determine if the given objects belong to the category.
         Return |true| iff all the given objects belong to the 
         category."
    shared default Boolean contains(Object... objects) {
        return forAll(Object obj in objects) every (contains(obj));
    }

}