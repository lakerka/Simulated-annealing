package root;
import java.util.Random;


public class SimulatedAnnealing {
        
    private double initialTemperature;
    private double tempCoolingRate;
    private double equilibriumTemperature;
    private IIterationCountGetter itCountGetter;
    private Graph graph;
    private int[] shortestPath;
    private int[] tmpPath;
    private double shortestPathLen;
    
    public SimulatedAnnealing(double initialTemperature,
            double temperatureCoolingRate,
            double equilibriumTemperature,
            IIterationCountGetter iterationCountGetter,
            Graph graph) {
        this.initialTemperature = initialTemperature;
        this.tempCoolingRate = temperatureCoolingRate;
        this.equilibriumTemperature = equilibriumTemperature;
        this.itCountGetter = iterationCountGetter;
        this.graph = graph;
        
        int vertexCount = graph.getVertexCount();
        this.tmpPath = new int[vertexCount];
        this.shortestPath = new int[vertexCount];
    }
    
    public int[] runSimulatedAnnealing(int[] providedShortestPath) {
        
        double curTemperature = initialTemperature;
        if (providedShortestPath == null) {
            initPath(shortestPath, graph);
        }else {
            Utils.copy(providedShortestPath, shortestPath);
        }
        shortestPathLen = graph.getPathLen(shortestPath);
                
        while (curTemperature > equilibriumTemperature) {
            int iterationCount = itCountGetter.getIterationCount(curTemperature);
            for (int it = 0; it < iterationCount; it++) {
                Utils.copy(shortestPath, tmpPath);
                double curPathLen = probabalisticSwap(shortestPathLen, 
                        curTemperature, shortestPath, tmpPath, graph);
                shortestPathLen = Math.min(curPathLen, shortestPathLen);
            }
            // decrease temperature
            curTemperature = getNewTemperature1(curTemperature, tempCoolingRate);
        }
        return shortestPath;
    }

    public static double getNewTemperature3(double curTemp, int time) {
        // inverse log decrease
        return Math.max(curTemp/Math.log((double)(time + 2)), 0.0);
    }

    public static double getNewTemperature2(double curTemp, double cooledOff) {
        // additive decrease
        return Math.max(curTemp - cooledOff, 0.0);
    }
    
    public static double getNewTemperature1(double curTemp, double coolingRate) {
        // decreases temperature by multiplying by constant
        return Math.max(curTemp*coolingRate, 0.0);
    }
    
    public static boolean isNewPathAccepted(double oldPathLen, double newPathLen, double temperature) {
        double pathLenDiff = oldPathLen - newPathLen ;
        double acceptProb = Math.exp(pathLenDiff/temperature);
        
        double uniformRandom = (new Random()).nextDouble();
//        System.out.print("old pathLen: " + decimalFormat.format(oldPathLen));
//        System.out.print(" new pathLen: " + decimalFormat.format(newPathLen));
//        System.out.print(" temp: " + decimalFormat.format(temperature));
////        System.out.print(" e^" + decimalFormat.format(-(newPathLen - oldPathLen)/temperature));
//        System.out.print(" accept prob: " + decimalFormat.format(acceptProb));
//        System.out.print(" random prob: " + decimalFormat.format(uniformRandom));
//        System.out.println(" accept: " + String.valueOf(newPathLen < oldPathLen) + " || " + String.valueOf(uniformRandom < acceptProb));
        
//        System.out.println( " is accepted: " + String.valueOf((newPathLen < oldPathLen) || (uniformRandom < acceptProb)));
//        System.out.println();
        if ((newPathLen < oldPathLen) || (uniformRandom < acceptProb)) {
            return true;
        }
        return false;
    }
    
    
    public static double probabalisticSwap(double curPathLen, double temperature, int[] curPath, int[] tmpPath, Graph graph) {
        int vCount = graph.getVertexCount();
        int rnd1 = 0, rnd2 = 0;
        while (rnd1 == rnd2) {
            rnd1 = Utils.randInt(0, vCount - 1);
            rnd2 = Utils.randInt(0, vCount - 1);
        }
        // swap vertexes
        tmpPath[rnd1] = curPath[rnd2];
        tmpPath[rnd2] = curPath[rnd1];
        double newPathLen = graph.getPathLen(tmpPath);
        
//        System.out.print("cur pathLen: " + String.valueOf(curPathLen));
//        System.out.print(" new pathLen: " + String.valueOf(newPathLen));
//        System.out.print(" temp: " + String.valueOf(temp));
//        System.out.println( " is accepted: " + isNewPathAccepted(curPathLen, newPathLen, temp));
        
        if (isNewPathAccepted(curPathLen, newPathLen, temperature)) {
            // save new path
            Utils.swap(curPath, rnd1, rnd2);
            return newPathLen;
        }
        return curPathLen;
    }
    
    public static void initPath(int[] path, Graph graph) {
        int visitedCount = 0;
        int vertexCount = graph.getVertexCount();
        while (visitedCount < vertexCount) {
            Vertex randVertex = graph.getRandomUnvisitedVertex();
            int id =randVertex.getId();
            graph.setVisited(id, true);
            path[visitedCount] = id;
            visitedCount += 1;
        }
    }
}
