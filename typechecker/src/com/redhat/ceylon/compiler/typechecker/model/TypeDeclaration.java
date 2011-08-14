package com.redhat.ceylon.compiler.typechecker.model;

import static com.redhat.ceylon.compiler.typechecker.model.Util.arguments;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isResolvable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class TypeDeclaration extends Declaration implements Scope, Generic {

    private ProducedType extendedType;
    private List<ProducedType> satisfiedTypes = new ArrayList<ProducedType>();
    private List<ProducedType> caseTypes = null;
    private List<TypeParameter> typeParameters = Collections.emptyList();
    private ProducedType selfType;

    @Override
    public boolean isParameterized() {
        return !typeParameters.isEmpty();
    }

    public List<TypeParameter> getTypeParameters() {
        return typeParameters;
    }

    public void setTypeParameters(List<TypeParameter> typeParameters) {
        this.typeParameters = typeParameters;
    }

    public Class getExtendedTypeDeclaration() {
        if (getExtendedType()==null) {
            return null;
        }
        else {
            return (Class) getExtendedType().getDeclaration();
        }
    }

    public ProducedType getExtendedType() {
        return extendedType;
    }

    public void setExtendedType(ProducedType extendedType) {
        this.extendedType = extendedType;
    }

    public List<TypeDeclaration> getSatisfiedTypeDeclarations() {
        List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
        for (ProducedType pt: getSatisfiedTypes()) {
            list.add(pt.getDeclaration());
        }
        return list;
    }

    public List<ProducedType> getSatisfiedTypes() {
        return satisfiedTypes;
    }

    public void setSatisfiedTypes(List<ProducedType> satisfiedTypes) {
        this.satisfiedTypes = satisfiedTypes;
    }

    public List<TypeDeclaration> getCaseTypeDeclarations() {
        List<TypeDeclaration> list = new ArrayList<TypeDeclaration>();
        for (ProducedType pt: getCaseTypes()) {
            list.add(pt.getDeclaration());
        }
        return list;
    }

    public List<ProducedType> getCaseTypes() {
        return caseTypes;
    }

    public void setCaseTypes(List<ProducedType> caseTypes) {
        this.caseTypes = caseTypes;
    }

    @Override
    public ProducedReference getProducedReference(ProducedType pt,
            List<ProducedType> typeArguments) {
        return getProducedType(pt, typeArguments);
    }

    /**
     * Get a produced type for this declaration by
     * binding explicit or inferred type arguments
     * and type arguments of the type of which this
     * declaration is a member, in the case that this
     * is a nested type.
     *
     * @param outerType the qualifying produced
     * type or null if this is not a
     * nested type declaration
     * @param typeArguments arguments to the type
     * parameters of this declaration
     */
    public ProducedType getProducedType(ProducedType outerType,
            List<ProducedType> typeArguments) {
        /*if (!acceptsArguments(this, typeArguments)) {
              return null;
          }*/
        ProducedType pt = new ProducedType();
        pt.setDeclaration(this);
        pt.setDeclaringType(outerType);
        pt.setTypeArguments(arguments(this, outerType, typeArguments));
        return pt;
    }

    /**
     * The type of the declaration as seen from
     * within the body of the declaration itself.
     * <p/>
     * Note that for certain special types which
     * we happen to know don't have type arguments,
     * we use this as a convenience method to
     * quickly get a produced type for use outside
     * the body of the declaration, but this is not
     * really correct!
     */
    public ProducedType getType() {
        ProducedType pt = new ProducedType();
        if (isMember()) {
            pt.setDeclaringType(((ClassOrInterface) getContainer()).getType());
        }
        pt.setDeclaration(this);
        //each type parameter is its own argument
        Map<TypeParameter, ProducedType> map = new HashMap<TypeParameter, ProducedType>();
        for (TypeParameter p: getTypeParameters()) {
            ProducedType pta = new ProducedType();
            pta.setDeclaration(p);
            map.put(p, pta);
        }
        pt.setTypeArguments(map);
        return pt;
    }

    private List<Declaration> getMembers(String name, List<TypeDeclaration> visited) {
        if (visited.contains(this)) {
            return Collections.emptyList();
        }
        else {
            visited.add(this);
            List<Declaration> members = new ArrayList<Declaration>();
            for (Declaration d: getMembers()) {
                if (d.getName()!=null && d.getName().equals(name)) {
                    members.add(d);
                }
            }
            if (members.isEmpty()) {
                members.addAll(getInheritedMembers(name));
            }
            return members;
        }
    }
    
    public List<Declaration> getInheritedMembers(String name) {
        return getInheritedMembers(name, new ArrayList<TypeDeclaration>());
    }
    
    private List<Declaration> getInheritedMembers(String name, List<TypeDeclaration> visited) {
        List<Declaration> members = new ArrayList<Declaration>();
        for (TypeDeclaration t: getSatisfiedTypeDeclarations()) {
            //if ( !(t instanceof TypeParameter) ) { //don't look for members in a type parameter with a self-referential lower bound
                for (Declaration d: t.getMembers(name, visited)) {
                    if (d.isShared() && isResolvable(d)) {
                        members.add(d);
                    }
                }
            //}
        }
        ProducedType et = getExtendedType();
        if (et!=null) {
            for (Declaration d: et.getDeclaration().getMembers(name, visited)) {
                if (d.isShared() && isResolvable(d)) {
                    members.add(d);
                }
            }
        }
        return members;
    }
    
    public Declaration getRefinedDeclaration(String name) {
    	Declaration ed = getExtendedTypeDeclaration().getDirectMember(name);
    	if (ed!=null) {
    		return ed;
    	}
		for (TypeDeclaration td: getSatisfiedTypeDeclarations()) {
			Declaration sd = td.getDirectMember(name);
			if (sd!=null) {
				return sd;
			}
		}
    	return getDirectMember(name);
    }

    @Override
    public Declaration getMember(String name) {
        //first search for the member in the local
        //scope, including non-shared declarations
        Declaration d = getDirectMember(name);
        if (d!=null && d.isShared()) {
            //if it's shared, it's what we're 
            //looking for, return it
            return d;
        }
        else {
            //now look for inherited shared declarations
            Declaration s = getSupertypeDeclaration(name);
            if (s!=null) {
                return s;
            }
        }
        //finally return the non-shared member we
        //found earlier, so that the caller give a
        //nice error message
        return d;
    }
    
    @Override
    public Declaration getMemberOrParameter(String name) {
        //first search for the member or parameter 
        //in the local scope, including non-shared 
        //declarations
        Declaration d = getDirectMemberOrParameter(name);
        if (d!=null) {
            return d;
        }
        else {
            //now look for inherited shared declarations
            return getSupertypeDeclaration(name);
        }
    }

    @Override
    public boolean isInherited(Declaration d) {
        if (d.getContainer()==this) {
            return false;
        }
        else if (isInheritedFromSupertype(d)) {
            return true;
        }
        else if (getContainer()!=null) {
            return getContainer().isInherited(d);
        }
        else {
            return false;
        }
    }

    @Override
    public TypeDeclaration getInheritingDeclaration(Declaration d) {
        if (d.getContainer()==this) {
            return null;
        }
        else if (isInheritedFromSupertype(d)) {
            return this;
        }
        else if (getContainer()!=null) {
            return getContainer().getInheritingDeclaration(d);
        }
        else {
            return null;
        }
    }

    private boolean isInheritedFromSupertype(final Declaration member) {
        class Criteria implements ProducedType.Criteria {
            @Override
            public boolean satisfies(TypeDeclaration type) {
                return type!=TypeDeclaration.this &&
                    type.getDirectMember(member.getName())==member;
            }
        };
        return getType().getSupertype(new Criteria())!=null;
    }

    private Declaration getSupertypeDeclaration(final String name) {
        class Criteria implements ProducedType.Criteria {
            @Override
            public boolean satisfies(TypeDeclaration type) {
                Declaration d = type.getDirectMember(name);
                if (d!=null && d.isShared()) {
                    return true;
                }
                else {
                    return false;
                }
            }
        };
        ProducedType st = getType().getSupertype(new Criteria());
        if (st!=null) {
            return st.getDeclaration().getDirectMember(name);
        }
        else {
            return null;
        }
    }

    public boolean isAlias() {
        return false;
    }

    public void setSelfType(ProducedType selfType) {
        this.selfType = selfType;
    }

    public ProducedType getSelfType() {
        return selfType;
    }
    
    @Override
    public Map<String, Declaration> getMatchingDeclarations(Unit unit, String startingWith) {
    	Map<String, Declaration> result = getMatchingMemberDeclarations(startingWith);
    	result.putAll(super.getMatchingDeclarations(unit, startingWith));
    	return result;
    }

	public Map<String, Declaration> getMatchingMemberDeclarations(String startingWith) {
		Map<String, Declaration> result = new TreeMap<String, Declaration>();
        TypeDeclaration et = getExtendedTypeDeclaration();
    	if (et!=null) {
    		result.putAll(et.getMatchingMemberDeclarations(startingWith));
    	}
    	for (TypeDeclaration st: getSatisfiedTypeDeclarations()) {
    		result.putAll(st.getMatchingMemberDeclarations(startingWith));
    	}
        for (Declaration d: getMembers()) {
            if (isResolvable(d) && d.isShared() && 
            		isNameMatching(startingWith, d)) {
                result.put(d.getName(), d);
            }
        }
    	//TODO: self type?
    	return result;
	}

}
