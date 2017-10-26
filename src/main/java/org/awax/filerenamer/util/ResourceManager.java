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
package org.awax.filerenamer.util;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;
import org.awax.filerenamer.FileRenamer;

/**
 * Permet de gérer l'accès aux différentes ressources et fichiers de
 * configuration de l'application.
 * 
 * @author AwaX
 * @created 28 mai 2014
 * @version 1.0
 */
public class ResourceManager {

	public static final String USER_DIR = System.getProperty("user.dir");
	public static final String RESOURCES_PATH = FileRenamer.class.getProtectionDomain().getCodeSource().getLocation()
			.getPath();

	private static ResourceManager INSTANCE = null;

	private final Logger logger;
	private CombinedConfiguration configuration;

	/**
	 * Permet d'instancier le gestionnaire de resources.
	 */
	private ResourceManager () {
		this.logger = Logger.getLogger(getClass());
		this.logger.debug("Loading application properties");
		DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
		builder.setLogger(new Log4JLogger(this.logger));
		builder.setConfigurationBasePath(RESOURCES_PATH);
		builder.setBasePath(RESOURCES_PATH);
		try {
			builder.setFile(new File(getClass().getClassLoader().getResource("configuration-descriptor.xml").getPath()));
			builder.setEncoding("UTF8");
			this.configuration = builder.getConfiguration(true);
		} catch (Exception e) {
			this.logger.fatal("An error occured while building application properties", e);
			Runtime.getRuntime().exit(-1);
		}
	}

	/**
	 * Renvoie l'instance unique du {@link ResourceManager}.
	 * 
	 * @return Instance unique du gestionnaire de resources.
	 */
	public final static synchronized ResourceManager getInstance () {
		if (ResourceManager.INSTANCE == null) {
			ResourceManager.INSTANCE = new ResourceManager();
		}
		return ResourceManager.INSTANCE;
	}

	/**
	 * Permet d'accéder à une resource de l'application via le nom du fichier.
	 * 
	 * @param name
	 *            Nom du fichier à récupérer.
	 * @return {@link URL} pointant vers la resource recherchée ou
	 *         <code>null</code> si la resource n'existe pas.
	 */
	public final static URL getResource (final String name) {
		return ResourceManager.getInstance().getClass().getClassLoader().getResource(name);
	}

	/**
	 * Permet de mettre à jour la valeur associée à la clé de propriété
	 * spécifiée.
	 * 
	 * @param key
	 *            Clé de propriété.
	 * @param value
	 *            Valeur associée à la clé spécifiée.
	 * @return Valeur associée à la clé avant modification.
	 */
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

	/**
	 * Renvoie la chaîne de texte associée à la clé de propriété spécifiée. Si
	 * la clé n'existe pas alors une exception de type
	 * {@link IllegalArgumentException} est lancée.
	 * 
	 * @param key
	 *            Clé de propriété.
	 * @return Chaîne de texte associée à la clé spécifiée.
	 */
	public String getString (final String key) {
		if (this.configuration.containsKey(key)) {
			return this.configuration.getString(key);
		}
		throw new IllegalArgumentException("Key does not exist : " + key);
	}

	/**
	 * Renvoie la valeur de type <code>int</code> associée à la clé de propriété
	 * spécifiée. Si la clé n'existe pas, alors une exception de type
	 * {@link IllegalArgumentException} est lancée. Si la valeur ne peut être
	 * parsée, alors une exception de type {@link NumberFormatException} est
	 * lancée.
	 * 
	 * @param key
	 *            Clé de propriété.
	 * @return Valeur de type <code>int</code> associée à la clé spécifiée
	 */
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

	/**
	 * Renvoie une icône instanciée à partir du chemin récupéré grâce à la clé
	 * de propriété spécifiée. Si la clé n'existe pas, alors une exception du
	 * type {@link IllegalArgumentException} est lancée.
	 * 
	 * @param key
	 *            Clé de propriété.
	 * @return Nouvelle instance de {@link ImageIcon}.
	 */
	public ImageIcon getIcon (final String key) {
		String path = getString(key);
		if (path != null) {
			File file = new File(path);
			if (file.exists()) {
				return new ImageIcon(path);
			}
			throw new IllegalStateException("Cannot load icon : specified resource does not exist");
		}
		throw new IllegalStateException("Cannot load icon : path is null or empty");
	}
}