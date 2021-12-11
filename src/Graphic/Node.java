package Graphic;

class Node {

    //public int  x, y;
    public String id, label;
    boolean isOver=false;
    Node(String id_, String label_,boolean isO) {
        id = id_;
        label = label_;
        isOver=isO;
    }

}
