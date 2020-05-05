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

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

    private final JTable table;

    private final int spacing;
    private final Map<TableColumn, Integer> columnSizes = new HashMap<>();

    private boolean isColumnHeaderIncluded;
    private boolean isColumnDataIncluded;
    private boolean isOnlyAdjustLarger;
    private boolean isDynamicAdjustment;

    public TableColumnAdjuster(JTable table) {
        this(table, 6);
    }

    public TableColumnAdjuster(JTable table, int spacing) {
        this.table = table;
        this.spacing = spacing;
        setColumnHeaderIncluded(true);
        setColumnDataIncluded(true);
        setOnlyAdjustLarger(false);
        setDynamicAdjustment(false);
        installActions();
    }

    public void adjustColumns() {
        TableColumnModel columnModel = this.table.getColumnModel();

        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            adjustColumn(i);
        }

        this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
    }

    public void adjustColumn(final int column) {
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

    public void restoreColumns() {
        TableColumnModel tcm = this.table.getColumnModel();

        for (int i = 0; i < tcm.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    private int getColumnHeaderWidth(int column) {
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

    private int getColumnDataWidth(int column) {
        if (!this.isColumnDataIncluded) {
            return 0;
        }
        int preferredWidth = 0;
        int maxWidth = this.table.getColumnModel().getColumn(column).getMaxWidth();

        for (int row = 0; row < this.table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));
            if (preferredWidth >= maxWidth) {
                break;
            }
        }
        return preferredWidth;
    }

    private int getCellDataWidth(int row, int column) {
        TableCellRenderer cellRenderer = this.table.getCellRenderer(row, column);
        Component c = this.table.prepareRenderer(cellRenderer, row, column);
        return c.getPreferredSize().width + this.table.getIntercellSpacing().width;
    }

    private void updateTableColumn(int column, int w) {
        final TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

        if (!tableColumn.getResizable()) {
            return;
        }
        int width = w + this.spacing;
        if (this.isOnlyAdjustLarger) {
            width = Math.max(width, tableColumn.getPreferredWidth());
        }

        this.columnSizes.put(tableColumn, tableColumn.getWidth());
        this.table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }

    private void restoreColumn(int column) {
        TableColumn tableColumn = this.table.getColumnModel().getColumn(column);
        Integer width = this.columnSizes.get(tableColumn);

        if (width != null) {
            this.table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width);
        }
    }

    private void installActions() {
        installColumnAction(true, true, "adjustColumn", "control ADD");
        installColumnAction(false, true, "adjustColumns", "control shift ADD");
        installColumnAction(true, false, "restoreColumn", "control SUBTRACT");
        installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT");
        installToggleAction(true, false, "toggleDynamic", "control MULTIPLY");
        installToggleAction(false, true, "toggleLarger", "control DIVIDE");
    }

    private void installColumnAction(boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
        Action action = new ColumnAction(isSelectedColumn, isAdjust);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    private void installToggleAction(boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
        Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
        KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
        table.getInputMap().put(ks, key);
        table.getActionMap().put(key, action);
    }

    public void setColumnHeaderIncluded(boolean isColumnHeaderIncluded) {
        this.isColumnHeaderIncluded = isColumnHeaderIncluded;
    }

    public void setColumnDataIncluded(boolean isColumnDataIncluded) {
        this.isColumnDataIncluded = isColumnDataIncluded;
    }

    public void setOnlyAdjustLarger(boolean isOnlyAdjustLarger) {
        this.isOnlyAdjustLarger = isOnlyAdjustLarger;
    }

    public void setDynamicAdjustment(boolean isDynamicAdjustment) {
        if (this.isDynamicAdjustment != isDynamicAdjustment) {
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
    public void propertyChange(PropertyChangeEvent e) {
        if ("model".equals(e.getPropertyName())) {
            TableModel model = (TableModel) e.getOldValue();
            model.removeTableModelListener(this);

            model = (TableModel) e.getNewValue();
            model.addTableModelListener(this);
            adjustColumns();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (!this.isColumnDataIncluded) {
            return;
        }
        if (e.getType() == TableModelEvent.UPDATE) {
            int column = this.table.convertColumnIndexToView(e.getColumn());

            if (column < 0) {
                return;
            }
            if (this.isOnlyAdjustLarger) {
                int row = e.getFirstRow();
                TableColumn tableColumn = this.table.getColumnModel().getColumn(column);

                if (tableColumn.getResizable()) {
                    int width = getCellDataWidth(row, column);
                    updateTableColumn(column, width);
                }
            }
            else {
                adjustColumn(column);
            }
        }
        else {
            adjustColumns();
        }
    }

    class ColumnAction extends AbstractAction {

        private static final long serialVersionUID = 8373114505358954905L;

        private final boolean isSelectedColumn;
        private final boolean isAdjust;

        public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
            this.isSelectedColumn = isSelectedColumn;
            this.isAdjust = isAdjust;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isSelectedColumn) {
                int[] columns = table.getSelectedColumns();

                for (int column : columns) {
                    if (this.isAdjust) {
                        adjustColumn(column);
                    } else restoreColumn(column);
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

    class ToggleAction extends AbstractAction {

        private static final long serialVersionUID = 1936147391564067381L;
        private final boolean isToggleDynamic;
        private final boolean isToggleLarger;

        public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
            this.isToggleDynamic = isToggleDynamic;
            this.isToggleLarger = isToggleLarger;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.isToggleDynamic) {
                setDynamicAdjustment(!isDynamicAdjustment);
                return;
            }
            if (this.isToggleLarger) {
                setOnlyAdjustLarger(!isOnlyAdjustLarger);
            }
        }
    }
}
