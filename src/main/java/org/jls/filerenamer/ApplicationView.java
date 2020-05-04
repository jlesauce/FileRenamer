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
package org.jls.filerenamer;

import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.gui.*;
import org.jls.filerenamer.util.FileInfo;
import org.jls.filerenamer.util.ResourceManager;
import org.jls.filerenamer.util.TableColumnAdjuster;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ApplicationView extends JFrame implements Observer, TreeSelectionListener {

    private final ApplicationModel model;
    private final ApplicationController controller;
    private final Logger logger;
    private final ResourceManager props;

    private FileTree fileBrowser;
    private FileTable fileTable;
    private FileFiltersPanel fileFiltersPanel;
    private RenamingPanel renamingPanel;
    private TableColumnAdjuster tableAdjuster;

    public ApplicationView(final ApplicationModel model, final ApplicationController controller) {
        super(model.getAppName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.model = model;
        this.controller = controller;
        this.logger = LogManager.getLogger();
        this.props = ResourceManager.getInstance();
        createComponents();
        createGui();
        addListeners();
        model.addObserver(this);
    }

    public void showGui() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createComponents() {
        this.fileBrowser = new FileTree();
        this.fileTable = new FileTable(new FileTableModel(this.model.getCurrentFileSelection()));
        this.fileTable.getTableModel().setShowOnlyFiles(true);
        this.fileFiltersPanel = new FileFiltersPanel(this.controller);
        this.renamingPanel = new RenamingPanel(this.controller);
        this.tableAdjuster = new TableColumnAdjuster(this.fileTable);
        this.tableAdjuster.setColumnHeaderIncluded(true);
        this.tableAdjuster.setColumnDataIncluded(true);
        this.tableAdjuster.setDynamicAdjustment(false);
        this.tableAdjuster.setOnlyAdjustLarger(true);
    }

    private void createGui() {
        // Top panels
        JScrollPane browserScroll = new JScrollPane(this.fileBrowser.getJTree());
        JScrollPane tableScroll = new JScrollPane(this.fileTable);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, browserScroll, tableScroll);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerSize(splitPane.getDividerSize() / 2);

        // Bottom panels
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add(this.props.getString("mainPanel.tab.fileFiltersPanel.label"), this.fileFiltersPanel);
        tabbedPane.add(this.props.getString("mainPanel.tab.renamingPanel.label"), this.renamingPanel);

        // Main Panel
        setLayout(new MigLayout("fill"));
        add(splitPane, "grow, pushy, wrap");
        add(tabbedPane, "grow");
    }

    private void addListeners() {
        this.fileBrowser.getJTree().addTreeSelectionListener(this);
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (o instanceof ApplicationModel) {
            this.logger.debug("Updating current file selection");
            this.fileTable.getTableModel().updateTableData(this.model.getCurrentFileSelection());
            this.tableAdjuster.adjustColumns();
        }
    }

    @Override
    public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {
        if (treeSelectionEvent.getSource().equals(this.fileBrowser.getJTree())) {
            onFileBrowserValueChanged(treeSelectionEvent);
        }
    }

    private void onFileBrowserValueChanged(final TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
        File nodeFile = (File) node.getUserObject();

        if (nodeFile.isDirectory()) {
            updateFileSelection(nodeFile);
        }
    }

    private void updateFileSelection(final File directory) {
        File[] files = this.model.getFileSystemView().getFiles(directory, true);
        ArrayList<FileInfo> fileInfoList = new ArrayList<>();
        for (File file : files) {
            fileInfoList.add(new FileInfo(file));
        }
        this.model.setFileSelection(fileInfoList);
    }
}
