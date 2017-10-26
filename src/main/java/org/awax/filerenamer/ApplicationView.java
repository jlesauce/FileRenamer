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
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.awax.filerenamer.gui.FileBrowser;
import org.awax.filerenamer.gui.FileTable;
import org.awax.filerenamer.gui.FileTableModel;
import org.awax.filerenamer.gui.FileFiltersPanel;
import org.awax.filerenamer.gui.RenamingPanel;
import org.awax.filerenamer.util.FileInfo;
import org.awax.filerenamer.util.ResourceManager;
import org.awax.filerenamer.util.TableColumnAdjuster;

/**
 * Fenêtre principale de l'application.
 * 
 * @author AwaX
 * @created 23 oct. 2014
 * @version 1.0
 */
public class ApplicationView extends JFrame implements Observer, TreeSelectionListener {

	private static final long serialVersionUID = 807507002677441420L;

	private final ApplicationModel model;
	private final ApplicationController controller;
	private final Logger logger;
	private final ResourceManager props;

	private FileBrowser fileBrowser;
	private FileTable fileTable;
	private FileFiltersPanel fileFiltersPanel;
	private RenamingPanel renamingPanel;
	private TableColumnAdjuster tableAdjuster;

	/**
	 * Permet d'instancier la fenêtre principale de l'application.
	 * 
	 * @param model
	 *            Modèle de données de l'application.
	 * @param controller
	 *            Contrôleur de l'application.
	 */
	public ApplicationView (final ApplicationModel model, final ApplicationController controller) {
		super(model.getAppName());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.model = model;
		this.controller = controller;
		this.logger = Logger.getLogger(getClass());
		this.props = ResourceManager.getInstance();
		createComponents();
		createGui();
		addListeners();
		model.addObserver(this);
	}

	/**
	 * Permet d'afficher l'application.
	 */
	public void showGui () {
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		this.fileBrowser = new FileBrowser();
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

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
		// Top panels
		JScrollPane browserScroll = new JScrollPane(this.fileBrowser.getJTree());
		JScrollPane tableScroll = new JScrollPane(this.fileTable);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, browserScroll, tableScroll);
		splitPane.setResizeWeight(0.0);
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

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.fileBrowser.getJTree().addTreeSelectionListener(this);
	}

	@Override
	public void update (Observable o, Object arg) {
		if (o instanceof ApplicationModel) {
			// Mise à jour de la sélection
			this.logger.debug("Updating current file selection");
			this.fileTable.getTableModel().setData(this.model.getCurrentFileSelection());
			this.tableAdjuster.adjustColumns();
		}
	}

	@Override
	public void valueChanged (TreeSelectionEvent e) {
		// File Browser
		if (e.getSource().equals(this.fileBrowser.getJTree())) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
			File nodeFile = (File) node.getUserObject();
			if (nodeFile.isDirectory()) {
				File[] files = this.model.getFileSystemView().getFiles(nodeFile, true);
				ArrayList<FileInfo> fileInfoList = new ArrayList<>();
				for (File f : files) {
					fileInfoList.add(new FileInfo(f));
				}
				this.model.setFileSelection(fileInfoList);
			}
		}
	}
}