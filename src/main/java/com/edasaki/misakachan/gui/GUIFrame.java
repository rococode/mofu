package com.edasaki.misakachan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.IOException;
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
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edasaki.misakachan.utils.FileUtils;

@SuppressWarnings("serial")
public class GUIFrame extends JFrame {
    private static final String ICON_URL = "/public/assets/gfx/icon256-t.png";

    protected final Logger logger = LoggerFactory.getLogger(GUIFrame.class);

    private JPanel consolePanel;
    private JScrollPane consoleScrollPane;
    private JTextArea consoleTextArea = new JTextArea();
    private JButton button = new JButton("Launch Browser");

    private void configureButton(JPanel panel) {

        //        button.setBorderPainted(false);
        button.setFocusPainted(false);
        //        button.setContentAreaFilled(false);
        button.setForeground(Color.decode("#D5654C"));
        button.setBackground(Color.decode("#F1E0BF"));

        Border line = new LineBorder(Color.decode("#96B0A3"), 20);
        Border innerline = new LineBorder(Color.decode("#8AA296"), 5);
        Border margin = new EmptyBorder(30, 0, 30, 0);
        Border compound = new CompoundBorder(line, new CompoundBorder(innerline, margin));
        Font f = FontManager.getFont("/public/assets/fonts/Ubuntu-Regular.ttf");
        button.setFont(f);
        button.setBorder(compound);
        panel.add(button, BorderLayout.PAGE_START);
    }

    private void configureConsole(JPanel panel) {
        consolePanel = new JPanel(new BorderLayout());
        consoleScrollPane = new JScrollPane(consoleTextArea);
        consoleScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        consoleTextArea.setEditable(false);
        Font f = FontManager.getFont("/public/assets/fonts/RobotoMono-Regular.ttf");
        f = f.deriveFont(Font.PLAIN, 12f);
        consoleTextArea.setFont(f);
        consoleTextArea.setBorder(BorderFactory.createEmptyBorder(5, 8, 0, 0));
        consolePanel.add(consoleScrollPane);

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "  Status  ");
        border.setTitlePosition(TitledBorder.TOP);
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitleColor(Color.decode("#4B291A"));
        f = FontManager.getFont("/public/assets/fonts/Ubuntu-Regular.ttf");
        f = f.deriveFont(Font.PLAIN, 16f);
        border.setTitleFont(f);
        consolePanel.setBorder(border);
        consolePanel.setBackground(Color.decode("#96B0A3"));
        panel.add(consolePanel);
    }

    public GUIFrame() {
        super("misakachan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImageIcon icon;
        try {
            icon = new ImageIcon(FileUtils.getResourceAsFile(ICON_URL).toURI().toURL());
            setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel panel = new JPanel(new BorderLayout());

        configureButton(panel);
        configureConsole(panel);

        panel.setPreferredSize(new Dimension(600, 300));
        panel.setBackground(Color.CYAN);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    public void setButtonListener(ActionListener listener) {
        button.addActionListener(listener);
    }

    public void println(String s) {
        StringBuilder sb = new StringBuilder();
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
        sb.append(consoleTextArea.getText());
        consoleTextArea.setText(sb.toString());
        consoleTextArea.setCaretPosition(0);
    }
}
