package univ.bupt.soon.servadj.Service;

import univ.bupt.soon.servadj.Topology.SimpleEdge;
import univ.bupt.soon.servadj.Topology.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yuqia_000 on 2017/6/17.
 */
public class ComputePath extends Thread {
    public HashMap<Service, GraphPath> serviceGraphPathHashMap = new HashMap<Service, GraphPath>();
    public BlockingQueue<Service> serviceBlockingQueue;
    public SimpleWeightedGraph<Vertex, SimpleEdge> graph;

//    public ComputePath(BlockingQueue<org.apache.felix.scr.annotations.Service> servicesToComputePath, SimpleWeightedGraph<Vertex, SimpleEdge> graph) {
//
//    }

    public ComputePath(BlockingQueue<Service> bq, SimpleWeightedGraph graph) {
        this.serviceBlockingQueue = bq;
        this.graph = graph;
    }

    public GraphPath findShortestPath(Service service, SimpleWeightedGraph graph) {
        Vertex srcNode = service.srcNode;
        Vertex desNode = service.desNode;
        GraphPath shortestPath = DijkstraShortestPath.findPathBetween(graph, srcNode, desNode);
        service.isComputed = true;
        return shortestPath;
    }

    public void allocateResource(Service service) {
        try {
            GraphPath servicePath = serviceGraphPathHashMap.get(service);
            List<SimpleEdge> edgeList = servicePath.getEdgeList();
            int n = service.numberOfWavelenthes;
            /** 分配波长(满足波长一致性) */
            int count = 0;  //记录各边都满足的连续波长数
            List<Integer> freeWavelenthesNumber = new ArrayList<Integer>(); // 统计空闲波长号用
            //资源统计
            for(int i = 0; i < SimpleEdge.DEFAULTNUMBEROFWAVELENTHES; i ++) {    //对于每个波长号，统计各个边的该波长号是否都空闲
                int edgeCount = 0;
                Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                while (edgeIterator.hasNext()) {
                    SimpleEdge currentEdge = edgeIterator.next();
                    if(currentEdge.wavelenthOccupation[i] == false) {
                        edgeCount++;
                    }
                }
                if(edgeCount == edgeList.size()) {
                    count++;
                    freeWavelenthesNumber.add(Integer.valueOf(i));
                }
            }
            //分配资源
            if(count >= n) {
                System.out.printf("分配的波长资源：");
                for(int i = 0; i < n; i++) {
                    int currentWavelenthNumber = freeWavelenthesNumber.get(i).intValue();   //取出波长号
                    service.wavelengthesNumber.add(Integer.valueOf(currentWavelenthNumber));//将波长号放入servce对象中
                    Iterator<SimpleEdge> edgeIterator = edgeList.iterator();
                    while (edgeIterator.hasNext()) {
                        SimpleEdge currentEdge = edgeIterator.next();
                        currentEdge.wavelenthOccupation[currentWavelenthNumber] = true;
                    }
                    System.out.print("[" + currentWavelenthNumber + "]");
                }
                System.out.printf("\n");
                service.isAllocated = true;

            }else {
                service.isBlocked = true;
                service.wavelengthesNumber.clear(); //如果分配资源不成功，就释放service对象占用的波长号
                System.out.println("没有足够资源分配给业务 " + service.serviceId + " 。");
            }




        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        while (true) {
            try {
                /**算路*/
                Service service = serviceBlockingQueue.take();
                GraphPath graphPath = findShortestPath(service, this.graph);
                serviceGraphPathHashMap.put(service, graphPath);
                service.isComputed = true;
                service.setGraphPath(graphPath);
                System.out.printf("业务 " + service.serviceId + " 已算路: ");
                List<Vertex> vertexList = graphPath.getVertexList();
                Iterator<Vertex> iterator = vertexList.iterator();
                while (iterator.hasNext()) {
                    Vertex vertex = iterator.next();
                    if(iterator.hasNext() == true) {
                        System.out.printf(vertex.nodeId + " -> ");
                    }else {
                        System.out.printf(vertex.nodeId + "\n");
                    }
                }

                /**资源分配*/
                allocateResource(service);

                /**业务离去*/
                if(service.isResourceAllocated() == true) {     //如果分配了资源
                    Timer leavingTimer = new Timer();
                    ServiceLeavingTask serviceLeavingTask = new ServiceLeavingTask(service);
                    leavingTimer.schedule(serviceLeavingTask, service.serviceTime * 1000);  //业务时间结束后离去
                }


            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
