package dssl.interpret.element.primitive;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.Element;
import dssl.interpret.value.BoolValue;

public class BoolElement extends PrimitiveElement<@NonNull Boolean> {
	
	public BoolElement(@NonNull Boolean rawValue) {
		super(new BoolValue(rawValue));
	}
	
	@Override
	public @NonNull String typeName() {
		return "bool";
	}
	
	@Override
	public Element castInternal(@NonNull Element elem) {
		return elem.boolCastExplicit();
	}
	
	public boolean primitiveBool() {
		return value.raw;
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
		exec.push(new BoolElement(!value.raw));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onNeg(TokenExecutor exec) {
		throw unaryOpError("neg");
	}
	
	@Override
	public @NonNull Element clone() {
		return new BoolElement(value.raw.booleanValue());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("bool", value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolElement) {
			BoolElement other = (BoolElement) obj;
			return value.equals(other.value);
		}
		return false;
	}
}
