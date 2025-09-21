package com.medusa.gruul.addon.full.reduction;

/**
 * @author 张治保
 * @since 2024/6/19
 */
public class NumberMatrix {

    /**
     * 大小未 m、x、n 的数字矩阵 matrix， 从任意位置触发 可以往同行、同列移动，但不能往对角线移动，求最大移动次数
     *
     * @param matrix 数字矩阵
     * @return 最大移动次数
     */
    public int maxMove(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] dp = new int[m][n];
        // 初始化 dp 数组 第一行和第一列 为 matrix 的第一行和第一列的累加和
        dp[0][0] = matrix[0][0];
        // 初始化第一列
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i - 1][0] + matrix[i][0];
        }
        // 初始化第一行
        for (int i = 1; i < n; i++) {
            dp[0][i] = dp[0][i - 1] + matrix[0][i];
        }
        // 从第二行第二列开始遍历，每个位置的最大值为上方和左方的最大值加上当前位置的值
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                //
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]) + matrix[i][j];
            }
        }
        return dp[m - 1][n - 1];
    }
}
