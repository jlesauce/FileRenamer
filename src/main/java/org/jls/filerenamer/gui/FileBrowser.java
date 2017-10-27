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

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Arbre permettant d'explorer les données du disque dur.
 * 
 * @author AwaX
 * @created 24 oct. 2014
 * @version 1.0
 */
public class FileBrowser implements TreeSelectionListener {

	private final FileSystemView fileSystemView;
	private JTree tree;

	/**
	 * Permet d'instancier l'arbre d'exploration.
	 */
	public FileBrowser () {
		this.fileSystemView = FileSystemView.getFileSystemView();
		createTree();
		addListeners();
	}

	/**
	 * Permet de créer l'arbre et d'afficher les dossiers et fichiers contenus
	 * sur le disque.
	 */
	private void createTree () {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		File[] roots = fileSystemView.getRoots();
		for (File file : roots) {
			walk(root, file, 0, 1);
		}
		this.tree = new JTree(new DefaultTreeModel(root));
		this.tree.setRootVisible(false);
		this.tree.setCellRenderer(new FileBrowserCellRenderer(this.tree));
		this.tree.repaint();
	}

	/**
	 * Permet de lister de manière récursive les dossiers et fichiers traversés
	 * à partir du chemin spécifié.
	 * 
	 * @param node
	 *            Noeud de l'arbre correspondant à la racine à partir du chemin
	 *            spécifié.
	 * @param dir
	 *            Dossier racine à partir duquel s'effectuera la découverte des
	 *            fichiers.
	 * @param depth
	 *            Profondeur au moment de l'appel récursif de la fonction (la
	 *            profondeur doit être de zéro pour un premier appel).
	 * @param maxDepth
	 *            Profondeur maximum de la recherche (une profondeur de zéro
	 *            permettra de découvrir uniquement les fichiers contenus au
	 *            premier niveau du chemin spécifié).
	 */
	private void walk (DefaultMutableTreeNode node, File dir, int depth, int maxDepth) {
		if (node.isLeaf()) {
			ArrayList<File> directories = new ArrayList<>();
			ArrayList<File> files = new ArrayList<>();
			// Détection des fichiers
			for (File file : fileSystemView.getFiles(dir, true)) {
				if (!file.getAbsolutePath().endsWith(".lnk")) {
					if (file.isDirectory()) {
						directories.add(file);
					} else {
						files.add(file);
					}
				}
			}
			// Stockage dans l'ordre des dossiers puis des fichiers
			directories.addAll(files);
			for (File f : directories) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
				node.add(child);
				if (depth < maxDepth) {
					// Si c'est un dossier non vide
					for (File subfile : fileSystemView.getFiles(f, true)) {
						DefaultMutableTreeNode childOfChild = new DefaultMutableTreeNode(subfile);
						child.add(childOfChild);
						// Appel récursif
						int newDepth = depth + 1;
						walk(node, subfile, newDepth, maxDepth);
					}
				}
			}
		}
	}

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.tree.addTreeSelectionListener(this);
	}

	@Override
	public void valueChanged (TreeSelectionEvent e) {
		// On récupère le noeud de l'évènement
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		// On balaye tous les noeuds d'en dessous
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			File nodeFile = (File) child.getUserObject();
			// Si c'est un dossier alors on récupère ses fichiers
			if (nodeFile.isDirectory()) {
				walk(child, nodeFile, 0, 0);
			}
		}
	}

	/**
	 * Renvoie l'arbre associé.
	 * 
	 * @return Arbre associé.
	 */
	public JTree getJTree () {
		return this.tree;
	}
}