
import java.io.*;

/**
 * Created by et on 10/9/16.
 */
public class homework {
    
    public static void main(String[] args) throws IOException {
        
        String input = "input.txt";
        BufferedReader in = new BufferedReader(new FileReader(input));
        /**
         * input format:
         * <N>  board width an height
         * <MODE> "MINIMAX" or "ALPHABETA"
         * <YOUPLAY> "X" or "O"
         * <DEPTH>
         * <...CELLVALUES...>
         * <...BOARDSTATE...>
         */
        int width = Integer.parseInt(in.readLine()); // 0<boardWidth<=26
        String mode = in.readLine();
        String player = in.readLine();
        int depth = Integer.parseInt(in.readLine());
        
        int[][] values = new int[width][width];
        for (int i = 0; i < width; i++) {
            String cell = in.readLine();
            String[] strs = cell.split(" ");
            for (int j = 0; j < width; j++) {
                values[i][j] = Integer.parseInt(strs[j]);
            }
        }
        
        String[][] state = new String[width][width];
        for (int i = 0; i < width; i++) {
            String boardState = in.readLine();
            for (int j = 0; j < width; j++) {
                state[i][j] = String.valueOf(boardState.charAt(j));
            }
        }
        GameBoard gb = new GameBoard(width, mode, player, depth, values, state);
        //gb.testBoard();
        gb.write();
        
    }
}

class GameBoard {
    private int width;
    private String mode;
    private String player;
    private int depth;
    private int[][] values;
    private String[][] state;
    private int[] act;
    
    public GameBoard(int width, String mode, String player, int depth, int[][] values, String[][] state) {
        this.width = width;
        this.mode = mode;
        this.player = player;
        this.depth = depth;
        this.values = values;
        this.state = state;
        act = new int[3];
    }
    
    //write output
    public void write() {
        String[] res = new String[3];
        if(mode.equals("MINIMAX")) {
            res = minimax();
        }
        else{
            res = abSearch();
            //System.out.println(res[0]+".."+res[1]+".."+res[2]);
            
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
            bw.write(res[1]);
            bw.write(res[0]);
            bw.write(" ");
            bw.write(res[2]);
            bw.newLine();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    bw.write(state[i][j]);
                }
                if(i!=width-1) {
                    bw.newLine();
                }
            }
            bw.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    //test whether input is correct
    public void testBoard() {
        System.out.println("Board width is : " + width);
        System.out.println("Mode is : " + mode);
        System.out.println("player : " + player);
        System.out.println("depth : " + depth);
        System.out.println("Cell Values : ");
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                System.out.print(values[i][j] + " ");
            }
            System.out.println();
        }
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                System.out.print(state[i][j] + " ");
            }
            System.out.println();
        }
        
    }
    public void doRaid(int i, int j,String[][] sta,String player){
        String temp =null;
        
        if(player.equals("X")){
            temp = "O";
        }
        if(player.equals("O")){
            temp = "X";
        }
        //System.out.println(i+" "+j);
        boolean b1 = i < width - 1 && state[i + 1][j].equals(temp);
        boolean b2 = i > 0 && state[i - 1][j].equals(temp);
        boolean b3 = j < width - 1 && state[i][j + 1].equals(temp);
        boolean b4 = j > 0 && state[i][j - 1].equals(temp);
        
        
        if (b1) state[i + 1][j] = player;
        
        if (b2) state[i - 1][j] = player;
        
        if (b3) state[i][j + 1] = player;
        
        if (b4) state[i][j - 1] = player;
        
        
    }
    public String[] abSearch(){
        String[] result = new String[3];// res[0]=coordinate, res[1] = actions;
        int res = 0;
        
        if (player.equals("X")) {
            //System.out.println("depth::: "+depth);
            res = abMaxValue(depth, state,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
            //System.out.println("RES:" +res);
            int[] coord = act;
            //            for (int m = 0; m < width; m++) {
            //                for (int n = 0; n < width; n++) {
            //                    System.out.print(state[m][n]);
            //                }
            //                System.out.println();
            //            }
            
            result[0] = String.valueOf(coord[0] + 1);
            result[1] = Character.toString((char) (coord[1] + 65));
            state[coord[0]][coord[1]] = "X";
            result[2] = (coord[2]==0)?"Stake":"Raid";
        } else {
            res = abMinValue(depth, state,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
            
            
            int[] coord = act;
            
            result[0] = String.valueOf(coord[0] + 1);
            result[1] = Character.toString((char) (coord[1] + 65));
            state[coord[0]][coord[1]] = "O";
            //result[2] = stakeOrRaid(coord[0],coord[1],state,"O");
            result[2] = (coord[2]==0)?"Stake":"Raid";
        }
        
        return result;
    }
    public int abMaxValue(int depth,String[][] sta, int alpha,int beta,boolean a){
        if (terminal(sta)) {
            return getScore(sta);
        }
        int score = Integer.MIN_VALUE;
        //base case
        if (depth == 1) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")) {
                        int v = getS(i, j, "X", sta);
                        if (a&&v > score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 0;
                        }
                        score = Math.max(score, v);
                        if(score>=beta) return score;
                        alpha = Math.max(alpha,score);
                        
                    }
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")&&!isStake(i,j,"X",sta)) {
                        int v = getR(i, j, "X", sta);
                        if (a&&v > score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 1;
                            
                        }
                        score = Math.max(score, v);
                        
                        if(score>=beta) return score;
                        alpha = Math.max(alpha,score);
                    }
                }
            }
            if(a&&act[2]==1) {
                doRaid(act[0], act[1], state, "X");
            }
            //System.out.println(score);
            return score;
        }
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                
                if (sta[i][j].equals(".")) {
                    
                    
                    sta[i][j] = "X";
                    int v = abMinValue(depth - 1, sta,alpha,beta,false);
                    //                    if(depth==4){
                    //                        System.out.println("("+i+","+j+") "+v);
                    //                    }
                    if (a&&v > score) {
                        ;
                        act[0] = i;
                        act[1] = j;
                        act[2] = 0;
                    }
                    score = Math.max(score, v);
                    sta[i][j] = ".";
                    
                    if(score>=beta) return score;
                    alpha = Math.max(alpha,score);
                }
            }
        }
        for(int i =0;i<width;i++){
            for(int j = 0;j<width;j++){
                if(state[i][j].equals(".")&&!isStake(i,j,"X",sta)) {
                    sta[i][j] = "X";
                    boolean b1 = i < width - 1 && sta[i + 1][j].equals("O");
                    if (b1) sta[i + 1][j] = "X";
                    boolean b2 = i > 0 && sta[i - 1][j].equals("O");
                    if (b2) sta[i - 1][j] = "X";
                    boolean b3 = j < width - 1 && sta[i][j + 1].equals("O");
                    if (b3) sta[i][j + 1] = "X";
                    boolean b4 = j > 0 && sta[i][j - 1].equals("O");
                    if (b4) sta[i][j - 1] = "X";
                    int v = abMinValue(depth - 1, sta,alpha,beta, false);
                    //                    if(depth==4){
                    //                        System.out.println("("+i+","+j+") "+v);
                    //                    }
                    if (a && v > score) {
                        
                        act[0] = i;
                        act[1] = j;
                        act[2] = 1;
                        //doRaid(i,j,state,"X");
                    }
                    
                    score = Math.max(score, v);
                    if (b1) sta[i + 1][j] = "O";
                    if (b2) sta[i - 1][j] = "O";
                    if (b3) sta[i][j + 1] = "O";
                    if (b4) sta[i][j - 1] = "O";
                    sta[i][j] = ".";
                    if(score>=beta) return score;
                    alpha = Math.max(alpha,score);
                }
            }
        }
        if(a&&act[2] ==1) {
            doRaid(act[0], act[1], state, "X");
        }
        
        
        
        return score;
    }
    public int abMinValue(int depth,String[][] sta, int alpha,int beta, boolean a){
        if (terminal(sta)) {
            return getScore(sta);
        }
        int score = Integer.MAX_VALUE;
        if (depth == 1) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")) {
                        int v = getS(i, j, "O", sta);
                        if (a&&v < score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 0;
                            
                        }
                        score = Math.min(score, v);
                        if(score<=alpha) return score;
                        beta = Math.min(beta,score);
                        
                        
                    }
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")&&!isStake(i,j,"O",sta)) {
                        int v = getR(i, j, "O", sta);
                        if (a&&v < score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 1;
                            //doRaid(i,j,state,"O");
                        }
                        //System.out.println(i+" , "+j+"Score: "+v);
                        score = Math.min(score, v);
                        if(score<=alpha) return score;
                        beta = Math.min(beta,score);
                        
                        
                    }
                }
            }
            if(a&&act[2]==1) {
                doRaid(act[0], act[1], state, "O");
            }
            return score;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                
                if (sta[i][j].equals(".")) {
                    
                    
                    sta[i][j] = "O";
                    int v = abMaxValue(depth - 1, sta,alpha,beta,false);
                    if (a&&v < score) {
                        act[0] = i;
                        act[1] = j;
                        act[2] = 0;
                    }
                    score = Math.min(score, v);
                    sta[i][j] = ".";
                    if(score<=alpha) return score;
                    beta = Math.min(beta,score);
                    
                    
                    
                }
            }
        }
        for(int i =0;i<width;i++){
            for(int j=0;j<width;j++){
                if(sta[i][j].equals(".")&&!isStake(i,j,"O",sta)){
                    sta[i][j] = "O";
                    boolean b1 = i < width - 1 && sta[i + 1][j].equals("X");
                    if (b1) sta[i + 1][j] = "O";
                    boolean b2 = i > 0 && sta[i - 1][j].equals("X");
                    if (b2) sta[i - 1][j] = "O";
                    boolean b3 = j < width - 1 && sta[i][j + 1].equals("X");
                    if (b3) sta[i][j + 1] = "O";
                    boolean b4 = j > 0 && sta[i][j - 1].equals("X");
                    if (b4) sta[i][j - 1] = "O";
                    int v = abMaxValue(depth - 1, sta,alpha,beta,false);
                    if (a&&v < score) {
                        act[0] = i;
                        act[1] = j;
                        act[2] = 1;
                        //doRaid(i,j,state,"O");
                    }
                    score = Math.min(score, v);
                    
                    if (b1) sta[i + 1][j] = "X";
                    if (b2) sta[i - 1][j] = "X";
                    if (b3) sta[i][j + 1] = "X";
                    if (b4) sta[i][j - 1] = "X";
                    sta[i][j] = ".";
                }
            }
        }
        if(a&&act[2]==1) {
            doRaid(act[0], act[1], state, "O");
        }
        return score;
    }
    //minimax algorithm returns an action;
    public String[] minimax() {
        String[] result = new String[3];// res[0]=coordinate, res[1] = actions;
        int res = 0;
        //System.out.println("ORI: "+getScore(state));
        if (player.equals("X")) {
            //System.out.println("depth::: "+depth);
            res = maxValue(depth, state,true);
            int[] coord = act;
            //            for (int m = 0; m < width; m++) {
            //                for (int n = 0; n < width; n++) {
            //                    System.out.print(state[m][n]);
            //                }
            //                System.out.println();
            //            }
            
            result[0] = String.valueOf(coord[0] + 1);
            result[1] = Character.toString((char) (coord[1] + 65));
            state[coord[0]][coord[1]] = "X";
            result[2] = (coord[2]==0)?"Stake":"Raid";
        } else {
            res = minValue(depth, state,true);
            
            
            int[] coord = act;
            
            result[0] = String.valueOf(coord[0] + 1);
            result[1] = Character.toString((char) (coord[1] + 65));
            state[coord[0]][coord[1]] = "O";
            result[2] = (coord[2]==0)?"Stake":"Raid";
        }
        
        return result;
    }
    
    //maxValue for X, X play
    public int maxValue(int depth, String[][] sta,boolean a) {
        if (terminal(sta)) {
            return getScore(sta);
        }
        int score = Integer.MIN_VALUE;
        //base case
        if (depth == 1) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")) {
                        int v = getS(i, j, "X", sta);
                        if (a&&v > score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 0;
                        }
                        score = Math.max(score, v);
                        
                        
                    }
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")&&!isStake(i,j,"X",sta)) {
                        
                        int v = getR(i, j, "X", sta);
                        //System.out.println(Character.toString((char) (j + 65))+","+(i+1)+" Score :"+v);
                        if (a&&v > score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 1;
                        }
                        score = Math.max(score, v);
                        
                        
                    }
                }
            }
            if(a&&act[2]==1) {
                doRaid(act[0], act[1], state, "X");
            }
            
            //System.out.println(score);
            return score;
        }
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                
                if (sta[i][j].equals(".")) {
                    
                    
                    sta[i][j] = "X";
                    int v = minValue(depth - 1, sta,false);
                    if (a&&v > score) {
                        
                        act[0] = i;
                        act[1] = j;
                        act[2] = 0;
                    }
                    score = Math.max(score, v);
                    sta[i][j] = ".";
                }
            }
        }
        for(int i =0;i<width;i++){
            for(int j = 0;j<width;j++){
                if(state[i][j].equals(".")&&!isStake(i,j,"X",sta)) {
                    sta[i][j] = "X";
                    boolean b1 = i < width - 1 && sta[i + 1][j].equals("O");
                    if (b1) sta[i + 1][j] = "X";
                    boolean b2 = i > 0 && sta[i - 1][j].equals("O");
                    if (b2) sta[i - 1][j] = "X";
                    boolean b3 = j < width - 1 && sta[i][j + 1].equals("O");
                    if (b3) sta[i][j + 1] = "X";
                    boolean b4 = j > 0 && sta[i][j - 1].equals("O");
                    if (b4) sta[i][j - 1] = "X";
                    int v = minValue(depth - 1, sta, false);
                    if (a && v > score) {
                        
                        act[0] = i;
                        act[1] = j;
                        act[2] = 1;
                    }
                    
                    score = Math.max(score, v);
                    if (b1) sta[i + 1][j] = "O";
                    if (b2) sta[i - 1][j] = "O";
                    if (b3) sta[i][j + 1] = "O";
                    if (b4) sta[i][j - 1] = "O";
                    sta[i][j] = ".";
                }
            }
        }
        
        if(a&&act[2]==1) {
            doRaid(act[0], act[1], state, "X");
        }
        return score;
    }
    
    //minValue for X, O play
    public int minValue(int depth, String[][] sta,boolean a) {
        if (terminal(sta)) {
            return getScore(sta);
        }
        int score = Integer.MAX_VALUE;
        if (depth == 1) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")) {
                        int v = getS(i, j, "O", sta);
                        if (a&&v < score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 0;
                        }
                        score = Math.min(score, v);
                        
                    }
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (sta[i][j].equals(".")&&!isStake(i,j,"O",sta)) {
                        int v = getR(i, j, "O", sta);
                        if (a&&v < score) {
                            act[0] = i;
                            act[1] = j;
                            act[2] = 1;
                            //doRaid(i,j,state,"O");
                        }
                        score = Math.min(score, v);
                        
                    }
                }
            }
            if(a&&act[2]==1) {
                doRaid(act[0], act[1], state, "O");
            }
            return score;
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                
                if (sta[i][j].equals(".")) {
                    
                    
                    sta[i][j] = "O";
                    int v = maxValue(depth - 1, sta,false);
                    if (a&&v < score) {
                        act[0] = i;
                        act[1] = j;
                        act[2] = 0;
                    }
                    score = Math.min(score, v);
                    sta[i][j] = ".";
                    
                    
                    
                    
                }
            }
        }
        for(int i =0;i<width;i++){
            for(int j=0;j<width;j++){
                if(sta[i][j].equals(".")&&!isStake(i,j,"O",sta)){
                    sta[i][j] = "O";
                    boolean b1 = i < width - 1 && sta[i + 1][j].equals("X");
                    if (b1) sta[i + 1][j] = "O";
                    boolean b2 = i > 0 && sta[i - 1][j].equals("X");
                    if (b2) sta[i - 1][j] = "O";
                    boolean b3 = j < width - 1 && sta[i][j + 1].equals("X");
                    if (b3) sta[i][j + 1] = "O";
                    boolean b4 = j > 0 && sta[i][j - 1].equals("X");
                    if (b4) sta[i][j - 1] = "O";
                    int v = maxValue(depth - 1, sta,false);
                    if (a&&v < score) {
                        act[0] = i;
                        act[1] = j;
                        act[2] = 1;
                        //doRaid(i,j,state,"O");
                    }
                    score = Math.min(score, v);
                    
                    if (b1) sta[i + 1][j] = "X";
                    if (b2) sta[i - 1][j] = "X";
                    if (b3) sta[i][j + 1] = "X";
                    if (b4) sta[i][j - 1] = "X";
                    sta[i][j] = ".";
                }
            }
        }
        if(a&&act[2]==1) {
            doRaid(act[0], act[1], state, "O");
        }
        return score;
    }
    
    public boolean terminal(String[][] sta) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (sta[i][j].equals(".")) return false;
            }
        }
        return true;
    }
    
    
    public int getScore(String[][] sta) {
        int res = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (sta[i][j].equals("X")) res += values[i][j];
                if (sta[i][j].equals("O")) res -= values[i][j];
            }
        }
        return res;
    }
    
    public int getS(int i, int j, String player, String[][] sta){
        int scoreOriginal = getScore(sta);
        if (player.equals("X")) return scoreOriginal + values[i][j];
        if (player.equals("O")) return scoreOriginal - values[i][j];
        return 0;
    }
    public int getR(int i, int j, String player, String[][] sta){
        int value = getScore(sta);
        if (player.equals("X")) {
            value+=values[i][j];
            if (i < width - 1 && sta[i + 1][j].equals("O")) value += 2 * values[i + 1][j];
            if (i > 0 && sta[i - 1][j].equals("O")) value += 2 * values[i - 1][j];
            if (j < width - 1 && sta[i][j + 1].equals("O")) value += 2 * values[i][j + 1];
            if (j > 0 && sta[i][j - 1].equals("O")) value += 2 * values[i][j - 1];
            return value;
        } else {
            value-=values[i][j];
            if (i < width - 1 && sta[i + 1][j].equals("X")) value -= 2 * values[i + 1][j];
            if (i > 0 && sta[i - 1][j].equals("X")) value -= 2 * values[i - 1][j];
            if (j < width - 1 && sta[i][j + 1].equals("X")) value -= 2 * values[i][j + 1];
            if (j > 0 && sta[i][j - 1].equals("X")) value -= 2 * values[i][j - 1];
            return value;
        }
    }
    
    public boolean isStake(int i, int j, String player, String[][] sta) {
        if (i < width - 1 && sta[i + 1][j].equals(player)) return false;
        if (i > 0 && sta[i - 1][j].equals(player)) return false;
        if (j < width - 1 && sta[i][j + 1].equals(player)) return false;
        if (j > 0 && sta[i][j - 1].equals(player)) return false;
        return true;
    }
    
    
}
