<!-- TOC start -->
- [Assignment 3: Exception handling and file handling](#practica-3-excepcionesYficheros)
- [Introduction](#introduccion)
- [Exception handling](#exceptions)
	- [Exceptions in the commands and the controller](#command-exceptions)
	- [Exceptions in `GameModel`](#gamemodel-exceptions)
- [Saving the state of the game to file: the `save` command](#save-command)
	- [File format](#formato-fichero)
- [Loading the state of the game from file](#carga-config)
	- [Game object factory](#factoria-objetos)
	- [Reading the file data: the `FileGameConfiguration` class](#configuraciones-iniciales)
	- [Encapsulating the loaded state of the game: the `GameConfiguration` class](#configuracion-juego)
	- [Reading the state of the game from file: the `load` command](#load-command)
	- [Modifying the `Game` class to implement the load command](#game-load)
	- [Errors occuring during the loading of a file](#file-exceptions)
	- [Adapting the `reset` method of the `Game` class](#reset-load-game)
	- [Initial configurations of the gameSacando las configuraciones iniciales de la clase Game (opcional)](#level-conf)
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
the execution of the commands.

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
## Exceptions thrown in the control part of the application

We define a new exception class called `CommandException` and two subclasses:

- `CommandParseException`: used for exceptions that occur during the parsing of a command
  (so during the execution of the `parse` method of the `Command` class)
	
- `CommandExecuteException`: used for exceptions that occur during the execution of a command
  (so during the execution of the `execute` method of the `Command` class)

### Parsing errors

First we deal with the occurrence of errors during the parsing, which will lead to the throwing
of a `CommandParseException`, instead of returning `null` as was done in the previous
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
- Mensaje al intentar establecer un rol en una posición no valida:

	```
	[DEBUG] Executing: setRole Walker None 2

	[ERROR] Error: Invalid command parameters
	[ERROR] Error: Invalid position: (None,3)
	```

- - Mensaje al intentar establecer un rol inexistente:

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


<!---
- `NotAllowedDirectionException`: excepción que se produce al tratar de establecer una dirección del lemming incorrecta (ej. `UP` se puede convertir a una dirección pero no es válida). La utilizaremos en la sección de los ficheros.

	- `GameStateParseException`: excepción que se produce al tratar de parsear un estado del juego en formato texto y no poder convertirlo al estado correspondiente. La veremos en la sección de los ficheros.
	
	- `NegativeHeightException`: excepción que se produce al tratar de establecer a un objeto una altura negativa.
	- `PositionParseException`: excepción que se produce al tratar de parsear una posición y no poder convertir los valores a enteros o no tener el formato adecuado.
	- `RoleParseException`: excepción que se produce al tratar de parsear un role y no poder convertir al rol correspondiente.
	- `DirectionParseException`: excepción que se produce al tratar de parsear una dirección y no poder convertirla a la dirección correspondiente.
	- `HeightParseException`: excepción que se produce al tratar de parsear una altura y no poder convertirla a un entero.
--->

<!--
- `GameLoadException`: excepción lanzada al tratar de cargar la configuración de juego de un fichero de texto cuando el formato de éste es incorrecto, bien porque en alguna línea del fichero (ver más adelante) no hay suficientes parámetros o bien porque los parámetros son incorrectos (rol desconocido o formato de la posición incorrecto, etc.). Esta excepción caputarará todas las excepciones de tipo `GameParseException` que se produzcan durante la carga del fichero.
-->

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

<!-- TOC --><a name="save-command"></a>
# Saving the state of the game to file: the `save` command

