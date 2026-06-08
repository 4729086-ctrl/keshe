package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-01: 登录页面（买家/卖家共用）
 */
public class LoginPanel extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginBtn, toRegisterBtn, toSellerRegisterBtn;

    public LoginPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel title = new JLabel("鳝溪购物交易中心", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        JLabel subtitle = new JLabel("用户登录", SwingConstants.CENTER);
        subtitle.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridy = 1;
        add(subtitle, gbc);

        // 角色选择
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("登录身份："), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"买家", "卖家"});
        add(roleCombo, gbc);

        // 用户名
        gbc.gridy = 3; gbc.gridx = 0;
        add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // 密码
        gbc.gridy = 4; gbc.gridx = 0;
        add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // 登录按钮
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        loginBtn = new JButton("登录");
        loginBtn.setPreferredSize(new Dimension(200, 35));
        loginBtn.addActionListener(e -> doLogin());
        add(loginBtn, gbc);

        // 注册链接
        gbc.gridy = 6; gbc.gridwidth = 1;
        gbc.gridx = 0;
        toRegisterBtn = new JButton("注册买家账号");
        toRegisterBtn.setBorderPainted(false); toRegisterBtn.setContentAreaFilled(false);
        toRegisterBtn.setForeground(Color.BLUE);
        toRegisterBtn.addActionListener(e -> AppContext.getInstance().navigateTo("register"));
        add(toRegisterBtn, gbc);

        gbc.gridx = 1;
        toSellerRegisterBtn = new JButton("注册卖家账号");
        toSellerRegisterBtn.setBorderPainted(false); toSellerRegisterBtn.setContentAreaFilled(false);
        toSellerRegisterBtn.setForeground(Color.BLUE);
        toSellerRegisterBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerRegister"));
        add(toSellerRegisterBtn, gbc);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码");
            return;
        }

        try {
            String role = roleCombo.getSelectedIndex() == 0 ? "USER" : "SELLER";
            String apiPath = role.equals("USER") ? "/api/user/login" : "/api/seller/login";

            Map<String, Object> body = Map.of("username", username, "password", password);
            Map<String, Object> result = HttpHelper.postMap(apiPath, body);
            if ((int) result.get("code") != 200) {
                JOptionPane.showMessageDialog(this, result.get("message"));
                return;
            }

            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String token = (String) data.get("token");
            Long userId = Long.valueOf(data.get("userId").toString());
            String uname = (String) data.get("username");
            String shopName = (String) data.get("shopName");

            AppContext.getInstance().onLoginSuccess(token, userId, uname, role, shopName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "登录失败: " + ex.getMessage());
        }
    }
}
