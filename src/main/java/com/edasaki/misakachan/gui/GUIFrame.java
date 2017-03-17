package com.edasaki.misakachan.gui;

import com.edasaki.misakachan.utils.MFileUtils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.Desktop.Action;
import java.awt.TrayIcon.MessageType;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public final class GUIFrame extends JFrame {
    private static final String ICON_URL = "/public/assets/gfx/icon256-t.png";

    private JTextArea consoleTextArea = new JTextArea();
    private JButton button = new JButton("Launch Browser");

    private static TrayIcon trayIcon;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(GUIFrame::cleanup));
    }

    private static void cleanup() {
        if (trayIcon != null) {
            SystemTray.getSystemTray().remove(trayIcon);
        }
    }

    protected void setButtonListener(ActionListener listener) {
        button.addActionListener(listener);
    }

    protected void println(String s) {
        StringBuilder sb = new StringBuilder();
        LocalTime now = LocalTime.now();
        sb.append(String.format("%02d", now.getHour()));
        sb.append(':');
        sb.append(String.format("%02d", now.getMinute()));
        sb.append(':');
        sb.append(String.format("%02d", now.getSecond()));
        sb.append('.');
        sb.append(String.format("%03d", TimeUnit.NANOSECONDS.toMillis(now.getNano())));
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

    private void configureButton(JPanel panel) {
        button.setFocusPainted(false);
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
        JPanel consolePanel = new JPanel(new BorderLayout());
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
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

    private void configurePhantomWarning(JPanel panel) {
        JPanel jsPanel = new JPanel(new BorderLayout());
        JTextArea text = new JTextArea();
        Font f = FontManager.getFont("/public/assets/fonts/RobotoMono-Regular.ttf");
        f = f.deriveFont(Font.PLAIN, 10f);
        text.setFont(f);
        Border border = BorderFactory.createEmptyBorder(5, 8, 5, 8);
        text.setBorder(border);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setText("NOTE: You may be asked for permission to run \"PhantomJS.exe\". It sounds super sketchy, I know. It's actually a pretty big open-source project that's essentially a web browser without a UI. misakachan uses it to access some sites like KissManga. You can read more about it at https://github.com/ariya/phantomjs (click here to open in browser).");
        text.addMouseListener(new BrowseMouseAction("https://github.com/ariya/phantomjs", 2000L));
        text.setBackground(Color.decode("#96B0A3"));
        text.setEnabled(false);
        text.setEditable(false);
        text.setHighlighter(null);
        text.setDisabledTextColor(Color.decode("#555555"));
        jsPanel.add(text);
        panel.add(jsPanel, BorderLayout.PAGE_END);
    }

    public GUIFrame() {
        super("misakachan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            // Set icon
            final ImageIcon icon = new ImageIcon(MFileUtils.getResourceAsFile(ICON_URL).toURI().toURL());
            setIconImage(icon.getImage());
            // Build panel
            JPanel panel = new JPanel(new BorderLayout());
            configureButton(panel);
            configureConsole(panel);
            configurePhantomWarning(panel);
            panel.setPreferredSize(new Dimension(600, 375));
            getContentPane().add(panel);
            pack();

            // Support for minimize to tray
            if (SystemTray.isSupported()) {
                addWindowStateListener(new WindowStateListener() {
                    public void windowStateChanged(WindowEvent e) {
                        // Move to tray icon
                        if (e.getNewState() == Frame.ICONIFIED) {
                            trayIcon = new TrayIcon(icon.getImage());
                            trayIcon.setImageAutoSize(true);
                            trayIcon.setToolTip("misakachan");
                            // Initialize menu
                            final PopupMenu popup = new PopupMenu();
                            // Forums link
                            MenuItem forumsItem = new MenuItem("Forums");
                            forumsItem.addActionListener(new BrowseAction("http://misakachan.net"));
                            popup.add(forumsItem);
                            // GitHub link
                            MenuItem githubItem = new MenuItem("GitHub");
                            githubItem.addActionListener(new BrowseAction("https://github.com/edasaki/misakachan"));
                            popup.add(githubItem);
                            // Divider
                            popup.addSeparator();
                            // Exit
                            MenuItem exitItem = new MenuItem("Exit");
                            exitItem.addActionListener((event) -> {
                                System.exit(0);
                            });
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

    private static final class BrowseAction implements ActionListener {
        private String url;

        private BrowseAction(String url) {
            this.url = url;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class BrowseMouseAction implements MouseListener {
        private String url;
        private long delay;
        private long last;

        private BrowseMouseAction(String url, long delay) {
            this.url = url;
            this.delay = delay;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (System.currentTimeMillis() - last < delay)
                return;
            last = System.currentTimeMillis();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
