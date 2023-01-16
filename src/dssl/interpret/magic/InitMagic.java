package dssl.interpret.magic;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.element.BlockElement;

public class InitMagic extends BlockMagic {
	
	public InitMagic(@NonNull BlockElement block) {
		super("init", block);
	}
}
