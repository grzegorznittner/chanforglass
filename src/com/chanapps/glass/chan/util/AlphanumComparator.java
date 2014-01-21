package com.chanapps.glass.chan.util;

/*
 * The Alphanum Algorithm is an improved sorting algorithm for strings
 * containing numbers.  Instead of sorting numbers in ASCII order like
 * a standard sort, this algorithm sorts numbers in numeric order.
 *
 * The Alphanum Algorithm is discussed at http ://www.DaveKoelle.com
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

import java.util.Comparator;

/**
 * This is an updated version with enhancements made by Daniel Migowski,
 * Andre Bogus, and David Koelle
 *
 * To convert to use Templates (Java 1.5+):
 *   - Change "implements Comparator" to "implements Comparator<String>"
 *   - Change "compare(Object o1, Object o2)" to "compare(String s1, String s2)"
 *   - Remove the type checking and casting in compare().
 *
 * To use this class:
 *   Use the static "sort" method from the java.util.Collections class:
 *   Collections.sort(your list, new AlphanumComparator());
 */

public class AlphanumComparator<T> implements Comparator<Stringy>
{
    @Override
    public int compare(Stringy p1, Stringy p2) {
        boolean isFirstNumeric, isSecondNumeric;
        
        String o1 = p1.toString();
        String o2 = p2.toString();

        isFirstNumeric = o1.matches("\\d+");
        isSecondNumeric = o2.matches("\\d+");

        if (isFirstNumeric && isSecondNumeric) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
        }
        else if (isFirstNumeric && !isSecondNumeric) {
            return 1; // numbers always larger than letters
        }
        else if (isSecondNumeric) {
            return -1; // numbers always larger than letters
        } else {
            String[] split1 = o1.split("[^0-9]");
            String[] split2 = o2.split("[^0-9]");
            isFirstNumeric = split1.length == 0 ? false : split1[0].matches("\\d+");
            isSecondNumeric = split2.length == 0 ? false : split2[0].matches("\\d+");

            if (isFirstNumeric &&isSecondNumeric) {
                    int intCompare = Integer.valueOf(o1.split("[^0-9]")[0])
                            .compareTo(Integer.valueOf(o2.split("[^0-9]")[0]));
                    if (intCompare == 0) {
                        return o1.compareToIgnoreCase(o2);
                    }
                    return intCompare;
            }
            else if (isFirstNumeric) {
                    return 1; // numbers always larger than letters
            }
            else if (isSecondNumeric) {
                return -1; // numbers always larger than letters
            } else {
                return o1.compareToIgnoreCase(o2);
            }
        }
    }
}
