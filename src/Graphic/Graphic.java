package Graphic;

import Automata.DFA;
import com.google.gson.Gson;
import net.sourceforge.plantuml.Run;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <pre>
 *     author : 伍昶旭
 *     e-mail : changxvwu@bupt.edu.cn
 *     time   : 2021/12/8
 *     desc   :
 *     version:
 * </pre>
 */
public class Graphic {
    public static void generateUML(DFA dfa) throws IOException {
        File file = new File("src/Automata/status_uml.txt");
        FileWriter fw = new FileWriter(file);
        fw.write("@startuml\n");
        for (int i = 0; i < dfa.states.length; i++) {
            fw.write("circle q" + i + "\n");
        }
        for (int i = 0; i < dfa.stateChangeTable.length; i++) {
            for (int j = 0; j < dfa.stateChangeTable[0].length; j++) {
                fw.write("q" + i + "->" + "q" + dfa.stateChangeTable[i][j] + ":" + j + "\n");
            }
        }
        fw.write("@enduml");
        fw.close();
    }

    public static void generatePNG(String[] args) throws IOException, InterruptedException {
        Run.main(args);
    }


    public static void showHTML(DFA dfa) throws IOException {
        //generateJSON
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < dfa.states.length; i++) {
            nodes.add(new Node(""+i, "q" + i,dfa.finalStates[i]));
        }
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < dfa.stateChangeTable.length; i++) {
            for (int j = 0; j < dfa.stateChangeTable[0].length; j++) {
                edges.add(new Edge(""+i, ""+dfa.stateChangeTable[i][j], ""+j,
                        j==1&&dfa.stateChangeTable[i][0]==dfa.stateChangeTable[i][1]?true:false));
            }
        }

        GraphInfo gi=new GraphInfo(nodes,edges);
        Gson gson = new Gson();
        String dfaJson = gson.toJson(gi);
        System.out.print(dfaJson);

        File file = new File("src/Graphic/Index/dfa.json");
        FileWriter fw = new FileWriter(file);
        fw.write(dfaJson);
        fw.close();

//        Server s=new Server();
//        new Thread(() -> {
//            try {
//                s.startServer(dfaJson);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("1111111111");
//        }).start();


        File htmlFile = new File("src/Graphic/Index/index.html");
        Desktop.getDesktop().browse(htmlFile.toURI());
    }
}

