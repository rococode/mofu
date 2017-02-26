package com.edasaki.misakachan.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.edasaki.misakachan.utils.MFileUtils;
import com.edasaki.misakachan.utils.logging.M;

@SuppressWarnings("serial")
public class GUIFrame extends JFrame {
    private static final String ICON_URL = "/public/assets/gfx/icon256-t.png";

    private JPanel consolePanel;
    private JScrollPane consoleScrollPane;
    private JTextArea consoleTextArea = new JTextArea();
    private JButton button = new JButton("Launch Browser");

    private static TrayIcon trayIcon;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cleanup();
            }
        });
    }

    public static void cleanup() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }

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

        try {
            final ImageIcon icon = new ImageIcon(MFileUtils.getResourceAsFile(ICON_URL).toURI().toURL());
            setIconImage(icon.getImage());

            JPanel panel = new JPanel(new BorderLayout());

            configureButton(panel);
            configureConsole(panel);

            panel.setPreferredSize(new Dimension(600, 300));
            panel.setBackground(Color.CYAN);
            getContentPane().add(panel);
            pack();
            if (SystemTray.isSupported()) {
                addWindowStateListener(new WindowStateListener() {
                    public void windowStateChanged(WindowEvent e) {
                        M.edb("Captured: " + e.getNewState());
                        if (e.getNewState() == Frame.ICONIFIED) {
                            trayIcon = new TrayIcon(icon.getImage());
                            trayIcon.setImageAutoSize(true);
                            trayIcon.setToolTip("misakachan");
                            // Create a pop-up menu components
                            final PopupMenu popup = new PopupMenu();
                            MenuItem forumsItem = new MenuItem("Forums");
                            forumsItem.addActionListener((event) -> {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                                    try {
                                        Desktop.getDesktop().browse(new URI("http://misakachan.net"));
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            });
                            MenuItem githubItem = new MenuItem("GitHub");
                            githubItem.addActionListener((event) -> {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                                    try {
                                        Desktop.getDesktop().browse(new URI("https://github.com/edasaki/misakachan"));
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            });
                            MenuItem exitItem = new MenuItem("Exit");
                            exitItem.addActionListener((event) -> {
                                System.exit(0);
                            });
                            //Add components to pop-up menu
                            popup.add(forumsItem);
                            popup.add(githubItem);
                            popup.addSeparator();
                            popup.add(exitItem);
                            trayIcon.setPopupMenu(popup);
                            trayIcon.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    if (SwingUtilities.isLeftMouseButton(e)) {
                                        setVisible(true);
                                        SystemTray.getSystemTray().remove(trayIcon);
                                        setState(Frame.NORMAL);
                                        toFront();
                                        repaint();
                                    }
                                }
                            });
                            try {
                                SystemTray.getSystemTray().add(trayIcon);
                                setVisible(false);
                                try {
                                    Thread.sleep(100L);
                                    trayIcon.displayMessage("misaka alert", "misakachan is now minimized\nto the system tray!", MessageType.NONE);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            } catch (AWTException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                });
            }
            setLocationRelativeTo(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
