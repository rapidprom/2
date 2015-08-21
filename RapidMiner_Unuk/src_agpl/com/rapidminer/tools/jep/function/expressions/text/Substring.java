/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2013 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.tools.jep.function.expressions.text;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import com.rapidminer.tools.expression.parser.UnknownValue;

/**
 * Calculates the substring of the given string and pushed it on the result stack.
 * 
 * @author Sebastian Land
 */
public class Substring extends PostfixMathCommand {

	public Substring() {
		numberOfParameters = 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(Stack stack) throws ParseException {
		checkStack(stack);// check the stack

		// initialize the result to the first argument
		Object length = stack.pop();
		Object start = stack.pop();
		Object stringObject = stack.pop();

		// checking for unknown value
		if (stringObject == UnknownValue.UNKNOWN_NOMINAL) {
			stack.push(UnknownValue.UNKNOWN_NOMINAL);
			return;
		}

		if (!(stringObject instanceof String) || !(start instanceof Double) || !(length instanceof Double)) {
			throw new ParseException(
					"Invalid argument type, must be (string, number, number)");
		}
		int startI = ((Double) start).intValue();
		int lenI = ((Double) length).intValue();
		String string = (String) stringObject;
		try {
			stack.push(string.substring(startI, startI + lenI));
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid argument value: Start and length exceed given string: " + string);
		}
	}
}
