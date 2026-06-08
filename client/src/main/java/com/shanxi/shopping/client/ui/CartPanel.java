package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * PG-06: 购物车页面
 */
public class CartPanel extends JPanel {

    private JPanel itemPanel;
    private JLabel totalLabel;
    private JButton settleBtn, backBtn, selectAllBtn, deleteBtn;

    public CartPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("productList"));
        selectAllBtn = new JButton("全选/取消");
        selectAllBtn.addActionListener(e -> toggleSelectAll());
        deleteBtn = new JButton("删除选中");
        deleteBtn.addActionListener(e -> deleteSelected());
        topBar.add(backBtn);
        topBar.add(selectAllBtn);
        topBar.add(deleteBtn);
        add(topBar, BorderLayout.NORTH);

        itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(itemPanel), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("合计: ¥0.00");
        settleBtn = new JButton("去结算");
        settleBtn.addActionListener(e -> doSettle());
        bottomBar.add(totalLabel);
        bottomBar.add(settleBtn);
        add(bottomBar, BorderLayout.SOUTH);
    }

    public void refresh() {
        itemPanel.removeAll();
        try {
            Map<String, Object> result = HttpHelper.getMap("/api/cart");
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("data");

            double total = 0;
            if (list != null) {
                for (Map<String, Object> item : list) {
                    JPanel row = createCartRow(item);
                    itemPanel.add(row);
                    if ((int) item.get("checked") == 1) {
                        total += ((Number) item.get("price")).doubleValue() * (int) item.get("quantity");
                    }
                }
            }
            totalLabel.setText("合计: ¥" + String.format("%.2f", total));
        } catch (Exception ex) {
            itemPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        itemPanel.revalidate();
        itemPanel.repaint();
    }

    private JPanel createCartRow(Map<String, Object> item) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        Long id = Long.valueOf(item.get("id").toString());
        JCheckBox cb = new JCheckBox("", (int) item.get("checked") == 1);
        cb.addActionListener(e -> {
            try { ApiClient.put("/api/cart/" + id + "/checked", Map.of("checked", cb.isSelected())); refresh(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        row.add(cb);
        row.add(new JLabel(item.get("productName").toString()));
        row.add(new JLabel("¥" + item.get("price")));
        row.add(new JLabel("x" + item.get("quantity")));
        row.add(new JLabel("库存: " + item.get("stock")));

        JButton delBtn = new JButton("删除");
        delBtn.addActionListener(e -> {
            try { ApiClient.delete("/api/cart/" + id); refresh(); }
            catch (Exception ex) { ex.printStackTrace(); }
        });
        row.add(delBtn);
        return row;
    }

    private void toggleSelectAll() {
        try {
            ApiClient.put("/api/cart/check-all", Map.of("checked", true));
            refresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void deleteSelected() {
        try {
            Map<String, Object> result = HttpHelper.getMap("/api/cart");
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("data");

            List<Long> toDelete = new ArrayList<>();
            if (list != null) {
                for (Map<String, Object> item : list) {
                    if ((int) item.get("checked") == 1) {
                        toDelete.add(Long.valueOf(item.get("id").toString()));
                    }
                }
            }
            if (!toDelete.isEmpty()) {
                ApiClient.delete("/api/cart/batch", Map.of("ids", toDelete));
            }
            refresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void doSettle() {
        AppContext.getInstance().navigateTo("orderConfirm");
    }
}
