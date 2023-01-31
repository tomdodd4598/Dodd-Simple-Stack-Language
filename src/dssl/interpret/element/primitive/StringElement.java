package dssl.interpret.element.primitive;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.collection.*;
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
	public @NonNull ListElement listCastExplicit() {
		return new ListElement(list());
	}
	
	@Override
	public @NonNull TupleElement tupleCastExplicit() {
		return new TupleElement(list());
	}
	
	@Override
	public @NonNull SetElement setCastExplicit() {
		return new SetElement(list());
	}
	
	@Override
	public @NonNull DictElement dictCastExplicit() {
		int elemCount = list().size();
		if ((elemCount & 1) == 0) {
			return new DictElement(list());
		}
		throw new IllegalArgumentException(String.format("Failed to cast tuple \"%s\" to dict as construction requires even number of arguments but received %s!", toString(), elemCount));
	}
	
	@Override
	public TokenResult onSize(TokenExecutor exec) {
		exec.push(new IntElement(size()));
		return TokenResult.PASS;
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
	
	@Override
	public TokenResult onUnpack(TokenExecutor exec) {
		for (@NonNull Element elem : list()) {
			exec.push(elem);
		}
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onEmpty(TokenExecutor exec) {
		exec.push(new BoolElement(value.raw.isEmpty()));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onContains(TokenExecutor exec, @NonNull Element elem) {
		exec.push(new BoolElement(contains(elem)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onContainsall(TokenExecutor exec, @NonNull Element elem) {
		if (!(elem instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsall\" requires iterable element as second argument!"));
		}
		exec.push(new BoolElement(((IterableElement) elem).collection().stream().allMatch(this::contains)));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onGet(TokenExecutor exec, @NonNull Element elem) {
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"get\" requires non-negative int value element as argument!"));
		}
		
		exec.push(list().get(primitiveInt));
		return TokenResult.PASS;
	}
	
	@Override
	public TokenResult onPut(TokenExecutor exec, @NonNull Element elem0, @NonNull Element elem1) {
		throw keywordError("put");
	}
	
	@Override
	public TokenResult onPutall(TokenExecutor exec, @NonNull Element elem) {
		throw keywordError("putall");
	}
	
	protected List<@NonNull Element> list() {
		if (list == null) {
			list = value.raw.chars().mapToObj(x -> new CharElement((char) x)).collect(Collectors.toList());
		}
		return list;
	}
	
	protected boolean contains(@NonNull Element elem) {
		if (elem instanceof CharElement || elem instanceof StringElement) {
			return value.raw.contains(elem.toString());
		}
		else {
			return false;
		}
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
