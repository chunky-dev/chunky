#!/bin/sh

INK="/Applications/Inkscape.app/Contents/Resources/bin/inkscape"
DIR="Chunky.iconset"

if [ ! -d "$DIR" ]; then
	mkdir "$DIR"
fi

for W in 16 32 64 128 256 512; do
	$INK -z -e "${PWD}/${DIR}/icon_${W}x${W}.png" -w $W -h $W "${PWD}/chunky/res/chunky-icon.svg"
done

iconutil -c icns "$DIR"
rm -r "$DIR"
