package org.dpytel.intellij.plugin.maventest.text;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 */
public class TextBundle {

    private static ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle");

    public static String getText(String key, Object... params) {
        MessageFormat formatter = new MessageFormat("");
        formatter.applyPattern(messages.getString(key));
        return formatter.format(params);
    }
}
