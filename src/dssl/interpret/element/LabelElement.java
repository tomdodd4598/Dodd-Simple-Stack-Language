package dssl.interpret.element;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import dssl.Helpers;
import dssl.interpret.*;
import dssl.interpret.element.clazz.InstanceElement;
import dssl.interpret.element.primitive.StringElement;

public class LabelElement extends Element {
	
	protected final @NonNull Scope scope;
	
	public final @NonNull String identifier;
	protected final @NonNull String shallow;
	
	public LabelElement(@NonNull Scope scope, @NonNull String identifier) {
		this(scope, identifier, identifier);
	}
	
	protected LabelElement(@NonNull Scope scope, @NonNull String identifier, @NonNull String shallow) {
		super();
		this.scope = scope;
		this.identifier = identifier;
		this.shallow = shallow;
	}
	
	protected LabelElement(@NonNull LabelElement prev, @NonNull String extension) {
		super();
		Scope scope;
		Def def = prev.scope.getDef(prev.shallow);
		if (def != null && def.elem instanceof InstanceElement) {
			InstanceElement instance = (InstanceElement) def.elem;
			this.scope = instance;
			identifier = instance.scopeIdentifier + "." + extension;
		}
		else if ((scope = prev.scope.getClazz(prev.shallow)) != null) {
			this.scope = scope;
			identifier = prev.identifier + "." + extension;
		}
		else {
			throw new IllegalArgumentException(String.format("Member \"%s\" not defined!", Helpers.memberString(prev.identifier, extension)));
		}
		shallow = extension;
	}
	
	@Override
	public @NonNull String typeName() {
		return "label";
	}
	
	@Override
	public @NonNull StringElement stringCastExplicit() {
		return new StringElement(identifier);
	}
	
	public Def getDef() {
		return scope.getDef(shallow);
	}
	
	public void setDef(@NonNull Element value, boolean shadow) {
		scope.setDef(shallow, value, shadow);
	}
	
	public Clazz getClazz() {
		return scope.getClazz(shallow);
	}
	
	public void setClazz(ScopeMaps maps) {
		scope.setClazz(shallow, maps);
	}
	
	public @NonNull LabelElement extended(@NonNull String extension) {
		return new LabelElement(this, extension);
	}
	
	@Override
	public @NonNull Element clone() {
		return new LabelElement(scope, identifier, shallow);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash("label", scope, identifier, shallow);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LabelElement) {
			LabelElement other = (LabelElement) obj;
			return scope.equals(other.scope) && identifier.equals(other.identifier) && shallow.equals(other.shallow);
		}
		return false;
	}
	
	@Override
	public @NonNull String toString() {
		return "label:" + identifier;
	}
	
	@Override
	public @NonNull String toDebugString() {
		return "/" + identifier;
	}
}
