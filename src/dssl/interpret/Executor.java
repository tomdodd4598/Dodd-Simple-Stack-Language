package dssl.interpret;

import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.*;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.Helpers.Pair;
import dssl.interpret.def.*;
import dssl.interpret.element.*;
import dssl.interpret.element.bracket.*;
import dssl.interpret.element.collection.*;
import dssl.interpret.element.dict.DictElement;
import dssl.interpret.element.magic.MagicLabelElement;
import dssl.interpret.element.range.RangeElement;
import dssl.interpret.element.value.IterableElement;
import dssl.interpret.element.value.primitive.*;
import dssl.lexer.Lexer;
import dssl.node.*;

public class Executor extends Interpreter {
	
	protected final Executor root;
	protected boolean halt = false;
	
	protected final Deque<@NonNull Element> elemStack;
	protected final List<String> printList = new ArrayList<>();
	
	protected final boolean debugMode;
	
	public Executor(Lexer lexer, boolean debugMode) {
		this(new LexerIterator(lexer), debugMode);
	}
	
	public Executor(Iterator<Token> iterator, boolean debugMode) {
		super(iterator, null);
		root = this;
		elemStack = new ArrayDeque<>();
		this.debugMode = debugMode;
	}
	
	public Executor(Lexer lexer, Executor previous) {
		this(new LexerIterator(lexer), previous);
	}
	
	public Executor(Iterator<Token> iterator, Executor previous) {
		super(iterator, previous);
		root = previous.root;
		elemStack = root.elemStack;
		debugMode = root.debugMode;
	}
	
	public void setup() {
		
	}
	
	@Override
	public InterpretResult interpret() {
		loop: while (iterator.hasNext()) {
			InterpretResult readResult = read(iterator.next());
			switch (readResult) {
				case PASS:
					continue loop;
				case CONTINUE:
				case BREAK:
					if (this == root) {
						throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used in the root scope!", readResult));
					}
				default:
					return readResult;
			}
		}
		return InterpretResult.PASS;
	}
	
	@Override
	protected InterpretResult read(Token token) {
		if (root.halt) {
			return InterpretResult.QUIT;
		}
		InterpretResult result = TOKEN_FUNCTION_MAP.apply(this, token);
		if (debugMode && !(token instanceof TBlank || token instanceof TComment)) {
			System.err.printf("%s -> %s\n", token.getText(), elemStackDebugString());
		}
		
		for (String str : printList) {
			// TODO: Custom output stream
			System.out.print(str);
		}
		printList.clear();
		
		return result;
	}
	
	public void push(@NonNull Element elem) {
		elemStack.push(elem);
	}
	
	public @NonNull Element peek() {
		Element peek = Helpers.nullable(elemStack.peek());
		if (peek == null) {
			throw new NoSuchElementException();
		}
		return peek;
	}
	
	public @NonNull Element[] peek(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		Iterator<@NonNull Element> iterator = elemStack.iterator();
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
		Iterator<@NonNull Element> iterator = elemStack.iterator();
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
		return elemStack.pop();
	}
	
	@SuppressWarnings("null")
	public @NonNull Element[] pop(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		for (int i = 0; i < count; ++i) {
			elems[count - i - 1] = elemStack.pop();
		}
		return elems;
	}
	
	protected static final Collector<CharSequence, ?, String> SPACE_JOIN_COLLECTOR;
	
	static {
		SPACE_JOIN_COLLECTOR = Collectors.joining(" ");
	}
	
	protected String elemStackDebugString() {
		return StreamSupport.stream(Spliterators.spliterator(elemStack.descendingIterator(), elemStack.size(), Spliterator.ORDERED), false).map(elem -> elem.toBriefDebugString()).collect(SPACE_JOIN_COLLECTOR);
	}
	
	@FunctionalInterface
	protected static interface ExecutorTokenFunction {
		
		InterpretResult apply(Executor exec, Token token);
	}
	
	protected static class TokenFunctionMap {
		
		protected final Map<Class<? extends Token>, ExecutorTokenFunction> internalMap = new HashMap<>();
		
		protected <T extends Token> void put(Class<T> clazz, ExecutorTokenFunction function) {
			internalMap.put(clazz, function);
		}
		
		protected <T extends Token> ExecutorTokenFunction get(Class<T> clazz) {
			return (ExecutorTokenFunction) internalMap.get(clazz);
		}
		
		protected <T extends Token> InterpretResult apply(Executor exec, T token) {
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
		TOKEN_FUNCTION_MAP.put(TBlank.class, Executor::onBlank);
		TOKEN_FUNCTION_MAP.put(TComment.class, Executor::onComment);
		
		TOKEN_FUNCTION_MAP.put(TLBrace.class, Executor::onLBrace);
		TOKEN_FUNCTION_MAP.put(TRBrace.class, Executor::onRBrace);
		
		TOKEN_FUNCTION_MAP.put(TLBracket.class, Executor::onLBracket);
		TOKEN_FUNCTION_MAP.put(TRBracket.class, Executor::onRBracket);
		
		TOKEN_FUNCTION_MAP.put(TImport.class, Executor::onImport);
		TOKEN_FUNCTION_MAP.put(TNative.class, Executor::onNative);
		
		TOKEN_FUNCTION_MAP.put(TDef.class, Executor::onDef);
		
		TOKEN_FUNCTION_MAP.put(TClass.class, Executor::onClass);
		TOKEN_FUNCTION_MAP.put(TThis.class, Executor::onThis);
		
		TOKEN_FUNCTION_MAP.put(TExch.class, Executor::onExch);
		TOKEN_FUNCTION_MAP.put(TPop.class, Executor::onPop);
		TOKEN_FUNCTION_MAP.put(TDup.class, Executor::onDup);
		TOKEN_FUNCTION_MAP.put(TClone.class, Executor::onClone);
		
		TOKEN_FUNCTION_MAP.put(TRoll.class, Executor::onRoll);
		TOKEN_FUNCTION_MAP.put(TRid.class, Executor::onRid);
		TOKEN_FUNCTION_MAP.put(TCopy.class, Executor::onCopy);
		
		TOKEN_FUNCTION_MAP.put(TIndex.class, Executor::onIndex);
		TOKEN_FUNCTION_MAP.put(TCount.class, Executor::onCount);
		TOKEN_FUNCTION_MAP.put(TCountto.class, Executor::onCountto);
		
		TOKEN_FUNCTION_MAP.put(TRead.class, Executor::onRead);
		TOKEN_FUNCTION_MAP.put(TPrint.class, Executor::onPrint);
		TOKEN_FUNCTION_MAP.put(TPrintln.class, Executor::onPrintln);
		TOKEN_FUNCTION_MAP.put(TInterpret.class, Executor::onInterpret);
		
		TOKEN_FUNCTION_MAP.put(TInt.class, Executor::onInt);
		TOKEN_FUNCTION_MAP.put(TBool.class, Executor::onBool);
		TOKEN_FUNCTION_MAP.put(TFloat.class, Executor::onFloat);
		TOKEN_FUNCTION_MAP.put(TChar.class, Executor::onChar);
		TOKEN_FUNCTION_MAP.put(TString.class, Executor::onString);
		
		TOKEN_FUNCTION_MAP.put(TRange.class, Executor::onRange);
		TOKEN_FUNCTION_MAP.put(TList.class, Executor::onList);
		TOKEN_FUNCTION_MAP.put(TTuple.class, Executor::onTuple);
		TOKEN_FUNCTION_MAP.put(TSet.class, Executor::onSet);
		TOKEN_FUNCTION_MAP.put(TDict.class, Executor::onDict);
		
		TOKEN_FUNCTION_MAP.put(TNull.class, Executor::onNull);
		TOKEN_FUNCTION_MAP.put(THash.class, Executor::onHash);
		
		TOKEN_FUNCTION_MAP.put(TForeach.class, Executor::onForeach);
		TOKEN_FUNCTION_MAP.put(TUnpack.class, Executor::onUnpack);
		
		TOKEN_FUNCTION_MAP.put(TSize.class, Executor::onSize);
		TOKEN_FUNCTION_MAP.put(TEmpty.class, Executor::onEmpty);
		
		TOKEN_FUNCTION_MAP.put(THas.class, Executor::onHas);
		TOKEN_FUNCTION_MAP.put(TAdd.class, Executor::onAdd);
		TOKEN_FUNCTION_MAP.put(TRem.class, Executor::onRem);
		TOKEN_FUNCTION_MAP.put(THasall.class, Executor::onHasall);
		TOKEN_FUNCTION_MAP.put(TAddall.class, Executor::onAddall);
		TOKEN_FUNCTION_MAP.put(TRemall.class, Executor::onRemall);
		TOKEN_FUNCTION_MAP.put(TClear.class, Executor::onClear);
		
		TOKEN_FUNCTION_MAP.put(TGet.class, Executor::onGet);
		TOKEN_FUNCTION_MAP.put(TPut.class, Executor::onPut);
		TOKEN_FUNCTION_MAP.put(TPutall.class, Executor::onPutall);
		
		TOKEN_FUNCTION_MAP.put(THaskey.class, Executor::onHaskey);
		TOKEN_FUNCTION_MAP.put(THasvalue.class, Executor::onHasvalue);
		TOKEN_FUNCTION_MAP.put(THasentry.class, Executor::onHasentry);
		TOKEN_FUNCTION_MAP.put(TKeys.class, Executor::onKeys);
		TOKEN_FUNCTION_MAP.put(TValues.class, Executor::onValues);
		TOKEN_FUNCTION_MAP.put(TEntries.class, Executor::onEntries);
		
		TOKEN_FUNCTION_MAP.put(TType.class, Executor::onType);
		TOKEN_FUNCTION_MAP.put(TCast.class, Executor::onCast);
		
		TOKEN_FUNCTION_MAP.put(TExec.class, Executor::onExec);
		TOKEN_FUNCTION_MAP.put(TIf.class, Executor::onIf);
		TOKEN_FUNCTION_MAP.put(TIfelse.class, Executor::onIfelse);
		TOKEN_FUNCTION_MAP.put(TRepeat.class, Executor::onRepeat);
		TOKEN_FUNCTION_MAP.put(TLoop.class, Executor::onLoop);
		
		TOKEN_FUNCTION_MAP.put(TQuit.class, Executor::onQuit);
		TOKEN_FUNCTION_MAP.put(TContinue.class, Executor::onContinue);
		TOKEN_FUNCTION_MAP.put(TBreak.class, Executor::onBreak);
		
		TOKEN_FUNCTION_MAP.put(TEquals.class, Executor::onEquals);
		
		TOKEN_FUNCTION_MAP.put(TIncrement.class, Executor::onIncrement);
		TOKEN_FUNCTION_MAP.put(TDecrement.class, Executor::onDecrement);
		
		TOKEN_FUNCTION_MAP.put(TPlusEquals.class, Executor::onPlusEquals);
		TOKEN_FUNCTION_MAP.put(TAndEquals.class, Executor::onAndEquals);
		TOKEN_FUNCTION_MAP.put(TOrEquals.class, Executor::onOrEquals);
		TOKEN_FUNCTION_MAP.put(TXorEquals.class, Executor::onXorEquals);
		TOKEN_FUNCTION_MAP.put(TMinusEquals.class, Executor::onMinusEquals);
		TOKEN_FUNCTION_MAP.put(TConcatEquals.class, Executor::onConcatEquals);
		
		TOKEN_FUNCTION_MAP.put(TLeftShiftEquals.class, Executor::onLeftShiftEquals);
		TOKEN_FUNCTION_MAP.put(TRightShiftEquals.class, Executor::onRightShiftEquals);
		
		TOKEN_FUNCTION_MAP.put(TMultiplyEquals.class, Executor::onMultiplyEquals);
		TOKEN_FUNCTION_MAP.put(TDivideEquals.class, Executor::onDivideEquals);
		TOKEN_FUNCTION_MAP.put(TRemainderEquals.class, Executor::onRemainderEquals);
		TOKEN_FUNCTION_MAP.put(TPowerEquals.class, Executor::onPowerEquals);
		TOKEN_FUNCTION_MAP.put(TIdivideEquals.class, Executor::onIdivideEquals);
		TOKEN_FUNCTION_MAP.put(TModuloEquals.class, Executor::onModuloEquals);
		
		TOKEN_FUNCTION_MAP.put(TEqualTo.class, Executor::onEqualTo);
		TOKEN_FUNCTION_MAP.put(TNotEqualTo.class, Executor::onNotEqualTo);
		
		TOKEN_FUNCTION_MAP.put(TLessThan.class, Executor::onLessThan);
		TOKEN_FUNCTION_MAP.put(TLessOrEqual.class, Executor::onLessOrEqual);
		TOKEN_FUNCTION_MAP.put(TMoreThan.class, Executor::onMoreThan);
		TOKEN_FUNCTION_MAP.put(TMoreOrEqual.class, Executor::onMoreOrEqual);
		
		TOKEN_FUNCTION_MAP.put(TPlus.class, Executor::onPlus);
		TOKEN_FUNCTION_MAP.put(TAnd.class, Executor::onAnd);
		TOKEN_FUNCTION_MAP.put(TOr.class, Executor::onOr);
		TOKEN_FUNCTION_MAP.put(TXor.class, Executor::onXor);
		TOKEN_FUNCTION_MAP.put(TMinus.class, Executor::onMinus);
		TOKEN_FUNCTION_MAP.put(TConcat.class, Executor::onConcat);
		
		TOKEN_FUNCTION_MAP.put(TLeftShift.class, Executor::onLeftShift);
		TOKEN_FUNCTION_MAP.put(TRightShift.class, Executor::onRightShift);
		
		TOKEN_FUNCTION_MAP.put(TMultiply.class, Executor::onMultiply);
		TOKEN_FUNCTION_MAP.put(TDivide.class, Executor::onDivide);
		TOKEN_FUNCTION_MAP.put(TRemainder.class, Executor::onRemainder);
		TOKEN_FUNCTION_MAP.put(TPower.class, Executor::onPower);
		TOKEN_FUNCTION_MAP.put(TIdivide.class, Executor::onIdivide);
		TOKEN_FUNCTION_MAP.put(TModulo.class, Executor::onModulo);
		
		TOKEN_FUNCTION_MAP.put(TNot.class, Executor::onNot);
		TOKEN_FUNCTION_MAP.put(TNeg.class, Executor::onNeg);
		// TOKEN_FUNCTION_MAP.put(TInv.class, Executor::onInv);
		
		TOKEN_FUNCTION_MAP.put(TIntValue.class, Executor::onIntValue);
		TOKEN_FUNCTION_MAP.put(TBoolValue.class, Executor::onBoolValue);
		TOKEN_FUNCTION_MAP.put(TFloatValue.class, Executor::onFloatValue);
		TOKEN_FUNCTION_MAP.put(TCharValue.class, Executor::onCharValue);
		TOKEN_FUNCTION_MAP.put(TStringValue.class, Executor::onStringValue);
		
		TOKEN_FUNCTION_MAP.put(TLabel.class, Executor::onLabel);
		TOKEN_FUNCTION_MAP.put(TIdentifier.class, Executor::onIdentifier);
		TOKEN_FUNCTION_MAP.put(TMember.class, Executor::onMember);
		
		TOKEN_FUNCTION_MAP.put(TMagicLabel.class, Executor::onMagicLabel);
	}
	
	protected static InterpretResult onBlank(Executor exec, Token token) {
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onComment(Executor exec, Token token) {
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onLBrace(Executor exec, Token token) {
		ScopeCollector collector = new ScopeCollector(exec.iterator);
		collector.interpret();
		exec.push(new FunctionElement(collector.tokens));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRBrace(Executor exec, Token token) {
		throw new IllegalArgumentException(String.format("Encountered \"}\" token without corresponding \"{\" token!"));
	}
	
	protected static InterpretResult onLBracket(Executor exec, Token token) {
		exec.push(new LBracketElement());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRBracket(Executor exec, Token token) {
		exec.push(new RBracketElement());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onDef(Executor exec, Token token) {
		return assign(exec, true);
	}
	
	protected static InterpretResult onExch(Executor exec, Token token) {
		@NonNull Element[] elems = exec.pop(2);
		exec.push(elems[1]);
		exec.push(elems[0]);
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onPop(Executor exec, Token token) {
		exec.pop();
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onDup(Executor exec, Token token) {
		exec.push(exec.peek());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onClone(Executor exec, Token token) {
		exec.push(exec.peek().clone());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRoll(Executor exec, Token token) {
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
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRid(Executor exec, Token token) {
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
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onCopy(Executor exec, Token token) {
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
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onIndex(Executor exec, Token token) {
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
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onCount(Executor exec, Token token) {
		exec.push(new IntElement(exec.elemStack.size()));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onCountto(Executor exec, Token token) {
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
		exec.push(new IntElement(countElemsToLabel(exec, ((LabelElement) elem).identifier, includeLabel)));
		return InterpretResult.PASS;
	}
	
	// TODO: Custom input stream
	protected static InterpretResult onRead(Executor exec, Token token) {
		exec.push(new StringElement(Helpers.readLine()));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onPrint(Executor exec, Token token) {
		exec.printList.add(exec.pop().toString());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onPrintln(Executor exec, Token token) {
		exec.printList.add(exec.pop().toString() + "\n");
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onInterpret(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		StringElement stringElem = elem.stringCastImplicit();
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"interpret\" requires string value element as argument!"));
		}
		
		return new Executor(new Lexer(Helpers.getPushbackReader(new StringReader(stringElem.toString()))), exec).interpret();
	}
	
	protected static InterpretResult onInt(Executor exec, Token token) {
		exec.push(exec.pop().intCastExplicit());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onBool(Executor exec, Token token) {
		exec.push(exec.pop().boolCastExplicit());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onFloat(Executor exec, Token token) {
		exec.push(exec.pop().floatCastExplicit());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onChar(Executor exec, Token token) {
		exec.push(exec.pop().charCastExplicit());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onString(Executor exec, Token token) {
		exec.push(exec.pop().stringCastExplicit());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRange(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new RangeElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.rangeCastExplicit());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onList(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new ListElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.listCastExplicit());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onTuple(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new TupleElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.tupleCastExplicit());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onSet(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new SetElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.setCastExplicit());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onDict(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (elem instanceof RBracketElement) {
			exec.push(new DictElement(getElemsToLBracket(exec)));
		}
		else {
			exec.push(elem.dictCastExplicit());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onNull(Executor exec, Token token) {
		exec.push(NullElement.INSTANCE);
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onHash(Executor exec, Token token) {
		exec.push(new IntElement(exec.pop().hashCode()));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onForeach(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires iterable element as first argument!"));
		}
		
		if (!(elem1 instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires function element as second argument!"));
		}
		loop: for (@NonNull Element elem : (IterableElement) elem0) {
			exec.push(elem);
			InterpretResult invokeResult = ((FunctionElement) elem1).invoke(exec);
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return InterpretResult.QUIT;
				default:
					break;
			}
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onUnpack(Executor exec, Token token) {
		return exec.pop().onUnpack(exec);
	}
	
	protected static InterpretResult onSize(Executor exec, Token token) {
		return exec.pop().onSize(exec);
	}
	
	protected static InterpretResult onEmpty(Executor exec, Token token) {
		return exec.pop().onEmpty(exec);
	}
	
	protected static InterpretResult onHas(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onHas(exec, elem1);
	}
	
	protected static InterpretResult onAdd(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAdd(exec, elem1);
	}
	
	protected static InterpretResult onRem(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRem(exec, elem1);
	}
	
	protected static InterpretResult onHasall(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onHasall(exec, elem1);
	}
	
	protected static InterpretResult onAddall(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onAddall(exec, elem1);
	}
	
	protected static InterpretResult onRemall(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onRemall(exec, elem1);
	}
	
	protected static InterpretResult onClear(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		return elem.onClear(exec);
	}
	
	protected static InterpretResult onGet(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onGet(exec, elem1);
	}
	
	protected static InterpretResult onPut(Executor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPut(exec, elem1, elem2);
	}
	
	protected static InterpretResult onPutall(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		return elem0.onPutall(exec, elem1);
	}
	
	protected static InterpretResult onHaskey(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"haskey\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHaskey(exec, elem1);
	}
	
	protected static InterpretResult onHasvalue(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasvalue\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHasvalue(exec, elem1);
	}
	
	protected static InterpretResult onHasentry(Executor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		if (!(elem0 instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"hasentry\" requires dict element as argument!"));
		}
		return ((DictElement) elem0).onHasentry(exec, elem1, elem2);
	}
	
	protected static InterpretResult onKeys(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"keyset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onKeys(exec);
	}
	
	protected static InterpretResult onValues(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"values\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onValues(exec);
	}
	
	protected static InterpretResult onEntries(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof DictElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"entryset\" requires dict element as argument!"));
		}
		return ((DictElement) elem).onEntries(exec);
	}
	
	protected static InterpretResult onType(Executor exec, Token token) {
		exec.push(new TypeElement(exec.pop()));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onCast(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof TypeElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"cast\" requires type element as argument!"));
		}
		exec.push(((TypeElement) elem).internal.cast(exec.pop()));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onExec(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"exec\" requires function element as argument!"));
		}
		return ((FunctionElement) elem).invoke(exec);
	}
	
	protected static InterpretResult onIf(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		BoolElement boolElem = elem0.boolCastImplicit();
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires bool value element as first argument!"));
		}
		
		if (!(elem1 instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires function element as second argument!"));
		}
		if (boolElem.primitiveBool()) {
			return ((FunctionElement) elem1).invoke(exec);
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onIfelse(Executor exec, Token token) {
		@NonNull Element elem2 = exec.pop(), elem1 = exec.pop(), elem0 = exec.pop();
		BoolElement boolElem = elem0.boolCastImplicit();
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires bool value element as first argument!"));
		}
		
		if (!(elem1 instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires function element as second argument!"));
		}
		else if (!(elem2 instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires function element as third argument!"));
		}
		
		if (boolElem.primitiveBool()) {
			return ((FunctionElement) elem1).invoke(exec);
		}
		else {
			return ((FunctionElement) elem2).invoke(exec);
		}
	}
	
	protected static InterpretResult onRepeat(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
		IntElement intElem = elem0.intCastImplicit();
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative int value element as first argument!"));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative int value element as first argument!"));
		}
		
		if (!(elem1 instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires function element as second argument!"));
		}
		loop: for (int i = 0; i < primitiveInt; ++i) {
			InterpretResult invokeResult = ((FunctionElement) elem1).invoke(exec);
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return InterpretResult.QUIT;
				default:
					break;
			}
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onLoop(Executor exec, Token token) {
		@NonNull Element elem = exec.pop();
		if (!(elem instanceof FunctionElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"loop\" requires function element as second argument!"));
		}
		loop: while (true) {
			InterpretResult invokeResult = ((FunctionElement) elem).invoke(exec);
			switch (invokeResult) {
				case CONTINUE:
					continue;
				case BREAK:
					break loop;
				case QUIT:
					return InterpretResult.QUIT;
				default:
					break;
			}
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onQuit(Executor exec, Token token) {
		exec.root.halt = true;
		return InterpretResult.QUIT;
	}
	
	protected static InterpretResult onContinue(Executor exec, Token token) {
		return InterpretResult.CONTINUE;
	}
	
	protected static InterpretResult onBreak(Executor exec, Token token) {
		return InterpretResult.BREAK;
	}
	
	protected static InterpretResult onEquals(Executor exec, Token token) {
		return assign(exec, false);
	}
	
	protected static InterpretResult onIncrement(Executor exec, Token token) {
		@NonNull Element elem = exec.peek();
		Def<?> def;
		if (!(elem instanceof LabelElement) || !((def = exec.getDef(((LabelElement) elem).identifier)) instanceof VariableDef)) {
			throw new IllegalArgumentException(String.format("Increment operator \"++\" requires variable label element as argument!"));
		}
		exec.push(((VariableDef) def).getElement().onPlus(new IntElement(1)));
		return assign(exec, false);
	}
	
	protected static InterpretResult onDecrement(Executor exec, Token token) {
		@NonNull Element elem = exec.peek();
		Def<?> def;
		if (!(elem instanceof LabelElement) || !((def = exec.getDef(((LabelElement) elem).identifier)) instanceof VariableDef)) {
			throw new IllegalArgumentException(String.format("Decrement operator \"--\" requires variable label element as argument!"));
		}
		exec.push(((VariableDef) def).getElement().onMinus(new IntElement(1)));
		return assign(exec, false);
	}
	
	protected static InterpretResult onPlusEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onPlus(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onAndEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onAnd(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onOrEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onOr(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onXorEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onXor(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onMinusEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onMinus(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onConcatEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onConcat(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onLeftShiftEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onArithmeticLeftShift(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onRightShiftEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onArithmeticRightShift(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onMultiplyEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onMultiply(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onDivideEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onDivide(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onRemainderEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onRemainder(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onPowerEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onPower(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onIdivideEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onIdivide(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onModuloEquals(Executor exec, Token token) {
		AssignmentOpPair elems = assignmentOpElems(exec, token);
		exec.push(elems.left.getElement().onModulo(elems.right));
		return assign(exec, false);
	}
	
	protected static InterpretResult onEqualTo(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onEqualTo(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onNotEqualTo(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onNotEqualTo(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onLessThan(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onLessThan(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onLessOrEqual(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onLessOrEqual(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onMoreThan(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMoreThan(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onMoreOrEqual(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMoreOrEqual(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onPlus(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onPlus(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onAnd(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onAnd(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onOr(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onOr(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onXor(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onXor(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onMinus(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMinus(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onConcat(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onConcat(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onLeftShift(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onArithmeticLeftShift(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRightShift(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onArithmeticRightShift(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onMultiply(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onMultiply(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onDivide(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onDivide(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onRemainder(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onRemainder(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onPower(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onPower(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onIdivide(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onIdivide(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onModulo(Executor exec, Token token) {
		ElementPair elems = binaryOpElems(exec, token);
		exec.push(elems.left.onModulo(elems.right));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onNot(Executor exec, Token token) {
		exec.push(exec.pop().onNot());
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onNeg(Executor exec, Token token) {
		exec.push(exec.pop().onNeg());
		return InterpretResult.PASS;
	}
	
	/*protected static InterpretResult onInv(Executor exec, Token token) {
		exec.push(exec.pop().onInv());
		return InterpretResult.PASS;
	}*/
	
	protected static InterpretResult onIntValue(Executor exec, Token token) {
		exec.push(new IntElement(new BigInteger(token.getText())));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onBoolValue(Executor exec, Token token) {
		exec.push(new BoolElement(Boolean.parseBoolean(token.getText())));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onFloatValue(Executor exec, Token token) {
		exec.push(new FloatElement(Double.parseDouble(token.getText())));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onCharValue(Executor exec, Token token) {
		exec.push(new CharElement(Helpers.parseChar(token.getText())));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onStringValue(Executor exec, Token token) {
		exec.push(new StringElement(Helpers.parseString(token.getText())));
		return InterpretResult.PASS;
	}
	
	@SuppressWarnings("null")
	protected static InterpretResult onLabel(Executor exec, Token token) {
		exec.push(new LabelElement(token.getText().substring(1)));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onIdentifier(Executor exec, Token token) {
		String identifier = token.getText();
		Def<?> def = exec.getDef(identifier);
		if (def == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", identifier));
		}
		else {
			exec.push(def.getElement());
		}
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult onMagicLabel(Executor exec, Token token) {
		exec.push(new MagicLabelElement(token.getText().substring(1)));
		return InterpretResult.PASS;
	}
	
	protected static InterpretResult assign(Executor exec, boolean shadow) {
		@NonNull Element[] elems = exec.pop(2);
		if (!(elems[0] instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"def\" requires label element as first argument!"));
		}
		
		LabelElement def = (LabelElement) elems[0];
		if (!shadow && exec.getDef(def.identifier) == null) {
			throw new IllegalArgumentException(String.format("Variable \"%s\" not defined!", def.identifier));
		}
		
		if (elems[1] instanceof FunctionElement) {
			exec.setFunction(def.identifier, ((FunctionElement) elems[1]).tokens, shadow);
		}
		else {
			exec.setVariable(def.identifier, elems[1], shadow);
		}
		return InterpretResult.PASS;
	}
	
	protected static int countElemsToLabel(Executor exec, String identifier, boolean includeLabel) {
		Iterator<@NonNull Element> iter = exec.elemStack.iterator();
		int count = 0;
		
		while (iter.hasNext()) {
			@SuppressWarnings("null") @NonNull Element elem = iter.next();
			if (elem instanceof LabelElement && ((LabelElement) elem).identifier.equals(identifier)) {
				return includeLabel ? count + 1 : count;
			}
			else {
				++count;
			}
		}
		throw new IllegalArgumentException(String.format("Label \"/%s\" did not exist on stack!", identifier));
	}
	
	protected static List<@NonNull Element> getElemsToLBracket(Executor exec) {
		List<@NonNull Element> list = new ArrayList<>();
		
		while (!exec.elemStack.isEmpty()) {
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
	
	protected static class AssignmentOpPair extends Pair<VariableDef, @NonNull Element> {
		
		public AssignmentOpPair(VariableDef left, @NonNull Element right) {
			super(left, right);
		}
	}
	
	protected static AssignmentOpPair assignmentOpElems(Executor exec, Token token) {
		@NonNull Element elem1 = exec.pop(), elem0 = exec.peek();
		Def<?> def;
		if (!(elem0 instanceof LabelElement) || !((def = exec.getDef(((LabelElement) elem0).identifier)) instanceof VariableDef)) {
			throw new IllegalArgumentException(String.format("Assignment operator \"%s\" requires variable label element as first argument!", token.getText()));
		}
		return new AssignmentOpPair((VariableDef) def, elem1);
	}
	
	protected static class ElementPair extends Pair<@NonNull Element, @NonNull Element> {
		
		public ElementPair(@NonNull Element left, @NonNull Element right) {
			super(left, right);
		}
	}
	
	protected static ElementPair binaryOpElems(Executor exec, Token token) {
		@NonNull Element[] elems = exec.pop(2);
		return new ElementPair(elems[0], elems[1]);
	}
}
