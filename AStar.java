import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class Node implements Comparable<Node> {    //fで比較
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

    @Override                   //Overrideを明示する
    public int compareTo(Node other) {
        return this.f - other.f;
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

public class AStar {

    // Goal state for 8-puzzle
    private static final int[][] GOAL_STATE = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    };

    public static void main(String[] args) {
        // Generate a random initial state
        int[][] initialState = generateRandomState();

        // Solve the puzzle
        solve(initialState);
    }

    private static void solve(int[][] initialState) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Node> closedList = new HashSet<>();

        Node startNode = new Node(initialState, 0, calculateHeuristic(initialState), null);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (isGoal(currentNode.state)) {
                printSolution(currentNode);
                return;
            }

            closedList.add(currentNode);

            for (Node child : generateSuccessors(currentNode)) {
                if (closedList.contains(child)) {
                    continue;
                }

                boolean inOpenList = false;
                for (Node openNode : openList) {
                    if (openNode.equals(child)) {
                        inOpenList = true;
                        if (child.f < openNode.f) {
                            openList.remove(openNode);
                            openList.add(child);
                        }
                        break;
                    }
                }

                if (!inOpenList) {
                    openList.add(child);
                }
            }
        }

        System.out.println("No solution found.");
    }

    private static List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        int[][] state = node.state;
        int g = node.g;
        int[] blankPos = findBlankPosition(state);
        int blankRow = blankPos[0];
        int blankCol = blankPos[1];

        int[][] directions = {
            {0, 1}, {1, 0}, {0, -1}, {-1, 0}
        };

        for (int[] direction : directions) {
            int newRow = blankRow + direction[0];
            int newCol = blankCol + direction[1];

            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
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
        int misplacedTiles = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != 0 && state[i][j] != GOAL_STATE[i][j]) {
                    misplacedTiles++;
                }
            }
        }
        return misplacedTiles;
    }

    private static int[] findBlankPosition(int[][] state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static int[][] generateRandomState() {
        List<Integer> tiles = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            tiles.add(i);
        }
        Collections.shuffle(tiles);
        int[][] state = new int[3][3];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = tiles.get(k++);
            }
        }
        return state;
    }

    private static int[][] deepCopyState(int[][] state) {
        int[][] newState = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(state[i], 0, newState[i], 0, 3);
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
