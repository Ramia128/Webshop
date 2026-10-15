package se.iths.rami.webshop;

public interface OutInputHandler {

    String menu();

    void theme();

    void info(String message, String title);

    void errorInfo(String message);

    String[] prompt(String title, String[] label, String[] values);

    void showTable(String title, String[] columns, Object[][] data);
}
