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
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

public class FileFilter {

    private String startsWith;
    private String endsWith;
    private String contains;
    private FileNameExtensionFilter extensionFilter;
    private String regex;

    public FileFilter(String startsWith, String endsWith, String contains, FileNameExtensionFilter extensionFilter,
                      String regex) {
        super();
        this.startsWith = startsWith;
        this.endsWith = endsWith;
        this.contains = contains;
        this.extensionFilter = extensionFilter;
        this.regex = regex;
    }

    public boolean accept(File file) {
        String nameWithoutExt = FilenameUtils.removeExtension(file.getName());
        if (this.startsWith != null && !this.startsWith.isEmpty() && !nameWithoutExt.startsWith(this.startsWith)) {
            return false;
        }
        if (this.endsWith != null && !this.endsWith.isEmpty() && !nameWithoutExt.endsWith(this.endsWith)) {
            return false;
        }
        if (this.contains != null && !contains.isEmpty() && !nameWithoutExt.contains(this.contains)) {
            return false;
        }
        if (this.regex != null && !regex.isEmpty() && !nameWithoutExt.matches(regex)) {
            return false;
        }
        if (!file.isDirectory() && this.extensionFilter != null) {
            return this.extensionFilter.accept(file);
        }
        return true;
    }

    public ArrayList<File> filter(Collection<? extends File> files) {
        ArrayList<File> acceptedFiles = new ArrayList<>();
        for (File file : files) {
            if (accept(file)) {
                acceptedFiles.add(file);
            }
        }
        return acceptedFiles;
    }

    @Override
    public String toString() {
        String[] extensions = this.extensionFilter != null ? this.extensionFilter.getExtensions() : null;
        String exs = null;
        if (extensions != null) {
            exs = "";
            for (String ex : extensions) {
                exs += ex + ",";
            }
            exs = exs.substring(0, exs.length() - 1);
        }
        String instance = getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this));
        return "[" + instance + ", StartsWith=" + this.startsWith + ", EndsWith=" + this.endsWith + ", Contains="
                + this.contains + ", Matches=" + this.regex + ", ExtensionFilter=(" + exs + ")]";
    }
}
