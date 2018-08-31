package univ.bupt.soon.servadj.Topology;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jgrapht.graph.SimpleWeightedGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleGraph {
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph= new SimpleWeightedGraph<Vertex, SimpleEdge>(SimpleEdge.class);
    public HashMap<String, Vertex> vertexHashMap = new HashMap<String, Vertex>();

    public SimpleWeightedGraph<Vertex,SimpleEdge> parseJsonToGraph() throws IOException {
        ObjectMapper mapper = new ObjectMapper();


        try {
            JsonNode root = mapper.readTree(new FileReader("src/main/java/univ/bupt/soon/servconstruct/Topology/DefaultTopology.json"));
            JsonNode jsonGraph = root.path("Graph");
            JsonNode vertex = jsonGraph.path("vertex");
            JsonNode jsonEdge = jsonGraph.get("edge");


            Iterator<JsonNode> edgeElementfield = jsonEdge.elements();
            while (edgeElementfield.hasNext()) {
                JsonNode element = edgeElementfield.next();

                String srcId = element.get("srcId").toString();
                String desId = element.get("desId").toString();
                double metric = element.get("metric").asDouble();



                Vertex srcNode = new Vertex(srcId);
                Vertex desNode = new Vertex(desId);
                vertexHashMap.put(srcId, srcNode);
                vertexHashMap.put(desId, desNode);
                graph.addVertex(srcNode);
                graph.addVertex(desNode);
                SimpleEdge simpleEdge = new SimpleEdge(srcNode, desNode);
                //SimpleEdge edge =
                graph.addEdge(srcNode, desNode, simpleEdge);
                graph.setEdgeWeight(simpleEdge, metric);
//            }
        }
            }catch (Exception e) {
        e.printStackTrace();
            }

        return this.graph;
        }

//        public static void main(String[] args) {
//        //拓扑读取
//        SimpleGraph simpleGraph = new SimpleGraph();
//            try {
//                simpleGraph.parseJsonToGraph();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            //业务发生 与 初步算路
//        BlockingQueue<Service> servicesToComputePath = new ArrayBlockingQueue<Service>(10);
//        PoissionStream poissionStreamThread = new PoissionStream(servicesToComputePath);
//        ComputePath computePathThread = new ComputePath(servicesToComputePath, simpleGraph.graph);
//
//        poissionStreamThread.start();
//        computePathThread.start();
//        }


}


