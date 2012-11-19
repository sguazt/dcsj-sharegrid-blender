#!/bin/sh

jarfile=dist/dcsj-sharegrid-blender-*.jar
scene="$1"
scenefile="examples/in/$1.in"

if [ ! -f $scenefile ]; then
	echo "Scene file '$scenefile' not found."
	exit 1
fi

cp="$jarfile"
for j in lib/*.jar; do
    cp+=":$j"
done

MGROOT=/usr/local/opt/shareGrid/clientSG
export MGROOT

#java -cp "$cp" -jar "$jarfile"
java -cp "$cp" it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.ui.ConsoleAppUI < "$scenefile"
