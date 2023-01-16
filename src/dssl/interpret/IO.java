package dssl.interpret;

import org.eclipse.jdt.annotation.NonNull;

public interface IO {
	
	public void print(String str);
	
	public void debug(String str);
	
	public @NonNull String read();
}
