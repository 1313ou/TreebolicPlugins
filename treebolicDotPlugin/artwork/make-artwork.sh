#!/bin/bash

thisdir="`dirname $(readlink -m $0)`"
thisdir="$(readlink -m ${thisdir})"
dirres=../src/main/res
dirassets=../src/main/assets
dirapp=..

declare -A web_res
web_res=([web]=512)
web_list="ic_launcher.svg"

declare -A launcher_res
launcher_res=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
launcher_list="ic_launcher.svg"

declare -A splash_res
splash_res=([mdpi]=144 [hdpi]=192 [xhdpi]=288 [xxhdpi]=384 [xxxhdpi]=576)
splash_list="ic_splash.svg"

declare -A icon_res
icon_res=([mdpi]=48 [hdpi]=72 [xhdpi]=96 [xxhdpi]=144 [xxxhdpi]=192)
icon_list="ic_treebolic.svg"

for svg in ${web_list}; do
	for r in ${!web_res[@]}; do 
		d="${dirapp}"
		mkdir -p ${d}
		png="${svg%.svg}-web.png"
		echo "${svg}.svg -> ${d}/${png}.png @ resolution ${web_res[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${web_res[$r]} > /dev/null 2> /dev/null
	done
done

for svg in ${launcher_list}; do
	for r in ${!launcher_res[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg}.svg -> ${d}/${png}.png @ resolution ${launcher_res[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${launcher_res[$r]} > /dev/null 2> /dev/null
	done
done

for svg in ${splash_list}; do
	for r in ${!splash_res[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg}.svg -> ${d}/${png}.png @ resolution ${splash_res[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${splash_res[$r]} > /dev/null 2> /dev/null
	done
done

for svg in ${icon_list}; do
	for r in ${!icon_res[@]}; do 
		d="${dirres}/drawable-${r}"
		mkdir -p ${d}
		png="${svg%.svg}.png"
		echo "${svg}.svg -> ${d}/${png} @ resolution ${icon_res[$r]}"
		inkscape ${svg} --export-png=${d}/${png} -h${icon_res[$r]} > /dev/null 2> /dev/null
	done
done


