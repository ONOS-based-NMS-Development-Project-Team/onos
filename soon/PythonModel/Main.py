from NeurosNetwork import NeuroNetwork
import tensorflow as tf
from xmlProcessor import analyzeXml_list, analyzeXml_dict
from ReadFile import read_data, read_parameters
import numpy as np
import socket


# 生成数据集
def generate_data_set(list):
    matrix = np.mat(list)
    x_data = matrix[:, 0:4]
    y_data = matrix[:, 4]
    return x_data, y_data

def get_ip_address():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('8.8.8.8', 80))
        ip = s.getsockname()[0]
    finally:
        s.close()
    return ip


if __name__ == '__main__':
    print(get_ip_address())
    # datalist = analyzeXml_list('/home/mahaoli/CodeHouse/Apptest/data.xml')
    # # paradict = analyzeXml_dict('/home/mahaoli/CodeHouse/Apptest/para.xml')
    # print(datalist[0])
    # data = read_data(datalist)
    # # para = read_parameters(paradict)
    # print(data[0])
    #
    # # 数据集制作
    # x, y = generate_data_set(data)
    #
    # # 模型初始化
    # # 具体参数由para指定，这里只是举了一个例子
    # network = NeuroNetwork(4,1,1,0.1,100,10,tf.train.GradientDescentOptimizer,tf.train.exponential_decay,
    #                         tf.nn.relu,0.0,tf.reduce_mean,[80])
    #
    # # 训练过程
    # network.traingResult(x, y)
    #
    # # 得到训练后的结果
    # para_dict = network.getWeights()
    # path = '/home/mahaoli/CodeHouse/Apptest/test.xml'

    # 生成结果对应的xml文件
    # generate_xml_dict(para_dict, path)

    # 传送对应结果
