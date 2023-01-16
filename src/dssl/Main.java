package dssl;

import java.io.*;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.lexer.Lexer;
import dssl.node.*;

public class Main {
	
	public static void main(String[] args) {
		boolean console = false;
		final boolean debug;
		if (args.length == 0) {
			console = true;
			debug = false;
		}
		else {
			debug = trim(args[0]).equals("d");
		}
		
		if (debug) {
			if (args.length == 1) {
				console = true;
			}
		}
		
		IO consoleIO = new IO() {
			
			@Override
			public void print(String str) {
				System.out.print(str);
			}
			
			@Override
			public void debug(String str) {
				System.err.print(str);
			}
			
			@Override
			public @NonNull String read() {
				String str = null;
				try {
					str = READER.readLine();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				if (str == null) {
					throw new RuntimeException("Failed to read input!");
				}
				return str;
			}
		};
		
		if (console) {
			System.out.println("INFO: Console mode was enabled!");
			System.out.println(debug ? "INFO: Debug mode was enabled!\n" : "");
			
			Iterator<@NonNull Token> consoleIterator = new Iterator<@NonNull Token>() {
				
				LexerIterator internal = null;
				String str = null;
				
				@Override
				public boolean hasNext() {
					return (str = consoleIO.read()) != null;
				}
				
				@Override
				public @NonNull Token next() {
					while (internal == null || !internal.hasNext()) {
						System.out.print(">>> ");
						internal = new LexerIterator(str);
					}
					Token next = internal.next();
					if (debug && !(next instanceof TBlank || next instanceof TComment)) {
						System.out.print("::: ");
					}
					return next;
				}
			};
			Interpreter interpreter = new Interpreter(consoleIterator, consoleIO, debug);
			interpreter.run();
		}
		else {
			if (debug) {
				System.out.println("INFO: Debug mode was enabled!\n");
			}
			
			try (PushbackReader reader = Helpers.getPushbackReader(new FileReader(args[debug ? 1 : 0]))) {
				Interpreter interpreter = new Interpreter(new Lexer(reader), consoleIO, debug);
				interpreter.run();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	
	private static String trim(String arg) {
		return Helpers.lowerCase(arg.replaceAll("-|_", ""));
	}
}
