package org.onosproject.soon;

/**
 * 机器学习应用对应的类型
 */
public enum MLAppType {
    ORIGINAL_DATA,  // 获取原始网络数据，不进行机器学习应用
    ALARM_PREDICTION,  // 告警预测应用
    FAILURE_LOCATION,  // 故障定位应用
    FAILURE_CLASSIFICATION,  // 故障分类应用。该应用和故障定位应用在同一个app里实现。
}
