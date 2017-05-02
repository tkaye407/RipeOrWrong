README FOR RE_TRAINING GOOGLENET

Adapted from TensorFlow for Poets blog: https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#1

Step 1: Download Docker for Mac or current system: https://docs.docker.com/docker-for-mac/

Step 2: run ~ docker run hello-world 
	IF this works then docker is working properly 

Step 3: Start a docker terminal ~ docker run -it gcr.io/tensorflow/tensorflow:latest-devel

Step 4: Run below code to make sure that TensorFlow is working properly: 
	python

	import tensorflow as tf
	hello = tf.constant('Hello, TensorFlow!')
	sess = tf.Session()
	print(sess.run(hello))

Step 5: Make a directory called tf_files and inside of it put a folder name say "APPLE_PHOTOS" 
		Then inside of APPLE_PHOTOS put the folders with the retraining images
		Each folder should be names with the category it represents ("bad apple", "good apple" , etc)

Step 6: Start docker again with access to the tf_files" 
	~ docker run -it -v $HOME/tf_files:/tf_files  gcr.io/tensorflow/tensorflow:latest-devel

Step 7: get the training code --> run below code
	~ cd /tensorflow
	~ git pull

Step 8: run the command below to retrain the model. THAT SIMPLE
		This script loads the pre-trained Inception v3 model, removes the old final layer, and trains a new one on the flower photos you've downloaded.
	
	python tensorflow/examples/image_retraining/retrain.py \
	--bottleneck_dir=/tf_files/bottlenecks \
	--how_many_training_steps 1000 \
	--model_dir=/tf_files/inception \
	--output_graph=/tf_files/retrained_graph.pb \
	--output_labels=/tf_files/retrained_labels.txt \
	--image_dir /tf_files/PHOTO_DIRECTORY

Step 9: Inside of /tf_files/ you will find the retrained graph file and its labels as specified above

IF the above steps do not work, please refer to TensorFlow for Poets codelab: https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0
