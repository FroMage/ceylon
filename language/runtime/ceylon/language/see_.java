package ceylon.language;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Method;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ceylon(major = 3)
@Method
public class see_
{
    public static Nothing see(@Name("programElements") @Sequenced
            @TypeInfo("ceylon.language.Iterable<ceylon.language.Void>")
            final ceylon.language.Iterable<? extends java.lang.Object> value) {
        return null;
    }
    @Ignore
    public static Nothing see() {
        return null;
    }
    private see_(){}
}
