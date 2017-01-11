package com.edasaki.misakachan.gui;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.net.URI;

import com.edasaki.misakachan.Misaka;

public class GuiManager {

    private GUIFrame frame;

    public void showFrame() {
        frame = new GUIFrame();
        frame.setVisible(true);
        frame.setButtonListener((e) -> {
            launchBrowser();
        });
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new EnterKeyDispatcher());
    }

    private void launchBrowser() {
        String url = "http://127.0.0.1:" + Misaka.getInstance().spark.getPort();
        updateStatus("Navigating in web browser to " + url + "...");
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
                updateStatus("Successfully opened misakachan in browser!");
            } catch (Exception e) {
                e.printStackTrace();
                updateStatus("ERROR: Could not launch default browser.");
                updateStatus("To manually open misakachan, go to your web browser and open " + url);
            }
        } else {
            updateStatus("ERROR: Could not launch default browser.");
            updateStatus("To manually open misakachan, go to your web browser and open " + url);
        }
    }

    public GuiManager() {
    }

    public void updateStatus(String s) {
        frame.println(s);
    }

    protected class EnterKeyDispatcher implements KeyEventDispatcher {
        private long last;

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (System.currentTimeMillis() - last < 500)
                    return false;
                last = System.currentTimeMillis();
                launchBrowser();
            }
            return false;
        }
    }

}
