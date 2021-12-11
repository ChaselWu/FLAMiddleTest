import Automata.DFA;

/**
 * <pre>
 *     author : 武连增
 *     e-mail : wulianzeng@bupt.edu.cn
 *     time   : 2021/12/07
 *     desc   :
 *     version:
 * </pre>
 */
public class RegularGrammar {
    public int[] variable;
    public char[] terminal = new char[] {'0','1'};
    public int initStart;
    public int[][] production;
    public RegularGrammar(DFA dfa) {
        this.initStart = dfa.initState;
        this.terminal = dfa.alphabet;
        this.variable = dfa.states;
        this.production = dfa.stateChangeTable;
    }
    @Override
    public String toString() {
        String output = "";
        for(int i=0;i<this.production.length;i++){
            if(i==this.production[i][0]&&i==this.production[i][1]
                    &&this.variable[i]!=2&&this.variable[i]!=3){
                for(int j=0;j<this.production.length;j++){
                    for(int k=0;k<2;k++){
                        if(this.production[j][k]==i){
                            this.production[j][k]=-1;
                        }
                    }
                }
            }
        }
        for(int i=0;i<this.production.length;i++) {
            String part = "";
            char x = 'A';
            if(this.variable[i]==2) {
                if(i==this.production[i][0]&&i!=this.production[i][1]&&this.production[i][1]!=-1) {
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0])
                            + "|" + "0"
                            + "|" + "1" + (char)(x+this.production[i][1]);
                }
                else if(i!=this.production[i][0]&&i==this.production[i][1]&&this.production[i][0]!=-1) {
                    part = (char)(x+i) + " > " + "1" + (char)(x+this.production[i][0])
                            + "|" + "1"
                            + "|" + "0" + (char)(x+this.production[i][1]);
                }
                else if(i==this.production[i][0]&&i==this.production[i][1]){
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0])
                            + "|" + "0"
                            + "|" + "1" + (char)(x+this.production[i][1])
                            + "|" + "1";
                }
                else if(this.production[i][0]==-1&&this.production[i][1]==-1){
                    part = "";
                }
                else if(this.production[i][0]==-1&&this.production[i][1]==i){
                    part = (char)(x+i) + " > " + "1" + (char)(x+this.production[i][1])
                            + "|" + "1";
                }
                else if(this.production[i][0]==i&&this.production[i][1]==-1){
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0])
                            + "|" + "0";
                }
                else if(this.production[i][0]==-1&&this.production[i][1]!=i){
                    part = (char)(x+i) + " > " + "1" + (char)(x+this.production[i][1]);
                }
                else if(this.production[i][0]!=i&&this.production[i][1]==-1){
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0]);
                }
                else {
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0])
                            + "|" + "1" + (char)(x+this.production[i][1]);
                }
            }
            else if(variable[i]==3){
                if(this.production[i][0]==-1&&this.production[i][1]!=-1){
                    part = (char) (x + i) + " > " + "1" + (char) (x + this.production[i][1])
                            + "|" + "1";
                }
                else if(this.production[i][0]!=-1&&this.production[i][1]==-1){
                    part = (char) (x + i) + " > " + "0" + (char) (x + this.production[i][0])
                            + "|" + "0";
                }
                else if(i==this.production[i][0]&&i!=this.production[i][1]){
                    part = (char) (x + i) + " > " + "0" + (char) (x + this.production[i][0])
                            + "|" + "0"
                            + "|" + (char) (x + this.production[i][1]);
                }
                else if(i!=this.production[i][0]&&i==this.production[i][1]) {
                    part = (char) (x + i) + " > " + "0" + (char) (x + this.production[i][0])
                            + "|" + (char) (x + this.production[i][1])
                            + "|" + "1";
                }
            }
            else {
                if(this.production[i][0]==-1&&this.production[i][1]!=-1){
                    part = (char) (x + i) + " > " + "1" + (char) (x + this.production[i][1]);
                }
                else if(this.production[i][0]!=-1&&this.production[i][1]==-1){
                    part = (char) (x + i) + " > " + "0" + (char) (x + this.production[i][0]);
                }
                else if(this.production[i][0]==-1&&this.production[i][1]==-1){
                    part = "";
                }
                else if(variable[(this.production[i][0])]==2&&variable[(this.production[i][1])]==2
                        &&this.production[this.production[i][0]][0]==-1
                        &&this.production[this.production[i][0]][1]==-1){
                    part = (char) (x + i) + " > " + "0" + "|" + "1";
                }
                else if(variable[(this.production[i][0])]!=2&&variable[(this.production[i][1])]==2
                        &&this.production[this.production[i][1]][0]==-1
                        &&this.production[this.production[i][1]][1]==-1){
                    part = (char)(x+i) + " > " + "0" + (char)(x+this.production[i][0])
                            + "|" + "1";
                }
                else if(variable[(this.production[i][0])]==2&&variable[(this.production[i][1])]!=2
                        &&this.production[this.production[i][0]][0]==-1
                        &&this.production[this.production[i][0]][1]==-1){
                    part = (char)(x+i) + " > " + "0"
                            + "|" + "1" + (char)(x+this.production[i][1]);
                }
                else {
                    part = (char) (x + i) + " > " + "0" + (char) (x + this.production[i][0])
                            + "|" + "1" + (char) (x + this.production[i][1]);
                }
            }
            if(part!="") {
                output = output + part + "\n";
            }
        }
        return output;
    }
}
