package se.iths.rami.webshop;

public class TestHandler extends JOptionPaneOutInputHandler {

    public String[] promptInput;
    public String infoMessage;
    public String errorMessage;
    public Object[][] testData;
    private int promptCount = 0;

    @Override
    public String[] prompt(String title, String[] label, String[] values) {
        if (promptCount++ == 0) {
            return promptInput;
        }
        return null;
    }

    @Override
    public void info(String message, String title) {
        infoMessage = message;
    }

    @Override
    public void errorInfo(String message) {
        errorMessage = message;
    }

    @Override
    public void showTable(String title, String[] columns, Object[][] data) {
        testData = data;
    }
}
