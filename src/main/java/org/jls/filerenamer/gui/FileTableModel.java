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

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.FilenameUtils;
import org.jls.filerenamer.util.FileInfo;

public class FileTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 2054550595249194702L;

    private final String[] columns = { "Icon", "Ext", "Name", "Final Name", "Path" };
    private final ArrayList<FileInfo> fileInfoList;

    private boolean showOnlyDirectories;
    private boolean showOnlyFiles;

    public FileTableModel(final ArrayList<FileInfo> files) {
        super();
        fileInfoList = files;
        showOnlyDirectories = false;
        showOnlyFiles = false;
    }

    public void updateTableData (final ArrayList<FileInfo> files) {
        fileInfoList.clear();
        addFilesToTable(files);
        fireTableDataChanged();
    }

    private void addFilesToTable (final ArrayList<FileInfo> files) {
        for (FileInfo file : files) {
            if (file.getFile().isDirectory() && !showOnlyFiles) {
                fileInfoList.add(file);
            } else if (!showOnlyDirectories) {
                fileInfoList.add(file);
            }
        }
    }

    public void setShowOnlyDirectories (final boolean onlyDirectories) {
        showOnlyDirectories = onlyDirectories;
        showOnlyFiles = !onlyDirectories;
    }

    public void setShowOnlyFiles (final boolean onlyFiles) {
        showOnlyFiles = onlyFiles;
        showOnlyDirectories = !onlyFiles;
    }

    @Override
    public int getRowCount () {
        return fileInfoList != null ? fileInfoList.size() : 0;
    }

    @Override
    public int getColumnCount () {
        return columns != null ? columns.length : 0;
    }

    @Override
    public Object getValueAt (final int rowIndex, final int columnIndex) {
        if (rowIndex >= 0 && rowIndex < getRowCount() && columnIndex >= 0 && columnIndex < getColumnCount()) {
            FileInfo file = fileInfoList.get(rowIndex);
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
    public Class<?> getColumnClass (final int columnIndex) {
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
    public String getColumnName (final int column) {
        if (column >= 0 && column < getColumnCount()) {
            return columns[column];
        }
        return null;
    }

    @Override
    public boolean isCellEditable (final int rowIndex, final int columnIndex) {
        return false;
    }

    @Override
    public void setValueAt (final Object aValue, final int rowIndex, final int columnIndex) {
    }
}
