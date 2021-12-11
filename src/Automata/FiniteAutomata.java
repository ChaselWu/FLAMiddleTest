package Automata;

import java.util.Objects;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/11/30
 *     desc   :
 *     version:
 * </pre>
 */
public abstract class FiniteAutomata {
    /**
     * 状态集<br>
     * 长度——状态个数<br>
     * 索引——状态<br>
     * 存入数据——0表示普通状态，1表示起始状态，2表示终止状态
     */
    public final int[] states;
    public final char[] alphabet = {'0', '1'}; // 字母表
    public final int initState; // 初始状态
    public final boolean[] finalStates; // 终止状态集

    public FiniteAutomata(int stateNum, int initState, boolean[] finalStates) {
        this.states = new int[stateNum];
        this.initState = initState;
        this.finalStates = Objects.requireNonNull(finalStates);
        states[initState] = 1;
        for (int i = 0; i < states.length; ++i) {
            if (finalStates[i]) {
                states[i] += 2;
            }
        }
    }
}
