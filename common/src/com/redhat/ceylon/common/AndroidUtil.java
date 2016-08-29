package com.redhat.ceylon.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class AndroidUtil {

    private static Boolean isRunningAndroid;
    
    public static boolean isRunningAndroid(){
        if(isRunningAndroid != null)
            return isRunningAndroid;
        Object app;
        try {
            app = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null);
            isRunningAndroid = app != null;
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException
                | SecurityException | ClassNotFoundException e) {
            isRunningAndroid = false;
        }
        return isRunningAndroid;
    }
    
    public static List<String> getDexEntries() {
        try {
            Object app = Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null);
            Object codePath = Class.forName("android.app.Application").getMethod("getPackageCodePath").invoke(app);
            Class<?> dexFileClass = Class.forName("dalvik.system.DexFile");
            Object dexFile = dexFileClass.getConstructor(String.class).newInstance(codePath);
            Enumeration<String> entries = (Enumeration<String>) dexFileClass.getMethod("entries").invoke(dexFile);
            List<String> dexEntries = new LinkedList<>();
            while(entries.hasMoreElements()){
                String entry = entries.nextElement();
                entry = entry.replace('.', '/')+".class";
                dexEntries.add(entry);
            }
            dexFileClass.getMethod("close").invoke(dexFile);
            return dexEntries;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
    
}
