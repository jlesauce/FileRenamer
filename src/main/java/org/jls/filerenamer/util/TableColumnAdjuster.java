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
package org.jls.filerenamer.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Classe permettant d'adapter la largeur des colonnes d'une JTable à leur
 * contenu. Plusieurs propriétés permettent de calculer la manière dont la
 * largeur des colonnes est calculée. Une autre propriété permet de spécifier
 * quelles colonnes doivent être ajustées dynamiquement ou non.
 * 
 * @author LE SAUCE Julien
 * @version 1.0
 */
public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

	private JTable table;

	private int spacing;
	private Map<TableColumn, Integer> columnSizes = new HashMap<>();

	private boolean isColumnHeaderIncluded;
	private boolean isColumnDataIncluded;
	private boolean isOnlyAdjustLarger;
	private boolean isDynamicAdjustment;

	/**
	 * Permet d'instancier un ajusteur à partir d'une {@link JTable}.
	 * 
	 * @param table
	 *            Table dont les colonnes doivent être ajustées automatiquement.
	 */
	public TableColumnAdjuster (JTable table) {
		this(table, 6);
	}

	/**
	 * Permet d'instancier un ajusteur à partir d'une {@link JTable}.
	 * 
	 * @param table
	 *            Table dont les colonnes doivent être ajustées automatiquement.
	 * @param spacing
	 *            Espacement entre les colonnes.
	 */
	public TableColumnAdjuster (JTable table, int spacing) {
		this.table = table;
		this.spacing = spacing;
		setColumnHeaderIncluded(true);
		setColumnDataIncluded(true);
		setOnlyAdjustLarger(false);
		setDynamicAdjustment(false);
		installActions();
	}

	/**
	 * Permet d'ajuster la largeur des colonnes de la table à leur contenu.
	 */
	public void adjustColumns () {
		TableColumnModel columnModel = this.table.getColumnModel();

		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			adjustColumn(i);
		}

		this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
	}

	/**
	 * Permet d'ajuster la largeur d'une colonne en particulier à son contenu.
	 * 
	 * @param column
	 *            Indice de la colonne dans la table.
	 */
	public void adjustColumn (final int column) {
		if (column < 0) {
			throw new ArrayIndexOutOfBoundsException(column);
		}

		TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable()) {
			return;
		}

		int columnHeaderWidth = getColumnHeaderWidth(column);
		int columnDataWidth = getColumnDataWidth(column);
		int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

		updateTableColumn(column, preferredWidth);
		this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
	}

	/**
	 * Permet de restaurer la largeur des colonnes de la table à leur largeur
	 * précédente.
	 */
	public void restoreColumns () {
		TableColumnModel tcm = this.table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			restoreColumn(i);
		}
	}

	/**
	 * Permet de déterminer la largeur de la colonne d'entête spécifiée de la
	 * table.
	 * 
	 * @param column
	 *            Indice de la colonne dans la table.
	 * @return Largeur de la colonne d'entête.
	 */
	private int getColumnHeaderWidth (int column) {
		if (!this.isColumnHeaderIncluded) {
			return 0;
		}

		TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			renderer = this.table.getTableHeader().getDefaultRenderer();
		}

		Component c = renderer.getTableCellRendererComponent(this.table, value, false, false, -1, column);
		return c.getPreferredSize().width;
	}

	/**
	 * Permet de calculer la meilleure largeur pour la colonne en se basant sur
	 * la cellule la plus large de la colonne.
	 * 
	 * @param column
	 *            Indice de la colonne dans la table.
	 * @return Largeur la mieux adaptée pour la colonne.
	 */
	private int getColumnDataWidth (int column) {
		if (!this.isColumnDataIncluded) {
			return 0;
		}
		int preferredWidth = 0;
		int maxWidth = this.table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < this.table.getRowCount(); row++) {
			preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
			// Si on dépasse la largeur maximale, inutile de continuer
			if (preferredWidth >= maxWidth) {
				break;
			}
		}
		return preferredWidth;
	}

	/**
	 * Permet de récupérer la largeur occupée par le contenu d'une cellule en
	 * particulier.
	 * 
	 * @param row
	 *            Indice de la ligne dans la table.
	 * @param column
	 *            Indice de la colonne dans la table.
	 * @return Largeur adaptée pour la cellule spécifiée.
	 */
	private int getCellDataWidth (int row, int column) {
		// On utilise le renderer de la cellule pour calculer la largeur
		// préférée
		TableCellRenderer cellRenderer = this.table.getCellRenderer(row, column);
		Component c = this.table.prepareRenderer(cellRenderer, row, column);
		int width = c.getPreferredSize().width + this.table.getIntercellSpacing().width;
		return width;
	}

	/**
	 * Permet de mettre à jour la largeur d'une colonne.
	 * 
	 * @param column
	 *            Indice de la colonne dans la table.
	 * @param w
	 *            Largeur de la colonne.
	 */
	private void updateTableColumn (int column, int w) {
		final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable()) {
			return;
		}
		int width = w + this.spacing;
		// Permet de ne pas rogner la colonne
		if (this.isOnlyAdjustLarger) {
			width = Math.max(width, tableColumn.getPreferredWidth());
		}

		this.columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));
		this.table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}

	/**
	 * Permet de restaurer la largeur d'une colonne à sa largeur précédente.
	 * 
	 * @param column
	 *            Indice de la colonne dans la tble.
	 */
	private void restoreColumn (int column) {
		TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
		Integer width = this.columnSizes.get(tableColumn);

		if (width != null) {
			this.table.getTableHeader().setResizingColumn(tableColumn);
			tableColumn.setWidth(width.intValue());
		}
	}

	/**
	 * Installation des {@link Action} pour permettre à l'utilisateur le
	 * contrôle de certaines fonctionnalités.
	 */
	private void installActions () {
		installColumnAction(true, true, "adjustColumn", "control ADD");
		installColumnAction(false, true, "adjustColumns", "control shift ADD");
		installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
		installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");
		installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
		installToggleAction(false, true, "toggleLarger", "control DIVIDE");
	}

	/**
	 * Mise à jour de l'entrée et de la map des actions avec un nouveau
	 * {@link ColumnAction}.
	 * 
	 * @param isSelectedColumn
	 * @param isAdjust
	 * @param key
	 * @param keyStroke
	 */
	private void installColumnAction (boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
		Action action = new ColumnAction(isSelectedColumn, isAdjust);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		table.getInputMap().put(ks, key);
		table.getActionMap().put(key, action);
	}

	/**
	 * Update the input and action maps with new {@link ToggleAction}.
	 * 
	 * @param isToggleDynamic
	 * @param isToggleLarger
	 * @param key
	 * @param keyStroke
	 */
	private void installToggleAction (boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
		Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		table.getInputMap().put(ks, key);
		table.getActionMap().put(key, action);
	}

	/**
	 * Indique s'il faut inclure la taille des entêtes dans le calcul des
	 * largeurs des colonnes.
	 * 
	 * @param isColumnHeaderIncluded
	 *            <code>true</code> pour inclure les entêtes, <code>false</code>
	 *            sinon.
	 */
	public void setColumnHeaderIncluded (boolean isColumnHeaderIncluded) {
		this.isColumnHeaderIncluded = isColumnHeaderIncluded;
	}

	/**
	 * Indique s'il faut tenir compte des données contenues dans les cellules
	 * dans le calcul des largeurs des colonnes.
	 * 
	 * @param isColumnDataIncluded
	 *            <code>true</code> pour tenir compte des données,
	 *            <code>false</code> sinon.
	 */
	public void setColumnDataIncluded (boolean isColumnDataIncluded) {
		this.isColumnDataIncluded = isColumnDataIncluded;
	}

	/**
	 * Indique si lors de l'ajustement des colonnes on peut diminuer la largeur
	 * des colonnes par rapport à leur état actuel.
	 * 
	 * @param isOnlyAdjustLarger
	 *            <code>true</code> si l'on peut diminuer la taille des
	 *            colonnes, <code>false</code> sinon.
	 */
	public void setOnlyAdjustLarger (boolean isOnlyAdjustLarger) {
		this.isOnlyAdjustLarger = isOnlyAdjustLarger;
	}

	/**
	 * Indique si lors d'un changement dans le modèle de données, la largeur des
	 * colonnes doit être recalculée.
	 * 
	 * @param isDynamicAdjustment
	 *            <code>true</code> si la largeur des colonnes doit être
	 *            recalculée, <code>false</code> sinon.
	 */
	public void setDynamicAdjustment (boolean isDynamicAdjustment) {
		if (this.isDynamicAdjustment != isDynamicAdjustment) {
			// On ajoute ou on retire le listener
			if (isDynamicAdjustment) {
				this.table.addPropertyChangeListener(this);
				this.table.getModel().addTableModelListener(this);
			} else {
				this.table.removePropertyChangeListener(this);
				this.table.getModel().removeTableModelListener(this);
			}
		}
		this.isDynamicAdjustment = isDynamicAdjustment;
	}

	@Override
	public void propertyChange (PropertyChangeEvent e) {
		// Lorsqu'un changement est opéré dans le modèle de données, on met
		// à jour les listeners et les largeurs des colonnes
		if ("model".equals(e.getPropertyName())) {
			TableModel model = (TableModel) e.getOldValue();
			model.removeTableModelListener(this);

			model = (TableModel) e.getNewValue();
			model.addTableModelListener(this);
			adjustColumns();
		}
	}

	@Override
	public void tableChanged (TableModelEvent e) {
		if (!this.isColumnDataIncluded) {
			return;
		}
		// Lorsqu'une cellule a été mise à jour
		if (e.getType() == TableModelEvent.UPDATE) {
			int column = this.table.convertColumnIndexToView(e.getColumn());

			if (column < 0) {
				return;
			}
			// Si on ne peut qu'agrandir les cellules
			if (this.isOnlyAdjustLarger) {
				int row = e.getFirstRow();
				TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

				if (tableColumn.getResizable()) {
					int width = getCellDataWidth(row, column);
					updateTableColumn(column, width);
				}
			}
			// Sinon on ajuste toutes les colonnes
			else {
				adjustColumn(column);
			}
		}
		// Sinon c'est que la modification affecte plus d'une colonne
		else {
			adjustColumns();
		}
	}

	/**
	 * Action to adjust or restore the width of a single column or all columns.
	 * 
	 * @author LE SAUCE Julien
	 * @version 1.0
	 */
	class ColumnAction extends AbstractAction {

		private static final long serialVersionUID = 8373114505358954905L;

		private boolean isSelectedColumn;
		private boolean isAdjust;

		/**
		 * Permet d'instancier une action.
		 * 
		 * @param isSelectedColumn
		 * @param isAdjust
		 */
		public ColumnAction (boolean isSelectedColumn, boolean isAdjust) {
			this.isSelectedColumn = isSelectedColumn;
			this.isAdjust = isAdjust;
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			// Handle selected column(s) width change actions
			if (this.isSelectedColumn) {
				int[] columns = table.getSelectedColumns();

				for (int i = 0; i < columns.length; i++) {
					if (this.isAdjust) {
						adjustColumn(columns[i]);
					} else restoreColumn(columns[i]);
				}
			} else {
				if (this.isAdjust) {
					adjustColumns();
				} else {
					restoreColumns();
				}
			}
		}
	}

	/**
	 * Toggle properties of the {@link TableColumnAdjuster} so the user can
	 * customize the functionality to their preferences.
	 * 
	 * @author LE SAUCE Julien
	 * @version 1.0
	 */
	class ToggleAction extends AbstractAction {

		private static final long serialVersionUID = 1936147391564067381L;
		private boolean isToggleDynamic;
		private boolean isToggleLarger;

		/**
		 * Permet d'instancier une action.
		 * 
		 * @param isToggleDynamic
		 * @param isToggleLarger
		 */
		public ToggleAction (boolean isToggleDynamic, boolean isToggleLarger) {
			this.isToggleDynamic = isToggleDynamic;
			this.isToggleLarger = isToggleLarger;
		}

		@Override
		public void actionPerformed (ActionEvent e) {
			if (this.isToggleDynamic) {
				setDynamicAdjustment(!isDynamicAdjustment);
				return;
			}
			if (this.isToggleLarger) {
				setOnlyAdjustLarger(!isOnlyAdjustLarger);
				return;
			}
		}
	}
}