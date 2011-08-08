package com.redhat.ceylon.compiler.typechecker.model;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isNamed;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isResolvable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Element {

    Scope container;
    Unit unit;
    List<Declaration> members = new ArrayList<Declaration>();

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit compilationUnit) {
        this.unit = compilationUnit;
    }

    public Scope getContainer() {
        return container;
    }

    public void setContainer(Scope scope) {
        this.container = scope;
    }

    public List<Declaration> getMembers() {
        return members;
    }

    public String getQualifiedNameString() {
        return getContainer().getQualifiedNameString();
    }

    @Deprecated
    public List<String> getQualifiedName() {
        return getContainer().getQualifiedName();
    }

    /**
     * Search only directly inside the given scope,
     * without considering containing scopes or
     * imports.
     */
    protected Declaration getMemberOrParameter(String name) {
        return getDirectMemberOrParameter(name);
    }

    public Declaration getDirectMemberOrParameter(String name) {
        for (Declaration d: getMembers()) {
            if (isResolvable(d) && isNamed(name, d)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Search only directly inside the given scope,
     * without considering containing scopes or
     * imports, and ignoring parameters.
     */
    public Declaration getMember(String name) {
        return getDirectMember(name);
    }

    public Declaration getDirectMember(String name) {
        for (Declaration d: getMembers()) {
            if (isResolvable(d)
                    //&& d.isShared()
                    && !isParameter(d)
                    && isNamed(name, d)) { //don't return parameters
                return d;
            }
        }
        return null;
    }

    protected boolean isParameter(Declaration d) {
        return d instanceof Parameter
                || d instanceof TypeParameter;
    }

    public ProducedType getDeclaringType(Declaration d) {
        return getContainer().getDeclaringType(d);
    }

    /**
     * Search in the given scope, taking into account
     * containing scopes and imports
     */
    public Declaration getMemberOrParameter(Unit unit, String name) {
        Declaration d = getMemberOrParameter(name);
        if (d!=null) {
            return d;
        }
        else if (getContainer()!=null) {
            return getContainer().getMemberOrParameter(unit, name);
        }
        else {
            //union type or bottom type 
            return null;
        }
    }
    
    public boolean isInherited(Declaration d) {
        if (d.getContainer()==this) {
            return false;
        }
        else if (getContainer()!=null) {
            return getContainer().isInherited(d);
        }
        else {
            return false;
        }
    }
    
    public TypeDeclaration getInheritingDeclaration(Declaration d) {
        if (d.getContainer()==this) {
            return null;
        }
        else if (getContainer()!=null) {
            return getContainer().getInheritingDeclaration(d);
        }
        else {
            return null;
        }
    }
    
    public Map<String, Declaration> getMatchingDeclarations(Unit unit, String startingWith) {
    	Map<String, Declaration> result = getContainer()
    			.getMatchingDeclarations(unit, startingWith);
        for (Declaration d: getMembers()) {
            if (isResolvable(d) && isNameMatching(startingWith, d)) {
                result.put(d.getName(), d);
            }
        }
    	return result;
    }

}
