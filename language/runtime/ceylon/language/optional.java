package ceylon.language;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Method;

@Ceylon(major = 2)
@Method
public final class optional {
    public static Nothing optional(){
        return null;
    }
    private optional(){}
}
