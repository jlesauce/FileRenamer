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
package org.jls.filerenamer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jls.filerenamer.ApplicationController;
import org.jls.filerenamer.util.FileFilter;
import org.jls.filerenamer.util.InvalidFilterException;
import org.jls.filerenamer.util.ResourceManager;

import net.miginfocom.swing.MigLayout;

public class FileFiltersPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 3484339236357878437L;

    private final ApplicationController controller;
    private final Logger logger;
    private final ResourceManager props;

    private JLabel lblStartsWith;
    private JLabel lblEndsWith;
    private JLabel lblContains;
    private JLabel lblFileExtension;
    private JLabel lblRegex;

    private JTextField tfStartsWith;
    private JTextField tfEndsWith;
    private JTextField tfContains;
    private JTextField tfFileExtension;
    private JTextField tfRegex;

    private JButton btnApplyFilter;
    private JButton btnClear;
    private JButton btnReset;

    public FileFiltersPanel(final ApplicationController controller) {
        super();
        this.controller = controller;
        this.logger = LogManager.getLogger();
        this.props = ResourceManager.getInstance();
        createComponents();
        createGui();
        addListeners();
    }

    public void pop(final String title, final String msg, final int type) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(controller.getView(), msg, title, type));
    }

    private void createComponents() {
        this.lblStartsWith = new JLabel(this.props.getString("fileFiltersPanel.label.startsWith"));
        this.lblEndsWith = new JLabel(this.props.getString("fileFiltersPanel.label.endsWith"));
        this.lblContains = new JLabel(this.props.getString("fileFiltersPanel.label.contains"));
        this.lblFileExtension = new JLabel(this.props.getString("fileFiltersPanel.label.fileExtension"));
        this.lblRegex = new JLabel(this.props.getString("fileFiltersPanel.label.regex"));
        this.tfStartsWith = new JTextField();
        this.tfEndsWith = new JTextField();
        this.tfContains = new JTextField();
        this.tfFileExtension = new JTextField();
        this.tfRegex = new JTextField();
        this.btnApplyFilter = new JButton(this.props.getString("fileFiltersPanel.button.applyFilter.label"));
        this.btnClear = new JButton(this.props.getString("fileFiltersPanel.button.clear.label"));
        this.btnReset = new JButton(this.props.getString("fileFiltersPanel.button.reset.label"));
    }

    private void createGui() {
        setLayout(new MigLayout("insets 20 20 20 20", "[][100]15[][100]15[][100]", "[][]15[]"));
        add(this.lblStartsWith, "");
        add(this.tfStartsWith, "grow");
        add(this.lblEndsWith, "");
        add(this.tfEndsWith, "grow");
        add(this.lblContains, "");
        add(this.tfContains, "grow, wrap");
        add(this.lblFileExtension, "");
        add(this.tfFileExtension, "grow");
        add(this.lblRegex, "");
        add(this.tfRegex, "grow, wrap");
        add(this.btnApplyFilter, "split 3, span, center");
        add(this.btnClear, "");
        add(this.btnReset, "");
    }

    private void addListeners() {
        this.btnApplyFilter.addActionListener(this);
        this.btnClear.addActionListener(this);
        this.btnReset.addActionListener(this);
    }

    private static FileFilter computeFileFilter(final String startsWith, final String endsWith, final String contains,
                                                final String extensionFilter, final String regex) throws InvalidFilterException {
        String starting = startsWith.isEmpty() ? null : startsWith;
        String ending = endsWith.isEmpty() ? null : endsWith;
        String containing = contains.isEmpty() ? null : contains;
        String matching = regex.isEmpty() ? null : regex;
        try {
            FileNameExtensionFilter fileNameFilter = null;
            if (extensionFilter != null && !extensionFilter.isEmpty()) {
                String[] extensions = extensionFilter.split("[.,;:]");
                fileNameFilter = new FileNameExtensionFilter("File extension filter", extensions);
            }
            if (starting == null && ending == null && containing == null && matching == null
                    && fileNameFilter == null) {
                return null;
            }
            return new FileFilter(starting, ending, containing, fileNameFilter, matching);
        } catch (Exception e) {
            throw new InvalidFilterException(
                    ResourceManager.getInstance().getString("fileFiltersPanel.error.invalidExtensionFileFilter"), e);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            JButton btn = (JButton) e.getSource();

            if (this.btnApplyFilter.equals(btn)) {
                String startsWith = this.tfStartsWith.getText();
                String endsWith = this.tfEndsWith.getText();
                String contains = this.tfContains.getText();
                String extensionFilter = this.tfFileExtension.getText().trim().replaceAll(" ", "");
                String regex = this.tfRegex.getText();
                try {
                    FileFilter filter = computeFileFilter(startsWith, endsWith, contains, extensionFilter, regex);
                    this.controller.applyFileFilter(filter);
                } catch (InvalidFilterException e1) {
                    this.logger.error("At least one filter parameter is invalid", e1);
                    pop(this.props.getString("fileFiltersPanel.error.invalidFilterParam.header"),
                            this.props.getString("fileFiltersPanel.error.invalidFilterParam") + "\n\n"
                                    + e1.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (this.btnClear.equals(btn)) {
                this.tfStartsWith.setText("");
                this.tfEndsWith.setText("");
                this.tfContains.setText("");
                this.tfFileExtension.setText("");
                this.tfRegex.setText("");
            } else if (this.btnReset.equals(btn)) {
                this.controller.resetFilters();
            }
        }
    }
}
