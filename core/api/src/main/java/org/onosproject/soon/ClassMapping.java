package org.onosproject.soon;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.mlmodel.MLAlgorithmConfig;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.config.nn.NNAlgorithmConfig;
import org.onosproject.soon.mlmodel.config.nn.NNMonitorData;

import java.util.Map;

/**
 * 模型映射
 */
public class ClassMapping {

    private ClassMapping(){}

    // 算法类型对应的算法配置类，和返回结果类
    public static Map<MLAlgorithmType, Pair<Class, Class>> mappings = Maps.newHashMap();

    static {
        mappings.put(MLAlgorithmType.FCNNModel, Pair.of(NNAlgorithmConfig.class, NNMonitorData.class));
    }
}
