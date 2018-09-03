package univ.bupt.soon.mlshow.front.handler.dataset;

/**
 * #event:modelLibraryDataRequest
 * {
 * 	"event":"modelLibraryDataRequest",
 * 	"payload":{
 * 		"firstCol":"applicationType",
 * 		"firstDir":"asc",
 * 		"secondCol":"modelId",
 * 		"secondDir":"asc"
 *
 *        }
 * }
 * #event:modelLibraryDataResopnse
 * {
 * 	"event":"modelLibraryDataResopnse",
 * 	"payload":{
 * 		"modelLibrarys":[{
 * 			"applicationType":"",
 * 			"modelId":"",
 * 			"algorithmType":"",
 * 			"trainDataSetId":"",
 * 			"testDataSetId":[],
 * 			"modelState":"",
 * 			"modelAccuracy":"",
 * 			"algorithmParams":{}
 *        },{}],
 * 		"ANNOTS":{}
 *    }
 * }
 */
public class ModelLibraryDataRequestHandler {
}
