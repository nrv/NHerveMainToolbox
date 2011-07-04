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
package plugins.nherve.toolbox.image.toolboxes;

/**
 * The Class Conversion.
 * 
 * @author Nicolas HERVE - nicolas.herve@pasteur.fr
 */
public class Conversion {
	
	/** The Constant UNDEFINED. */
	public static final int UNDEFINED = -1;

	/**
	 * Instantiates a new conversion.
	 */
	public Conversion() {
	}

	// RGB to I1H2H3
	/**
	 * Private_ rg b_to_ i1.
	 * 
	 * @param R
	 *            the r
	 * @param G
	 *            the g
	 * @param B
	 *            the b
	 * @return the int
	 */
	public static int private_RGB_to_I1(int R, int G, int B) {
		int I1;
		float inv_quotient = 1.0f / 3.0f;

		I1 = (int) ((R + G + B) * inv_quotient + 0.5);

		return (I1);

	}

	/**
	 * Private_ rg b_to_ h2.
	 * 
	 * @param R
	 *            the r
	 * @param G
	 *            the g
	 * @param B
	 *            the b
	 * @return the int
	 */
	public static int private_RGB_to_H2(int R, int G, int B) {
		int H2;

		H2 = R - G;

		return (H2);
	}

	/**
	 * Private_ rg b_to_ h3.
	 * 
	 * @param R
	 *            the r
	 * @param G
	 *            the g
	 * @param B
	 *            the b
	 * @return the int
	 */
	public static int private_RGB_to_H3(int R, int G, int B) {
		float H3;

		H3 = (0.5f * (R + G) - B);
		// System.out.println(" H3:"+H3+ "round: ");
		return (Math.round(H3));
	}

	/**
	 * Private_ h1 h2 h3_to_ r.
	 * 
	 * @param H1
	 *            the h1
	 * @param H2
	 *            the h2
	 * @param H3
	 *            the h3
	 * @return the int
	 */
	public static int private_H1H2H3_to_R(int H1, int H2, int H3) {
		float R;
		int retour;
		float un_tier = 1.f / 3.f;

		R = (float) (H1 + 0.5 * H2 + un_tier * H3 + 0.5);

		retour = (int) Math.max(R, 0);
		// System.out.println(" R:"+R+ "max: "+retour+" round:"+Math.round(R));
		retour = Math.min(retour, 255);

		return (retour);
	}

	/**
	 * Private_ h1 h2 h3_to_ g.
	 * 
	 * @param H1
	 *            the h1
	 * @param H2
	 *            the h2
	 * @param H3
	 *            the h3
	 * @return the int
	 */
	public static int private_H1H2H3_to_G(int H1, int H2, int H3) {
		float G;
		int retour;
		float un_tier = 1.f / 3.f;

		G = (H1 - 0.5f * H2 + un_tier * H3 + 0.5f);

		retour = (int) Math.max(G, 0);
		retour = Math.min(retour, 255);

		return (retour);
	}

	/**
	 * Private_ h1 h2 h3_to_ b.
	 * 
	 * @param H1
	 *            the h1
	 * @param H2
	 *            the h2
	 * @param H3
	 *            the h3
	 * @return the int
	 */
	public static int private_H1H2H3_to_B(int H1, int H2, int H3) {
		float B;
		int retour;
		float deux_tier = 2.0f / 3.0f;

		B = (float) (H1 - deux_tier * H3 + 0.5);

		retour = (int) Math.max(B, 0);
		retour = Math.min(retour, 255);

		return (retour);
	}

	// RGB to I1I2I3

	/**
	 * Private_ rg b_to_ i2.
	 * 
	 * @param R
	 *            the r
	 * @param G
	 *            the g
	 * @param B
	 *            the b
	 * @return the int
	 */
	public static int private_RGB_to_I2(int R, int G, int B) {
		int I2;

		I2 = R - B;

		return (I2);
	}

	/**
	 * Private_ rg b_to_ i3.
	 * 
	 * @param R
	 *            the r
	 * @param G
	 *            the g
	 * @param B
	 *            the b
	 * @return the int
	 */
	public static int private_RGB_to_I3(int R, int G, int B) {
		int I3;

		I3 = (int) (G - (R + B) * 0.5);

		return (I3);
	}

	/**
	 * Private_ i1 i2 i3_to_ r.
	 * 
	 * @param I1
	 *            the i1
	 * @param I2
	 *            the i2
	 * @param I3
	 *            the i3
	 * @return the int
	 */
	public static int private_I1I2I3_to_R(int I1, int I2, int I3) {
		float R;
		int retour;
		float un_tier = 1.f / 3.f;

		R = (float) (I1 + 0.5 * I2 - un_tier * I3 + 0.5);

		retour = (int) Math.max(R, 0);
		retour = Math.min(retour, 255);

		return (retour);
	}

	/**
	 * Private_ i1 i2 i3_to_ g.
	 * 
	 * @param I1
	 *            the i1
	 * @param I2
	 *            the i2
	 * @param I3
	 *            the i3
	 * @return the int
	 */
	public static int private_I1I2I3_to_G(int I1, int I2, int I3) {
		float G;
		int retour;
		float deux_tier = 2.f / 3.f;

		G = (float) (I1 + I3 * deux_tier + 0.5);

		retour = (int) Math.max(G, 0);
		retour = Math.min(retour, 255);

		return (retour);
	}

	/**
	 * Private_ i1 i2 i3_to_ b.
	 * 
	 * @param I1
	 *            the i1
	 * @param I2
	 *            the i2
	 * @param I3
	 *            the i3
	 * @return the int
	 */
	public static int private_I1I2I3_to_B(int I1, int I2, int I3) {
		float B;
		int retour;
		float un_tier = 1.0f / 3.0f;

		B = (float) (I1 - 0.5 * I2 - un_tier * I3 + 0.5);

		retour = (int) Math.max(B, 0);
		retour = Math.min(retour, 255);

		return (retour);
	}

	/* Fast conversion of Foley p. 592 */
	/* Given: R,G,B, each in [0,1] */
	/* Out: H in [0, 360), S and V in [0,1] except if s=0, then H=UNDEFINED, */
	/* which is some constant defined with a value outside the interval [0,360] */
	/**
	 * RG b_to_ hsv.
	 * 
	 * @param r
	 *            the r
	 * @param g
	 *            the g
	 * @param b
	 *            the b
	 * @param h
	 *            the h
	 * @param s
	 *            the s
	 * @param v
	 *            the v
	 */
	public static void RGB_to_HSV(double r, double g, double b, double[] h, double[] s, double[] v) {
		double max;
		double min;
		double delta;

		max = Math.max(b, Math.max(r, g));
		min = Math.min(b, Math.min(r, g));

		v[0] = max; /* This is a value v */
		/*
		 * Next calculate saturation, S. Saturation is 0 if red, green and blue
		 * are all 0
		 */
		s[0] = (max != 0.0) ? ((max - min) / max) : 0.0;
		if (s[0] == 0.0)
			h[0] = UNDEFINED;
		else { /* Chromatic case: Saturation is not 0 */
			delta = max - min; /* so determine hue */
			if (r == max)
				h[0] = (g - b) / delta; /*
										 * Resulting color is between yellow and
										 * magenta
										 */
			else if (g == max)
				h[0] = 2.0 + (b - r) / delta; /*
											 * Resulting color is between cyan
											 * and yellow
											 */
			else if (b == max)
				h[0] = 4.0 + (r - g) / delta; /*
											 * Resulting color is between
											 * magenta and cyan
											 */
			h[0] *= 60.0; /* Convert hue to degrees */
			if (h[0] < 0.0)
				h[0] += 360.0; /* Make sure hue is nonnegative */
		} /* Chromatic case */
	}

}
