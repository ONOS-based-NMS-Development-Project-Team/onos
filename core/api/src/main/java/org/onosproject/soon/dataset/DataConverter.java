package org.onosproject.soon.dataset;

import org.onosproject.soon.dataset.original.Item;

import java.util.List;

/**
 * 将原始数据转换为可以直接进行训练的训练集数据的工具类
 */
public interface DataConverter {

    /**
     * 将多条原始数据转化成可以直接用于训练的数据形式
     * @param items 数据
     * @param containLabel 是否包含标签
     * @return 可用于训练的数据
     */
    List<List<Double>> convertSegment(List<Item> items, boolean containLabel);


    /**
     * 将一条原始数据转化成可以直接用于训练的数据形式
     * @param item 数据
     * @param containLabel 是否包含标签
     * @return 可用于训练的数据
     */
    List<Double> convertOneItem(Item item, boolean containLabel);

}
