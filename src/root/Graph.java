package root;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Graph {
    private int vertexCount;
    private boolean isBidirectional;

    private Vertex[] vertexes;
    private double[][] dist;
    private boolean[] isVisited;
    private int visitedCount;

    public Graph(String filename) {
        
        File file = new File(filename);
        try {
            Scanner sc = new Scanner(file);
            boolean cordsStarted = false;
            int vertexIndex = 0;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!cordsStarted && line.contains("TYPE: TSP")) {
                    this.isBidirectional = true;
                }
                if (!cordsStarted && line.contains("DIMENSION")) {
                    this.vertexCount = getFirstInteger(line);
                    this.vertexes = new Vertex[vertexCount];
                    this.dist = new double[vertexCount][vertexCount];
                    this.isVisited = new boolean[vertexCount];
                    this.visitedCount = 0;
                    clearDistances();
                    clearVisited();
                }
                if (line.contains("EOF")) {
                    break;
                }
                if (cordsStarted) {
                    String[] lineVals = line.split(" ");
                    int id = Integer.valueOf(lineVals[0]);
                    int x = Integer.valueOf(lineVals[1]);
                    int y = Integer.valueOf(lineVals[2]);
                    vertexes[vertexIndex] = new Vertex(id, x, y);
                    vertexIndex += 1;
                }
                if (line.contains("NODE_COORD_SECTION")) {
                    cordsStarted = true;
                }
            }
            sc.close();
            initDist();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Graph(int vertexCount, boolean isBidirectional) {
        this.isBidirectional = isBidirectional;
        this.vertexCount = vertexCount;
        this.dist = new double[vertexCount][vertexCount];
        this.isVisited = new boolean[vertexCount];
        this.visitedCount = 0;
        clearDistances();
        clearVisited();
    }

    private int sqr(int a) {
        return a * a;
    }

    private void initDist() {
        for (int i = 0; i < vertexCount; i++) {
            Vertex v1 = vertexes[i];
            for (int j = i + 1; j < vertexCount; j++) {
                Vertex v2 = vertexes[j];
                double d = Math.sqrt(sqr(v1.getX() - v2.getX())
                        + sqr(v1.getY() - v2.getY()));
                dist[i][j] = d;
                dist[j][i] = d;
            }
        }
    }

    private int getFirstInteger(String s) {
        boolean numberDetected = false;
        int number = 0;
        int strLen = s.length();
        for (int i = 0; i < strLen; i++) {
            char ch = s.charAt(i);
            while (Character.isDigit(ch)) {
                numberDetected = true;
                number = 10 * number + (ch - 48);
                if (i + 1 >= strLen) {
                    break;
                }
                i += 1;
                ch = s.charAt(i);
            }
            if (numberDetected) {
                break;
            }
        }
        return number;
    }

    public void clearVisited() {
        for (int i = 0; i < vertexCount; i++) {
            isVisited[i] = false;
        }
        visitedCount = 0;
    }

    public void clearDistances() {
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                dist[i][j] = 0.0;
            }
        }
    }

    public boolean isVisited(int id) {
        return isVisited[id - 1];
    }

    public void setVisited(int id, boolean visited) {
        boolean curVisited = isVisited[id - 1];
        if (!curVisited && visited) {
            visitedCount += 1;
        }else if (curVisited && !visited) {
            visitedCount -= 1;
        }
        isVisited[id - 1] = visited;
    }

    public double getDist(int id1, int id2) {
        return dist[id1 - 1][id2 - 1];
    }

    public void setDist(int v1, int v2, double distance) {
        dist[v1][v2] = distance;
        if (isBidirectional) {
            dist[v2][v1] = distance;
        }
    }
    

    public Vertex getRandomUnvisitedVertex() {
        if (visitedCount == vertexCount) {
            throw new IllegalStateException("All vertexes are visited!");
        }
        int upTo = vertexCount - visitedCount - 1;
        int thToTake = Utils.randInt(0, upTo);
        int th = 0;
        for (int i = 0; i < vertexCount; i++) {
            Vertex v = vertexes[i];
            if (!isVisited(v.getId())) {
                if (th == thToTake) {
                    return v;
                }
                th += 1;
            }
        }
        return null;
    }
    
    public int getVertexCount() {
        return this.vertexCount;
    }

    public int getVisitedCount() {
        return this.visitedCount;
    }
}
