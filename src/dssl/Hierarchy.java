package dssl;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;

public class Hierarchy<K, V> {
	
	protected final Map<K, V> internal;
	protected final List<Hierarchy<K, V>> parents;
	
	public Hierarchy() {
		this(new HashMap<>(), Arrays.asList());
	}
	
	protected Hierarchy(Map<K, V> internal, List<Hierarchy<K, V>> parents) {
		this.internal = internal;
		this.parents = parents;
	}
	
	protected @Nullable V putInternal(K key, V value, boolean shadow) {
		if (shadow || internal.containsKey(key)) {
			return internal.put(key, value);
		}
		for (Hierarchy<K, V> parent : parents) {
			V prev = parent.putInternal(key, value, false);
			if (prev != null) {
				return prev;
			}
		}
		return null;
	}
	
	public @Nullable V put(K key, V value, boolean shadow) {
		V prev = putInternal(key, value, shadow);
		if (!shadow && prev == null) {
			throw new IllegalArgumentException(String.format("Encountered unexpected key \"%s\"!", key));
		}
		return prev;
	}
	
	public void putAll(Hierarchy<K, V> other, boolean shadow) {
		for (Entry<K, V> entry : other.internal.entrySet()) {
			put(entry.getKey(), entry.getValue(), shadow);
		}
		for (Hierarchy<K, V> parent : other.parents) {
			putAll(parent, shadow);
		}
	}
	
	public @Nullable V get(K key) {
		if (internal.containsKey(key)) {
			return internal.get(key);
		}
		for (Hierarchy<K, V> parent : parents) {
			V value = parent.get(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
	
	public boolean containsKey(K key) {
		if (internal.containsKey(key)) {
			return true;
		}
		for (Hierarchy<K, V> parent : parents) {
			if (parent.containsKey(key)) {
				return true;
			}
		}
		return false;
	}
	
	public Hierarchy<K, V> copy(boolean child) {
		return child ? new Hierarchy<>(new HashMap<>(), Arrays.asList(this)) : this;
	}
	
	public Hierarchy<K, V> branch(List<Hierarchy<K, V>> parents) {
		return new Hierarchy<>(internal, parents);
	}
}
