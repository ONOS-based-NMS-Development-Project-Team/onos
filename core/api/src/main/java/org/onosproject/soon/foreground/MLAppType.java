package org.onosproject.soon.foreground;

/**
 * 机器学习应用对应的类型。一个app module中可能包含多个机器学习应用。
 */
public enum MLAppType {
    // 获取原始网络的历史告警数据
    ORIGINAL_HISTORY_ALARM_DATA() {
        @Override
        public String getName() {
            return "original_history_alarm_data";
        }
    },
    // 获取原始网络的当前告警数据
    ORIGINAL_CURRENT_ALARM_DATA() {
        @Override
        public String getName() {
            return "original_current_alarm_data";
        }
    },
    // 获取原始网络的性能数据
    ORIGINAL_PERFORMANCE_DATA() {
        @Override
        public String getName() {
            return "original_performance_data";
        }
    },
    // 告警预测应用
    ALARM_PREDICTION() {
        @Override
        public String getName() {
            return "alarm_prediction";
        }
    },
    // 故障定位应用
    FAILURE_LOCATION() {
        @Override
        public String getName() {
            return "failure_location";
        }
    },
    // 故障分类应用。该应用和故障定位应用在同一个app里实现。
    FAILURE_CLASSIFICATION() {
        @Override
        public String getName() {
            return "failure_classification";
        }
    },
    // 商业区业务预测
    BUSINESS_AREA_PREDICTION() {
        @Override
        public String getName() {
            return "business_area_prediction";
        }
    },
    // 住宅区业务预测
    RESIDENTIAL_AREA_PREDICTION() {
        @Override
        public String getName() {
            return "residential_area_prediction";
        }
    },
    // 链路预测
    LINK_PREDICTION() {
        @Override
        public String getName() {
            return "link_prediction";
        }
    };


    public String getName() {
        return null;
    }
}
