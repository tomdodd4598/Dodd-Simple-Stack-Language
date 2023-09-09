package dssl.interpret;

import java.util.*;

public class Reverse<T> implements Iterable<T> {
	
	protected final Deque<T> deque;
	
	public Reverse(Deque<T> deque) {
		this.deque = deque;
	}
	
	@Override
	public Iterator<T> iterator() {
		return deque.descendingIterator();
	}
	
	public int size() {
		return deque.size();
	}
}
