package dssl.interpret;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.*;

import dssl.*;
import dssl.Helpers.Pair;
import dssl.interpret.element.*;
import dssl.interpret.element.bracket.*;
import dssl.interpret.element.clazz.*;
import dssl.interpret.element.collection.*;
import dssl.interpret.element.primitive.*;
import dssl.interpret.token.BlockToken;
import dssl.node.*;

public class TokenExecutor extends TokenReader implements HierarchicalScope {
	
	protected final Hierarchy<@NonNull String, Def> defHierarchy;
	protected final Hierarchy<@NonNull String, Macro> macroHierarchy;
	protected final Hierarchy<@NonNull String, Clazz> clazzHierarchy;
	protected final Hierarchy<@NonNull String, Magic> magicHierarchy;
	
	protected TokenExecutor(Interpreter interpreter, TokenIterator iterator) {
		super(interpreter, iterator);
		defHierarchy = new Hierarchy<>();
		macroHierarchy = new Hierarchy<>();
		clazzHierarchy = new Hierarchy<>();
		magicHierarchy = new Hierarchy<>();
	}
	
	public TokenExecutor(TokenIterator iterator, TokenExecutor prev, boolean child) {
		super(iterator, prev);
		defHierarchy = prev.defHierarchy.copy(child);
		macroHierarchy = prev.macroHierarchy.copy(child);
		clazzHierarchy = prev.clazzHierarchy.copy(child);
		magicHierarchy = prev.magicHierarchy.copy(child);
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
					if (isRoot()) {
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
	public String getIdentifier() {
		return null;
	}
	
	@Override
	public Hierarchy<@NonNull String, Def> getDefHierarchy() {
		return defHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Macro> getMacroHierarchy() {
		return macroHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Clazz> getClazzHierarchy() {
		return clazzHierarchy;
	}
	
	@Override
	public Hierarchy<@NonNull String, Magic> getMagicHierarchy() {
		return magicHierarchy;
	}
	
	public boolean isRoot() {
		return this == interpreter.root;
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
		TOKEN_FUNCTION_MAP.put(TMacro.class, TokenExecutor::onMacro);
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
	
	protected TokenResult onBlank(@NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected TokenResult onComment(@NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected TokenResult onLBrace(@NonNull Token token) {
		TokenCollector collector = new TokenCollector(interpreter, iterator);
		collector.iterate();
		push(new BlockElement(collector.listStack.pop()));
		return TokenResult.PASS;
	}
	
	protected TokenResult onRBrace(@NonNull Token token) {
		throw new IllegalArgumentException(String.format("Encountered \"}\" token without corresponding \"{\" token!"));
	}
	
	protected TokenResult onLBracket(@NonNull Token token) {
		push(new LBracketElement());
		return TokenResult.PASS;
	}
	
	protected TokenResult onRBracket(@NonNull Token token) {
		push(new RBracketElement());
		return TokenResult.PASS;
	}
	
	protected TokenResult onImport(@NonNull Token token) {
		return interpreter.importImpl.onImport(this);
	}
	
	protected TokenResult onNative(@NonNull Token token) {
		return interpreter.nativeImpl.onNative(this);
	}
	
	protected TokenResult onDef(@NonNull Token token) {
		return assign(true);
	}
	
	protected TokenResult onMacro(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"macro\" requires label element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"macro\" requires block element as second argument!"));
		}
		
		LabelElement label = (LabelElement) elem0;
		if (Helpers.KEYWORDS.contains(label.identifier)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a macro identifier!", label.identifier));
		}
		label.setMacro((BlockElement) elem1);
		return TokenResult.PASS;
	}
	
	protected TokenResult onClass(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0;
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires block element as last argument!"));
		}
		
		List<@NonNull Clazz> supers = new ArrayList<>();
		while ((elem0 = pop()) instanceof ClassElement) {
			supers.add(((ClassElement) elem0).clazz);
		}
		
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires label element as first argument!"));
		}
		
		LabelElement label = (LabelElement) elem0;
		if (Helpers.KEYWORDS.contains(label.identifier)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a class identifier!", label.identifier));
		}
		TokenExecutor clazzExec = ((BlockElement) elem1).executor(this);
		label.setClazz(clazzExec, supers);
		TokenResult result = clazzExec.iterate();
		return result;
	}
	
	protected TokenResult onMagic(@NonNull Token token) {
		if (isRoot()) {
			throw new IllegalArgumentException(String.format("Keyword \"magic\" can not be used by the root executor!"));
		}
		
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"magic\" requires label element as first argument!"));
		}
		
		((LabelElement) elem0).setMagic((BlockElement) elem1);
		return TokenResult.PASS;
	}
	
	protected TokenResult onNew(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"new\" requires class element as argument!"));
		}
		return ((ClassElement) elem).instantiate(this);
	}
	
	protected TokenResult onExch(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		push(elem1);
		push(elem0);
		return TokenResult.PASS;
	}
	
	protected TokenResult onPop(@NonNull Token token) {
		pop();
		return TokenResult.PASS;
	}
	
	protected TokenResult onDup(@NonNull Token token) {
		push(peek());
		return TokenResult.PASS;
	}
	
	protected TokenResult onClone(@NonNull Token token) {
		push(peek().clone());
		return TokenResult.PASS;
	}
	
	protected TokenResult onRoll(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.intCastImplicit(), intElem1 = elem1.intCastImplicit();
		if (intElem0 == null || intElem1 == null) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires two int value elements as arguments!"));
		}
		
		int count = intElem0.primitiveInt(), roll = intElem1.primitiveInt();
		if (count < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires non-negative int value element as first argument!"));
		}
		
		@NonNull Element[] elems = pop(count);
		for (int i = 0; i < count; ++i) {
			push(elems[Helpers.mod(i - roll, count)]);
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onRid(@NonNull Token token) {
		@NonNull Element elem = pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"rid\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"rid\" requires non-negative int value element as argument!"));
		}
		
		for (int i = 0; i < primitiveInt; ++i) {
			pop();
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onCopy(@NonNull Token token) {
		@NonNull Element elem = pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"copy\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"copy\" requires non-negative int value element as argument!"));
		}
		
		for (@NonNull Element e : peek(primitiveInt)) {
			push(e);
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onIndex(@NonNull Token token) {
		@NonNull Element elem = pop();
		IntElement intElem = elem.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"index\" requires non-negative int value element as argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"index\" requires non-negative int value element as argument!"));
		}
		
		push(peekAt(primitiveInt));
		return TokenResult.PASS;
	}
	
	protected TokenResult onCount(@NonNull Token token) {
		push(new IntElement(elemStackSize()));
		return TokenResult.PASS;
	}
	
	protected TokenResult onCountto(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"countto\" requires label element as argument!"));
		}
		push(new IntElement(countElemsToLabel((LabelElement) elem)));
		return TokenResult.PASS;
	}
	
	protected TokenResult onRead(@NonNull Token token) {
		String str = interpreter.io.read();
		if (str != null) {
			push(new StringElement(str));
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onPrint(@NonNull Token token) {
		interpreter.printList.add(pop().toString());
		return TokenResult.PASS;
	}
	
	protected TokenResult onPrintln(@NonNull Token token) {
		interpreter.printList.add(pop().toString());
		interpreter.printList.add("\n");
		return TokenResult.PASS;
	}
	
	protected TokenResult onInterpret(@NonNull Token token) {
		@NonNull Element elem = pop();
		StringElement stringElem = elem.stringCastImplicit();
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"interpret\" requires string value element as argument!"));
		}
		return new TokenExecutor(new LexerIterator(stringElem.toString()), this, false).iterate();
	}
	
	protected TokenResult onInt(@NonNull Token token) {
		push(pop().intCastExplicit());
		return TokenResult.PASS;
	}
	
	protected TokenResult onBool(@NonNull Token token) {
		push(pop().boolCastExplicit());
		return TokenResult.PASS;
	}
	
	protected TokenResult onFloat(@NonNull Token token) {
		push(pop().floatCastExplicit());
		return TokenResult.PASS;
	}
	
	protected TokenResult onChar(@NonNull Token token) {
		push(pop().charCastExplicit());
		return TokenResult.PASS;
	}
	
	protected TokenResult onString(@NonNull Token token) {
		push(pop().stringCastExplicit());
		return TokenResult.PASS;
	}
	
	protected TokenResult onRange(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (elem instanceof RBracketElement) {
			push(new RangeElement(getElemsToLBracket()));
		}
		else {
			push(elem.rangeCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onList(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (elem instanceof RBracketElement) {
			push(new ListElement(getElemsToLBracket()));
		}
		else {
			push(elem.listCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onTuple(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (elem instanceof RBracketElement) {
			push(new TupleElement(getElemsToLBracket()));
		}
		else {
			push(elem.tupleCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onSet(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (elem instanceof RBracketElement) {
			push(new SetElement(getElemsToLBracket()));
		}
		else {
			push(elem.setCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onDict(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (elem instanceof RBracketElement) {
			push(new DictElement(getElemsToLBracket()));
		}
		else {
			push(elem.dictCastExplicit());
		}
		return TokenResult.PASS;
	}
	
	protected TokenResult onNull(@NonNull Token token) {
		push(NullElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected TokenResult onHash(@NonNull Token token) {
		push(new IntElement(pop().hashCode()));
		return TokenResult.PASS;
	}
	
	protected TokenResult onForeach(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires iterable element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires block element as second argument!"));
		}
		
		loop: for (@NonNull Element elem : (IterableElement) elem0) {
			push(elem);
			TokenResult invokeResult = ((BlockElement) elem1).executor(this).iterate();
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
	
	protected TokenResult onUnpack(@NonNull Token token) {
		return pop().onUnpack(this);
	}
	
	protected TokenResult onSize(@NonNull Token token) {
		return pop().onSize(this);
	}
	
	protected TokenResult onEmpty(@NonNull Token token) {
		return pop().onEmpty(this);
	}
	
	protected TokenResult onContains(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onContains(this, elem1);
	}
	
	protected TokenResult onAdd(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onAdd(this, elem1);
	}
	
	protected TokenResult onRemove(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRemove(this, elem1);
	}
	
	protected TokenResult onContainsall(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onContainsall(this, elem1);
	}
	
	protected TokenResult onAddall(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onAddall(this, elem1);
	}
	
	protected TokenResult onRemoveall(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRemoveall(this, elem1);
	}
	
	protected TokenResult onClear(@NonNull Token token) {
		return pop().onClear(this);
	}
	
	protected TokenResult onGet(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onGet(this, elem1);
	}
	
	protected TokenResult onPut(@NonNull Token token) {
		@NonNull Element elem2 = pop(), elem1 = pop(), elem0 = pop();
		return elem0.onPut(this, elem1, elem2);
	}
	
	protected TokenResult onPutall(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onPutall(this, elem1);
	}
	
	protected TokenResult onContainskey(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containskey\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainskey(this, elem1);
	}
	
	protected TokenResult onContainsvalue(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsvalue\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainsvalue(this, elem1);
	}
	
	protected TokenResult onContainsentry(@NonNull Token token) {
		@NonNull Element elem2 = pop(), elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"containsentry\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onContainsentry(this, elem1, elem2);
	}
	
	protected TokenResult onKeys(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"keyset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onKeys(this);
	}
	
	protected TokenResult onValues(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"values\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onValues(this);
	}
	
	protected TokenResult onEntries(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"entryset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onEntries(this);
	}
	
	protected TokenResult onType(@NonNull Token token) {
		push(new TypeElement(pop()));
		return TokenResult.PASS;
	}
	
	protected TokenResult onCast(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof TypeElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"cast\" requires type element as argument!"));
		}
		push(((TypeElement) elem).internal.cast(pop()));
		return TokenResult.PASS;
	}
	
	protected TokenResult onExec(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"exec\" requires block element as argument!"));
		}
		return ((BlockElement) elem).executor(this).iterate();
	}
	
	protected TokenResult onIf(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		BoolElement boolElem = elem0.boolCastImplicit();
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires bool value element as first argument!"));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires block element as second argument!"));
		}
		
		if (boolElem.primitiveBool()) {
			return ((BlockElement) elem1).executor(this).iterate();
		}
		else {
			return TokenResult.PASS;
		}
	}
	
	protected TokenResult onIfelse(@NonNull Token token) {
		@NonNull Element elem2 = pop(), elem1 = pop(), elem0 = pop();
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
			return ((BlockElement) elem1).executor(this).iterate();
		}
		else {
			return ((BlockElement) elem2).executor(this).iterate();
		}
	}
	
	protected TokenResult onRepeat(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
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
			TokenResult invokeResult = ((BlockElement) elem1).executor(this).iterate();
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
	
	protected TokenResult onLoop(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"loop\" requires block element as second argument!"));
		}
		
		loop: while (true) {
			TokenResult invokeResult = ((BlockElement) elem).executor(this).iterate();
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
	
	protected TokenResult onQuit(@NonNull Token token) {
		interpreter.halt = true;
		return TokenResult.QUIT;
	}
	
	protected TokenResult onContinue(@NonNull Token token) {
		return TokenResult.CONTINUE;
	}
	
	protected TokenResult onBreak(@NonNull Token token) {
		return TokenResult.BREAK;
	}
	
	protected TokenResult onEquals(@NonNull Token token) {
		return assign(false);
	}
	
	protected TokenResult onIncrement(@NonNull Token token) {
		@NonNull Element elem = peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Increment operator \"++\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		return opAssign(def.elem.onPlus(this, new IntElement(1)));
	}
	
	protected TokenResult onDecrement(@NonNull Token token) {
		@NonNull Element elem = peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Decrement operator \"--\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		return opAssign(def.elem.onMinus(this, new IntElement(1)));
	}
	
	protected TokenResult onPlusEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onPlus(this, elems.right));
	}
	
	protected TokenResult onAndEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onAnd(this, elems.right));
	}
	
	protected TokenResult onOrEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onOr(this, elems.right));
	}
	
	protected TokenResult onXorEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onXor(this, elems.right));
	}
	
	protected TokenResult onMinusEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onMinus(this, elems.right));
	}
	
	protected TokenResult onConcatEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onConcat(this, elems.right));
	}
	
	protected TokenResult onLeftShiftEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onLeftShift(this, elems.right));
	}
	
	protected TokenResult onRightShiftEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onRightShift(this, elems.right));
	}
	
	protected TokenResult onMultiplyEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onMultiply(this, elems.right));
	}
	
	protected TokenResult onDivideEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onDivide(this, elems.right));
	}
	
	protected TokenResult onRemainderEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onRemainder(this, elems.right));
	}
	
	protected TokenResult onPowerEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onPower(this, elems.right));
	}
	
	protected TokenResult onIdivideEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onIdivide(this, elems.right));
	}
	
	protected TokenResult onModuloEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.left.elem.onModulo(this, elems.right));
	}
	
	protected TokenResult onEqualTo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onEqualTo(this, elem1);
	}
	
	protected TokenResult onNotEqualTo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onNotEqualTo(this, elem1);
	}
	
	protected TokenResult onLessThan(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLessThan(this, elem1);
	}
	
	protected TokenResult onLessOrEqual(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLessOrEqual(this, elem1);
	}
	
	protected TokenResult onMoreThan(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMoreThan(this, elem1);
	}
	
	protected TokenResult onMoreOrEqual(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMoreOrEqual(this, elem1);
	}
	
	protected TokenResult onPlus(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onPlus(this, elem1);
	}
	
	protected TokenResult onAnd(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onAnd(this, elem1);
	}
	
	protected TokenResult onOr(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onOr(this, elem1);
	}
	
	protected TokenResult onXor(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onXor(this, elem1);
	}
	
	protected TokenResult onMinus(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMinus(this, elem1);
	}
	
	protected TokenResult onConcat(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onConcat(this, elem1);
	}
	
	protected TokenResult onLeftShift(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLeftShift(this, elem1);
	}
	
	protected TokenResult onRightShift(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRightShift(this, elem1);
	}
	
	protected TokenResult onMultiply(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMultiply(this, elem1);
	}
	
	protected TokenResult onDivide(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onDivide(this, elem1);
	}
	
	protected TokenResult onRemainder(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRemainder(this, elem1);
	}
	
	protected TokenResult onPower(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onPower(this, elem1);
	}
	
	protected TokenResult onIdivide(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onIdivide(this, elem1);
	}
	
	protected TokenResult onModulo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onModulo(this, elem1);
	}
	
	protected TokenResult onNot(@NonNull Token token) {
		return pop().onNot(this);
	}
	
	protected TokenResult onNeg(@NonNull Token token) {
		return pop().onNeg(this);
	}
	
	protected TokenResult onDeref(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"deref\" requires label element as argument!"));
		}
		
		LabelElement label = (LabelElement) elem;
		TokenResult result = scopeAction(label::getDef, label::getMacro, label::getClazz);
		if (result == null) {
			throw new IllegalArgumentException(String.format("Variable, macro or class \"%s\" not defined!", label.identifier));
		}
		return result;
	}
	
	protected TokenResult onIntValue(@NonNull Token token) {
		push(new IntElement(new BigInteger(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onBoolValue(@NonNull Token token) {
		push(new BoolElement(Boolean.parseBoolean(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onFloatValue(@NonNull Token token) {
		push(new FloatElement(Double.parseDouble(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onCharValue(@NonNull Token token) {
		push(new CharElement(Helpers.parseChar(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onLineStringValue(@NonNull Token token) {
		push(new StringElement(Helpers.parseLineString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onBlockStringValue(@NonNull Token token) {
		push(new StringElement(Helpers.parseBlockString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected TokenResult onIdentifier(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String identifier = token.getText();
		if (Helpers.KEYWORDS.contains(identifier)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as an identifier!", identifier));
		}
		
		TokenResult result = scopeAction(this, identifier);
		if (result == null) {
			throw new IllegalArgumentException(String.format("Variable, macro or class \"%s\" not defined!", identifier));
		}
		return result;
	}
	
	@SuppressWarnings("null")
	protected TokenResult onLabel(@NonNull Token token) {
		push(new LabelElement(this, token.getText().substring(1)));
		return TokenResult.PASS;
	}
	
	protected TokenResult onMember(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String member = token.getText().substring(1);
		if (Helpers.KEYWORDS.contains(member)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a member!", member));
		}
		
		@NonNull Element elem = pop();
		TokenResult result;
		if (elem instanceof LabelElement) {
			push(((LabelElement) elem).extended(member));
			return TokenResult.PASS;
		}
		else if (elem instanceof InstanceElement) {
			InstanceElement instance = (InstanceElement) elem;
			if ((result = instance.scopeAction(this, member)) == null) {
				throw new IllegalArgumentException(String.format("Instance member \"%s\" not defined!", Helpers.memberString(instance.clazz.identifier, member)));
			}
			return result;
		}
		else if (elem instanceof ClassElement) {
			Clazz clazz = ((ClassElement) elem).clazz;
			if ((result = clazz.scopeAction(this, member)) == null) {
				throw new IllegalArgumentException(String.format("Class member \"%s\" not defined!", Helpers.memberString(clazz.identifier, member)));
			}
			return result;
		}
		else {
			throw new IllegalArgumentException(String.format("Member access \".%s\" requires label, instance or class element as first argument!", member));
		}
	}
	
	protected TokenResult onBlock(@NonNull Token token) {
		push(new BlockElement(((BlockToken) token).tokens));
		return TokenResult.PASS;
	}
	
	protected TokenResult assign(boolean def) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("%s requires label element as first argument!", def ? "Keyword \"def\"" : "Assignment"));
		}
		
		LabelElement label = (LabelElement) elem0;
		if (def) {
			if (Helpers.KEYWORDS.contains(label.identifier)) {
				throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as a variable identifier!", label.identifier));
			}
		}
		else if (label.getDef() == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", label.identifier));
		}
		
		label.setDef(elem1, def);
		return TokenResult.PASS;
	}
	
	protected int countElemsToLabel(LabelElement label) {
		Iterator<@NonNull Element> iter = interpreter.elemStack.iterator();
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
	
	protected List<@NonNull Element> getElemsToLBracket() {
		List<@NonNull Element> list = new ArrayList<>();
		
		while (!interpreter.elemStack.isEmpty()) {
			@NonNull Element elem = pop();
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
	
	protected AssignmentOpPair assignmentOpElems(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = peek();
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
	
	protected TokenResult opAssign(TokenResult opResult) {
		if (opResult.equals(TokenResult.PASS)) {
			return assign(false);
		}
		else {
			return opResult;
		}
	}
	
	public @Nullable TokenResult scopeAction(Supplier<Def> getDef, Supplier<Macro> getMacro, Supplier<Clazz> getClazz) {
		Def def;
		Macro macro;
		Clazz clazz;
		if ((def = getDef.get()) != null) {
			push(def.elem);
			return TokenResult.PASS;
		}
		else if ((macro = getMacro.get()) != null) {
			return macro.block.executor(this).iterate();
		}
		else if ((clazz = getClazz.get()) != null) {
			push(clazz.elem);
			return TokenResult.PASS;
		}
		else {
			return null;
		}
	}
}
