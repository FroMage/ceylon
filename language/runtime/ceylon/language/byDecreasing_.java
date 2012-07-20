package ceylon.language;

import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Method;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;

@Ceylon(major = 2)
@Method
public class byDecreasing_ {

    private byDecreasing_(){}

    @TypeParameters({@TypeParameter(value="Element"),
            @TypeParameter(value="Value", satisfies="ceylon.language.Comparable<Value>")})
    @TypeInfo("ceylon.language.Callable<ceylon.language.Nothing|ceylon.language.Comparison,Element,Element>")
    public static <Element,Value extends Comparable<? super Value>> Callable<? extends Comparison> byDecreasing(
            @Name("comparable")
            @TypeInfo("ceylon.language.Callable<ceylon.language.Nothing|Value,Element>")
            final Callable<? extends Value> comparable) {
        return new AbstractCallable<Comparison>("byDecreasing") {
            public Comparison $call(java.lang.Object x, java.lang.Object y) {
                Value cx = comparable.$call(x);
                Value cy = comparable.$call(y);
                if (cx!=null && cy!=null) {
                    return cy.compare(cx);
                }
                else {
                    return null;
                }
            }
            
        };
    }
}
