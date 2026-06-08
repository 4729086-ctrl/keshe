package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-11: 地址管理页面
 */
public class AddressPanel extends JPanel {

    private JPanel addrListPanel;
    private JButton addBtn, backBtn;

    public AddressPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("profile"));
        addBtn = new JButton("新增地址");
        addBtn.addActionListener(e -> showAddDialog());
        topBar.add(backBtn);
        topBar.add(addBtn);
        add(topBar, BorderLayout.NORTH);

        addrListPanel = new JPanel();
        addrListPanel.setLayout(new BoxLayout(addrListPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(addrListPanel), BorderLayout.CENTER);
    }

    public void refresh() {
        addrListPanel.removeAll();
        try {
            ObjectMapper mapper = new ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/user/addresses"))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("data");

            if (list != null) {
                for (Map<String, Object> addr : list) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                    Long id = Long.valueOf(addr.get("id").toString());
                    String text = (Integer.valueOf(1).equals(addr.get("isDefault")) ? "[默认] " : "")
                            + addr.get("receiverName") + " " + addr.get("receiverPhone") + " "
                            + addr.get("province") + addr.get("city") + addr.get("district") + " " + addr.get("detail");
                    row.add(new JLabel(text));

                    JButton defaultBtn = new JButton("设为默认");
                    defaultBtn.addActionListener(e -> {
                        try { ApiClient.put("/api/user/addresses/" + id + "/default", Map.of()); refresh(); }
                        catch (Exception ex) { ex.printStackTrace(); }
                    });
                    row.add(defaultBtn);

                    JButton delBtn = new JButton("删除");
                    delBtn.addActionListener(e -> {
                        try { ApiClient.delete("/api/user/addresses/" + id); refresh(); }
                        catch (Exception ex) { ex.printStackTrace(); }
                    });
                    row.add(delBtn);
                    addrListPanel.add(row);
                }
            }
        } catch (Exception ex) {
            addrListPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        addrListPanel.revalidate();
        addrListPanel.repaint();
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "新增地址", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(8, 2, 5, 5));
        JTextField nameF = new JTextField(), phoneF = new JTextField(),
                provinceF = new JTextField(), cityF = new JTextField(),
                districtF = new JTextField(), detailF = new JTextField();
        JCheckBox defaultCb = new JCheckBox("默认地址");

        form.add(new JLabel("收货人：")); form.add(nameF);
        form.add(new JLabel("电话：")); form.add(phoneF);
        form.add(new JLabel("省：")); form.add(provinceF);
        form.add(new JLabel("市：")); form.add(cityF);
        form.add(new JLabel("区/县：")); form.add(districtF);
        form.add(new JLabel("详细地址：")); form.add(detailF);
        form.add(new JLabel()); form.add(defaultCb);

        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(e -> {
            try {
                ApiClient.post("/api/user/addresses", Map.of(
                        "receiverName", nameF.getText(), "receiverPhone", phoneF.getText(),
                        "province", provinceF.getText(), "city", cityF.getText(),
                        "district", districtF.getText(), "detail", detailF.getText(),
                        "isDefault", defaultCb.isSelected() ? 1 : 0
                ));
                dialog.dispose();
                refresh();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "保存失败: " + ex.getMessage()); }
        });
        form.add(saveBtn);
        dialog.add(form);
        dialog.setVisible(true);
    }
}
