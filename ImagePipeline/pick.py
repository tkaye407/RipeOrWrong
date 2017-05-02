import pickle
import numpy as np
import random
from scipy.misc import imread
import glob, os

size = 128
width = size * size * 3
data_chunk = 200

data = np.empty((0,width), int)
label = []
files = []

orig_cwd = os.getcwd()
os.chdir(os.getcwd() + "/Images")
for file in glob.glob("*.jpg"):
	files.append(file)

random.shuffle(files)

for file in files:
	cpic = imread(file)
	arr = np.empty((3,size*size), int)

	for i in range(0, size):
		for j in range(0, size):
			arr[0,i*size + j] = cpic[i, j, 0]
			arr[1,i*size + j] = cpic[i, j, 1]
			arr[2,i*size + j] = cpic[i, j, 2]
	arr = arr.flatten()
	data = np.vstack([data, arr])
	label.append(file[0])
print data.shape

data_chunk = int(len(files) / 6)

os.chdir(orig_cwd + "/apples-batches-python")
for i in range(1,6): 
	this_data = data[i*data_chunk:(i+1)*data_chunk]
	print this_data.shape
	this_label = label[i*data_chunk:(i+1)*data_chunk]
	dictA = {"data": this_data, "labels": this_label}
	fn = "data_batch_" + str(i)
	with open(fn, "wb") as f:
		pickle.dump(dictA, f)

this_data = data[5*data_chunk:]
print this_data.shape
this_label = label[5*data_chunk:]
dictA = {"data": this_data, "labels": this_label}
with open("test_batch", "wb") as f:
	pickle.dump(dictA, f)


#print np.reshape(cpic, (3, 32*32), order ="C")


