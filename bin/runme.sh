#!/bin/sh

jarfile=dist/dcs-sharegrid-blender-*.jar

cp="$jarfile"
for j in lib/*.jar; do
    cp+=":$j"
done

MGROOT=/usr/local/opt/shareGrid/clientSG
export MGROOT

#java -cp "$cp" -jar "$jarfile"
java -cp "$cp" it.unipmn.di.dcs.sharegrid.submitter.app.render.blender.ui.ConsoleAppUI
