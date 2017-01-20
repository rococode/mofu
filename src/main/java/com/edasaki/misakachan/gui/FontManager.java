package com.edasaki.misakachan.gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import com.edasaki.misakachan.utils.MFileUtils;

public class FontManager {
    private static HashMap<String, Font> loadedFonts = new HashMap<String, Font>();

    public static Font getFont(String path) {
        if (loadedFonts.containsKey(path))
            return loadedFonts.get(path);
        try {
            File file = MFileUtils.getResourceAsFile(path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, file);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Hashtable<TextAttribute, Object> attr = new Hashtable<TextAttribute, Object>();
            attr.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            attr.put(TextAttribute.SIZE, 28f);
            attr.put(TextAttribute.TRACKING, 0);
            font = font.deriveFont(attr);
            ge.registerFont(font);
            loadedFonts.put(path, font);
            return font;
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
