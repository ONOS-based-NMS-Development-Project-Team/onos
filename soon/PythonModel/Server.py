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


def new_client(client, server):
    print("given id %d" % client['id'])
    # server.send_message_to_all("hello")


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
            global data_train_dict
            # with open("/home/mahaoli/data.json", 'w') as f:
            #     f.write(100 * str(content))
            #     f.close()
            data_train_dict = {}
            data_train_id = content["datasetId"]
            print("trainID:", data_train_id)
            data_train_dict[data_train_id] = content
            # data_list.append(data_train_dict)
        elif label == "/config/model":
            print(label)
            print(content)
            global model_para
            model_para = read_parameters(content)
            print(model_para)
        elif label == "/config/train/id":
            global train_id
            train_id = content
            print("needID:", train_id)
        elif label == "/config/test":
            global data_test_dict
            data_test_dict = {}
            data_test_id = content["datasetId"]
            data_test_dict[data_test_id] = content
            print("test_dataSet")
        elif label == "/control/start":
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
            global networks
            networks = NeurosNetwork(inputNum, outputNum, hiddenNum, learningRate, epoch, batchSize, optimiezer
                                     , lrAdjust, axtivationFunction, dropout, lossFunction, hiddenLayer)
            dataTrainSet = data_train_dict[train_id]
            x = dataTrainSet["input"]
            y = dataTrainSet["output"]
            X = np.mat(x)
            Y = np.mat(y)
            networks.traingResult(X, Y)
            lossdict = networks.traingResult(X, Y)
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
        elif label == "/apply":
            print("model input:")
            print(content)
            pred_X = np.mat(content)
            prediction = networks.verify(pred_X)
            prediction_json = prediction.tolist()
            # prediction = str(prediction_json)
            json_pred = json.dumps(prediction_json)
            pred = str(msg_id) + '\n' + '/notify/apply' + '\n' + json_pred
            await websocket.send(pred)
        elif label == "/get/uri":
            print("cal_tensorboard")
            tensor_link = 'www.cctv.com'
            # json_tblink = json.dumps(tensor_link)
            tb_link = str(msg_id) + '\n' + '/notify/uri' + '\n' + tensor_link
            await websocket.send(tb_link)
        else:
            print("others")

if __name__ == '__main__':
    start_server = websockets.serve(ws_handler=handler, host='10.108.69.165', port=9999, max_size=100*1024*1024)
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()