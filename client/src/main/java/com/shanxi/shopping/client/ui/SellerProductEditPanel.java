package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * PG-14: 商品新增/编辑表单
 */
public class SellerProductEditPanel extends JPanel {

    private JTextField nameField, priceField, stockField, categoryField, imageField;
    private JTextArea descArea;
    private JButton saveBtn, backBtn;
    private Long editProductId = null;

    public SellerProductEditPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品管理");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("sellerProduct"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("商品名称："), gbc);
        gbc.gridx = 1; nameField = new JTextField(20); form.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("分类ID："), gbc);
        gbc.gridx = 1; categoryField = new JTextField(10); form.add(categoryField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("价格："), gbc);
        gbc.gridx = 1; priceField = new JTextField(10); form.add(priceField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("库存："), gbc);
        gbc.gridx = 1; stockField = new JTextField(10); form.add(stockField, gbc);
        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("主图URL："), gbc);
        gbc.gridx = 1; imageField = new JTextField(20); form.add(imageField, gbc);
        gbc.gridx = 0; gbc.gridy = 5; form.add(new JLabel("描述："), gbc);
        gbc.gridx = 1; descArea = new JTextArea(4, 20);
        form.add(new JScrollPane(descArea), gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        saveBtn = new JButton("保存商品");
        saveBtn.addActionListener(e -> saveProduct());
        form.add(saveBtn, gbc);

        add(form, BorderLayout.CENTER);
    }

    public void loadProduct(Long productId) {
        this.editProductId = productId;
        // 加载已有商品信息并填充表单
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.net.http.HttpRequest req = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("http://localhost:8080/api/products/" + productId))
                    .header("Authorization", "Bearer " + AppContext.getInstance().getToken())
                    .GET().build();
            java.net.http.HttpResponse<String> resp = java.net.http.HttpClient.newHttpClient()
                    .send(req, java.net.http.HttpResponse.BodyHandlers.ofString());
            Map<String, Object> result = mapper.readValue(resp.body(), Map.class);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            nameField.setText((String) data.get("name"));
            categoryField.setText(String.valueOf(data.get("categoryId")));
            priceField.setText(String.valueOf(data.get("price")));
            stockField.setText(String.valueOf(data.get("stock")));
            imageField.setText(data.get("mainImage") != null ? data.get("mainImage").toString() : "");
            descArea.setText(data.get("description") != null ? data.get("description").toString() : "");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载商品信息失败: " + ex.getMessage());
        }
    }

    private void saveProduct() {
        try {
            Map<String, Object> body = Map.of(
                    "name", nameField.getText(),
                    "categoryId", Long.parseLong(categoryField.getText()),
                    "price", new BigDecimal(priceField.getText()),
                    "stock", Integer.parseInt(stockField.getText()),
                    "mainImage", imageField.getText(),
                    "description", descArea.getText(),
                    "status", 1,
                    "images", new ArrayList<String>()
            );
            if (editProductId != null) {
                ApiClient.put("/api/seller/products/" + editProductId, body);
            } else {
                ApiClient.post("/api/seller/products", body);
            }
            JOptionPane.showMessageDialog(this, "保存成功");
            AppContext.getInstance().navigateTo("sellerProduct");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "保存失败: " + ex.getMessage());
        }
    }
}
