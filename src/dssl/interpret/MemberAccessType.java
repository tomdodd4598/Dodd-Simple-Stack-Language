package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.Element;

public enum MemberAccessType {
	
	INSTANCE("Instance", "."),
	STATIC("Static", "::");
	
	private @NonNull String str;
	public @NonNull String separator;
	
	private MemberAccessType(@NonNull String str, @NonNull String separator) {
		this.str = str;
		this.separator = separator;
	}
	
	public @NonNull String nextIdentifier(@NonNull String prevIdentifier, @NonNull String extension) {
		return prevIdentifier + separator + extension;
	}
	
	public @NonNull String nextIdentifier(@NonNull Element elem, @NonNull String extension) {
		return elem.scopeAccessIdentifier(this) + separator + extension;
	}
	
	@Override
	public @NonNull String toString() {
		return str;
	}
}
