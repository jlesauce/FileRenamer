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
package org.jls.filerenamer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

/**
 * Permet de filtrer des fichiers selon différents paramètres.
 * 
 * @author AwaX
 * @created 28 oct. 2014
 * @version 1.0
 */
public class FileFilter {

	private String startsWith;
	private String endsWith;
	private String contains;
	private FileNameExtensionFilter extensionFilter;
	private String regex;

	/**
	 * Permet d'instancier un filtre selon les différents paramètres
	 * disponibles. Tous les paramètres de filtrage sont additifs, c'est-à-dire
	 * qu'ils seront tous utilisés lors du filtrage. Pour ne pas utiliser tel ou
	 * tel paramètre, il suffit de spécifier l'argument <code>null</code>.
	 * 
	 * @param startsWith
	 *            Permet de spécifier le début du nom du fichier.
	 * @param endsWith
	 *            Permet de spécifier la fin du nom du fichier sans l'extension.
	 * @param contains
	 *            Permet de spécifier ce que doit contenir le nom du fichier.
	 * @param extensionFilter
	 *            Filtre permettant de filtrer les fichiers selon leur
	 *            extension.
	 * @param regex
	 *            Regex permettant de spécifier une expression régulière pour
	 *            matcher le nom du fichier.
	 */
	public FileFilter (String startsWith, String endsWith, String contains, FileNameExtensionFilter extensionFilter,
			String regex) {
		super();
		this.startsWith = startsWith;
		this.endsWith = endsWith;
		this.contains = contains;
		this.extensionFilter = extensionFilter;
		this.regex = regex;
	}

	/**
	 * Permet de tester le fichier spécifié, en renvoyant <code>true</code> si
	 * le fichier est accepté, <code>false</code> sinon. Si le fichier est un
	 * dossier alors le paramètre de filtrage de l'extension est ignoré.
	 * 
	 * @param file
	 *            Fichier à tester.
	 * @return <code>true</code> si le fichier est accepté (c'est-à-dire si le
	 *         fichier n'est filtré par aucun des paramètres du filtre),
	 *         <code>false</code> sinon.
	 */
	public boolean accept (File file) {
		String nameWithoutExt = FilenameUtils.removeExtension(file.getName());
		if (this.startsWith != null && !this.startsWith.isEmpty() && !nameWithoutExt.startsWith(this.startsWith)) {
			return false;
		}
		if (this.endsWith != null && !this.endsWith.isEmpty() && !nameWithoutExt.endsWith(this.endsWith)) {
			return false;
		}
		if (this.contains != null && !contains.isEmpty() && !nameWithoutExt.contains(this.contains)) {
			return false;
		}
		if (this.regex != null && !regex.isEmpty() && !nameWithoutExt.matches(regex)) {
			return false;
		}
		if (!file.isDirectory() && this.extensionFilter != null) {
			return this.extensionFilter.accept(file);
		}
		return true;
	}

	/**
	 * Permet de filtrer une liste de fichiers en ne gardant que les fichiers
	 * dont la fonction {@link #accept(File)} renvoie <code>true</code>.
	 * 
	 * @param files
	 *            Liste de fichiers à filtrer.
	 * @return Nouvelle liste contenant uniquement les fichiers n'ayant pas été
	 *         filtrés.
	 */
	public ArrayList<File> filter (Collection<? extends File> files) {
		ArrayList<File> acceptedFiles = new ArrayList<>();
		for (File file : files) {
			if (accept(file)) {
				acceptedFiles.add(file);
			}
		}
		return acceptedFiles;
	}

	@Override
	public String toString () {
		String[] extensions = this.extensionFilter != null ? this.extensionFilter.getExtensions() : null;
		String exs = null;
		if (extensions != null) {
			exs = "";
			for (String ex : extensions) {
				exs += ex + ",";
			}
			exs = exs.substring(0, exs.length() - 1);
		}
		String instance = getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
		return "[" + instance + ", StartsWith=" + this.startsWith + ", EndsWith=" + this.endsWith + ", Contains="
				+ this.contains + ", Matches=" + this.regex + ", ExtensionFilter=(" + exs + ")]";
	}
}