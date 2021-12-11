import Automata.DFA;
import Automata.NFA;
import Graphic.Graphic;

import java.io.IOException;
import java.util.Scanner;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/11/30
 *     desc   :
 *     version:
 * </pre>
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String regex = scanner.next();
        boolean outRg = scanner.nextInt() == 1;
        NFA nfa = TransformUtil.regexToNfa(regex);
        DFA dfa = TransformUtil.minimize(TransformUtil.nfaToDfa(nfa));
        System.out.println(dfa);
        Graphic.showHTML(dfa);
//        Graphic.generatePNG(args);
//        Graphic.generateUML(dfa);
    }
}
