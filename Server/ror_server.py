#!/usr/bin/env python

import random
import socket, select
from time import gmtime, strftime
from random import randint
import tensorflow as tf

imgcounter = 1
basename = "image%s.jpg"

HOST = socket.gethostname()
print HOST
PORT = 6663

connected_clients_sockets = []
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server_socket.bind((HOST, PORT))
server_socket.listen(10)
connected_clients_sockets.append(server_socket)
buffer_size = 4096

while True:
    read_sockets, write_sockets, error_sockets = select.select(connected_clients_sockets, [], [])
    for sock in read_sockets:
        if sock == server_socket:
            sockfd, client_address = server_socket.accept()
            connected_clients_sockets.append(sockfd)
        else:
            try:
                #print ' Buffer size is %s' % buffer_size
                data = sock.recv(buffer_size)
                txt = str(data)

                if txt.startswith('SIZE'):
                    tmp = txt.split()
                    size = int(tmp[1])

                    print 'got size'
                    #print 'size is %s' % size

                    sock.send("GOT SIZE")
                    # Now set the buffer size for the image 
                    buffer_size = 40960000

                elif txt.startswith('BYE'):
                    sock.shutdown()
                elif data:
                    file_name = basename % imgcounter
                    myfile = open(file_name, 'wb')
                    # data = sock.recv(buffer_size)
                    if not data:
                        myfile.close()
                        break
                    myfile.write(data)
                    myfile.close()

                
                    image_data = tf.gfile.FastGFile(file_name, 'rb').read()
                    # Loads label file, strips off carriage return
                    label_lines = [line.rstrip() for line in tf.gfile.GFile("retrained_labels.txt")]
                    # Unpersists graph from file
                    with tf.gfile.FastGFile("retrained_graph.pb", 'rb') as f:
                        graph_def = tf.GraphDef()
                        graph_def.ParseFromString(f.read())
                        _ = tf.import_graph_def(graph_def, name='')
                    with tf.Session() as sess:
                        # Feed the image_data as input to the graph and get first prediction
                        softmax_tensor = sess.graph.get_tensor_by_name('final_result:0')
                        predictions = sess.run(softmax_tensor, \
                                 {'DecodeJpeg/contents:0': image_data})
                        # Sort to show labels of first prediction in order of confidence
                        top_k = predictions[0].argsort()[-len(predictions[0]):][::-1]
                        for node_id in top_k:
                            human_string = label_lines[node_id]
                            score = predictions[0][node_id]
                            sock.send('%s %.5f ' % (human_string, score))
                            print('%s (%.5f) - ' % (human_string, score))
                    sock.send("GOT IMAGE")
                    os.remove(file)
                    sock.shutdown()
            except:
                print "OUT"
                sock.close()
                connected_clients_sockets.remove(sock)
                continue
        imgcounter += 1
server_socket.close() 
