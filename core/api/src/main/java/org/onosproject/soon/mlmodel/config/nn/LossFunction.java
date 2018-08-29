package org.onosproject.soon.mlmodel.config.nn;


/**
 * loss函数计算
 */
public enum LossFunction {

    L1LOSS() {
        @Override
        public String getName() {
            return "l1loss";
        }
    },
    MSELOSS() {
        @Override
        public String getName() {
            return "mseloss";
        }
    },
    NLLLOSS() {
        @Override
        public String getName() {
            return "nllloss";
        }
    },
    CROSSENTROPY() {
        @Override
        public String getName() {
            return "crossentropyloss";
        }
    };

    public String getName() {
        return null;
    }
}
