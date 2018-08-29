package org.onosproject.soon.dataset;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.onosproject.soon.dataset.dataset.SegmentForDataset;
import org.onosproject.soon.dataset.original.Item;

import java.util.List;

/**
 * 训练集/测试集数据片段
 */
public abstract class DataSegment <T extends Item> {

    // 判断是训练集还是测试集
    private boolean isTrainData;
    // 数据集id
    private int datasetId;
    // 该对象是否表示了该id数据集的所有数据？因为大量数据传输可能造成中断，因此增加这个选项可以支持分段传输。
    private boolean isPartOfDataset;
    // 该对象在该id数据集中的序列下标。如果该对象就是完整的数据集本身，则值必须为0
    private int index;
    // 数据内容
    private List<T> datas;

    /**
     * 将数据数字化，并且转换成list形式
     * @return
     */
    public abstract List<List<Double>> convertData();


    /**
     * 获取可以用于训练的数据集片段
     * @return
     */
    public SegmentForDataset convertForDataset() {
        List<List<Double>> data = convertData();
        SegmentForDataset rtn = new SegmentForDataset();
        rtn.setDatas(data);
        rtn.setDatasetId(datasetId);
        rtn.setIndex(index);
        rtn.setPartOfDataset(isPartOfDataset);
        rtn.setTrainData(isTrainData);
        return rtn;
    }


    public boolean isPartOfDataset() {
        return isPartOfDataset;
    }

    public void setPartOfDataset(boolean partOfDataset) {
        isPartOfDataset = partOfDataset;
    }


    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public boolean isTrainData() {
        return isTrainData;
    }

    public void setTrainData(boolean trainData) {
        isTrainData = trainData;
    }

    public int getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        DataSegment<?> that = (DataSegment<?>) o;

        return new EqualsBuilder()
                .append(isTrainData, that.isTrainData)
                .append(datasetId, that.datasetId)
                .append(isPartOfDataset, that.isPartOfDataset)
                .append(index, that.index)
                .append(datas, that.datas)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(isTrainData)
                .append(datasetId)
                .append(isPartOfDataset)
                .append(index)
                .append(datas)
                .toHashCode();
    }
}
