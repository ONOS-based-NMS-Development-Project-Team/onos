package univ.bupt.soon.mlshow.front.handler.model;

import com.google.common.base.Objects;
import org.onosproject.soon.foreground.MLAppType;
import org.onosproject.soon.mlmodel.MLAlgorithmType;
import org.onosproject.soon.mlmodel.MLModelDetail;

import java.util.Date;

import java.util.Map;

/**
 * #soonEvent:modelLibraryManagementRequest   对应的response也是modelLibraryDataResponse
 * {
 * 	"event":"modelLibraryManagementRequest",
 * 	"payload":{
 * 		"modelId":"",
 * 		"action":"",
 * 		"applicationType":"",
 * 		"algorithmType":"",
 * 		"trainDataSetId":"",
 * 		"testDataSetId":[],
 * 		"modelAccuracy":"",
 * 		"algorithmParams":{}
 *        }
 * #event:modelLibraryManagementResponse
 * {
 * 	"event":"modelLibraryManagementResponse",
 * 	"payload":{
 * 		"modelLibrarys":[{
 * 			"applicationType":"",
 * 			"modelId":"",
 * 			"algorithmType":"",
 * 			"trainDataSetId":"",
 * 			"testDataSetId":[],
 * 			"modelState":"",
 * 		    "loss":"",
 * 		    "remainingTime":"",
 * 		    "precision":"",
 * 		    "modelLink":"",
 * 			"modelAccuracy":"",
 * 			"algorithmParams":{}
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 * }
 */

public class ModelLibraryInfo {
    //模型相关信息.Pair.Left表示模型id,Pair.Right表示模型的其他信息
    private MLAppType mlAppType;
    private MLAlgorithmType mlAlgorithmType;
    private MLModelDetail mlModelDetail;
    private final int modelId;
    private int[] testDataSetId;
    private double loss;
    private Date remainingTime;
    private double precision;
    private String modelLink;


    public ModelLibraryInfo (MLAppType mlAppType,MLAlgorithmType mlAlgorithmType,MLModelDetail mlModelDetail,int modelId) {
        this.mlAppType = mlAppType;
        this.mlAlgorithmType = mlAlgorithmType;
        this.mlModelDetail = mlModelDetail;
        this.modelId = modelId;
    }

    public MLAppType getMlAppType () { return mlAppType; }

    public void setMlAppType (MLAppType mlAppType) {this.mlAppType = mlAppType;}

    public MLAlgorithmType getMlAlgorithmType() { return mlAlgorithmType; }

    public void setMlAlgorithmType(MLAlgorithmType mlAlgorithmType) { this.mlAlgorithmType = mlAlgorithmType; }

    public MLModelDetail getMlModelDetail () {return mlModelDetail;}

    public void setMlModelDetail (MLModelDetail mlModelDetail) {this.mlModelDetail = mlModelDetail;}

    public int[] getTestDataSetId () {return testDataSetId;}

    public void setTestDataSetId (int[] testDataSetId) {this.testDataSetId = testDataSetId;}

    public Date getRemainingTime () {return remainingTime;}

    public void setRemainingTime (Date remainingTime) {this.remainingTime = remainingTime;}

    public double getLoss() { return loss; }

    public void setLoss(double loss) { this.loss = loss; }

    public double getPrecision() { return precision; }

    public void setPrecision(double precision) { this.precision = precision; }

    public String getModelLink() { return modelLink; }

    public void setModelLink(String modelLink) { this.modelLink = modelLink; }

    public int getModelId () {return modelId;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelLibraryInfo that = (ModelLibraryInfo) o;
        return Objects.equal(mlAppType, that.mlAppType) &&
                Objects.equal(modelId, that.modelId) &&
                Objects.equal(mlModelDetail, that.mlModelDetail) &&
                Objects.equal(testDataSetId,that.testDataSetId) &&
                Objects.equal(loss,that.loss) &&
                Objects.equal(remainingTime,that.remainingTime) &&
                Objects.equal(precision,that.precision) &&
                Objects.equal(modelLink,that.modelLink);
    }

    @Override
    public int hashCode () {return Objects.hashCode(mlAppType,mlModelDetail,modelId,testDataSetId,loss,remainingTime,precision,modelLink);}
}
