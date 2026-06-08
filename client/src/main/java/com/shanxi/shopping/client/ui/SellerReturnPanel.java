package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-17: 退货处理页面
 */
public class SellerReturnPanel extends JPanel {

    private JPanel returnListPanel;
    private JButton backBtn;

    public SellerReturnPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerProduct"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        returnListPanel = new JPanel();
        returnListPanel.setLayout(new BoxLayout(returnListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(returnListPanel), BorderLayout.CENTER);
    }

    public void refresh() {
        returnListPanel.removeAll();
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/seller/returns"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("data");

            if (list != null) {
                for (Map<String, Object> r : list) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    Long returnId = Long.valueOf(r.get("id").toString());
                    int status = (int) r.get("status");
                    String statusText = switch (status) {
                        case 1 -> "待处理"; case 2 -> "已同意"; case 3 -> "已拒绝"; case 4 -> "已完成";
                        default -> "未知";
                    };
                    row.add(new JLabel("退货ID: " + returnId + "  订单ID: " + r.get("orderId")));
                    row.add(new JLabel("金额: ¥" + r.get("refundAmount") + "  状态: " + statusText));
                    row.add(new JLabel("原因: " + r.get("reason")));

                    if (status == 1) {
                        JButton approveBtn = new JButton("同意");
                        approveBtn.addActionListener(e -> {
                            try {
                                ApiClient.put("/api/seller/returns/" + returnId, Map.of("approved", true, "remark", "同意退货"));
                                refresh();
                            } catch (Exception ex) { ex.printStackTrace(); }
                        });
                        row.add(approveBtn);
                        JButton rejectBtn = new JButton("拒绝");
                        rejectBtn.addActionListener(e -> {
                            try {
                                ApiClient.put("/api/seller/returns/" + returnId, Map.of("approved", false, "remark", "拒绝退货"));
                                refresh();
                            } catch (Exception ex) { ex.printStackTrace(); }
                        });
                        row.add(rejectBtn);
                    }
                    returnListPanel.add(row);
                }
            }
        } catch (Exception ex) {
            returnListPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        returnListPanel.revalidate();
        returnListPanel.repaint();
    }
}
