package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public class ElementKey {
	
	public final TokenExecutor exec;
	public final @NonNull Element elem;
	
	public ElementKey(TokenExecutor exec, @NonNull Element elem) {
		this.exec = exec;
		this.elem = elem;
	}
	
	@Override
	public @NonNull ElementKey clone() {
		return elem.dynClone(exec).toKey(exec);
	}
	
	@Override
	public int hashCode() {
		return elem.dynHash(exec);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Element other) {
			return elem.dynEqualTo(exec, other);
		}
		else if (obj instanceof ElementKey key) {
			return elem.dynEqualTo(exec, key.elem);
		}
		else {
			return false;
		}
	}
	
	@Override
	public @NonNull String toString() {
		return elem.stringCast(exec).toString(exec);
	}
}
