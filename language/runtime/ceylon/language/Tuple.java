package ceylon.language;

import com.redhat.ceylon.compiler.java.language.ArraySequence;
import com.redhat.ceylon.compiler.java.metadata.Annotation;
import com.redhat.ceylon.compiler.java.metadata.Annotations;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Class;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.SatisfiedTypes;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;
import com.redhat.ceylon.compiler.java.metadata.Variance;

@Ceylon(major = 3)
@TypeParameters({
    @TypeParameter(value = "Element", variance = Variance.OUT),
    @TypeParameter(value = "First", variance = Variance.OUT),
    @TypeParameter(value = "Rest", variance = Variance.OUT)
 })
@Class(extendsType="ceylon.language::Object")
@SatisfiedTypes("ceylon.language::Sequence<Element>")
public class Tuple<Element, First, Rest> 
        implements Sequence<Element> {

    private final Iterable$impl<Element> iterable$impl = new Iterable$impl<Element>(this);
    private final Sequence$impl<Element> sequence$impl = new Sequence$impl<Element>(this);
    private final List$impl<Element> list$impl = new List$impl<Element>(this);
    private final Correspondence$impl<Integer,Element> correspondence$impl = new Correspondence$impl<Integer,Element>(this);
    private final Category$impl category$impl = new Category$impl(this);
    private final Collection$impl<Element> collection$impl = new Collection$impl<Element>(this);
	private final Element first;
	private final List<Element> rest;
	
	public Tuple(@TypeInfo("First&Element") 
	             @Name("first") Element first,
	             @TypeInfo("Rest&Empty|Rest&Sequence<Element>")
			     @Name("rest") List<Element> rest) {
		this.first = first;
		this.rest = rest;
	}
	
	@TypeInfo("First&Element")
	public Element getFirst() {
		return first;
	}
	
	@TypeInfo("Rest&Empty|Rest&Sequence<Element>")
	public List<Element> getRest() {
		return rest;
	}

	@Override
	@Annotations(@Annotation("actual"))
	public long getSize() {
		return list$impl.getSize();
	}

	@Override
	@Annotations(@Annotation("actual"))
	public boolean defines(Integer key) {
		return correspondence$impl.defines(key);
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeInfo("ceylon.language::Nothing|Element")
	public Element item(@Name("index") Integer key) {
	    final long idx = key.value;
	    if (idx > 0) {
            return rest.item(key.getPredecessor());
	    } else if (idx == 0) {
	        return first;
	    }
		return null;
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeInfo("ceylon.language::Iterator<Element>")
	public Iterator<? extends Element> getIterator() {
		return list$impl.getIterator();
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeInfo("Element|ceylon.language::Nothing")
	public Element findLast(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.findLast(selecting);
	}

	@Override @SuppressWarnings("rawtypes")
	@Annotations(@Annotation("actual"))
	@TypeParameters(@TypeParameter("Other"))
	@TypeInfo("ceylon.language::Sequence<Element|Other>")
	public <Other> Sequence withLeading(@Name("element")
            @TypeInfo("Other") Other element) {
		return list$impl.withLeading(element);
	}

	@Override @SuppressWarnings("rawtypes")
	@Annotations(@Annotation("actual"))
	@TypeParameters(@TypeParameter("Other"))
	@TypeInfo("ceylon.language::Sequence<Element|Other>")
	public <Other> Sequence withTrailing(@Name("element")
            @TypeInfo("Other") Other element) {
		return list$impl.withTrailing(element);
	}

	@Override
	@Annotations(@Annotation("actual"))
	public boolean getEmpty() {
		return false;
	}

	@Override
	@Annotations(@Annotation("actual"))
	public boolean contains(@Name("element")
	        @TypeInfo("ceylon.language::Object")
	        java.lang.Object element) {
		return collection$impl.contains(element);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Result>")
	@TypeParameters(@TypeParameter("Result"))
	public <Result> Iterable<? extends Result> map(
	        @TypeInfo("ceylon.language::Callable<Result,Element>")
			Callable<? extends Result> collecting) {
		return iterable$impl.map(collecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element>")
	public Iterable<? extends Element> filter(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
			Callable<? extends Boolean> selecting) {
		return iterable$impl.filter(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("Result")
	@TypeParameters(@TypeParameter("Result"))
	public <Result> Result fold(@Name("initial")
            @TypeInfo("Result") Result initial,
            @Name("accumulating")
            @TypeInfo("ceylon.language::Callable<Result,Result,Element>")
            Callable<? extends Result> accumulating) {
		return iterable$impl.fold(initial, accumulating);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("Element|ceylon.language::Nothing")
	public Element find(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.find(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Empty|ceylon.language::Sequence<Element>")
	public List<? extends Element> select(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.select(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Boolean")
	public boolean any(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.any(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Boolean")
	public boolean every(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.every(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element>")
	public Iterable<? extends Element> skipping(long skip) {
		return iterable$impl.skipping(skip);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element>")
	public Iterable<? extends Element> taking(long take) {
		return iterable$impl.taking(take);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element>")
	public Iterable<? extends Element> by(long step) {
		return iterable$impl.by(step);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Integer")
	public long count(
	        @TypeInfo("ceylon.language::Callable<ceylon.language::Boolean,Element>")
	        Callable<? extends Boolean> selecting) {
		return iterable$impl.count(selecting);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element&ceylon.language::Object>")
	public Iterable<? extends Element> getCoalesced() {
		return iterable$impl.getCoalesced();
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<ceylon.language::Entry<ceylon.language::Integer,Element&ceylon.language::Object>>")
	public Iterable<? extends Entry<? extends Integer, ? extends Element>> getIndexed() {
		return iterable$impl.getIndexed();
	}

	@Override @SuppressWarnings("rawtypes")
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Iterable<Element|Other>")
	@TypeParameters(@TypeParameter("Other"))
	public <Other> Iterable chain(@Name("other")
            @TypeInfo("ceylon.language::Iterable<Other>")
        	Iterable<? extends Other> other) {
		return iterable$impl.chain(other);
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeParameters(@TypeParameter(value = "Grouping", satisfies = "ceylon.language::Object"))
	@TypeInfo("ceylon.language::Map<Grouping,ceylon.language::Sequence<Element>>")
	public <Key> Map<? extends Key, ? extends Sequence<? extends Element>> group(@Name("grouping")
	        @TypeInfo("ceylon.language::Callable<Grouping,Element>")
			Callable<? extends Key> grouping) {
		return iterable$impl.group(grouping);
	}

	@Override
	@Annotations(@Annotation("default"))
	public boolean containsEvery(@Sequenced @Name("elements")
	        @TypeInfo("ceylon.language::Iterable<ceylon.language::Object>")
			Iterable<?> elements) {
		return category$impl.containsEvery(elements);
	}

	@Override
	@Ignore
	public boolean containsEvery() {
		return category$impl.containsEvery();
	}

	@Override
	@Ignore
	public Iterable<?> containsEvery$elements() {
		return category$impl.containsEvery$elements();
	}

	@Override
	@Annotations(@Annotation("default"))
	public boolean containsAny(@Sequenced @Name("elements")
	        @TypeInfo("ceylon.language::Iterable<ceylon.language::Object>")
	        Iterable<?> elements) {
		return category$impl.containsAny(elements);
	}

	@Override
	@Ignore
	public boolean containsAny() {
		return category$impl.containsAny();
	}

	@Override
	@Ignore
	public Iterable<?> containsAny$elements() {
		return category$impl.containsAny$elements();
	}

	@Override
	@Annotations(@Annotation("actual"))
	public Collection<? extends Element> getClone() {
		return this;
	}

	@Override
	@Annotations(@Annotation("default"))
	public Category getKeys() {
		return correspondence$impl.getKeys();
	}

	@Override
	@Annotations(@Annotation("default"))
	public boolean definesEvery(@Sequenced @Name("keys")
            @TypeInfo("ceylon.language::Iterable<ceylon.language::Integer>")
        	Iterable<? extends Integer> keys) {
		return correspondence$impl.definesEvery(keys);
	}

	@Override
	@Ignore
	public boolean definesEvery() {
		return correspondence$impl.definesEvery();
	}

	@Override
	@Ignore
	public Iterable<? extends Integer> definesEvery$keys() {
		return correspondence$impl.definesEvery$keys();
	}

	@Override
	@Annotations(@Annotation("default"))
	public boolean definesAny(@Sequenced @Name("keys")
            @TypeInfo("ceylon.language::Iterable<ceylon.language::Integer>")
        	Iterable<? extends Integer> keys) {
		return correspondence$impl.definesAny(keys);
	}

	@Override
	@Ignore
	public boolean definesAny() {
		return correspondence$impl.definesAny();
	}

	@Override
	@Ignore
	public Iterable<? extends Integer> definesAny$keys() {
		return correspondence$impl.definesAny$keys();
	}

	@Override
	@Annotations(@Annotation("default"))
	@TypeInfo("ceylon.language::Empty|ceylon.language::Sequence<Element|ceylon.language::Nothing>")
	public List<? extends Element> items(@Sequenced @Name("keys")
            @TypeInfo("ceylon.language::Iterable<ceylon.language::Integer>")
        	Iterable<? extends Integer> keys) {
		return correspondence$impl.items(keys);
	}

	@Override
	@Ignore
	public Iterable<? extends Element> items() {
		return correspondence$impl.items();
	}

	@Override
	@Ignore
	public Iterable<? extends Integer> items$keys() {
		return correspondence$impl.items$keys();
	}

	@Override
	@Ignore
	public Correspondence$impl<? super Integer, ? extends Element> $ceylon$language$Correspondence$impl() {
		return correspondence$impl;
	}

	@Override
	@Ignore
	public Correspondence$impl<? super Integer, ? extends Element>.Items Items$new(Sequence<? extends Integer> keys) {
		return correspondence$impl.Items$new(keys);
	}

	@Override
	@Annotations(@Annotation("default"))
	public List<? extends Element> span(Integer from,
	        @Name("to") @TypeInfo("ceylon.language::Nothing|ceylon.language::Integer") Integer to) {
	    long end = to==null ? getSize() : to.value;
	    long _from = from.value;
        return _from<=end ? this.segment(from,end-_from+1)
                : this.segment(Integer.instance(end),_from-end+1).getReversed().getSequence();
	}

    @Override @SuppressWarnings("unchecked")
	@Annotations(@Annotation("default"))
	public List<? extends Element> segment(Integer from, long length) {
	    long _from = from.value;
        if (_from <= 0) {
            return length==1 ? new ArraySequence<Element>(first)
                : rest.segment(Integer.instance(0),length+_from-1).withLeading(first);
        }
        return rest.segment(from.getPredecessor(),length);
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeInfo("ceylon.language::Integer")
	public Integer getLastIndex() {
	    Integer li = rest.getLastIndex();
		return li == null ? Integer.instance(0) : li.getSuccessor();
	}

	@Override
	@Annotations(@Annotation("default"))
	public Element getLast() {
		return sequence$impl.getLast();
	}

    @Override @SuppressWarnings("unchecked")
	@Annotations(@Annotation("actual"))
    @TypeInfo("ceylon.language::Sequence<Element>")
	public Sequence<? extends Element> getReversed() {
		return rest.getReversed().withTrailing(first);
	}

	@Override
	@Annotations(@Annotation("actual"))
    @TypeInfo("ceylon.language::Sequence<Element>")
	public Sequence<? extends Element> getSequence() {
		return this;
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeInfo("ceylon.language::Sequence<Element>")
	public Sequence<? extends Element> sort(
            @TypeInfo("ceylon.language::Callable<ceylon.language::Nothing|ceylon.language::Comparison,Element,Element>")
			Callable<? extends Comparison> comparing) {
		return sequence$impl.sort(comparing);
	}

	@Override
	@Annotations(@Annotation("actual"))
	@TypeParameters(@TypeParameter("Result"))
	@TypeInfo("ceylon.language::Sequence<Result>")
	public <Result> Sequence<? extends Result> collect(
            @TypeInfo("ceylon.language::Callable<Result,Element>")
			Callable<? extends Result> collecting) {
	    return sequence$impl.collect(collecting);
	}

}
