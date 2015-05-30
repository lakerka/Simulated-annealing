package root;

public interface IRequestShortestPathListener {

    public void onRequestShortestPath(int[] curShortestPath,
            double curShortestPathLen);
}
