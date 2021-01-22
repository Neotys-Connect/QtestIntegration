package com.neotys.qtest.context.common;

import javax.swing.*;
import java.net.URL;

public class QtestUtils {

    private static final ImageIcon Qtest_ICON;
    private static final String IMAGE_NAME="qTest_logo_15px.png";

    static {
        final URL iconURL = QtestUtils.class.getResource(IMAGE_NAME);
        if (iconURL != null) {
            Qtest_ICON = new ImageIcon(iconURL);
        } else {
            Qtest_ICON = null;
        }
    }

    public QtestUtils() {
    }

    public static ImageIcon getQtest_ICON() {
        return Qtest_ICON;
    }
}
