# Preguntas Frecuentes

## Práctica 1


### Al resetear el juego, ¿qué mapa debe aparecer?

El reset deberá resetear el mapa en *ejecución*. Por defecto el mapa en ejecución es el que hemos llamado 1, pero ten en cuenta que el mapa en ejecución se puede cambiar a través de los argumentos del programa. Por lo que si se ha iniciado con el mapa 0 el programa al resetear deberá mostrar el mapa 0.

Se podría realizar(opcional) una extensión simple en el que dicho comando permita recibir un argumento entero con el número del mapa. De tal forma que si el número fuera correcto se cargaría el plano correspondiente a ese mapa y en caso contrario se indicaría con un mensaje de error. De esta forma se podría utilizar tanto el reset simple: `reset`, como el con nivel: `reset numMap`.

### La salida por consola muestra caracteres extraños, ¿qué ocurre?

Lo más probable es que la codificación que esté usando Eclipse no sea UTF-8. Para cambiarla:
- Selecciona el proyecto y pulsa el botón derecho seleccionando la opción *Properties*. 
- Elige el menú *Resource* y  comprueba que el valor de *Text File Encoding* es *UTF-8*. 
- En caso contrario, selecciona dicha opción.

### ¿Puedo llevar las posiciones separadas en dos valores: (columna, fila) o (fila, columna)?

No, se os ha pedido expresamente que creéis una clase para manejar las posiciones `Position` y además que sea **inmutable**. El único método de la práctica que recibe las posiciones en **dos valores enteros** es el método `Game.positionToString(int col, int row)`.

Si lo piensas como matriz el acceso estándar suele ser `(fila, columna)` y a futuro cuando nos refiramos a una posición del tablero desde la vista lo haremos a través de su fila y columna (en esta práctica no lo utilizamos). Pero, si lo piensas como puntos en el plano coordenadas `(x, y)` la `x` corresponde a las columnas y la `y` a las filas. Decide una representación interna en el *modelo* y mantenla a lo largo del proyecto. 

### ¿Qué hace el lemming en cada iteración?

El lemming en cada iteración hace un **paso**. Si se encuentra caminando y no hay obstáculo en su dirección un paso consiste en avanzar una posición. Si se encuentra con una pared un paso consiste en cambiar de orientación. Si se encuentra en el aire un paso consiste en caer una posición o morir si se incrusta en el suelo. Si se encuentra en la misma casilla que la puerta un paso consiste en salir. Puedes ver la ejecución de dos casos en los ficheros de ejemplos que os hemos entregados.

### ¿En una misma posición puede haber varios lemmings?

Sí, en una misma posición pueden cohexistir varios lemmings. Por lo que deberás mostrarlos todos.

Ej. En la posición F7 hay dos lemmings caminantes uno mirando a la derecha y otro a la izquierda.


```
Number of cycles: 6
Lemmings in board: 3
Dead lemmings: 1
Lemmings exit door: 0 ┃2

      1    2    3    4    5    6    7    8    9   10  
   ┌——————————————————————————————————————————————————┐
  A┃                                                  ┃A
  B┃                                        ▓▓▓▓▓▓▓▓▓▓┃B
  C┃                                                  ┃C
  D┃                                     ᗺ            ┃D
  E┃          ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓                         ┃E
  F┃                     🚪        Bᗺ  ▓▓▓▓▓          ┃F
  G┃                    ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓          ┃G
  H┃                                                  ┃H
  I┃                                        ▓▓▓▓▓     ┃I
  J┃▓▓▓▓▓▓▓▓▓▓                              ▓▓▓▓▓▓▓▓▓▓┃J
   └——————————————————————————————————————————————————┘
      1    2    3    4    5    6    7    8    9   10  
```


### ¿Se borra el lemming cuando pasa en la puerta?

Sí, la semántica actual de estar vivo es equivalente a encontrarse en el tablero. Por lo que al salir deberá desaparecer del `GameObjectContainer`. Así que el `GameObjectContainer` es el responsable de borrar los objetos que no se encuentren vivos. Piensa cuales de sus métodos deben ser **públicos** y cuales **privados**.
