package org.onosproject.soon.mlmodel.config.nn;

/**
 * 参数初始化方法
 */
public enum ParamInit {
    // 按照默认方法初始化，即不指定初始化方法
    DEFAULT () {
        @Override
        public String getName() {
            return "default";
        }
    },
    // 初始化为常量0
    CONSTANT0 () {
        @Override
        public String getName() {
            return "constant0";
        }
    },
    // 初始化为常量1
    CONSTANT1 () {
        @Override
        public String getName() {
            return "constant1";
        }
    },
    // 随机初始化
    RANDOM () {
        @Override
        public String getName() {
            return "random";
        }
    };


    public String getName() {
        return null;
    }
}
