// DroneShowSetup.java
// Entry point: launches the Swing frame and uses DronePanel

import javax.swing.*;
import java.awt.*;

public class DroneShowSetup {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Drone Show Prototype");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Add the DronePanel (animation canvas)
            DronePanel panel = new DronePanel();
            frame.add(panel, BorderLayout.CENTER);

            // Play button to trigger animations
            JButton playButton = new JButton("Play");
            playButton.addActionListener(e -> panel.startNextAnimation());
            frame.add(playButton, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}