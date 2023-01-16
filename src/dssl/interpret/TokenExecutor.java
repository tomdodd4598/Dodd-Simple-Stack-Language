package dssl.interpret;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.NonNull;

import dssl.*;
import dssl.Helpers.Pair;
import dssl.interpret.element.*;
import dssl.interpret.element.bracket.*;
import dssl.interpret.element.clazz.*;
import dssl.interpret.element.collection.*;
import dssl.interpret.element.primitive.*;
import dssl.interpret.magic.*;
import dssl.interpret.token.BlockToken;
import dssl.node.*;

public class TokenExecutor extends TokenReader implements Scope {
	
	protected final Hierarchy<String, Def> defHierarchy;
	protected final Hierarchy<String, Clazz> clazzHierarchy;
	protected final Map<String, Magic> magicMap = new HashMap<>();
	
	protected TokenExecutor(Interpreter interpreter, Iterator<@NonNull Token> iterator) {
		super(interpreter, iterator);
		defHierarchy = new Hierarchy<>(null);
		clazzHierarchy = new Hierarchy<>(null);
	}
	
	public TokenExecutor(Iterator<@NonNull Token> iterator, TokenExecutor prev) {
		super(iterator, prev);
		defHierarchy = new Hierarchy<>(prev == null ? null : prev.defHierarchy);
		clazzHierarchy = new Hierarchy<>(prev == null ? null : prev.clazzHierarchy);
	}
	
	@Override
	public TokenResult iterate() {
		loop: while (iterator.hasNext()) {
			TokenResult readResult = read(iterator.next());
			switch (readResult) {
				case PASS:
					continue loop;
				case CONTINUE:
				case BREAK:
					if (this == interpreter.root) {
						throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used by the root executor!", readResult));
					}
				default:
					return readResult;
			}
		}
		return TokenResult.PASS;
	}
	
	@Override
	protected TokenResult read(@NonNull Token token) {
		if (interpreter.halt) {
			return TokenResult.QUIT;
		}
		TokenResult result = TOKEN_FUNCTION_MAP.apply(this, token);
		if (interpreter.debug && !(token instanceof TBlank || token instanceof TComment)) {
			interpreter.io.debug(token.getText().trim().replaceAll("\\s+", " ") + " -> " + elemStackDebugString() + "\n");
		}
		
		for (String str : interpreter.printList) {
			interpreter.io.print(str);
		}
		interpreter.printList.clear();
		
		return result;
	}
	
	@Override
	public Def getDef(String identifier) {
		return defHierarchy.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		defHierarchy.put(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public Clazz getClazz(String shallow) {
		return clazzHierarchy.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, Map<String, Def> defMap, Map<String, Clazz> clazzMap, Map<String, Magic> magicMap) {
		clazzHierarchy.put(shallow, new Clazz(null, shallow, defMap, clazzMap, magicMap), true);
	}
	
	public void push(@NonNull Element elem) {
		interpreter.elemStack.push(elem);
	}
	
	public @NonNull Element peek() {
		Element peek = Helpers.nullable(interpreter.elemStack.peek());
		if (peek == null) {
			throw new NoSuchElementException();
		}
		return peek;
	}
	
	public @NonNull Element[] peek(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		Iterator<@NonNull Element> iterator = interpreter.elemStack.iterator();
		int i = 0;
		while (i < count) {
			Element next = Helpers.nullable(iterator.next());
			if (next == null) {
				throw new NoSuchElementException();
			}
			else {
				elems[count - ++i] = next;
			}
		}
		return elems;
	}
	
	public @NonNull Element peekAt(int index) {
		@NonNull Element elem;
		Iterator<@NonNull Element> iterator = interpreter.elemStack.iterator();
		int i = 0;
		do {
			Element next = Helpers.nullable(iterator.next());
			if (next == null) {
				throw new NoSuchElementException();
			}
			else {
				elem = next;
			}
		}
		while (i++ < index);
		return elem;
	}
	
	@SuppressWarnings("null")
	public @NonNull Element pop() {
		return interpreter.elemStack.pop();
	}
	
	@SuppressWarnings("null")
	public @NonNull Element[] pop(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		for (int i = 0; i < count; ++i) {
			elems[count - i - 1] = interpreter.elemStack.pop();
		}
		return elems;
	}
	
	protected String elemStackDebugString() {
		return StreamSupport.stream(Spliterators.spliterator(interpreter.elemStack.descendingIterator(), interpreter.elemStack.size(), Spliterator.ORDERED), false).map(Element::toDebugString).collect(Helpers.SPACE_JOIN_COLLECTOR);
	}
	
	@FunctionalInterface
	protected static interface ExecutorTokenFunction {
		
		TokenResult apply(TokenExecutor exec, Token token);
	}
	
	protected static class TokenFunctionMap {
		
		protected final Map<Class<? extends Token>, ExecutorTokenFunction> internalMap = new HashMap<>();
		
		protected <T extends Token> void put(Class<T> clazz, ExecutorTokenFunction function) {
			internalMap.put(clazz, function);
		}
		
		protected <T extends Token> ExecutorTokenFunction get(Class<T> clazz) {
			return (ExecutorTokenFunction) internalMap.get(clazz);
		}
		
		protected <T extends Token> TokenResult apply(TokenExecutor exec, @NonNull T token) {
			Class<T> clazz = (Class<T>) token.getClass();
			ExecutorTokenFunction function = get(clazz);
			if (function == null) {
				throw new IllegalArgumentException(String.format("Encountered unsupported %s token \"%s\"!", clazz.getSimpleName(), token.getText()));
			}
			else {
				return function.apply(exec, token);
			}
		}
	}
	
	protected static final TokenFunctionMap TOKEN_FUNCTION_MAP = new TokenFunctionMap();
	
	static {
		TOKEN_FUNCTION_MAP.put(TBlank.class, TokenExecutor::onBlank);
		TOKEN_FUNCTION_MAP.put(TComment.class, TokenExecutor::onComment);
		
		TOKEN_FUNCTION_MAP.put(TLBrace.class, TokenExecutor::onLBrace);
		TOKEN_FUNCTION_MAP.put(TRBrace.class, TokenExecutor::onRBrace);
		
		TOKEN_FUNCTION_MAP.put(TLBracket.class, TokenExecutor::onLBracket);
		TOKEN_FUNCTION_MAP.put(TRBracket.class, TokenExecutor::onRBracket);
		
		// TODO
		// TOKEN_FUNCTION_MAP.put(TImport.class, TokenExecutor::onImport);
		// TOKEN_FUNCTION_MAP.put(TNative.class, TokenExecutor::onNative);
		
		TOKEN_FUNCTION_MAP.put(TDef.class, TokenExecutor::onDef);
		TOKEN_FUNCTION_MAP.put(TClass.class, TokenExecutor::onClass);
		TOKEN_FUNCTION_MAP.put(TMagic.class, TokenExecutor::onMagic);
		TOKEN_FUNCTION_MAP.put(TNew.class, TokenExecutor::onNew);
		
		TOKEN_FUNCTION_MAP.put(TExch.class, TokenExecutor::onExch);
		TOKEN_FUNCTION_MAP.put(TPop.class, TokenExecutor::onPop);
		TOKEN_FUNCTION_MAP.put(TDup.class, TokenExecutor::onDup);
		TOKEN_FUNCTION_MAP.put(TClone.class, TokenExecutor::onClone);
		
		TOKEN_FUNCTION_MAP.put(TRoll.class, TokenExecutor::onRoll);
		TOKEN_FUNCTION_MAP.put(TRid.class, TokenExecutor::onRid);
		TOKEN_FUNCTION_MAP.put(TCopy.class, TokenExecutor::onCopy);
		
		TOKEN_FUNCTION_MAP.put(TIndex.class, TokenExecutor::onIndex);
		TOKEN_FUNCTION_MAP.put(TCount.class, TokenExecutor::onCount);
		TOKEN_FUNCTION_MAP.put(TCountto.class, TokenExecutor::onCountto);
		
		TOKEN_FUNCTION_MAP.put(TRead.class, TokenExecutor::onRead);
		TOKEN_FUNCTION_MAP.put(TPrint.class, TokenExecutor::onPrint);
		TOKEN_FUNCTION_MAP.put(TPrintln.class, TokenExecutor::onPrintln);
		TOKEN_FUNCTION_MAP.put(TInterpret.class, TokenExecutor::onInterpret);
		
		TOKEN_FUNCTION_MAP.put(TInt.class, TokenExecutor::onInt);
		TOKEN_FUNCTION_MAP.put(TBool.class, TokenExecutor::onBool);
		TOKEN_FUNCTION_MAP.put(TFloat.class, TokenExecutor::onFloat);
		TOKEN_FUNCTION_MAP.put(TChar.class, TokenExecutor::onChar);
		TOKEN_FUNCTION_MAP.put(TString.class, TokenExecutor::onString);
		
		TOKEN_FUNCTION_MAP.put(TRange.class, TokenExecutor::onRange);
		TOKEN_FUNCTION_MAP.put(TList.class, TokenExecutor::onList);
		TOKEN_FUNCTION_MAP.put(TTuple.class, TokenExecutor::onTuple);
		TOKEN_FUNCTION_MAP.put(TSet.class, TokenExecutor::onSet);
		TOKEN_FUNCTION_MAP.put(TDict.class, TokenExecutor::onDict);
		
		TOKEN_FUNCTION_MAP.put(TNull.class, TokenExecutor::onNull);
		TOKEN_FUNCTION_MAP.put(THash.class, TokenExecutor::onHash);
		
		TOKEN_FUNCTION_MAP.put(TForeach.class, TokenExecutor::onForeach);
		TOKEN_FUNCTION_MAP.put(TUnpack.class, TokenExecutor::onUnpack);
		
		TOKEN_FUNCTION_MAP.put(TSize.class, TokenExecutor::onSize);
		TOKEN_FUNCTION_MAP.put(TEmpty.class, TokenExecutor::onEmpty);
		
		TOKEN_FUNCTION_MAP.put(THas.class, TokenExecutor::onHas);
		TOKEN_FUNCTION_MAP.put(TAdd.class, TokenExecutor::onAdd);
		TOKEN_FUNCTION_MAP.put(TRem.class, TokenExecutor::onRem);
		TOKEN_FUNCTION_MAP.put(THasall.class, TokenExecutor::onHasall);
		TOKEN_FUNCTION_MAP.put(TAddall.class, TokenExecutor::onAddall);
		TOKEN_FUNCTION_MAP.put(TRemall.class, TokenExecutor::onRemall);
		TOKEN_FUNCTION_MAP.put(TClear.class, TokenExecutor::onClear);
		
		TOKEN_FUNCTION_MAP.put(TGet.class, TokenExecutor::onGet);
		TOKEN_FUNCTION_MAP.put(TPut.class, TokenExecutor::onPut);
		TOKEN_FUNCTION_MAP.put(TPutall.class, TokenExecutor::onPutall);
		
		TOKEN_FUNCTION_MAP.put(THaskey.class, TokenExecutor::onHaskey);
		TOKEN_FUNCTION_MAP.put(THasvalue.class, TokenExecutor::onHasvalue);
		TOKEN_FUNCTION_MAP.put(THasentry.class, TokenExecutor::onHasentry);
		TOKEN_FUNCTION_MAP.put(TKeys.class, TokenExecutor::onKeys);
		TOKEN_FUNCTION_MAP.put(TValues.class, TokenExecutor::onValues);
		TOKEN_FUNCTION_MAP.put(TEntries.class, TokenExecutor::onEntries);
		
		TOKEN_FUNCTION_MAP.put(TType.class, TokenExecutor::onType);
		TOKEN_FUNCTION_MAP.put(TCast.class, TokenExecutor::onCast);
		
		TOKEN_FUNCTION_MAP.put(TExec.class, TokenExecutor::onExec);
		TOKEN_FUNCTION_MAP.put(TIf.class, TokenExecutor::onIf);
		TOKEN_FUNCTION_MAP.put(TIfelse.class, TokenExecutor::onIfelse);
		TOKEN_FUNCTION_MAP.put(TRepeat.class, TokenExecutor::onRepeat);
		TOKEN_FUNCTION_MAP.put(TLoop.class, TokenExecutor::onLoop);
		
		TOKEN_FUNCTION_MAP.put(TQuit.class, TokenExecutor::onQuit);
		TOKEN_FUNCTION_MAP.put(TContinue.class, TokenExecutor::onContinue);
		TOKEN_FUNCTION_MAP.put(TBreak.class, TokenExecutor::onBreak);
		
		TOKEN_FUNCTION_MAP.put(TEquals.class, TokenExecutor::onEquals);
		
		TOKEN_FUNCTION_MAP.put(TIncrement.class, TokenExecutor::onIncrement);
		TOKEN_FUNCTION_MAP.put(TDecrement.class, TokenExecutor::onDecrement);
		
		TOKEN_FUNCTION_MAP.put(TPlusEquals.class, TokenExecutor::onPlusEquals);
		TOKEN_FUNCTION_MAP.put(TAndEquals.class, TokenExecutor::onAndEquals);
		TOKEN_FUNCTION_MAP.put(TOrEquals.class, TokenExecutor::onOrEquals);
		TOKEN_FUNCTION_MAP.put(TXorEquals.class, TokenExecutor::onXorEquals);
		TOKEN_FUNCTION_MAP.put(TMinusEquals.class, TokenExecutor::onMinusEquals);
		TOKEN_FUNCTION_MAP.put(TConcatEquals.class, TokenExecutor::onConcatEquals);
		
		TOKEN_FUNCTION_MAP.put(TLeftShiftEquals.class, TokenExecutor::onLeftShiftEquals);
		TOKEN_FUNCTION_MAP.put(TRightShiftEquals.class, TokenExecutor::onRightShiftEquals);
		
		TOKEN_FUNCTION_MAP.put(TMultiplyEquals.class, TokenExecutor::onMultiplyEquals);
		TOKEN_FUNCTION_MAP.put(TDivideEquals.class, TokenExecutor::onDivideEquals);
		TOKEN_FUNCTION_MAP.put(TRemainderEquals.class, TokenExecutor::onRemainderEquals);
		TOKEN_FUNCTION_MAP.put(TPowerEquals.class, TokenExecutor::onPowerEquals);
		TOKEN_FUNCTION_MAP.put(TIdivideEquals.class, TokenExecutor::onIdivideEquals);
		TOKEN_FUNCTION_MAP.put(TModuloEquals.class, TokenExecutor::onModuloEquals);
		
		TOKEN_FUNCTION_MAP.put(TEqualTo.class, TokenExecutor::onEqualTo);
		TOKEN_FUNCTION_MAP.put(TNotEqualTo.class, TokenExecutor::onNotEqualTo);
		
		TOKEN_FUNCTION_MAP.put(TLessThan.class, TokenExecutor::onLessThan);
		TOKEN_FUNCTION_MAP.put(TLessOrEqual.class, TokenExecutor::onLessOrEqual);
		TOKEN_FUNCTION_MAP.put(TMoreThan.class, TokenExecutor::onMoreThan);
		TOKEN_FUNCTION_MAP.put(TMoreOrEqual.class, TokenExecutor::onMoreOrEqual);
		
		TOKEN_FUNCTION_MAP.put(TPlus.class, TokenExecutor::onPlus);
		TOKEN_FUNCTION_MAP.put(TAnd.class, TokenExecutor::onAnd);
		TOKEN_FUNCTION_MAP.put(TOr.class, TokenExecutor::onOr);
		TOKEN_FUNCTION_MAP.put(TXor.class, TokenExecutor::onXor);
		TOKEN_FUNCTION_MAP.put(TMinus.class, TokenExecutor::onMinus);
		TOKEN_FUNCTION_MAP.put(TConcat.class, TokenExecutor::onConcat);
		
		TOKEN_FUNCTION_MAP.put(TLeftShift.class, TokenExecutor::onLeftShift);
		TOKEN_FUNCTION_MAP.put(TRightShift.class, TokenExecutor::onRightShift);
		
		TOKEN_FUNCTION_MAP.put(TMultiply.class, TokenExecutor::onMultiply);
		TOKEN_FUNCTION_MAP.put(TDivide.class, TokenExecutor::onDivide);
		TOKEN_FUNCTION_MAP.put(TRemainder.class, TokenExecutor::onRemainder);
		TOKEN_FUNCTION_MAP.put(TPower.class, TokenExecutor::onPower);
		TOKEN_FUNCTION_MAP.put(TIdivide.class, TokenExecutor::onIdivide);
		TOKEN_FUNCTION_MAP.put(TModulo.class, TokenExecutor::onModulo);
		
		TOKEN_FUNCTION_MAP.put(TNot.class, TokenExecutor::onNot);
		TOKEN_FUNCTION_MAP.put(TNeg.class, TokenExecutor::onNeg);
		
		TOKEN_FUNCTION_MAP.put(TDeref.class, TokenExecutor::onDeref);
		
		TOKEN_FUNCTION_MAP.put(TIntValue.class, TokenExecutor::onIntValue);
		TOKEN_FUNCTION_MAP.put(TBoolValue.class, TokenExecutor::onBoolValue);
		TOKEN_FUNCTION_MAP.put(TFloatValue.class, TokenExecutor::onFloatValue);
		TOKEN_FUNCTION_MAP.put(TCharValue.class, TokenExecutor::onCharValue);
		TOKEN_FUNCTION_MAP.put(TStringValue.class, TokenExecutor::onStringValue);
		
		TOKEN_FUNCTION_MAP.put(TIdentifier.class, TokenExecutor::onIdentifier);
		TOKEN_FUNCTION_MAP.put(TLabel.class, TokenExecutor::onLabel);
		TOKEN_FUNCTION_MAP.put(TMember.class, TokenExecutor::onMember);
		
		TOKEN_FUNCTION_MAP.put(BlockToken.class, TokenExecutor::onBlock);
	}
	
	protected static TokenResult onBlank(TokenExecutor exec, Token token) {
		return TokenResult.PASS;
	}
	
	protected static TokenResult onComment(TokenExecutor exec, Token token) {
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLBrace(TokenExecutor exec, Token token) {
		TokenCollector collector = new TokenCollector(exec.interpreter, exec.iterator);
		collector.iterate();
		exec.push(new BlockElement(collector.listStack.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRBrace(TokenExecutor exec, Token token) {
		throw new IllegalArgumentException(String.format("Encountered \"}\" token without corresponding \"{\" token!"));
	}
	
	protected static TokenResult onLBracket(TokenExecutor exec, Token token) {
		exec.push(new LBracketElement());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRBracket(TokenExecutor exec, Token token) {
		exec.push(new RBracketElement());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDef(TokenExecutor exec, Token token) {
		return assign(exec, true);
	}
	
	protected static TokenResult onClass(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires label element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires block element as second argument!"));
		}
		
		TokenExecutor clazzExec = ((BlockElement) elem1).executor(exec);
		TokenResult result = clazzExec.iterate();
		((LabelElement) elem0).setClazz(clazzExec.defHierarchy.internal, clazzExec.clazzHierarchy.internal, clazzExec.magicMap);
		return result;
	}
	
	protected static TokenResult onMagic(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"magic\" requires label element as first argument!"));
		}
		
		String identifier = ((LabelElement) elem0).identifier;
		switch (identifier) {
			case "init":
				if (!(elem1 instanceof BlockElement)) {
					throw new IllegalArgumentException(String.format("Magic \"init\" requires block element as second argument!"));
				}
				exec.magicMap.put("init", new InitMagic((BlockElement) elem1));
				break;
			default:
				throw new IllegalArgumentException(String.format("Magic \"%s\" not supported!", identifier));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNew(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"new\" requires class element as argument!"));
		}
		return ((ClassElement) elem).instantiate(exec);
	}
	
	protected static TokenResult onExch(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem1);
		exec.push(elem0);
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPop(TokenExecutor exec, Token token) {
		exec.pop();
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDup(TokenExecutor exec, Token token) {
		exec.push(exec.peek());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onClone(TokenExecutor exec, Token token) {
		exec.push(exec.peek().clone());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRoll(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		IntElement intElem0 = elem0.intCastImplicit(), intElem1 = elem1.intCastImplicit();
		if (intElem0 == null || intElem1 == null) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires two int value elements as arguments!"));
		}
		
		int count = intElem0.primitiveInt(), roll = intElem1.primitiveInt();
		if (count < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires non-negative int value element as first argument!"));
		}
		
		@NonNull Element[] elems = exec.pop(count);
		for (int i = 0; i < count; ++i) {
			exec.push(elems[Helpers.mod(i - roll, count)]);
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRid(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"rid\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"rid\" requires non-negative int value element as argument!"));
		}
		
		for (int i = 0; i < primitiveInt; ++i) {
			exec.pop();
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCopy(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"copy\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"copy\" requires non-negative int value element as argument!"));
		}
		
		for (@NonNull Element el : exec.peek(primitiveInt)) {
			exec.push(el);
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIndex(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"index\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"index\" requires non-negative int value element as argument!"));
		}
		
		exec.push(exec.peekAt(primitiveInt));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCount(TokenExecutor exec, Token token) {
		exec.push(new IntElement(exec.interpreter.elemStack.size()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCountto(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		boolean includeLabel = false;
		if (!(elem instanceof LabelElement)) {
			boolean valid = false;
			BoolElement boolElem = elem.boolCastImplicit();
			if (boolElem != null) {
				@NonNull Element next = exec.pop();
				if (next instanceof LabelElement) {
					includeLabel = boolElem.primitiveBool();
					elem = next;
					valid = true;
				}
			}
			if (!valid) {
				throw new IllegalArgumentException(String.format("Keyword \"countto\" requires label element as first argument, and bool value element as optional second argument!"));
			}
		}
		exec.push(new IntElement(countElemsToLabel(exec, (LabelElement) elem, includeLabel)));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRead(TokenExecutor exec, Token token) {
		exec.push(new StringElement(exec.interpreter.io.read()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPrint(TokenExecutor exec, Token token) {
		exec.interpreter.printList.add(exec.pop().toString());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPrintln(TokenExecutor exec, Token token) {
		exec.interpreter.printList.add(exec.pop().toString());
		exec.interpreter.printList.add("\n");
		return TokenResult.PASS;
	}
	
	protected static TokenResult onInterpret(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		StringElement stringElem = elem.stringCastImplicit();
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"interpret\" requires string value element as argument!"));
		}
		return new TokenExecutor(new LexerIterator(stringElem.toString()), exec).iterate();
	}
	
	protected static TokenResult onInt(TokenExecutor exec, Token token) {
		exec.push(exec.pop().intCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBool(TokenExecutor exec, Token token) {
		exec.push(exec.pop().boolCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onFloat(TokenExecutor exec, Token token) {
		exec.push(exec.pop().floatCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onChar(TokenExecutor exec, Token token) {
		exec.push(exec.pop().charCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onString(TokenExecutor exec, Token token) {
		exec.push(exec.pop().stringCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRange(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new RangeElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.rangeCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onList(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new ListElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.listCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onTuple(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new TupleElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.tupleCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onSet(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new SetElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.setCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDict(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new DictElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.dictCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNull(TokenExecutor exec, Token token) {
		exec.push(NullElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected static TokenResult onHash(TokenExecutor exec, Token token) {
		exec.push(new IntElement(exec.pop().hashCode()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onForeach(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires iterable element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires block element as second argument!"));
		}
		
		loop: for (@NonNull Element elem : (IterableElement) elem0) {
			exec.push(elem);
			TokenResult invokeResult = ((BlockElement) elem1).executor(exec).iterate();
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return TokenResult.QUIT;
				default:
					break;
			}
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onUnpack(TokenExecutor exec, Token token) {
		return exec.pop().onUnpack(exec);
	}
	
	protected static TokenResult onSize(TokenExecutor exec, Token token) {
		return exec.pop().onSize(exec);
	}
	
	protected static TokenResult onEmpty(TokenExecutor exec, Token token) {
		return exec.pop().onEmpty(exec);
	}
	
	protected static TokenResult onHas(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onHas(exec, elem1);
	}
	
	protected static TokenResult onAdd(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAdd(exec, elem1);
	}
	
	protected static TokenResult onRem(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRem(exec, elem1);
	}
	
	protected static TokenResult onHasall(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onHasall(exec, elem1);
	}
	
	protected static TokenResult onAddall(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAddall(exec, elem1);
	}
	
	protected static TokenResult onRemall(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRemall(exec, elem1);
	}
	
	protected static TokenResult onClear(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		return elem.onClear(exec);
	}
	
	protected static TokenResult onGet(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onGet(exec, elem1);
	}
	
	protected static TokenResult onPut(TokenExecutor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPut(exec, elem1, elem2);
	}
	
	protected static TokenResult onPutall(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPutall(exec, elem1);
	}
	
	protected static TokenResult onHaskey(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"haskey\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHaskey(exec, elem1);
	}
	
	protected static TokenResult onHasvalue(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasvalue\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHasvalue(exec, elem1);
	}
	
	protected static TokenResult onHasentry(TokenExecutor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasentry\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHasentry(exec, elem1, elem2);
	}
	
	protected static TokenResult onKeys(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"keyset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onKeys(exec);
	}
	
	protected static TokenResult onValues(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"values\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onValues(exec);
	}
	
	protected static TokenResult onEntries(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"entryset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onEntries(exec);
	}
	
	protected static TokenResult onType(TokenExecutor exec, Token token) {
		exec.push(new TypeElement(exec.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCast(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof TypeElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"cast\" requires type element as argument!"));
		}
		exec.push(((TypeElement) elem).internal.cast(exec.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onExec(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"exec\" requires block element as argument!"));
		}
		return ((BlockElement) elem).executor(exec).iterate();
	}
	
	protected static TokenResult onIf(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		BoolElement boolElem = elem0.boolCastImplicit();
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires bool value element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires block element as second argument!"));
		}
		
		if (boolElem.primitiveBool()) {
			return ((BlockElement) elem1).executor(exec).iterate();
		}
		else {
			return TokenResult.PASS;
		}
	}
	
	protected static TokenResult onIfelse(TokenExecutor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		BoolElement boolElem = elem0.boolCastImplicit();
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires bool value element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires block element as second argument!"));
		}
		else if (!(elem2 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires block element as third argument!"));
		}
		
		if (boolElem.primitiveBool()) {
			return ((BlockElement) elem1).executor(exec).iterate();
		}
		else {
			return ((BlockElement) elem2).executor(exec).iterate();
		}
	}
	
	protected static TokenResult onRepeat(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		IntElement intElem = elem0.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative int value element as first argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative int value element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires block element as second argument!"));
		}
		
		loop: for (int i = 0; i < primitiveInt; ++i) {
			TokenResult invokeResult = ((BlockElement) elem1).executor(exec).iterate();
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return TokenResult.QUIT;
				default:
					break;
			}
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLoop(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"loop\" requires block element as second argument!"));
		}
		
		loop: while (true) {
			TokenResult invokeResult = ((BlockElement) elem).executor(exec).iterate();
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return TokenResult.QUIT;
				default:
					break;
			}
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onQuit(TokenExecutor exec, Token token) {
		exec.interpreter.halt = true;
		return TokenResult.QUIT;
	}
	
	protected static TokenResult onContinue(TokenExecutor exec, Token token) {
		return TokenResult.CONTINUE;
	}
	
	protected static TokenResult onBreak(TokenExecutor exec, Token token) {
		return TokenResult.BREAK;
	}
	
	protected static TokenResult onEquals(TokenExecutor exec, Token token) {
		return assign(exec, false);
	}
	
	protected static TokenResult onIncrement(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Increment operator \"++\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		exec.push(def.elem.onPlus(new IntElement(1)));
		return assign(exec, false);
	}
	
	protected static TokenResult onDecrement(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Decrement operator \"--\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		exec.push(def.elem.onMinus(new IntElement(1)));
		return assign(exec, false);
	}
	
	protected static TokenResult onPlusEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onPlus(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onAndEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onAnd(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onOrEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onOr(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onXorEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onXor(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onMinusEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onMinus(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onConcatEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onConcat(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onLeftShiftEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onArithmeticLeftShift(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onRightShiftEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onArithmeticRightShift(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onMultiplyEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onMultiply(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onDivideEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onDivide(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onRemainderEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onRemainder(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onPowerEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onPower(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onIdivideEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onIdivide(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onModuloEquals(TokenExecutor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onModulo(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onEqualTo(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		boolean nullLeft = NullElement.INSTANCE.equals(elems.left), nullRight = NullElement.INSTANCE.equals(elems.right);
		if (nullLeft || nullRight) {
			exec.push(new BoolElement(nullLeft == nullRight));
		}
		else {
			exec.push(elems.left.onEqualTo(elems.right));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNotEqualTo(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		boolean nullLeft = NullElement.INSTANCE.equals(elems.left), nullRight = NullElement.INSTANCE.equals(elems.right);
		if (nullLeft || nullRight) {
			exec.push(new BoolElement(nullLeft != nullRight));
		}
		else {
			exec.push(elems.left.onNotEqualTo(elems.right));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLessThan(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onLessThan(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLessOrEqual(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onLessOrEqual(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMoreThan(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMoreThan(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMoreOrEqual(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMoreOrEqual(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPlus(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onPlus(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onAnd(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onAnd(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onOr(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onOr(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onXor(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onXor(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMinus(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMinus(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onConcat(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onConcat(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLeftShift(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onArithmeticLeftShift(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRightShift(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onArithmeticRightShift(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMultiply(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMultiply(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDivide(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onDivide(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRemainder(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onRemainder(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPower(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onPower(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIdivide(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onIdivide(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onModulo(TokenExecutor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onModulo(elems.right));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNot(TokenExecutor exec, Token token) {
		exec.push(exec.pop().onNot());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNeg(TokenExecutor exec, Token token) {
		exec.push(exec.pop().onNeg());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDeref(TokenExecutor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"deref\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def;
		Clazz clazz;
		if ((def = label.getDef()) != null) {
			exec.push(def.elem);
		}
		else if ((clazz = label.getClazz()) != null) {
			exec.push(clazz.elem);
		}
		else {
			throw new IllegalArgumentException(String.format("Variable or class \"%s\" not defined!", label.identifier));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIntValue(TokenExecutor exec, Token token) {
		exec.push(new IntElement(new BigInteger(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBoolValue(TokenExecutor exec, Token token) {
		exec.push(new BoolElement(Boolean.parseBoolean(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onFloatValue(TokenExecutor exec, Token token) {
		exec.push(new FloatElement(Double.parseDouble(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCharValue(TokenExecutor exec, Token token) {
		exec.push(new CharElement(Helpers.parseChar(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onStringValue(TokenExecutor exec, Token token) {
		exec.push(new StringElement(Helpers.parseString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIdentifier(TokenExecutor exec, Token token) {
		String identifier = token.getText();
		Def def;
		Clazz clazz;
		if ((def = exec.getDef(identifier)) != null) {
			exec.push(def.elem);
		}
		else if ((clazz = exec.getClazz(identifier)) != null) {
			exec.push(clazz.elem);
		}
		else {
			throw new IllegalArgumentException(String.format("Variable or class \"%s\" not defined!", identifier));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLabel(TokenExecutor exec, Token token) {
		@SuppressWarnings("null") @NonNull String label = token.getText().substring(1);
		if (Helpers.KEYWORDS.contains(label)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a label!", label));
		}
		exec.push(new LabelElement(exec, label));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMember(TokenExecutor exec, Token token) {
		@SuppressWarnings("null") @NonNull String member = token.getText().substring(1);
		if (Helpers.KEYWORDS.contains(member)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a member!", member));
		}
		
		@NonNull Element elem = exec.pop();
		Def def;
		Clazz clazz, subclazz;
		if (elem instanceof LabelElement) {
			exec.push(((LabelElement) elem).extended(member));
		}
		else if (elem instanceof InstanceElement) {
			InstanceElement instance = (InstanceElement) elem;
			if ((def = instance.defMap.get(member)) != null) {
				exec.push(def.elem);
			}
			else if ((subclazz = instance.clazzMap.get(member)) != null) {
				exec.push(subclazz.elem);
			}
			else {
				clazz = instance.clazz;
				if ((def = clazz.defMap.get(member)) != null) {
					exec.push(instance);
					exec.push(def.elem);
				}
				else if ((subclazz = clazz.clazzMap.get(member)) != null) {
					exec.push(instance);
					exec.push(subclazz.elem);
				}
				else {
					throw new IllegalArgumentException(String.format("Instance member \"%s.%s\" not defined!", clazz.identifier, member));
				}
			}
		}
		else if (elem instanceof ClassElement) {
			clazz = ((ClassElement) elem).clazz;
			if ((def = clazz.defMap.get(member)) != null) {
				exec.push(def.elem);
			}
			else if ((subclazz = clazz.clazzMap.get(member)) != null) {
				exec.push(subclazz.elem);
			}
			else {
				throw new IllegalArgumentException(String.format("Class member \"%s.%s\" not defined!", clazz.identifier, member));
			}
		}
		else {
			throw new IllegalArgumentException(String.format("Member access \".%s\" requires label, instance or class element as first argument!", member));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBlock(TokenExecutor exec, Token token) {
		exec.push(new BlockElement(((BlockToken) token).tokens));
		return TokenResult.PASS;
	}
	
	protected static TokenResult assign(TokenExecutor exec, boolean shadow) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("%s requires label element as first argument!", shadow ? "Keyword \"def\"" : "Assignment"));
		}
		
		LabelElement label = (LabelElement) elem0;
		if (!shadow && label.getDef() == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		label.setDef(elem1, shadow);
		return TokenResult.PASS;
	}
	
	protected static int countElemsToLabel(TokenExecutor exec, LabelElement label, boolean includeLabel) {
		Iterator<@NonNull Element> iter = exec.interpreter.elemStack.iterator();
		int count = 0;
		
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (label.equals(elem)) {
				return includeLabel ? count + 1 : count;
			}
			else {
				++count;
			}
		}
		throw new IllegalArgumentException(String.format("Label \"/%s\" did not exist on stack!", label.identifier));
	}
	
	protected static List<@NonNull Element> getElemsToLBracket(TokenExecutor exec) {
		List<@NonNull Element> list = new ArrayList<>();
		
		while (!exec.interpreter.elemStack.isEmpty()) {
			@NonNull Element elem = exec.pop();
			if (elem instanceof LBracketElement) {
				Collections.reverse(list);
				return list;
			}
			else {
				list.add(elem);
			}
		}
		throw new IllegalArgumentException(String.format("LBracket element did not exist on stack!"));
	}
	
	protected static class AssignmentOpPair extends Pair<Def, @NonNull Element> {
		
		public AssignmentOpPair(Def left, @NonNull Element right) {
			super(left, right);
		}
	}
	
	protected static AssignmentOpPair assignmentOpElems(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.peek();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Assignment operator \"%s\" requires label element as first argument!", token.getText()));
		}
		
		LabelElement label = (LabelElement) elem0;
		Def def = label.getDef();
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		return new AssignmentOpPair(def, elem1);
	}
	
	protected static class ElementPair extends Pair<@NonNull Element, @NonNull Element> {
		
		public ElementPair(@NonNull Element left, @NonNull Element right) {
			super(left, right);
		}
	}
	
	protected static ElementPair binaryOpElems(TokenExecutor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return new ElementPair(elem0, elem1);
	}
}
