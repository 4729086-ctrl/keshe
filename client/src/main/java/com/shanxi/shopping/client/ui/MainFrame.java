package com.shanxi.shopping.client.ui;

import com.shanxi.shopping.client.common.AppContext;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口 — CardLayout 管理页面切换
 *
 * 页面 key 对照：
 *   login, register, sellerRegister       (公共)
 *   productList, productDetail, cart,
 *   orderConfirm, orderList, orderDetail,
 *   returnApply, addressManage, profile    (买家)
 *   sellerProduct, sellerProductEdit,
 *   sellerOrder, sellerShip, sellerReturn,
 *   sellerStats, sellerShop               (卖家)
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel contentPane;

    // 所有页面面板
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private SellerRegisterPanel sellerRegisterPanel;
    private ProductListPanel productListPanel;
    private ProductDetailPanel productDetailPanel;
    private CartPanel cartPanel;
    private OrderConfirmPanel orderConfirmPanel;
    private OrderListPanel orderListPanel;
    private OrderDetailPanel orderDetailPanel;
    private ReturnPanel returnPanel;
    private AddressPanel addressPanel;
    private ProfilePanel profilePanel;
    private SellerProductPanel sellerProductPanel;
    private SellerProductEditPanel sellerProductEditPanel;
    private SellerOrderPanel sellerOrderPanel;
    private SellerShippingPanel sellerShippingPanel;
    private SellerReturnPanel sellerReturnPanel;
    private SellerStatsPanel sellerStatsPanel;
    private SellerShopPanel sellerShopPanel;

    private JMenuBar menuBar;
    private JMenu userMenu;
    private JMenuItem logoutItem;

    public MainFrame() {
        setTitle("鳝溪购物交易中心");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        setContentPane(contentPane);

        initPages();
        buildMenuBar();

        // 默认显示登录页
        cardLayout.show(contentPane, "login");
    }

    private void buildMenuBar() {
        menuBar = new JMenuBar();
        userMenu = new JMenu("用户");
        logoutItem = new JMenuItem("退出登录");
        logoutItem.addActionListener(e -> AppContext.getInstance().logout());
        userMenu.add(logoutItem);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);
        menuBar.setVisible(false);
    }

    private void initPages() {
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
        sellerRegisterPanel = new SellerRegisterPanel();
        productListPanel = new ProductListPanel();
        productDetailPanel = new ProductDetailPanel();
        cartPanel = new CartPanel();
        orderConfirmPanel = new OrderConfirmPanel();
        orderListPanel = new OrderListPanel();
        orderDetailPanel = new OrderDetailPanel();
        returnPanel = new ReturnPanel();
        addressPanel = new AddressPanel();
        profilePanel = new ProfilePanel();
        sellerProductPanel = new SellerProductPanel();
        sellerProductEditPanel = new SellerProductEditPanel();
        sellerOrderPanel = new SellerOrderPanel();
        sellerShippingPanel = new SellerShippingPanel();
        sellerReturnPanel = new SellerReturnPanel();
        sellerStatsPanel = new SellerStatsPanel();
        sellerShopPanel = new SellerShopPanel();

        // 公共
        contentPane.add(loginPanel, "login");
        contentPane.add(registerPanel, "register");
        contentPane.add(sellerRegisterPanel, "sellerRegister");
        // 买家
        contentPane.add(productListPanel, "productList");
        contentPane.add(productDetailPanel, "productDetail");
        contentPane.add(cartPanel, "cart");
        contentPane.add(orderConfirmPanel, "orderConfirm");
        contentPane.add(orderListPanel, "orderList");
        contentPane.add(orderDetailPanel, "orderDetail");
        contentPane.add(returnPanel, "returnApply");
        contentPane.add(addressPanel, "addressManage");
        contentPane.add(profilePanel, "profile");
        // 卖家
        contentPane.add(sellerProductPanel, "sellerProduct");
        contentPane.add(sellerProductEditPanel, "sellerProductEdit");
        contentPane.add(sellerOrderPanel, "sellerOrder");
        contentPane.add(sellerShippingPanel, "sellerShip");
        contentPane.add(sellerReturnPanel, "sellerReturn");
        contentPane.add(sellerStatsPanel, "sellerStats");
        contentPane.add(sellerShopPanel, "sellerShop");
    }

    /** 页面导航 */
    public void navigateTo(String pageKey) {
        // 进入页面前刷新数据
        refreshPage(pageKey);
        cardLayout.show(contentPane, pageKey);
    }

    private void refreshPage(String pageKey) {
        switch (pageKey) {
            case "productList" -> productListPanel.refresh();
            case "productDetail" -> {
                Long productId = AppContext.getInstance().getAttribute("productId");
                if (productId != null) productDetailPanel.loadProduct(productId);
            }
            case "cart" -> cartPanel.refresh();
            case "orderConfirm" -> orderConfirmPanel.refresh();
            case "orderList" -> orderListPanel.refresh();
            case "orderDetail" -> {
                Long orderId = AppContext.getInstance().getAttribute("orderId");
                if (orderId != null) orderDetailPanel.loadOrder(orderId);
            }
            case "addressManage" -> addressPanel.refresh();
            case "profile" -> profilePanel.refresh();
            case "sellerProduct" -> sellerProductPanel.refresh();
            case "sellerProductEdit" -> {
                Long productId = AppContext.getInstance().getAttribute("productId");
                if (productId != null) sellerProductEditPanel.loadProduct(productId);
            }
            case "sellerOrder" -> sellerOrderPanel.refresh();
            case "sellerShip" -> {
                Long orderId = AppContext.getInstance().getAttribute("orderId");
                if (orderId != null) sellerShippingPanel.loadOrder(orderId);
            }
            case "sellerStats" -> sellerStatsPanel.refresh();
            case "sellerShop" -> sellerShopPanel.refresh();
            case "sellerReturn" -> sellerReturnPanel.refresh();
            default -> {} // 不需要刷新
        }
    }

    /** 登录成功回调 */
    public void onLoginSuccess() {
        menuBar.setVisible(true);
        String role = AppContext.getInstance().getRole();
        if ("USER".equals(role)) {
            navigateTo("productList");
        } else {
            navigateTo("sellerProduct");
        }
    }

    /** 退出登录回调 */
    public void onLogout() {
        menuBar.setVisible(false);
        navigateTo("login");
    }
}
