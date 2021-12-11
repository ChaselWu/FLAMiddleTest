import Automata.DFA;
import Automata.NFA;

import java.util.*;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/11/30
 *     desc   :
 *     version:
 * </pre>
 */
public class TransformUtil {
    private TransformUtil() {
        throw new AssertionError();
    }

    /**
     * 正则表达式转换为NFA
     *
     * @param regex 待转换正则表达式
     * @return 正则表达式对应NFA
     */
    public static NFA regexToNfa(String regex) {
        if (regex.isEmpty()) {
            System.err.println("表达式为空！");
            return NFA.EMPTY_NFA;
        }
        String post = regexToPostRegex(regex);
        System.out.println(post);
        return postRegexToNfa(post);
    }


    /**
     * @param nfa 待转换NFA
     * @return NFA对应DFA
     */
    public static DFA nfaToDfa(NFA nfa) {
        // 先求出所有状态的e-closure
        ArrayList<HashSet<Integer>> closures = new ArrayList<>();
        for (int i = 0; i < nfa.states.length; ++i) {
            closures.add(getClosure(i, nfa));
        }

        ArrayList<HashSet<Integer>> stateSetList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> stateChangeTable = new ArrayList<>();
        stateSetList.add(closures.get(0));
        for (int i = 0; i < stateSetList.size(); ++i) {
            ArrayList<Integer> line = new ArrayList<>();
            for (int j = 0; j < 2; ++j) {
                HashSet<Integer> set = move(stateSetList.get(i), nfa.alphabet[j], nfa);
                HashSet<Integer> closure = getClosure(set, nfa);
                if (!stateSetList.contains(closure)) {
                    stateSetList.add(closure);
                }
                line.add(stateSetList.indexOf(closure));
            }
            stateChangeTable.add(line);
        }

        // 构造状态转移表
        int[][] table = new int[stateChangeTable.size()][2];
        for (int i = 0; i < table.length; ++i) {
            for (int j = 0; j < 2; ++j) {
                table[i][j] = stateChangeTable.get(i).get(j);
            }
        }
        // 包含NFA终止状态的集合对应的状态为终止态
        boolean[] finalStates = new boolean[table.length];
        for (int i = 0; i < finalStates.length; ++i) {
            finalStates[i] = stateSetList.get(i).contains(nfa.states.length - 1);
        }
        return new DFA(table.length, 0, finalStates, table);
    }

    /**
     * 最小化DFA
     *
     * @param dfa 需要最小化的DFA
     * @return 最小化后的DFA
     */
    public static DFA minimize(DFA dfa) {
        HashSet<Integer> reachableStates = new HashSet<>();
        reachableStates.add(dfa.initState);
        for (int[] line : dfa.stateChangeTable) {
            for (int state : line) {
                reachableStates.add(state);
            }
        }

        ArrayList<Integer> unreachableStates = new ArrayList<>();
        for (int state : dfa.states) {
            if (!reachableStates.contains(state)) {
                unreachableStates.add(state);
            }
        }

        int[] delete = new int[reachableStates.size()];
        for (int i = 0; i < delete.length; ++i) {
            for (Integer integer : unreachableStates) {
                if (i < integer) {
                    break;
                }
                ++delete[i];
            }
        }

        // 修改跳转后状态
        for (int i = 0; i < dfa.stateChangeTable.length; ++i) {
            for (int j = 0; j < 2; ++j) {
                dfa.stateChangeTable[i][j] -= delete[dfa.stateChangeTable[i][j]];
            }
        }

        // 删除不可达状态
        boolean[] reachableFinalStates = new boolean[reachableStates.size()];
        int[][] reachableChangeTable = new int[reachableStates.size()][dfa.alphabet.length];
        for (int i = 0, index = 0; i < dfa.stateChangeTable.length; ++index) {
            if (reachableStates.contains(i)) {
                reachableChangeTable[i] = dfa.stateChangeTable[index];
                reachableFinalStates[i] = dfa.finalStates[index];
                ++i;
            }
        }

        int[] setMap = new int[reachableStates.size()];
        int key = 0;
        HashMap<Integer, ArrayList<Integer>> statesList = new HashMap<>();
        ArrayList<Integer> finalStates = new ArrayList<>();
        ArrayList<Integer> nonFinalStates = new ArrayList<>();
        statesList.put(key++, finalStates);
        statesList.put(key++, nonFinalStates);
        // 划分后的状态集
        ArrayList<ArrayList<Integer>> splitList = new ArrayList<>();

        // 把状态划分为终结状态和非终结状态
        for (int i = 0; i < reachableChangeTable.length; ++i) {
            if (dfa.finalStates[i]) {
                finalStates.add(i);
            } else {
                nonFinalStates.add(i);
            }
        }

        int lastSplitSize = 0;
        // 如果上一次分割的子集数与当前子集数不同，则循环直到子集数不再改变，即为分割完毕
        while (!statesList.isEmpty() || lastSplitSize != splitList.size()) {
            lastSplitSize = splitList.size();
            if (!splitList.isEmpty()) {
                key = 0;
                statesList.clear();
                for (int i = 0; i < splitList.size(); ++i) {
                    statesList.put(i, splitList.get(i));
                    ++key;
                }
                splitList.clear();
            }
            int deleteCnt = 0;
            for (int i = 0; i < statesList.size() + deleteCnt; ++i) {
                // 先更新setMap
                for (Map.Entry<Integer, ArrayList<Integer>> entry : statesList.entrySet()) {
                    for (int s : entry.getValue()) {
                        setMap[s] = entry.getKey();
                    }
                }
                ArrayList<Integer> list = statesList.get(i);
                // 状态集只包含一个状态时说明该状态集已划分完毕
                if (list.size() == 1) {
                    // 原集删掉，加入到分割后状态集
                    splitList.add(statesList.get(i));
                    statesList.remove(i);
                    ++deleteCnt;
                    continue;
                }
                for (int k = 0; k < 2; ++k) {
                    // setMap记录的状态所在的集合的key为key，所有跳转到该集合的状态的集合的key为value
                    // setMap的大小表示list这个集合划分的子集合数量
                    HashMap<Integer, Integer> setMapToKey = new HashMap<>();
                    for (int curState : list) {
                        int nextState = reachableChangeTable[curState][k];
                        // 如果没有新集合则构造新集合，否则在查到的集合中插入状态
                        if (setMapToKey.get(setMap[nextState]) == null) {
                            setMapToKey.put(setMap[nextState], key);
                            ArrayList<Integer> newList = new ArrayList<>();
                            statesList.put(key++, newList);
                            newList.add(curState);
                        } else {
                            statesList.get(setMapToKey.get(setMap[nextState])).add(curState);
                        }
                    }
                    // 划分成功则不再划分，删除原集合
                    if (setMapToKey.size() > 1) {
                        statesList.remove(i);
                        ++deleteCnt;
                        break;
                    } else if (setMapToKey.size() == 1) {
                        // 若失败则删除生成的多余集合
                        statesList.remove(--key);
                    }
                    // 第二次划分失败，删除原集合，加到不可分集合中
                    if (setMapToKey.size() == 1 && k == 1) {
                        splitList.add(statesList.get(i));
                        statesList.remove(i);
                        ++deleteCnt;
                    }
                }
            }
        }

        // 把等价状态合并一下
        // 多余状态集，Key是多余的状态，Value是合并后的状态
        HashMap<Integer, Integer> extraStates = new HashMap<>();
        for (ArrayList<Integer> arr : splitList) {
            if (arr.size() > 1) {
                Collections.sort(arr);
                for (int i = 1; i < arr.size(); ++i) {
                    extraStates.put(arr.get(i), arr.get(0));
                }
            }
        }
        int[] deleteCounts = new int[reachableStates.size()];
        for (int i = 0; i < deleteCounts.length; ++i) {
            for (Integer integer : extraStates.keySet()) {
                if (i < integer) {
                    break;
                }
                ++deleteCounts[i];
            }
        }
        // 修改跳转后状态
        for (int i = 0; i < reachableChangeTable.length; ++i) {
            for (int j = 0; j < 2; ++j) {
                Integer val = extraStates.get(reachableChangeTable[i][j]);
                if (val != null) {
                    reachableChangeTable[i][j] = val;
                }
                reachableChangeTable[i][j] -= deleteCounts[reachableChangeTable[i][j]];
            }
        }

        int[][] finalStateChangeTable = new int[reachableChangeTable.length - extraStates.size()][2];
        boolean[] terminalStates = new boolean[finalStateChangeTable.length];
        for (int i = 0, index = 0; i < finalStateChangeTable.length; ++index) {
            if (!extraStates.containsKey(index)) {
                finalStateChangeTable[i] = reachableChangeTable[index];
                terminalStates[i] = reachableFinalStates[index];
                ++i;
            }
        }
        return new DFA(finalStateChangeTable.length, 0, terminalStates, finalStateChangeTable);
    }

    /**
     * 后缀正则表达式转换成NFA
     *
     * @param postRegex 后缀表达式
     * @return 转化后的NFA
     */
    private static NFA postRegexToNfa(String postRegex) {
        Stack<NFA.Unit> stack = new Stack<>();
        NFA.Unit lhs;
        NFA.Unit rhs;
        for (int i = 0; i < postRegex.length(); ++i) {
            char ch = postRegex.charAt(i);
            if (ch == '0' || ch == '1') {
                NFA.Unit unit = new NFA.Unit(ch);
                stack.push(unit);
            } else {
                rhs = stack.pop();
                switch (ch) {
                    case '&':
                        lhs = stack.pop();
                        lhs.concat(rhs);
                        stack.push(lhs);
                        break;
                    case '+':
                        lhs = stack.pop();
                        lhs.or(rhs);
                        stack.push(lhs);
                        break;
                    case '*':
                        rhs.star();
                        stack.push(rhs);
                        break;
                    default:
                        System.err.println("Error operator!!");
                        return NFA.EMPTY_NFA;
                }
            }
        }
        return stack.peek().create();
    }

    /**
     * 正则表达式中缀转后缀
     *
     * @param regex 中缀正则表达式
     * @return 后缀正则表达式
     */
    private static String regexToPostRegex(String regex) {
        // 定义优先级
        HashMap<Character, Integer> priority = new HashMap<>();
        priority.put('(', 0);
        priority.put('*', 3);
        priority.put('&', 2); // 连接操作
        priority.put('+', 1); // 或操作

        String concatRegex = addConcat(regex);
        System.out.println("Concat: " + concatRegex);
        // 中缀转后缀
        Stack<Character> operands = new Stack<>();
        StringBuilder postRegex = new StringBuilder();
        for (int i = 0; i < concatRegex.length(); ++i) {
            char c = concatRegex.charAt(i);
            if (c == '0' || c == '1') {
                postRegex.append(c);
            } else {
                if (c == '(') {
                    operands.push(c);
                } else if (c == ')') {
                    while (!operands.empty() && !operands.peek().equals('(')) {
                        postRegex.append(operands.pop());
                    }
                    operands.pop();
                } else {
                    while (!operands.empty() && priority.get(c) <= priority.get(operands.peek())) {
                        postRegex.append(operands.pop());
                    }
                    operands.push(c);
                }
            }
        }
        while (!operands.empty()) {
            postRegex.append(operands.pop());
        }

        return postRegex.toString();
    }

    /**
     * 为正则表达式添加连接符
     *
     * @param regex 省略连接符的正则表达式
     * @return 含连接符的正则表达式
     */
    private static String addConcat(String regex) {
        StringBuilder concatRegex = new StringBuilder(regex);
        // 给正则表达式添加连接符
        for (int i = 0; i < concatRegex.length() - 1; ++i) {
            if (concatRegex.charAt(i) == '(' || concatRegex.charAt(i) == '+') {
                continue;
            }
            char c = concatRegex.charAt(i + 1);
            if (c == '0' || c == '1' || c == '(') {
                concatRegex.insert(++i, '&');
            }
        }
        return concatRegex.toString();
    }

    /**
     * 获取NFA对应状态的e-closure
     *
     * @param state 状态
     * @param nfa 获取e-closure的nfa
     * @return e-closure
     */
    private static HashSet<Integer> getClosure(int state, NFA nfa) {
        HashSet<Integer> set = new HashSet<>();
        set.add(state);
        return getClosure(set, nfa);
    }

    /**
     * 获取NFA对应状态集的e-closure
     *
     * @param stateSet 状态集
     * @param nfa 获取e-closure的nfa
     * @return e-closure
     */
    private static HashSet<Integer> getClosure(HashSet<Integer> stateSet, NFA nfa) {
        HashSet<Integer> eClosure = new HashSet<>();
        for (int state : stateSet) {
            eClosure.add(state);
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(state);
            boolean[] visited = new boolean[nfa.states.length];
            while (!queue.isEmpty()) {
                int s = queue.poll();
                visited[s] = true;
                LinkedList<NFA.Unit.TableBody> table = nfa.adjacentTable.get(s);
                table.forEach(tableBody -> {
                    if (tableBody.letter == NFA.Unit.TableBody.EPSILON) {
                        int index = tableBody.index;
                        if (visited[index]) {
                            return;
                        }
                        queue.offer(index);
                        eClosure.add(index);
                    }
                });
            }
        }
        return eClosure;
    }

    /**
     * @param stateSet 状态集
     * @param letter 转移字母
     * @param nfa nfa
     * @return 状态集move一个字母后的集合
     */
    private static HashSet<Integer> move(HashSet<Integer> stateSet, char letter, NFA nfa) {
        HashSet<Integer> set = new HashSet<>();
        stateSet.forEach(integer -> {
            LinkedList<NFA.Unit.TableBody> list = nfa.adjacentTable.get(integer);
            list.forEach(tableBody -> {
                if (tableBody.letter == letter) {
                    set.add(tableBody.index);
                }
            });
        });
        return set;
    }
}
