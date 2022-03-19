package dssl.interpret.element.value.primitive;

import java.util.Objects;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.value.StringValue;

public class StringElement extends PrimitiveElement<@NonNull String> {
	
	public StringElement(@NonNull String rawValue) {
		super(new StringValue(rawValue));
	}
	
	@Override
	public @NonNull String typeName() {
		return "string";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.stringCastExplicit();
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
		return new StringElement(value.raw);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("string", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StringElement) {
			StringElement other = (StringElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull String toBriefDebugString() {
		return "\"" + StringEscapeUtils.escapeJava(value.raw) + "\"";
	}
}
