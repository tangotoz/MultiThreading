package com.tango.experiment.client;

import javax.swing.*;
import com.tango.experiment.client.GUI.LoginFrame;
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
