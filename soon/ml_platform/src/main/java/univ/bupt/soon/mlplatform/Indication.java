package univ.bupt.soon.mlplatform;


import java.net.URL;

/**
 * 与python端交互过程中，会遇到的消息头定义。
 */
public enum Indication {

    /** 1. 配置训练集，模型参数 **/
    // 配置训练数据集
    CONFIG_TRAIN_DATASET() {
        public String getName() {
            return "/config/train";
        }
    },
    // 配置测试数据集
    CONFIG_TEST_DATASET() {
        public String getName() {
            return "/config/test";
        }
    },
    // 配置模型所使用的算法类型
    CONFIG_MODEL_TYPE() {
        @Override
        public String getName() {
            return "/config/model/type";
        }
    },
    // 配置模型参数
    CONFIG_MODEL() {
        public String getName() {
            return "/config/model";
        }
    },
    // 配置训练数据集id，表示更换训练数据集
    CONFIG_TRAIN_DATASET_ID() {
        public String getName() {
            return "/config/train/id";
        }
    },


    /** 2. 开始，停止训练 **/
    CONTROL_START() {
        public String getName() {
            return "/control/start";
        }
    },
    CONTROL_STOP() {
        public String getName() {
            return "/control/stop";
        }
    },

    /** 3. 模型应用  **/
    // 应用模型
    MODEL_APPLY() {
        public String getName() {
            return "/apply";
        }
    },
    // 获取模型应用结果
    MODEL_APPLY_NOTIFY() {
        public String getName() {
            return "/notify/apply";
        }
    },

    /** 4. 删除模型，训练集  **/
    // 删除指定训练集
    CONTROL_DELETE_TRAIN () {
        public String getName() {
            return "/control/delete/train";
        }
    },
    // 删除指定测试集
    CONTROL_DELETE_TEST() {
        public String getName() {
            return "/control/delete/test";
        }
    },
    // 删除该模型
    CONTROL_DELETE_MODEL() {
        public String getName() {
            return "/control/delete/model";
        }
    },

    /** 5. 获取训练链接  **/
    // 请求训练结束后的tensorboard的uri
    GET_URL() {
        public String getName() {
            return "/get/uri";
        }
    },
    // 回复训练结束后的uri请求
    URL_NOTIFY() {
        public String getName() {
            return "/notify/uri";
        }
    },

    /** 6. 异常  **/
    // 接收到配置异常信息
    EXCEPTION_CONFIG_NOTIFY() {
        public String getName() {
            return "/notify/exception/configuring";
        }
    },
    // 接收到运行异常信息
    EXCEPTION_RUN_NOTIFY() {
        public String getName() {
            return "/notify/exception/running";
        }
    },


    /** 7. 训练结束提示  **/
    // 接收训练结束提示信息
    END_NOTIFY() {
        public String getName() {
            return "/notify/train_end";
        }
    },


    /** 8. 训练过程中的数据更新  **/
    PROCESS_NOTIFY() {
        public String getName() {
            return "/notify/process";
        }
    },

    /** 9. 接收消息的解析失败 **/
    PARSE_FAILURE() {
        public String getName() {
            return "/parse/failure";
        }
    };


    public abstract String getName();

    /**
     * 将str转化成对应的indication
     * @param str
     * @return
     */
    public static Indication extractNotify(String str) {
        if (str.equals(PROCESS_NOTIFY.getName())) {
            return PROCESS_NOTIFY;
        } else if (str.equals(EXCEPTION_CONFIG_NOTIFY.getName())) {
            return EXCEPTION_CONFIG_NOTIFY;
        } else if (str.equals(EXCEPTION_RUN_NOTIFY.getName())) {
            return EXCEPTION_RUN_NOTIFY;
        }else if (str.equals(END_NOTIFY.getName())) {
            return END_NOTIFY;
        } else if (str.equals(URL_NOTIFY.getName())) {
            return URL_NOTIFY;
        } else if (str.equals(MODEL_APPLY_NOTIFY.getName())) {
            return MODEL_APPLY_NOTIFY;
        } else {
            return null;
        }
    }
}
