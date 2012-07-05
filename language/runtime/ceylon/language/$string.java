package ceylon.language;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Method;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ceylon(major = 2)
@Method
public final class $string {
    
    private $string() {}
    
    @TypeInfo("ceylon.language.String")
    public static java.lang.String string(@Name("characters") @Sequenced 
    @TypeInfo("ceylon.language.Iterable<ceylon.language.Character>")
    final Iterable<? extends Character> characters) {
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        java.lang.Object $tmp;
        for (Iterator<? extends Character> $val$iter$0 = characters.getIterator(); 
                !(($tmp = $val$iter$0.next()) instanceof Finished);) {
            sb.append($tmp);
        }
        return sb.toString();
    }
    @Ignore
    public static java.lang.String string() {
        return "";
    }
}
