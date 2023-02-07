package dssl.interpret.element.primitive;

import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.CharValue;

public class CharElement extends PrimitiveElement<@NonNull Character, @NonNull CharValue> {
	
	public CharElement(@NonNull Character rawValue) {
		super(BuiltIn.CHAR_CLAZZ, new CharValue(rawValue));
	}
	
	@Override
	public @NonNull String typeName() {
		return "char";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.charCastExplicit();
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
		throw unaryOpError("!");
	}
	
	public char primitiveChar() {
		return value.raw.charValue();
	}
	
	@Override
	public @NonNull Element clone() {
		return new CharElement(primitiveChar());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("char", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharElement) {
			CharElement other = (CharElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "'" + StringEscapeUtils.escapeJava(value.raw.toString()) + "'";
	}
}
