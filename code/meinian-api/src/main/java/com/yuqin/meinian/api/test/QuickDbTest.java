package com.yuqin.meinian.api.test;

import java.math.BigDecimal;
import java.sql.*;

public class QuickDbTest {

    public static void main(String[] args) {
        // 数据库连接信息（请根据您的实际配置修改）
        String url = "jdbc:mysql://192.168.5.3:3320/meinian-his?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&nullCatalogMeansCurrent=true";
        String username = "root";
        String password = "123456";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 1. 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. 建立连接
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功！");

            // 3. 执行查询
            String sql = "SELECT order_id, transaction_id, total_amount, order_status, customer_id " +
                    "FROM trade_order " +
                    "WHERE order_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 25);
            rs = pstmt.executeQuery();

            // 4. 处理结果
            if (rs.next()) {
                int orderId = rs.getInt("order_id");
                String transactionId = rs.getString("transaction_id");
                BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                int orderStatus = rs.getInt("order_status");
                int customerId = rs.getInt("customer_id");

                System.out.println("========== 查询结果 ==========");
                System.out.println("orderId: " + orderId);
                System.out.println("transactionId: " + transactionId);
                System.out.println("totalAmount: " + totalAmount);
                System.out.println("orderStatus: " + orderStatus);
                System.out.println("customerId: " + customerId);
                System.out.println("==============================");
            } else {
                System.out.println("未找到 orderId = 25 的记录");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动未找到: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
        } finally {
            // 5. 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}