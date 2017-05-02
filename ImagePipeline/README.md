# READ ME FOR IMAGE PIPELINE: 


1. Step 1: put all dataset photos in directory for Image PipeLine

2. Step 2: Set the desired number of SeamCarver iterations 
⋅⋅* Open SeamCarver2.java --> set NUM_PHOTOS constant
⋅⋅* Feel free to change any other values: 
⋅⋅* MAX_REMROW: maximum number of rows to remove 
⋅⋅* MAX_REMCOL: maximum number of cols to remove
⋅⋅* FINAL_WIDTH: final width of the image
⋅⋅* FINAL_HEIGH: final height of the image

3. Step 3: properly name the dataset photos #_apple_#.jpg
⋅⋅* Can also open run.sh and change which photos the program is looking for 

4.  Step 4: run the command ~ sh run.sh
⋅⋅* This will generate all permutations of the images --> will take a lot of time

5.  Step 5: run the command ~ sh prepare.sh
⋅⋅* This will pickle all of the images in the Images folder and make a .tar.gz folder for the images
⋅⋅* Pickled images will be separated out over 6 files
