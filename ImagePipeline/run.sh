#!/bin/bash

javac ImagePipeline.java

for i in $(ls ?_apple?*.jpg)
do
	echo "Running test on" $i

	java ImagePipeline $i
	
done






