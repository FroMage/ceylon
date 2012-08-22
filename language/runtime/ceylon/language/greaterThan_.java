package ceylon.language;

import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Method;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;

@Ceylon(major = 3)
@Method
public class greaterThan_ {
    private greaterThan_(){}

    @TypeParameters(@TypeParameter(value="Element", satisfies="ceylon.language.Comparable<Element>"))
    @TypeInfo("ceylon.language.Callable<ceylon.language.Boolean,Element>")
    public static <Element extends Comparable<? super Element>> Callable<? extends Boolean> greaterThan(
            @Name("val") @TypeInfo("Element")
            final Element val) {
        return new AbstractCallable<Boolean>("greaterThan"){
            public Boolean $call(java.lang.Object element) {
                return Boolean.instance(((Element)element).compare(val) == larger_.getLarger());
            }
        };
    }

}
