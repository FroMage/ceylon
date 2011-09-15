package com.redhat.ceylon.compiler.typechecker.model;

import static com.redhat.ceylon.compiler.typechecker.model.Util.list;
import static com.redhat.ceylon.compiler.typechecker.model.Util.contains;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a named, annotated program element:
 * a class, interface, type parameter, parameter,
 * method, local, or attribute.
 *
 * @author Gavin King
 */
public abstract class Declaration extends Element {

    String name;
    boolean shared;
    boolean formal;
    boolean actual;
    boolean def;
    List<Annotation> annotations = new ArrayList<Annotation>();
    Scope visibleScope;
    Declaration refinedDeclaration = this;

    public Scope getVisibleScope() {
        return visibleScope;
    }

    public void setVisibleScope(Scope visibleScope) {
        this.visibleScope = visibleScope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public boolean isParameterized() {
        return false;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "[" + getName() + "]";
    }

    @Override @Deprecated
    public List<String> getQualifiedName() {
        return list(getContainer().getQualifiedName(), getName());
    }

    @Override
    public String getQualifiedNameString() {
        return getContainer().getQualifiedNameString() + "." + getName();
    }

    public boolean isActual() {
        return actual;
    }

    public void setActual(boolean actual) {
        this.actual = actual;
    }

    public boolean isFormal() {
        return formal;
    }

    public void setFormal(boolean formal) {
        this.formal = formal;
    }

    public boolean isDefault() {
        return def;
    }

    public void setDefault(boolean def) {
        this.def = def;
    }
    
    public Declaration getRefinedDeclaration() {
		return refinedDeclaration;
	}
    
    public void setRefinedDeclaration(Declaration refinedDeclaration) {
		this.refinedDeclaration = refinedDeclaration;
	}

    /**
     * Determine if this declaration is visible
     * in the given scope, by considering if it
     * is shared or directly defined in a
     * containing scope.
     */
    public boolean isVisible(Scope scope) {
        if (getVisibleScope()==null) {
            return true;
        }
        else {
            return contains(getVisibleScope(), scope);
        }
        /*
        * Note that this implementation is not quite
        * right, since for a shared member
        * declaration it does not check if the
        * containing declaration is also visible in
        * the given scope, but this is okay for now
        * because of how this method is used.
        */
        /*if (isShared()) {
            return true;
        }
        else {
            return isDefinedInScope(scope);
        }*/
    }

    /**
     * Determine if this declaration is directly
     * defined in a containing scope of the given
     * scope.
     */
    public boolean isDefinedInScope(Scope scope) {
        while (scope!=null) {
            if (getContainer()==scope) {
                return true;
            }
            scope = scope.getContainer();
        }
        return false;
    }

    public boolean isCaptured() {
        return false;
    }

    public boolean isToplevel() {
        return getContainer() instanceof Package;
    }

    public boolean isClassMember() {
        return getContainer() instanceof Class;
    }

    public boolean isInterfaceMember() {
        return getContainer() instanceof Interface;
    }

    public boolean isClassOrInterfaceMember() {
        return getContainer() instanceof ClassOrInterface;
    }
    
    public boolean isMember() {
    	return false;
    }

    /**
     * Get a produced reference for this declaration
     * by binding explicit or inferred type arguments
     * and type arguments of the type of which this
     * declaration is a member, in the case that this
     * is a member.
     *
     * @param outerType the qualifying produced
     * type or null if this is not a
     * nested type declaration
     * @param typeArguments arguments to the type
     * parameters of this declaration
     */
    public abstract ProducedReference getProducedReference(ProducedType pt,
            List<ProducedType> typeArguments);

    @Override
    public boolean equals(Object object) {
        if (object instanceof Declaration) {
            Declaration that = (Declaration) object;
            return this==that || getName()!=null && that.getName()!=null &&
                    that.getName().equals(getName()) &&
                    that.getClass().equals(getClass()) &&
                    (getContainer()==null && that.getContainer()==null ||
                            that.getContainer().equals(getContainer())); 
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return getName()==null ? 0 : getName().hashCode();
    }
    
    /**
     * Does this declaration refine the given declaration?
     */
    public boolean refines(Declaration other) {
        if (equals(other)) {
            return true;
        }
        else {
            if (isClassOrInterfaceMember()) {
                ClassOrInterface type = (ClassOrInterface) getContainer();
                return other.getName()!=null && getName()!=null &&
                        other.getName().equals(getName()) && 
                        type.isMember(other);
            }
            else {
                return false;
            }
        }
    }
    
}
