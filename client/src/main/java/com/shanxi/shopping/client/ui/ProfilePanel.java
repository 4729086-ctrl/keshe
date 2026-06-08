package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PG-12: 个人信息页
 */
public class ProfilePanel extends JPanel {

    private JLabel usernameLabel, emailLabel, phoneLabel;
    private JButton editBtn, pwdBtn, addrBtn, backBtn;

    public ProfilePanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("productList"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; info.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1; usernameLabel = new JLabel(); info.add(usernameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; info.add(new JLabel("邮箱："), gbc);
        gbc.gridx = 1; emailLabel = new JLabel(); info.add(emailLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; info.add(new JLabel("手机号："), gbc);
        gbc.gridx = 1; phoneLabel = new JLabel(); info.add(phoneLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        editBtn = new JButton("修改资料");
        editBtn.addActionListener(e -> showEditDialog());
        pwdBtn = new JButton("修改密码");
        pwdBtn.addActionListener(e -> showPwdDialog());
        addrBtn = new JButton("管理收货地址");
        addrBtn.addActionListener(e -> AppContext.getInstance().navigateTo("addressManage"));
        btns.add(editBtn); btns.add(pwdBtn); btns.add(addrBtn);
        info.add(btns, gbc);

        add(info, BorderLayout.CENTER);
    }

    public void refresh() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/user/profile"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            usernameLabel.setText((String) data.get("username"));
            emailLabel.setText(data.get("email") != null ? data.get("email").toString() : "未设置");
            phoneLabel.setText(data.get("phone") != null ? data.get("phone").toString() : "未设置");
        } catch (Exception ex) {
            usernameLabel.setText("加载失败");
        }
    }

    private void showEditDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "修改资料", true);
        dlg.setSize(300, 200); dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField emailF = new JTextField(), phoneF = new JTextField();
        form.add(new JLabel("邮箱：")); form.add(emailF);
        form.add(new JLabel("手机号：")); form.add(phoneF);
        JButton save = new JButton("保存");
        save.addActionListener(e -> {
            try {
                ApiClient.put("/api/user/profile", Map.of("email", emailF.getText(), "phone", phoneF.getText()));
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
                ApiClient.put("/api/user/password", Map.of("oldPassword", new String(oldF.getPassword()), "newPassword", np));
                dlg.dispose();
                JOptionPane.showMessageDialog(this, "密码修改成功");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, ex.getMessage()); }
        });
        form.add(save);
        dlg.add(form);
        dlg.setVisible(true);
    }
}
