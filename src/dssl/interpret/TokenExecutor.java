package dssl.interpret;

import java.math.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import dssl.*;
import dssl.Helpers.Pair;
import dssl.interpret.element.*;
import dssl.interpret.element.bracket.*;
import dssl.interpret.element.clazz.ClassElement;
import dssl.interpret.element.iter.IterElement;
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
		prelude();
	}
	
	public TokenExecutor(TokenIterator iterator, TokenExecutor prev, boolean child) {
		super(iterator, prev);
		defHierarchy = prev.defHierarchy.copy(child);
		macroHierarchy = prev.macroHierarchy.copy(child);
		clazzHierarchy = prev.clazzHierarchy.copy(child);
		magicHierarchy = prev.magicHierarchy.copy(child);
	}
	
	protected void prelude() {
		BuiltIn.CLAZZ_MAP.forEach((k, v) -> getClazzHierarchy().put(k, v, true));
	}
	
	@Override
	public @NonNull TokenResult iterate() {
		loop: while (iterator.hasNext()) {
			@NonNull TokenResult readResult = read(iterator.next());
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
	protected @NonNull TokenResult read(@NonNull Token token) {
		if (interpreter.halt) {
			return TokenResult.QUIT;
		}
		@NonNull TokenResult result = TOKEN_FUNCTION_MAP.apply(this, token);
		if (interpreter.debug && !Helpers.isSeparator(token)) {
			interpreter.hooks.debug(token.getText().trim().replaceAll("\\s+", " ") + " -> " + debug() + "\n");
		}
		
		for (String str : interpreter.printList) {
			interpreter.hooks.print(str);
		}
		interpreter.printList.clear();
		
		return result;
	}
	
	@Override
	public @Nullable String scopeIdentifier() {
		return null;
	}
	
	@Override
	public void checkCollision(@NonNull String identifier) {
		if (BuiltIn.KEYWORDS.contains(identifier)) {
			throw new IllegalArgumentException(String.format("Identifier \"%s\" already used for keyword!", identifier));
		}
		HierarchicalScope.super.checkCollision(identifier);
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
	
	protected Deque<@NonNull Element> stack() {
		return interpreter.stack;
	}
	
	public void push(@NonNull Element elem) {
		stack().push(elem);
	}
	
	@SuppressWarnings("unused")
	public @NonNull Element peek() {
		@SuppressWarnings("null") Element peek = stack().peek();
		if (peek == null) {
			throw new NoSuchElementException();
		}
		return peek;
	}
	
	@SuppressWarnings("unused")
	public @NonNull Element[] peek(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		Iterator<@NonNull Element> iter = stack().iterator();
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
		Iterator<@NonNull Element> iter = stack().iterator();
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
		return stack().pop();
	}
	
	public @NonNull Element[] pop(int count) {
		@NonNull Element[] elems = new @NonNull Element[count];
		for (int i = 0; i < count; ++i) {
			elems[count - i - 1] = pop();
		}
		return elems;
	}
	
	public int stackSize() {
		return stack().size();
	}
	
	protected String debug() {
		return Helpers.stream(() -> stack().descendingIterator()).map(x -> x.debug(this)).collect(Collectors.joining(" "));
	}
	
	@FunctionalInterface
	protected static interface TokenFunction {
		
		@NonNull
		TokenResult apply(TokenExecutor exec, @NonNull Token token);
	}
	
	protected static class TokenFunctionMap {
		
		protected final Map<Class<? extends Token>, TokenFunction> internal = new HashMap<>();
		
		protected <T extends Token> void put(Class<T> clazz, TokenFunction function) {
			internal.put(clazz, function);
		}
		
		protected <T extends Token> TokenFunction get(Class<T> clazz) {
			return internal.get(clazz);
		}
		
		protected <T extends Token> @NonNull TokenResult apply(TokenExecutor exec, @NonNull T token) {
			Class<T> clazz = (Class<T>) token.getClass();
			TokenFunction function = get(clazz);
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
		
		TOKEN_FUNCTION_MAP.put(TDictLBracket.class, TokenExecutor::onDictLBracket);
		TOKEN_FUNCTION_MAP.put(TDictRBracket.class, TokenExecutor::onDictRBracket);
		
		TOKEN_FUNCTION_MAP.put(TSetLBracket.class, TokenExecutor::onSetLBracket);
		TOKEN_FUNCTION_MAP.put(TSetRBracket.class, TokenExecutor::onSetRBracket);
		
		TOKEN_FUNCTION_MAP.put(TListLBracket.class, TokenExecutor::onListLBracket);
		TOKEN_FUNCTION_MAP.put(TListRBracket.class, TokenExecutor::onListRBracket);
		
		TOKEN_FUNCTION_MAP.put(TRangeLBracket.class, TokenExecutor::onRangeLBracket);
		TOKEN_FUNCTION_MAP.put(TRangeRBracket.class, TokenExecutor::onRangeRBracket);
		
		TOKEN_FUNCTION_MAP.put(TInclude.class, TokenExecutor::onInclude);
		TOKEN_FUNCTION_MAP.put(TImport.class, TokenExecutor::onImport);
		
		TOKEN_FUNCTION_MAP.put(TNative.class, TokenExecutor::onNative);
		
		TOKEN_FUNCTION_MAP.put(TDef.class, TokenExecutor::onDef);
		TOKEN_FUNCTION_MAP.put(TMacro.class, TokenExecutor::onMacro);
		TOKEN_FUNCTION_MAP.put(TClass.class, TokenExecutor::onClass);
		TOKEN_FUNCTION_MAP.put(TMagic.class, TokenExecutor::onMagic);
		
		TOKEN_FUNCTION_MAP.put(TDeref.class, TokenExecutor::onDeref);
		
		TOKEN_FUNCTION_MAP.put(TDelete.class, TokenExecutor::onDelete);
		
		TOKEN_FUNCTION_MAP.put(TNew.class, TokenExecutor::onNew);
		
		TOKEN_FUNCTION_MAP.put(TNull.class, TokenExecutor::onNull);
		TOKEN_FUNCTION_MAP.put(TType.class, TokenExecutor::onType);
		TOKEN_FUNCTION_MAP.put(TCast.class, TokenExecutor::onCast);
		TOKEN_FUNCTION_MAP.put(TIs.class, TokenExecutor::onIs);
		
		TOKEN_FUNCTION_MAP.put(TExch.class, TokenExecutor::onExch);
		TOKEN_FUNCTION_MAP.put(TRoll.class, TokenExecutor::onRoll);
		TOKEN_FUNCTION_MAP.put(TPop.class, TokenExecutor::onPop);
		TOKEN_FUNCTION_MAP.put(TDup.class, TokenExecutor::onDup);
		
		TOKEN_FUNCTION_MAP.put(TStacksize.class, TokenExecutor::onStacksize);
		
		TOKEN_FUNCTION_MAP.put(TRead.class, TokenExecutor::onRead);
		TOKEN_FUNCTION_MAP.put(TPrint.class, TokenExecutor::onPrint);
		TOKEN_FUNCTION_MAP.put(TPrintln.class, TokenExecutor::onPrintln);
		TOKEN_FUNCTION_MAP.put(TInterpret.class, TokenExecutor::onInterpret);
		
		TOKEN_FUNCTION_MAP.put(TExec.class, TokenExecutor::onExec);
		TOKEN_FUNCTION_MAP.put(TIf.class, TokenExecutor::onIf);
		TOKEN_FUNCTION_MAP.put(TIfelse.class, TokenExecutor::onIfelse);
		TOKEN_FUNCTION_MAP.put(TLoop.class, TokenExecutor::onLoop);
		TOKEN_FUNCTION_MAP.put(TRepeat.class, TokenExecutor::onRepeat);
		TOKEN_FUNCTION_MAP.put(TForeach.class, TokenExecutor::onForeach);
		
		TOKEN_FUNCTION_MAP.put(TContinue.class, TokenExecutor::onContinue);
		TOKEN_FUNCTION_MAP.put(TBreak.class, TokenExecutor::onBreak);
		TOKEN_FUNCTION_MAP.put(TQuit.class, TokenExecutor::onQuit);
		
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
		
		TOKEN_FUNCTION_MAP.put(TIntValue.class, TokenExecutor::onIntValue);
		TOKEN_FUNCTION_MAP.put(TBoolValue.class, TokenExecutor::onBoolValue);
		TOKEN_FUNCTION_MAP.put(TFloatValue.class, TokenExecutor::onFloatValue);
		TOKEN_FUNCTION_MAP.put(TCharValue.class, TokenExecutor::onCharValue);
		
		TOKEN_FUNCTION_MAP.put(TBlockStringValue.class, TokenExecutor::onBlockStringValue);
		TOKEN_FUNCTION_MAP.put(TLineStringValue.class, TokenExecutor::onLineStringValue);
		
		TOKEN_FUNCTION_MAP.put(TIdentifier.class, TokenExecutor::onIdentifier);
		TOKEN_FUNCTION_MAP.put(TLabel.class, TokenExecutor::onLabel);
		TOKEN_FUNCTION_MAP.put(TMember.class, TokenExecutor::onMember);
		TOKEN_FUNCTION_MAP.put(TModule.class, TokenExecutor::onModule);
		
		TOKEN_FUNCTION_MAP.put(BlockToken.class, TokenExecutor::onBlock);
	}
	
	// KEYWORDS
	
	protected @NonNull TokenResult onBlank(@NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onComment(@NonNull Token token) {
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onLBrace(@NonNull Token token) {
		TokenCollector collector = new TokenCollector(interpreter, iterator);
		collector.iterate();
		push(new BlockElement(collector.listStack.pop()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onRBrace(@NonNull Token token) {
		throw new IllegalArgumentException(String.format("Encountered \"}\" token without corresponding \"{\" token!"));
	}
	
	protected @NonNull TokenResult onDictLBracket(@NonNull Token token) {
		push(DictLBracketElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onDictRBracket(@NonNull Token token) {
		push(new DictElement(this, getElemsToLBracket(DictLBracketElement.INSTANCE, DictRBracketElement.INSTANCE)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onSetLBracket(@NonNull Token token) {
		push(SetLBracketElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onSetRBracket(@NonNull Token token) {
		push(new SetElement(getElemsToLBracket(SetLBracketElement.INSTANCE, SetRBracketElement.INSTANCE)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onListLBracket(@NonNull Token token) {
		push(ListLBracketElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onListRBracket(@NonNull Token token) {
		push(new ListElement(getElemsToLBracket(ListLBracketElement.INSTANCE, ListRBracketElement.INSTANCE)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onRangeLBracket(@NonNull Token token) {
		push(RangeLBracketElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onRangeRBracket(@NonNull Token token) {
		push(new RangeElement(this, getElemsToLBracket(RangeLBracketElement.INSTANCE, RangeRBracketElement.INSTANCE)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onInclude(@NonNull Token token) {
		return interpreter.hooks.onInclude(this);
	}
	
	protected @NonNull TokenResult onImport(@NonNull Token token) {
		return interpreter.hooks.onImport(this);
	}
	
	protected @NonNull TokenResult onNative(@NonNull Token token) {
		return interpreter.hooks.onNative(this);
	}
	
	protected @NonNull TokenResult onDef(@NonNull Token token) {
		assign(pop(), pop(), AssignmentType.DEF);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onMacro(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"macro\" requires %s element as first argument!", BuiltIn.LABEL));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"macro\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		
		((LabelElement) elem0).setMacro((BlockElement) elem1);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onClass(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0;
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires %s element as last argument!", BuiltIn.BLOCK));
		}
		
		ArrayList<Clazz> supers = new ArrayList<>();
		while ((elem0 = pop()) instanceof ClassElement) {
			supers.add(((ClassElement) elem0).internal);
		}
		
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"class\" requires %s element as first argument!", BuiltIn.LABEL));
		}
		
		LabelElement label = (LabelElement) elem0;
		
		for (Clazz clazz : supers) {
			if (!clazz.type.canExtend()) {
				throw new IllegalArgumentException(String.format("Class \"%s\" can not extend class \"%s\"!", label.fullIdentifier, clazz.fullIdentifier));
			}
		}
		supers.add(BuiltIn.SCOPE_CLAZZ);
		
		TokenExecutor clazzExec = ((BlockElement) elem1).executor(this);
		label.setClazz(ClazzType.STANDARD, clazzExec, supers);
		return clazzExec.iterate();
	}
	
	protected @NonNull TokenResult onMagic(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"magic\" requires %s element as first argument!", BuiltIn.LABEL));
		}
		
		((LabelElement) elem0).setMagic((BlockElement) elem1);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onDeref(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"deref\" requires %s element as argument!", BuiltIn.LABEL));
		}
		
		LabelElement label = (LabelElement) elem;
		TokenResult result = scopeAction(label::getDef, label::getMacro, label::getClazz);
		if (result == null) {
			throw Helpers.defError(label.fullIdentifier);
		}
		return result;
	}
	
	protected @NonNull TokenResult onDelete(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"delete\" requires %s element as argument!", BuiltIn.LABEL));
		}
		return ((LabelElement) elem).delete();
	}
	
	protected @NonNull TokenResult onNew(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"new\" requires %s element as argument!", BuiltIn.CLASS));
		}
		return ((ClassElement) elem).internal.instantiate(this);
	}
	
	protected @NonNull TokenResult onNull(@NonNull Token token) {
		push(NullElement.INSTANCE);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onType(@NonNull Token token) {
		push(pop().clazz.clazzElement());
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onCast(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem1 instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"cast\" requires %s element as second argument!", BuiltIn.CLASS));
		}
		
		push(((ClassElement) elem1).internal.cast(this, elem0));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onIs(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem1 instanceof ClassElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"is\" requires %s element as second argument!", BuiltIn.CLASS));
		}
		
		push(new BoolElement(elem0.clazz.is(((ClassElement) elem1).internal)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onExch(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		push(elem1);
		push(elem0);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onRoll(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.asInt(this);
		if (intElem0 == null) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires non-negative %s element as first argument!", BuiltIn.INT));
		}
		
		int count = intElem0.primitiveInt();
		if (count < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires non-negative %s element as first argument!", BuiltIn.INT));
		}
		
		IntElement intElem1 = elem1.asInt(this);
		if (intElem1 == null) {
			throw new IllegalArgumentException(String.format("Keyword \"roll\" requires %s element as second argument!", BuiltIn.INT));
		}
		
		int roll = intElem1.primitiveInt();
		@NonNull Element[] elems = pop(count);
		for (int i = 0; i < count; ++i) {
			push(elems[Helpers.mod(i - roll, count)]);
		}
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onPop(@NonNull Token token) {
		pop();
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onDup(@NonNull Token token) {
		push(peek());
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onStacksize(@NonNull Token token) {
		push(new IntElement(stackSize()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onRead(@NonNull Token token) {
		String str = interpreter.hooks.read();
		push(str == null ? NullElement.INSTANCE : new StringElement(str));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onPrint(@NonNull Token token) {
		interpreter.printList.add(pop().stringCast(this).toString());
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onPrintln(@NonNull Token token) {
		interpreter.printList.add(pop().stringCast(this).toString());
		interpreter.printList.add("\n");
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onInterpret(@NonNull Token token) {
		@NonNull Element elem = pop();
		StringElement stringElem = elem.asString(this);
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"interpret\" requires %s element as argument!", BuiltIn.STRING));
		}
		return new TokenExecutor(new LexerIterator(stringElem.toString()), this, false).iterate();
	}
	
	protected @NonNull TokenResult onExec(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"exec\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		return ((BlockElement) elem).invoke(this);
	}
	
	protected @NonNull TokenResult onIf(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		BoolElement boolElem = elem0.asBool(this);
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires %s element as first argument!", BuiltIn.BOOL));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"if\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		
		return boolElem.primitiveBool() ? ((BlockElement) elem1).invoke(this) : TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onIfelse(@NonNull Token token) {
		@NonNull Element elem2 = pop(), elem1 = pop(), elem0 = pop();
		BoolElement boolElem = elem0.asBool(this);
		if (boolElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires %s element as first argument!", BuiltIn.BOOL));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		else if (!(elem2 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"ifelse\" requires %s element as third argument!", BuiltIn.BLOCK));
		}
		
		return ((BlockElement) (boolElem.primitiveBool() ? elem1 : elem2)).invoke(this);
	}
	
	protected @NonNull TokenResult onLoop(@NonNull Token token) {
		@NonNull Element elem = pop();
		if (!(elem instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"loop\" requires %s element as argument!", BuiltIn.BLOCK));
		}
		
		BlockElement block = (BlockElement) elem;
		loop: while (true) {
			TokenResult invokeResult = block.invoke(this);
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
	
	protected @NonNull TokenResult onRepeat(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem = elem0.asInt(this);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative %s element as first argument!", BuiltIn.INT));
		}
		
		int primitiveInt = intElem.primitiveInt();
		if (primitiveInt < 0) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires non-negative %s element as first argument!", BuiltIn.INT));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"repeat\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		
		BlockElement block = (BlockElement) elem1;
		loop: for (int i = 0; i < primitiveInt; ++i) {
			TokenResult invokeResult = block.invoke(this);
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
	
	protected @NonNull TokenResult onForeach(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		if (!(elem0 instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires %s element as first argument!", BuiltIn.ITERABLE));
		}
		if (!(elem1 instanceof BlockElement)) {
			throw new IllegalArgumentException(String.format("Keyword \"foreach\" requires %s element as second argument!", BuiltIn.BLOCK));
		}
		
		BlockElement block = (BlockElement) elem1;
		loop: for (@NonNull Element e : ((IterableElement) elem0).internalIterable(this)) {
			push(e);
			TokenResult invokeResult = block.invoke(this);
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
	
	protected @NonNull TokenResult onContinue(@NonNull Token token) {
		return TokenResult.CONTINUE;
	}
	
	protected @NonNull TokenResult onBreak(@NonNull Token token) {
		return TokenResult.BREAK;
	}
	
	protected @NonNull TokenResult onQuit(@NonNull Token token) {
		interpreter.halt = true;
		return TokenResult.QUIT;
	}
	
	protected @NonNull TokenResult onEquals(@NonNull Token token) {
		assign(pop(), pop(), AssignmentType.EQUALS);
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onIncrement(@NonNull Token token) {
		@NonNull Element elem = peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Increment operator \"++\" requires %s element as argument!", BuiltIn.LABEL));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw Helpers.variableError(label.fullIdentifier);
		}
		
		return opAssign(def.elem.onPlus(this, new IntElement(BigInteger.ONE)));
	}
	
	protected @NonNull TokenResult onDecrement(@NonNull Token token) {
		@NonNull Element elem = peek();
		if (!(elem instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Decrement operator \"--\" requires %s element as argument!", BuiltIn.LABEL));
		}
		
		LabelElement label = (LabelElement) elem;
		Def def = label.getDef();
		if (def == null) {
			throw Helpers.variableError(label.fullIdentifier);
		}
		
		return opAssign(def.elem.onMinus(this, new IntElement(BigInteger.ONE)));
	}
	
	protected @NonNull TokenResult onPlusEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onPlus(this, elems.second));
	}
	
	protected @NonNull TokenResult onAndEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onAnd(this, elems.second));
	}
	
	protected @NonNull TokenResult onOrEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onOr(this, elems.second));
	}
	
	protected @NonNull TokenResult onXorEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onXor(this, elems.second));
	}
	
	protected @NonNull TokenResult onMinusEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onMinus(this, elems.second));
	}
	
	protected @NonNull TokenResult onConcatEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onConcat(this, elems.second));
	}
	
	protected @NonNull TokenResult onLeftShiftEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onLeftShift(this, elems.second));
	}
	
	protected @NonNull TokenResult onRightShiftEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onRightShift(this, elems.second));
	}
	
	protected @NonNull TokenResult onMultiplyEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onMultiply(this, elems.second));
	}
	
	protected @NonNull TokenResult onDivideEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onDivide(this, elems.second));
	}
	
	protected @NonNull TokenResult onRemainderEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onRemainder(this, elems.second));
	}
	
	protected @NonNull TokenResult onPowerEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onPower(this, elems.second));
	}
	
	protected @NonNull TokenResult onIdivideEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onIdivide(this, elems.second));
	}
	
	protected @NonNull TokenResult onModuloEquals(@NonNull Token token) {
		AssignmentOpPair elems = assignmentOpElems(token);
		return opAssign(elems.first.elem.onModulo(this, elems.second));
	}
	
	protected @NonNull TokenResult onEqualTo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onEqualTo(this, elem1);
	}
	
	protected @NonNull TokenResult onNotEqualTo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onNotEqualTo(this, elem1);
	}
	
	protected @NonNull TokenResult onLessThan(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLessThan(this, elem1);
	}
	
	protected @NonNull TokenResult onLessOrEqual(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLessOrEqual(this, elem1);
	}
	
	protected @NonNull TokenResult onMoreThan(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMoreThan(this, elem1);
	}
	
	protected @NonNull TokenResult onMoreOrEqual(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMoreOrEqual(this, elem1);
	}
	
	protected @NonNull TokenResult onPlus(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onPlus(this, elem1);
	}
	
	protected @NonNull TokenResult onAnd(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onAnd(this, elem1);
	}
	
	protected @NonNull TokenResult onOr(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onOr(this, elem1);
	}
	
	protected @NonNull TokenResult onXor(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onXor(this, elem1);
	}
	
	protected @NonNull TokenResult onMinus(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMinus(this, elem1);
	}
	
	protected @NonNull TokenResult onConcat(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onConcat(this, elem1);
	}
	
	protected @NonNull TokenResult onLeftShift(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onLeftShift(this, elem1);
	}
	
	protected @NonNull TokenResult onRightShift(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRightShift(this, elem1);
	}
	
	protected @NonNull TokenResult onMultiply(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onMultiply(this, elem1);
	}
	
	protected @NonNull TokenResult onDivide(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onDivide(this, elem1);
	}
	
	protected @NonNull TokenResult onRemainder(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onRemainder(this, elem1);
	}
	
	protected @NonNull TokenResult onPower(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onPower(this, elem1);
	}
	
	protected @NonNull TokenResult onIdivide(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onIdivide(this, elem1);
	}
	
	protected @NonNull TokenResult onModulo(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = pop();
		return elem0.onModulo(this, elem1);
	}
	
	protected @NonNull TokenResult onNot(@NonNull Token token) {
		return pop().onNot(this);
	}
	
	protected @NonNull TokenResult onIntValue(@NonNull Token token) {
		push(new IntElement(new BigInteger(token.getText())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onBoolValue(@NonNull Token token) {
		push(new BoolElement(Boolean.parseBoolean(token.getText())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onFloatValue(@NonNull Token token) {
		push(new FloatElement(Double.parseDouble(token.getText())));
		return TokenResult.PASS;
	}
	
	@SuppressWarnings("null")
	protected @NonNull TokenResult onCharValue(@NonNull Token token) {
		push(new CharElement(Helpers.parseChar(token.getText())));
		return TokenResult.PASS;
	}
	
	@SuppressWarnings("null")
	protected @NonNull TokenResult onBlockStringValue(@NonNull Token token) {
		push(new StringElement(Helpers.parseBlockString(token.getText())));
		return TokenResult.PASS;
	}
	
	@SuppressWarnings("null")
	protected @NonNull TokenResult onLineStringValue(@NonNull Token token) {
		push(new StringElement(Helpers.parseLineString(token.getText())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onIdentifier(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String identifier = token.getText();
		checkKeyword(identifier, "an identifier");
		
		TokenResult result = scopeAction(this, identifier);
		if (result == null) {
			throw Helpers.defError(identifier);
		}
		return result;
	}
	
	protected @NonNull TokenResult onLabel(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String identifier = token.getText().substring(1);
		checkKeyword(identifier, "a label identifier");
		push(new LabelElement(this, identifier));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onMember(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String member = token.getText().substring(1);
		// checkKeyword(member, "a member identifier");
		
		@NonNull Element elem = pop();
		if (elem instanceof LabelElement) {
			push(((LabelElement) elem).extended(member));
			return TokenResult.PASS;
		}
		
		TokenResult result = elem.memberAccess(this, member);
		if (result == null) {
			throw elem.memberAccessError(member);
		}
		return result;
	}
	
	protected @NonNull TokenResult onModule(@NonNull Token token) {
		@SuppressWarnings("null") @NonNull String identifier = token.getText().substring(1);
		// checkKeyword(identifier, "a module identifier");
		push(new ModuleElement(identifier));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult onBlock(@NonNull Token token) {
		push(new BlockElement(((BlockToken) token).tokens));
		return TokenResult.PASS;
	}
	
	protected void assign(@NonNull Element elem1, @NonNull Element elem0, @NonNull AssignmentType type) {
		if (elem1 instanceof IterableElement && elem0 instanceof IterableElement) {
			IterElement iter1 = ((IterableElement) elem1).iterator(this), iter0 = ((IterableElement) elem0).iterator(this);
			while (iter1.hasNext(this) && iter0.hasNext(this)) {
				assign(iter1.next(this), iter0.next(this), type);
			}
			
			if (iter0.hasNext(this)) {
				throw new IllegalArgumentException(String.format("%s for %s elements requires length of second iterator to be greater than or equal to length of first iterator!", type.labelErrorPrefix(), BuiltIn.ITERABLE));
			}
		}
		else {
			if (!(elem0 instanceof LabelElement)) {
				throw new IllegalArgumentException(String.format("%s requires %s element as first argument!", type.labelErrorPrefix(), BuiltIn.LABEL));
			}
			
			LabelElement label = (LabelElement) elem0;
			boolean def = type.equals(AssignmentType.DEF);
			if (!def && label.getDef() == null) {
				throw Helpers.variableError(label.fullIdentifier);
			}
			label.setDef(elem1, def);
		}
	}
	
	protected void checkKeyword(@NonNull String identifier, @NonNull String type) {
		if (BuiltIn.KEYWORDS.contains(identifier)) {
			throw new IllegalArgumentException(String.format("Keyword \"%s\" can not be used as %s!", identifier, type));
		}
	}
	
	protected Reverse<@NonNull Element> getElemsToLBracket(@NonNull LBracketElement lbracket, @NonNull RBracketElement rbracket) {
		Deque<@NonNull Element> deque = new ArrayDeque<>();
		while (!stack().isEmpty()) {
			@NonNull Element elem = pop();
			if (elem.equals(lbracket)) {
				return new Reverse<>(deque);
			}
			else {
				deque.add(elem);
			}
		}
		throw new IllegalArgumentException(String.format("Encountered \"%s\" token without corresponding \"%s\" token!", rbracket, lbracket));
	}
	
	protected static enum AssignmentType {
		
		DEF,
		EQUALS;
		
		protected String labelErrorPrefix() {
			return equals(EQUALS) ? "Assignment" : "Keyword \"def\"";
		}
	}
	
	protected static class AssignmentOpPair extends Pair<Def, @NonNull Element> {
		
		public AssignmentOpPair(Def left, @NonNull Element right) {
			super(left, right);
		}
	}
	
	protected AssignmentOpPair assignmentOpElems(@NonNull Token token) {
		@NonNull Element elem1 = pop(), elem0 = peek();
		if (!(elem0 instanceof LabelElement)) {
			throw new IllegalArgumentException(String.format("Assignment operator \"%s\" requires %s element as first argument!", token.getText(), BuiltIn.LABEL));
		}
		
		LabelElement label = (LabelElement) elem0;
		Def def = label.getDef();
		if (def == null) {
			throw Helpers.variableError(label.fullIdentifier);
		}
		
		return new AssignmentOpPair(def, elem1);
	}
	
	protected @NonNull TokenResult opAssign(@NonNull TokenResult opResult) {
		assign(pop(), pop(), AssignmentType.EQUALS);
		return opResult;
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
			return macro.invokable.invoke(this);
		}
		else if ((clazz = getClazz.get()) != null) {
			push(clazz.clazzElement());
			return TokenResult.PASS;
		}
		else {
			return null;
		}
	}
	
	// BUILT-INS
	
	protected @NonNull TokenResult finite() {
		@NonNull FloatElement floatElem = builtInSingleFloat("finite");
		push(new BoolElement(Double.isFinite(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult infinite() {
		@NonNull FloatElement floatElem = builtInSingleFloat("infinite");
		push(new BoolElement(Double.isInfinite(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult inv() {
		@NonNull FloatElement floatElem = builtInSingleFloat("inv");
		push(new FloatElement(1.0 / floatElem.primitiveFloat()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult neg() {
		@NonNull Element elem = pop();
		IntElement intElem = elem.asInt(this);
		if (intElem != null) {
			push(new IntElement(intElem.value.raw.negate()));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem = elem.asFloat(this);
		if (floatElem != null) {
			push(new FloatElement(-floatElem.primitiveFloat()));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"neg\" requires %s or %s element as argument!", BuiltIn.INT, BuiltIn.FLOAT));
	}
	
	protected @NonNull TokenResult abs() {
		@NonNull Element elem = pop();
		IntElement intElem = elem.asInt(this);
		if (intElem != null) {
			push(new IntElement(intElem.value.raw.abs()));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem = elem.asFloat(this);
		if (floatElem != null) {
			push(new FloatElement(Math.abs(floatElem.primitiveFloat())));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"abs\" requires %s or %s element as argument!", BuiltIn.INT, BuiltIn.FLOAT));
	}
	
	protected @NonNull TokenResult sgn() {
		@NonNull Element elem = pop();
		IntElement intElem = elem.asInt(this);
		if (intElem != null) {
			push(new IntElement(intElem.value.raw.signum()));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem = elem.asFloat(this);
		if (floatElem != null) {
			push(new FloatElement(Math.signum(floatElem.primitiveFloat())));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"sgn\" requires %s or %s element as argument!", BuiltIn.INT, BuiltIn.FLOAT));
	}
	
	protected @NonNull TokenResult floor() {
		@NonNull FloatElement floatElem = builtInSingleFloat("floor");
		push(new IntElement(floatElem.bigFloat().setScale(0, RoundingMode.FLOOR).toBigInteger()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult ceil() {
		@NonNull FloatElement floatElem = builtInSingleFloat("ceil");
		push(new IntElement(floatElem.bigFloat().setScale(0, RoundingMode.CEILING).toBigInteger()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult trunc() {
		@NonNull FloatElement floatElem = builtInSingleFloat("trunc");
		push(new IntElement(floatElem.bigFloat().toBigInteger()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult fract() {
		@NonNull FloatElement floatElem = builtInSingleFloat("fract");
		@NonNull BigDecimal bd = floatElem.bigFloat();
		push(new FloatElement(bd.subtract(new BigDecimal(bd.toBigInteger())).doubleValue()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult round() {
		@NonNull FloatElement floatElem = builtInSingleFloat("round");
		push(new IntElement(floatElem.bigFloat().round(MathContext.UNLIMITED).toBigInteger()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult places() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		FloatElement floatElem = elem0.asFloat(this);
		if (floatElem == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"places\" requires %s element as first argument!", BuiltIn.FLOAT));
		}
		
		IntElement intElem = elem1.asInt(this);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"places\" requires %s element as second argument!", BuiltIn.INT));
		}
		
		push(new FloatElement(floatElem.bigFloat().setScale(intElem.primitiveInt(), BigDecimal.ROUND_HALF_UP).doubleValue()));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult sin() {
		@NonNull FloatElement floatElem = builtInSingleFloat("sin");
		push(new FloatElement(Math.sin(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult cos() {
		@NonNull FloatElement floatElem = builtInSingleFloat("cos");
		push(new FloatElement(Math.cos(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult tan() {
		@NonNull FloatElement floatElem = builtInSingleFloat("tan");
		push(new FloatElement(Math.tan(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult asin() {
		@NonNull FloatElement floatElem = builtInSingleFloat("asin");
		push(new FloatElement(Math.asin(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult acos() {
		@NonNull FloatElement floatElem = builtInSingleFloat("acos");
		push(new FloatElement(Math.acos(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult atan() {
		@NonNull FloatElement floatElem = builtInSingleFloat("atan");
		push(new FloatElement(Math.atan(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult sinc() {
		@NonNull FloatElement floatElem = builtInSingleFloat("sinc");
		double f = floatElem.primitiveFloat();
		push(new FloatElement(f == 0.0 ? 1.0 : Math.sin(f) / f));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult atan2() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		FloatElement floatElem0 = elem0.asFloat(this);
		if (floatElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"atan2\" requires %s element as first argument!", BuiltIn.FLOAT));
		}
		
		FloatElement floatElem1 = elem1.asFloat(this);
		if (floatElem1 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"atan2\" requires %s element as second argument!", BuiltIn.FLOAT));
		}
		
		push(new FloatElement(Math.atan2(floatElem0.primitiveFloat(), floatElem1.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult hypot() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		FloatElement floatElem0 = elem0.asFloat(this);
		if (floatElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"hypot\" requires %s element as first argument!", BuiltIn.FLOAT));
		}
		
		FloatElement floatElem1 = elem1.asFloat(this);
		if (floatElem1 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"hypot\" requires %s element as second argument!", BuiltIn.FLOAT));
		}
		
		push(new FloatElement(Math.hypot(floatElem0.primitiveFloat(), floatElem1.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult rads() {
		@NonNull FloatElement floatElem = builtInSingleFloat("rads");
		push(new FloatElement(Math.toRadians(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult degs() {
		@NonNull FloatElement floatElem = builtInSingleFloat("degs");
		push(new FloatElement(Math.toDegrees(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult exp() {
		@NonNull FloatElement floatElem = builtInSingleFloat("exp");
		push(new FloatElement(Math.exp(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult ln() {
		@NonNull FloatElement floatElem = builtInSingleFloat("ln");
		push(new FloatElement(Math.log(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult log2() {
		@NonNull FloatElement floatElem = builtInSingleFloat("log2");
		push(new FloatElement(Math.log(floatElem.primitiveFloat()) / Constants.LN_2));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult log10() {
		@NonNull FloatElement floatElem = builtInSingleFloat("log10");
		push(new FloatElement(Math.log10(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult log() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		FloatElement floatElem0 = elem0.asFloat(this);
		if (floatElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"log\" requires %s element as first argument!", BuiltIn.FLOAT));
		}
		
		FloatElement floatElem1 = elem1.asFloat(this);
		if (floatElem1 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"log\" requires %s element as second argument!", BuiltIn.FLOAT));
		}
		
		push(new FloatElement(Math.log(floatElem0.primitiveFloat()) / Math.log(floatElem1.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult expm1() {
		@NonNull FloatElement floatElem = builtInSingleFloat("expm1");
		push(new FloatElement(Math.expm1(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult ln1p() {
		@NonNull FloatElement floatElem = builtInSingleFloat("ln1p");
		push(new FloatElement(Math.log1p(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult sqrt() {
		@NonNull FloatElement floatElem = builtInSingleFloat("sqrt");
		push(new FloatElement(Math.sqrt(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult cbrt() {
		@NonNull FloatElement floatElem = builtInSingleFloat("cbrt");
		push(new FloatElement(Math.cbrt(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult root() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		FloatElement floatElem0 = elem0.asFloat(this);
		if (floatElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"root\" requires %s element as first argument!", BuiltIn.FLOAT));
		}
		
		FloatElement floatElem1 = elem1.asFloat(this);
		if (floatElem1 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"root\" requires %s element as second argument!", BuiltIn.FLOAT));
		}
		
		push(new FloatElement(Math.pow(floatElem0.primitiveFloat(), 1.0 / floatElem1.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult isqrt() {
		@NonNull IntElement intElem = builtInSingleInt("isqrt");
		push(new IntElement(Helpers.iroot(intElem.value.raw, 2)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult icbrt() {
		@NonNull IntElement intElem = builtInSingleInt("icbrt");
		push(new IntElement(Helpers.iroot(intElem.value.raw, 3)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult iroot() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.asInt(this);
		if (intElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"iroot\" requires %s element as first argument!", BuiltIn.INT));
		}
		
		IntElement intElem1 = elem1.asInt(this);
		if (intElem1 == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"iroot\" requires %s element as second argument!", BuiltIn.INT));
		}
		
		push(new IntElement(Helpers.iroot(intElem0.value.raw, intElem1.primitiveInt())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult sinh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("sinh");
		push(new FloatElement(Math.sinh(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult cosh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("cosh");
		push(new FloatElement(Math.cosh(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult tanh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("tanh");
		push(new FloatElement(Math.tanh(floatElem.primitiveFloat())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult asinh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("asinh");
		double f = floatElem.primitiveFloat();
		push(new FloatElement(Math.log(f + Math.sqrt(f * f + 1.0))));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult acosh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("acosh");
		double f = floatElem.primitiveFloat();
		push(new FloatElement(Math.log(f + Math.sqrt(f * f - 1.0))));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult atanh() {
		@NonNull FloatElement floatElem = builtInSingleFloat("atanh");
		double f = floatElem.primitiveFloat();
		push(new FloatElement(0.5 * Math.log((1.0 + f) / (1.0 - f))));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult min() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.asInt(this), intElem1 = elem1.asInt(this);
		boolean firstValid = false;
		if ((firstValid |= intElem0 != null) && intElem1 != null) {
			push(new IntElement(intElem0.value.raw.min(intElem1.value.raw)));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem0 = elem0.asFloat(this), floatElem1 = elem1.asFloat(this);
		if ((firstValid |= floatElem0 != null) && floatElem1 != null) {
			push(new FloatElement(Math.min(floatElem0.primitiveFloat(), floatElem1.primitiveFloat())));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"min\" requires %s or %s element as %s argument!", BuiltIn.INT, BuiltIn.FLOAT, firstValid ? "second" : "first"));
	}
	
	protected @NonNull TokenResult max() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.asInt(this), intElem1 = elem1.asInt(this);
		boolean firstValid = false;
		if ((firstValid |= intElem0 != null) && intElem1 != null) {
			push(new IntElement(intElem0.value.raw.max(intElem1.value.raw)));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem0 = elem0.asFloat(this), floatElem1 = elem1.asFloat(this);
		if ((firstValid |= floatElem0 != null) && floatElem1 != null) {
			push(new FloatElement(Math.max(floatElem0.primitiveFloat(), floatElem1.primitiveFloat())));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"max\" requires %s or %s element as %s argument!", BuiltIn.INT, BuiltIn.FLOAT, firstValid ? "second" : "first"));
	}
	
	protected @NonNull TokenResult clamp() {
		@NonNull Element elem2 = pop(), elem1 = pop(), elem0 = pop();
		IntElement intElem0 = elem0.asInt(this), intElem1 = elem1.asInt(this), intElem2 = elem2.asInt(this);
		boolean firstValid = false, secondValid = false;
		if ((firstValid |= intElem0 != null) && (secondValid |= intElem1 != null) && intElem2 != null) {
			push(new IntElement(Helpers.clamp(intElem0.value.raw, intElem1.value.raw, intElem2.value.raw)));
			return TokenResult.PASS;
		}
		
		FloatElement floatElem0 = elem0.asFloat(this), floatElem1 = elem1.asFloat(this), floatElem2 = elem2.asFloat(this);
		if ((firstValid |= floatElem0 != null) && (secondValid |= floatElem1 != null) && floatElem2 != null) {
			push(new FloatElement(Helpers.clamp(floatElem0.primitiveFloat(), floatElem1.primitiveFloat(), floatElem2.primitiveFloat())));
			return TokenResult.PASS;
		}
		
		throw new IllegalArgumentException(String.format("Built-in math macro \"clamp\" requires %s or %s element as %s argument!", BuiltIn.INT, BuiltIn.FLOAT, secondValid ? "third" : (firstValid ? "second" : "first")));
	}
	
	protected @NonNull TokenResult clamp8() {
		@NonNull IntElement intElem = builtInSingleInt("clamp8");
		push(new IntElement(Helpers.clamp(intElem.value.raw, Constants.MIN_INT_8, Constants.MAX_INT_8)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult clamp16() {
		@NonNull IntElement intElem = builtInSingleInt("clamp16");
		push(new IntElement(Helpers.clamp(intElem.value.raw, Constants.MIN_INT_16, Constants.MAX_INT_16)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult clamp32() {
		@NonNull IntElement intElem = builtInSingleInt("clamp32");
		push(new IntElement(Helpers.clamp(intElem.value.raw, Constants.MIN_INT_32, Constants.MAX_INT_32)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult clamp64() {
		@NonNull IntElement intElem = builtInSingleInt("clamp64");
		push(new IntElement(Helpers.clamp(intElem.value.raw, Constants.MIN_INT_64, Constants.MAX_INT_64)));
		return TokenResult.PASS;
	}
	
	protected @NonNull IntElement builtInSingleInt(String name) {
		@NonNull Element elem = pop();
		IntElement intElem = elem.asInt(this);
		if (intElem == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"%s\" requires %s element as argument!", name, BuiltIn.INT));
		}
		return intElem;
	}
	
	protected @NonNull FloatElement builtInSingleFloat(String name) {
		@NonNull Element elem = pop();
		FloatElement floatElem = elem.asFloat(this);
		if (floatElem == null) {
			throw new IllegalArgumentException(String.format("Built-in math macro \"%s\" requires %s element as argument!", name, BuiltIn.FLOAT));
		}
		return floatElem;
	}
	
	protected @NonNull TokenResult args() {
		push(new ListElement(interpreter.args.stream().map(StringElement::new)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult rootPath() {
		push(new StringElement(Helpers.normalizedPathString(interpreter.hooks.getRootPath(this))));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult rootDir() {
		push(new StringElement(Helpers.normalizedPathString(interpreter.hooks.getRootPath(this).getParent())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult fromRoot() {
		@NonNull Element elem = pop();
		StringElement stringElem = elem.asString(this);
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Built-in env macro \"fromRoot\" requires %s element as argument!", BuiltIn.STRING));
		}
		
		push(new StringElement(Helpers.normalizedPathString(interpreter.hooks.getRootPath(this).getParent().resolve(stringElem.toString()))));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult readFile() {
		@NonNull Element elem = pop();
		StringElement stringElem = elem.asString(this);
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Built-in fs macro \"readFile\" requires %s element as argument!", BuiltIn.STRING));
		}
		
		push(new StringElement(Helpers.readFile(stringElem.toString())));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult writeFile() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		StringElement stringElem0 = elem0.asString(this);
		if (stringElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in fs macro \"writeFile\" requires %s element as first argument!", BuiltIn.STRING));
		}
		
		Helpers.writeFile(stringElem0.toString(), elem1.stringCast(this).toString());
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult readLines() {
		@NonNull Element elem = pop();
		StringElement stringElem = elem.asString(this);
		if (stringElem == null) {
			throw new IllegalArgumentException(String.format("Built-in fs macro \"readLines\" requires %s element as argument!", BuiltIn.STRING));
		}
		
		push(new ListElement(Helpers.readLines(stringElem.toString()).stream().map(StringElement::new)));
		return TokenResult.PASS;
	}
	
	protected @NonNull TokenResult writeLines() {
		@NonNull Element elem1 = pop(), elem0 = pop();
		StringElement stringElem0 = elem0.asString(this);
		if (stringElem0 == null) {
			throw new IllegalArgumentException(String.format("Built-in fs macro \"writeLines\" requires %s element as first argument!", BuiltIn.STRING));
		}
		
		if (!(elem1 instanceof IterableElement)) {
			throw new IllegalArgumentException(String.format("Built-in fs macro \"writeLines\" requires %s element as second argument!", BuiltIn.ITERABLE));
		}
		
		Helpers.writeLines(stringElem0.toString(), ((IterableElement) elem1).stream(this).map(x -> x.stringCast(this).toString()));
		return TokenResult.PASS;
	}
}
