<!-- TOC start -->
- [Assignment 2 - Part II: Lemmings Extended](#práctica-2-parte-ii-lemmings-extended)
- [New command and new role: SetRoleCommand and Parachuter](#command-setRoleCommand_parachuter)
	- [Interface implemented by the roles: `LemmingRole`](#interfaz-lemmingRole)
	- [New role class: `ParachuterRole`](#parachuter)
	- [Role factory](#factoria-de-roles)
	- [New command class: `SetRoleCommand`](#command-setRoleCommand)
- [New role and new object: DownCaverRole and MetalWall](#downCaver-metalWall)
	- [Generalizing the **interactions** between game objects](#interfaces-de-gameobject)
	- [Details of DownCaverRole and MetalWall](#detalles-downCaver-metalWall)
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

First we create an interface to be implemented by the different role classes, including, of course, WalkerRole`: 

```java
public interface LemmingRole {

    public void start( Lemming lemming );
    public void advance( Lemming lemming );
    public String getIcon( Lemming lemming );
	// ...
}
```

For the moment, a role consists of the implementation of the two methods `play` and `getIcon`. The latter method will contain
a non-empty body if there is any action to be taken on assigning the role to a lemming. To enable lemmings to change role at
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

In absence of a `SetRoleCommand`, we can test the different aspects of our ParachuterRole class by creating falling
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
given position (e.g. moving in opposite directions), for simplicity, you may apply the role to the first lemming found in that
position by the container (so what this command does in such cases is implementation-dependent). Recall, however, that the
container does not know which game objects are lemmings, for which reason, we add the method

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
