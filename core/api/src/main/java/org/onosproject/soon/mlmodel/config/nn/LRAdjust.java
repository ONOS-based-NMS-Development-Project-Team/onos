package org.onosproject.soon.mlmodel.config.nn;

/**
 * 学习率调整方法
 */
public enum LRAdjust {
    CONSTANT() {
        @Override
        public String getName() {
            return "constant";
        }
    },
    LINEAR() {
        @Override
        public String getName() {
            return "linear";
        }
    },
    MULTIPLE() {
        @Override
        public String getName() {
            return "multiple";
        }
    },
    ONPLATEAU() {
        @Override
        public String getName() {
            return "onplateau";
        }
    };


    public String getName() {
        return null;
    }

    public static LRAdjust parseStr(String str) {
        switch (str) {
            case "constant":
                return CONSTANT;

            case "linear":
                return LINEAR;

            case "multiple":
                return MULTIPLE;

            case "onplateau":
                return ONPLATEAU;

            default:
                return null;
        }
    }
}
