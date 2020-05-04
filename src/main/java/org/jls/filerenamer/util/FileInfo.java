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

import java.io.File;

import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;

public class FileInfo {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();

    private File file;
    private String newName;

    public FileInfo(final File file) {
        this.file = file;
        this.newName = "";
    }

    public boolean renameTo(String filename) {
        File newFile = new File(this.file.getParent() + File.separator + filename);
        setNewName(filename);
        return this.file.renameTo(newFile);
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Icon getIcon() {
        return FILE_SYSTEM_VIEW.getSystemIcon(this.file);
    }

    public String getDisplayName() {
        return FILE_SYSTEM_VIEW.getSystemDisplayName(this.file);
    }

    public String getPath() {
        return this.file.getPath();
    }

    public String getExtension() {
        return FilenameUtils.getExtension(this.file.getName());
    }

    public long getLength() {
        return this.file.length();
    }

    public String getNewName() {
        return this.newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public String toString() {
        return "[" + this.file.toString() + "]";
    }
}
