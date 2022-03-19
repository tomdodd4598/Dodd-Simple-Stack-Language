package dssl.interpret.element.value.primitive;

import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.value.CharValue;

public class CharElement extends PrimitiveElement<@NonNull Character> {
	
	public CharElement(@NonNull Character rawValue) {
		super(new CharValue(rawValue));
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
	public @NonNull Element onNot() {
		throw unaryOpError("not");
	}
	
	@Override
	public @NonNull Element onNeg() {
		throw unaryOpError("neg");
	}
	
	@Override
	public @NonNull Element onInv() {
		throw unaryOpError("inv");
	}
	
	@Override
	public @NonNull Element clone() {
		return new CharElement(value.raw.charValue());
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
	public @NonNull String toBriefDebugString() {
		return "'" + StringEscapeUtils.escapeJava(value.raw.toString()) + "'";
	}
}
