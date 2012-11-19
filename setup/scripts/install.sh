#!/bin/bash

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

MGROOT_GUESS=
CLASSPATH_GUESS=.
LAUNCHER_CMD=DcsShareGridBlender.sh
#IMG2MOV_CMD=images2movie.sh
#MOV2MOV_CMD=movies2movie.sh
JAR_FILE=`basename dcs-sharegrid-blender-*.jar`
#JAR_FILE=`ls dist/*.jar`
#JAR_FILE=`basename $JAR_FILE`

# A simple relative to absolute path translator
function rel2abs()
{
	[ "$#" -eq 1 ] || return 1

	MGROOT_GUESS=$(cd -P -- "$1" && pwd -P)

	return 0
}

## Set MGROOT_GUESS variabile to the MyGrid installation path
# Try to guess a reasonable value (should work for ShareGrid users)
rel2abs `pwd `/../clientSG
# If not work the user should manuaaly set the MGROOT_GUESS variable to a good value
#MGROOT_GUESS=/full/path/to/mygrid/installation

OK=1
if [ -z $MGROOT_GUESS ]; then
	echo "!! WARNING !!"
	echo "MGROOT_GUESS variable is not set!"
	echo "Please, set it to the MyGrid installation path."
	OK=0
fi
if [ $OK -eq 1 ] && [ ! -d $MGROOT_GUESS ]; then
	echo "!! WARNING !!"
	echo "MGROOT_GUESS variable is set to a non existent path ($MGROOT_GUESS)!"
	echo "Please, set it to the MyGrid installation path."
	OK=0
fi

if [ $OK -eq 1 ] && [ -e "$MGROOT_GUESS/bin/mygrid" ]; then
	echo "The path '$MGROOT_GUESS' seems to work!"
	OK=1
else
	echo "The '$MGROOT_GUESS' does not seem a valid MyGrid installation path!"
	OK=0
fi

if [ $OK -eq 1 ]; then

{
	# Build classpath string
	for jar in `ls lib/*.jar`; do
		CLASSPATH_GUESS="$CLASSPATH_GUESS:$jar"
	done

	# Create the launcher script
	cat <<EOT
#!/bin/sh

#------------------------------------------------------------------------------#
# [ShareGrid project - http://dcs.di.unipmn.it]
#
# Launcher script for the DCS Grid Blender submitter.
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
#------------------------------------------------------------------------------#
#
# Version: 1.1
# Author: Marco Guazzone, marco.guazzone@mfn.unipmn.it
#------------------------------------------------------------------------------#

MGROOT=$MGROOT_GUESS

if [ -z \$MGROOT ]; then
	echo "!! WARNING !!"
	echo "MGROOT variable is not set!"
	echo "Please, set it to the MyGrid installation path."
	exit -1
fi
if [ ! -d \$MGROOT ]; then
	echo "!! WARNING !!"
	echo "MGROOT variable is set to a non existent path (\$MGROOT)!"
	echo "Please, set it to the MyGrid installation path."
	exit -2
fi

if [ ! -e "\$MGROOT/bin/mygrid" ]; then
	echo "The '\$MGROOT' does not seem a valid MyGrid installation path!"
	exit -3
fi

RETVAL=0

#libs=.:lib/dcs-jcommons-1.6.11.jar:lib/dcs-jgrid-core-1.0.8.jar:lib/dcs-jgrid-ourgrid-1.0.8.jar:lib/javacc.jar:lib/log4j.jar:lib/ourgrid.jar
classpath=$CLASSPATH_GUESS

java -cp \$classpath -jar $JAR_FILE

RETVAL=\$?

exit \$RETVAL
EOT
} > "$LAUNCHER_CMD"

	chmod u+x "$LAUNCHER_CMD"
#	chmod u+x "$IMG2MOV_CMD"
#	chmod u+x "$MOV2MOV_CMD"

	echo "Installation succeded!"
else
	echo "Installation failed. Sorry!"
fi

echo "Bye!!"
