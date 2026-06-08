package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-18: 销售统计页面
 */
public class SellerStatsPanel extends JPanel {

    private JTextArea statsArea;
    private JButton backBtn, refreshBtn;

    public SellerStatsPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerProduct"));
        refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refresh());
        topBar.add(backBtn);
        topBar.add(refreshBtn);
        add(topBar, BorderLayout.NORTH);

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("宋体", Font.PLAIN, 14));
        add(new JScrollPane(statsArea), BorderLayout.CENTER);
    }

    public void refresh() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/seller/stats"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");

            StringBuilder sb = new StringBuilder();
            sb.append("========== 销售统计 ==========\n\n");
            sb.append("总订单数: ").append(data.get("totalOrders")).append("\n");
            sb.append("待付款: ").append(data.get("pendingPay")).append("\n");
            sb.append("待发货: ").append(data.get("pendingShip")).append("\n");
            sb.append("待收货: ").append(data.get("pendingReceive")).append("\n");
            sb.append("已完成: ").append(data.get("completed")).append("\n");
            sb.append("已取消: ").append(data.get("cancelled")).append("\n");
            sb.append("退货中: ").append(data.get("returning")).append("\n");
            sb.append("已退货: ").append(data.get("returned")).append("\n");
            statsArea.setText(sb.toString());
        } catch (Exception ex) {
            statsArea.setText("加载失败: " + ex.getMessage());
        }
    }
}
