import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

class Node {
    int[][] board;
    int x, y;
    int cost, level;
    Node parent;

    public Node(int[][] board, int x, int y, int newX, int newY, int level, Node parent) {
        this.board = new int[4][4];
        for (int i = 0; i < 4; i++) {
            this.board[i] = Arrays.copyOf(board[i], 4);
        }
        this.board[x][y] = this.board[newX][newY];
        this.board[newX][newY] = 0;
        this.cost = Integer.MAX_VALUE;
        this.level = level;
        this.parent = parent;
        this.x = newX;
        this.y = newY;
    }
}

class Puzzle {
    static int[] row = { 1, 0, -1, 0 };
    static int[] col = { 0, -1, 0, 1 };

    public static int calculateCost(int[][] initial, int[][] goal) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (initial[i][j] != 0 && initial[i][j] != goal[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public static boolean isSafe(int x, int y) {
        return (x >= 0 && x < 4 && y >= 0 && y < 4);
    }

    public static void printBoard(int[][] board) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printPath(Node root) {
        if (root == null) {
            return;
        }
        printPath(root.parent);
        printBoard(root.board);
        System.out.println();
    }

    public static void solve(int[][] initial, int x, int y, int[][] goal) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.cost + a.level));
        Node root = new Node(initial, x, y, x, y, 0, null);
        root.cost = calculateCost(initial, goal);
        pq.add(root);

        while (!pq.isEmpty()) {
            Node min = pq.poll();

            if (min.cost == 0) {
                printPath(min);
                return;
            }

            for (int i = 0; i < 4; i++) {
                if (isSafe(min.x + row[i], min.y + col[i])) {
                    Node child = new Node(min.board, min.x, min.y, min.x + row[i], min.y + col[i], min.level + 1, min);
                    child.cost = calculateCost(child.board, goal);
                    pq.add(child);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[][] initial = new int[4][4];
        int[][] goal = { 
            { 1, 2, 3, 4 }, 
            { 5, 6, 7, 8 }, 
            { 9, 10, 11, 12 }, 
            { 13, 14, 15, 0 } 
        };
        int x = 0, y = 0;

        System.out.println("Enter the initial state of the 15 puzzle (4x4 matrix):");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                initial[i][j] = sc.nextInt();
                if (initial[i][j] == 0) {
                    x = i;
                    y = j;
                }
            }
        }

        solve(initial, x, y, goal);
    }
}
