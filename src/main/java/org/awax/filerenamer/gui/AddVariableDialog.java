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
package org.awax.filerenamer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.awax.filerenamer.util.ResourceManager;

import net.miginfocom.swing.MigLayout;

public class AddVariableDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 5572840744028343980L;

    public static final int CANCEL_OPTION = 0;
    public static final int APPROVE_OPTION = 1;

    private final ResourceManager props;
    private int returnOption;

    private JLabel lblType;
    private JLabel lblNbDigits;
    private JLabel lblInitialValue;
    private JComboBox<String> boxType;
    private JSpinner spNbDigits;
    private JSpinner spInitialValue;
    private JButton btnOk;
    private JButton btnCancel;

    public AddVariableDialog() {
        super();
        props = ResourceManager.getInstance();
        returnOption = CANCEL_OPTION;
        setTitle(props.getString("addVarDialog.panel.title"));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        createGuiComponents();
        addGuiComponents();
        addGuiListeners();
    }

    private void createGuiComponents () {
        this.lblType = new JLabel(props.getString("addVarDialog.label.type"));
        this.lblNbDigits = new JLabel(props.getString("addVarDialog.label.nbDigits"));
        this.lblInitialValue = new JLabel(props.getString("addVarDialog.label.initialValue"));
        this.boxType = new JComboBox<String>(getVariableTypes());
        this.spNbDigits = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        this.spInitialValue = new JSpinner(new SpinnerNumberModel(0, 0, (int) (Math.pow(10, 4) - 1), 1));
        this.btnOk = new JButton(props.getString("common.label.ok"));
        this.btnCancel = new JButton(props.getString("common.label.cancel"));
    }

    private String[] getVariableTypes () {
        String[] types = { "Integer", "Letter" };
        return types;
    }

    private void addGuiComponents () {
        setLayout(new MigLayout("insets 20 20 20 20", "[][75]", "[][][]25[]"));
        add(this.lblType, "");
        add(this.boxType, "grow, wrap");
        add(this.lblNbDigits, "");
        add(this.spNbDigits, "grow, wrap");
        add(this.lblInitialValue, "");
        add(this.spInitialValue, "grow, wrap");
        add(this.btnOk, "split, span, center");
        add(this.btnCancel, "");
    }

    private void addGuiListeners () {
        this.btnOk.addActionListener(this);
        this.btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed (final ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            handleButtonAction((JButton) e.getSource());
        }
    }

    private void handleButtonAction (final JButton btn) {
        if (this.btnOk.equals(btn)) {
            handleButtonOk();
        } else if (this.btnCancel.equals(btn)) {
            handleButtonCancel();
        }
    }

    private void handleButtonOk () {
        returnOption = APPROVE_OPTION;
        dispose();
    }

    private void handleButtonCancel () {
        returnOption = CANCEL_OPTION;
        dispose();
    }

    public int getVariableNbOfDigits () {
        return Integer.parseInt(spNbDigits.getValue().toString());
    }

    public Object getVariableInitialValue () {
        return this.spInitialValue.getValue();
    }

    public String getVariableType () {
        return this.boxType.getSelectedItem().toString();
    }

    public int getDialogReturnOption () {
        return returnOption;
    }
}
