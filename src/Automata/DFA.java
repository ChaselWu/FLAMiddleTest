package Automata;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/11/30
 *     desc   :
 *     version:
 * </pre>
 */
public class DFA extends FiniteAutomata {
    /**
     * 状态转移表<br>
     * 行为状态，列为字母，值为跳转到的状态
     */
    public int[][] stateChangeTable;

    public DFA(int stateNum, int initState, boolean[] finalStates, int[][] stateChangeTable) {
        super(stateNum, initState, finalStates);
        this.stateChangeTable = stateChangeTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t").append("0\t").append("1\n");
        for (int i = 0; i < stateChangeTable.length; ++i) {
            if (states[i] == 1) {
                builder.append("#");
            } else if (states[i] == 2) {
                builder.append("*");
            } else if (states[i] == 3) {
                builder.append("#*");
            }
            builder.append("q").append(i).append("\t").
                    append("q").append(stateChangeTable[i][0]).append("\t").
                    append("q").append(stateChangeTable[i][1]).append("\n");
        }
        return builder.toString();
    }
}
