package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.api.ApiClient;
import com.shanxi.shopping.client.api.HttpHelper;
import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * PG-08: 我的订单列表
 */
public class OrderListPanel extends JPanel {

    private JPanel orderPanel;
    private JButton backBtn;

    public OrderListPanel() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtn = new JButton("返回商品列表");
        backBtn.addActionListener(e -> AppContext.getInstance().navigateTo("productList"));
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        orderPanel = new JPanel();
        orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(orderPanel), BorderLayout.CENTER);
    }

    public void refresh() {
        orderPanel.removeAll();
        try {
            Map<String, Object> result = HttpHelper.getMap("/api/orders?page=1&pageSize=50");
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");

            if (list == null || list.isEmpty()) {
                orderPanel.add(new JLabel("暂无订单"));
            } else {
                for (Map<String, Object> o : list) {
                    orderPanel.add(createOrderCard(o));
                    orderPanel.add(Box.createVerticalStrut(5));
                }
            }
        } catch (Exception ex) {
            orderPanel.add(new JLabel("加载失败: " + ex.getMessage()));
        }
        orderPanel.revalidate();
        orderPanel.repaint();
    }

    private JPanel createOrderCard(Map<String, Object> o) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.add(new JLabel("订单号: " + o.get("orderNo") + "  金额: ¥" + o.get("totalAmount")));
        info.add(new JLabel("状态: " + o.get("statusDesc") + "  时间: " + o.get("createdAt")));
        card.add(info, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout());
        Long orderId = Long.valueOf(o.get("id").toString());
        int status = (int) o.get("status");

        JButton detailBtn = new JButton("详情");
        detailBtn.addActionListener(e -> {
            AppContext.getInstance().setAttribute("orderId", orderId);
            AppContext.getInstance().navigateTo("orderDetail");
        });
        btns.add(detailBtn);

        if (status == 1) {
            JButton payBtn = new JButton("付款");
            payBtn.addActionListener(e -> {
                try { ApiClient.put("/api/orders/" + orderId + "/pay"); refresh(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "付款失败: " + ex.getMessage()); }
            });
            btns.add(payBtn);
            JButton cancelBtn = new JButton("取消");
            cancelBtn.addActionListener(e -> {
                try { ApiClient.put("/api/orders/" + orderId + "/cancel"); refresh(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "取消失败: " + ex.getMessage()); }
            });
            btns.add(cancelBtn);
        }
        if (status == 3) {
            JButton receiveBtn = new JButton("确认收货");
            receiveBtn.addActionListener(e -> {
                try { ApiClient.put("/api/orders/" + orderId + "/receive"); refresh(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "收货失败: " + ex.getMessage()); }
            });
            btns.add(receiveBtn);
        }
        if (status == 4) {
            JButton returnBtn = new JButton("申请退货");
            returnBtn.addActionListener(e -> {
                AppContext.getInstance().navigateTo("returnApply");
            });
            btns.add(returnBtn);
        }
        card.add(btns, BorderLayout.EAST);
        return card;
    }
}
