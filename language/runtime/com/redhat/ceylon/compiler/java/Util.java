package com.redhat.ceylon.compiler.java;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ceylon.language.Iterable;
import ceylon.language.Iterator;
import ceylon.language.exhausted_;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Class;
import com.redhat.ceylon.compiler.java.metadata.SatisfiedTypes;

/**
 * Helper class for generated Ceylon code that needs to call implementation logic.
 * 
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
public class Util {
    
    /**
     * Returns true if the given object satisfies ceylon.language.Identifiable
     */
    public static boolean isIdentifiable(java.lang.Object o){
        return satisfiesInterface(o, "ceylon.language.Identifiable");
    }
    
    /**
     * Returns true if the given object extends ceylon.language.IdentifiableObject
     */
    public static boolean isIdentifiableObject(java.lang.Object o){
        return extendsClass(o, "ceylon.language.IdentifiableObject");
    }
    
    /**
     * Returns true if the given object extends the given class
     */
    public static boolean extendsClass(java.lang.Object o, String className) {
        if(o == null)
            return false;
        if(className == null)
            throw new IllegalArgumentException("Type name cannot be null");
        return classExtendsClass(o.getClass(), className);
    }
    
    private static boolean classExtendsClass(java.lang.Class<?> klass, String className) {
        if(klass == null)
            return false;
        if (klass.getName().equals(className))
            return true;
        if ((className.equals("ceylon.language.IdentifiableObject"))
                && klass!=java.lang.Object.class
                && !(klass.getName().equals("ceylon.language.StringOfNone") || klass.getName().equals("ceylon.language.StringOfSome"))
                //&& klass!=java.lang.String.class
        		&& !klass.isAnnotationPresent(Class.class)
        		&& (!klass.isInterface() || !klass.isAnnotationPresent(Ceylon.class))) {
        	//TODO: this is broken for a Java class that
        	//      extends a Ceylon class
        	return true;
        }
        Class classAnnotation = klass.getAnnotation(Class.class);
        if (classAnnotation != null) {
            String superclassName = classAnnotation.extendsType();
            int i = superclassName.indexOf('<');
            if (i>0) {
                superclassName = superclassName.substring(0, i);
            }
            if (superclassName.isEmpty()) {
                return false;
            }
            try {
                return classExtendsClass(
                        java.lang.Class.forName(superclassName, true, klass.getClassLoader()), 
                        className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return classExtendsClass(klass.getSuperclass(), className);
    }
    
    /**
     * Returns true if the given object satisfies the given interface
     */
    public static boolean satisfiesInterface(java.lang.Object o, String className){
        if(o == null)
            return false;
        if(className == null)
            throw new IllegalArgumentException("Type name cannot be null");
        // we use a hash set to speed things up for interfaces, to avoid looking at them twice
        Set<java.lang.Class<?>> alreadyVisited = new HashSet<java.lang.Class<?>>();
        return classSatisfiesInterface(o.getClass(), className, alreadyVisited);
    }

    private static boolean classSatisfiesInterface(java.lang.Class<?> klass, String className, 
            Set<java.lang.Class<?>> alreadyVisited) {
        if(klass == null
                || klass == ceylon.language.Void.class)
            return false;
        if ((className.equals("ceylon.language.Identifiable"))
                && klass!=java.lang.Object.class
                //&& klass!=java.lang.String.class
                && !klass.isAnnotationPresent(Ceylon.class)) {
            //TODO: this is broken for a Java class that
            //      extends a Ceylon class
            return true;
        }
        // try the interfaces
        if(lookForInterface(klass, className, alreadyVisited))
            return true;
        // try its superclass
        Class classAnnotation = klass.getAnnotation(Class.class);
        String superclassName;
        if (classAnnotation!=null) {
            superclassName = classAnnotation.extendsType();
            int i = superclassName.indexOf('<');
            if (i>0) {
                superclassName = superclassName.substring(0, i);
            }
            
        } else {
            // Maybe the class didn't have an extends, so implictly IdentifiableObject
            superclassName = "ceylon.language.IdentifiableObject";
        }
        if (!superclassName.isEmpty()) {
            try {
                return classSatisfiesInterface(
                        java.lang.Class.forName(superclassName, true, klass.getClassLoader()), 
                        className, alreadyVisited);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return classSatisfiesInterface(klass.getSuperclass(), className, alreadyVisited);
    }

    private static boolean lookForInterface(java.lang.Class<?> klass, String className, 
            Set<java.lang.Class<?>> alreadyVisited){
        if (klass.getName().equals(className))
            return true;
        // did we already visit this type?
        if(!alreadyVisited.add(klass))
            return false;
        // first see if it satisfies it directly
        SatisfiedTypes satisfiesAnnotation = klass.getAnnotation(SatisfiedTypes.class);
        if (satisfiesAnnotation!=null){
            for (String satisfiedType : satisfiesAnnotation.value()){
                int i = satisfiedType.indexOf('<');
                if (i>0) {
                    satisfiedType = satisfiedType.substring(0, i);
                }
                try {
                    if (lookForInterface(
                            java.lang.Class.forName(satisfiedType, true, klass.getClassLoader()), 
                            className, alreadyVisited)) {
                        return true;
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // now look at this class's interfaces
        for (java.lang.Class<?> intrface : klass.getInterfaces()){
            if (lookForInterface(intrface, className, alreadyVisited))
                return true;
        }
        // no luck
        return false;
    }
    
    //
    // Java variadic conversions
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> collectIterable(Iterable<? extends T> sequence) {
        List<T> list = new LinkedList<T>();
        if (sequence != null) {
            Iterator<? extends T> iterator = sequence.getIterator();
            Object o; 
            while((o = iterator.next()) != exhausted_.getExhausted()){
                list.add((T)o);
            }
        }
        return list;
    }

    public static boolean[] toBooleanArray(ceylon.language.Iterable<? extends ceylon.language.Boolean> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toBooleanArray((ceylon.language.FixedSized<? extends ceylon.language.Boolean>)sequence);
        List<ceylon.language.Boolean> list = collectIterable(sequence);
        boolean[] ret = new boolean[list.size()];
        int i=0;
        for(ceylon.language.Boolean e : list){
            ret[i++] = e.booleanValue();
        }
        return ret;
    }
    
    private static boolean[] toBooleanArray(ceylon.language.FixedSized<? extends ceylon.language.Boolean> sequence){
        boolean[] ret = new boolean[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = sequence.getFirst().booleanValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Boolean>)sequence).getRest();
            else
                break;
        }
        return ret;
    }
    
    public static byte[] toByteArray(ceylon.language.Iterable<? extends ceylon.language.Integer> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toByteArray((ceylon.language.FixedSized<? extends ceylon.language.Integer>)sequence);
        List<ceylon.language.Integer> list = collectIterable(sequence);
        byte[] ret = new byte[list.size()];
        int i=0;
        for(ceylon.language.Integer e : list){
            ret[i++] = (byte)e.longValue();
        }
        return ret;
    }

    private  static byte[] toByteArray(ceylon.language.FixedSized<? extends ceylon.language.Integer> sequence){
        byte[] ret = new byte[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = (byte) sequence.getFirst().longValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Integer>)sequence).getRest();
            else
                break;
        }
        return ret;
    }
    
    public static short[] toShortArray(ceylon.language.Iterable<? extends ceylon.language.Integer> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toShortArray((ceylon.language.FixedSized<? extends ceylon.language.Integer>)sequence);
        List<ceylon.language.Integer> list = collectIterable(sequence);
        short[] ret = new short[list.size()];
        int i=0;
        for(ceylon.language.Integer e : list){
            ret[i++] = (short)e.longValue();
        }
        return ret;
    }

    private static short[] toShortArray(ceylon.language.FixedSized<? extends ceylon.language.Integer> sequence){
        short[] ret = new short[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = (short) sequence.getFirst().longValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Integer>)sequence).getRest();
            else
                break;
        }
        return ret;
    }
    
    public static int[] toIntArray(ceylon.language.Iterable<? extends ceylon.language.Integer> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toIntArray((ceylon.language.FixedSized<? extends ceylon.language.Integer>)sequence);
        List<ceylon.language.Integer> list = collectIterable(sequence);
        int[] ret = new int[list.size()];
        int i=0;
        for(ceylon.language.Integer e : list){
            ret[i++] = (int)e.longValue();
        }
        return ret;
    }

    private static int[] toIntArray(ceylon.language.FixedSized<? extends ceylon.language.Integer> sequence){
        int[] ret = new int[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = (int) sequence.getFirst().longValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Integer>)sequence).getRest();
            else
                break;
        }
        return ret;
    }
    
    public static long[] toLongArray(ceylon.language.Iterable<? extends ceylon.language.Integer> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toLongArray((ceylon.language.FixedSized<? extends ceylon.language.Integer>)sequence);
        List<ceylon.language.Integer> list = collectIterable(sequence);
        long[] ret = new long[list.size()];
        int i=0;
        for(ceylon.language.Integer e : list){
            ret[i++] = e.longValue();
        }
        return ret;
    }

    private static long[] toLongArray(ceylon.language.FixedSized<? extends ceylon.language.Integer> sequence){
        long[] ret = new long[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = sequence.getFirst().longValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Integer>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static float[] toFloatArray(ceylon.language.Iterable<? extends ceylon.language.Float> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toFloatArray((ceylon.language.FixedSized<? extends ceylon.language.Float>)sequence);
        List<ceylon.language.Float> list = collectIterable(sequence);
        float[] ret = new float[list.size()];
        int i=0;
        for(ceylon.language.Float e : list){
            ret[i++] = (float)e.doubleValue();
        }
        return ret;
    }

    private static float[] toFloatArray(ceylon.language.FixedSized<? extends ceylon.language.Float> sequence){
        float[] ret = new float[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = (float) sequence.getFirst().doubleValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Float>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static double[] toDoubleArray(ceylon.language.Iterable<? extends ceylon.language.Float> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toDoubleArray((ceylon.language.FixedSized<? extends ceylon.language.Float>)sequence);
        List<ceylon.language.Float> list = collectIterable(sequence);
        double[] ret = new double[list.size()];
        int i=0;
        for(ceylon.language.Float e : list){
            ret[i++] = e.doubleValue();
        }
        return ret;
    }

    private static double[] toDoubleArray(ceylon.language.FixedSized<? extends ceylon.language.Float> sequence){
        double[] ret = new double[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = sequence.getFirst().doubleValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Float>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static char[] toCharArray(ceylon.language.Iterable<? extends ceylon.language.Character> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toCharArray((ceylon.language.FixedSized<? extends ceylon.language.Character>)sequence);
        List<ceylon.language.Character> list = collectIterable(sequence);
        char[] ret = new char[list.size()];
        int i=0;
        // FIXME: this is invalid and should yield a larger array by splitting chars > 16 bits in two
        for(ceylon.language.Character e : list){
            ret[i++] = (char)e.intValue();
        }
        return ret;
    }

    private static char[] toCharArray(ceylon.language.FixedSized<? extends ceylon.language.Character> sequence){
        char[] ret = new char[(int) sequence.getSize()];
        int i=0;
        // FIXME: this is invalid and should yield a larger array by splitting chars > 16 bits in two
        while(!sequence.getEmpty()){
            ret[i++] = (char) sequence.getFirst().intValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Character>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static int[] toCodepointArray(ceylon.language.Iterable<? extends ceylon.language.Character> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toCodepointArray((ceylon.language.FixedSized<? extends ceylon.language.Character>)sequence);
        List<ceylon.language.Character> list = collectIterable(sequence);
        int[] ret = new int[list.size()];
        int i=0;
        for(ceylon.language.Character e : list){
            ret[i++] = e.intValue();
        }
        return ret;
    }

    private static int[] toCodepointArray(ceylon.language.FixedSized<? extends ceylon.language.Character> sequence){
        int[] ret = new int[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = (char) sequence.getFirst().intValue();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.Character>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static java.lang.String[] toJavaStringArray(ceylon.language.Iterable<? extends ceylon.language.String> sequence){
        if(sequence instanceof ceylon.language.FixedSized)
            return toJavaStringArray((ceylon.language.FixedSized<? extends ceylon.language.String>)sequence);
        List<ceylon.language.String> list = collectIterable(sequence);
        java.lang.String[] ret = new java.lang.String[list.size()];
        int i=0;
        for(ceylon.language.String e : list){
            ret[i++] = e.toString();
        }
        return ret;
    }

    private static java.lang.String[] toJavaStringArray(ceylon.language.FixedSized<? extends ceylon.language.String> sequence){
        java.lang.String[] ret = new java.lang.String[(int) sequence.getSize()];
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = sequence.getFirst().toString();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends ceylon.language.String>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static <T> T[] toArray(ceylon.language.FixedSized<? extends T> sequence,
            T[] ret){
        int i=0;
        while(!sequence.getEmpty()){
            ret[i++] = sequence.getFirst();
            if(sequence instanceof ceylon.language.Some<?>)
                sequence = ((ceylon.language.Some<? extends T>)sequence).getRest();
            else
                break;
        }
        return ret;
    }

    public static <T> T[] toArray(ceylon.language.Iterable<? extends T> iterable,
            java.lang.Class<T> klass){
        List<T> list = collectIterable(iterable);
        @SuppressWarnings("unchecked")
        T[] ret = (T[]) java.lang.reflect.Array.newInstance(klass, list.size());
        return list.toArray(ret);
    }
}
