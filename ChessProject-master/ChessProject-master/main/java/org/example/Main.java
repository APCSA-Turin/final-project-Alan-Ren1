package org.example;
import javax.swing.*;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DisplayPanel panel = new DisplayPanel();
        frame.add(panel);
        frame.pack();

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        panel.launchGame();
    }
}
