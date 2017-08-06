#!/bin/bash
rm cdSource.tex

for class in `find ../Archipelago-core -name '*.java'` 
do
	echo "Processing $class"
	short="${class##*/}"
	echo "Shortened $short"
	echo "\\textbf{$short}" >> cdSource.tex
	echo "\\lstinputlisting{$class}" >> cdSource.tex
done
