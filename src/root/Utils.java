package root;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Utils {

    

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    public static double randDouble() {
        Random generator = new Random();
        return generator.nextDouble();
    }
    
    public static void copy(int[] source, int[] target) {
        for (int i = 0; i < source.length; i++) {
            target[i] = source[i];
        }
    }
    
    public static void copy(int[] source, List<Integer> target) {
        target.clear();
        int lastInd = 0;
        try {
            for (int i = 0; i < source.length; i++) {
                lastInd = i;
                int valToAdd = source[i];
                target.add(valToAdd);
            }
        }catch(ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Source array len: " + String.valueOf(source.length));
            System.out.println("Last index: " + String.valueOf(lastInd));
            System.out.println("Array:");
            System.out.println(Arrays.toString(source));
            
        }
    }

    public static void copy(List<Integer> source, int[] target) {
        for (int i = 0; i < source.size(); i++) {
            target[i] = source.get(i);
        }
    }
    
    public static void swap(int[] source, int ind1, int ind2) {
        int tmp = source[ind1];
        source[ind1] = source[ind2];
        source[ind2] = tmp;
    }
    
    public static void savePathToFile(String filename, int[] path) {
        List<Integer> pathList = new ArrayList<Integer>();
        for (int i = 0; i < path.length; i++) {
            pathList.add(path[i]);
        }
        savePathToFile(filename, pathList);
    }
    
    public static void savePathToFile(String filename, List<Integer> path) {
        File file = new File(filename);
        try {
            PrintWriter printer = new PrintWriter(file);
            for (int i = 0; i < 5; i++) {
                printer.write("\n");
            }
            for (int i = 0; i < path.size(); i++) {
                printer.println(path.get(i));
            }
            printer.println(-1);
            printer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
