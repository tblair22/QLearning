/*
Trent Blair
4/18/22
This project makes the maze for the QLearning lab
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Maze {

    private Random r;
    int width;
    int height;


    public Maze(){
        r = new Random();
        width = 5;
        height = 5;
    }
    public void buildMaze(){

        int destination = r.nextInt(25);//builds random maze inside of 15

        int spacesAdded = 0;
        String s = new String();

        int clearOrBlocked;
        for(int i = 0; i< 25; i++){
            if(spacesAdded % 5 == 0 && spacesAdded != 0){
                s += "\n";
            }
            clearOrBlocked = r.nextInt(3);
            if(i == destination){
                s+= "F";
                spacesAdded += 1;
            }else if(clearOrBlocked == 0 || clearOrBlocked == 1){
                s += "1";
                spacesAdded += 1;
            }else{
                s += "V";
                spacesAdded += 1;
            }
        }
        try {
            FileWriter Writer = new FileWriter("src/Maze.txt");//this is the file that shows the maze
            Writer.write(s);
            Writer.close();
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
    }

}