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
package org.jls.filerenamer.util;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.FileRenamer;

public class ResourceManager {

    private static final String slash = File.separator;

    /*
     * Directories
     */
    public static final String RESOURCES_DIR = "resources";

    /*
     * Package resources
     */
    public static final String RESOURCES_PATH = FileRenamer.class.getProtectionDomain().getCodeSource()
            .getLocation().getPath() + RESOURCES_DIR;
    public static final String IMG_PATH = "img";
    public static final String PROPERTIES_PATH = "properties";

    public static final String LOG4J_FILE = "log4j2.xml";

	private static ResourceManager INSTANCE = null;

	private final Logger logger;
	private CombinedConfiguration configuration;

	   private ResourceManager() {
	        this.logger = LogManager.getLogger();
	        DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
	        builder.setConfigurationBasePath(RESOURCES_PATH);
	        builder.setBasePath(RESOURCES_PATH);
	        try {
	            builder.setFile(getResourceAsFile("configuration-descriptor.xml"));
	            builder.setEncoding("UTF8");
	            this.configuration = builder.getConfiguration(true);
	        } catch (Exception e) {
	            this.logger.fatal("An error occured while building application properties", e);
	            Runtime.getRuntime().exit(-1);
	        }
	    }

	    public final static ResourceManager getInstance () {
	        if (ResourceManager.INSTANCE == null) {
	            ResourceManager.INSTANCE = new ResourceManager();
	        }
	        return ResourceManager.INSTANCE;
	    }

	    public final static URL getResource (final String name) throws FileNotFoundException {
	        URL url = Thread.currentThread().getContextClassLoader().getResource(name);
	        if (url == null) {
	            url = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_DIR + File.separator + name);
	        }
	        if (url == null) {
	            throw new FileNotFoundException("Resource not found");
	        }
	        return url;
	    }

	    public final static InputStream getResourceAsStream (final String name) {
	        return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
	    }

	    public final static File getResourceAsFile (final String name) throws FileNotFoundException {
	        return new File(getResource(name).getPath());
	    }

	    public String setProperty (final String key, final String value) {
	        if (value != null && !value.isEmpty()) {
	            if (this.configuration.containsKey(key)) {
	                String oldValue = this.getString(key);
	                this.configuration.setProperty(key, value);
	                return oldValue;
	            }
	            throw new IllegalArgumentException("Key does not exist : " + key);
	        }
	        throw new IllegalArgumentException("Value cannot be null or empty");
	    }

	    public String getString (final String key) throws IllegalArgumentException {
	        if (this.configuration.containsKey(key)) {
	            return this.configuration.getString(key);
	        }
	        throw new IllegalArgumentException("Key does not exist : " + key);
	    }

	    public int getInt (final String key) {
	        String str = getString(key);
	        if (!str.isEmpty()) {
	            try {
	                return Integer.parseInt(str);
	            } catch (Exception e) {
	                throw new NumberFormatException("Cannot parse value to integer : " + str);
	            }
	        }
	        throw new IllegalStateException("Empty value");
	    }

	    public Color getColor (final String key) {
	        String str = getString(key);
	        if (!str.isEmpty()) {
	            try {
	                return Color.decode(str);
	            } catch (Exception e) {
	                throw new IllegalArgumentException("Cannot create color : " + str, e);
	            }
	        }
	        throw new IllegalStateException("Empty value");
	    }

	    public ImageIcon getIcon (final String key) throws FileNotFoundException {
	        String filename = getString(key);
	        String path = IMG_PATH + slash + filename;
	        URL url = getResource(path);
	        if (url == null) {
	            throw new IllegalStateException("The file associated with key '" + key
	                    + "' does not exist or the invoker doesn't have adequate  privileges to get the resource");
	        }
	        return new ImageIcon(url);
	    }
}