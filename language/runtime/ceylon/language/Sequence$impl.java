package ceylon.language;

import com.redhat.ceylon.compiler.java.metadata.Ignore;

@Ignore
public final class Sequence$impl<Element> {
    private final ceylon.language.Iterable$impl<Element> $ceylon$language$Iterable$this;
    
    private final Sequence<Element> $this;
    
    public Sequence$impl(Sequence<Element> $this) {
        this.$ceylon$language$Iterable$this = new ceylon.language.Iterable$impl<Element>($this);
        this.$this = $this;
    }

    public Element getLast(){
        return _getLast($this);
    }
    static <Element> Element _getLast(Sequence<Element> $this){
        Element x = $this.item(Integer.instance($this.getLastIndex().longValue()));
        if (x != null) {
            return x;
        }
        else {
            return $this.getFirst(); //actually never occurs
        } 
    }

    public Sequence<? extends Element> getSequence() {
        return _getSequence($this);
    }
    public static <Element> Sequence<? extends Element> _getSequence(Sequence<Element> $this) {
        return $this;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Sequence<? extends Element> sort(Callable<? extends Comparison> f) {
        return (Sequence)$ceylon$language$Iterable$this.sort(f).getSequence();
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <Element> Sequence<? extends Element> _sort(Sequence<? extends Element> $this, Callable<? extends Comparison> f) {
        return (new Sequence$impl($this)).sort(f).getSequence();
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <Result> Sequence<? extends Result> collect(Callable<? extends Result> f) {
        return (Sequence)$ceylon$language$Iterable$this.collect(f);
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <Element,Result> Sequence<? extends Result> _collect(Sequence<? extends Element> $this, Callable<? extends Result> f) {
        return (new Sequence$impl($this)).collect(f);
    }

}    