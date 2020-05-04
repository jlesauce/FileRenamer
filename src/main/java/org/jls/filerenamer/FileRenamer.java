/*
 * MIT License
 *
 * Copyright 2020 Julien LE SAUCE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.jls.filerenamer;

import java.io.File;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.jls.filerenamer.util.ResourceManager;

public class FileRenamer {

    private static final String LOG4J_SYSTEM_PROPERTY_KEY = "log4j.configurationFile";

    public static void main(final String[] args) {
        configureLogger();
        setNimbusLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ApplicationModel model = new ApplicationModel();
                ApplicationController controller = new ApplicationController(model);
                controller.showGui();
            }
        });
    }

    private static void configureLogger() {
        if (!isLoggerConfigurationFileFound()) {
            setLog4jConfigurationSystemProperty();
        }
        LogManager.getLogger().info("log4j configuration file set : {}", System.getProperty(LOG4J_SYSTEM_PROPERTY_KEY));
    }

    private static boolean isLoggerConfigurationFileFound() {
        return System.getProperty(LOG4J_SYSTEM_PROPERTY_KEY) != null;
    }

    private static void setLog4jConfigurationSystemProperty() {
        URL url = getLog4jConfigurationFileUrl();
        System.setProperty(LOG4J_SYSTEM_PROPERTY_KEY, url.getFile());
    }

    private static URL getLog4jConfigurationFileUrl() {
        String path = ResourceManager.LOG4J_FILE;
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) {
            path = ResourceManager.RESOURCES_DIR + File.separator + ResourceManager.LOG4J_FILE;
            url = Thread.currentThread().getContextClassLoader().getResource(path);
        }
        return url;
    }

    private static void setNimbusLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            LogManager.getLogger().warn("Cannot set java look and feel");
        }
    }
}
