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

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;
import org.awax.filerenamer.util.FileInfo;

/**
 * Modèle de données de l'application.
 * 
 * @author AwaX
 * @created 23 oct. 2014
 * @version 1.0
 */
public class ApplicationModel extends Observable {

	private final Logger logger;
	private final String appName;
	private final FileSystemView fileSystemView;
	private ArrayList<FileInfo> fileSelection;
	private ArrayList<FileInfo> currentFileSelection;

	/**
	 * Permet d'instancier le modèle de données de l'application par défaut.
	 */
	public ApplicationModel () {
		this.logger = Logger.getLogger(getClass());
		this.appName = "File Renamer";
		this.fileSystemView = FileSystemView.getFileSystemView();
		this.fileSelection = new ArrayList<>();
		this.currentFileSelection = new ArrayList<>();
	}

	/**
	 * Permet de notifier tous les abonnés d'une modification du modèle de
	 * données.
	 * 
	 * @param obj
	 *            (Optionnel) Il est possible de préciser un objet à transmettre
	 *            aux abonnés.
	 */
	public void notifyChanged (Object... obj) {
		this.setChanged();
		if (obj == null || obj.length == 0) {
			this.notifyObservers();
		} else if (obj.length == 1) {
			this.notifyObservers(obj);
		} else {
			throw new IllegalArgumentException("Only one argument accepted");
		}
		this.clearChanged();
	}

	public String getAppName () {
		return this.appName;
	}

	public FileSystemView getFileSystemView () {
		return this.fileSystemView;
	}

	public ArrayList<FileInfo> getFileSelection () {
		return this.fileSelection;
	}

	public void setFileSelection (ArrayList<FileInfo> fileSelection) {
		this.logger.debug("Updating file selection");
		this.fileSelection = fileSelection;
		setCurrentFileSelection(fileSelection);
	}

	public ArrayList<FileInfo> getCurrentFileSelection () {
		return this.currentFileSelection;
	}

	public void setCurrentFileSelection (ArrayList<FileInfo> fileSelection) {
		this.logger.debug("Updating current file selection");
		this.currentFileSelection = fileSelection;
		notifyChanged(fileSelection);
	}
}