#!/bin/bash
#----------------------------------------------------------
#
# Copyright 2009 Pedro Pablo Gomez-Martin,
#                Marco Antonio Gomez-Martin
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#----------------------------------------------------------

# Recorre todos los ficheros .jpg y .png del directorio, y los
# convierte a eps utilizando el programa sam2p.

# Se basó originalmente en:

# http://www.tug.org/pipermail/texhax/2003-June/000396.html
# http://amath.colorado.edu/documentation/LaTeX/reference/figures.html

# aunque ya no tiene nada que ver :-)


# Convierte a .eps el fichero .jpg o .png recibido
# como primer parámetro. Se hacen algunas comprobaciones
# sobre el fichero (que no está duplicado)
# Si hay algún problema devuelve 1; en otro caso, devuelve 0
function convert {
    f=$1

    # Nos aseguramos de que no hay un .jpg y un .png con el
    # mismo nombre (que terminarían ocasionando el mismo .eps).
    jpg=$(echo $f | sed s/png$/jpg/)
    png=$(echo $f | sed s/jpg$/png/)

    if [ -e $jpg ] && [ -e $png ]; then
        echo "Repetición de nombres en $jpg y $png no permitida." > /dev/stderr
        exit 1
    fi

    # Construimos el nombre del fichero con extensión .eps
    # Como no sabemos si lo que tenemos es un .jpg o un .png
    # sustituimos ambas y listo.
    eps=$(echo $f | sed s/png$/eps/)
    eps=$(echo $eps | sed s/jpg$/eps/)

    echo -n "$f --> $eps ... "

    # Si el fichero $eps existe y es más nuevo que el original,
    # nos ahorramos la conversión.
    [ $eps -nt $f ] && echo $eps está actualizado. && exit 0

    # Hacemos la comprobación de si está instalado sam2p
    # en un lugar "tan profundo" para que sólo falle si
    # realmente lo vamos a usar. Esto significa que vamos
    # a comprobarlo por cada imagen... La otra opción
    # habría sido comprobarlo directamente en updateAll.sh
    # pero en ese caso, si no tenemos el sam2p, pero tampoco
    # tenemos imágenes que convertir, acabaríamos con un 
    # "error" que no resulta problemático.
    # De hecho lo hacemos incluso después de haber comprobado
    # que no tenemos ya el .eps actualizado, para no quejarnos
    # si el fichero existe realmente.
    if ! which sam2p > /dev/null; then
        echo "El programa sam2p no está disponible." > /dev/stderr
        echo "No pueden convertirse las imagenes de mapas de bits." > /dev/stderr
	exit 1
    fi

    sam2p $f $eps >/dev/null 2>&1 && echo Hecho. || (echo ERROR. && rm -f $eps && exit 1)

}



for f in {*.jpg,*.png}; do
    [ ! -e $f ] && continue
    # Necesario porque el for mete en f un
    # *.jpg o *.png si no hay ningún jpg
    # o png, a si es que nos aseguramos de
    # que el fichero existe.

    # Convertimos todos los ficheros encontrados
    if ! convert $f; then
        exit 1
    fi
done
