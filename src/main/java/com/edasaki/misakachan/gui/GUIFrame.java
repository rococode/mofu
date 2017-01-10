package com.edasaki.misakachan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edasaki.misakachan.Launcher;

@SuppressWarnings("serial")
public class GUIFrame extends JFrame {
    private static final String ICON_URL = "/public/assets/gfx/icon256-t.png";

    protected final Logger logger = LoggerFactory.getLogger(GUIFrame.class);

    private JPanel consolePanel;
    private JScrollPane consoleDisplay;
    private JTextArea console = new JTextArea();
    private JButton button = new JButton("Launch Browser");

    private void configureButton(JPanel panel) {

        //        button.setBorderPainted(false);
        button.setFocusPainted(false);
        //        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(50, -50, 50, -50);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
        panel.add(button, BorderLayout.PAGE_START);
    }

    private void configureConsole(JPanel panel) {
        consolePanel = new JPanel(new BorderLayout());
        consoleDisplay = new JScrollPane(console);
        consoleDisplay.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        console.setEditable(false);
        consolePanel.add(consoleDisplay);
        consolePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Status"));
        panel.add(consolePanel);
    }

    public GUIFrame() {
        super("misakachan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        URL iconURL = Launcher.class.getResource(ICON_URL);
        logger.debug(iconURL.toString());
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());

        JPanel panel = new JPanel(new BorderLayout());

        configureButton(panel);
        configureConsole(panel);

        panel.setPreferredSize(new Dimension(600, 300));
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public void setButtonListener(ActionListener listener) {
        button.addActionListener(listener);
    }

    public void println(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append(' ');
        LocalTime now = LocalTime.now();
        sb.append(String.format("%02d", now.getHour()));
        sb.append(':');
        sb.append(String.format("%02d", now.getMinute()));
        sb.append(':');
        sb.append(String.format("%02d", now.getSecond()));
        sb.append(' ');
        sb.append('>');
        sb.append('>');
        sb.append(' ');
        sb.append(s);
        sb.append('\n');
        sb.append(console.getText());
        console.setText(sb.toString());
        console.setCaretPosition(0);
    }
}
