import java.io.*;
import java.util.*;

public class CYKParser {

    static class Grammar {
        Set<String> nonTerminals = new HashSet<>();
        Map<String, List<String>> rhsToLhs = new HashMap<>();
        String startSymbol = "S0";

        public void loadGrammar(String filename) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("->");
                if (parts.length != 2) continue;
                String lhs = parts[0].trim();
                nonTerminals.add(lhs);
                if (startSymbol == null) startSymbol = lhs;

                String[] rhsParts = parts[1].trim().split("\\|");
                for (String rhs : rhsParts) {
                    rhs = rhs.trim();
                    if (rhs.equals("ε")) {
                        rhs = ""; // epsilon is stored as empty string
                    }
                    rhsToLhs.computeIfAbsent(rhs, k -> new ArrayList<>()).add(lhs);
                }
            }
            reader.close();
        }

        public List<String> getProducers(String rhs) {
            return rhsToLhs.getOrDefault(rhs, new ArrayList<>());
        }

        public boolean startSymbolDerivesEpsilon() {
            return getProducers("").contains(startSymbol);
        }
    }

    public static boolean cykParse(String input, Grammar grammar) {
        int n = input.length();

        // Handle empty string input
        if (n == 0) {
            return grammar.startSymbolDerivesEpsilon();
        }

        List<String>[][] table = new ArrayList[n][n];

        // Initialize table for substrings of length 1 (terminals)
        for (int i = 0; i < n; i++) {
            Set<String> initial = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            queue.add(String.valueOf(input.charAt(i)));
            while (!queue.isEmpty()) {
                String symbol = queue.poll();
                for (String producer : grammar.getProducers(symbol)) {
                    if (!initial.contains(producer)) {
                        initial.add(producer);
                        queue.add(producer); // recursively check if this has producers too
                    }
                }
            }
            table[i][0] = new ArrayList<>(initial);
        }

        // Build CYK table
        for (int l = 2; l <= n; l++) { // length of span
            for (int s = 0; s <= n - l; s++) { // start of span
                int row = s;
                int col = l - 1;
                table[row][col] = new ArrayList<>();
                for (int p = 1; p < l; p++) {
                    List<String> left = table[row][p - 1];
                    List<String> right = table[row + p][l - p - 1];
                    if (left == null || right == null) continue;

                    for (String b : left) {
                        for (String c : right) {
                            String combined = b + c;
                            for (String producer : grammar.getProducers(combined)) {
                                if (!table[row][col].contains(producer)) {
                                    table[row][col].add(producer);
                                }
                            }
                        }
                    }
                }
            }
        }

        // First pass: compute column widths
        int[][] lengths = new int[n][n];
        int[] colWidths = new int[n];

        for (int row = 0; row < n; row++) {
            for (int col = row; col < n; col++) {
                int i = row;
                int j = col - row;
                String content;
                List<String> cell = table[i][j];
                content = "(" + (i + 1) + "," + (col + 1) + "): ";
                content += (cell == null || cell.isEmpty()) ? "∅" : String.join(",", cell);
                lengths[i][j] = content.length();
                colWidths[col] = Math.max(colWidths[col], content.length());
            }
        }

        System.out.println("\nCYK Parse Table:");

        for (int row = n - 1; row >= 0; row--) {
            for (int col = 0; col < n; col++) {
                if (col < row) {
                    System.out.print(" ".repeat(colWidths[col] + 1));
                    continue;
                }
                int i = row;
                int j = col - row;
                List<String> cell = table[i][j];
                String content = "(" + (i + 1) + "," + (col + 1) + "): ";
                content += (cell == null || cell.isEmpty()) ? "∅" : String.join(",", cell);
                int pad = colWidths[col] - content.length();
                System.out.print(content + " ".repeat(pad + 1));
            }
            System.out.println();
        }

        return table[0][n - 1] != null && table[0][n - 1].contains(grammar.startSymbol);
    }

    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar();
        grammar.loadGrammar("CNF.txt");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string to parse: ");
        String input = scanner.nextLine().trim();

        boolean result = cykParse(input, grammar);
        System.out.println();
        System.out.println("The string " + "'" + input + "'" + " is " + result + ".");
    }
}
