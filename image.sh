#!/bin/bash

set -e

img_url=$(echo "$2" | grep -o "https://.*\"" | sed "s/\"//")
curl "$img_url" > FRONT_COVER.jpg
eyeD3 --add-image="FRONT_COVER.jpg":FRONT_COVER "/Volumes/Niklas/m/$1.mp3"
rm FRONT_COVER.jpg
