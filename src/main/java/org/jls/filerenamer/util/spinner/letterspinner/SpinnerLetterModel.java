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
package org.jls.filerenamer.util.spinner.letterspinner;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SpinnerListModel;

/**
 * Permet d'implémenter un {@link SpinnerListModel} permettant de créer un
 * spinner affichant les lettres de A à Z. Lorsque la lettre Z est atteinte
 * alors on ajoute un digit (Exemple : AZ) ce qui revient en fait à compter en
 * base 26.
 *
 * @author Julien
 * @created 1 nov. 2014
 * @version 1.0
 */
public class SpinnerLetterModel extends SpinnerListModel {

    private static final long serialVersionUID = -8797552703384030419L;

    private final ArrayList<Character> list;
    private Character value;

    /**
     * Permet d'instancier un modèle.
     *
     * @param value
     *            Valeur initiale du modèle.
     */
    public SpinnerLetterModel(final char value) {
        this.list = new ArrayList<Character>();
        for (int i = 'a'; i <= 'z'; i++) {
            this.list.add((char) ('a' + i));
        }
        this.value = value;
    }

    @Override
    public List<?> getList () {
        ArrayList<Character> newList = new ArrayList<>();
        for (char c : this.list) {
            newList.add(c);
        }
        return newList;
    }

    @Override
    public Object getValue () {
        return this.value;
    }

    @Override
    public void setValue (final Object elt) {
        this.value = (Character) elt;
    }

    @Override
    public Object getNextValue () {
        return super.getNextValue();
    }

    @Override
    public Object getPreviousValue () {
        return super.getPreviousValue();
    }
}
