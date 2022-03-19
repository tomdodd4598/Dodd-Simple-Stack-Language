package dssl.interpret.element.value.primitive;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;
import dssl.interpret.value.FloatValue;

public class FloatElement extends PrimitiveElement<@NonNull Double> {
	
	public FloatElement(@NonNull Double rawValue) {
		super(new FloatValue(rawValue));
	}
	
	@Override
	public @NonNull String typeName() {
		return "float";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.floatCastExplicit();
	}
	
	@Override
	public @NonNull Element onNot() {
		throw unaryOpError("not");
	}
	
	@Override
	public @NonNull Element onNeg() {
		return new FloatElement(-value.raw);
	}
	
	@Override
	public @NonNull Element onInv() {
		throw unaryOpError("inv");
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("float", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FloatElement) {
			FloatElement other = (FloatElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public @NonNull Element clone() {
		return new FloatElement(value.raw.doubleValue());
	}
}
