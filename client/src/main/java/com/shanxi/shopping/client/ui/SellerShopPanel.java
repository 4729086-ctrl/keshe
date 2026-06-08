package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-19: 店铺信息页面
 */
public class SellerShopPanel extends JPanel {

    private JLabel shopNameLabel, usernameLabel, phoneLabel, emailLabel, descLabel;
    private JButton editBtn, pwdBtn, backBtn;

    public SellerShopPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerProduct"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; info.add(new JLabel("店铺名称："), gbc);
        gbc.gridx = 1; shopNameLabel = new JLabel(); info.add(shopNameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; info.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1; usernameLabel = new JLabel(); info.add(usernameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; info.add(new JLabel("手机号："), gbc);
        gbc.gridx = 1; phoneLabel = new JLabel(); info.add(phoneLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; info.add(new JLabel("邮箱："), gbc);
        gbc.gridx = 1; emailLabel = new JLabel(); info.add(emailLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; info.add(new JLabel("简介："), gbc);
        gbc.gridx = 1; descLabel = new JLabel(); info.add(descLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        editBtn = new JButton("修改信息");
        editBtn.addActionListener(e -> showEditDialog());
        pwdBtn = new JButton("修改密码");
        pwdBtn.addActionListener(e -> showPwdDialog());
        btns.add(editBtn); btns.add(pwdBtn);
        info.add(btns, gbc);

        add(info, BorderLayout.CENTER);
    }

    public void refresh() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/seller/profile"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            shopNameLabel.setText((String) data.get("shopName"));
            usernameLabel.setText((String) data.get("username"));
            phoneLabel.setText((String) data.get("phone"));
            emailLabel.setText(data.get("email") != null ? data.get("email").toString() : "未设置");
            descLabel.setText(data.get("description") != null ? data.get("description").toString() : "暂无简介");
        } catch (Exception ex) {
            shopNameLabel.setText("加载失败");
        }
    }

    private void showEditDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改店铺信息", true);
        dlg.setSize(350, 250); dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField nameF = new JTextField(), phoneF = new JTextField(), emailF = new JTextField(), descF = new JTextField();
        form.add(new JLabel("店铺名称：")); form.add(nameF);
        form.add(new JLabel("手机号：")); form.add(phoneF);
        form.add(new JLabel("邮箱：")); form.add(emailF);
        form.add(new JLabel("简介：")); form.add(descF);
        JButton save = new JButton("保存");
        save.addActionListener(e -> {
            try {
                ApiClient.put("/api/seller/profile", Map.of(
                        "shopName", nameF.getText(), "phone", phoneF.getText(),
                        "email", emailF.getText(), "description", descF.getText()
                ));
                dlg.dispose(); refresh();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, ex.getMessage()); }
        });
        form.add(save);
        dlg.add(form);
        dlg.setVisible(true);
    }

    private void showPwdDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改密码", true);
        dlg.setSize(300, 200); dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        JPasswordField oldF = new JPasswordField(), newF = new JPasswordField(), confirmF = new JPasswordField();
        form.add(new JLabel("原密码：")); form.add(oldF);
        form.add(new JLabel("新密码：")); form.add(newF);
        form.add(new JLabel("确认密码：")); form.add(confirmF);
        JButton save = new JButton("保存");
        save.addActionListener(e -> {
            String np = new String(newF.getPassword());
            if (!np.equals(new String(confirmF.getPassword()))) {
                JOptionPane.showMessageDialog(dlg, "两次密码不一致"); return;
            }
            try {
                ApiClient.put("/api/seller/password", Map.of("oldPassword", new String(oldF.getPassword()), "newPassword", np));
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "密码修改成功");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, ex.getMessage()); }
        });
        form.add(save);
        dlg.add(form);
        dlg.setVisible(true);
    }
}
