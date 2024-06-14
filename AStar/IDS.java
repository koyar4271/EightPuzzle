import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Node {
    int[][] state;
    int depth;
    Node parent;

    public Node(int[][] state, int depth, Node parent) {
        this.state = state;
        this.depth = depth;
        this.parent = parent;
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

public class IDS {
    private static final int[][] GOAL_STATE = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 0}
    };
    private static final int NODE_LIMIT = 30000000;
    private static int nodeCount = 0;

    public static void main(String[] args) {
        int[][] initialState = generateRandomState();
        boolean result = iddfs(initialState);
        if (!result) {
            System.out.println("No solution found.");
        }
        System.out.println(nodeCount);
    }

    private static boolean iddfs(int[][] initialState) {
        for (int depth = 0; depth < Integer.MAX_VALUE; depth++) {
            Set<Node> visited = new HashSet<>();
            nodeCount = 0;
            if (dls(new Node(initialState, 0, null), depth, visited)) {
                return true;
            }
            if (nodeCount >= NODE_LIMIT) {
                System.out.println("Node limit reached, terminating search.");
                return false;
            }
        }
        return false;
    }

    private static boolean dls(Node node, int depth, Set<Node> visited) {
        nodeCount++;
        if (nodeCount > NODE_LIMIT) {
            return false;
        }
        if (isGoal(node.state)) {
            //System.out.println("Goal found at depth: " + node.depth);
            return true;
        }
        if (depth == 0) {
            return false;
        }

        visited.add(node);
        for (Node child : generateSuccessors(node)) {
            if (!visited.contains(child) && dls(child, depth - 1, visited)) {
                return true;
            }
        }
        visited.remove(node);
        return false;
    }

    private static List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        int[][] state = node.state;
        int depth = node.depth;
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

                Node childNode = new Node(newState, depth + 1, node);
                successors.add(childNode);
            }
        }

        return successors;
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
}
