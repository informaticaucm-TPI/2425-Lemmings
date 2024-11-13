<!-- TOC start -->
- [Assignment 2 - Part II: Lemmings Extended](#práctica-2-parte-ii-lemmings-extended)
- [New command and new role: SetRoleCommand and Parachuter](#command-setRoleCommand_parachuter)
	- [Interface implemented by the roles: `LemmingRole`](#interfaz-lemmingRole)
	- [New role class: `ParachuterRole`](#parachuter)
	- [Role factory](#factoria-de-roles)
	- [New command class: `SetRoleCommand`](#command-setRoleCommand)
- [New role and new object: DownCaverRole and MetalWall](#downCaver-metalWall)
	- [Generalizing the **interactions** between game objects](#interfaces-de-gameobject)
	- [Details of `DownCaverRole` and `MetalWall`](#detalles-downCaver-metalWall)
	- [Applying *double-dispatch* to the ExitDoor](#dd-exitDoor)
	- [Extending the reset command (optional)](#reset-num)
<!-- TOC end -->
<!-- TOC --><a name="práctica-2-parte-ii-lemmings-extended"></a>
# Assignment 2 - Parte II: Lemmings Extended

**Submission: November 18th, 12:00**

**Objectives:** Inheritance, polymorphism, abstract classes and interfaces.

<!--
**Preguntas Frecuentes**: Como es habitual que tengáis dudas (es normal) las iremos recopilando en este [documento de preguntas frecuentes](../faq.md). Para saber los últimos cambios que se han introducido [puedes consultar la historia del documento](https://github.com/informaticaucm-TPI/2425-Lemmings/commits/main/enunciados/faq.md).
-->

In this assignment, we will extend the code with new functionality. The principle objective is to enable the lemmings to have
different possible behaviours and interactions with their environment, encapsulated in the concept of roles, But first a **warning**:

**IMPORTANT**: Any of the following, on its own, is sufficient reason to fail:

- breaking encapsulation,
- the use of methods that return lists,
- the use of `instanceof` or `getClass`, since identifying the dynamic types of objects is simply a way of avoiding the use of
  polymorphism and dynamic binding, i.e. of avoiding the use of OOP.
- the use of a *DIY instanceof* (e.g. each subclass of `GameObject` has a set of methods `isX`, one for each concrete subclass of
  `GameObject`, where the method `isX` returns `true` in the concrete `GameObject` subclass `X` and `false` in any other concrete
   `GameObject` subclass); such a solution is even worse than using `instanceof` or `getClass` since it is simply a clumsier, more
   verbose, way of doing the same thing.

<!-- TOC --><a name="command-setRoleCommand_parachuter"></a>
## New command and new role: `SetRoleCommand` and `Parachuter`

In this section we create a new command that enables the role of a lemming to be changed. However, in order to be
able to use this command, the game must have more than one role so we also need to create a new role. We begin by
creating the new role of Parachuter. The following is an execution trace after implementing both the new role and the
new command:

<!-- TOC --><a name="command-setRoleCommand-example"></a>
```
Command > h
[DEBUG] Executing: h

Available commands:
   [s]et[R]ole ROLE ROW COL: set the lemming in position (ROW,COL) to role ROLE
      [P]arachuter: Lemming that falls with a parachute
      [W]alker: Lemming that walks
   [n]one | "": user does not perform any action
   [r]eset: reset the game to initial configuration
   [h]elp: print this help message
   [e]xit: exit the game

Command > 
[DEBUG] Executing: 

Number of cycles: 3
Lemmings in board: 4
Dead lemmings: 0
Lemmings exit door: 0 ┃2

      1    2    3    4    5    6    7    8    9   10  
   ┌——————————————————————————————————————————————————┐
  A┃                                     ᗺ            ┃A
  B┃                                        ▓▓▓▓▓▓▓▓▓▓┃B
  C┃                                                  ┃C
  D┃                           B                      ┃D
  E┃          ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓  B                      ┃E
  F┃                     🚪            ▓▓▓▓▓          ┃F
  G┃                    ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓          ┃G
  H┃                                                  ┃H
  I┃                                        ▓▓▓▓▓     ┃I
  J┃▓▓▓▓▓▓▓▓▓▓  B                           ▓▓▓▓▓▓▓▓▓▓┃J
   └——————————————————————————————————————————————————┘
      1    2    3    4    5    6    7    8    9   10  

Command > sr Parachuter A 8
[DEBUG] Executing: sr Parachuter A 8

Number of cycles: 4
Lemmings in board: 3
Dead lemmings: 1
Lemmings exit door: 0 ┃2

      1    2    3    4    5    6    7    8    9   10  
   ┌——————————————————————————————————————————————————┐
  A┃                                                  ┃A
  B┃                                    🪂  ▓▓▓▓▓▓▓▓▓▓┃B
  C┃                                                  ┃C
  D┃                                                  ┃D
  E┃          ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓  B                      ┃E
  F┃                     🚪    B       ▓▓▓▓▓          ┃F
  G┃                    ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓          ┃G
  H┃                                                  ┃H
  I┃                                        ▓▓▓▓▓     ┃I
  J┃▓▓▓▓▓▓▓▓▓▓                              ▓▓▓▓▓▓▓▓▓▓┃J
   └——————————————————————————————————————————————————┘
      1    2    3    4    5    6    7    8    9   10  

Command > 
```

We propose the following division into tasks.

<!-- TOC --><a name="interfaz-lemmingRole"></a>
### Interface implemented by the roles: `LemmingRole`

First we create an interface to be implemented by the different role classes, including, of course, `WalkerRole`: 

```java
public interface LemmingRole {

    public void start( Lemming lemming );
    public void advance( Lemming lemming );
    public String getIcon( Lemming lemming );
	// ...
}
```

For the moment, a role consists of the implementation of the two methods `play` and `getIcon`. The implementation of the
`start` method will comprise
a non-empty body only if there is any action to be taken on assigning the role to a lemming. To enable lemmings to change role at
execution time, we use the interface as the type of the attribute `role` of the lemming class:

```java
	private LemmingRole role;
```

In this way, we can use polymorphism to assign to the `role` attribute any object implementing the `LemmingRole` interface,
such as `WalkerRole`, `ParachuterRole` or others.

<!-- TOC --><a name="parachuter"></a>
### New role class: `ParachuterRole`

This new role can be applied to any lemming on the board by using the `SetRoleCommand`. If applied to a lemming that is
falling, the force with which it is falling is reduced to 0. A falling lemming with the parachuter role maintains that
role until it touches the ground, at which point its role reverts to the basic `WalkerRole` (walking in the same direction
as before the fall). Note that a lemming with
a parachute falls at the same speed as a lemming without a parachute, i.e. one cell per cycle, but a lemming
with a parachute cannot be killed by a fall, however large. If the role is applied to a lemming that is not in the air,
it has no effect, i.e. the lemming remains in the basic `WalkerRole`.

As stated above, the `ParachuterRole` class must implement the `LemmingRole` interface. Recall that the `advance` method
has access to the lemming object to which the role is being applied and can consult its state and change that state
(via methods). If, in your solution to Assignment 1, you have implemented the different aspects of the lemming
behaviour in different lower-level behaviour methods called from the principle method, e.g. a method `fall`, you can
simply call these methods from the `advance` method of the role. If not, you will need to do so now to avoid repeating
code.

Finally, we need code for execution-time role changes. To this end, we add two methods to the `Lemming` class: one
to enable an execution-time role change and one to disable such a role change and revert back to the basic
`WalkerRole`:

```java
	public void setRole(LemmingRole role);
	public void disableRole();
```

Due to the polymorphic context, we will later need to modify these methods slightly, see below.

In absence of a `SetRoleCommand`, we can test the different aspects of our `ParachuterRole` class by creating falling
and non-falling lemming with parachutes in the initial configuration.


<!-- TOC --><a name="factoria-de-roles"></a>
### Role factory

We now add a *role factory*. A **factory** enables the logic of object creation to be separated from the location in
the code where the object is created. This factory is implemented in the `LemmingRoleFactory` class in the `lemmingsRoles`
package, which has a method 

```java
	public static LemmingRole parse(String input);
```
To create a new object implementing the `LemmingRole` interface from the string `role_name`, we simply execute:

```java
	LemmingRole role = LemmingRoleFactory.parse(role_name);
```

You should use the same technique as employed in the `CommandGenerator` to create the role factory. As for the case of the
commands parsed by the `CommandGenerator`, the role names can be full names or abbreviated names. Contrary to the case of
the concrete commands, the information message does not need to be split into two separate strings. As in the case of the 
`CommandGenerator`, stateless roles can return `this` but stateful roles must return a new object (`return new XXXRole()`).

To create the factory correctly, you will need to add one or more methods to the `LemmingRole` interface.


<!-- TOC --><a name="command-setRoleCommand"></a>
### New Command: `SetRoleCommand`

We now add a new command that enables a lemming's role to be changed at execution time. This command has three arguments: the
role to be applied and the two coordinates of the lemming's position on the board. Since there may be more than one lemming at a
given position (e.g. two lemmings moving in opposite directions), for simplicity, you may apply the role to the first lemming 
found in that position by the container (so what this command does in such cases is implementation-dependent). Recall, however,
that the container does not know which game objects are lemmings, for which reason, we add the method

```java
	public boolean setRole(LemmingRole role);
```

to the `GameObject` class. Note that we have modified the `setRole` method proposed above so that it returns `boolean`
instead of `void`. This boolean is used to indicate whether the operation was successful or not, so in game objects that are
not lemmmings the method should simply return `false`, simplifying the code for the class `SetRoleCommand`. If there is no
lemming on the chosen position or the position is invalid, the following error message should be displayed:

````
[ERROR] Error: SetRoleCommand error (Incorrect position or no object in that position admits that role)
````

If the role doesn't exist, on the other hand, the following error message should be displayed:

```
[ERROR] Error: Unknown Role
```

The `helpText()` method of this command should call a method similar to the `commandHelp` method of the `CommandGenerator`
in order to obtain the text associated to each role, so you must implement such a method if you have not already done so.
The required output can be seen in the use of the help command in the [execution trace](#command-setRoleCommand-example) at
the beginning of this document. You may add new messages to the `Messages` class if you wish.

<!-- TOC --><a name="factoría-de-roles"></a>
<!-- Versión detallada de la factoría de roles
# Factoría de roles

Lo primero que vamos a hacer va a ser añadir una ***factoría*** de roles a nuestra práctica.
Una factoría nos permite separar la lógica de la creación de un objeto del lugar en el que se crea. Dicha factoría va a venir dada por una clase `LemmingRolFactory` en el paquete `lemmingsRoles` con un método 

```java
	public LemmingRol spawnRol(String input);
```

Usando la factoría, para crear un rol cuyo tipo viene dado en un string `input`, basta hacer

```java
LemmingRol rol = LemmingRolFactory.spawnRol(input);
```

Fíjate que al crear el rol no conocemos cuál es su tipo.
La implementación de este método seguirá la misma estructura que el método `parse` de `CommandGenerator`. 
`LemmingRolFactory` mantendrá una lista de roles disponibles, al igual que `CommandGenerator` mantiene una lista de comandos disponibles. 

```java
private static final List<LemmingRol> AVAILABLE_LEMMINGS_ROLES = Arrays.asList(
	new WalkerRol(),
	new ParachuteRol()
);
```

El método `spawnRol` recorrerá esa lista hasta que un rol realice el parser hasta que uno devuelva un rol. Para ello fijémonos que sólo es necesario añadir en la interfaz `LemmingRol` el siguiente método:

```java
	public LemmingRol parse(String input);
```

Fíjate que la implementación de este método se puede realizar consultando a cada rol si el símbolo del rol coindice con el determinado por el `input`. Si es así, se solo es necesario realizar una copia del rol para no devolver el rol que usa la factoría como patrón. Es decir, su implementación podría ir en una clase abstracta `LemmingAbstractRole` y ser la siguiente: 

```java
	public LemmingRol parse(String rolWord) {
		if ( matchRoleName()) return createInstance();
		else return null;
	}
```

De esta forma sólo sería necesario definir en cada rol los dos métodos de los que depende `matchRoleName()` y `createInstance()`, sin necesidad de incluirlos en el interfaz `LemmingRol`.

```java
protected abstract LemmingRol createInstance();
```

que implementaremos de forma muy sencilla en cada uno de los roles. Por ejemplo, en `WalkerRol` su implementación se limita a devolver un `WalkerRol`

```java
@Override
protected LemmingRol createInstance(){
	return new WalkerRol();
}
```

Observa que para crear los roles en la lista `AVAILABLE_LEMMINGS_ROLES` necesitamos que los roles tengan un constructor sin argumentos y como la factoría se encuentra en el mismo paquete que los roles solo es necesario que la visibilidad de dicho constructor sea solo las clases del mismo paquete. De esta forma se evitará un uso incorrecto de dicho constructor. 

-->

<!-- TOC --><a name="downCaver-metalWall"></a>
## New role and new game object: `DownCaverRole` and `MetalWall`

We now extend the program further and in so doing, demonstrate that the program structure we have created facilitates such extension.
We add a caver role which causes a lemming to dig, that is, to eliminate the solid object in the cell immediately below it and
then occupy the position formerly occupied by this solid object. As for the parachuter role, if the lemming cannot dig, the caver role has no effect. The failed attempt to change the lemming's role does not consume a cycle so the lemming will then move.

For the behaviour associated to this new role to depend on the lemmings interaction with its environment, we introduce a new type of
game object: a metal wall that the caver cannot penetrate. To implement this, we could add a new property to game objects, that 
of being *hard* or *soft* but this would oblige us to modify various classes. Moreover, following this logic would oblige us to
add new methods and/or attributes with each extension, which could be viewed as using a *DIY instanceof*. Instead,
we generalise the *interactions* betwen the game objects.

<!-- TOC --><a name="interfaces-de-gameobject"></a>
### Generalising the interactions between game objects

In Assignment 1, we already had cases of a lemming interacting with its environment, for example, a lemming interacting with the
exit door in order to know whether to successfully leave the game. However, this was implemented in an ad-hoc manner by
simply using a method `isExit` of the game which called the container which checked if the exit was indeed in this position.
The interaction between a caver lemming and the object below it is another example; in this case, if the floor is hard,
the role has no effect but if the floor is soft, the caver will dig it out.

To model these interactions between objects, we will use the interface `GameItem` and a technique known as ***double-dispatch***,
in which the run-time selection of the method body to be executed (this being known as *dispatch*) depends on the dynamic class
of *both* of the objects involved in the interaction. This is implemented by having the dispatch depend not only on the dynamic
type of the object on which the method call was made, the usual dynamic-binding case, but also on the dynamic type of the object
that constitutes the method argument (or, more generally, on that of one of the method arguments, for double-dispatch, or on that
of several of the method arguments, for multi-dispatch).
In fact, neither multiple dispatch, nor double dispatch is implemented in Java but there are ways to simulate it. For simplicity,
we choose one that, strictly speaking, is not even double dispatch since the dispatch depends on the dynamic type of one of the
participants in the interaction and the static type of the other. This leads to undesireable repetition of code.

The `GameObject` class implements the `GameItem` interface in order for all game objects to have the possibility of
interacting with the other game objects via the methods of this interface (and *only* via the methods of this interface).

```java
public interface GameItem {
	public boolean receiveInteraction(GameItem other);

	public boolean interactWith(Lemming lemming);
	public boolean interactWith(Wall wall);
	public boolean interactWith(ExitDoor door);

	public boolean isSolid();
	public boolean isAlive();
	public boolean isExit();
	public boolean isInPosition(Position pos);
}
```

Note the overloading of the methods `interactWith`. Note also that the interaction is not implemented symmetrically, as indicated
by the use of the word "receive" in the name of the main method `receiveInteraction`, indicating that each interaction is
initiated by one of the objects involved. A valid implementation of this method is as follows:

```java
	  public boolean receiveInteraction(GameItem other) {
			return other.interactWith(this);
	  }
```

Each concrete subclass of `GameObject` must have an implementation of the `receiveInteraction` method but it cannot obtain it
by inheritance and dynamic binding, i.e. by simply using the above method code as that of a `default` method in the
`GameItem` interface (or by simply placing this code in the `GameObject` class). This is because, though a call to a
method of the object referenced by `this` in a `default` method body (e.g. `this.toString()`) will use normal dynamic binding and
will work correctly (i.e. will use the method body of the dynamic type of the object implementing the interface), the same is not
true in the `GameItem` interface because method overloading is resolved at compile time, meaning that the static type of the
object referenced by `this` will be used. In consequence, in this *"false" double dispatch*, the code for the `receiveInteraction`
method has to be copied into each of the concrete subclasses of `GameObject`. 

We then extend this way of coding the interaction between game objects to the container by adding a method in the
`GameObjectContainer` class that carries out all the interactions of a given object (provided as argument) with the other objects
that it manages:

```java
	  public boolean receiveInteractionsFrom(GameItem obj) {...}
```

The boolean return value serves to indicate whether any object has been modified by this interaction. Finally, in order for other
game objects to be able to generate interactions with their environment, a method calling this container method should be added
to the `Game` class and declared in the `GameWorld` interface. In particular, we will use it to implement the behaviour
of the `DownCaverRole` class.

<!-- TOC --><a name="detalles-downCaver-metalWall"></a>
###  Details of `DownCaverRole` and `MetalWall`

We implement the new game object in a class called `MetalWall` on which interaction with other game objects has no effect. 

We implement the new role in a class called `DownCaverRole`. As state above, the caver role causes a lemming to dig, that is, to eliminate the solid object in the cell immediately below it, if the object is soft, and then occupy the position formerly occupied by this soft solid object. The icon used to represent a caver lamming is:  `Messages.LEMMING_DOWN_CAVER`. If the lemming cannot dig, the caver role has no effect. The help message for the caver role is: `[D]own[C]aver: Lemming caves downwards` (indicating that the long name is `DownCaver` and the short name is `DC`).

To enable the lemmings to interact with the `Wall` class requires overwriting the method

```java
    public boolean interactWith(Wall obj){...}
```

in the `Lemming` class. Note that the interaction between lemmings and other game objects depend on the role that the lemming has at the time of interaction. For this reason, in deciding what it's behaviour is to be, the lemming must involve the role. To this end, we add the following methods to the `LemmingRole` interface:

```java
	public boolean receiveInteraction(GameItem other, Lemming lemming);

	public boolean interactWith(Lemming receiver, Lemming lemming);
	public boolean interactWith(Wall wall, Lemming lemming);
	public boolean interactWith(ExitDoor door, Lemming lemming);
```

The second parameter of these methods plays the same role as the parameter of type `Lemming` in the `advance` method, that is, the lemming that has that role.

Notice that the great majority of lemmings do not interact with any other game objects. This lack of interaction could be programmed as the default behaviour either by creating a superclass of the other roles which contains this default behaviour (which will be abstract) or by creating a default method in the `LemmingRole` interface.


<!-- TOC --><a name="dd-exitDoor"></a>
###  Applying *double-dispatch* to the `ExitDoor` (optional)

To verify the versatility of the ***double dispatch*** we propose to eliminate the method `isExit` from all game objects and, in consequence also from the game and the container (it is, in any case, a type of *DIY instanceof*), implementing only the method 

```java
    public boolean interactWith(ExitDoor obj){...}
```

in the lemming. This change reduces the size of the code. Such a simplification could be applied to other methods (though this is not obligatory).

<!-- <span style="color:red">**AE**: ¿quitamos del enunciado la extensión del reset?</span> -->
<!-- TOC --><a name="reset-num"></a>
### Extending the `reset` command (optional)

We could extend slightly the functionality of the `reset` command so that it not only resets the current game but also enables a different initial state to be loaded. This can be accomplished by adding an optional argument: the level. If this argument is absent, the previous implementation of the `reset` command is used, if the argument is present and corresponds to a predefined level, the corresponding initial state is loaded, if the argument is present and does not correspond to a predefined level, the following error message is printed:

```
[ERROR] Error: Not valid level number
```

The output of the help command will now be as follows:

```
Command > h
[DEBUG] Executing: h

Available commands:
   [s]et[R]ole ROLE ROW COL: set the lemming in position (ROW,COL) to role ROLE
      [D]own[C]aver: Lemming caves downwards
	  [P]arachuter: Lemming falls with a parachute
      [W]alker: Lemming that walks
   [n]one | "": user does not perform any action
   [r]eset [numLevel]: reset the game to initial configuration, if not numLevel, else load the initial state for numLevel
   [h]elp: print this help message
   [e]xit: exit the game
```
