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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.util.FileFilter;
import org.jls.filerenamer.util.FileInfo;
import org.jls.filerenamer.util.MalformedTagException;
import org.jls.filerenamer.util.Tag;

public class ApplicationController {

    private final ApplicationModel model;
    private final ApplicationView view;
    private final Logger logger;

    public ApplicationController(final ApplicationModel model) {
        this.model = model;
        this.view = new ApplicationView(model, this);
        this.logger = LogManager.getLogger();
    }

    public void showGui() {
        this.view.showGui();
    }

    public void applyFileFilter(final FileFilter filter) {
        if (filter != null) {
            this.logger.debug("Aplying filter : " + filter.toString());
            ArrayList<FileInfo> acceptedFiles = new ArrayList<>();
            for (FileInfo file : this.model.getCurrentFileSelection()) {
                if (filter.accept(file.getFile())) {
                    acceptedFiles.add(file);
                }
            }
            this.model.setCurrentFileSelection(acceptedFiles);
            return;
        }
        this.logger.debug("Filter ignored because it is null");
    }

    public void renameCurrentSelection(final String pattern, final boolean preview) throws MalformedTagException {
        Matcher m = Pattern.compile("\\{(.*?)}").matcher(pattern);
        ArrayList<Tag> tags = new ArrayList<>();
        int cpt = 0;
        while (m.find()) {
            Tag tag;
            String tagStr = m.group(1);
            if (tagStr.contains("{") || tagStr.contains("}")) {
                throw new MalformedTagException("Nested brackets detected : " + tagStr);
            }
            try {
                tag = Tag.valueOf(tagStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MalformedTagException("Unknown tag : " + tagStr, e);
            }
            tags.add(tag);
            cpt++;
        }

        if (cpt == 0 && (pattern.contains("{") || pattern.contains("}"))) {
            throw new MalformedTagException("Open bracket detected : " + pattern);
        }

        ArrayList<FileInfo> currentSelection = this.model.getCurrentFileSelection();
        cpt = 0;
        for (FileInfo file : currentSelection) {
            String filename = pattern;
            // Construction du pattern
            for (Tag tag : tags) {
                filename = filename.replace(tag.toTagString(), computeTag(tag, file));
            }
            if (preview) {
                file.setNewName(filename + "." + file.getExtension());
            } else {
                file.renameTo(filename + "." + file.getExtension());
            }
            cpt++;
        }
        this.model.notifyChanged(this.model.getCurrentFileSelection());
    }

    public void resetFilters() {
        this.model.setCurrentFileSelection(this.model.getFileSelection());
    }

    private static String computeTag(final Tag tag, final FileInfo file) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat yearSmall = new SimpleDateFormat("yy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        switch (tag) {
            case DATE:
                return date.format(cal.getTime());
            case YEAR:
                return year.format(cal.getTime());
            case YEAR_SMALL:
                return yearSmall.format(cal.getTime());
            case MONTH:
                return month.format(cal.getTime());
            case DAY:
                return day.format(cal.getTime());
            case DIR_NAME:
                return file.getFile().getParentFile().getName();
            case FILE_NAME:
            case IMPORTED_NAME:
                return FilenameUtils.getBaseName(file.getDisplayName());
            default:
                throw new IllegalArgumentException("Illegal tag : " + tag);
        }
    }

    public ApplicationView getView() {
        return this.view;
    }
}
