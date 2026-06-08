package com.shanxi.shopping.client;

import com.shanxi.shopping.client.common.AppContext;
import com.shanxi.shopping.client.ui.MainFrame;

import javax.swing.*;

/**
 * 鳝溪购物交易中心 — Swing 客户端入口
 */
public class ShoppingClient {

    public static void main(String[] args) {
        // 设置 FlatLaf 或系统外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            AppContext.getInstance().setMainFrame(frame);
            frame.setVisible(true);
        });
    }
}
