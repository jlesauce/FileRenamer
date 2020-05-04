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

import java.io.File;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class FileTree implements TreeSelectionListener {

    private final FileSystemView fileSystemView;
    private JTree tree;

    public FileTree() {
        this.fileSystemView = FileSystemView.getFileSystemView();
        createTree();
        addGuiListeners();
    }

    private void createTree() {
        DefaultMutableTreeNode rootNode = createTreeNodes();

        this.tree = new JTree(new DefaultTreeModel(rootNode));
        configureTree();
        this.tree.repaint();
    }

    private DefaultMutableTreeNode createTreeNodes() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        File[] rootFiles = fileSystemView.getRoots();

        walkThroughFilesRecursively(rootNode, rootFiles);

        return rootNode;
    }

    private void walkThroughFilesRecursively(final DefaultMutableTreeNode rootNode, final File[] rootFiles) {
        for (File rootFile : rootFiles) {
            int depthInTree = 0;
            int maxDepth = 1;
            walkThroughRootFile(rootNode, rootFile, depthInTree, maxDepth);
        }
    }

    private void walkThroughRootFile(final DefaultMutableTreeNode node,
                                     final File fromDirectory,
                                     final int currentDepth,
                                     final int maxDepthFromRoot) {
        if (node.isLeaf()) {
            ArrayList<File> directories = new ArrayList<>();
            ArrayList<File> files = new ArrayList<>();
            for (File file : fileSystemView.getFiles(fromDirectory, true)) {
                if (!file.getAbsolutePath().endsWith(".lnk")) {
                    if (file.isDirectory()) {
                        directories.add(file);
                    } else {
                        files.add(file);
                    }
                }
            }
            directories.addAll(files);
            for (File f : directories) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
                node.add(child);
                if (currentDepth < maxDepthFromRoot) {
                    for (File subfile : fileSystemView.getFiles(f, true)) {
                        DefaultMutableTreeNode childOfChild = new DefaultMutableTreeNode(subfile);
                        child.add(childOfChild);
                        int newDepth = currentDepth + 1;
                        walkThroughRootFile(node, subfile, newDepth, maxDepthFromRoot);
                    }
                }
            }
        }
    }

    private void configureTree() {
        this.tree.setRootVisible(false);
        this.tree.setCellRenderer(new FileBrowserCellRenderer());
    }

    private void addGuiListeners() {
        this.tree.addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(final TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            File nodeFile = (File) child.getUserObject();
            if (nodeFile.isDirectory()) {
                walkThroughRootFile(child, nodeFile, 0, 0);
            }
        }
    }

    public JTree getJTree() {
        return this.tree;
    }
}
