package org.textsearch.utils;

/**
 * Автомат Левенштейна для нечеткого поиска по словарю
 * Позволяет быстро находить слова с опечатками
 */
public class LevenshteinAutomaton {
    private final String pattern;
    private final int maxEdits;

    public LevenshteinAutomaton(String pattern, int maxEdits) {
        this.pattern = pattern;
        this.maxEdits = maxEdits;
    }

    /**
     * Проверяет, принимает ли автомат слово (расстояние Левенштейна <= maxEdits)
     */
    public boolean accept(String word) {
        int m = pattern.length();
        int n = word.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (pattern.charAt(i - 1) == word.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[m][n] <= maxEdits;
    }
} 