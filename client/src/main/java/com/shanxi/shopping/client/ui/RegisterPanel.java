package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-02: 买家注册页面
 */
public class RegisterPanel extends JPanel {

    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmField;
    private JButton registerBtn, backBtn;

    public RegisterPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("买家注册", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        String[] labels = {"用户名：", "密码：", "确认密码：", "邮箱：", "手机号："};
        JTextField[] fields;
        gbc.gridy = 1; gbc.gridx = 0; add(new JLabel(labels[0]), gbc);
        gbc.gridx = 1; usernameField = new JTextField(15); add(usernameField, gbc);
        gbc.gridy = 2; gbc.gridx = 0; add(new JLabel(labels[1]), gbc);
        gbc.gridx = 1; passwordField = new JPasswordField(15); add(passwordField, gbc);
        gbc.gridy = 3; gbc.gridx = 0; add(new JLabel(labels[2]), gbc);
        gbc.gridx = 1; confirmField = new JPasswordField(15); add(confirmField, gbc);
        gbc.gridy = 4; gbc.gridx = 0; add(new JLabel(labels[3]), gbc);
        gbc.gridx = 1; emailField = new JTextField(15); add(emailField, gbc);
        gbc.gridy = 5; gbc.gridx = 0; add(new JLabel(labels[4]), gbc);
        gbc.gridx = 1; phoneField = new JTextField(15); add(phoneField, gbc);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
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

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空"); return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "两次密码不一致"); return;
        }

        try {
            Map<String, Object> body = Map.of(
                    "username", username, "password", password,
                    "email", emailField.getText().trim(),
                    "phone", phoneField.getText().trim()
            );
            ApiClient.post("/api/user/register", body);
            JOptionPane.showMessageDialog(this, "注册成功！请登录");
            AppContext.getInstance().navigateTo("login");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "注册失败: " + ex.getMessage());
        }
    }
}
