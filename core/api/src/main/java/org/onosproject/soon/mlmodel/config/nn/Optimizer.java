package org.onosproject.soon.mlmodel.config.nn;


/**
 * 优化器
 */
public enum Optimizer {
    SGD() {
        @Override
        public String getName() {
            return "sgd";
        }
    },
    ADAMSGD() {
        @Override
        public String getName() {
            return "adamsgd";
        }
    },
    NESTROV() {
        @Override
        public String getName() {
            return "nestrov";
        }
    };


    public String getName() {
        return null;
    }
}
