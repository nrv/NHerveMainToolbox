/*
 * Copyright 2010, 2011 Institut Pasteur.
 * 
 * This file is part of NHerve Main Toolbox, which is an ICY plugin.
 * 
 * NHerve Main Toolbox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NHerve Main Toolbox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with NHerve Main Toolbox. If not, see <http://www.gnu.org/licenses/>.
 */
package plugins.nherve.toolbox;

/**
 * The Class Pair.
 * 
 * @param <T1>
 *            the generic type
 * @param <T2>
 *            the generic type
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Pair<T1, T2> implements Comparable<Pair<T1, T2>> {
	
	/** The first. */
	public final T1 first;
	
	/** The second. */
	public final T2 second;

	/**
	 * Instantiates a new pair.
	 * 
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 */
	private Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Of.
	 * 
	 * @param <T1>
	 *            the generic type
	 * @param <T2>
	 *            the generic type
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * @return the pair
	 */
	public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) {
		return new Pair<T1, T2>(first, second);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Pair<T1, T2> o) {
		int cmp = compare(first, o.first);
		return cmp == 0 ? compare(second, o.second) : cmp;
	}

	/**
	 * Compare.
	 * 
	 * @param <T1>
	 *            the generic type
	 * @param <T2>
	 *            the generic type
	 * @param o1
	 *            the o1
	 * @param o2
	 *            the o2
	 * @return the int
	 */
	@SuppressWarnings("unchecked")
	private static <T1, T2> int compare(Object o1, Object o2) {
		return o1 == null ? o2 == null ? 0 : -1 : o2 == null ? +1 : ((Pair<T1, T2>) o1).compareTo((Pair<T1, T2>)o2);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 31 * hashcode(first) + hashcode(second);
	}

	// todo move this to a helper class.
	/**
	 * Hashcode.
	 * 
	 * @param o
	 *            the o
	 * @return the int
	 */
	private static int hashcode(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		if (this == obj)
			return true;
		return equal(first, ((Pair) obj).first) && equal(second, ((Pair) obj).second);
	}

	/**
	 * Equal.
	 * 
	 * @param o1
	 *            the o1
	 * @param o2
	 *            the o2
	 * @return true, if successful
	 */
	private boolean equal(Object o1, Object o2) {
		return o1 == null ? o2 == null : (o1 == o2 || o1.equals(o2));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + first + ", " + second + ')';
	}
}
