package org.onosproject.soon.foreground;

/**
 * 基于机器学习的应用的注册服务
 */
public interface MLAppRegistry {

    /**
     * 注册基于机器学习的应用服务
     * @param service 模型控制服务
     * @param name 基于机器学习的应用名
     * @return 注册是否成功
     */
    boolean register(ModelControlService service, MLAppType name);

    /**
     * 注销基于机器学习的应用服务
     * @param name 应用名
     * @return 注销是否成功
     */
    boolean unregister(MLAppType name);
}
