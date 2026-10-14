package railsync;

import java.util.*;

/** Implementations of the DSA algorithms demonstrated by RailSync. */
public final class DsaAlgorithms {
    private DsaAlgorithms() {}

    // CO1: Knuth-Morris-Pratt substring search. Returns first match index, or -1.
    public static int kmpSearch(String text, String pattern) {
        if (text == null || pattern == null) throw new IllegalArgumentException("Text and pattern are required.");
        if (pattern.isEmpty()) return 0;
        int[] lps = buildLps(pattern);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == pattern.length()) return i - j;
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return -1;
    }

    private static int[] buildLps(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0, i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    // CO2: Suffix array using prefix-doubling; returns starting positions in sorted order.
    public static int[] suffixArray(String s) {
        int n = s.length();
        Integer[] suffixes = new Integer[n];
        int[] rank = new int[n];
        int[] nextRank = new int[n];
        for (int i = 0; i < n; i++) {
            suffixes[i] = i;
            rank[i] = s.charAt(i);
        }
        for (int k = 1; k < n; k *= 2) {
            final int step = k;
            Arrays.sort(suffixes, (a, b) -> {
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = a + step < n ? rank[a + step] : -1;
                int rb = b + step < n ? rank[b + step] : -1;
                return Integer.compare(ra, rb);
            });
            if (n == 0) break;
            nextRank[suffixes[0]] = 0;
            for (int i = 1; i < n; i++) {
                int a = suffixes[i - 1], b = suffixes[i];
                boolean different = rank[a] != rank[b]
                        || (a + step < n ? rank[a + step] : -1)
                        != (b + step < n ? rank[b + step] : -1);
                nextRank[b] = nextRank[a] + (different ? 1 : 0);
            }
            System.arraycopy(nextRank, 0, rank, 0, n);
            if (rank[suffixes[n - 1]] == n - 1) break;
            if (k > n / 2) break; // prevents integer overflow on very long strings
        }
        int[] result = new int[n];
        for (int i = 0; i < n; i++) result[i] = suffixes[i];
        return result;
    }

    // Kasai LCP: lcp[i] is the common-prefix length of suffixArray[i] and suffixArray[i-1].
    public static int[] lcpKasai(String s, int[] sa) {
        int n = s.length();
        int[] rank = new int[n];
        int[] lcp = new int[n];
        for (int i = 0; i < n; i++) rank[sa[i]] = i;
        int h = 0;
        for (int i = 0; i < n; i++) {
            int r = rank[i];
            if (r == 0) continue;
            int j = sa[r - 1];
            while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) h++;
            lcp[r] = h;
            if (h > 0) h--;
        }
        return lcp;
    }

    // CO3: Edit distance with insertion, deletion, substitution.
    public static int levenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    // CO4: Edmonds-Karp maximum flow. Input capacity matrix is not modified.
    public static int edmondsKarp(int[][] capacity, int source, int sink) {
        int n = capacity.length;
        if (source < 0 || sink < 0 || source >= n || sink >= n || source == sink)
            throw new IllegalArgumentException("Invalid source/sink.");
        int[][] residual = new int[n][n];
        for (int i = 0; i < n; i++) {
            if (capacity[i].length != n) throw new IllegalArgumentException("Capacity matrix must be square.");
            residual[i] = Arrays.copyOf(capacity[i], n);
        }
        int maxFlow = 0;
        int[] parent = new int[n];
        while (bfsResidual(residual, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v])
                pathFlow = Math.min(pathFlow, residual[parent[v]][v]);
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    private static boolean bfsResidual(int[][] residual, int source, int sink, int[] parent) {
        Arrays.fill(parent, -1);
        boolean[] seen = new boolean[residual.length];
        Queue<Integer> q = new LinkedList<>();
        q.add(source); seen[source] = true;
        while (!q.isEmpty()) {
            int u = q.remove();
            for (int v = 0; v < residual.length; v++) {
                if (!seen[v] && residual[u][v] > 0) {
                    parent[v] = u; seen[v] = true; q.add(v);
                }
            }
        }
        return seen[sink];
    }

    // CO5: 2-approximation for vertex cover. Edges are int pairs [u,v].
    public static Set<Integer> vertexCover2Approx(int vertexCount, int[][] edges) {
        Set<Integer> cover = new LinkedHashSet<>();
        List<int[]> remaining = new ArrayList<>();
        for (int[] e : edges) {
            if (e.length != 2 || e[0] < 0 || e[1] < 0 || e[0] >= vertexCount || e[1] >= vertexCount)
                throw new IllegalArgumentException("Each edge must contain valid vertex indices.");
            if (e[0] != e[1]) remaining.add(Arrays.copyOf(e, 2));
        }
        boolean[] covered = new boolean[vertexCount];
        for (int[] edge : remaining) {
            int u = edge[0], v = edge[1];
            if (!covered[u] && !covered[v]) {
                cover.add(u); cover.add(v);
                covered[u] = true; covered[v] = true;
            }
        }
        return cover;
    }

    // CO6: Randomized QuickSort for integer arrays.
    public static void randomizedQuickSort(int[] a) {
        randomizedQuickSort(a, 0, a.length - 1, new Random());
    }

    private static void randomizedQuickSort(int[] a, int low, int high, Random random) {
        if (low >= high) return;
        int pivotIndex = low + random.nextInt(high - low + 1);
        swap(a, pivotIndex, high);
        int p = partition(a, low, high);
        randomizedQuickSort(a, low, p - 1, random);
        randomizedQuickSort(a, p + 1, high, random);
    }

    private static int partition(int[] a, int low, int high) {
        int pivot = a[high], i = low;
        for (int j = low; j < high; j++) {
            if (a[j] <= pivot) swap(a, i++, j);
        }
        swap(a, i, high);
        return i;
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
