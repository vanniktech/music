#!/bin/bash

set -e

echo
img_url=$(echo "$2" | ack -o "https:\/\/.*(?=\\&)" | sed 's/\\//')
echo $img_url

curl "$img_url" > FRONT_COVER.jpg
eyeD3 --remove-all "/Users/Niklas/Downloads/$1.mp3" || true
eyeD3 --add-image="FRONT_COVER.jpg":FRONT_COVER "/Users/Niklas/Downloads/$1.mp3"
rm FRONT_COVER.jpg
echo "Success"