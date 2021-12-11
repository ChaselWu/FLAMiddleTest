package Automata;


import net.sourceforge.plantuml.Run;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Graphic {
    public static void generateUML(DFA dfa) throws IOException {
        File file = new File("src/Automata/status_uml.txt");
        FileWriter fw = new FileWriter(file);
        fw.write("@startuml\nagent dfa\n");
        for (var i : dfa.states) {
            fw.write("interface q" + i + "\n");
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
}
