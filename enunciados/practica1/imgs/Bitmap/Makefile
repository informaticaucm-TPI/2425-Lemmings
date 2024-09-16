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

#
# Convierte las imágenes png/jpg de todos los subdirectorios
# de nivel 1 de éste a .eps.
# Hace uso del guión updateAll.sh (que a su vez usa
# update-eps.sh) que aparecen en este directorio.
#
convert:
	./updateAll.sh


#
# Borra todos los ficheros intermedios (es decir, los .eps
# que haya generado el objetivo convert).
#
clean:
	./cleanAll.sh

#
# Borra todos los ficheros intermedios, las copias de seguridad
# y los ficheros generados
#
distclean: clean
# 	TODO: no sé... quizá haya que borrar algún backup o algo :-m
