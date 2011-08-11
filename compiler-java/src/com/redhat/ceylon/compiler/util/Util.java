package com.redhat.ceylon.compiler.util;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Getter;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.sun.tools.javac.parser.Token;

public class Util {
    public static boolean isErasedAttribute(String name){
        // ERASURE
        return "hash".equals(name) || "string".equals(name);
    }
    
    public static String quoteMethodName(String name){
        // ERASURE
        if ("hash".equals(name)) {
            return "hashCode";
        } else if ("string".equals(name)) {
            return "toString";
        } else if ("hashCode".equals(name)) {
            return "$hashCode";
        } else if ("toString".equals(name)) {
            return "$toString";
        } else {
            return quoteIfJavaKeyword(name);
        }
    }
    
    public static String quoteIfJavaKeyword(String name){
        if(isJavaKeyword(name))
            return "$"+name;
        return name;
    }
    
    private static boolean isJavaKeyword(String name) {
        try{
            Token token = Token.valueOf(name.toUpperCase());
            return token != null && token.name != null && token.name.equals(name);
        }catch(IllegalArgumentException x){
            return false;
        }
    }

    public static String strip(String str){
        return (str.charAt(0) == '$') ? str.substring(1) : str;
    }

    public static String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String getGetterName(String property){
        // ERASURE
        if ("hash".equals(property)) {
            // FIXME This is NOT the way to handle this, we should check that we're
            // actually trying to override the hash attribute defined in Equality
            return "hashCode";
        } else if ("string".equals(property)) {
            return "toString";
        }
        
        return "get"+capitalize(strip(property));
    }

    public static String getSetterName(String property){
        return "set"+capitalize(strip(property));
    }
    
    public static String getAttributeName(String getterName) {
        return Character.toLowerCase(getterName.charAt(3)) + getterName.substring(4);
    }

    public static String getConcreteMemberInterfaceImplementationName(String name){
        return name + "$impl";
    }
    
    // FIXME: add this to Declaration?
    public static boolean isClassAttribute(Declaration decl) {
        return (decl.getContainer() instanceof com.redhat.ceylon.compiler.typechecker.model.Class)
        	 && (decl.isCaptured() || decl.isShared());
    }

    // FIXME: add this to Declaration?
	public static boolean isInnerMethod(Declaration decl) {
        return decl.getContainer() instanceof com.redhat.ceylon.compiler.typechecker.model.Method;
	}
	
	public static String getQualifiedPrefixedName(Declaration decl){
	    String name = decl.getQualifiedNameString();
	    String prefix;
	    if(decl instanceof ClassOrInterface)
	        prefix = "C";
	    else if(decl instanceof Value)
	        prefix = "V";
        else if(decl instanceof Getter)
            prefix = "G";
        else if(decl instanceof Setter)
            prefix = "S";
        else if(decl instanceof Method)
            prefix = "M";
        else
            throw new RuntimeException("Don't know how to prefix decl: "+decl);
	    return prefix + name;
	}

    public static String getSimpleName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }
}
