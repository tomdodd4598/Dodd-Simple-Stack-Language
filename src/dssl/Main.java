package dssl;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import dssl.interpret.*;
import dssl.interpret.element.*;
import dssl.interpret.element.primitive.StringElement;
import dssl.lexer.Lexer;
import dssl.node.Token;

public class Main {
	
	static class Input {
		
		final List<@NonNull String> args;
		final Set<@NonNull String> options;
		
		Input(@NonNull String[] args) {
			Map<Boolean, List<@NonNull String>> map = Arrays.stream(args).collect(Collectors.partitioningBy(x -> x.charAt(0) == '-'));
			this.args = map.get(false);
			this.options = map.get(true).stream().map(x -> Helpers.lowerCase(x).substring(1)).collect(Collectors.toSet());
		}
	}
	
	static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(@NonNull String[] args) {
		Input input = new Input(args);
		
		final boolean console = input.args.isEmpty();
		final boolean debug = input.options.contains("d");
		final boolean natives = input.options.contains("n");
		final Path rootPath = (console ? Paths.get("").resolve(".console.dssl") : Paths.get(input.args.get(0))).toAbsolutePath().normalize();
		
		if (console) {
			System.out.println("INFO: Console mode was enabled!");
			
			if (debug) {
				System.out.println("INFO: Debug mode was enabled!");
			}
			if (natives) {
				System.out.println("INFO: Native keyword was enabled!");
			}
		}
		
		Hooks hooks = new Hooks() {
			
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
				return Helpers.getThrowing(READER::readLine);
			}
			
			@Override
			public @NonNull TokenResult onInclude(TokenExecutor exec) {
				@NonNull Element elem = exec.pop();
				StringElement stringElem = elem.asString(exec);
				if (stringElem != null) {
					try (FileReader fileReader = new FileReader(stringElem.toString(exec)); PushbackReader pushbackReader = Helpers.getPushbackReader(fileReader)) {
						return new TokenExecutor(new LexerIterator(pushbackReader), exec, false).iterate();
					}
					catch (Exception e) {
						throw Helpers.panic(e);
					}
				}
				else if (elem instanceof ModuleElement module) {
					exec.putAll(module.internal, true, true);
					return TokenResult.PASS;
				}
				else {
					throw new IllegalArgumentException(String.format("Keyword \"include\" requires %s or %s element as argument!", BuiltIn.STRING, BuiltIn.MODULE));
				}
			}
			
			@Override
			public @NonNull TokenResult onImport(TokenExecutor exec) {
				@NonNull Element elem1 = exec.pop(), elem0 = exec.pop();
				if (!(elem0 instanceof LabelElement label)) {
					throw new IllegalArgumentException(String.format("Keyword \"import\" requires %s element as first argument!", BuiltIn.LABEL));
				}
				
				StringElement stringElem = elem1.asString(exec);
				if (stringElem != null) {
					try (FileReader fileReader = new FileReader(stringElem.toString(exec)); PushbackReader pushbackReader = Helpers.getPushbackReader(fileReader)) {
						TokenExecutor otherExec = exec.interpreter.newExecutor(new LexerIterator(pushbackReader));
						label.setClazz(ClazzType.INTERNAL, otherExec, new ArrayList<>());
						return otherExec.iterate();
					}
					catch (Exception e) {
						throw Helpers.panic(e);
					}
				}
				else if (elem1 instanceof ModuleElement module) {
					label.setClazz(ClazzType.INTERNAL, module.internal, new ArrayList<>());
					return TokenResult.PASS;
				}
				else {
					throw new IllegalArgumentException(String.format("Keyword \"import\" requires %s or %s element as second argument!", BuiltIn.STRING, BuiltIn.MODULE));
				}
			}
			
			@Override
			public @NonNull TokenResult onNative(TokenExecutor exec) {
				if (natives) {
					return NativeImpl.INSTANCE.onNative(exec);
				}
				else {
					throw new IllegalArgumentException(String.format("Keyword \"native\" not enabled!"));
				}
			}
			
			@Override
			public TokenIterator getBlockIterator(TokenExecutor exec, @NonNull BlockElement block) {
				return new TokenIterator() {
					
					Iterator<@NonNull Token> internal = block.tokens.iterator();
					
					@Override
					public void onStart() {
						curr = getNextChecked();
					}
					
					@Override
					public boolean validNext() {
						return internal.hasNext();
					}
					
					@SuppressWarnings("null")
					@Override
					protected Token getNext() {
						return internal.next();
					}
				};
			}
			
			@Override
			public Path getRootPath(TokenExecutor exec) {
				return rootPath;
			}
		};
		
		if (console) {
			Interpreter interpreter = new Interpreter(input.args, new LexerIterator(""), hooks, debug);
			while (true) {
				String str;
				hooks.print(">>> ");
				if ((str = hooks.read()) == null) {
					break;
				}
				
				try {
					TokenIterator iterator = new LexerIterator(str) {
						
						@Override
						public @NonNull Token next() {
							@NonNull Token next = super.next();
							if (debug && !Helpers.isSeparator(next)) {
								hooks.print("::: ");
							}
							return next;
						}
					};
					
					if (new TokenExecutor(iterator, interpreter.root, false).iterate().equals(TokenResult.QUIT)) {
						break;
					}
				}
				catch (Throwable e) {
					interpreter.printList.clear();
					
					while (e.getCause() != null && e.getMessage() == null) {
						e = e.getCause();
					}
					
					String message = e.getMessage(), error = e.getClass().getSimpleName();
					if (message != null && !message.isBlank()) {
						error += ": ";
						error += message;
					}
					
					hooks.debug("ERROR: " + error + "\n");
				}
			}
		}
		else {
			try (PushbackReader reader = Helpers.getPushbackReader(new FileReader(rootPath.toFile()))) {
				new Interpreter(input.args, new Lexer(reader), hooks, debug).run();
			}
			catch (Exception e) {
				throw Helpers.panic(e);
			}
		}
	}
}
