/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Julien Le Sauce
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.awax.filerenamer;

import java.io.File;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awax.filerenamer.util.ResourceManager;

public class FileRenamer {

    public static void main (final String[] args) {
        /*
         * Configure the logger
         */
        String log4jKey = "log4j.configurationFile";
        // If no configuration is configured
        if (System.getProperty(log4jKey) == null) {
            // Load the default configuration
            String path = ResourceManager.LOG4J_FILE;
            URL url = Thread.currentThread().getContextClassLoader().getResource(path);
            if (url == null) {
                path = ResourceManager.RESOURCES_DIR + File.separator + ResourceManager.LOG4J_FILE;
                url = Thread.currentThread().getContextClassLoader().getResource(path);
            }
            System.setProperty(log4jKey, url.getFile());
        }
        final Logger logger = LogManager.getLogger();
        logger.info("log4j configuration file set : {}", System.getProperty(log4jKey));

        /*
         * Look & Feel Nimbus
         */
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    logger.info("Setting Nimbus look & feel");
                    break;
                }
            }
        } catch (Exception e) {
            logger.warn("Cannot set java look and feel");
        }

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run () {
                ApplicationModel model = new ApplicationModel();
                ApplicationController controller = new ApplicationController(model);
                controller.showGui();
            }
        });
    }
}
