package univ.bupt.soon.servadj.Topology;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Created by yuqia on 2017/6/15.
 */
public class SimpleEdge extends DefaultWeightedEdge {
    public Vertex srcVertex;
    public Vertex desVertex;
    public double capacity;
    public double unitWavelength = 6.25;  //每个波长的固定带宽
    public double metric;
    public int numberOfWavelenth;
    public boolean[] wavelenthOccupation;
    public static int DEFAULTNUMBEROFWAVELENTHES = 40;
    public SimpleEdge() {

    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex) {
        this(srcVertex, desVertex, 100, DEFAULTNUMBEROFWAVELENTHES);//默认链路带宽容量和波长数
    }

    public SimpleEdge(Vertex srcVertex, Vertex desVertex, double capacity, int numberOfWavelenth) {
        this.srcVertex = srcVertex;
        this.desVertex = desVertex;
        this.capacity = capacity;
        this.numberOfWavelenth = numberOfWavelenth;
        this.wavelenthOccupation = new boolean[numberOfWavelenth];
        for(int i = 0; i < numberOfWavelenth; i++) {
            wavelenthOccupation[i] = false;
        }
    }

    /*
    @Override
    public int hashCode() {
        int x = srcVertex.hashCode();
        int y = desVertex.hashCode();
        return (x * y + x + y);
    }*/

    @Override
    public boolean equals(Object obj) {
        SimpleEdge x = (SimpleEdge)obj;
        if(x.srcVertex.nodeId.equals(this.srcVertex.nodeId) &&
                x.desVertex.nodeId.equals(this.desVertex.nodeId)) {
            return true;
        }else if(x.srcVertex.nodeId.equals(this.desVertex.nodeId) || x.desVertex.nodeId.equals(this.srcVertex.nodeId)) {
            return true;
        }else {
            return false;
        }

    }

}
