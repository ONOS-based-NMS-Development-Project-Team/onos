package org.onosproject.soon.dataset.dataset;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

public class SegmentForDataset {

    // 判断是训练集还是测试集
    private boolean isTrainData;
    // 数据集id
    private int datasetId;
    // 该对象是否表示了该id数据集的所有数据？因为大量数据传输可能造成中断，因此增加这个选项可以支持分段传输。
    private boolean isPartOfDataset;
    // 该对象在该id数据集中的序列下标。如果该对象就是完整的数据集本身，则值必须为0
    private int index;
    // 数据内容
    private List<List<Double>> datas;


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

    public boolean isPartOfDataset() {
        return isPartOfDataset;
    }

    public void setPartOfDataset(boolean partOfDataset) {
        isPartOfDataset = partOfDataset;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<List<Double>> getDatas() {
        return datas;
    }

    public void setDatas(List<List<Double>> datas) {
        this.datas = datas;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SegmentForDataset that = (SegmentForDataset) o;

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
