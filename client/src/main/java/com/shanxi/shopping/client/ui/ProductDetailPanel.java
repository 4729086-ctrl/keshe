package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-05: 商品详情页
 */
public class ProductDetailPanel extends JPanel {

    private JLabel nameLabel, priceLabel, stockLabel, salesLabel, shopLabel, descLabel;
    private JSpinner quantitySpinner;
    private JButton addCartBtn, backBtn;
    private Long currentProductId;

    public ProductDetailPanel() {
        setLayout(new BorderLayout());

        // 返回按钮
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("productList"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // 商品信息
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; infoPanel.add(new JLabel("商品名称："), gbc);
        gbc.gridx = 1; nameLabel = new JLabel(); infoPanel.add(nameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; infoPanel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1; priceLabel = new JLabel(); infoPanel.add(priceLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2; infoPanel.add(new JLabel("库存："), gbc);
        gbc.gridx = 1; stockLabel = new JLabel(); infoPanel.add(stockLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; infoPanel.add(new JLabel("销量："), gbc);
        gbc.gridx = 1; salesLabel = new JLabel(); infoPanel.add(salesLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4; infoPanel.add(new JLabel("店铺："), gbc);
        gbc.gridx = 1; shopLabel = new JLabel(); infoPanel.add(shopLabel, gbc);

        // 数量选择
        gbc.gridx = 0; gbc.gridy = 5; infoPanel.add(new JLabel("数量："), gbc);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        gbc.gridx = 1; infoPanel.add(quantitySpinner, gbc);

        // 按钮
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        addCartBtn = new JButton("加入购物车");
        addCartBtn.addActionListener(e -> addToCart());
        btnPanel.add(addCartBtn);
        infoPanel.add(btnPanel, gbc);

        add(infoPanel, BorderLayout.CENTER);

        // 描述
        descLabel = new JLabel();
        descLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane descScroll = new JScrollPane(descLabel);
        descScroll.setPreferredSize(new Dimension(300, 200));
        descScroll.setBorder(BorderFactory.createTitledBorder("商品描述"));
        add(descScroll, BorderLayout.SOUTH);
    }

    public void loadProduct(Long productId) {
        this.currentProductId = productId;
        try {
            Map<String, Object> result = HttpHelper.getMap("/api/products/" + productId);
            Map<String, Object> data = (Map<String, Object>) result.get("data");

            nameLabel.setText((String) data.get("name"));
            priceLabel.setText("¥" + data.get("price"));
            stockLabel.setText(String.valueOf(data.get("stock")));
            salesLabel.setText(String.valueOf(data.get("salesCount")));
            shopLabel.setText((String) data.get("shopName"));
            descLabel.setText("<html>" + data.get("description") + "</html>");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载商品详情失败: " + ex.getMessage());
        }
    }

    private void addToCart() {
        if (currentProductId == null) return;
        int qty = (int) quantitySpinner.getValue();
        try {
            ApiClient.post("/api/cart", Map.of("productId", currentProductId, "quantity", qty));
            JOptionPane.showMessageDialog(this, "已加入购物车");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "添加失败: " + ex.getMessage());
        }
    }
}
