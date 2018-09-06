package univ.bupt.soon.almpred;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.soon.dataset.DatabaseAdapter;
import org.onosproject.soon.dataset.original.AlarmPredictionItem;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.foreground.ModelControlServiceAbstract;
import org.onosproject.soon.platform.MLPlatformService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class AlarmPredService extends ModelControlServiceAbstract {

    public AlarmPredService(MLAppType serviceName, String tableName, Class itemClass, Class platformCallbackClass,
                            DatabaseAdapter database, MLPlatformService platformService) {
        super(serviceName, tableName, itemClass, platformCallbackClass, database, platformService);
    }

    @Override
    public Pair<Set<Integer>, Set<Integer>> transAvailableDataset(int i) {
        // 可用的数据集有两个
        // TODO
    }

    @Override
    public List<List<Double>> parseInput(List<Item> data) {
        List<List<Double>> inputData = Lists.newArrayList();
        for (Item lpi : data) {
            AlarmPredictionItem item = (AlarmPredictionItem) lpi;
            List<Double> tmpIn = Lists.newArrayList();
            // 解析输入
            for (double d : item.getInput()) {
                tmpIn.add(d);
            }
            inputData.add(tmpIn);
        }
        return inputData;
    }


    /**
     * 要保证拿到的都是训练集的数据,并且要作出训练集类型的区分
     * TODO 这里没有做任何区分!!! 需要修改
     */
    @Override
    public List<Item> updateData(int offset, int limit) {
        return  database.queryData("*", " offset "+offset+" limit "+limit,
                tableName, itemClass);
    }


}
