from NeurosNetwork import NeurosNetwork
import tensorflow as tf
from xmlProcessor import analyzeXml_list, analyzeXml_dict
from ReadFile import read_data, read_parameters
import numpy as np
import websocket11
from xmlProcessor import generate_xml_dict


# 生成数据集
def generate_data_set(list):
    matrix = np.mat(list)
    x_data = matrix[:, 0:4]
    y_data = matrix[:, 4]
    return x_data, y_data


if __name__ == '__main__':
    # 从接收到的文件中解析数据
    datalist = analyzeXml_list('/home/mahaoli/CodeHouse/Apptest/data.xml')
    paradict = analyzeXml_dict('/home/mahaoli/CodeHouse/Apptest/para.xml')
    print(datalist[0])
    data = read_data(datalist)
    para = read_parameters(paradict)
    print(data[0])

    # 数据集制作
    x, y = generate_data_set(data)

    # 模型初始化
    # 具体参数由para指定，这里只是举了一个例子
    network = NeurosNetwork(4, 1, 2, 0.01, 1001, tf.nn.relu, [4, 5])

    # 训练过程
    network.traingResult(x, y)

    # 得到训练后的结果
    para_dict = network.getWeights()
    path = '/home/mahaoli/CodeHouse/Apptest/test.xml'

    # 生成结果对应的xml文件
    generate_xml_dict(para_dict, path)

    # 传送对应结果
