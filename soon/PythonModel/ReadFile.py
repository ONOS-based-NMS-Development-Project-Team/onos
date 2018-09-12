import tensorflow as tf
import hashlib
import os
from NeurosNetwork import NeuroNetwork
def read_data(data_list):
    listData = []
    for i in range(len(data_list)):
        list = []
        list.append(int(data_list[i]['level']))
        list.append(int(data_list[i]['name']))
        list.append(int(data_list[i]['node']))
        list.append(int(data_list[i]['board']))
        list.append(int(data_list[i]['location']))
        listData.append(list)
    return listData




def read_parameters(dict):
    bias = dict["biasInit"]
    weight = dict['weightInit']
    activation_function = dict['activationFunction']
    if activation_function == 'relu':
        activation_function = tf.nn.relu
    epoch = dict['epoch']
    learning_rate = dict["learningRate"]
    inputlayer_nnumber = dict["inputNum"]
    outlayer_nnumber = dict["outputNum"]
    hlayer_neurons = dict["hiddenLayer"]
    hiddenNum = len(hlayer_neurons)
    lossFunction = dict["lossFunction"]
    if lossFunction == 'mseloss':
        lossFunction = tf.reduce_mean
    batchSize = dict["batchSize"]
    optimizer = dict["optimizer"]
    if optimizer == 'sgd':
        optimizer = tf.train.GradientDescentOptimizer
    lrAdjust = dict["lrAdjust"]
    if lrAdjust == 'constant':
        lrAdjust = tf.train.linear_cosine_decay
    dropout = dict["dropout"]

    return [inputlayer_nnumber,outlayer_nnumber,hiddenNum,learning_rate,epoch,
            batchSize,optimizer,lrAdjust,activation_function,dropout,
            lossFunction,hlayer_neurons]

def MD5_Value(filename):
    md5_value = hashlib.md5()
    with open(filename, 'rb') as f:
        while True:
            data = f.read(2048)
            if not data:
                break
            md5_value.update(data)
    return md5_value.hexdigest()


if __name__ == '__main__':
    print("hh")
    print(os.environ['HOME'])
    print('hh')
    os.system("gnome-terminal -e 'ls'")
    # os.system("ls")
    networks = NeuroNetwork(31,16,1,0.01,101,10,tf.train.GradientDescentOptimizer,tf.train.exponential_decay
                             ,tf.nn.relu,0.0,tf.reduce_mean,[40])
    print(networks)