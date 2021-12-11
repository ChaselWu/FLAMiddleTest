package Graphic;

class Edge {
    public String source, target, label;
    public boolean isRepeated=false;
    Edge(String s, String t, String l,boolean isR_) {
        source = s;
        target = t;
        label = l;
        isRepeated=isR_;
    }
}
