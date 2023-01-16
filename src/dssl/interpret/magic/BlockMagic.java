package dssl.interpret.magic;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

public abstract class BlockMagic extends Magic {
	
	public final @NonNull BlockElement block;
	
	public BlockMagic(@NonNull String identifier, @NonNull BlockElement block) {
		super(identifier);
		this.block = block;
	}
}
