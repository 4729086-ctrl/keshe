package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-16: 发货处理页面
 */
public class SellerShippingPanel extends JPanel {

    private JTextField orderIdField, companyField, logisticsNoField;
    private JButton shipBtn, backBtn;

    public SellerShippingPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回订单管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerOrder"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("订单ID："), gbc);
        gbc.gridx = 1; orderIdField = new JTextField(10); form.add(orderIdField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("物流公司："), gbc);
        gbc.gridx = 1; companyField = new JTextField(15); form.add(companyField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("物流单号："), gbc);
        gbc.gridx = 1; logisticsNoField = new JTextField(15); form.add(logisticsNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        shipBtn = new JButton("确认发货");
        shipBtn.addActionListener(e -> doShip());
        form.add(shipBtn, gbc);

        add(form, BorderLayout.CENTER);
    }

    public void loadOrder(Long orderId) {
        orderIdField.setText(String.valueOf(orderId));
    }

    private void doShip() {
        String orderId = orderIdField.getText().trim();
        String company = companyField.getText().trim();
        String no = logisticsNoField.getText().trim();
        if (orderId.isEmpty() || company.isEmpty() || no.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整信息"); return;
        }
        try {
            ApiClient.put("/api/seller/orders/" + orderId + "/ship", Map.of("logisticsCompany", company, "logisticsNo", no));
            JOptionPane.showMessageDialog(this, "发货成功");
            AppContext.getInstance().navigateTo("sellerOrder");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "发货失败: " + ex.getMessage());
        }
    }
}
