package org.onosproject.soon.mlmodel.config.nn;

/**
 * 激活函数的选择
 */
public enum ActivationFunction {

    SIGMOID() {
        @Override
        public String getName() {
            return "sigmoid";
        }
    },
    RELU() {
        @Override
        public String getName() {
            return "relu";
        }
    },
    RELU6() {
        @Override
        public String getName() {
            return "relu6";
        }
    },
    TANH() {
        @Override
        public String getName() {
            return "tanh";
        }
    };


    public String getName() {
        return null;
    }
}
