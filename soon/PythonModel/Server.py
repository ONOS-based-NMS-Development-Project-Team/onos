import asyncio
import websockets
import json
import threading
import numpy as np
from ReadFile import read_parameters
from NeurosNetwork import NeuroNetwork
from random import randint
import datetime

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
            print("trainDataSetEnd")
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
            data_test_id = content["datasetId"]
            data_test_dict[data_test_id] = content
            print("test_ID:", data_test_id)
            received_testID = str(msg_id) + '\n' + '/notify/test_dataset_end' + '\n' + str(data_test_id)
            await websocket.send(received_testID)
        elif label == "/control/start":
            nowtime = datetime.datetime.now().strftime("%Y%m%d%H%M%S")
            j = int(str(randint(1, 6)) + str(nowtime)[10:])
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
            global_networks.networks = NeuroNetwork(inputNum, outputNum, hiddenNum, learningRate, epoch, batchSize, optimiezer
                                     , lrAdjust, axtivationFunction, dropout, lossFunction, hiddenLayer)
            dataTrainSet = data_train_dict[train_id]
            print("threading_current_thread:", threading.current_thread().name)
            print(global_networks.networks)
            x = dataTrainSet["input"]
            y = dataTrainSet["output"]
            X = np.mat(x)
            Y = np.mat(y)
            if inputNum == 36:
                pre_AP_one = []
                num_class = 2
                # trans label
                list1 = Y.tolist()
                for one in list1:
                    if one[0] == 1.0 and one[1] == 0.0:
                        pre_AP_one.append(1)
                        # print("one:", one)
                    else:
                        pre_AP_one.append(0)
                print(pre_AP_one)
                one_hot = global_networks.networks.one_hot(pre_AP_one, num_class)
                Y_OH = np.mat(global_networks.networks.sess.run(one_hot))
            elif inputNum == 30:
                pre_FC_one = []
                num_class = 8
                list2 = Y.tolist()
                print(list2)
                for one in list2:
                    pre_FC_one.append(int(one[0]))
                print(pre_FC_one)
                one_hot = global_networks.networks.one_hot(pre_FC_one, num_class)
                Y_OH = np.mat(global_networks.networks.sess.run(one_hot))
                print(Y_OH[:5])
            else:
                Y_OH = Y
            try:
                # global_networks.networks.traingResult(X, Y, j)
                lossdict = global_networks.networks.traingResult(X, Y_OH, j)
                for i in range(len(lossdict)):
                    a = lossdict[i].tolist()
                    dict = {"loss": a, "remainingTime": 0, "precision": 0}
                    json_str = json.dumps(dict)
                    string = str(msg_id) + '\n' + '/notify/process' + '\n' + json_str
                    await websocket.send(string)
                print("transported loss end")
                end = 'train_end'
                print(end)
                end_str = json.dumps(end)
                end = str(msg_id) + '\n' + '/notify/train_end' + '\n' + end_str
                await websocket.send(end)
            except:
                return global_networks.networks.reset()
        elif label == "/eval":
            test_id = content
            print("needID:", test_id)
            dataTestSet = data_test_dict[test_id]
            x = dataTestSet["input"]
            y = dataTestSet["output"]
            X = np.mat(x)
            Y = np.mat(y)
            if model_para[0] == 36:
                pre_AP_one = []
                num_class = 2
                # trans label
                list1 = Y.tolist()
                for one in list1:
                    if one[0] == 1.0 and one[1] == 0.0:
                        pre_AP_one.append(1)
                        # print("one:", one)
                    else:
                        pre_AP_one.append(0)
                print(pre_AP_one)
                one_hot = global_networks.networks.one_hot(pre_AP_one, num_class)
                Y_OH = np.mat(global_networks.networks.sess.run(one_hot))
            elif model_para[0] == 30:
                pre_FC_one = []
                num_class = 8
                list2 = Y.tolist()
                print(list2)
                for one in list2:
                    pre_FC_one.append(int(one[0]))
                print(pre_FC_one)
                one_hot = global_networks.networks.one_hot(pre_FC_one, num_class)
                Y_OH = np.mat(global_networks.networks.sess.run(one_hot))
                print(Y_OH[:5])
            else:
                Y_OH = Y
            try:
                acc = global_networks.networks.testResult(X, Y_OH)
                real_eval = []
                for j in acc:
                    eval = []
                    eval.append(j)
                    real_eval.append(eval)
                    print(j)
                    string = str(msg_id) + '\n' + '/notify/eval' + '\n' + str(test_id) + '\n' + str(real_eval)
                    await websocket.send(string)
            except:
                return global_networks.networks.reset()
        elif label == "/apply":
            print("model input:")
            print(content)
            pred_X = np.mat(content)
            if model_para[0] == 30:
                prediction = np.argmax(global_networks.networks.verify(pred_X), axis=1)
                ppp = []
                for q in prediction:
                    pred_1 = []
                    pred_1.append(q+0.0)
                    ppp.append(pred_1)
                print(ppp)
                json_pred = json.dumps(ppp)
            elif model_para[0] == 36:
                prediction_1 = np.argmax(global_networks.networks.verify(pred_X), axis=1)
                qqq = []
                for p in prediction_1:
                    pred_1 = []
                    pred_1.append(p+0.0)
                    qqq.append(pred_1)
                print(qqq)
                json_pred = json.dumps(qqq)
            else:
                prediction = global_networks.networks.verify(pred_X)
                prediction_json = prediction.tolist()
                print("prediction:", prediction)
                json_pred = json.dumps(prediction_json)
            pred = str(msg_id) + '\n' + '/notify/apply' + '\n' + json_pred
            # global_networks.networks.sess.close()
            await websocket.send(pred)
            global_networks.networks.reset()
            print("-----------------------------------------------------------------------")
        elif label == "/get/uri":
            try:
                print("cal_tensorboard")
                tensor_link = global_networks.networks.tb_exe(j)
                # json_tblink = json.dumps(tensor_link)
                # tensor_link = "111"
                tb_link = str(msg_id) + '\n' + '/notify/uri' + '\n' + tensor_link
                print(tb_link)
                await websocket.send(tb_link)
                # global_networks.networks.reset()
            except:
                global_networks.networks.reset()
        else:
            print("others")

if __name__ == '__main__':
    data_train_dict = {}
    data_test_dict = {}
    model_para = {}
    train_id = 0
    test_id = 0
    # global i
    j = 0
    global_networks = threading.local()
    start_server = websockets.serve(ws_handler=handler, host='localhost', port=9999, max_size=100*1024*1024)
    print("开始监听。。。。")
    asyncio.get_event_loop().run_until_complete(start_server)
    asyncio.get_event_loop().run_forever()
