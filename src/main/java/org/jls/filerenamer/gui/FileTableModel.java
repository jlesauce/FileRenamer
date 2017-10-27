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
package org.jls.filerenamer.gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;
import org.jls.filerenamer.util.FileInfo;

public class FileTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -2244804522248015803L;

	private final String[] columns = {"Icon", "Ext", "Name", "Final Name", "Path" };
	private final ArrayList<FileInfo> files;

	private boolean showOnlyDirectories;
	private boolean showOnlyFiles;

	/**
	 * Permet d'instancier un modèle de données.
	 * 
	 * @param files
	 *            Liste des fichiers affichés par la table.
	 */
	public FileTableModel (final ArrayList<FileInfo> files) {
		super();
		this.files = files;
		this.showOnlyDirectories = false;
		this.showOnlyFiles = false;
	}

	/**
	 * Permet de mettre à jour la table.
	 * 
	 * @param files
	 *            Liste des fichiers à afficher, si <code>null</code> est
	 *            spécifié alors la table est simplement vidée.
	 */
	public void setData (final File[] data) {
		this.files.clear();
		if (files != null) {
			for (File file : data) {
				FileInfo fileInfo = new FileInfo(file);
				if (file.isDirectory()) {
					if (!this.showOnlyFiles) {
						this.files.add(fileInfo);
					}
				} else {
					if (!this.showOnlyDirectories) {
						this.files.add(fileInfo);
					}
				}
			}
		}
		fireTableDataChanged();
	}

	/**
	 * Permet de mettre à jour la table.
	 * 
	 * @param files
	 *            Liste des fichiers à afficher, si <code>null</code> est
	 *            spécifié alors la table est simplement vidée.
	 */
	public void setData (final ArrayList<FileInfo> data) {
		this.files.clear();
		if (files != null) {
			for (FileInfo file : data) {
				if (file.getFile().isDirectory()) {
					if (!this.showOnlyFiles) {
						this.files.add(file);
					}
				} else {
					if (!this.showOnlyDirectories) {
						this.files.add(file);
					}
				}
			}
		}
		fireTableDataChanged();
	}

	/**
	 * Permet de spécifier au modèle de n'afficher que les dossiers. Par défaut
	 * le modèle affiche tous les fichiers.
	 * 
	 * @param showOnlyDirectories
	 *            <code>true</code> pour afficher uniquement les dossiers,
	 *            <code>false</code> pour tout afficher.
	 */
	public void setShowOnlyDirectories (boolean showOnlyDirectories) {
		this.showOnlyDirectories = showOnlyDirectories;
		this.showOnlyFiles = !showOnlyDirectories;
	}

	/**
	 * Permet de spécifier au modèle de n'afficher que les fichiers. Par défaut
	 * le modèle affiche tous les fichiers.
	 * 
	 * @param showOnlyFiles
	 *            <code>true</code> pour afficher uniquement les fichiers,
	 *            <code>false</code> pour tout afficher.
	 */
	public void setShowOnlyFiles (boolean showOnlyFiles) {
		this.showOnlyFiles = showOnlyFiles;
		this.showOnlyDirectories = !showOnlyFiles;
	}

	@Override
	public int getRowCount () {
		return this.files != null ? this.files.size() : 0;
	}

	@Override
	public int getColumnCount () {
		return this.columns != null ? this.columns.length : 0;
	}

	@Override
	public Object getValueAt (int rowIndex, int columnIndex) {
		if (rowIndex >= 0 && rowIndex < getRowCount() && columnIndex >= 0 && columnIndex < getColumnCount()) {
			FileInfo file = this.files.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return file.getIcon();
				case 1:
					return FilenameUtils.getExtension(file.getFile().getName());
				case 2:
					return file.getDisplayName();
				case 3:
					return file.getNewName();
				case 4:
					return file.getPath();
				default:
					return null;
			}
		}
		return null;
	}

	@Override
	public Class<?> getColumnClass (int columnIndex) {
		if (columnIndex >= 0 && columnIndex < getColumnCount()) {
			switch (columnIndex) {
				case 0:
					return Icon.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				case 3:
					return String.class;
				case 4:
					return String.class;
				default:
					return null;
			}
		}
		return String.class;
	}

	@Override
	public String getColumnName (int column) {
		if (column >= 0 && column < getColumnCount()) {
			return this.columns[column];
		}
		return null;
	}

	@Override
	public boolean isCellEditable (int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt (Object aValue, int rowIndex, int columnIndex) {
		// Nothing
	}
}