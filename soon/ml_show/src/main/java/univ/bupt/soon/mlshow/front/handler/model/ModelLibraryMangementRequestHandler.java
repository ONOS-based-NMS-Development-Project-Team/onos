package univ.bupt.soon.mlshow.front.handler.model;

/**
 * #soonEvent:modelLibraryMangementRequest   对应的response也是modelLibraryDataResponse
 * {
 * 	"event":"modelLibraryMangementRequest",
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
 * #event:modelLibraryMangementResponse
 * {
 * 	"event":"modelLibraryMangementResponse",
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
 * }
 */
public class ModelLibraryMangementRequestHandler {
}
