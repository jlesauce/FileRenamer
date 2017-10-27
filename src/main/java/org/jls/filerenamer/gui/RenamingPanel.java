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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.ApplicationController;
import org.jls.filerenamer.util.MalformedTagException;
import org.jls.filerenamer.util.Tag;

import net.miginfocom.swing.MigLayout;

/**
 * Panneau permettant à l'utilisateur de renommer les fichiers de la sélection
 * en cours.
 *
 * @author AwaX
 * @created 28 oct. 2014
 * @version 1.0
 */
public class RenamingPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6283254928957349454L;

    private final ApplicationController controller;
    private final Logger logger;

    private JLabel lblPattern;
    private JTextField tfPattern;
    private JTextField tfExtension;
    private JCheckBox cbChangeExtension;
    private JButton btnAddTag;
    private JButton btnAddVar;
    private JButton btnPreview;
    private JButton btnApply;
    private JButton btnClear;
    private JButton btnRevert;
    private JTable tagTable;

    /**
     * Permet d'instancier le panneau de renommage des fichiers.
     *
     * @param controller
     *            Contrôleur de l'application.
     */
    public RenamingPanel(final ApplicationController controller) {
        super();
        this.controller = controller;
        this.logger = LogManager.getLogger();
        createComponents();
        createGui();
        addListeners();
    }

    /**
     * Permet d'afficher un message à l'utilisateur de type fenêtre pop-up.
     *
     * @param title
     *            Titre de la fenêtre de message.
     * @param msg
     *            Message à afficher.
     * @param type
     *            Type de message à afficher (voir {@link JOptionPane} :
     *            ERROR_MESSAGE, INFORMATION_MESSAGE, WARNING_MESSAGE,
     *            QUESTION_MESSAGE, or PLAIN_MESSAGE.
     */
    public void pop (final String title, final String msg, final int type) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run () {
                JOptionPane.showMessageDialog(controller.getView(), msg, title, type);
            }
        });
    }

    /**
     * Permet d'instancier les différents éléments qui composent l'interface
     * graphique.
     */
    private void createComponents () {
        this.lblPattern = new JLabel("Pattern :");
        this.tfPattern = new JTextField();
        this.tfExtension = new JTextField();
        this.cbChangeExtension = new JCheckBox("Change Extension");
        this.btnAddTag = new JButton("Add Tag");
        this.btnAddVar = new JButton("Add Var");
        this.btnPreview = new JButton("Preview");
        this.btnApply = new JButton("Apply");
        this.btnClear = new JButton("Clear");
        this.btnRevert = new JButton("Revert");
        // Table des tags
        String[] col = { "Tag" };
        String[][] tags = new String[Tag.values().length][1];
        for (int i = 0; i < Tag.values().length; i++) {
            tags[i][0] = Tag.values()[i].toString();
        }
        this.tagTable = new JTable(tags, col) {

            private static final long serialVersionUID = 8449088512822196165L;

            @Override
            public boolean isCellEditable (final int row, final int column) {
                return false;
            }
        };
        this.tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Permet de créer l'interface graphique à partir de tous les éléments qui la
     * compose.
     */
    private void createGui () {
        JScrollPane tagTableScroll = new JScrollPane(this.tagTable);
        tagTableScroll.setPreferredSize(new Dimension(150, 150));

        JPanel leftPanel = new JPanel(new MigLayout("", "[][300][][50]", "[][]20[]"));
        leftPanel.add(this.lblPattern, "");
        leftPanel.add(this.tfPattern, "grow");
        leftPanel.add(this.cbChangeExtension, "");
        leftPanel.add(this.tfExtension, "grow, wrap");
        leftPanel.add(this.btnAddTag, "split, span, right");
        leftPanel.add(this.btnAddVar, "wrap");
        leftPanel.add(this.btnPreview, "split 4, span, center");
        leftPanel.add(this.btnApply, "");
        leftPanel.add(this.btnClear, "");
        leftPanel.add(this.btnRevert, "");

        setLayout(new MigLayout("insets 20 20 20 20"));
        add(leftPanel, "");
        add(tagTableScroll, "spany, grow");
    }

    /**
     * Renvoie le tag sélectionné dans la table des tags ou <code>null</code> si
     * aucun tag n'a été sélectionné.
     *
     * @return Tag sélectionné dans la table des tags ou <code>null</code> si aucun
     *         tag n'a été sélectionné.
     */
    private Tag getSelectedTag () {
        int row = this.tagTable.getSelectedRow();
        if (row >= 0) {
            String tagStr = this.tagTable.getModel().getValueAt(row, 0).toString();
            return Tag.valueOf(tagStr);
        }
        return null;
    }

    /**
     * Permet d'insérer une variable dans le pattern de renommage.
     *
     * @param type
     *            Type de variable.
     * @param value
     *            Valeur initiale de la variable.
     * @param nbDigits
     *            Nombre de digits à afficher.
     */
    public void insertVariable (final String type, final Object value, final int nbDigits) {
        // Type entier
        if ("Integer".equals(type)) {
            this.tfPattern.setText(this.tfPattern.getText() + "{inc=%" + nbDigits + "d," + value + "}");
        }
    }

    /**
     * Permet d'ajouter les différents écouteurs aux composants de l'interface
     * graphique.
     */
    private void addListeners () {
        this.btnAddTag.addActionListener(this);
        this.btnAddVar.addActionListener(this);
        this.btnPreview.addActionListener(this);
        this.btnApply.addActionListener(this);
        this.btnClear.addActionListener(this);
        this.btnRevert.addActionListener(this);
    }

    @Override
    public void actionPerformed (final ActionEvent e) {
        /*
         * JButton
         */
        if (e.getSource() instanceof JButton) {
            JButton btn = (JButton) e.getSource();

            // Add Tag
            if (this.btnAddTag.equals(btn)) {
                String old = this.tfPattern.getText();
                int caret = this.tfPattern.getCaretPosition();
                Tag tag = getSelectedTag();
                String tagStr = tag != null ? tag.toTagString() : "";
                if (old.isEmpty()) {
                    this.tfPattern.setText(tagStr);
                } else if (caret == old.length()) {
                    this.tfPattern.setText(old + tagStr);
                } else if (caret >= 0 && caret < old.length()) {
                    String newStr = old.substring(0, caret) + tagStr + old.substring(caret, old.length());
                    this.tfPattern.setText(newStr);
                }
                this.tfPattern.requestFocus();
            }
            // Add Tag
            else if (this.btnAddVar.equals(btn)) {
                AddVariableDialog dialog = new AddVariableDialog();
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setModal(true);
                dialog.pack();
                dialog.setLocationRelativeTo(this.controller.getView());
                dialog.setVisible(true);
                if (dialog.getDialogReturnOption() == AddVariableDialog.APPROVE_OPTION) {
                    insertVariable(dialog.getVariableType(), dialog.getVariableInitialValue(), dialog.getVariableNbOfDigits());
                }
            }
            // Clear
            else if (this.btnClear.equals(btn)) {
                this.tfPattern.setText("");
            }
            // Preview
            else if (this.btnPreview.equals(btn)) {
                try {
                    this.controller.renameCurrentSelection(this.tfPattern.getText(), true);
                } catch (MalformedTagException e1) {
                    this.logger.error(
                            "Invalid pattern string : " + this.tfPattern.getText() + " (" + e1.getMessage() + ")");
                    pop("Pattern Error",
                            "Invalid pattern string : " + this.tfPattern.getText() + "\n\n" + e1.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            // Apply
            else if (this.btnApply.equals(btn)) {
                try {
                    this.controller.renameCurrentSelection(this.tfPattern.getText(), true);
                    this.controller.renameCurrentSelection(this.tfPattern.getText(), false);
                } catch (MalformedTagException e1) {
                    this.logger.error(
                            "Invalid pattern string : " + this.tfPattern.getText() + " (" + e1.getMessage() + ")");
                    pop("Pattern Error",
                            "Invalid pattern string : " + this.tfPattern.getText() + "\n\n" + e1.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
