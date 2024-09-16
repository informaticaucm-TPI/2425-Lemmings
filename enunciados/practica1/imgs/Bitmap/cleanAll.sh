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

# Limpiamos el directorio actual...
rm -f *.eps

# Y ahora en los subdirectorios.
for DIR in *; do
	if [ -d $DIR ]; then
		if [ $DIR != "CVS" ]; then
			# Sólo si el subdirectorio no es el de
			# la información del CVS.
			cd $DIR
			rm -f *.eps
			cd ..;
		fi;
	fi;
done

# Ten en cuenta que estamos entrando en los directorios
# de primer nivel. TeXiS (su Makefile) asume que no habrá 
# subdirectorios dentro de los directorios de imagenes de
# cada capítulo precisamente para que este script sea más
# simple :-)
# Si realmente se quisieran soportar subdirectorios en
# los directorios de los capítulos, habría que hacer algo
# como:
#
# for DIR in $(find . -type d); then ...
#
# Pero luego teniendo en cuenta de quitar los CVS y los
# .svn

# Este script tiene una estructura similar a updateAll.sh.
# A su vez, ambos (updateAll.sh y cleanAll.sh) están
# copiados iguales en el directorio de imágenes vectoriales.
# Si modificas alguno, tendrás que modificar los demás.