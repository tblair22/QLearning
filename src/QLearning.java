/*
Trent Blair
4/18/22
This project is the Q learning part of the lab
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class QLearning {
    private Maze m;

    private final double B = 0.9;
    private final double C = 0.1;



    private final int mazeHeight = 5;

    private final int mazeWidth = 5;
    private final int stateCount = mazeHeight * mazeWidth;

    private final int reward = 100;
    private final int penalty = -10;


    private int[][] R;
    private double[][] Q;
    private char[][] maze;


    public static void main(String args[]) {
        QLearning ql = new QLearning();

        ql.init();
        ql.calculateQ();
        ql.printQ();
        ql.Policy();
    }
//initiates
    public void init() {

        m = new Maze();
        m.buildMaze();

        File file = new File("src/Maze.txt");

        R = new int[stateCount][stateCount];
        Q = new double[stateCount][stateCount];
        maze = new char[mazeHeight][mazeWidth];


        try (FileInputStream fis = new FileInputStream(file)) {

            int i = 0;
            int j = 0;

            int content;

            while ((content = fis.read()) != -1) {
                char c = (char) content;
                if (c != '1' && c != 'F' && c != 'V') {
                    continue;
                }
                maze[i][j] = c;
                j++;
                if (j == mazeWidth) {
                    j = 0;
                    i++;
                }
            }
            for (int k = 0; k < stateCount; k++) {
                i = k / mazeWidth;
                j = k - i * mazeWidth;


                for (int s = 0; s < stateCount; s++) {
                    R[k][s] = -1;

                }


                if (maze[i][j] != 'F') {


                    int goLeft = j - 1;
                    if (goLeft >= 0) {
                        int target = i * mazeWidth + goLeft;
                        if (maze[i][goLeft] == '1') {
                            R[k][target] = 0;
                        } else if (maze[i][goLeft] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }


                    int goRight = j + 1;
                    if (goRight < mazeWidth) {
                        int target = i * mazeWidth + goRight;
                        if (maze[i][goRight] == '1') {
                            R[k][target] = 0;
                        } else if (maze[i][goRight] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                    int Up = i - 1;
                    if (Up >= 0) {
                        int target = Up * mazeWidth + j;
                        if (maze[Up][j] == '1') {
                            R[k][target] = 0;
                        } else if (maze[Up][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }

                    int Down = i + 1;
                    if (Down < mazeHeight) {
                        int target = Down * mazeWidth + j;
                        if (maze[Down][j] == '1') {
                            R[k][target] = 0;
                        } else if (maze[Down][j] == 'F') {
                            R[k][target] = reward;
                        } else {
                            R[k][target] = penalty;
                        }
                    }
                }
            }
            initQ();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    void initQ()
    {
        for (int i = 0; i < stateCount; i++){
            for(int j = 0; j < stateCount; j++){
                Q[i][j] = (double)R[i][j];
            }
        }
    }

    void calculateQ() {
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            int crtState = rand.nextInt(stateCount);

            while (!FinalState(crtState)) {
                int[] actionsFromCurrentState = possibleActionsFromState(crtState);

                int index = rand.nextInt(actionsFromCurrentState.length);
                int nextState = actionsFromCurrentState[index];

                double q = Q[crtState][nextState];
                double maxQ = maxQ(nextState);
                int r = R[crtState][nextState];

                double value = q + C * (r + B * maxQ - q);
                Q[crtState][nextState] = value;

                crtState = nextState;
            }
        }
    }

    int[] possibleActionsFromState(int state) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < stateCount; i++) {
            if (R[state][i] != -1) {
                result.add(i);
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }
//prints the policy
    void Policy() {
        System.out.println("\nPrint policy");
        for (int i = 0; i < stateCount; i++) {
            System.out.println("From state " + i + " goto state " + getPolicyFromState(i));
        }
    }
    //the boolean for the final state
    boolean FinalState(int state) {
        int i = state / mazeWidth;
        int j = state - i * mazeWidth;

        return maze[i][j] == 'F';
    }

    double maxQ(int nextState) {
        int[] actionsFromState = possibleActionsFromState(nextState);

        double maxValue = -10;
        for (int nextAction : actionsFromState) {
            double value = Q[nextState][nextAction];

            if (value > maxValue)
                maxValue = value;//gets the biggest value
        }
        return maxValue;//prints it
    }

// gets the policy from the state
    int getPolicyFromState(int state) {
        int[] actionsFromState = possibleActionsFromState(state);

        double maxValue = Double.MIN_VALUE;
        int policyGotoState = state;


        for (int nextState : actionsFromState) {
            double value = Q[state][nextState];

            if (value > maxValue) {
                maxValue = value;
                policyGotoState = nextState;
            }
        }
        return policyGotoState;
    }

    void printQ() {

        String s = "Q matrix" + "\n";
        System.out.println("Q matrix");
        for (int i = 0; i < Q.length; i++) {
            System.out.print("From state " + i + ":  ");
            s += "From state" + i + ": ";
            for (int j = 0; j < Q[i].length; j++) {
                System.out.printf("%6.2f ", (Q[i][j]));
                s += String.format("%6.2f ", (Q[i][j]));
            }
            s += "\n";
            System.out.println();
        }
        try {
            FileWriter myWriter = new FileWriter("src/QSolution.txt");//this is the file that holds the solution
            myWriter.write(s);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("error");//will print if an error happens
            e.printStackTrace();
        }


    }
}