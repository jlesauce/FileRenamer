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
package org.jls.filerenamer.gui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileBrowserCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -1685600857378020416L;

    private final FileSystemView fileSystemView;
    private final JLabel label;

    private Color bgSelectedColor;
    private Color fgSelectedColor;
    private Color bgNonSelectedColor;
    private Color fgNonSelectedColor;

    public FileBrowserCellRenderer(final JTree tree) {
        label = new JLabel();
        label.setOpaque(true);
        fileSystemView = FileSystemView.getFileSystemView();
        configureSelectionColors();
    }

    private void configureSelectionColors () {
        bgSelectedColor = null;
        fgSelectedColor = new JLabel().getForeground();
        bgNonSelectedColor = null;
        fgNonSelectedColor = new JLabel().getForeground();
    }

    @Override
    public Component getTreeCellRendererComponent (final JTree tree,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean isExpanded,
                                                   final boolean isLeaf,
                                                   final int rowIndex,
                                                   final boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        File file = (File) node.getUserObject();

        updateFileNodeLabel(file);
        if (isSelected) {
            setFileNodeSelected();
        } else {
            setFileNodeNonSelected();
        }

        return label;
    }

    private void updateFileNodeLabel (final File file) {
        label.setIcon(fileSystemView.getSystemIcon(file));
        label.setText(fileSystemView.getSystemDisplayName(file));
        label.setToolTipText(file.getPath());
        label.setOpaque(false);
    }

    private void setFileNodeSelected () {
        label.setBackground(getBackgroundSelectionColor());
        label.setForeground(getTextSelectionColor());
    }

    private void setFileNodeNonSelected () {
        label.setBackground(getBackgroundNonSelectionColor());
        label.setForeground(getTextNonSelectionColor());
    }

    @Override
    public Color getBackgroundSelectionColor () {
        return bgSelectedColor;
    }

    @Override
    public Color getBackgroundNonSelectionColor () {
        return bgNonSelectedColor;
    }

    @Override
    public Color getTextSelectionColor () {
        return fgSelectedColor;
    }

    @Override
    public Color getTextNonSelectionColor () {
        return fgNonSelectedColor;
    }
}
