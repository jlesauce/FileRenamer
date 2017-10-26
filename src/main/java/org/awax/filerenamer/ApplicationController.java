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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awax.filerenamer.util.FileFilter;
import org.awax.filerenamer.util.FileInfo;
import org.awax.filerenamer.util.MalformedTagException;
import org.awax.filerenamer.util.Tag;

/**
 * Contrôleur de l'application.
 *
 * @author AwaX
 * @created 24 oct. 2014
 * @version 1.0
 */
public class ApplicationController {

    private final ApplicationModel model;
    private final ApplicationView view;
    private final Logger logger;

    /**
     * Permet d'instancier le contrôleur de l'application.
     *
     * @param model
     *            Modèle de données de l'application.
     */
    public ApplicationController(final ApplicationModel model) {
        this.model = model;
        this.view = new ApplicationView(model, this);
        this.logger = LogManager.getLogger();
    }

    /**
     * Permet d'afficher l'interface graphique.
     */
    public void showGui () {
        this.view.showGui();
    }

    /**
     * Permet de filtrer les fichiers affichés dans la table d'affichage de la
     * sélection en cours puis de mettre à jour le modèle de données. Si aucun
     * filtre n'est spécifié, c'est-à-dire que la valeur <code>null</code> est
     * passée en argument, alors aucun filtrage n'est appliqué et la valeur
     * <code>null</code> est renvoyée.
     *
     * @param filter
     *            Filtre à appliquer sur la liste de fichiers. Si la valeur
     *            <code>null</code> est spécifiée, aucun filtrage n'est appliqué.
     * @return Nouvelle liste de fichier après filtrage ou <code>null</code> si
     *         aucun filtre n'est spécifié.
     */
    public ArrayList<FileInfo> applyFileFilter (final FileFilter filter) {
        if (filter != null) {
            this.logger.debug("Aplying filter : " + filter.toString());
            ArrayList<FileInfo> acceptedFiles = new ArrayList<>();
            for (FileInfo file : this.model.getCurrentFileSelection()) {
                if (filter.accept(file.getFile())) {
                    acceptedFiles.add(file);
                }
            }
            this.model.setCurrentFileSelection(acceptedFiles);
            return acceptedFiles;
        }
        this.logger.debug("Filter ignored because it is null");
        return null;
    }

    /**
     * Permet de renommer la sélection en cours.
     *
     * @param pattern
     *            Pattern de renommage sans extension pouvant contenir des
     *            {@link Tag}.
     * @param preview
     *            <code>true</code> pour uniquement afficher le résultat sans
     *            modifier les fichiers, <code>false</code> pour appliquer
     *            définitivement le nouveau nom.
     * @throws MalformedTagException
     *             Si une erreur dans l'écriture des tags est détectée, une
     *             exception est levée.
     */
    public void renameCurrentSelection (final String pattern, final boolean preview) throws MalformedTagException {
        /*
         * Détection des tags
         */
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(pattern);
        ArrayList<Tag> tags = new ArrayList<>();
        int cpt = 0;
        while (m.find()) {
            Tag tag = null;
            // Extraction du tag
            String tagStr = m.group(1);
            if (tagStr.contains("{") || tagStr.contains("}")) {
                throw new MalformedTagException("Nested brackets detected : " + tagStr);
            }
            // Détection du tag
            try {
                tag = Tag.valueOf(tagStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new MalformedTagException("Unknown tag : " + tagStr, e);
            }
            // Ajout du tag
            tags.add(tag);
            cpt++;
        }
        // Si on détecte une accollade mais que le matcher n'a rien trouvé
        if (cpt == 0 && (pattern.contains("{") || pattern.contains("}"))) {
            throw new MalformedTagException("Open bracket detected : " + pattern);
        }
        /*
         * Balayage de la sélection
         */
        ArrayList<FileInfo> currentSelection = this.model.getCurrentFileSelection();
        cpt = 0;
        for (FileInfo file : currentSelection) {
            String filename = pattern;
            // Construction du pattern
            for (Tag tag : tags) {
                filename = filename.replace(tag.toTagString(), computeTag(tag, file, cpt));
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

    /**
     * Permet de réinitialiser la liste des fichiers affichés au moment de leur
     * sélection par l'utilisateur dans l'arbre d'exploration des dossiers.
     */
    public void resetFilters () {
        this.model.setCurrentFileSelection(this.model.getFileSelection());
    }

    /**
     * Permet de générer la valeur du tag spécifié.
     *
     * @param tag
     *            Tag à générer.
     * @param file
     *            Information sur le fichier ciblé.
     * @param iFile
     *            Indice du fichier dans la sélection.
     * @return Valeur associée au tag spécifié.
     */
    private static String computeTag (final Tag tag, final FileInfo file, final int iFile) {
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
                return FilenameUtils.getBaseName(file.getDisplayName());
            case IMPORTED_NAME:
                return FilenameUtils.getBaseName(file.getDisplayName());
            default:
                throw new IllegalArgumentException("Illegal tag : " + tag);
        }
    }

    public ApplicationView getView () {
        return this.view;
    }
}
