package dssl;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;
import dssl.lexer.Lexer;
import dssl.node.*;

public class Main {
	
	static class Input {
		
		final List<String> args;
		final Set<String> options;
		
		Input(String[] args) {
			Map<Boolean, List<String>> map = Arrays.asList(args).stream().collect(Collectors.partitioningBy(x -> x.charAt(0) == '-'));
			this.args = map.get(false);
			this.options = map.get(true).stream().map(x -> Helpers.lowerCase(x).substring(1)).collect(Collectors.toSet());
		}
	}
	
	static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) {
		Input input = new Input(args);
		
		final boolean console = input.args.isEmpty();
		final boolean debug = input.options.contains("d");
		final boolean natives = input.options.contains("n");
		
		if (console) {
			System.out.println("INFO: Console mode was enabled!");
			
			if (debug) {
				System.out.println("INFO: Debug mode was enabled!");
			}
			if (natives) {
				System.out.println("INFO: Native keyword was enabled!");
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
			public String read() {
				try {
					return READER.readLine();
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
		};
		
		Module moduleImpl = new Module() {
			
			@Override
			public TokenResult onInclude(TokenExecutor exec) {
				@NonNull Element elem = exec.pop();
				StringElement stringElem = elem.stringCast(false);
				if (stringElem != null) {
					try (PushbackReader reader = Helpers.getPushbackReader(new FileReader(stringElem.toString()))) {
						return new TokenExecutor(new LexerIterator(reader), exec, false).iterate();
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException();
					}
				}
				else if (elem instanceof ModuleElement) {
					exec.putAll(((ModuleElement) elem).clazz, true);
					return TokenResult.PASS;
				}
				else {
					throw new IllegalArgumentException(String.format("Keyword \"include\" requires string or module element as argument!"));
				}
			}
			
			@Override
			public TokenResult onImport(TokenExecutor exec) {
				@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
				if (!(elem0 instanceof LabelElement)) {
					throw new IllegalArgumentException(String.format("Keyword \"import\" requires label element as first argument!"));
				}
				
				StringElement stringElem = elem1.stringCast(false);
				if (stringElem != null) {
					try (PushbackReader reader = Helpers.getPushbackReader(new FileReader(stringElem.toString()))) {
						TokenExecutor otherExec = exec.interpreter.newExecutor(new LexerIterator(reader));
						((LabelElement) elem0).setClazz(otherExec, new ArrayList<>());
						return otherExec.iterate();
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException();
					}
				}
				else if (elem1 instanceof ModuleElement) {
					((LabelElement) elem0).setClazz(((ModuleElement) elem1).clazz, new ArrayList<>());
					return TokenResult.PASS;
				}
				else {
					throw new IllegalArgumentException(String.format("Keyword \"import\" requires string or module element as second argument!"));
				}
			}
		};
		
		Native nativeImpl = natives ? new NativeImpl() : new Native() {
			
			@Override
			public TokenResult onNative(TokenExecutor exec) {
				throw new IllegalArgumentException(String.format("Keyword \"native\" not enabled!"));
			}
		};
		
		if (console) {
			TokenIterator consoleIterator = new TokenIterator() {
				
				protected Lexer lexer = Helpers.stringLexer("");
				
				@Override
				public @NonNull Token next() {
					@NonNull Token next = super.next();
					if (debug && !Helpers.isSeparator(next)) {
						consoleIO.print("::: ");
					}
					return next;
				}
				
				@Override
				public void onStart() {
					curr = new EOF();
				}
				
				@Override
				public boolean validNext() {
					while (curr instanceof EOF) {
						String str;
						consoleIO.print(">>> ");
						if ((str = consoleIO.read()) == null) {
							return false;
						}
						lexer = Helpers.stringLexer(str);
						curr = getNextChecked();
						requireSeparator = false;
					}
					return true;
				}
				
				@Override
				public Token getNext() {
					return Helpers.getLexerNext(lexer);
				}
			};
			Interpreter interpreter = new Interpreter(consoleIterator, consoleIO, moduleImpl, nativeImpl, debug);
			interpreter.run();
		}
		else {
			try (PushbackReader reader = Helpers.getPushbackReader(new FileReader(input.args.get(0)))) {
				Interpreter interpreter = new Interpreter(new Lexer(reader), consoleIO, moduleImpl, nativeImpl, debug);
				interpreter.run();
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
}
