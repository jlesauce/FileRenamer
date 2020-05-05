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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.jls.filerenamer.util.ResourceManager;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.io.IOException;
import java.io.InputStream;

public class FileRenamer {

    public static void main(final String[] args) {
        configureLogger();
        setNimbusLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            ApplicationModel model = new ApplicationModel();
            ApplicationController controller = new ApplicationController(model);
            controller.showGui();
        });
    }

    private static void configureLogger() {
        InputStream log4jInStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(ResourceManager.LOG4J_FILE);
        if (log4jInStream == null) {
            System.err.println("ERROR: " + ResourceManager.LOG4J_FILE + " not found");
            return;
        }

        try {
            ConfigurationSource source = new ConfigurationSource(log4jInStream);
            Configurator.initialize(null, source);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to configure logger");
            e.printStackTrace();
        }
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
