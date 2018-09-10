package org.onosproject.soon.mlmodel;


/**
 * 训练所使用的网络模型
 */
public enum MLAlgorithmType {
    // 全连接神经网络
    FCNNModel() {
        @Override
        public String getName() {
            return "fcnn";
        }
    },
    RNNModel() {
        @Override
        public String getName() {
            return "rnn";
        }
    },
    CNNModel() {
        @Override
        public String getName() {
            return "cnn";
        }
    },
    LSTMModel() {
        @Override
        public String getName() {
            return "lstm";
        }
    },
    RandomForestModel() {
        @Override
        public String getName() {
            return "randomforest";
        }
    };

    public String getName() {
        return null;
    }

    public static MLAlgorithmType parseStr(String str) {
        switch (str) {
            case "fcnn":
                return FCNNModel;

            case "rnn":
                return RNNModel;

            case "cnn":
                return CNNModel;

            case "lstm":
                return LSTMModel;

            case "randomforest":
                return RandomForestModel;

            default:
                return null;
        }
    }
}
