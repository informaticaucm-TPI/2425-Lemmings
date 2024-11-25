<!-- TOC start -->
- [Assignment 3: Exception handling and file handling](#practica-3-excepcionesYficheros)
- [Introduction](#introduccion)
- [Exception handling](#exceptions)
	- [Exceptions thrown in the control part of the program](#command-exceptions)
	- [Exceptions thrown in the model part of the program](#gamemodel-exceptions)
- [File handling](#files)
	- [Serialization / deserialization](#serialization)
	- [Saving the game state to file: the `save` command](#save-command)
	- [Loading the game state from file: the `load` command](#load-command)
	- [Adapting the `reset` method of the `Game` class](#reset-load-game)
	- [Details of the `save` command (optional)](#save-command-details)
	- [Initial configurations of the game (optional)](#level-conf)
<!-- TOC end -->

<!-- TOC --><a name="practica-3-excepcionesYficheros"></a>
# Assignment 3: Exception-handling and file handling

**Submission: December 2nd 2024, 12:00**

**Objectives:** handling exceptions and files

<!--
**Preguntas Frecuentes**: Como es habitual que tengáis dudas (es normal) las iremos recopilando en este [documento de preguntas frecuentes](../faq.md). Para saber los últimos cambios que se han introducido [puedes consultar la historia del documento](https://github.com/informaticaucm-TPI/2425-Lemmings/commits/main/enunciados/faq.md).
-->

<!-- TOC --><a name="introduccion"></a>
# Introduction

In this assignment we extend the functionality of the lemmings game of the previous
assignment in two ways:

- *Exception handling*: errors that may occur during the execution of the application
can be more effectively dealt with using the exception mechanism of the Java language.
An exception object encapsulating any information about an error and its context that
is considered to be of interest, in particular, an error message, is created at the
point in the code where the error occurs and is then passed between the methods of
the call stack. As well as making the program more robust, this mechanism enables the
user to be informed about the occurrence of an error in whatever level of detail is
considered appropriate, while at the same time providing a great deal of flexibility
in regard to where the error is handled (in particular, to print an error message).

- *File handling*: a useful addition to the application is the facility to save the
state of a game to file and load the state of a game from file. The loading of the
initial state will than be a particular case of this general mechanism. To this end,
we add two new commands, one to write to a file and the other to read from a file.
The use of the *command pattern* introduced in the previous assignment greatly
facilitates the addition of new commands.
  
<!-- TOC --><a name="exceptions"></a>
# Exception handling

In this section, we present the exceptions that should be handled by the application and
give some information about their implementation. The section is divided into two parts,
the first dealing with exceptions that may be thrown in the control part of the
application, in particular, in the parsing of the user input, the second dealing with
exceptions that may be thrown in the model part of the application, in particular, in
the implementation of the execution of the commands.

It should be pointed out that file handling inevitably involves exception handling, particularly
when reading data from files, which is why these two topics are often introduced to students at
the same time. Exceptions that may be thrown during file handling (i.e.
during the execution of the `save` and `load` commands, or during the loading of the
initial state) will be dealt with in the file-handling part of this document.

You will have observed that there are circumstances in which a
command may fail, either in its parsing or in its execution. For example, the parsing
of the `setRole` command will fail if the user does not provide that correct number of
parameters or if the user provides letters where a number is expected. In the previous
assignment, on occurrence of such errors, the `parse` method of the `SetRoleCommand`
class and that of the `CommandGenerator` class simply returned `null`. Returning `null`
in case of error does not transmit any indication about the reason for the occurrence
of the error, nor does it permit the transmission of any other data about the error
that may be required to handle it correctly.
As another example, the execution of the `setRole` command will also fail if the
position of the lemming given by the user is outside the board or does not, in fact,
contain a lemming.
In the previous assignment, the occurrence of such an error was communicated to
the `execute` method of the `SetRoleCommand` class via a boolean return value (an alternative
could be to use some ad-hoc mechanism involving specific methods in the `Controller` class but,
apart from the problem of being ad-hoc, this would oblige the game to know about the controller).
Returning the value `false` to communicate the occurrence of an error has the same
problems as returning `null` for this same purpose.

In this assignment, we deal with these issues using the Java exception mechanism. An
exception mechanism provides a flexible communication channel between the location in
the code where an error occurs and the location in the code where that error is handled,
along which any required data concerning the occurrence of the error can be sent from
the former to the latter[^1]. In many cases, the data concerning the occurrence of the error
that is transmitted from one code location to another via an exception consists simply of
an error message, and the handling of this error consists simply of sending that message
to the standard output to be displayed on the screen. In the general case, however, more
data about the error and its context may be transmitted between code locations and the
error-handling may require more complex actions than simply printing a message to the
screen. In this application, the exception mechanism enables us to ensure that
the controller is responsible for sending all error messages to the view to be displayed.

[^1]: The error code mechanism of C is somewhat primitive in comparison, though it is also much
less computationally costly, which is why C++ retains it as well as having an exception mechanism
(though this exception mechanism is less type-checked and more difficult to use than the Java
equivalent)

For simplicity, we recommend that you place all programmer-defined exception classes
in a package called `tp1.exceptions`.


<!-- TOC --><a name="command-exceptions"></a>
## Exceptions thrown in the control part of the program

We define a new exception class called `CommandException` and two subclasses:

- `CommandParseException`: used for exceptions that occur during the parsing of a command
  (so during the execution of the `parse` method of the `Command` class)
	
- `CommandExecuteException`: used for exceptions that occur during the execution of a command
  (so during the execution of the `execute` method of the `Command` class)

### Parsing errors

First we deal with the occurrence of errors during the parsing of the commands, which will lead
to the throwing of a `CommandParseException`, instead of returning `null` as was done in the previous
assignment. 

The `parse` method of the `Command` class must now be declared to throw exceptions of type 
`CommandParseException`.

```java
  public abstract Command parse(String[] parameter) throws CommandParseException;
```
For example, the `parse()` method of the `NoParamsCommand` can now be implemented as follows:

```java
  public Command parse(String[] commandWords) throws CommandParseException {
    if (commandWords.length < 1 || !matchCommandName(commandWords[0]))
      return null;
            
    if (commandWords.length == 1 && matchCommandName(commandWords[0]))
      return this;
        
    throw new CommandParseException(Messages.COMMAND_INCORRECT_PARAMETER_NUMBER);
  }
```

Note that the `parse` method of the commands must only throw an exception if the first word
of the input data matches the command name and there is an error in the command
arguments. Not matching the command name is not an error but simply an indication that
the input text does not correspond to this particular command so, in this case, the `parse` method
must not throw an exception and must return `null` as it did in the previous assignment,
to enable the `parse` methods of the other commands to also check for a match.

The `parse` method of the `CommandGenerator` class must now also be declared to throw exceptions of
type  `CommandParseException` in order to transmit to the controller the exceptions thrown by the
commands:

```java
	public static Command parse(String[] commandWords) throws CommandParseException
```

but also to transmit exceptions of type `CommandParseException` thrown by itself, in the case
where none of the `parse` methods of the commands recognises the first word of the
input data, i.e. the case of an unknown command (instead of returning `null` and having the
controller handle this case with an *if-then-else*):

```java
	throw new CommandParseException(Messages.UNKNOWN_COMMAND.formatted(commandWords[0]));
```

The `NumberFormatException` is a system exception that is thrown when an attempt is made to parse
the `String`-representation of a number and convert it to the corresponding value of type
`int` or `long` or `float`, etc., in the case where the input string does not, in fact, represent
a number and cannot, therefore, be so converted. A `NumberFormatException` should be caught
in the `parse` method of the command where it occurs and wrapped in a `CommandParseException`.
The term "wrapping" refers
to the good practice of catching a lower-level exception and throwing a higher-level exception
which includes it (accomplished by passing the lower-level exception as argument to the constructor
of the higher-level exception) and *which contains a less specific message than the low-level one*[^2].

[^2]: You will probably have seen examples of bad practice in this regard, e.g. electronic commerce web
applications that produce messages for the user of the type "SQLException...". The user does not care about
this level of implementation detail and should not be receiving this type of message.

For example, in the `parse` method of the `SetRoleCommand` class you should do something similar
to the following:

```java
	} catch (NumberFormatException nfe) {
		throw new CommandParseException(Messages.INVALID_POSITION.formatted
			(Messages.POSITION.formatted(row, col)), nfe);
	}
```

### Execution errors

An error that occurs during the execution of a command will lead to the throwing of a
`CommandExecuteException` in the body of the `execute` method of the commands. This exception will
be used to wrap lower-level exceptions thrown on the occurrence of errors in the model
part of the application (we deal with these errors in detail in the next section).

The `execute()` method of the `Command` class must now be declared to throw exceptions of type 
`CommandExecuteException`:

```java
	public abstract void execute(GameModel game, GameView view) throws CommandExecuteException;
```

### Capturing parsing errors and execution errors in the controller

As already stated, both types of exceptions, `CommandParseException`s and `CommandExecuteException`s,
reach the controller, which is responsible for catching them and sending the 
messages they contain to the view for display. In the case where an exception wraps a lower-level
exception (which may, in turn, wrap an even lower level exception etc.), the controller sends
all levels of error messages to the view for display. 

The `run()` method of the controller will now contain the following code. Notice that to avoid
extra lines of code, we are using the fact that in Java, the assignment operator evaluates to the
value being assigned (as it does in C).


```java
	public void run() {
		...
		while (!game.isFinished()) {
			...
			try { ... }
			catch (CommandException e) {
				view.showError(e.getMessage());
				Thowable wrapped = e;
				// display all levels of error message
				while ( (wrapped = wrapped.getCause()) != null )
					view.showError(wrapped.getMessage());
			}
		}
		...
	}
```

In a real application, of course, a normal user would
almost certainly not be interested in seeing the messages from all the different levels (see the
above footnote about the bad practice in many electronic commerce applications of displaying
*SQLExceptions* to normal users), though an administrator may be, and comprehensive information
about errors is also what is required if it is to be written to an error log.

Finally, notice that, usually, both the parsing and the execution of commands with parameters 
throw more types of exception than do the parsing and execution of commands without parameters.
In the case of the `setRole` command, the parsing of its parameters may generate
`CommandParseException`s, or system exceptions that we wrap in `CommandParseException`s, and its
execution in the model part of the application may generate exceptions that we wrap in
`CommandExecuteException`s.

<!-- TOC --><a name="gamemodel-exceptions"></a>
## Exceptions thrown in the model part of the program

As stated above, the errors in the execution of commands arise in the game logic, that is, in the
model part of the program and, in the previous assignment, the methods involved returned a `boolean`
value to indicate whether the execution had succeeded or failed. For example, the `setRole` method
of `GameModel` returned `false` when the position passed as argument was outside the board or when
no lemming is present on that position. However, returning the value `false` did not permit these
two types of error to be distinguished. Generating error data, in particular an error message, at
the point in the code where the error occurs enables such distinction. We could maintain the
boolean to indicate whether or not the game state has been modified. The program con now display
the following error messages (notice the two messages, one from each level of exception):

- On trying to apply `setRole` to a position with no lemming:

	```
	[DEBUG] Executing: setRole Walker A 2

	[ERROR] Error: Command execute problem
	[ERROR] Error: No lemming in position (3,2) admits role Walker
	```

- On trying to apply `setRole` to a position outside the board:

	```
	[DEBUG] Executing: setRole Walker A 25

	[ERROR] Error: Command execute problem
	[ERROR] Error: Position (0,24) off the board
	```

<!--- PARSER errors
- Trying to change the role of a lemming in an invalid position:

	```
	[DEBUG] Executing: setRole Walker None 2

	[ERROR] Error: Invalid command parameters
	[ERROR] Error: Invalid position: (None,3)
	```

- Trying to set the role of the lemming to an unknown role:

	```
	[DEBUG] Executing: setRole Slepper A 2

	[ERROR] Error: Invalid command parameters
	[ERROR] Error: Unknown role: Sleeper
	```
---> 

We define a new exception `OffBoardException` to be thrown when there is an attempt to access a position
which is outside the board, which the method `setRole` of the `GameModel` interface is now declared to throw:

```java
public boolean setRole(LemmingRole role, Position pos) throws OffBoardException;
```

Similarly, we define the following exceptions, to be thrown by other methods of the `GameModel`
interface:

- `GameParseException`: to be thrown when parsing the text representation of a game object; this class has two subclasses:

   - `RoleParseException`: to be thrown by the `LemmingRoleFactory` class on parsing a `String` that does not
     correspond to any known role (c.f. the `CommandParseException` thrown by the `parse` method of the
     `CommandGenerator` class).
 
   - `ObjectParseException`: to be thrown during deserialization (see below) when trying to parse a `String`
     that does not correspond to the text representation of the expected game object.
	
as well as an exception class from which all these new exceptions inherit: `GameModelException`

The above exceptions are to be thrown by methods of the `GameModel` interface that are called from one or more
of the `execute` methods of the commands. As already stated, they should be caught in the corresponding `execute`
method and rethrown, wrapped in a `CommandExecuteException`. For example:

```java
} catch (OffBoardException obe) {
	throw new CommandExecuteException(Messages.ERROR_COMMAND_EXECUTE, obe);
}
```

With this procedure, no information is lost since the wrapped exception can be recovered using the
`getCause` method of the `Throwable` class (see the above code for the `run` method of the controller).

<!-- TOC --><a name="files"></a>
# File-handling

<!-- TOC --><a name="serialization"></a>
## Serialization / deserialization

In computing, the term *serialization* refers to converting computing structures into a stream
of bytes usually with the objective of saving this stream to a file or transmitting it on a network.
Commonly, the structure being serialized is the current state of an executing program, or of part
of an executing program. The term *deserialization*
refers to the inverse process of reconstructing the computing structures, commonly the state of
an executing program, or part of an executing program, from a stream of bytes.
Serialization/deserialization in which the
generated stream is a text stream is sometimes referred to as stringification/destringification.
Clearly, the format used for serialization/stringification should be designed in such a way
as to facilitate deserialization/destringification.

Java incorporates a generic serialization mechanism capable of converting any Java objects,
and therefore any executing Java program or part of any executing Java program,
into a binary stream (see classes `ObjectInputStream` and `ObjectOutputStream`).
We do not require such a generic serialization/deserialization
mechanism; our interest is simply to serialize to, and deserialize from, a text stream
that represents the current state of a lemmings game, with a view
to writing this state to, and reading this state from, a text file. Note that the textual
representation currently produced by the view is not a suitable format for serialization since
deserialization of this format would be a rather complicated enterprise. We therefore
define a text-serialized format, in which the state of the game is represented as a sequence
of game elements, each of which is represented as a sequence of values. This format could be
useful for debugging purposes as well as for saving the state of the game to a text file.

### Serialization format

We explain our text-based serialization format via a simple example.

The following represents the serialization of a game comprising one lemming, two walls
and an exit door:

```
0 1 0 0 2 
(3,2) Lemming RIGHT 1 Walker
(4,2) Wall
(4,3) MetalWall
(5,4) ExitDoor
```

The numbers on the first line represent the counter data, from left to right:

- the cycle number
- the number of lemmings on the board
- the number of dead lemmings
- the number of lemmings that have successfully exited the game
- the number of lemmings that need to succesfully exit to win the game.

Each of the following lines describes a game object, according to the pattern

```
    gameObjectPosition gameObjectType gameObjectAttributes
```

where the lemming is the only game object that has attributes. In the above example, the first of the
textual representations of a game object represents, from left to right:

- a game object situated in row `3` and column `2` (which corresponds to the user coordinates: row `D` and column `3`)
- which is a lemming
- whose direction of movement is `RIGHT`
- which is falling and has fallen `1` vertical position
- whose current role is `Walker`

Note that the type of game object can be identified either by its full name or by its short name, so
`Lemming`, `Wall`, `MetalWall` and `ExitDoor` can be represented by `L`, `W`, `MW` o `ED` respectively.


<!-- TOC --><a name="save-command"></a>
## Saving the game state to file: the `save` command

After defining our serialization format, the next logical step is to introduce a new command,
the `save` command, used to save the serialized current state of the game in a file.
However, since *implementation of this command is optional*, we postpone the details of
its presentation until later in this document, see the section
[Details of the `save` command (optional)](#save-command-details).


<!-- TOC --><a name="load-command"></a>
## Loading the game state from file: the `load` command

In the following sections we present an implementation of a new command, the `load` command,
used to read a state of the game from a file. The implementation of the `load` command
is inevitably more complicated than that of the corresponding `save` command. We divide the
task of implementing this command into the following subtasks:

- [create a `GameObjectFactory` class](#game-object-factory) to be used to parse the textual
  representation of the game objects,

- [define a `GameConfiguration` interface](#game-configuration) to be implemented by classes
  representing a game configuration, i.e a valid state of the lemmings game,

- [create a `FileGameConfiguration` class](#game-configuration-loader), responsible for
  loading the serialized data from file and storing it in a game configuration,

- [add the implementation of the load command to the `Game` class](#game-load),

- [create the `LoadCommand` class](#load-command-class).

On finishing these changes, we will need to check the exceptions that may be thrown on
loading a file, though, regarding errors in the content of the file, we recommend first
developing the code under the assumption that the file contains no such errors and then
modifying it to add the handling of the exceptions that may be produced.

Note that the behaviour of the `reset` command will need to be adapted to the existence
of the `load` command (reset should mean return to the last loaded state).


<!-- inner TOC --><a name="game-object-factory"></a>
### Game object factory

Every line of the above-described serialization format, except the first, contains a textual
representation of a game object. The deserialization, therefore, must create
a game object from each such textual representation. To do so, we create a `GameObjectFactory`
class, similar to the `LemmingRolesFactory` class, containing the following method:

```java
public GameObject parse(String line, GameWorld game) throws ObjectParserException, OffBoardException;
```
This method throws the following programmer-defined exceptions:

- `ObjectParserException`: thrown when a line cannot be parsed as the textual representation of
  a game object due to some problem with the format such as having too many components, involving
  an unknown object or and unknown role, numerical data containing non-digit characters, etc.

- `OffBoardException`: thrown when a position is off the board (this also covers negative values
  being used).

Recall that, as stated in the section on exception-handling, `ObjectParserException` is a
subclass of `GameParseException`, and both this latter exception and `OffBoardException` are
subclasses of `GameModelException`.

Your code will be more readable if the parse method calls auxilliary methods. We suggest the use
of the following (though this is not obligatory):

```java
private static Position getPositionFrom(String line) throws ObjectParseException, OffBoardException {...}
private static String getObjectNameFrom(String line) throws ObjectParseException {...}
private static Direction getLemmingDirectionFrom(String line) throws ObjectParseException {...}
private static int getLemmingHeigthFrom(String line) throws ObjectParseException {...}
private static LemmingRole getLemmingRoleFrom(String line) throws ObjectParseException {...}
```

These methods can take care of both checking the correct syntax and checking the static semantic
properties such as whether or not a position is off the board. Note that, like the `parse`
method of the `CommandGenerator` class, the `parse` method of the  `GameObjectFactory` class
never returns `null`; if the creation of a game object is not successful, it throws an
exception

<!-- inner TOC --><a name="game-configuration"></a>
### Representing the loaded state of the game: the `GameConfiguration` interface

A game state can be encapsulated in an object that stores the different components of such
a state, namely the value of the game counters and the set of game objects, where we will
assume that the latter is contained in a newly-created object of the `GameObjectContainer` class.
Any object representing a game state must provide methods to access the different components
of this state, i.e. it must implement an interface containing the following method declarations:

 ```java
	// game status
	public int getCycle();
	public int numLemmingsInBoard();
	public int numLemmingsDead();
	public int numLemingsExit();
	public int numLemmingToWin();
	// game objects
	public GameObjectContainer getGameObjects();
 ```

We will define this set of method declarations to be those of a `GameConfiguration` interface,
to be placed in the `tp1.logic` package.
If you wish to avoid repeating code, you could define an interface called, for example, `GameCounters`
containing the first five of the above methods and then inherit the `GameCounters` interface
in both the `GameConfiguration` interface and the `GameStatus` interface.


<!-- inner TOC --><a name="game-configuration-loader"></a>
### Reading the file data: the `FileGameConfiguration` class

As a general principle, reading from a file must *never* cause a program to crash
or leave it in an incoherent state. Catching *all* exceptions that could be thrown when reading 
from a file ensures that the program cannot crash. If the load fails, the program can handle
the exception, informing the user, and continue the game exactly as if the load had not been
attempted. Loading the file data into a special-purpose
class which is then only used (in our case as the new state of the game) if and when the data
has been completely and successfully loaded from file ensures that the program cannot be left
in an incoherent state. In our case, this special-purpose class is the `FileGameConfiguration`
class, to be placed in the `tp1.logic` package, which also implements the `GameConfiguration`
interface. To facilitate ensuring that an incoherent game state is never used as the new game state,
we perform the loading from file in the constructor of the `FileGameConfiguration`. Thus,
if the checking of validity is exhaustive, objects of this class that encapsulate an incoherent
game state can never exist. This constructor has two parameters:

- a `String` containing the name of the file from which the game state is to be loaded
- the game, typed as `GameWorld`, to be passed to the constructor of the game objects (via
  the `parse` method of each game object, via the `parse` method of `GameObjectFactory`).

```java
public FileGameConfiguration(String fileName, GameWorld game) throws GameLoadException;
```

The constructor can throw the following programmer-defined exception:

- `GameLoadException` (a subclass of `GameModelException`): used to wrap *any* exception thrown
  during loading, such as `FileNotFoundException`, any operating system exception,
  `ObjectParserException`, `OffBoardException`, or any other exception thrown due to an
  incorrect file format. 

Correct file format also includes the condition that the counter data of the first line of
the file should be coherent with the game object data of the subsequent lines
(consider the number of lemmings on the board, for example). Notice that this particular
validation would be easier to implement had the counters been implemented as static attributes
of the `Lemming` class rather than as instance attributes of the game. However, you are not
required to implement this validation, though you may do so if you wish. Correct file format
also includes conditions such as that the direction is a valid direction, counters do not
contain negative values, etc. If any of these conditions were incorrect, an exception should
be thrown which would then be wrapped in a `GameLoadException`. Again, you are not required
to implement this validation, though you may do so if you wish.

<!-- inner TOC --><a name="game-load"></a>
### Adding the implementation of the load command to the `Game` class

We need to add the possibility of loading the state of the game from file to the model
part of the application. To this end, we add a function called `load` to the `Game` class
(we will also need to add it to the corresponding interface): 

```java
	public void load(String fileName) throws GameLoadException {...}
```

This method simply creates a `FileGameConfiguration` object, typed as a `GameConfiguration`
and then sets the attributes of the game to the values returned by calls to the methods of the
`GameConfiguration` interface. It declares that it may throw the same exceptions that the
constructor of the `FileGameConfiguration` class declares it may throw.

<!-- inner TOC --><a name="load-command-class"></a>
### The `LoadCommand` class

We can now implement the `LoadCommand` class. The `execute` method could either call a
`load` method in the `Game` class, passing it the file-name `String` or open an input
stream to the file and then call a `load` method in the `Game` class, passing it the
input stream. For consistency of style of implementation across the different commands,
and as stated in the previous section, we propose the former. After loading the new state,
the `execute` method of the `LoadCommand` class must display the board.

Regarding the `parse` method, the `load` command could be implemented without parameters,
the name of the file to be used being stored in an attribute of the `LoadCommand` class, allowing
only a single saved file, or it could be implemented with one `String` parameter:
the file name. You are expected to implement the command with a file-name parameter so the
`parse` method will have to deal with this parameter.

The help message of the `load` command will have the following form:

```
[DEBUG] Executing: help

Available commands: 
   ...
   [l]oad <fileName>: load a state of the game from the text file <fileName>
   ...
```

<!--

## Errors occuring during the loading of a file

***** This section is not present in the english version since all of the errors have already been
***** discussed and all of the execution traces in the spanish version show a maximum of two error
***** messages being displayed while in the english-group spec. more than two may be produced
***** (there are more levels of wrapping).

-->

<!-- TOC --><a name="reset-load-game"></a>
## Adapting the `reset` method of the `Game` class

Resetting a game that has been loaded from file should place the game in the state it was in immediately
after the loading took place. This can be accomplished by having the `load` method of the `Game` class 
store the `FileGameConfiguration` object created during loading in an attribute of the `Game` class, e.g.:

```java
private GameConfiguration fileloader;
```

If the value of this attribute is `null`, the standard reset is carried out, otherwise, the last game
state loaded, the one stored in the `FileGameConfiguration` object that is stored in the `fileLoader`
attribute of game, is used.

<!-- TOC --><a name="save-command-details"></a>
## Details of the `save` command (optional)

The implementation of the `save` command is relatively simple. The `execute` method
of the `SaveCommand` class could be implemented in one of the following ways:

- The `execute` method calls a `save` method of `Game`, passing it the file-name `String`.
- The `execute` method opens an output stream to the file and calls a `stringify`[^3]
  method of `Game` passing it the output stream.
- The `execute` method  opens an output stream to the file, calls a no-argument `stringify`
  method of `Game` and writes the text serialization returned by this method to the output stream.

In the first case, the `save` method of `Game`  is responsible for opening the output stream to
the file, calling a `stringify` method of `Game` and writing the text serialization returned by
this method to the output stream. In the second case and third case, there is no `save` method
in `Game`; in the second, the `stringify` method of `Game` is responsible for writing the text
serialization to the output stream. 

For consistency of style of implementation across the different commands, we propose to implement
the first of these three possibilities [^4], so using a `save` method in the `Game` class of the form:

```java
	public void save(String fileName) throws GameModelException {...}
```

The `stringify` method of the game, in 
building its string represention of the current state of the game, calls a `stringify` method
of the container, which, in building its string representation of the current state of the
container, calls a `stringify` method of each game object.

[^3]: It could be argued that the Java `toString` method, rather than a `stringify` method should
be used for this purpose, rather than using it to return the textual representation sent to the
standard ouput for display. In proposing the use of a `stringify` method, we follow the principle that
the purpose of the `toString` method is to produce human-readable output (the Java documentation
says it should produce "a concise but informative representation that is easy for a person to read")
while that of the `stringify` method is to produce output to be used in serialization, which need
only be machine-readable. Of course, with the serialization format defined above, both text
representations used by the lemmings program are eminently human-readable, so the policy used
here is debatable.

[^4]: One could also interpret this serialization as another type of view and have the `execute`
of the `SaveCommand` class call a method of view which then calls the `stringify` method of `Game`.

Once the output stream is open, serialization should only throw exceptions in the case where a 
write-error occurs, for example due to some operating system problem; such an exception should
be caught and wrapped in a `GameModelException` (which would then be caught and wrapped in
a `CommandExecuteException`).

Regarding the `parse` method of the `SaveCommand`, the simplest implementation supposes that
the `save` command has no parameters, the name of the file to be used being "hardwired",
i.e. stored in an attribute of the `SaveCommand` class. Of course, this only allows a single
saved file. A more sophisticated implementation would use a `save` command with one `String`
parameter: the filename (to ensure consistent use of a file extension, the usual policy is that
the user provides a file name without a file extension and the program then adds this extension).
The `parse` command would then have to deal with this parameter. Assuming the implementation
with a file-name parameter, the help message of the `save` command has the following form:

```
[DEBUG] Executing: help

Available commands: 
   ...
   [s]ave <fileName>: save the current state of the game in the text file <fileName>
   ...
```

<!-- TOC --><a name="level-conf"></a>
## Initial configurations of the game (optional)

The initial configurations of the game could also be stored in the serialized format instead of
using `initX` methods. The serialized format could either be read from file (in which case, the game
would need to contain a correspondence between levels and file names) or could be stored in `final`
attributes of the game (the `String`s containing the serialized format could be encapsulated in a
Java record, for example). The second solution, the better of the two, could then use the
`FileGameConfiguration` class but modified to use a `StringReader` stream instead of a `FileReader`
stream.
