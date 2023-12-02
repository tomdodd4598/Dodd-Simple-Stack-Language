package dssl.interpret.element.clazz;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;

public class InstanceElement extends Element implements Scope {
	
	protected final Map<@NonNull String, Def> defMap;
	protected final Map<@NonNull String, Const> constMap;
	protected final Map<@NonNull String, Macro> macroMap;
	protected final Map<@NonNull String, Clazz> clazzMap;
	protected final Map<@NonNull String, Magic> magicMap;
	
	public final @NonNull String scopeIdentifier;
	
	public InstanceElement(@NonNull Clazz clazz) {
		this(clazz, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	protected InstanceElement(@NonNull Clazz clazz, Map<@NonNull String, Def> defMap, Map<@NonNull String, Const> constMap, Map<@NonNull String, Macro> macroMap, Map<@NonNull String, Clazz> clazzMap, Map<@NonNull String, Magic> magicMap) {
		super(clazz);
		this.defMap = defMap;
		this.constMap = constMap;
		this.macroMap = macroMap;
		this.clazzMap = clazzMap;
		this.magicMap = magicMap;
		scopeIdentifier = toString();
	}
	
	@Override
	public @NonNull StringElement stringCast(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "str");
		if (magic != null) {
			return exec.pop().stringCast(exec);
		}
		return super.stringCast(exec);
	}
	
	@Override
	public TokenResult onEqualTo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "eq", other);
		if (magic != null) {
			return magic;
		}
		return super.onEqualTo(exec, other);
	}
	
	@Override
	public TokenResult onNotEqualTo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "ne", other);
		if (magic != null) {
			return magic;
		}
		return super.onNotEqualTo(exec, other);
	}
	
	@Override
	public TokenResult onLessThan(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "lt", other);
		if (magic != null) {
			return magic;
		}
		return super.onLessThan(exec, other);
	}
	
	@Override
	public TokenResult onLessOrEqual(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "le", other);
		if (magic != null) {
			return magic;
		}
		return super.onLessOrEqual(exec, other);
	}
	
	@Override
	public TokenResult onMoreThan(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "gt", other);
		if (magic != null) {
			return magic;
		}
		return super.onMoreThan(exec, other);
	}
	
	@Override
	public TokenResult onMoreOrEqual(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "ge", other);
		if (magic != null) {
			return magic;
		}
		return super.onMoreOrEqual(exec, other);
	}
	
	@Override
	public TokenResult onPlus(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "add", other);
		if (magic != null) {
			return magic;
		}
		return super.onPlus(exec, other);
	}
	
	@Override
	public TokenResult onAnd(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "and", other);
		if (magic != null) {
			return magic;
		}
		return super.onAnd(exec, other);
	}
	
	@Override
	public TokenResult onOr(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "or", other);
		if (magic != null) {
			return magic;
		}
		return super.onOr(exec, other);
	}
	
	@Override
	public TokenResult onXor(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "xor", other);
		if (magic != null) {
			return magic;
		}
		return super.onXor(exec, other);
	}
	
	@Override
	public TokenResult onMinus(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "sub", other);
		if (magic != null) {
			return magic;
		}
		return super.onMinus(exec, other);
	}
	
	@Override
	public TokenResult onConcat(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "concat", other);
		if (magic != null) {
			return magic;
		}
		return super.onConcat(exec, other);
	}
	
	@Override
	public TokenResult onLeftShift(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "lshift", other);
		if (magic != null) {
			return magic;
		}
		return super.onLeftShift(exec, other);
	}
	
	@Override
	public TokenResult onRightShift(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "rshift", other);
		if (magic != null) {
			return magic;
		}
		return super.onRightShift(exec, other);
	}
	
	@Override
	public TokenResult onMultiply(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "mul", other);
		if (magic != null) {
			return magic;
		}
		return super.onMultiply(exec, other);
	}
	
	@Override
	public TokenResult onDivide(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "div", other);
		if (magic != null) {
			return magic;
		}
		return super.onDivide(exec, other);
	}
	
	@Override
	public TokenResult onRemainder(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "rem", other);
		if (magic != null) {
			return magic;
		}
		return super.onRemainder(exec, other);
	}
	
	@Override
	public TokenResult onPower(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "pow", other);
		if (magic != null) {
			return magic;
		}
		return super.onPower(exec, other);
	}
	
	@Override
	public TokenResult onIdivide(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "floordiv", other);
		if (magic != null) {
			return magic;
		}
		return super.onIdivide(exec, other);
	}
	
	@Override
	public TokenResult onModulo(TokenExecutor exec, @NonNull Element other) {
		TokenResult magic = magicAction(exec, "mod", other);
		if (magic != null) {
			return magic;
		}
		return super.onModulo(exec, other);
	}
	
	@Override
	public TokenResult onNot(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "not");
		if (magic != null) {
			return magic;
		}
		return super.onNot(exec);
	}
	
	@Override
	public @NonNull String debug(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "debug");
		if (magic != null) {
			return exec.pop().debug(exec);
		}
		return super.debug(exec);
	}
	
	@Override
	public @Nullable String getIdentifier() {
		return scopeIdentifier;
	}
	
	@Override
	public boolean hasDef(@NonNull String identifier, boolean shallow) {
		return defMap.containsKey(identifier);
	}
	
	@Override
	public Def getDef(@NonNull String identifier) {
		return defMap.get(identifier);
	}
	
	@Override
	public void setDef(@NonNull String identifier, @NonNull Element value, boolean shadow) {
		if (shadow) {
			checkCollision(identifier);
		}
		defMap.put(identifier, new Def(identifier, value));
	}
	
	@Override
	public boolean hasConst(@NonNull String identifier, boolean shallow) {
		return constMap.containsKey(identifier);
	}
	
	@Override
	public Const getConst(@NonNull String identifier) {
		return constMap.get(identifier);
	}
	
	@Override
	public void setConst(@NonNull String identifier, @NonNull Element value) {
		checkCollision(identifier);
		constMap.put(identifier, new Const(identifier, value));
	}
	
	@Override
	public boolean hasMacro(@NonNull String identifier, boolean shallow) {
		return macroMap.containsKey(identifier);
	}
	
	@Override
	public Macro getMacro(@NonNull String identifier) {
		return macroMap.get(identifier);
	}
	
	@Override
	public void setMacro(@NonNull String identifier, @NonNull Invokable invokable) {
		checkCollision(identifier);
		macroMap.put(identifier, new Macro(identifier, invokable));
	}
	
	@Override
	public boolean hasClazz(@NonNull String shallowIdentifier, boolean shallow) {
		return clazzMap.containsKey(shallowIdentifier);
	}
	
	@Override
	public Clazz getClazz(@NonNull String shallowIdentifier) {
		return clazzMap.get(shallowIdentifier);
	}
	
	@Override
	public void setClazz(@NonNull String shallowIdentifier, @NonNull ClazzType type, HierarchicalScope base, ArrayList<Clazz> supers) {
		checkCollision(shallowIdentifier);
		clazzMap.put(shallowIdentifier, new Clazz(scopeIdentifier, shallowIdentifier, type, base, supers));
	}
	
	@Override
	public boolean hasMagic(@NonNull String identifier, boolean shallow) {
		return magicMap.containsKey(identifier);
	}
	
	@Override
	public Magic getMagic(@NonNull String identifier) {
		return magicMap.get(identifier);
	}
	
	@Override
	public void setMagic(@NonNull String identifier, @NonNull Invokable invokable) {
		magicMap.put(identifier, new Magic(identifier, invokable));
	}
	
	public @Nullable TokenResult magicAction(TokenExecutor exec, @NonNull String identifier, @NonNull Element... args) {
		Magic magic = getMagic(identifier);
		boolean clazzMagic = false;
		if (magic == null) {
			magic = clazz.getMagic(identifier);
			clazzMagic = true;
		}
		if (magic != null) {
			if (clazzMagic) {
				exec.push(this);
			}
			for (@NonNull Element arg : args) {
				exec.push(arg);
			}
			return magic.invokable.invoke(exec);
		}
		return null;
	}
	
	@Override
	public @Nullable Scope getMemberScope(@NonNull MemberAccessType access) {
		return access.equals(MemberAccessType.STATIC) ? this : clazz;
	}
	
	protected <T> void addToScopeMap(Map<@NonNull String, T> source, Map<@NonNull Element, @NonNull Element> target) {
		for (String key : source.keySet()) {
			target.put(new StringElement(key), new LabelElement(this, key));
		}
	}
	
	@Override
	public @NonNull Element scope(TokenExecutor exec) {
		Map<@NonNull Element, @NonNull Element> map = new HashMap<>();
		addToScopeMap(defMap, map);
		addToScopeMap(constMap, map);
		addToScopeMap(macroMap, map);
		addToScopeMap(clazzMap, map);
		return new DictElement(map, false);
	}
	
	@Override
	public Object formatted(TokenExecutor exec) {
		TokenResult magic = magicAction(exec, "str");
		if (magic != null) {
			return exec.pop().stringCast(exec);
		}
		return super.formatted(exec);
	}
	
	@Override
	public @NonNull Element clone() {
		return new InstanceElement(clazz, new HashMap<>(defMap), new HashMap<>(constMap), new HashMap<>(macroMap), new HashMap<>(clazzMap), new HashMap<>(magicMap));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("instance", clazz, defMap, constMap, clazzMap, magicMap);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InstanceElement) {
			InstanceElement other = (InstanceElement) obj;
			return clazz.equals(other.clazz) && defMap.equals(other.defMap) && constMap.equals(other.constMap) && clazzMap.equals(other.clazzMap) && magicMap.equals(other.magicMap);
		}
		return false;
	}
}
