package univ.bupt.soon.mlshow.front.handler.model;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.onosproject.soon.MonitorData;
import org.onosproject.soon.dataset.original.Item;
import org.onosproject.soon.foreground.ForegroundCallback;

import java.net.URI;
import java.util.List;

public abstract class ForegroundCallbackAbstract implements ForegroundCallback {


    @Override
    public void operationFailure(int msgId,String description){

    }

    @Override
    public void appliedModelResult(int msgId, List<Item> input, List<String> output) {

    }

    @Override
    public void modelEvaluation(int msgId, String result) {

    }

    @Override
    public void trainDatasetTransEnd(int msgId, int trainDatasetId) {

    }

    @Override
    public void testDatasetTransEnd(int msgId, int testDatasetId) {

    }

    @Override
    public void ResultUrl(int msgId, URI uri) {

    }

    @Override
    public void trainEnd(int msgId) {

    }
    @Override
    public void intermediateResult(int msgId, MonitorData monitorData) {

    }
}
