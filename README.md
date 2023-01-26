Dodd Simple Stack Language
==========================

Summary
-------

The Dodd Simple Stack Language (DSSL) is a stack-based language, inspired primarily by Postscript, Python and Java. The lexing component is generated with the [SableCC](https://sablecc.org/) parser generator. The language syntax specification can be found [here](https://github.com/tomdodd4598/Dodd-Simple-Stack-Language/blob/main/src/dssl.sable).

If you have any questions, feel free to post an issue or ping me in the [eVault](https://discord.gg/KCPYgWw) Discord server!

Documentation
-------------

#### Program Arguments

DSSL accepts an input of one file *maximum* to run. Additional files should be accessed with the `import` keyword (see below). If no file is specified, the interpreter runs in **console mode**.

DSSL also accepts any combination of options:
- `-d`: **Debug mode** will print the stack to the console every time a token action occurs (see below).
- `-n`: **Natives mode** will allow the implementation-dependent `native` keyword to be used.

For example, running the file `script.dssl` with natives enabled would be achieved with **`java -jar dssl.jar -n script.dssl`**, where `dssl.jar` is the DSSL executable jar.

#### Comments

Scripts ran by DSSL can include single-line and multi-line comments: either any text after and on the same line as a `#`, or any text between a `/*` and a `*/`. They will be treated as token separators by the interpreter.

#### The Basics

DSSL scripts are read as a list of **tokens**, consisting of keywords, symbols, operators, numbers, characters, strings and names separated by comments and whitespace (spaces, tabs, and newlines). Each token that is read has an associated **action** which interacts with the internal data structures of DSSL: the **stack** and the **hierarchy**.

The stack is the global last in, first out store for **elements**, which consist of values and labels. In order to *push* to the stack, we could simply write a token, such as a string value:
```
"Hello, world!"
```
We could then print this string, which will also *pop* it from the stack:
```
"Hello, world!" println
```
At the end of this program, the stack is empty. We can also define and modify variables:
```
/msg "What's you name?" def
msg println
/msg "Hello " read "!" ~ ~ =
msg println
```
Here, we use the `def` keyword to define a variable with the label `/msg`, and access the value using its associated identifier `msg`. The `read` keyword pushes a string from user input to the stack, and the concatenation operators `~` join the three separate strings together. Finally, the assignment operator `=` is used to modify the value of `msg` before being printed again.

The hierarchy keeps track of all currently accessible variables and classes for the current **scope** of the program. An executed script will have a global scope at the root of the hierarchy, but variables and classes defined within a **block**, denoted by a matching pair of braces `{` and `}`, will only be accessible within that block and further internal blocks. Imported scripts will have their own hierarchies, the root scope of which is added to the importing script once the import is complete. For example, the following code would print `inner_x, inner_y, inner_x, outer_y`:
```
/x "outer_x, " def
/y "outer_y" def
{
    /x "inner_x, " =
    /y "inner_y, " def
    x print
    y print
} exec
x print
y print
```
Here we see a key difference between the assignment operator `=` and the `def` keyword: the former modifies a pre-existing variable, while the latter makes a new **shadowing** definition in the current scope. Note that in DSSL, blocks have to be explicitly executed with the `exec` keyword, since they become elements on the stack (see below).

#### Keyword Arguments

In DSSL, the *n*th argument of an *m*-parameter keyword is defined as the element *(m - n)* positions away from the top of the stack. For example, the `roll` keyword, which cycles elements on the stack, requires two arguments:
```
3 # first argument (number of elements to roll)
1 # second argument (number of positions to roll by)
roll
```
This terminology is also used when refering to functions.

#### Block Elements

When the interpreter encounters an opening brace `{`, it moves from **execution** mode to **collecton** mode. While in collection mode, tokens will simply be accumulated rather than executed. The interpreter will return to execution mode once the closing brace `}` paired with the original opening brace is read, at which point a **block element** containing all tokens enclosed within the braces will be added to the stack.

Like any other element, block elements can be stored in a variable. Such variables are DSSL's equivalent of named **functions**. Block elements on the stack alone can be thought of as anonymous functions. For example, the function `isEven` is defined to pop an integer value and push a `true` or `false` boolean value:
```
# int -> bool
/isEven {
    1 & 0 ==
} def
```
Block elements are also used in control flow statements. The following program prints whether integers up to ten are even by using the `loop`, `if` and `ifelse` keywords:
```
/n 0 def
{
	/n ++							# Increment n
	n 10 > { break } if				# Break out of loop if n > 10
	n " is " ~						# Concatenate n with " is "
	n isEven exec					# Push result of isEven execution
	{ "even" } { "odd" } ifelse ~	# Concatenate previous string with "even" or "odd"
	println							# Print string
} loop
```
The keyword `break` causes the interpreter to end the current loop. The other related keywords are `continue`, which moves to the next loop iteration, and `quit`, which stops the entire program.

#### Primitive Types

DSSL has five **primitive** types built into the language: `int`, `bool`, `float`, `char` and `string`. Integers can be arbitrarily big, floats are double-precision, and strings, when viewed as an ordered collection of characters, share all functionality with tuples apart from the ability to cast to other container types (see below).

Primitive values are the arguments to all basic mathematical operators, such as addition `+`, multiplication `*` and concatenation `~`. If an operator is not defined for the arguments it pops from the stack, it will throw an error. Primitive variables can also be modified by **assignment operators** such as and `+=`, `*=` and `~=`, as in Python and Java, using their labels as the first argument.

Strings can be written in two ways: either with single-line syntax between two quotation marks `"`, or with multi-line syntax between two backticks `` ` `` similar to text blocks in Java.
```
"This is a single-line string." println
    `
    This is a multi-line string.
    The common whitespace at the start of each line is ignored.
        For that reason, this line will retain four spaces.
    The first backtick must be on its own line.` println
```
Strings can also be prefixed with an `r`, which will tell the interpreter to treat `\` as a literal character rather than the start of an escape sequence, just like raw strings in Python. The escape sequences in DSSL are the same single-character escapes as in Java, `\t`, `\b`, `\n`, `\r`, `\f`, `\'`, `\"` and `\\`, along with the additional backtick escape ``\` ``.

#### Container Types

Like Python, DSSL has some useful **container** types, `range`, `list`, `tuple`, `set` and `dict`, built into the language alonside the primitive types. All of them are constructed by the action of the type name with a **collection** on the stack.

A collection is an opening bracket element `[` followed by any number of elements and finally a closing bracket element `]`. When the collection is used to form the container, the bracket elements are discarded. For example, the following code will push a tuple containing three elements to the stack:
```
[ 2 "pi" 6.283 ] tuple
```
Lists and sets are constructed in an intuitively similar manner to the tuple above, but are mutable unlike tuples. Ranges are constructed using one to three arguments as in Python, and can be used in a similar way. A `foreach` loop, which iterates through a collection and executes a block after pushing each element to the stack, could be used to rewrite the example using `isEven` above:
```
[ 1 11 ] range {
	/n exch def
	n " is " ~
	n isEven exec
	{ "even" } { "odd" } ifelse ~
	println
} foreach
```
Instead of manually incrementing a counter and conditionally breaking the loop, we instead use the `def` keyword to store the value of the current yielded element locally. The `exch` keyword, which swaps the two top elements on the stack, is needed to put the label before the value.

Dictionaries are constructed by using each two elements in the collection as a key-value pair. The following code creates a dictionary variable `intWords` with two entries and adds one more with the `put` keyword:
```
/intWords [ 1 "one" 2 "two" ] dict def
intWords 3 "three" put pop
```
The final `pop` keyword removes the top element of the stack, which is the return result of the `put` keyword, equal to `null` since no value was overwritten.

Many of DSSL's keywords, such as `contains`, `clear` and `get`, perform actions on containers. As mentioned above, they can also act on strings.

#### Type Casts

Some types can be **cast** to other types, which means that values with particular types can be transformed into values of different types. Any keyword requiring a value of a particular type will attempt an **implicit** cast on the value it pops from the stack, while use of the type keywords will cause **explicit** casts. If an implicit cast fails, it may or may not cause the action of the keyword causing it to throw an error, while a failed explicit cast will immediately throw an error.

Casts can also be made dynamically by using the `type` and `cast` keywords: the former pops an element and pushes an element storing its type, while the latter explicitly casts its first argument to the type stored by the second argument.

#### Importing
The `import` keyword is used to dynamically run other scripts. It requires two arguments: a label as the first and the script's path relative to the interpreter's working directory as the second. The script will be executed with its own root scope, and once finished, its hierarchy will become a **sub-hierarchy** of the current scope, accessible using the name of the label and **member identifiers**. This behaviour is similar to **namespacing** in other languages. For example, if an imported script defines a variable `x`, it can be accessed in the following way:
```
/Other "relative/path/to/other.dssl" import

/Other .x 10 *=		# Modify Other .var
Other .x println	# Print Other .var
```
More variables can also be added to the sub-hierarchy from outside the imported script:
```
/Other .y 0.577 def	# Define Other .y
Other .y println	# Print Other .y
```

An important note is that although sub-hierarchies are scoped, the definitions *within* sub-hierarchies are not. For example, a new definition of `Other .var` within an executed block element will not shadow the definition accessible in the outer scope.

#### Classes

DSSL adds support for object-oriented programming through classes. A class is defined using the `class` keyword with a label and block element as the first and second arguments, respectively. Once defined, the class's hierarchy becomes a sub-hierarchy of the current scope, much like the hierarchy of imported scripts. For that reason, classes can simply be used to construct namespaces without imports:
```
/Math {
    /pi 3.14159265358979323846 def
    /e 2.7182818284590452354 def
    
    /factorial {
		/result 1 def
        1 + [ 1 3 2 roll ] range {
			/result exch *=
		} foreach
		result
    } def
} class

"pi + e = " Math .pi Math .e + ~ println
"52! = " 52 Math .factorial exec ~ println
```
The `new` keyword is used to create **instances** of a class from a **class element** on the top of the stack, which can be created by using the class's identifier. The following defines two variables `a` and `b` as instances of the `Complex` class and prints them:
```
/Complex {
	/init {
		/this exch def		# Save instance to local variable
		/this .im exch def	# Define imaginary component to second argument
		/this .re exch def	# Define real component to first argument
		this				# Push instance back to the stack
	} magic
	
	/i 0.0 1.0 Complex new def
	
	/mult {
		dup .re /yre exch def .im /yim exch def
		dup .re /xre exch def .im /xim exch def
		xre yre * xim yim * - xre yim * xim yre * + Complex new
	} def
	
	/toString {
		/this exch def
		this .re string
		this .im 0.0 >= { '+' ~ } if
		this .im string ~ 'i' ~
	} def
} class

a Complex .toString exec println	# static access
b .toString exec println			# instance access
```
The instances are converted to strings by executing the `toString` function. While `a` is converted using a **static access** to the function, the interpreter knows that `b` is an instance of `Complex` and so performs an **instance access**, which first pushes `b` to the stack before pushing the function, resulting in the same outcome. The following is another example of instance access, used to define `c` as the product of `a` and `b`:
```
/c a b .mult exec def
```

The `magic` keyword is used to define functionality which can not be directly used in DSSL code. Currently, it is only used to define an **initializer** `init`, which is executed directly after the `new` keyword had pushed the new instance to the stack. It can be used to define and initialize local variables which all instances of the class will contain. If any instances of a class are created within the class definition itself, they must be created after the initializer has been defined.

Note that individual instances of classes can have their own unique local variables:
```
/c .shout { "Hello!" println } def
c .shout exec
```

#### DSSL 3 Planned Features
- Class extensions and inheritance.
- A `macro` keyword to define immediately-executing functions.
- Operator overloading with `magic` definitions.
- Container type keywords replaced with member-accessible macros.

#### Language Tokens

Below is a list of all keywords in DSSL and their associated actions. The expected arguments are written in italics.

- `{`: Denotes the start of a block.
- `}`: Denotes the end of a block.


- `[`: Pushes a left bracket.
- `]`: Pushes a right bracket.


- (*`label string`*) `import`: Executes the file specified by the string and stores its root hierarchy to the sub-hierarchy specified by the label.
- (*`arguments... string`*) `native`: Implementation-dependent keyword. This DSSL interpreter uses reflection to access the Java field or method specified by the string, which must be a fully qualified name. If a method is accessed, the elements preceding the string will be used as arguments, with the final argument being used as the instance if it is an instance method. DSSL will automatically convert between stack elements and native Java objects where possible.


- (*`label value`*) `def`: Defines the variable specified by the label and initialises it to the value.
- (*`label block`*) `class`: Defines the class specified by the label.
- `magic`: Used to define special functionality.
- (*`class`*) `new`: Creates a new instance of the class.


- (*`elem1 elem2`*) `exch`: Swaps the positions of the top two elements on the stack.
- (*`elem`*) `pop`: Pops the element from the stack.
- (*`elem`*) `dup`: Shallow copies the element on the top of the stack. The two elements will refer to the same underlying object.
- (*`elem`*) `clone`: Clones the element on the top of the stack. The two elements will refer to different underlying objects.


- (*`number distance`*) `roll`: Cycles the specified number of elements on the top of the stack clockwise by the specified distance.
- (*`number`*) `rid`: Pops the specified number of elements from the stack.
- (*`number`*) `copy`: Shallow copies the specified number of elements on the top of the stack in the same order.


- (*`n`*) `index`: Pushes the element *n* positions away from the top of the stack.
- `count`: Pushes an integer equal to the total number of elements on the stack.
- (*`label`*) `countto`: Pushes an integer equal to the number of elements on the stack before the first label matching the argument.


- `read`: Pushes a string read from the implementation-defined IO (console input for this interpreter).
- (*`elem`*) `print`: Prints the element to the implementation-defined IO (console output for this interpreter).
- (*`elem`*) `println`: Prints the element and adds a newline.
- (*`string`*) `interpret`: Executes the string as DSSL code.


- (*`elem`*) `int`: Explicitly casts the element to an integer.
- (*`elem`*) `bool`: Explicitly casts the element to a boolean.
- (*`elem`*) `float`: Explicitly casts the element to a float.
- (*`elem`*) `char`: Explicitly casts the element to a character.
- (*`elem`*) `string`: Explicitly casts the element to a string.


- (*`elem`*) `range`: Constructs a range from a collection if the element is a closing bracket, otherwise explicitly casts the element to a range.
- (*`elem`*) `list`: Constructs a list from a collection if the element is a closing bracket, otherwise explicitly casts the element to a list.
- (*`elem`*) `tuple`: Constructs a tuple from a collection if the element is a closing bracket, otherwise explicitly casts the element to a tuple.
- (*`elem`*) `set`: Constructs a set from a collection if the element is a closing bracket, otherwise explicitly casts the element to a set.
- (*`elem`*) `dict`: Constructs a dictionary from a collection if the element is a closing bracket, otherwise explicitly casts the element to a dictionary.


- `null`: Pushes the null element to the stack.
- (*`elem`*) `hash`: Pushes an integer equal to the hash code of the element.


- (*`container block`*) `foreach`: Executes the block once for each element in the container, pushing each element before each iteration.
- (*`container`*) `unpack`: Pushes all elements in the container to the stack.


- (*`container`*) `size`: Pushes an integer equal to the number of elements in the container.
- (*`container`*) `empty`: Pushes a true boolean if the size of the container is zero, and a false boolean otherwise.


- (*`elem container`*) `contains`: Pushes a true boolean if the container contains the element, and a false boolean otherwise.
- (*`elem container`*) `add`: Adds the element to the container.
- (*`elem container`*) `remove`: Removes the element from the container if present.
- (*`container1 container2`*) `containsall`: Pushes a true boolean if the second container contains all elements of the first container, and a false boolean otherwise.
- (*`container1 container2`*) `addall`: Adds all elements of the first container to the second container.
- (*`container1 container2`*) `removeall`: Removes all elements of the first container from the second container which are present.
- (*`container`*) `clear`: Removes all elements from the container.


- (*`elem container`*) `get`: Pushes the value with the key or index of the element, or null if the key has no associated value.
- (*`elem1 elem2 container`*) `put`: Puts the first and second elements into the container as a key-value pair.
- (*`container1 container2`*) `putall`: Puts all key-value pairs of the first container into the second container.


- (*`elem dict`*) `containskey`: Pushes a true boolean if the dictionary contains a key equal to the element, and a false boolean otherwise.
- (*`elem dict`*) `containsvalue`: Pushes a true boolean if the dictionary contains a value equal to the element, and a false boolean otherwise.
- (*`key value dict`*) `containsentry`: Pushes a true boolean if the dictionary contains the specified key-value pair, and a false boolean otherwise.
- (*`dict`*) `keys`: Pushes a container of the dictionary's keys.
- (*`dict`*) `values`: Pushes a container of the dictionary's values.
- (*`dict`*) `entries`: Pushes a container of the dictionary's key-value pairs.


- (*`elem`*) `type`: Pushes the type of the element.
- (*`elem type`*) `cast`: Explicitly casts the element to the type.


- (*`block`*) `exec`: Executes the block.
- (*`bool block`*) `if`: Executes the block if the boolean is true.
- (*`bool block1 block2`*) `ifelse`: Executes the first block if the boolean is true, and the second block otherwise.
- (*`int block`*) `repeat`: Executes the block the number of times specified by the integer.
- (*`block`*) `loop`: Executes the block continually.


- `quit`: Ends the program.
- `continue`: Jumps to the next iteration of the current loop.
- `break`: Ends the current loop.


- (*`label elem`*) `=`: Assigns the value of the element to the variable associated with the label.


- (*`label`*) `++`: Increments the value of the variable associated with the label.
- (*`label`*) `--`: Decrements the value of the variable associated with the label.


- (*`label elem`*) `+=`: Addition assignment.
- (*`label elem`*) `&=`: AND assignment.
- (*`label elem`*) `|=`: OR assignment.
- (*`label elem`*) `^=`: XOR assignment.
- (*`label elem`*) `-=`: Subtraction assignment.
- (*`label elem`*) `~=`: Concatenation assignment.


- (*`label elem`*) `<<=`: Left-shift assignment.
- (*`label elem`*) `>>=`: Right-shift assignment.


- (*`label elem`*) `*=`: Multiplication assignment.
- (*`label elem`*) `/=`: Division assignment.
- (*`label elem`*) `%=`: Remainder assignment.
- (*`label elem`*) `**=`: Power assignment.
- (*`label elem`*) `//=`: Integer division assignment.
- (*`label elem`*) `%%=`: Modulo assignment.


- (*`elem1 elem2`*) `==`: Pushes a true boolean if the elements are equal, and a false boolean otherwise.
- (*`elem1 elem2`*) `!=`: Pushes a true boolean if the elements are not equal, and a false boolean otherwise.


- (*`elem1 elem2`*) `<`: Pushes a true boolean if the first element is less than the second element, and a false boolean otherwise.
- (*`elem1 elem2`*) `<=`: Pushes a true boolean if the first element is less than or equal to the second element, and a false boolean otherwise.
- (*`elem1 elem2`*) `>`: Pushes a true boolean if the first element is greater than the second element, and a false boolean otherwise.
- (*`elem1 elem2`*) `>=`: Pushes a true boolean if the first element is greater than or equal to the second element, and a false boolean otherwise.


- (*`elem1 elem2`*) `+`: Pushes the sum of the two elements.
- (*`elem1 elem2`*) `&`: Pushes the AND value of the two elements.
- (*`elem1 elem2`*) `|`: Pushes the OR value of the two elements.
- (*`elem1 elem2`*) `^`: Pushes the XOR value of the two elements.
- (*`elem1 elem2`*) `-`: Pushes the subtraction of the second element from the first element.
- (*`elem1 elem2`*) `~`: Pushes the concatenation of the second element to the first element.


- (*`elem1 elem2`*) `<<`: Pushes the value of the first element left-shifted by the second element.
- (*`elem1 elem2`*) `>>`: Pushes the value of the first element right-shifted by the second element.


- (*`elem1 elem2`*) `*`: Pushes the product of the two elements.
- (*`elem1 elem2`*) `/`: Pushes the division of the first element by the second element.
- (*`elem1 elem2`*) `%`: Pushes remainder of the division of the first element by the second element.
- (*`elem1 elem2`*) `**`: Pushes the value of the first element to the power of the second element.
- (*`elem1 elem2`*) `//`: Pushes the integer division of the first element by the second element.
- (*`elem1 elem2`*) `%%`: Pushes the value of the first element modulo the second element.


- (*`elem`*) `not`: Pushes the logical inverse of the element.
- (*`elem`*) `neg`: Pushes the negative of the element.


- (*`label`*) `deref`: Pushes the value associated with the label.

Permissions
-----------

In practice, the license on any code I write means very little, but for those who want a some semblance of formality, let it be stated that all code is available under the [MIT License](https://github.com/tomdodd4598/Dodd-Simple-Stack-Language/blob/main/LICENSE.md).
