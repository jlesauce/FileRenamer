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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.awax.filerenamer.util.ResourceManager;

/**
 * Classe principale de l'application.
 * 
 * @author AwaX
 * @created 23 oct. 2014
 * @version 1.0
 */
public class FileRenamer {

	/**
	 * Permet de lancer l'application.
	 * 
	 * @param args
	 *            Pas d'arguments.
	 */
	public static void main (String[] args) {
		configureLogger();
		Logger logger = Logger.getLogger(FileRenamer.class);

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

		/*
		 * Lancement de l'application
		 */
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run () {
				ApplicationModel model = new ApplicationModel();
				ApplicationController controller = new ApplicationController(model);
				controller.showGui();
			}
		});
	}

	/**
	 * Permet de configurer le logger.
	 */
	private static void configureLogger () {
		File configFile = new File(ResourceManager.getResource("log4j.xml").getPath());
		// Chargement du fichier de configuration par défaut
		if (configFile.exists()) {
			DOMConfigurator.configure(configFile.getAbsolutePath());
		} else {
			throw new RuntimeException("Invalid log4j property file");
		}
	}
}