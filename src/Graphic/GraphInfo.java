package Graphic;

import java.util.ArrayList;

class GraphInfo {
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;

    GraphInfo(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.edges = edges;
        this.nodes = nodes;
    }
}
