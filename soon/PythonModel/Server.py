import asyncio
import websockets
import json
import threading
import numpy as np
from ReadFile import read_parameters
from NeurosNetwork import NeurosNetwork

import sys
import pydevd
# 设置调试
# sys.path.append('pycharm-debug.egg')
# pydevd.settrace('localhost', port=31235, stdoutToServer=True, stderrToServer=True)


def is_json(myjson):
    try:
        json_boject = json.loads(myjson)
    except ValueError:
        return False
    return True


async def handler(websocket, path):
    while True:
        message = await websocket.recv()
        msg_id, label, content = message.split('\n', 2)
        print(label)
        if is_json(content):
            content = json.loads(content)
        else:
            content = content
        if label == "/config/train":
            # for i in  content:
            #     print(len(i))
            print('length:', len(content))
            data_train_id = content["datasetId"]
            print("trainID:", data_train_id)
            data_train_dict[data_train_id] = content
            received_trdinID = str(msg_id) + '\n' + '/notify/train_dataset_end' + '\n' + str(data_train_id)
            await websocket.send(received_trdinID)
            # data_list.append(data_train_dict)
        elif label == "/control/delete/train":
            data_train_dict.pop(content)
        elif label == "/control/delete/test":
            data_test_dict.pop(content)
        elif label == "/config/model/type":
            print(label)
            print(content)
        elif label == "/config/model":
            print(label)
            print(content)
            # global model_para
            model_para = read_parameters(content)
            print(model_para)
        elif label == "/config/train/id":
            # global train_id
            train_id = content
            print("needID:", train_id)
        elif label == "/config/test":
            # global data_test_dict
            # data_test_dict = {}
            data_test_id = content["datasetId"]
            data_test_dict[data_test_id] = content
            print("test_ID:", data_test_id)
            received_testID = str(msg_id) + '\n' + '/notify/test_dataset_end' + '\n' + str(data_test_id)
            await websocket.send(received_testID)
        elif label == "/control/start":
            pass
            inputNum = model_para[0]
            outputNum = model_para[1]
            hiddenNum = model_para[2]
            learningRate = model_para[3]
            epoch = model_para[4]
            batchSize = model_para[5]
            optimiezer = model_para[6]
            lrAdjust = model_para[7]
            axtivationFunction = model_para[8]
            dropout = model_para[9]
            lossFunction = model_para[10]
            hiddenLayer = model_para[11]
            # global networks
            print("current_model:", model_para)
            global_networks.networks = NeurosNetwork(inputNum, outputNum, hiddenNum, learningRate, epoch, batchSize, optimiezer
                                     , lrAdjust, axtivationFunction, dropout, lossFunction, hiddenLayer)
            dataTrainSet = data_train_dict[train_id]
            print("threading_current_thread:", threading.current_thread().name)
            print(global_networks.networks)
            x = dataTrainSet["input"]
            y = dataTrainSet["output"]
            X = np.mat(x)
            Y = np.mat(y)
            global_networks.networks.traingResult(X, Y)
            lossdict = global_networks.networks.traingResult(X, Y)
            for i in range(len(lossdict)):
                a = lossdict[i].tolist()
                dict = {"loss": a, "remainingTime": 0, "precision": 0}
                json_str = json.dumps(dict)
                string = msg_id + '\n' + '/notify/process' + '\n' + json_str
                await websocket.send(string)
            print("transported loss end")
            end = 'train_end'
            end_str = json.dumps(end)
            end = str(msg_id) + '\n' + '/notify/train_end' + '\n' + end_str
            await websocket.send(end)
        elif label == "/eval":
            pass
            # global test_id
            # test_id = content
            # print("needID:", test_id)
            # dataTsetSet = data_test_dict[test_id]
            # x = dataTrainSet["input"]
            # y = dataTrainSet["output"]
            # X = np.mat(x)
            # Y = np.mat(y)
            # global_networks.networks.testResult(X, Y)
        elif label == "/apply":
            print("model input:")
            print(content)
            pred_X = np.mat(content)
            prediction = global_networks.networks.verify(pred_X)
            prediction_json = prediction.tolist()
            # prediction = str(prediction_json)
            json_pred = json.dumps(prediction_json)
            pred = str(msg_id) + '\n' + '/notify/apply' + '\n' + json_pred
            global_networks.networks.sess.close()
            await websocket.send(pred)
        elif label == "/get/uri":
            print("cal_tensorboard")
            tensor_link = 'www.cctv.com'
            # json_tblink = json.dumps(tensor_link)
            tb_link = str(msg_id) + '\n' + '/notify/uri' + '\n' + tensor_link
            await websocket.send(tb_link)
            print('---------------------------------------------------------------------')
            global_networks.networks.reset()
        else:
            print("others")

if __name__ == '__main__':
    data_train_dict = {}
    data_test_dict = {}
    model_para = {}
    train_id = 0
    test_id = 0
    global_networks = threading.local()
    start_server = websockets.serve(ws_handler=handler, host='10.117.63.234', port=9999, max_size=100*1024*1024)
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()