# Copyright 2016 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import gzip

import numpy as np
from six.moves import xrange  # pylint: disable=redefined-builtin

from tensorflow.contrib.learn.python.learn.datasets import base
from tensorflow.python.framework import dtypes

# SOURCE_URL = 'http://yann.lecun.com/exdb/mnist/'
EXAMPLES_PER_FILE = 1000
NUM_TRAIN_EXAMPLES = 5 * EXAMPLES_PER_FILE
NUM_TEST_EXAMPLES = 1000
VALIDATION_SIZE = 128
NUM_OUTPUTS = 2

IMG_SIZE = 128

class DataSet(object):

  # Construct the Dataset
  def __init__(self, images, labels, fake_data=False, one_hot=False, dtype=dtypes.float32, reshape=True):
    #`dtype` = `uint8` --> `[0, 255]`, vs `float32` -->`[0, 1]`.

    dtype = dtypes.as_dtype(dtype).base_dtype
    if dtype not in (dtypes.uint8, dtypes.float32):
      raise TypeError('Invalid image dtype %r, expected uint8 or float32' % dtype)
    if fake_data:
      self._num_examples = 10000
      self.one_hot = one_hot
    else:
      assert images.shape[0] == labels.shape[0], ('images.shape: %s labels.shape: %s' % (images.shape, labels.shape))
      self._num_examples = images.shape[0]

      # Convert shape from [num examples, rows, columns, depth]
      # to [num examples, rows*columns] (assuming depth == 1)
      if reshape:
        assert images.shape[3] == 1
        images = images.reshape(images.shape[0], images.shape[1] * images.shape[2])
      if dtype == dtypes.float32:
        # Convert from [0, 255] -> [0.0, 1.0].
        images = images.astype(np.float32)
        images = np.multiply(images, 1.0 / 255.0)
    self._images = images
    self._labels = labels
    self._epochs_completed = 0
    self._index_in_epoch = 0

  @property
  def images(self):
    return self._images

  @property
  def labels(self):
    return self._labels

  @property
  def num_examples(self):
    return self._num_examples

  @property
  def epochs_completed(self):
    return self._epochs_completed

  def next_batch(self, batch_size, fake_data=False, shuffle=True):
    """Return the next `batch_size` examples from this data set."""
    if fake_data:
      fake_image = [1] * 784
      if self.one_hot:
        fake_label = [1] + [0] * 9
      else:
        fake_label = 0
      return [fake_image for _ in xrange(batch_size)], [
          fake_label for _ in xrange(batch_size)
      ]
    start = self._index_in_epoch
    # Shuffle for the first epoch
    if self._epochs_completed == 0 and start == 0 and shuffle:
      perm0 = np.arange(self._num_examples)
      np.random.shuffle(perm0)
      self._images = self.images[perm0]
      self._labels = self.labels[perm0]
    # Go to the next epoch
    if start + batch_size > self._num_examples:
      # Finished epoch
      self._epochs_completed += 1
      # Get the rest examples in this epoch
      rest_num_examples = self._num_examples - start
      images_rest_part = self._images[start:self._num_examples]
      labels_rest_part = self._labels[start:self._num_examples]
      # Shuffle the data
      if shuffle:
        perm = np.arange(self._num_examples)
        np.random.shuffle(perm)
        self._images = self.images[perm]
        self._labels = self.labels[perm]
      # Start next epoch
      start = 0
      self._index_in_epoch = batch_size - rest_num_examples
      end = self._index_in_epoch
      images_new_part = self._images[start:end]
      labels_new_part = self._labels[start:end]
      return np.concatenate((images_rest_part, images_new_part), axis=0) , np.concatenate((labels_rest_part, labels_new_part), axis=0)
    else:
      self._index_in_epoch += batch_size
      end = self._index_in_epoch
      return self._images[start:end], self._labels[start:end]

def apples_reshape(batch_arg):
  output=np.reshape(batch_arg,(EXAMPLES_PER_FILE,3,IMG_SIZE,IMG_SIZE)).transpose(0,2,3,1)
  return output

def unpickle(file):
  import cPickle
  fo=open(file,'rb')
  dict=cPickle.load(fo)
  fo.close()
  return dict

def read_data_sets(fake_data=False, one_hot=False, dtype=dtypes.float32, reshape=True, validation_size=VALIDATION_SIZE):
  if fake_data:
    def fake():
      return DataSet([], [], fake_data=True, one_hot=one_hot, dtype=dtype)

    train = fake()
    validation = fake()
    test = fake()
    return base.Datasets(train=train, validation=validation, test=test)

  #Loading apples data and reshaping it to be batch_sizex32x32x3
  batch1=unpickle('apples-batches-python/data_batch_1')
  batch2=unpickle('apples-batches-python/data_batch_2')
  batch3=unpickle('apples-batches-python/data_batch_3')
  batch4=unpickle('apples-batches-python/data_batch_4')
  batch5=unpickle('apples-batches-python/data_batch_5')

  batch1_data=apples_reshape(batch1['data'])
  batch2_data=apples_reshape(batch2['data'])
  batch3_data=apples_reshape(batch3['data'])
  batch4_data=apples_reshape(batch4['data'])
  batch5_data=apples_reshape(batch5['data'])

  batch1_labels=batch1['labels']
  batch2_labels=batch2['labels']
  batch3_labels=batch3['labels']
  batch4_labels=batch4['labels']
  batch5_labels=batch5['labels']

  test_batch=unpickle('apples-batches-python/test_batch')
  test_images=apples_reshape(test_batch['data'])
  test_labels_data=test_batch['labels']

  train_images=np.concatenate((batch1_data,batch2_data,batch3_data,batch4_data,batch5_data),axis=0)
  train_labels_data=np.concatenate((batch1_labels,batch2_labels,batch3_labels,batch4_labels,batch5_labels),axis=0)


  #one-hot encodinf of labels
  train_labels=np.zeros((NUM_TRAIN_EXAMPLES,2),dtype=np.float32)
  test_labels=np.zeros((NUM_TEST_EXAMPLES,2),dtype=np.float32)
  for i in range(NUM_TRAIN_EXAMPLES):
      a=train_labels_data[i]
      train_labels[int(i),int(a)]=1.

  for j in range(NUM_TEST_EXAMPLES):
      b=test_labels_data[j]
      test_labels[int(j),int(b)]=1.

  if not 0 <= validation_size <= len(train_images):
    raise ValueError(
        'Validation size should be between 0 and {}. Received: {}.'.format(len(train_images), validation_size))

  validation_images = train_images[:validation_size]
  validation_labels = train_labels[:validation_size]
  train_images = train_images[validation_size:]
  train_labels = train_labels[validation_size:]

  train = DataSet(train_images, train_labels, dtype=dtype, reshape=reshape)
  validation = DataSet(validation_images, validation_labels, dtype=dtype, reshape=reshape)
  test = DataSet(test_images, test_labels, dtype=dtype, reshape=reshape)

  return base.Datasets(train=train, validation=validation, test=test)

