package se.iths.rami.webshop;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            ProductStorage storage = new ProductFileStorage();
            OutInputHandler outInputHandler = new JOptionPaneOutInputHandler();
            WebShopManager webShopManager = new WebShopManager(storage, outInputHandler);

            webShopManager.startWebshop();
        });
    }
}
