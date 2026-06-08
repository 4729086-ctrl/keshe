package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-15: 卖家订单管理
 */
public class SellerOrderPanel extends JPanel {

    private JPanel orderPanel;
    private JButton backBtn;

    public SellerOrderPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerProduct"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(orderPanel), BorderLayout.CENTER);
    }

    public void refresh() {
        orderPanel.removeAll();
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/seller/orders?page=1&pageSize=50"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

            if (list != null) {
                for (Map<String, Object> o : list) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    Long orderId = Long.valueOf(o.get("id").toString());
                    int status = (int) o.get("status");
                    row.add(new JLabel(o.get("orderNo") + "  ¥" + o.get("totalAmount") + "  " + o.get("statusDesc")));
                    row.add(new JLabel("收货: " + o.get("receiverName") + " " + o.get("receiverAddress")));

                    if (status == 2) {
                        JButton shipBtn = new JButton("发货");
                        shipBtn.addActionListener(e -> {
                            AppContext.getInstance().setAttribute("orderId", orderId);
                            AppContext.getInstance().navigateTo("sellerShip");
                        });
                        row.add(shipBtn);
                    }
                    orderPanel.add(row);
                }
            }
        } catch (Exception ex) {
            orderPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        orderPanel.revalidate();
        orderPanel.repaint();
    }
}
