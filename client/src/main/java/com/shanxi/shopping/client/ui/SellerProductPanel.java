package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-13: 卖家商品管理 / 卖家首页
 */
public class SellerProductPanel extends JPanel {

    private JPanel productListPanel;
    private JButton addBtn, orderBtn, returnBtn, statsBtn, shopBtn;

    public SellerProductPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addBtn = new JButton("新增商品");
        addBtn.addActionListener(e -> {
            // 清空编辑页并跳转
            AppContext.getInstance().navigateTo("sellerProductEdit");
        });
        orderBtn = new JButton("订单管理");
        orderBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerOrder"));
        returnBtn = new JButton("退货处理");
        returnBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerReturn"));
        statsBtn = new JButton("销售统计");
        statsBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerStats"));
        shopBtn = new JButton("店铺信息");
        shopBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerShop"));
        topBar.add(addBtn); topBar.add(orderBtn); topBar.add(returnBtn);
        topBar.add(statsBtn); topBar.add(shopBtn);
        add(topBar, BorderLayout.NORTH);

        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(productListPanel), BorderLayout.CENTER);
    }

    public void refresh() {
        productListPanel.removeAll();
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/seller/products?page=1&pageSize=50"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

            if (list != null) {
                for (Map<String, Object> p : list) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    Long productId = Long.valueOf(p.get("id").toString());
                    int status = (int) p.get("status");
                    String statusText = status == 1 ? "上架" : "下架";
                    row.add(new JLabel(p.get("name") + "  ¥" + p.get("price") + "  库存:" + p.get("stock") + "  [" + statusText + "]"));

                    JButton editBtn = new JButton("编辑");
                    editBtn.addActionListener(e -> {
                        AppContext.getInstance().setAttribute("productId", productId);
                        AppContext.getInstance().navigateTo("sellerProductEdit");
                    });
                    row.add(editBtn);

                    JButton toggleBtn = new JButton(status == 1 ? "下架" : "上架");
                    toggleBtn.addActionListener(e -> {
                        try {
                            ApiClient.put("/api/seller/products/" + productId + "/status", Map.of("status", status == 1 ? 0 : 1));
                            refresh();
                        } catch (Exception ex) { ex.printStackTrace(); }
                    });
                    row.add(toggleBtn);
                    productListPanel.add(row);
                }
            }
        } catch (Exception ex) {
            productListPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        productListPanel.revalidate();
        productListPanel.repaint();
    }
}
