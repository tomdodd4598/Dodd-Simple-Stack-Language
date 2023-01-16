package dssl.interpret.element.primitive;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.*;
import dssl.interpret.value.StringValue;

public class StringElement extends PrimitiveElement<@NonNull String> implements IterableElement {
	
	protected List<@NonNull Element> list = null;
	
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
	public int size() {
		return value.raw.length();
	}
	
	@Override
	public Iterator<@NonNull Element> iterator() {
		return list().iterator();
	}
	
	@Override
	public Collection<@NonNull Element> collection() {
		return list();
	}
	
	protected List<@NonNull Element> list() {
		if (list == null) {
			list = value.raw.chars().mapToObj(x -> new CharElement((char) x)).collect(Collectors.toList());
		}
		return list;
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
	public @NonNull String toDebugString() {
		return "\"" + StringEscapeUtils.escapeJava(value.raw) + "\"";
	}
}
