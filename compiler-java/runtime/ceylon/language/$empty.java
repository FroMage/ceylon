package ceylon.language;

import com.redhat.ceylon.compiler.metadata.java.Attribute;
import com.redhat.ceylon.compiler.metadata.java.Ceylon;

@Ceylon @Attribute
public class $empty {
    private final static Empty value = new Empty(){

        @Override
        public boolean defines(Natural key) {
            return false;
        }

        @Override
        public Category getKeys() {
            throw new RuntimeException("Not yet implemented");
        }

        @Override
        public boolean definesEvery(Natural... keys) {
            return false;
        }

        @Override
        public boolean definesAny(Natural... keys) {
            return false;
        }

        @Override
        public Nothing[] values(Natural... keys) {
            return null;
        }

        @Override
        public Iterator<Nothing> iterator() {
            return new Iterator<Nothing>() {

                @Override
                public Nothing getHead() {
                    return $nothing.getNothing();
                }

                @Override
                public Iterator<Nothing> getTail() {
                    return this;
                }
                
            };
        }

        @Override
        public Natural getSize() {
            return Natural.instance(0);
        }

        @Override
        public boolean getEmpty() {
            return false;
        }

        @Override
        public Nothing value(Natural key) {
            return $nothing.getNothing();
        }

        @Override
        public Nothing getFirst() {
            return $nothing.getNothing();
        }
    };
    
    public static Empty getEmpty(){
        return value;
    }
}
