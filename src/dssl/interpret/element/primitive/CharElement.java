package dssl.interpret.element.primitive;

import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.CharValue;

public class CharElement extends PrimitiveElement<@NonNull Character, @NonNull CharValue> {
	
	public CharElement(Interpreter interpreter, @NonNull Character rawValue) {
		super(interpreter, interpreter.builtIn.charClazz, new CharValue(rawValue));
	}
	
	@Override
	public @NonNull CharElement charCast(TokenExecutor exec) {
		return this;
	}
	
	@Override
	public @NonNull TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	public char primitiveChar() {
		return value.raw.charValue();
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		return "'" + StringEscapeUtils.escapeJava(value.raw.toString()) + "'";
	}
	
	@Override
	public @NonNull Element clone() {
		return new CharElement(interpreter, value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(BuiltIn.CHAR, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharElement other) {
			return value.equals(other.value);
		}
		return false;
	}
}
