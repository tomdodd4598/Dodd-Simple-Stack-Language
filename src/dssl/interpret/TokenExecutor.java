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
	
	protected final Hierarchy<@NonNull String, Def> defHierarchy;
	protected final Hierarchy<@NonNull String, Clazz> clazzHierarchy;
	protected final Map<@NonNull String, Magic> magicMap = new HashMap<>();
	
	protected TokenExecutor(Interpreter interpreter, TokenIterator iterator) {
		super(interpreter, iterator);
		defHierarchy = new Hierarchy<>(null);
		clazzHierarchy = new Hierarchy<>(null);
	}
	
	public TokenExecutor(TokenIterator iterator, TokenExecutor prev) {
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
		if (interpreter.debug && !Helpers.isSeparator(token)) {
			interpreter.io.debug(token.getText().trim().replaceAll("\\s+", " ") + " -> " + elemStackDebugString() + "\n");
		}
		
		for (String str : interpreter.printList) {
			interpreter.io.print(str);
		}
		interpreter.printList.clear();
		
		return result;
	}
	
	@Override
	public Def getDef(@NonNull String identifier) {
		return defHierarchy.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		defHierarchy.put(identifier, new Def(identifier, value), shadow);
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallow) {
		return clazzHierarchy.get(shallow);
	}
	
	@Override
	public void setClazz(@NonNull String shallow, ScopeMaps maps) {
		clazzHierarchy.put(shallow, new Clazz(null, shallow, maps), true);
	}
	
	@Override
	public ScopeMaps getMaps() {
		return new ScopeMaps(defHierarchy.internal, clazzHierarchy.internal, magicMap);
	}
	
	public void push(@NonNull Element elem) {
		interpreter.elemStack.push(elem);
	}
	
	@SuppressWarnings("unused")
	public @NonNull Element peek() {
		@SuppressWarnings("null") Element peek = interpreter.elemStack.peek();
		if (peek == null) {
			throw new NoSuchElementException();
		}
		return peek;
	}
	
	@SuppressWarnings("unused")
	public @NonNull Element[] peek(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		Iterator<@NonNull Element> iter = interpreter.elemStack.iterator();
		int i = 0;
		while (i < count) {
			@SuppressWarnings("null") Element next = iter.next();
			if (next == null) {
				throw new NoSuchElementException();
			}
			else {
				elems[count - ++i] = next;
			}
		}
		return elems;
	}
	
	@SuppressWarnings("unused")
	public @NonNull Element peekAt(int index) {
		@NonNull Element elem;
		Iterator<@NonNull Element> iter = interpreter.elemStack.iterator();
		int i = 0;
		do {
			@SuppressWarnings("null") Element next = iter.next();
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
	
	public int elemStackSize() {
		return interpreter.elemStack.size();
	}
	
	protected String elemStackDebugString() {
		return StreamSupport.stream(Spliterators.spliterator(interpreter.elemStack.descendingIterator(), elemStackSize(), Spliterator.ORDERED), false).map(Element::toDebugString).collect(Helpers.SPACE_JOIN_COLLECTOR);
	}
	
	@FunctionalInterface
	protected static interface ExecutorTokenFunction {
		
		TokenResult apply(TokenExecutor exec, @NonNull Token token);
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
		
		TOKEN_FUNCTION_MAP.put(TImport.class, TokenExecutor::onImport);
		TOKEN_FUNCTION_MAP.put(TNative.class, TokenExecutor::onNative);
		
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
		
		TOKEN_FUNCTION_MAP.put(TContains.class, TokenExecutor::onContains);
		TOKEN_FUNCTION_MAP.put(TAdd.class, TokenExecutor::onAdd);
		TOKEN_FUNCTION_MAP.put(TRemove.class, TokenExecutor::onRemove);
		TOKEN_FUNCTION_MAP.put(TContainsall.class, TokenExecutor::onContainsall);
		TOKEN_FUNCTION_MAP.put(TAddall.class, TokenExecutor::onAddall);
		TOKEN_FUNCTION_MAP.put(TRemoveall.class, TokenExecutor::onRemoveall);
		TOKEN_FUNCTION_MAP.put(TClear.class, TokenExecutor::onClear);
		
		TOKEN_FUNCTION_MAP.put(TGet.class, TokenExecutor::onGet);
		TOKEN_FUNCTION_MAP.put(TPut.class, TokenExecutor::onPut);
		TOKEN_FUNCTION_MAP.put(TPutall.class, TokenExecutor::onPutall);
		
		TOKEN_FUNCTION_MAP.put(TContainskey.class, TokenExecutor::onContainskey);
		TOKEN_FUNCTION_MAP.put(TContainsvalue.class, TokenExecutor::onContainsvalue);
		TOKEN_FUNCTION_MAP.put(TContainsentry.class, TokenExecutor::onContainsentry);
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
		
		TOKEN_FUNCTION_MAP.put(TLineStringValue.class, TokenExecutor::onLineStringValue);
		TOKEN_FUNCTION_MAP.put(TBlockStringValue.class, TokenExecutor::onBlockStringValue);
		
		TOKEN_FUNCTION_MAP.put(TIdentifier.class, TokenExecutor::onIdentifier);
		TOKEN_FUNCTION_MAP.put(TLabel.class, TokenExecutor::onLabel);
		TOKEN_FUNCTION_MAP.put(TMember.class, TokenExecutor::onMember);
		
		TOKEN_FUNCTION_MAP.put(BlockToken.class, TokenExecutor::onBlock);
	}
	
	protected static TokenResult onBlank(TokenExecutor exec, @NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected static TokenResult onComment(TokenExecutor exec, @NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLBrace(TokenExecutor exec, @NonNull Token token) {
		TokenCollector collector = new TokenCollector(exec.interpreter, exec.iterator);
		collector.iterate();
		exec.push(new BlockElement(collector.listStack.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRBrace(TokenExecutor exec, @NonNull Token token) {
		throw new IllegalArgumentException(String.format("Encountered \"}\" token without corresponding \"{\" token!"));
	}
	
	protected static TokenResult onLBracket(TokenExecutor exec, @NonNull Token token) {
		exec.push(new LBracketElement());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRBracket(TokenExecutor exec, @NonNull Token token) {
		exec.push(new RBracketElement());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onImport(TokenExecutor exec, @NonNull Token token) {
		return exec.interpreter.importImpl.onImport(exec);
	}
	
	protected static TokenResult onNative(TokenExecutor exec, @NonNull Token token) {
		return exec.interpreter.nativeImpl.onNative(exec);
	}
	
	protected static TokenResult onDef(TokenExecutor exec, @NonNull Token token) {
		return assign(exec, true);
	}
	
	protected static TokenResult onClass(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires label element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires block element as second argument!"));
		}
		
		TokenExecutor clazzExec = ((BlockElement) elem1).executor(exec);
		((LabelElement) elem0).setClazz(clazzExec.getMaps());
		TokenResult result = clazzExec.iterate();
		return result;
	}
	
	protected static TokenResult onMagic(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onNew(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"new\" requires class element as argument!"));
		}
		return ((ClassElement) elem).instantiate(exec);
	}
	
	protected static TokenResult onExch(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem1);
		exec.push(elem0);
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPop(TokenExecutor exec, @NonNull Token token) {
		exec.pop();
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDup(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.peek());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onClone(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.peek().clone());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRoll(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onRid(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onCopy(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onIndex(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onCount(TokenExecutor exec, @NonNull Token token) {
		exec.push(new IntElement(exec.elemStackSize()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCountto(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"countto\" requires label element as argument!"));
		}
		exec.push(new IntElement(countElemsToLabel(exec, (LabelElement) elem)));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRead(TokenExecutor exec, @NonNull Token token) {
		String str = exec.interpreter.io.read();
		if (str != null) {
			exec.push(new StringElement(str));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPrint(TokenExecutor exec, @NonNull Token token) {
		exec.interpreter.printList.add(exec.pop().toString());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPrintln(TokenExecutor exec, @NonNull Token token) {
		exec.interpreter.printList.add(exec.pop().toString());
		exec.interpreter.printList.add("\n");
		return TokenResult.PASS;
	}
	
	protected static TokenResult onInterpret(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		StringElement stringElem = elem.stringCastImplicit();
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"interpret\" requires string value element as argument!"));
		}
		return new TokenExecutor(new LexerIterator(stringElem.toString()), exec).iterate();
	}
	
	protected static TokenResult onInt(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().intCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBool(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().boolCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onFloat(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().floatCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onChar(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().charCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onString(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().stringCastExplicit());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRange(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new RangeElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.rangeCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onList(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new ListElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.listCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onTuple(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new TupleElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.tupleCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onSet(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new SetElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.setCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDict(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new DictElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.dictCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNull(TokenExecutor exec, @NonNull Token token) {
		exec.push(NullElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected static TokenResult onHash(TokenExecutor exec, @NonNull Token token) {
		exec.push(new IntElement(exec.pop().hashCode()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onForeach(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onUnpack(TokenExecutor exec, @NonNull Token token) {
		return exec.pop().onUnpack(exec);
	}
	
	protected static TokenResult onSize(TokenExecutor exec, @NonNull Token token) {
		return exec.pop().onSize(exec);
	}
	
	protected static TokenResult onEmpty(TokenExecutor exec, @NonNull Token token) {
		return exec.pop().onEmpty(exec);
	}
	
	protected static TokenResult onContains(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onContains(exec, elem1);
	}
	
	protected static TokenResult onAdd(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAdd(exec, elem1);
	}
	
	protected static TokenResult onRemove(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRemove(exec, elem1);
	}
	
	protected static TokenResult onContainsall(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onContainsall(exec, elem1);
	}
	
	protected static TokenResult onAddall(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAddall(exec, elem1);
	}
	
	protected static TokenResult onRemoveall(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRemoveall(exec, elem1);
	}
	
	protected static TokenResult onClear(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		return elem.onClear(exec);
	}
	
	protected static TokenResult onGet(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onGet(exec, elem1);
	}
	
	protected static TokenResult onPut(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPut(exec, elem1, elem2);
	}
	
	protected static TokenResult onPutall(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPutall(exec, elem1);
	}
	
	protected static TokenResult onContainskey(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containskey\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainskey(exec, elem1);
	}
	
	protected static TokenResult onContainsvalue(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsvalue\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainsvalue(exec, elem1);
	}
	
	protected static TokenResult onContainsentry(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsentry\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainsentry(exec, elem1, elem2);
	}
	
	protected static TokenResult onKeys(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"keyset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onKeys(exec);
	}
	
	protected static TokenResult onValues(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"values\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onValues(exec);
	}
	
	protected static TokenResult onEntries(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"entryset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onEntries(exec);
	}
	
	protected static TokenResult onType(TokenExecutor exec, @NonNull Token token) {
		exec.push(new TypeElement(exec.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCast(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof TypeElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"cast\" requires type element as argument!"));
		}
		exec.push(((TypeElement) elem).internal.cast(exec.pop()));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onExec(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"exec\" requires block element as argument!"));
		}
		return ((BlockElement) elem).executor(exec).iterate();
	}
	
	protected static TokenResult onIf(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onIfelse(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onRepeat(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onLoop(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onQuit(TokenExecutor exec, @NonNull Token token) {
		exec.interpreter.halt = true;
		return TokenResult.QUIT;
	}
	
	protected static TokenResult onContinue(TokenExecutor exec, @NonNull Token token) {
		return TokenResult.CONTINUE;
	}
	
	protected static TokenResult onBreak(TokenExecutor exec, @NonNull Token token) {
		return TokenResult.BREAK;
	}
	
	protected static TokenResult onEquals(TokenExecutor exec, @NonNull Token token) {
		return assign(exec, false);
	}
	
	protected static TokenResult onIncrement(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onDecrement(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onPlusEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onPlus(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onAndEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onAnd(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onOrEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onOr(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onXorEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onXor(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onMinusEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onMinus(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onConcatEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onConcat(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onLeftShiftEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onArithmeticLeftShift(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onRightShiftEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onArithmeticRightShift(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onMultiplyEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onMultiply(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onDivideEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onDivide(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onRemainderEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onRemainder(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onPowerEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onPower(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onIdivideEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onIdivide(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onModuloEquals(TokenExecutor exec, @NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.elem.onModulo(elems.right));
		return assign(exec, false);
	}
	
	protected static TokenResult onEqualTo(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		boolean nullLeft = NullElement.INSTANCE.equals(elem0), nullRight = NullElement.INSTANCE.equals(elem1);
		if (nullLeft || nullRight) {
			exec.push(new BoolElement(nullLeft == nullRight));
		}
		else {
			exec.push(elem0.onEqualTo(elem1));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		boolean nullLeft = NullElement.INSTANCE.equals(elem0), nullRight = NullElement.INSTANCE.equals(elem1);
		if (nullLeft || nullRight) {
			exec.push(new BoolElement(nullLeft != nullRight));
		}
		else {
			exec.push(elem0.onNotEqualTo(elem1));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLessThan(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onLessThan(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onLessOrEqual(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMoreThan(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onMoreThan(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onMoreOrEqual(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPlus(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onPlus(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onAnd(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onAnd(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onOr(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onOr(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onXor(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onXor(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMinus(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onMinus(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onConcat(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onConcat(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLeftShift(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onArithmeticLeftShift(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRightShift(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onArithmeticRightShift(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMultiply(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onMultiply(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDivide(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onDivide(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onRemainder(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onRemainder(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onPower(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onPower(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIdivide(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onIdivide(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onModulo(TokenExecutor exec, @NonNull Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		exec.push(elem0.onModulo(elem1));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNot(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().onNot());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onNeg(TokenExecutor exec, @NonNull Token token) {
		exec.push(exec.pop().onNeg());
		return TokenResult.PASS;
	}
	
	protected static TokenResult onDeref(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static TokenResult onIntValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new IntElement(new BigInteger(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBoolValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new BoolElement(Boolean.parseBoolean(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onFloatValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new FloatElement(Double.parseDouble(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onCharValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new CharElement(Helpers.parseChar(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onLineStringValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new StringElement(Helpers.parseLineString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBlockStringValue(TokenExecutor exec, @NonNull Token token) {
		exec.push(new StringElement(Helpers.parseBlockString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onIdentifier(TokenExecutor exec, @NonNull Token token) {
		@SuppressWarnings("null") @NonNull String identifier = token.getText();
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
	
	protected static TokenResult onLabel(TokenExecutor exec, @NonNull Token token) {
		@SuppressWarnings("null") @NonNull String label = token.getText().substring(1);
		if (Helpers.KEYWORDS.contains(label)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a label!", label));
		}
		exec.push(new LabelElement(exec, label));
		return TokenResult.PASS;
	}
	
	protected static TokenResult onMember(TokenExecutor exec, @NonNull Token token) {
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
					throw new IllegalArgumentException(String.format("Instance member \"%s\" not defined!", Helpers.memberString(clazz.identifier, member)));
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
				throw new IllegalArgumentException(String.format("Class member \"%s\" not defined!", Helpers.memberString(clazz.identifier, member)));
			}
		}
		else {
			throw new IllegalArgumentException(String.format("Member access \".%s\" requires label, instance or class element as first argument!", member));
		}
		return TokenResult.PASS;
	}
	
	protected static TokenResult onBlock(TokenExecutor exec, @NonNull Token token) {
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
	
	protected static int countElemsToLabel(TokenExecutor exec, LabelElement label) {
		Iterator<@NonNull Element> iter = exec.interpreter.elemStack.iterator();
		int count = 0;
		
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (label.equals(elem)) {
				return count;
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
	
	protected static AssignmentOpPair assignmentOpElems(TokenExecutor exec, @NonNull Token token) {
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
}
