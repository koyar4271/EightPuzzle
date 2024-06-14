import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class Node implements Comparable<Node> {
    int[][] state;
    int g; // Cost from start to this node
    int h; // Heuristic cost to goal
    int f; // Total cost (f = g + h)
    Node parent;

    public Node(int[][] state, int g, int h, Node parent) {
        this.state = state;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.f, other.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return Arrays.deepEquals(state, node.state);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(state);
    }
}

public class AStar15h2 {

    private static final int[][] GOAL_STATE = {
        { 1, 2, 3, 4 },
        { 5, 6, 7, 8 },
        { 9, 10, 11, 12 },
        { 13, 14, 15, 0 }
    };

    private static final int NODE_LIMIT = 10000000;

    public static void main(String[] args) {
        int[][] initialState = generateRandomState();
        solve(initialState);
    }

    private static void solve(int[][] initialState) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<Node, Node> openMap = new HashMap<>();
        Map<Node, Node> closedMap = new HashMap<>();
        int searchCost = 0;

        Node startNode = new Node(initialState, 0, calculateHeuristic(initialState), null);
        openList.add(startNode);
        openMap.put(startNode, startNode);

        while (!openList.isEmpty()) {
            searchCost++;
            if (searchCost > NODE_LIMIT) {
                System.out.println(searchCost);
                return;
            }
            Node currentNode = openList.poll();
            openMap.remove(currentNode);

            if (isGoal(currentNode.state)) {
                System.out.println( searchCost);
                //printSolution(currentNode);
                return;
            }

            closedMap.put(currentNode, currentNode);

            for (Node child : generateSuccessors(currentNode)) {
                if (closedMap.containsKey(child)) {
                    Node closedNode = closedMap.get(child);
                    if (child.f < closedNode.f) {
                        closedMap.remove(closedNode);
                        openList.add(child);
                        openMap.put(child, child);
                    }
                    continue;
                }

                if (openMap.containsKey(child)) {
                    Node openNode = openMap.get(child);
                    if (child.f < openNode.f) {
                        openList.remove(openNode);
                        openList.add(child);
                        openMap.put(child, child);
                    }
                } else {
                    openList.add(child);
                    openMap.put(child, child);
                }
            }
        }

        System.out.println("No solution found. Search cost: " + searchCost);
    }

    private static List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        int[][] state = node.state;
        int g = node.g;
        int[] blankPos = findBlankPosition(state);
        int blankRow = blankPos[0];
        int blankCol = blankPos[1];

        int[][] directions = {
            { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }
        };

        for (int[] direction : directions) {
            int newRow = blankRow + direction[0];
            int newCol = blankCol + direction[1];

            if (newRow >= 0 && newRow < 4 && newCol >= 0 && newCol < 4) {
                int[][] newState = deepCopyState(state);
                newState[blankRow][blankCol] = newState[newRow][newCol];
                newState[newRow][newCol] = 0;

                int h = calculateHeuristic(newState);
                Node childNode = new Node(newState, g + 1, h, node);
                successors.add(childNode);
            }
        }

        return successors;
    }

    private static int calculateHeuristic(int[][] state) {
        int manhattanDistance = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int tile = state[i][j];
                if (tile != 0) {
                    int goalRow = (tile - 1) / 4;
                    int goalCol = (tile - 1) % 4;
                    manhattanDistance += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return manhattanDistance;
    }

    private static int[] findBlankPosition(int[][] state) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (state[i][j] == 0) {
                    return new int[] { i, j };
                }
            }
        }
        return null;
    }

    private static int[][] generateRandomState() {
        List<Integer> tiles = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            tiles.add(i);
        }
        Collections.shuffle(tiles);
        int[][] state = new int[4][4];
        int k = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                state[i][j] = tiles.get(k++);
            }
        }
        return state;
    }

    private static int[][] deepCopyState(int[][] state) {
        int[][] newState = new int[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(state[i], 0, newState[i], 0, 4);
        }
        return newState;
    }

    private static boolean isGoal(int[][] state) {
        return Arrays.deepEquals(state, GOAL_STATE);
    }

    private static void printSolution(Node node) {
        List<int[][]> path = new ArrayList<>();
        while (node != null) {
            path.add(node.state);
            node = node.parent;
        }
        Collections.reverse(path);
        for (int[][] state : path) {
            printState(state);
            System.out.println();
        }
    }

    private static void printState(int[][] state) {
        for (int[] row : state) {
            for (int tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
    }
}
