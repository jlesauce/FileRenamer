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

/**
 * Exception lancée si une chaîne contenant un tag est mal construite.
 * 
 * @author AwaX
 * @created 28 oct. 2014
 * @version 1.0
 */
public class MalformedTagException extends Exception {

	private static final long serialVersionUID = 53561047896779373L;

	/**
	 * Permet d'instancier une {@link MalformedTagException} sans description.
	 */
	public MalformedTagException () {
		super();
	}

	/**
	 * Permet d'instancier une {@link MalformedTagException} en précisant une
	 * description de l'erreur.
	 * 
	 * @param msg
	 *            Description de l'erreur.
	 */
	public MalformedTagException (final String msg) {
		super(msg);
	}

	/**
	 * Permet d'instancier une {@link MalformedTagException} en précisant la
	 * cause de l'erreur.
	 * 
	 * @param cause
	 *            Cause de l'erreur (sauvegardée par la méthode
	 *            {@link #getCause()}) (La valeur <code>null</code> est
	 *            autorisée, et permet d'indiquer que la cause est inconnue ou
	 *            inexistante).
	 */
	public MalformedTagException (final Throwable cause) {
		super(cause);
	}

	/**
	 * Permet d'instancier une {@link MalformedTagException} en précisant la
	 * cause de l'erreur et sa description.
	 * 
	 * @param msg
	 *            Description de l'erreur.
	 * @param cause
	 *            Cause de l'erreur (sauvegardée par la méthode
	 *            {@link #getCause()}) (La valeur <code>null</code> est
	 *            autorisée, et permet d'indiquer que la cause est inconnue ou
	 *            inexistante).
	 */
	public MalformedTagException (final String msg, final Throwable cause) {
		super(msg, cause);
	}
}