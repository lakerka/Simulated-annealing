package root;

public interface OnRequestShortestPathListener {

    public void onRequestShortestPath(int[] curShortestPath,
            double curShortestPathLen, int[] shortestPathContainer);
}
