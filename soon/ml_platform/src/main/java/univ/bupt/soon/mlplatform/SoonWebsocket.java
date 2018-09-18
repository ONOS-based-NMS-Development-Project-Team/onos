package univ.bupt.soon.mlplatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.eclipse.jetty.websocket.WebSocket;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.*;
import org.onosproject.soon.platform.MLPlatformService;
import org.onosproject.soon.platform.PlatformCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import univ.bupt.soon.mlplatform.pojo.TransNNAlgorithmConfig;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Properties;


/**
 *
 */
public class SoonWebsocket implements WebSocket.OnTextMessage, WebSocket.OnControl {

    private final Logger log = LoggerFactory.getLogger(getClass());

    int modelId = -1; // 该websocket连接对应的modelId

    final int id;  // websocket连接的标识符
    int msgId = 0; // websocket连接中消息的索引。
    PlatformCallback pcb = null;  // 该websocket的回调接口
    Connection conn = null;
    PlatformImpl platform = new PlatformImpl();
    ChannelState state = ChannelState.CONFIGURING; // 初始化为配置状态
    static ObjectMapper mapper = new ObjectMapper();  // JSON转换

    Class modelConfigCls = null;  // 存储模型配置类型
    Class monitorDataCls = null;  // 模型训练过程性能变化的类型


    public SoonWebsocket(int id) {
        this.id = id;
    }

    @Override
    public boolean onControl(byte b, byte[] bytes, int i, int i1) {
        return false;
    }



    @Override
    public void onMessage(String s) {
        log.info("received message from {} is {} ", id, s);
        String[] strs = s.split("\n", 3);
        int msgId = Integer.parseInt(strs[0]);
        Indication ind = Indication.extractNotify(strs[1]);
        String content = strs[2];
        if (ind != null) {
            switch (ind) {
                case END_NOTIFY:
                        // 训练结束提示
                        state = ChannelState.COMPLETED;
                        // 通知训练结束
                        pcb.trainingEnd(msgId);
                    break;
                case URL_NOTIFY:
                    // 得到TensorBoard的URL
                    // 通知URL获取
                    URI uri = URI.create(content);
                    pcb.ResultUrl(msgId, uri);
                    break;
                case PROCESS_NOTIFY:
                    // 得到训练过程消息参数
                    try {
                        MonitorData md = (MonitorData)mapper.readValue(content, monitorDataCls);
                        // 通知训练进度
                        pcb.intermediateResult(msgId, md);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case EXCEPTION_CONFIG_NOTIFY:
                    // 异常配置消息参数
                    // 通知配置异常，状态改为CONFIGURING
                    state = ChannelState.CONFIGURING;
                    pcb.configException(msgId, content);
                    break;
                case EXCEPTION_RUN_NOTIFY:
                    // 异常运行参数
                    // 通知运行异常，状态改为STOPPED
                    state = ChannelState.STOPPED;
                    pcb.runningException(msgId, content);
                    break;
                case MODEL_APPLY_NOTIFY:
                    // 模型应用结果通知
                    try {
                        List<List<Double>> results = mapper.readValue(content, new TypeReference<List<List<Double>>>() {});
                        pcb.applyResult(msgId, results);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MODEL_EVAL_NOTIFY:
                    // 模型评估结果通知
                    // content第一行表示测试集id,第二行表示结果
                    try {
                        String[] res = content.split("\n", 2);
                        int testDatasetId = Integer.parseInt(res[0]);
                        List<List<Double>> results = mapper.readValue(res[1], new TypeReference<List<List<Double>>>() {
                        });
                        pcb.evalResult(msgId, testDatasetId, results);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case END_TRAIN_DATASET_NOTIFY:
                    // 某个训练集传输完成通知
                    // content表示训练集id
                    pcb.trainDatasetTransEnd(msgId, Integer.parseInt(content));
                    break;
                case END_TEST_DATASET_NOTIFY:
                    // 某个测试集传输完成通知
                    // content表示测试集id
                    pcb.testDatasetTransEnd(msgId, Integer.parseInt(content));
                    break;
                default:
                    throw new RuntimeException();
            }
        } else {
            // 说明消息发送有误，提示对端
            state.sendMessage(this, conn, Indication.PARSE_FAILURE.getName()+"\n");
        }

    }

    @Override
    public void onOpen(Connection connection) {
        conn = connection;
        log.info("new websocket connection {} opened!", id);
    }

    @Override
    public void onClose(int i, String s) {
        conn.close();
        log.info("websocket connection {} closed!", id);
    }


    /************************************************ 具体消息处理 **********************************************************/
    enum ChannelState {

        /**
         * 训练尚未开始,配置相关参数的状态
         */
        CONFIGURING() {

            /**
             * 只有configuring状态下可以进行模型配置
             */
            @Override
            boolean sendMLConfig(SoonWebsocket sw, Connection conn, MLAlgorithmConfig config) {
                if (config instanceof NNAlgorithmConfig) {
                    TransNNAlgorithmConfig tnc = TransNNAlgorithmConfig.trans((NNAlgorithmConfig)config);
                    return sendObjectMessage(sw, conn, Indication.CONFIG_MODEL, tnc);
                } else {
                    log.error("不可解析的类型");
                    return false;
                }

            }

            @Override
            boolean sendMLType(SoonWebsocket sw, Connection conn, MLAlgorithmType type) {
                String content = Indication.CONFIG_MODEL_TYPE.getName()+"\n"+type.getName();
                return sendMessage(sw, conn, content);
            }

            @Override
            boolean sendTrainData(SoonWebsocket sw, Connection conn, SegmentForDataset trainData) {
                return sendObjectMessage(sw, conn, Indication.CONFIG_TRAIN_DATASET, trainData);
            }

            @Override
            boolean sendTestData(SoonWebsocket sw, Connection conn, SegmentForDataset testData) {
                return sendObjectMessage(sw, conn, Indication.CONFIG_TEST_DATASET, testData);
            }

            @Override
            boolean sendTrainDatasetId(SoonWebsocket sw, Connection conn, int id) {
                String content = Indication.CONFIG_TRAIN_DATASET_ID.getName()+"\n"+id;
                return sendMessage(sw, conn, content);
            }

            @Override
            boolean startTrain(SoonWebsocket sw, Connection conn) {
                String content = Indication.CONTROL_START.getName()+"\n";
                boolean succ = sendMessage(sw, conn, content);
                if (succ) {
                    // 如果发送成功
                    sw.state = ChannelState.RUNNING;
                    return true;
                } else {
                    // 如果发送失败
                    return false;
                }
            }

            @Override
            boolean deleteTrainDataset(SoonWebsocket sw, Connection conn, int trainDataId) {
                StringBuilder builder = new StringBuilder(Indication.CONTROL_DELETE_TRAIN.getName());
                builder.append("\n").append(trainDataId);
                return sendMessage(sw, conn, builder.toString());
            }

            @Override
            boolean deleteTestDataset(SoonWebsocket sw, Connection conn, int testDataId) {
                StringBuilder builder = new StringBuilder(Indication.CONTROL_DELETE_TEST.getName());
                builder.append("\n").append(testDataId);
                return sendMessage(sw, conn, builder.toString());
            }
        },
        /**
         * 正在运行状态
         */
        RUNNING() {

            @Override
            boolean stopTrain(SoonWebsocket sw, Connection conn) {
                String content = Indication.CONTROL_STOP.getName()+"\n";
                boolean succ = sendMessage(sw, conn, content);
                if (succ) {
                    // 如果成功，状态转为STOPPED
                    sw.state = ChannelState.STOPPED;
                    return true;
                } else {
                    // 如果失败
                    return false;
                }
            }
        },
        /**
         * 停止状态。表示未完成训练，但是由于某种原因停止了训练
         */
        STOPPED() {

            @Override
            boolean deleteModel(SoonWebsocket sw, Connection conn) {
                String content = Indication.CONTROL_DELETE_MODEL.getName()+"\n";
                boolean succ = sendMessage(sw, conn, content);
                if (succ) {
                    // 如果发送成功，状态转为CONFIGURING
                    sw.state = ChannelState.CONFIGURING;
                    return true;
                } else {
                    return false;
                }
            }
        },
        /**
         * 完成状态。表示训练已经完成，并且已经有模型输出
         */
        COMPLETED() {

            @Override
            boolean queryURL(SoonWebsocket sw, Connection conn) {
                String content = Indication.GET_URL.getName()+"\n";
                return sendMessage(sw, conn, content);
            }

            @Override
            boolean applyModel(SoonWebsocket sw, Connection conn, List<List<Double>> input) {
                return sendObjectMessage(sw, conn, Indication.MODEL_APPLY, input);
            }

            @Override
            boolean evalModel(SoonWebsocket sw, Connection conn, int testDataId) {
                String content = Indication.MODEL_EVAL.getName()+"\n"+testDataId;
                boolean succ = sendMessage(sw, conn, content);
                return succ;
            }

            @Override
            boolean deleteModel(SoonWebsocket sw, Connection conn) {
                String content = Indication.CONTROL_DELETE_MODEL.getName()+"\n";
                boolean succ = sendMessage(sw, conn, content);
                if (succ) {
                    // 如果发送成功，状态转为CONFIGURING
                    sw.state = ChannelState.CONFIGURING;
                    return true;
                } else {
                    return false;
                }
            }
        };

        private static final Logger log = LoggerFactory.getLogger(ChannelState.class);

        ChannelState() {
            // nothing

        }


        /**
         * 发送训练集数据。只有处在CONFIGURING状态下才能发送训练集数据。
         * @param trainData 训练集数据
         * @return 训练集数据id
         */
        boolean sendTrainData(SoonWebsocket sw, Connection conn, SegmentForDataset trainData) {
            log.info("当前处在{}状态，无法发送训练数据集", sw.state);
            return false;
        }

        /**
         * 发送测试集数据
         *
         */
        boolean sendTestData(SoonWebsocket sw, Connection conn, SegmentForDataset testData) {
            log.info("当前处在{}状态，无法发送测试数据集", sw.state);
            return false;
        }


        /**
         * 发送模型使用的算法类型，以便为接下来的模型参数配置提供方法。只有处在CONFIGURING状态下才能发送模型类型，其他状态下不能。
         * @param type
         * @return
         */
        boolean sendMLType(SoonWebsocket sw, Connection conn, MLAlgorithmType type) {
            // 此时的状态不适合发送该消息
            log.info("当前处在{}状态，无法配置模型所使用的算法类型！！！", sw.state);
            return false;
        }

        /**
         * 发送具体的模型细节，包括算法类型，模型配置参数等等。只有处在CONFIGURING状态下才能发送模型细节，其他状态下发送都是非法的。
         * @param config
         */
        boolean sendMLConfig(SoonWebsocket sw, Connection conn, MLAlgorithmConfig config) {
            // 此时的状态不适合发送该消息
            log.info("当前处在{}状态，无法更改模型配置！！！", sw.state);
            return false;
        }


        /**
         * 发送训练集id。如果之前已经发送过训练集id，则表示更新训练集id。
         * 只有处在CONFIGURING状态才能更换训练集id
         * @param conn websocket连接
         * @param id 训练集id
         * @return
         */
        boolean sendTrainDatasetId(SoonWebsocket sw, Connection conn, int id) {
            log.info("当前处在{}状态，无法更改训练集id！！！", sw.state);
            return false;
        }


        /**
         * 开始训练。只有处在CONFIGURING才能开始训练
         */
        boolean startTrain(SoonWebsocket sw, Connection conn) {
            log.info("当前处在{}状态，无法开始训练！！！", sw.state);
            return false;
        }

        /**
         * 停止训练。只有处在RUNNING状态才能停止训练，停止后状态转为STOPPED
         */
        boolean stopTrain(SoonWebsocket sw, Connection conn) {
            log.info("当前处在{}状态，无法停止训练！！！", sw.state);
            return false;
        }

        /**
         * 请求TensorBoard的URL地址。只有处在COMPLETED状态才能得到
         * @return 如果发生错误，则返回false
         */
        boolean queryURL(SoonWebsocket sw, Connection conn) {
            log.info("当前处在{}状态，无法请求Tensorboard的URL！！！", sw.state);
            return false;
        }

        /**
         * 应用模型，得到结果。只有在状态机处在COMPLETED状态的时候，才能进行模型应用，其他时候都返回null
         * @param input 输入参数
         * @return 返回false表示当前无法应用模型。
         */
        boolean applyModel(SoonWebsocket sw, Connection conn, List<List<Double>> input) {
            log.info("当前处在{}状态，无法进行模型应用！！！", sw.state);
            return false;
        }

        /**
         * 在远端删除该模型。只有处在COMPLETED和STOPPED状态的时候，才能删除模型
         */
        boolean deleteModel(SoonWebsocket sw, Connection conn) {
            log.info("当前处在{}状态，无法进行模型删除！！！", sw.state);
            return false;
        }

        /**
         * 在远端删除训练集。只有在CONFIGURING状态下才能删除训练集
         * @param trainDataId 被删除的训练集id
         */
        boolean deleteTrainDataset(SoonWebsocket sw, Connection conn, int trainDataId) {
            log.info("当前处在{}状态，无法进行训练集删除！！！", sw.state);
            return false;
        }

        /**
         * 在远端删除测试集。只有在CONFIGURING状态下才能删除测试集。
         * @param testDataId 要删除的测试集id
         */
        boolean deleteTestDataset(SoonWebsocket sw, Connection conn, int testDataId) {
            log.info("当前处在{}状态，无法进行测试集{}的删除！！！", sw.state, testDataId);
            return false;
        }

        /**
         * 在测试集上评估模型的效果.只有COMPLETED状态下才能使用该方法
         * @param sw
         * @param conn
         * @param testDataId
         * @return
         */
        boolean evalModel(SoonWebsocket sw, Connection conn, int testDataId) {
            log.info("当前处在{}状态，无法进行模型在测试集{}上的评估！！！", sw.state, testDataId);
            return false;
        }


        /**
         * 通过连接conn发送消息content给对端
         * @param conn websocket连接
         * @param content 消息内容
         * @return 是否发送成功
         */
        boolean sendMessage(SoonWebsocket sw, Connection conn, String content) {
            try {
                sw.msgId = sw.msgId+1;
                log.info("message size is "+content.length());
                conn.sendMessage(sw.msgId+"\n"+content);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        /**
         * 通过连接conn发送带Object对象的内容给对端
         * @param conn websocket连接
         * @param ind 类型
         * @param obj 对象
         * @return 是否发送成功
         */
        boolean sendObjectMessage(SoonWebsocket sw, Connection conn, Indication ind, Object obj) {
            StringBuilder builder = new StringBuilder(ind.getName());
            try {
                String data = mapper.writeValueAsString(obj);
                builder.append("\n").append(data);
                return sendMessage(sw, conn, builder.toString());
            } catch (JsonProcessingException e) {
                return false;
            }
        }
    }


//    /**
//     * 做简单的最小集测试
//     */
//    private void test(SoonWebsocket ws) {
//        try {
//            // 模型id
//            ws.state.sendMLType(ws, ws.conn, MLAlgorithmType.FCNNModel);
//            monitorDataCls = NNMonitorData.class;
//            modelConfigCls = NNAlgorithmConfig.class;
//
//            // 发送数据集
//            SegmentForDataset sfd = new SegmentForDataset();
//            double i = 1.1;
//            List<List<Double>> datas = Lists.newArrayList();
//            for (int j = 0; j < 3; j++) {
//                List<Double> doubles = Lists.newArrayList(i, i + 1.1, i + 2.2);
//                i += 10;
//                datas.add(doubles);
//            }
//            sfd.setDatas(datas);
//            sfd.setTrainData(true);
//            ws.state.sendTrainData(ws, ws.conn, sfd);
//            sfd.setTrainData(false);
//            ws.state.sendTestData(ws, ws.conn, sfd);
//
//            // 发送模型参数
//            NNAlgorithmConfig nnconfig = new NNAlgorithmConfig(MLAlgorithmType.FCNNModel, 30, 7,
//                    Lists.newArrayList(1, 2, 3), ActivationFunction.RELU, ParamInit.DEFAULT, ParamInit.CONSTANT0,
//                    LossFunction.MSELOSS, 64, 100, Optimizer.NESTROV,
//                    1e-4, LRAdjust.LINEAR, 0.0);
//            ws.state.sendMLConfig(ws, ws.conn, nnconfig);
//
//            // 开始训练
//            ws.state.startTrain(ws, ws.conn);
//
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
