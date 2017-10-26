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

import net.miginfocom.swing.MigLayout;

/**
 * Panneau permettant à l'utilisateur de configurer l'ajout d'une variable dans
 * un pattern de renommage.
 * 
 * @author Julien
 * @created 1 nov. 2014
 * @version 1.0
 */
public class AddVarDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 5274275835367952060L;

	public static final int CANCEL_OPTION = 0;
	public static final int APPROVE_OPTION = 1;

	private int returnOption;

	private JLabel lblType;
	private JLabel lblNbDigits;
	private JLabel lblInitialValue;
	private JComboBox<String> boxType;
	private JSpinner spNbDigits;
	private JSpinner spInitialValue;
	private JButton btnOk;
	private JButton btnCancel;

	/**
	 * Permet d'instancier un panneau de configuration d'une variable
	 * automatique.
	 * 
	 */
	public AddVarDialog () {
		super();
		this.returnOption = CANCEL_OPTION;
		setTitle("Add Variable");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		createComponents();
		createGui();
		addListeners();
	}

	/**
	 * Permet d'instancier les différents éléments qui composent l'interface
	 * graphique.
	 */
	private void createComponents () {
		this.lblType = new JLabel("Type :");
		this.lblNbDigits = new JLabel("Nb Digits :");
		this.lblInitialValue = new JLabel("Initial Value :");
		String[] types = {"Integer", "Letter" };
		this.boxType = new JComboBox<String>(types);
		this.spNbDigits = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
		this.spInitialValue = new JSpinner(new SpinnerNumberModel(0, 0, (int) (Math.pow(10, 4) - 1), 1));
		this.btnOk = new JButton("OK");
		this.btnCancel = new JButton("Cancel");
	}

	/**
	 * Permet de créer l'interface graphique à partir de tous les éléments qui
	 * la compose.
	 */
	private void createGui () {
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

	/**
	 * Permet d'ajouter les différents écouteurs aux composants de l'interface
	 * graphique.
	 */
	private void addListeners () {
		this.btnOk.addActionListener(this);
		this.btnCancel.addActionListener(this);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		/*
		 * JButton
		 */
		if (e.getSource() instanceof JButton) {
			JButton btn = (JButton) e.getSource();

			// OK
			if (this.btnOk.equals(btn)) {
				this.returnOption = APPROVE_OPTION;
				dispose();
			}
			// Cancel
			else if (this.btnCancel.equals(btn)) {
				this.returnOption = CANCEL_OPTION;
				dispose();
			}
		}
	}

	/**
	 * Renvoie le nombre de digits à afficher.
	 * 
	 * @return Nombre de digits à afficher.
	 */
	public int getNbDigits () {
		return Integer.parseInt(this.spNbDigits.getValue().toString());
	}

	/**
	 * Renvoie la valeur initiale de la variable.
	 * 
	 * @return Valeur initiale de la variable.
	 */
	public Object getValue () {
		return this.spInitialValue.getValue();
	}

	/**
	 * Renvoie le type de variable.
	 * 
	 * @return Type de variable.
	 */
	public String getVarType () {
		return this.boxType.getSelectedItem().toString();
	}

	/**
	 * Renvoie l'action effectuée par l'utilisateur.
	 * 
	 * @return Action effectuée par l'utilisateur.
	 */
	public int getReturnOption () {
		return returnOption;
	}
}