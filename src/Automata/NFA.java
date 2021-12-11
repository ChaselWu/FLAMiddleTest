package Automata;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/11/30
 *     desc   :
 *     version:
 * </pre>
 */
public class NFA extends FiniteAutomata {
    // 采用邻接表存NFA图的边
    public final ArrayList<LinkedList<Unit.TableBody>> adjacentTable;
    // 空NFA，只含一个状态且无状态转移
    public final static NFA EMPTY_NFA = new NFA(1, 0, new boolean[]{true}, new ArrayList<>());

    public NFA(int states, int initState, boolean[] finalStates, ArrayList<LinkedList<Unit.TableBody>> adjacentTable) {
        super(states, initState, finalStates);
        this.adjacentTable = adjacentTable;
    }

    public static class Unit {
        ArrayList<LinkedList<TableBody>> adjTable;

        public Unit(char ch) {
            readState(ch);
        }

        public void readState(char ch) {
            adjTable = new ArrayList<>();
            LinkedList<TableBody> line = new LinkedList<>();
            line.add(new TableBody(1, ch));
            adjTable.add(line);
            line = new LinkedList<>();
            adjTable.add(line);
        }

        /**
         * 连接操作
         *
         * @param rhs 另一个Unit
         */
        public void concat(NFA.Unit rhs) {
            int lhsSize = this.adjTable.size();
            // 把邻接表组合在一起
            rhs.adjTable.forEach(list -> {
                // rhs邻接表元素的索引需要增加lhsSize
                list.forEach(tableBody -> tableBody.index += lhsSize);
                adjTable.add(list);
            });
            // lhs终止状态和rhs开始状态之间添加空转移
            adjTable.get(lhsSize - 1).add(new TableBody(lhsSize, 'e'));
        }

        /**
         * 或操作
         *
         * @param rhs 另一个Unit
         */
        public void or(NFA.Unit rhs) {
            int lhsSize = this.adjTable.size();
            int rhsSize = rhs.adjTable.size();
            int newSize = lhsSize + rhsSize + 2;
            // 修改两边邻接表索引的值
            this.adjTable.forEach(list -> list.forEach(tableBody -> ++tableBody.index));
            rhs.adjTable.forEach(list -> list.forEach(tableBody -> tableBody.index += (lhsSize + 1)));

            // 两边邻接表添加终止状态对or后的终止状态的空转移
            this.adjTable.get(lhsSize - 1).add(new TableBody(newSize - 1, 'e'));
            rhs.adjTable.get(rhsSize - 1).add(new TableBody(newSize - 1, 'e'));

            // 添加or运算初始状态
            LinkedList<TableBody> initStateList = new LinkedList<>();
            initStateList.add(new TableBody(1, 'e'));
            initStateList.add(new TableBody(lhsSize + 1, 'e'));

            // 构建新邻接表
            ArrayList<LinkedList<TableBody>> newAdjTable = new ArrayList<>(newSize);
            newAdjTable.add(initStateList);
            newAdjTable.addAll(this.adjTable);
            newAdjTable.addAll(rhs.adjTable);
            newAdjTable.add(new LinkedList<>());

            // 新的邻接表赋值给lhs
            this.adjTable = newAdjTable;
        }

        /**
         * 闭包运算
         */
        public void star() {
            int thisSize = this.adjTable.size();
            int newSize = thisSize + 2;
            // 邻接表索引+1
            this.adjTable.forEach(list -> list.forEach(tableBody -> ++tableBody.index));

            // 添加原终止状态对初始状态的空转移和对新终止状态的空转移
            this.adjTable.get(thisSize - 1).add(new TableBody(1, 'e'));
            this.adjTable.get(thisSize - 1).add(new TableBody(newSize - 1, 'e'));

            LinkedList<TableBody> initStateList = new LinkedList<>();
            initStateList.add(new TableBody(1, 'e'));
            initStateList.add(new TableBody(newSize - 1, 'e'));

            // 构建新邻接表
            ArrayList<LinkedList<TableBody>> newAdjTable = new ArrayList<>();
            newAdjTable.add(initStateList);
            newAdjTable.addAll(this.adjTable);
            newAdjTable.add(new LinkedList<>());

            this.adjTable = newAdjTable;
        }

        public NFA create() {
            int size = adjTable.size();
            boolean[] finalStates = new boolean[size];
            finalStates[size - 1] = true;
            return new NFA(size, 0, finalStates, adjTable);
        }

        public static class TableBody {
            public int index;
            public char letter;
            public static char EPSILON = 'e';

            /**
             * @param index  跳转到的状态
             * @param letter 跳转到状态所需要的字母
             */
            TableBody(int index, char letter) {
                this.index = index;
                this.letter = letter;
            }
        }
    }
}
