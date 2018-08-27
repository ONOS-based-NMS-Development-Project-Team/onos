import xmlProcessor
import hashlib
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
    bias = dict["bias"]
    weight = dict['weight']
    activation_function = dict['activationFunction']
    epoch = int(dict['epoch'])
    learning_rate = float(dict['learningRate'])
    inputlayer_nnumber = int(dict['neuronsNumberInputLayer'])
    outlayer_nnumber = int(dict['neuronsNumberOutputLayer'])
    hlayer_neurons = dict['neuronsNumberHiddenLayer']
    hidden_layer_neurons_list = []
    n = len(hlayer_neurons)
    i = 0
    l = 1
    while i < n and l < n:
        hidden_layer_neurons_list.append(int(hlayer_neurons[l]))
        i += 1
        l = 3 * i + 1
    hidden_layer_number = int(dict['numberHiddenLayer'])

    return [bias, weight, activation_function, epoch, learning_rate, inputlayer_nnumber, outlayer_nnumber,
           hidden_layer_neurons_list, hidden_layer_number]

def MD5_Value(filename):
    md5_value = hashlib.md5()
    with open(filename, 'rb') as f:
        while True:
            data = f.read(2048)
            if not data:
                break
            md5_value.update(data)
    return md5_value.hexdigest()