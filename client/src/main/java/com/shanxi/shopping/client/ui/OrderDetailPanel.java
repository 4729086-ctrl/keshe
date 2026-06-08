package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-09: 订单详情页
 */
public class OrderDetailPanel extends JPanel {

    private JTextArea detailArea;
    private JButton backBtn;

    public OrderDetailPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回订单列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("orderList"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("宋体", Font.PLAIN, 14));
        add(new JScrollPane(detailArea), BorderLayout.CENTER);
    }

    public void loadOrder(Long orderId) {
        try {
            detailArea.setText("加载中...");
            Map<String, Object> result = HttpHelper.getMap("/api/orders/" + orderId);
            Map<String, Object> data = (Map<String, Object>) result.get("data");

            StringBuilder sb = new StringBuilder();
            sb.append("订单号: ").append(data.get("orderNo")).append("\n");
            sb.append("金额: ¥").append(data.get("totalAmount")).append("\n");
            sb.append("状态: ").append(data.get("statusDesc")).append("\n");
            sb.append("收货人: ").append(data.get("receiverName")).append("\n");
            sb.append("电话: ").append(data.get("receiverPhone")).append("\n");
            sb.append("地址: ").append(data.get("receiverAddress")).append("\n");
            sb.append("物流: ").append(data.get("logisticsCompany")).append(" ").append(data.get("logisticsNo")).append("\n");
            sb.append("下单时间: ").append(data.get("createdAt")).append("\n");
            sb.append("--- 商品明细 ---\n");
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
            if (items != null) {
                for (Map<String, Object> item : items) {
                    sb.append("  ").append(item.get("productName"))
                      .append(" x").append(item.get("quantity"))
                      .append(" ¥").append(item.get("subtotal")).append("\n");
                }
            }
            detailArea.setText(sb.toString());
        } catch (Exception ex) {
            detailArea.setText("加载失败: " + ex.getMessage());
        }
    }
}
