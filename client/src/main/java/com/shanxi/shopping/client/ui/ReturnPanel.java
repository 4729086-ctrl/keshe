package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-10: 退货申请页面
 */
public class ReturnPanel extends JPanel {

    private JTextField orderIdField;
    private JTextArea reasonArea;
    private JButton submitBtn, backBtn;

    public ReturnPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回订单列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("orderList"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("订单ID："), gbc);
        gbc.gridx = 1; orderIdField = new JTextField(10); form.add(orderIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("退货原因："), gbc);
        gbc.gridx = 1; reasonArea = new JTextArea(4, 20);
        form.add(new JScrollPane(reasonArea), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        submitBtn = new JButton("提交退货申请");
        submitBtn.addActionListener(e -> submitReturn());
        form.add(submitBtn, gbc);

        add(form, BorderLayout.CENTER);
    }

    private void submitReturn() {
        String orderId = orderIdField.getText().trim();
        String reason = reasonArea.getText().trim();
        if (orderId.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整信息"); return;
        }
        try {
            ApiClient.post("/api/orders/" + orderId + "/return", Map.of("reason", reason));
            JOptionPane.showMessageDialog(this, "退货申请已提交");
            AppContext.getInstance().navigateTo("orderList");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "申请失败: " + ex.getMessage());
        }
    }
}
