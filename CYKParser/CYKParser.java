import java.io.*;
import java.util.*;

public class CYKParser {

    static class Grammar {
        Set<String> nonTerminals = new HashSet<>();
        Map<String, List<String>> productions = new HashMap<>();

        public void loadGrammar(String filename) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("->");
                if (parts.length != 2) continue;
                String lhs = parts[0].trim();
                nonTerminals.add(lhs);
                String[] rhsParts = parts[1].trim().split("\\|");
                for (String rhs : rhsParts) {
                    rhs = rhs.trim();
                    productions.computeIfAbsent(rhs, k -> new ArrayList<>()).add(lhs);
                }
            }
            reader.close();
        }

        public List<String> getProducers(String rhs) {
            return productions.getOrDefault(rhs, new ArrayList<>());
        }
    }

    public static boolean cykParse(String input, Grammar grammar) {
        int n = input.length();
        List<String>[][] table = new ArrayList[n][n];

        for (int i = 0; i < n; i++) {
            table[i][0] = new ArrayList<>(grammar.getProducers(String.valueOf(input.charAt(i))));
        }

        // Build the CYK table
        for (int l = 2; l <= n; l++) {
            for (int s = 0; s <= n - l; s++) {
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

        return table[0][n - 1] != null && table[0][n - 1].contains("S");
    }

    public static void main(String[] args) throws IOException {
        Grammar grammar = new Grammar();
        grammar.loadGrammar("CFG.txt");

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string to parse: ");
        String input = scanner.nextLine().trim();

        boolean result = cykParse(input, grammar);
        System.out.println("Accepted: " + result);
    }
}
