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
package org.awax.filerenamer.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Classe permettant de modifier le rendu des éléments s'affichant dans l'arbre
 * d'exploration des dossiers.
 * 
 * @author AwaX
 * @created 24 oct. 2014
 * @version 1.0
 */
public class FileBrowserCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 2683418285034624925L;

	private final FileSystemView fileSystemView;
	private final JLabel label;
	private final Color bgSelectedColor;
	private final Color fgSelectedColor;
	private final Color bgNonSelectedColor;
	private final Color fgNonSelectedColor;

	/**
	 * Permet d'instancier un file renderer.
	 * 
	 * @param tree
	 *            Arbre associé au renderer.
	 */
	public FileBrowserCellRenderer (final JTree tree) {
		this.label = new JLabel();
		this.label.setOpaque(true);
		this.fileSystemView = FileSystemView.getFileSystemView();
		this.bgSelectedColor = null;
		this.fgSelectedColor = new JLabel().getForeground();
		this.bgNonSelectedColor = null;
		this.fgNonSelectedColor = new JLabel().getForeground();
	}

	@Override
	public Component getTreeCellRendererComponent (final JTree tree, final Object value, final boolean sel,
			final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();
		this.label.setIcon(fileSystemView.getSystemIcon(file));
		this.label.setText(fileSystemView.getSystemDisplayName(file));
		this.label.setToolTipText(file.getPath());
		if (sel) {
			label.setOpaque(false);
			label.setBackground(bgSelectedColor);
			label.setForeground(fgSelectedColor);
		} else {
			label.setOpaque(false);
			label.setBackground(bgNonSelectedColor);
			label.setForeground(fgNonSelectedColor);
		}
		return label;
	}

	@Override
	public Color getBackgroundSelectionColor () {
		return this.bgSelectedColor;
	}

	@Override
	public Color getBackgroundNonSelectionColor () {
		return this.bgNonSelectedColor;
	}

	@Override
	public Color getTextSelectionColor () {
		return this.fgSelectedColor;
	}

	@Override
	public Color getTextNonSelectionColor () {
		return this.fgNonSelectedColor;
	}
}