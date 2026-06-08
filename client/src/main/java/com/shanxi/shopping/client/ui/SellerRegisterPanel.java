package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-03: 卖家注册页面
 */
public class SellerRegisterPanel extends JPanel {

    private JTextField usernameField, shopNameField, phoneField, emailField;
    private JPasswordField passwordField, confirmField;
    private JButton registerBtn, backBtn;

    public SellerRegisterPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("卖家注册", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; add(new JLabel("用户名："), gbc);
        gbc.gridx = 1; usernameField = new JTextField(15); add(usernameField, gbc);
        gbc.gridy = 2; gbc.gridx = 0; add(new JLabel("密码："), gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(15); add(passwordField, gbc);
        gbc.gridy = 3; gbc.gridx = 0; add(new JLabel("确认密码："), gbc);
        gbc.gridx = 1; confirmField = new JPasswordField(15); add(confirmField, gbc);
        gbc.gridy = 4; gbc.gridx = 0; add(new JLabel("店铺名称："), gbc);
        gbc.gridx = 1; shopNameField = new JTextField(15); add(shopNameField, gbc);
        gbc.gridy = 5; gbc.gridx = 0; add(new JLabel("手机号："), gbc);
        gbc.gridx = 1; phoneField = new JTextField(15); add(phoneField, gbc);
        gbc.gridy = 6; gbc.gridx = 0; add(new JLabel("邮箱："), gbc);
        gbc.gridx = 1; emailField = new JTextField(15); add(emailField, gbc);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        registerBtn = new JButton("注册");
        registerBtn.addActionListener(e -> doRegister());
        backBtn = new JButton("返回登录");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("login"));
        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);
        add(btnPanel, gbc);
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String shopName = shopNameField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || shopName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "带 * 项不能为空"); return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致"); return;
        }

        try {
            Map<String, Object> body = Map.of(
                    "username", username, "password", password,
                    "shopName", shopName, "phone", phone,
                    "email", emailField.getText().trim()
            );
            ApiClient.post("/api/seller/register", body);
            JOptionPane.showMessageDialog(this, "注册成功！请登录");
            AppContext.getInstance().navigateTo("login");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "注册失败: " + ex.getMessage());
        }
    }
}
