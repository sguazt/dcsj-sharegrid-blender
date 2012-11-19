#!/bin/sh

#------------------------------------------------------------------------------#
# [ShareGrid project - http://dcs.di.unipmn.it]
#
# Installer script for the DCS Grid Blender submitter.
#
# Author: Marco Guazzone (marco.guazzone@gmail.com)
#
# Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
# Science Department - University of Piemonte Orientale, Alessandria (Italy).
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

version="1.1"

scene=$1
imgfmt=$2
#scene=array3

if [ -z "$scene" ]; then
	echo "A scene name must be provided!"
	exit 1
fi
if [ -z "$imgfmt" ]; then
        imgfmt=png
        echo "Image format not specified: default is '$imgfmt'"
fi

cwd=`pwd`

rm -rf "$scene.blend-rendered"

for n in $(ls "$scene.blend-rendered.zip_"*) ; do
	echo "UNZIPPING $n"
	unzip -qo "$n"
	echo "UNZIPPIED $n"
done

cd "$scene.blend-rendered"

ffmpeg -i %04d.$imgfmt $scene.mpeg

ffplay $scene.mpeg 

cd $cwd

exit 0
