package dssl;

import java.io.*;
import java.util.Iterator;

import dssl.interpret.*;
import dssl.lexer.Lexer;
import dssl.node.*;

public class Main {
	
	public static void main(String[] args) {
		boolean consoleMode = false;
		final boolean debugMode;
		if (args.length == 0) {
			consoleMode = true;
			debugMode = false;
		}
		else {
			debugMode = trim(args[0]).equals("d");
		}
		
		if (debugMode) {
			if (args.length == 1) {
				consoleMode = true;
			}
		}
		
		if (consoleMode) {
			System.out.println("INFO: Console mode was enabled!");
			System.out.println(debugMode ? "INFO: Debug mode was enabled!\n" : "");
			
			Iterator<Token> consoleIterator = new Iterator<Token>() {
				
				LexerIterator internal = null;
				
				@Override
				public boolean hasNext() {
					return true;
				}
				
				@Override
				public Token next() {
					while (internal == null || !internal.hasNext()) {
						System.out.print(">>> ");
						internal = new LexerIterator(new Lexer(Helpers.getPushbackReader(new StringReader(Helpers.readLine()))));
					}
					Token next = internal.next();
					if (debugMode && !(next instanceof TBlank || next instanceof TComment)) {
						System.out.print("::: ");
					}
					return next;
				}
				
			};
			Executor executor = new Executor(consoleIterator, debugMode);
			executor.setup();
			executor.interpret();
		}
		else {
			if (debugMode) {
				System.out.println("INFO: Debug mode was enabled!\n");
			}
			
			try {
				PushbackReader reader = Helpers.getPushbackReader(new FileReader(args[debugMode ? 1 : 0]));
				Executor executor = new Executor(new Lexer(reader), debugMode);
				executor.setup();
				executor.interpret();
				reader.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String trim(String arg) {
		return Helpers.lowerCase(arg.replaceAll("-|_", ""));
	}
}
