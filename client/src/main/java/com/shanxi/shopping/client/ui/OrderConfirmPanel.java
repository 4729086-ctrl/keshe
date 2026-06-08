package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PG-07: 订单确认/结算页面
 */
public class OrderConfirmPanel extends JPanel {

    private JPanel summaryPanel;
    private JComboBox<String> addressCombo;
    private JLabel totalLabel;
    private JButton submitBtn, backBtn;
    private Map<Long, String> addressMap = new HashMap<>();
    private List<Long> checkedCartIds = new ArrayList<>();
    private double totalAmount = 0;

    public OrderConfirmPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回购物车");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("cart"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(summaryPanel), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.add(new JLabel("收货地址："));
        addressCombo = new JComboBox<>();
        bottomBar.add(addressCombo);
        totalLabel = new JLabel("合计: ¥0.00");
        bottomBar.add(totalLabel);
        submitBtn = new JButton("提交订单");
        submitBtn.addActionListener(e -> submitOrder());
        bottomBar.add(submitBtn);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void refresh() {
        summaryPanel.removeAll();
        addressCombo.removeAllItems();
        addressMap.clear();
        checkedCartIds.clear();
        totalAmount = 0;

        if (!AppContext.getInstance().isLoggedIn()) {
            summaryPanel.add(new JLabel("请先登录"));
            summaryPanel.revalidate();
            summaryPanel.repaint();
            return;
        }

        try {
            // 加载购物车勾选项
            Map<String, Object> result = HttpHelper.getMap("/api/cart");
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("data");

            if (list != null) {
                for (Map<String, Object> item : list) {
                    if ((int) item.get("checked") == 1) {
                        Long id = Long.valueOf(item.get("id").toString());
                        checkedCartIds.add(id);
                        double price = ((Number) item.get("price")).doubleValue();
                        int qty = (int) item.get("quantity");
                        totalAmount += price * qty;

                        JLabel line = new JLabel(item.get("productName") + "  x" + qty + "  ¥" + String.format("%.2f", price * qty));
                        summaryPanel.add(line);
                    }
                }
            }
            totalLabel.setText("合计: ¥" + String.format("%.2f", totalAmount));

            // 加载收货地址
            Map<String, Object> addrResult = HttpHelper.getMap("/api/user/addresses");
            List<Map<String, Object>> addrList = (List<Map<String, Object>>) addrResult.get("data");

            if (addrList != null) {
                for (Map<String, Object> addr : addrList) {
                    Long addrId = Long.valueOf(addr.get("id").toString());
                    String text = addr.get("receiverName") + " " + addr.get("receiverPhone") + " " + addr.get("detail");
                    addressMap.put(addrId, text);
                    addressCombo.addItem(text);
                }
            }
        } catch (Exception ex) {
            summaryPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private void submitOrder() {
        if (checkedCartIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有选中的商品"); return;
        }
        if (addressMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先添加收货地址"); return;
        }

        Long addressId = addressMap.keySet().toArray(new Long[0])[addressCombo.getSelectedIndex()];
        try {
            ApiClient.post("/api/orders", Map.of("addressId", addressId, "cartItemIds", checkedCartIds));
            JOptionPane.showMessageDialog(this, "下单成功！");
            AppContext.getInstance().navigateTo("orderList");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "下单失败: " + ex.getMessage());
        }
    }
}
