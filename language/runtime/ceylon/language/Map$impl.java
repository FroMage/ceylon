package ceylon.language;

import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import com.redhat.ceylon.compiler.java.language.AbstractIterable;
import com.redhat.ceylon.compiler.java.language.FilterIterable;
import com.redhat.ceylon.compiler.java.language.MapIterable;
import com.redhat.ceylon.compiler.java.metadata.Annotation;
import com.redhat.ceylon.compiler.java.metadata.Annotations;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;

@Ignore
public final class Map$impl<Key,Item> {
    private final Map<Key, Item> $this;

    public Map$impl(Map<Key,Item> $this) {
        this.$this = $this;
    }

    public boolean equals(java.lang.Object that) {
        return _equals($this, that);
    }
    static <Key,Item> boolean _equals(final Map<Key,Item> $this, java.lang.Object that) {
        if (that instanceof Map) {
            Map other = (Map) that;
            if (other.getSize()==$this.getSize()) {
                java.lang.Object elem;
                for (ceylon.language.Iterator<? extends Entry<? extends Key,? extends Item>> iter = $this.getIterator();
                        !((elem = iter.next()) instanceof Finished);) {
                    Entry<Key,Item> entry = (Entry<Key,Item>) elem;
                    java.lang.Object y = other.item(entry.getKey());
                    Item x = entry.getItem();
                    if (x==y || x!=null && y!=null && x.equals(y)) {
                        continue;
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return _hashCode($this);
    }
    static <Key,Item> int _hashCode(final Map<Key,Item> $this) {
        int hashCode = 1;
        java.lang.Object elem;
        for (Iterator<? extends Entry<? extends Key,? extends Item>> iter=$this.getIterator(); !((elem = iter.next()) instanceof Finished);) {
            hashCode *= 31;
            if (elem != null) {
                hashCode += elem.hashCode();
            }
        }
        return hashCode;
    }

    public Set<? extends Key> getKeys(){
        return _getKeys($this);
    }
    static <Key,Item> Set<? extends Key> _getKeys(final Map<Key,Item> $this){
        class keySet implements Set<Key>{

            @Override
            public Collection<? extends Key> getClone() {
                return this;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return (int) $this.getSize();
            }

            @Override
            public Iterator<? extends Key> getIterator() {
                return new Iterator<Key>() {
                    final Iterator<? extends Entry<? extends Key, ? extends Item>> orig = $this.getIterator();
                    @SuppressWarnings("unchecked")
                    public java.lang.Object next() {
                        java.lang.Object tmp = orig.next();
                        if (tmp instanceof Finished) {
                            return tmp;
                        }
                        return ((Entry<? extends Key, ? extends Item>)tmp).getKey();
                    }
                };
            }

            @Override
            public long getSize() {
                return $this.getSize();
            }

            @Override
            public java.lang.String toString() {
                return Collection$impl._toString(this);
            }

            @Override
            public <Other> Set<? extends Object> union(Set<? extends Other> set) {
                return bottom_.getBottom();
            }

            @Override
            public <Other> Set<? extends Object> intersection(Set<? extends Other> set) {
                return bottom_.getBottom();
            }

            @Override
            public <Other> Set<? extends Object> exclusiveUnion(Set<? extends Other> set) {
                return bottom_.getBottom();
            }

            @Override
            public <Other> Set<? extends Key> complement(Set<? extends Other> set) {
                return bottom_.getBottom();
            }

            // concrete interface methods:

            @Override
            @Ignore
            public Iterable<? extends Key> getRest() {
                return Iterable$impl._getRest(this);
            }

            @Override
            @Ignore
            public Key getFirst() {
                return Iterable$impl._getFirst(this);
            }
            @Override @Ignore public Key getLast() {
                return Iterable$impl._getLast(this);
            }

            @Override
            @Ignore
            public boolean getEmpty() {
                return Collection$impl._getEmpty(this);
            }

            @Override
            @Ignore
            public boolean contains(java.lang.Object element) {
                return Collection$impl._contains(this, element);
            }

            @Override
            @Ignore
            public boolean containsEvery(Iterable<?> elements) {
                return Category$impl._containsEvery(this, elements);
            }

            @Override
            @Ignore
            public boolean containsEvery() {
                return Category$impl._containsEvery(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsEvery$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean containsAny(Iterable<?> elements) {
                return Category$impl._containsAny(this, elements);
            }

            @Override
            @Ignore
            public boolean containsAny() {
                return Category$impl._containsAny(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsAny$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean superset(Set<? extends java.lang.Object> set) {
                return Set$impl._superset(this, set);
            }

            @Override
            @Ignore
            public boolean subset(Set<? extends java.lang.Object> set) {
                return Set$impl._subset(this, set);
            }
            @Override
            @Ignore
            public Iterable<? extends Key> getSequence() {
                return Iterable$impl._getSequence(this);
            }
            @Override @Ignore
            public Key find(Callable<? extends Boolean> f) {
                return Iterable$impl._find(this, f);
            }
            @Override @Ignore
            public Key findLast(Callable<? extends Boolean> f) {
                return Iterable$impl._findLast(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Key> sort(Callable<? extends Comparison> f) {
                return Iterable$impl._sort(this, f);
            }
            @Override
            @Ignore
            public <Result> Iterable<Result> map(Callable<? extends Result> f) {
                return new MapIterable<Key, Result>(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Key> filter(Callable<? extends Boolean> f) {
                return new FilterIterable<Key>(this, f);
            }
            @Override @Ignore
            public <Result> Iterable<? extends Result> collect(Callable<? extends Result> f) {
                return new MapIterable<Key, Result>(this, f).getSequence();
            }
            @Override @Ignore
            public Iterable<? extends Key> select(Callable<? extends Boolean> f) {
                return new FilterIterable<Key>(this, f).getSequence();
            }
            @Override
            @Ignore
            public <Result> Result fold(Result ini, Callable<? extends Result> f) {
                return Iterable$impl._fold(this, ini, f);
            }
            @Override @Ignore
            public boolean any(Callable<? extends Boolean> f) {
                return Iterable$impl._any(this, f);
            }
            @Override @Ignore
            public boolean every(Callable<? extends Boolean> f) {
                return Iterable$impl._every(this, f);
            }
			@Override @Ignore
			public Iterable<? extends Key> skipping(long skip) {
				return Iterable$impl._skipping(this, skip);
			}

			@Override @Ignore
			public Iterable<? extends Key> taking(long take) {
				return Iterable$impl._taking(this, take);
			}

			@Override @Ignore
			public Iterable<? extends Key> by(long step) {
				return Iterable$impl._by(this, step);
			}
            @Override @Ignore
            public long count(Callable<? extends Boolean> f) {
                return Iterable$impl._count(this, f);
            }
            @Override @Ignore
            public Iterable<? extends Key> getCoalesced() {
                return Iterable$impl._getCoalesced(this);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Integer, ? extends Key>> getIndexed() {
                return Iterable$impl._getIndexed(this);
            }
			@Override @Ignore public <Other>Iterable chain(Iterable<? extends Other> other) {
				return Iterable$impl._chain(this, other);
			}
		    @Override @Ignore
		    public <Key2> Map<? extends Key2, ? extends Sequence<? extends Key>> group(Callable<? extends Key2> grouping) {
		        return Iterable$impl._group(this, grouping);
		    }
        }
        return new keySet();
    }

    public Collection<? extends Item> getValues(){
        return _getValues($this);
    }
    static <Key,Item> Collection<? extends Item> _getValues(final Map<Key,Item> $this){
        class valueCollection implements Collection<Item> {

            @Override
            public Collection<? extends Item> getClone() {
                return this;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                return false;
            }

            @Override
            public int hashCode() {
                return $this.hashCode();
            }

            @Override
            public Iterator<? extends Item> getIterator() {
                final Iterator<? extends Entry<? extends Key, ? extends Item>> orig = $this.getIterator();
                return new Iterator<Item>() {
                    @SuppressWarnings("unchecked")
                    public java.lang.Object next() {
                        java.lang.Object tmp = orig.next();
                        if (tmp instanceof Finished) {
                            return tmp;
                        }
                        return ((Entry<? extends Key, ? extends Item>)tmp).getItem();
                    }
                };
            }

            @Override
            public long getSize() {
                return $this.getSize();
            }

            @Override
            public java.lang.String toString() {
                return Collection$impl._toString(this);
            }

            // concrete interface methods:

            @Override
            @Ignore
            public Iterable<? extends Item> getRest() {
                return Iterable$impl._getRest(this);
            }

            @Override
            @Ignore
            public Item getFirst() {
                return Iterable$impl._getFirst(this);
            }
            @Override @Ignore public Item getLast() {
                return Iterable$impl._getLast(this);
            }

            @Override
            @Ignore
            public boolean containsEvery(Iterable<?> elements) {
                return Category$impl._containsEvery(this, elements);
            }

            @Override
            @Ignore
            public boolean containsEvery() {
                return Category$impl._containsEvery(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsEvery$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean containsAny(Iterable<?> elements) {
                return Category$impl._containsAny(this, elements);
            }

            @Override
            @Ignore
            public boolean containsAny() {
                return Category$impl._containsAny(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsAny$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean getEmpty() {
                return Collection$impl._getEmpty(this);
            }

            @Override
            @Ignore
            public boolean contains(java.lang.Object element) {
                return Collection$impl._contains(this, element);
            }

            @Override
            @Ignore
            public Iterable<? extends Item> getSequence() {
                return Iterable$impl._getSequence(this);
            }
            @Override @Ignore
            public Item find(Callable<? extends Boolean> f) {
                return Iterable$impl._find(this, f);
            }
            @Override @Ignore
            public Item findLast(Callable<? extends Boolean> f) {
                return Iterable$impl._findLast(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Item> sort(Callable<? extends Comparison> f) {
                return Iterable$impl._sort(this, f);
            }
            @Override
            @Ignore
            public <Result> Iterable<? extends Result> map(Callable<? extends Result> f) {
                return new MapIterable<Item, Result>(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Item> filter(Callable<? extends Boolean> f) {
                return new FilterIterable<Item>(this, f);
            }
            @Override @Ignore
            public <Result> Iterable<? extends Result> collect(Callable<? extends Result> f) {
                return new MapIterable<Item, Result>(this, f).getSequence();
            }
            @Override @Ignore
            public Iterable<? extends Item> select(Callable<? extends Boolean> f) {
                return new FilterIterable<Item>(this, f).getSequence();
            }
            @Override
            @Ignore
            public <Result> Result fold(Result ini, Callable<? extends Result> f) {
                return Iterable$impl._fold(this, ini, f);
            }
            @Override @Ignore
            public boolean any(Callable<? extends Boolean> f) {
                return Iterable$impl._any(this, f);
            }
            @Override @Ignore
            public boolean every(Callable<? extends Boolean> f) {
                return Iterable$impl._every(this, f);
            }
			@Override @Ignore
			public Iterable<? extends Item> skipping(long skip) {
				return Iterable$impl._skipping(this, skip);
			}

			@Override @Ignore
			public Iterable<? extends Item> taking(long take) {
				return Iterable$impl._taking(this, take);
			}

			@Override @Ignore
			public Iterable<? extends Item> by(long step) {
				return Iterable$impl._by(this, step);
			}
            @Override @Ignore
            public long count(Callable<? extends Boolean> f) {
                return Iterable$impl._count(this, f);
            }
            @Override @Ignore
            public Iterable<? extends Item> getCoalesced() {
                return Iterable$impl._getCoalesced(this);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Integer, ? extends Item>> getIndexed() {
                return Iterable$impl._getIndexed(this);
            }
			@Override @Ignore public <Other>Iterable chain(Iterable<? extends Other> other) {
				return Iterable$impl._chain(this, other);
			}
		    @Override @Ignore
		    public <Key2> Map<? extends Key2, ? extends Sequence<? extends Item>> group(Callable<? extends Key2> grouping) {
		        return Iterable$impl._group(this, grouping);
		    }
        }
        return new valueCollection();
    }

    public Map<? extends Item, ? extends Set<? extends Key>> getInverse(){
        return _getInverse($this);
    }
    static <Key,Item> Map<? extends Item, ? extends Set<? extends Key>> _getInverse(final Map<Key,Item> $this){
        class inverse implements Map<Item, Set<? extends Key>>{

            @Override
            public Collection<? extends Entry<? extends Item, ? extends Set<? extends Key>>> getClone() {
                return this;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                return _equals(this, obj);
            }

            @Override
            public int hashCode() {
                return (int) $this.getSize();
            }

            @Override
            public Set<? extends Key> item(final java.lang.Object key) {
                return new LazySet<Key>(new AbstractIterable<Key>(){
                    public Iterator<? extends Key> getIterator() {
                        return new Iterator<Key>(){
                            final Iterator<? extends Entry<? extends Key, ? extends Item>> orig = $this.getIterator();
                            public java.lang.Object next() {
                                java.lang.Object tmp = orig.next();
                                while (!(tmp instanceof Finished)) {
                                    @SuppressWarnings("unchecked")
                                    Entry<? extends Key, ? extends Item> e = (Entry<? extends Key, ? extends Item>)tmp;
                                    if (e.getItem().equals(key)) {
                                        return e.getKey();
                                    }
                                    tmp = orig.next();
                                }
                                return tmp;
                            }
                        };
                    }
                });
            }

            @Override
            public Iterator<? extends Entry<? extends Item, ? extends Set<? extends Key>>> getIterator() {
                return new Iterator<Entry<? extends Item, ? extends Set<Key>>>(){
                    private final Iterator<? extends Entry<? extends Key, ? extends Item>> orig = $this.getIterator();
                    @SuppressWarnings("unchecked")
                    public java.lang.Object next() {
                        java.lang.Object tmp = orig.next();
                        if (tmp instanceof Finished) {
                            return tmp;
                        }
                        final Item item = ((Entry<? extends Key, ? extends Item>)tmp).getItem();
                        return new Entry<Item, Set<? extends Key>>(item, item(item));
                    }
                };
            }

            @Override
            public long getSize() {
                return $this.getSize();
            }

            @Override
            public java.lang.String toString() {
                return Collection$impl._toString(this);
            }

            // concrete interface methods:

            @Override
            @Ignore
            public boolean defines(java.lang.Object key) {
                return Correspondence$impl._defines(this, key);
            }

            @Override
            @Ignore
            public boolean definesEvery(Iterable<? extends java.lang.Object> keys) {
                return Correspondence$impl._definesEvery(this, keys);
            }

            @Override
            @Ignore
            public boolean definesEvery() {
                return Correspondence$impl._definesEvery(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<? extends java.lang.Object> definesEvery$keys() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean definesAny(Iterable<? extends java.lang.Object> keys) {
                return Correspondence$impl._definesAny(this, keys);
            }

            @Override
            @Ignore
            public boolean definesAny() {
                return Correspondence$impl._definesAny(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<? extends java.lang.Object> definesAny$keys() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public Iterable<? extends Set<? extends Key>> items(Iterable<? extends java.lang.Object> keys) {
                return Correspondence$impl._items(this, keys);
            }

            @Override
            @Ignore
            public Iterable<? extends Set<? extends Key>> items() {
                return Correspondence$impl._items(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<? extends java.lang.Object> items$keys() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean getEmpty() {
                return Collection$impl._getEmpty(this);
            }

            @Override
            @Ignore
            public boolean contains(java.lang.Object element) {
                return Collection$impl._contains(this, element);
            }

            @Override
            @Ignore
            public boolean containsEvery(Iterable<?> elements) {
                return Category$impl._containsEvery(this, elements);
            }

            @Override
            @Ignore
            public boolean containsEvery() {
                return Category$impl._containsEvery(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsEvery$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public boolean containsAny(Iterable<?> elements) {
                return Category$impl._containsAny(this, elements);
            }

            @Override
            @Ignore
            public boolean containsAny() {
                return Category$impl._containsAny(this, empty_.getEmpty());
            }

            @Override
            @Ignore
            public Iterable<?> containsAny$elements() {
                return empty_.getEmpty();
            }

            @Override
            @Ignore
            public Set<? extends Item> getKeys() {
                return Map$impl._getKeys(this);
            }

            @Override
            @Ignore
            public Collection<? extends Set<? extends Key>> getValues() {
                return Map$impl._getValues(this);
            }

            @Override
            @Ignore
            public Map<? extends Set<? extends Key>, ? extends Set<? extends Item>> getInverse() {
                return Map$impl._getInverse(this);
            }
            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> getSequence() {
                    return Iterable$impl._getSequence(this);
            }
            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> getRest() {
                return Iterable$impl._getRest(this);
            }
            @Override
            @Ignore
            public Entry<? extends Item, ? extends Set<? extends Key>> getFirst() {
                return Iterable$impl._getFirst(this);
            }
            @Override @Ignore
            public Entry<? extends Item, ? extends Set<? extends Key>> getLast() {
                return Iterable$impl._getLast(this);
            }
            @Override @Ignore
            public Entry<? extends Item, ? extends Set<? extends Key>> find(Callable<? extends Boolean> f) {
                return Iterable$impl._find(this, f);
            }
            @Override @Ignore
            public Entry<? extends Item, ? extends Set<? extends Key>> findLast(Callable<? extends Boolean> f) {
                return Iterable$impl._findLast(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> sort(Callable<? extends Comparison> f) {
                return Iterable$impl._sort(this, f);
            }
            @Override
            @Ignore
            public <Result> Iterable<? extends Result> map(Callable<? extends Result> f) {
                return new MapIterable<Entry<? extends Item, ? extends Set<? extends Key>>, Result>(this, f);
            }
            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> filter(Callable<? extends Boolean> f) {
                return new FilterIterable<Entry<? extends Item, ? extends Set<? extends Key>>>(this, f);
            }
            @Override @Ignore
            public <Result> Iterable<? extends Result> collect(Callable<? extends Result> f) {
                return new MapIterable<Entry<? extends Item, ? extends Set<? extends Key>>, Result>(this, f).getSequence();
            }
            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> select(Callable<? extends Boolean> f) {
                return new FilterIterable<Entry<? extends Item, ? extends Set<? extends Key>>>(this, f).getSequence();
            }
            @Override
            @Ignore
            public <Result> Result fold(Result ini, Callable<? extends Result> f) {
                return Iterable$impl._fold(this, ini, f);
            }
            @Override @Ignore
            public boolean any(Callable<? extends Boolean> f) {
                return Iterable$impl._any(this, f);
            }
            @Override @Ignore
            public boolean every(Callable<? extends Boolean> f) {
                return Iterable$impl._every(this, f);
            }
			@Override @Ignore
			public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> skipping(long skip) {
				return Iterable$impl._skipping(this, skip);
			}

			@Override @Ignore
			public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> taking(long take) {
				return Iterable$impl._taking(this, take);
			}

			@Override @Ignore
			public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> by(long step) {
				return Iterable$impl._by(this, step);
			}
            @Override @Ignore
            public long count(Callable<? extends Boolean> f) {
                return Iterable$impl._count(this, f);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Item, ? extends Set<? extends Key>>> getCoalesced() {
                return Iterable$impl._getCoalesced(this);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Integer, ? extends Entry<? extends Item, ? extends Set<? extends Key>>>> getIndexed() {
                return Iterable$impl._getIndexed(this);
            }
            @Override @Ignore public <Other>Iterable chain(Iterable<? extends Other> other) {
                return Iterable$impl._chain(this, other);
            }
            @Override @Ignore
            public <Key2> Map<? extends Key2, ? extends Sequence<? extends Entry<? extends Item, ? extends Set<? extends Key>>>> group(Callable<? extends Key2> grouping) {
                return Iterable$impl._group(this, grouping);
            }

			@Override @Ignore
			public <Result> Map<? extends Item, ? extends Result> mapItems(Callable<? extends Result> mapping) {
			    return Map$impl._mapItems(this, mapping);
			}
        }
        return new inverse();
    }

    public <Result> Map<? extends Key, ? extends Result> mapItems(Callable<Result> mapping) {
        return Map$impl._mapItems($this, mapping);
    }

    static <Key,Item, Result> Map<? extends Key, ? extends Result> _mapItems(
            final Map<? extends Key, ? extends Item> $this, final Callable<Result> mapping) {
        return new Map<Key, Result>() {

            @Override @Ignore
            public Result item(java.lang.Object key) {
                Item e = $this.item(key);
                return e == null ? null : mapping.$call(key, e);
            }

            @Override @Ignore
            public boolean defines(java.lang.Object key) {
                return $this.defines(key);
            }

            @Override @Ignore
            public boolean definesEvery(
                    Iterable<? extends java.lang.Object> keys) {
                return $this.definesEvery(keys);
            }

            @Override @Ignore
            public boolean definesEvery() {
                return $this.definesEvery();
            }

            @Override @Ignore
            public Iterable<? extends java.lang.Object> definesEvery$keys() {
                return $this.definesEvery$keys();
            }

            @Override @Ignore
            public boolean definesAny(Iterable<? extends java.lang.Object> keys) {
                return $this.definesAny(keys);
            }

            @Override @Ignore
            public boolean definesAny() {
                return $this.definesAny();
            }

            @Override @Ignore
            public Iterable<? extends java.lang.Object> definesAny$keys() {
                return $this.definesAny$keys();
            }

            @Override
            public Iterable<? extends Result> items(
                    Iterable<? extends java.lang.Object> keys) {
                return Correspondence$impl._items(this, keys);
            }

            @Override
            public Iterable<? extends Result> items() {
                return Correspondence$impl._items(this, empty_.getEmpty());
            }

            @Override
            public Iterable<? extends java.lang.Object> items$keys() {
                return empty_.getEmpty();
            }

            @Override
            public boolean getEmpty() {
                return $this.getEmpty();
            }

            @Override
            public boolean contains(java.lang.Object element) {
                return Collection$impl._contains(this, element);
            }

            @Override
            public Iterator<? extends Entry<? extends Key, ? extends Result>> getIterator() {
                final Iterator<? extends Entry<? extends Key, ? extends Item>> iter = $this.getIterator();
                return new Iterator<Entry<Key, Result>>(){
                    @Override @Ignore
                    public java.lang.Object next() {
                        java.lang.Object e = iter.next();
                        return e == exhausted_.getExhausted() ? e : new Entry(((Entry)e).getKey(),
                                mapping.$call(((Entry)e).getKey(), ((Entry)e).getItem()));
                    }
                };
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> getSequence() {
                return Iterable$impl._getSequence(this);
            }

            @Override
            @Ignore
            public Iterable<? extends Entry<? extends Key, ? extends Result>> getRest() {
                return Iterable$impl._getRest(this);
            }

            @Override
            @Ignore
            public Entry<? extends Key, ? extends Result> getFirst() {
                return Iterable$impl._getFirst(this);
            }
            @Override @Ignore
            public Entry<? extends Key, ? extends Result> getLast() {
                return Iterable$impl._getLast(this);
            }

            @Override
            public <R2> Iterable<? extends R2> map(
                    Callable<? extends R2> collecting) {
                return new MapIterable<Entry<? extends Key, ? extends Result>, R2>(this, collecting);
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> filter(
                    Callable<? extends Boolean> selecting) {
                return new FilterIterable<Entry<? extends Key, ? extends Result>>(this, selecting);
            }
            @Override
            public <R2> Iterable<? extends R2> collect(
                    Callable<? extends R2> collecting) {
                return new MapIterable<Entry<? extends Key, ? extends Result>, R2>(this, collecting).getSequence();
            }
            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> select(
                    Callable<? extends Boolean> selecting) {
                return new FilterIterable<Entry<? extends Key, ? extends Result>>(this, selecting).getSequence();
            }

            @Override
            public <R2> R2 fold(R2 initial,
                    Callable<? extends R2> accumulating) {
                return Iterable$impl._fold(this, initial, accumulating);
            }

            @Override @Ignore
            public Entry<? extends Key, ? extends Result> find(
                    Callable<? extends Boolean> selecting) {
                return Iterable$impl._find(this, selecting);
            }

            @Override @Ignore
            public Entry<? extends Key, ? extends Result> findLast(
                    Callable<? extends Boolean> selecting) {
                return Iterable$impl._findLast(this, selecting);
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> sort(
                    Callable<? extends Comparison> comparing) {
                return Iterable$impl._sort(this, comparing);
            }

            @Override
            public boolean any(Callable<? extends Boolean> selecting) {
                return Iterable$impl._any(this, selecting);
            }

            @Override
            public boolean every(Callable<? extends Boolean> selecting) {
                return Iterable$impl._every(this, selecting);
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> skipping(
                    long skip) {
                return Iterable$impl._skipping(this, skip);
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> taking(
                    long take) {
                return Iterable$impl._taking(this, take);
            }

            @Override
            public Iterable<? extends Entry<? extends Key, ? extends Result>> by(
                    long step) {
                return Iterable$impl._by(this, step);
            }
            @Override @Ignore
            public long count(Callable<? extends Boolean> f) {
                return Iterable$impl._count(this, f);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Key, ? extends Result>> getCoalesced() {
                return Iterable$impl._getCoalesced(this);
            }
            @Override @Ignore
            public Iterable<? extends Entry<? extends Integer, ? extends Entry<? extends Key, ? extends Result>>> getIndexed() {
                return Iterable$impl._getIndexed(this);
            }
            @Override @Ignore public <Other>Iterable chain(Iterable<? extends Other> other) {
                return Iterable$impl._chain(this, other);
            }
            @Override @Ignore
            public <Key2> Map<? extends Key2, ? extends Sequence<? extends Entry<? extends Key, ? extends Result>>> group(Callable<? extends Key2> grouping) {
                return Iterable$impl._group(this, grouping);
            }

            @Override
            public long getSize() {
                return $this.getSize();
            }

            @Override
            public boolean containsEvery(Iterable<?> elements) {
                return Category$impl._containsEvery(this, elements);
            }

            @Override
            public boolean containsEvery() {
                return Category$impl._containsEvery(this, empty_.getEmpty());
            }

            @Override
            public Iterable<?> containsEvery$elements() {
                return empty_.getEmpty();
            }

            @Override
            public boolean containsAny(Iterable<?> elements) {
                return Category$impl._containsAny(this, elements);
            }

            @Override
            public boolean containsAny() {
                return Category$impl._containsAny(this, empty_.getEmpty());
            }

            @Override
            public Iterable<?> containsAny$elements() {
                return empty_.getEmpty();
            }

            @Override
            public Collection<? extends Entry<? extends Key, ? extends Result>> getClone() {
                return this;
            }

            @Override
            public Set<? extends Key> getKeys() {
                return $this.getKeys();
            }

            @Override
            public Collection<? extends Result> getValues() {
                return _getValues(this);
            }

            @Override
            public Map<? extends Result, ? extends Set<? extends Key>> getInverse() {
                return _getInverse(this);
            }

            @Override
            public <R2> Map<? extends Key, ? extends R2> mapItems(
                    Callable<? extends R2> mapping) {
                return _mapItems(this, mapping);
            }

            @Override
            public java.lang.String toString() {
            	return Collection$impl._toString(this);
            }

        };
    }
}
