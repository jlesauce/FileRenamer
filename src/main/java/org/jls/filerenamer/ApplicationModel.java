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
package org.jls.filerenamer;

import java.util.ArrayList;
import java.util.Observable;

import javax.swing.filechooser.FileSystemView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.util.FileInfo;

public class ApplicationModel extends Observable {

    private final Logger logger;
    private final String appName;
    private final FileSystemView fileSystemView;
    private ArrayList<FileInfo> fileSelection;
    private ArrayList<FileInfo> currentFileSelection;

    public ApplicationModel() {
        logger = LogManager.getLogger();
        appName = "File Renamer";
        fileSystemView = FileSystemView.getFileSystemView();
        fileSelection = new ArrayList<>();
        currentFileSelection = new ArrayList<>();
    }

    public void notifyChanged (final Object... obj) {
        setChanged();
        if (obj == null || obj.length == 0) {
            notifyObservers();
        } else if (obj.length == 1) {
            notifyObservers(obj);
        } else {
            throw new IllegalArgumentException("Only one argument accepted");
        }
        clearChanged();
    }

    public String getAppName () {
        return appName;
    }

    public FileSystemView getFileSystemView () {
        return fileSystemView;
    }

    public ArrayList<FileInfo> getFileSelection () {
        return fileSelection;
    }

    public void setFileSelection (final ArrayList<FileInfo> fileSelection) {
        logger.debug("Updating file selection");
        this.fileSelection = fileSelection;
        setCurrentFileSelection(fileSelection);
    }

    public ArrayList<FileInfo> getCurrentFileSelection () {
        return currentFileSelection;
    }

    public void setCurrentFileSelection (final ArrayList<FileInfo> fileSelection) {
        logger.debug("Updating current file selection");
        currentFileSelection = fileSelection;
        notifyChanged(fileSelection);
    }
}
