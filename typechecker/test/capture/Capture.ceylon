variable Natural n:=0;

class Capture() {
    
    n:=12;
    
    void use(Object o) {}
    
    class X() {}
    
    void methodWithVariable() {
        variable X x := X();
        void innerMethod() {
            @error use(x);
        }
        X innerGetter {
            @error return x;
        }
        assign innerGetter {
            @error x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                @error x:=X();
            }
        }
    }
    
    void methodWithConstant() {
        X x = X();
        void innerMethod() {
            use(x);
        }
        X innerGetter {
            return x;
        }
        assign innerGetter {
            @error x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                use(x);
            }
        }
    }
    
    void methodWithParameter(X x) {
        void innerMethod() {
            use(x);
        }
        X innerGetter {
            return x;
        }
        assign innerGetter {
            @error x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                use(x);
            }
        }
    }
    
    X getterWithVariable {
        variable X x := X();
        void innerMethod() {
            @error use(x);
        }
        X innerGetter {
            @error return x;
        }
        assign innerGetter {
            @error x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                @error x:=X();
            }
        }
        return x;
    }
    
    class ClassWithVariable() {
        @captured variable X x := X();
        @uncaptured variable X y := X();
        @captured shared variable X z := X();
        void innerMethod() {
            use(x);
        }
        X innerGetter {
            return x;
        }
        assign innerGetter {
            x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                x:=X();
            }
        }
    }
    
    class ClassWithParameter(@captured X x) {
        void innerMethod() {
            use(x);
        }
        X innerGetter {
            return x;
        }
        assign innerGetter {
            @error x:=innerGetter;
        }
        void containingMethod() {
            void nestedMethod() {
                use(x);
            }
        }
    }
    
    class WithMultipleParameters(@captured String w,
                                 @captured String x, 
                                 @uncaptured String y, 
                                 @uncaptured String z) {
        shared String yy = y;
        use(z);
        shared void xx() {
            if (true) {
                use(x);
            }
        }
        shared class Inner() {
            use(w);
        }
    }
    
    class UnsharedDuped(@uncaptured String s) {
        @uncaptured String s = s;
    }
    
    class SharedDuped(@uncaptured String s) {
        @captured shared String s = s;
    }
    
    class UnsharedDuped2(@captured String s) {
        String s { return s; }
    }
    
    class SharedDuped2(@captured String s) {
        shared String s { return s; }
    }
    
    class WithMultipleAttributes() {
        @uncaptured String x = "X";
        @captured String y = "Y";
        @captured String w = "W";
        @captured String z = "Z";
        use(x);
        void inner() {
            use(y);
            use(this.w);
        }
        class Inner() {
            use(outer.z);
        }
    }
    
    class WithNamedArgument() {
        @uncaptured String hello = "hello";
        use { o=hello; };
    }
    
    class WithNamedArgumentInMethod() {
        @captured String hello = "hello";
        void method() {
            use { o=hello; };
        }
    }
    
    class WithNamedArgumentInClass() {
        @captured String hello = "hello";
        class Class() {
            use { o=hello; };
        }
    }
    
    class WithAttributeArgument() {
        @captured String hello = "hello";
        use {
            value o {
                return hello;
            }
        };
    }
    
    class WithObjectArgument() {
        @captured String hello = "hello";
        use {
            object o {
                writeLine(hello);
            }
        };
    }
    
    class WithQualifiedRef() {
        @captured String hello = "hello";
        @captured value q = WithQualifiedRef();
        function f() { 
            return q.hello;
        }
    }
    
}