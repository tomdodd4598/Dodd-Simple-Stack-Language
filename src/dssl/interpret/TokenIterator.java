package dssl.interpret;

import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.node.*;

public abstract class TokenIterator implements Iterator<@NonNull Token> {
	
	protected @NonNull Token prev = new InvalidToken(""), curr = new InvalidToken("");
	protected boolean start = true, requireSeparator = false;
	
	public TokenIterator() {
		
	}
	
	@Override
	public boolean hasNext() {
		if (start) {
			onStart();
			start = false;
		}
		return validNext();
	}
	
	@Override
	public @NonNull Token next() {
		boolean notSeparator = !Helpers.isSeparator(curr);
		if (requireSeparator && notSeparator) {
			throw new IllegalArgumentException(String.format("Encountered tokens \"%s\" and \"%s\" not separated by comment or whitespace!", prev.getText(), curr.getText()));
		}
		else {
			requireSeparator = notSeparator;
			prev = curr;
			curr = getNextChecked();
			return prev;
		}
	}
	
	public abstract void onStart();
	
	public abstract boolean validNext();
	
	protected abstract Token getNext();
	
	protected final @NonNull Token getNextChecked() {
		Token next = getNext();
		if (next == null) {
			throw new IllegalArgumentException(String.format("Encountered null token!"));
		}
		else {
			return next;
		}
	}
}
