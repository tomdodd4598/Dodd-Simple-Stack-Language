package dssl;

import java.util.*;

import org.eclipse.jdt.annotation.Nullable;

public class Hierarchy<K, V> {
	
	public final Map<K, V> internal = new HashMap<>();
	protected final Hierarchy<K, V> prev;
	
	public Hierarchy(Hierarchy<K, V> prev) {
		this.prev = prev;
	}
	
	public @Nullable V put(K key, V value, boolean shadow) {
		if (shadow || prev == null || internal.containsKey(key)) {
			return internal.put(key, value);
		}
		else {
			return prev.put(key, value, shadow);
		}
	}
	
	public @Nullable V get(K key) {
		if (internal.containsKey(key)) {
			return internal.get(key);
		}
		return prev == null ? null : prev.get(key);
	}
	
	public boolean containsKey(K key) {
		if (internal.containsKey(key)) {
			return true;
		}
		return prev == null ? false : prev.containsKey(key);
	}
}
