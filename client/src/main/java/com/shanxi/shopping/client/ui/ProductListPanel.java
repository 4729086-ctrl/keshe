package com.shanxi.shopping.client.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-04: 商品列表 / 买家首页
 */
public class ProductListPanel extends JPanel {

    private JTextField searchField;
    private JButton searchBtn, cartBtn, orderBtn, profileBtn;
    private JPanel productPanel;
    private JScrollPane scrollPane;

    public ProductListPanel() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("搜索商品："));
        searchField = new JTextField(20);
        topBar.add(searchField);
        searchBtn = new JButton("搜索");
        searchBtn.addActionListener(e -> refresh());
        topBar.add(searchBtn);
        topBar.add(Box.createHorizontalStrut(30));
        cartBtn = new JButton("购物车");
        cartBtn.addActionListener(e -> AppContext.getInstance().navigateTo("cart"));
        topBar.add(cartBtn);
        orderBtn = new JButton("我的订单");
        orderBtn.addActionListener(e -> AppContext.getInstance().navigateTo("orderList"));
        topBar.add(orderBtn);
        profileBtn = new JButton("个人信息");
        profileBtn.addActionListener(e -> AppContext.getInstance().navigateTo("profile"));
        topBar.add(profileBtn);
        add(topBar, BorderLayout.NORTH);

        // 商品展示区
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(productPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        productPanel.removeAll();
        try {
            String keyword = searchField.getText().trim();
            String path = "/api/products?page=1&pageSize=50";
            if (!keyword.isEmpty()) path += "&keyword=" + keyword;

            // 直接用 HttpHelper
            String pathStr = "/api/products?page=1&pageSize=50";
            if (!keyword.isEmpty()) pathStr += "&keyword=" + keyword;
            Map<String, Object> result = HttpHelper.getMap(pathStr);
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

            if (list == null || list.isEmpty()) {
                productPanel.add(new JLabel("暂无商品", SwingConstants.CENTER));
            } else {
                for (Map<String, Object> p : list) {
                    productPanel.add(createProductCard(p));
                    productPanel.add(Box.createVerticalStrut(8));
                }
            }
        } catch (Exception ex) {
            productPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        productPanel.revalidate();
        productPanel.repaint();
    }

    private JPanel createProductCard(Map<String, Object> p) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setPreferredSize(new Dimension(800, 100));

        // 左侧：图片占位
        JLabel imgLabel = new JLabel("[图]");
        imgLabel.setPreferredSize(new Dimension(80, 80));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.add(imgLabel, BorderLayout.WEST);

        // 中间：商品信息
        JPanel info = new JPanel(new GridLayout(3, 1));
        info.add(new JLabel(p.get("name").toString()));
        info.add(new JLabel("¥" + p.get("price").toString()));
        info.add(new JLabel("销量: " + p.get("salesCount") + "  库存: " + p.get("stock")));
        card.add(info, BorderLayout.CENTER);

        // 右侧：按钮
        JButton detailBtn = new JButton("查看详情");
        Long productId = Long.valueOf(p.get("id").toString());
        detailBtn.addActionListener(e -> {
            AppContext.getInstance().setAttribute("productId", productId);
            AppContext.getInstance().navigateTo("productDetail");
        });
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.add(detailBtn);
        card.add(btnPanel, BorderLayout.EAST);

        return card;
    }
}
