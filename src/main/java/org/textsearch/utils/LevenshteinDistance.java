package org.textsearch.utils;

/**
 * Утилита для вычисления расстояния Левенштейна
 * Используется для нечеткого поиска с учетом опечаток
 */
public class LevenshteinDistance {
    
    /**
     * Вычисляет расстояние Левенштейна между двумя строками
     * @param s1 первая строка
     * @param s2 вторая строка
     * @return минимальное количество операций для преобразования s1 в s2
     */
    public static int calculate(String s1, String s2) {
        if (s1 == null || s2 == null) {
            throw new IllegalArgumentException("Strings cannot be null");
        }
        
        int len1 = s1.length();
        int len2 = s2.length();
        
        // Создаем матрицу для динамического программирования
        int[][] matrix = new int[len1 + 1][len2 + 1];
        
        // Инициализируем первую строку и столбец
        for (int i = 0; i <= len1; i++) {
            matrix[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            matrix[0][j] = j;
        }
        
        // Заполняем матрицу
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                matrix[i][j] = Math.min(
                    Math.min(matrix[i - 1][j] + 1, matrix[i][j - 1] + 1),
                    matrix[i - 1][j - 1] + cost
                );
            }
        }
        
        return matrix[len1][len2];
    }
    
    /**
     * Вычисляет нормализованное расстояние Левенштейна (0-1)
     * @param s1 первая строка
     * @param s2 вторая строка
     * @return нормализованное расстояние (0 = идентичные, 1 = максимально разные)
     */
    public static double calculateNormalized(String s1, String s2) {
        int distance = calculate(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        return maxLength == 0 ? 0.0 : (double) distance / maxLength;
    }
    
    /**
     * Проверяет, похожи ли строки (расстояние Левенштейна <= threshold)
     * @param s1 первая строка
     * @param s2 вторая строка
     * @param threshold максимальное допустимое расстояние
     * @return true если строки похожи
     */
    public static boolean isSimilar(String s1, String s2, int threshold) {
        return calculate(s1, s2) <= threshold;
    }
    
    /**
     * Проверяет, похожи ли строки по нормализованному расстоянию
     * @param s1 первая строка
     * @param s2 вторая строка
     * @param threshold максимальное нормализованное расстояние (0-1)
     * @return true если строки похожи
     */
    public static boolean isSimilarNormalized(String s1, String s2, double threshold) {
        return calculateNormalized(s1, s2) <= threshold;
    }
} 