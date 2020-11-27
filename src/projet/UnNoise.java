package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class UnNoise {
	/**
	 * @author Xavier Philippeau
	 * 
	 *         UnNoise Plugin.
	 * 
	 *         A noise detection and removal filter with edge preservation.
	 */
	public enum Methode{
		ROAD,VARIANCE;
	}

	public static BufferedImage run(BufferedImage source, Methode estimator, int aperture, double factor, int itermax) {
		
		// project image to HSV color space
		int[][] work = null;
		work = transformRGBtoHSV(source);

		// filter = Noise Estimation (->mask) + Pixel replacement
		int[][] mask = null;
		for (int i = 0; i < itermax; i++) {
			if (estimator == Methode.ROAD)
				mask = ROAD(work, aperture, factor);

			if (estimator == Methode.VARIANCE)
				mask = Variance(work, aperture, factor);

			work = inpaint(work, mask);
		}

		// project image to RGB color space
		BufferedImage newIp = null;
		newIp = transformHSVtoRGB(source, work);

		// show new image
		return newIp;
	}
	
	private static int[][] getIntTable(int width, int height){
		int table[][] = new int[width][];
		for(int i=0;i<width;i++){
			table[i] = new int[height];
		}
		return table;
	}

	// RGB -> HSV
	private static int[][] transformRGBtoHSV(BufferedImage source) {
		int[][] dest = getIntTable(source.getWidth(), source.getHeight());
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				Color color = new Color(source.getRGB(x, y));
				float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				dest[x][y] = (int) (hsb[2] * 255);
			}
		}
		return dest;
	}

	// HSV -> RGB
	private static BufferedImage transformHSVtoRGB(BufferedImage source, int[][] bp) {
		BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				Color color = new Color(source.getRGB(x, y));
				float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
				int rgb = Color.HSBtoRGB(hsb[0], hsb[1], bp[x][y] / 255f);
				dest.setRGB(x, y, rgb);
			}
		}
		return dest;
	}

	/**
	 * Noise estimation using the ROAD Estimator
	 * 
	 * (ROAD: R. Garnett, T. Huegerich, C. Chui and W.-J. He)
	 * 
	 * @param c
	 *            Input data (luminosity/gray-level)
	 * @param aperture
	 *            Window aperture
	 * @param coef
	 *            variance theshold factor
	 * @return mask (255="masked" 0="unmasked")
	 */
	private static int[][] ROAD(int[][] c, int aperture, double coef) {

		int width = c.length;
		int height = c[0].length;

		// value-to-distance table
		double[] distance = new double[256];
		distance[0] = 0;
		for (int i = 1; i < 256; i++) {
			double x = (double) i / 255;
			double log = Math.log(x) / Math.log(2);
			distance[i] = 1 + Math.max(log, -5) / 5;
		}

		int[][] mask = getIntTable(width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				// compute distances between pixels
				int[] dist = new int[(2 * aperture + 1) * (2 * aperture + 1)];
				int average = 0;
				int count = 0;
				for (int dy = -aperture; dy <= aperture; dy++) {
					for (int dx = -aperture; dx <= aperture; dx++) {
						if ((x + dx) < 0)
							continue;
						if ((x + dx) >= width)
							continue;
						if ((y + dy) < 0)
							continue;
						if ((y + dy) >= height)
							continue;
						
						if (c[x + dx][y + dy] < 0)
							continue;

						int i = Math.abs(c[x + dx][y + dy] - c[x][y]);
						dist[count] = (int) (255.0 * distance[i]);
						count++;

						average += c[x + dx][y + dy];
					}
				}
				average /= count;

				// Noise estimation (ROAD at rank nmb_pixels/2)
				Arrays.sort(dist, 0, count);
				int road = 0;
				int rank = count / 2;
				for (int i = 0; i < rank; i++)
					road += dist[i];

				// compute threshold (= variance)
				int etype = 0;
				count = 0;
				for (int dy = -aperture; dy <= aperture; dy++) {
					for (int dx = -aperture; dx <= aperture; dx++) {
						if ((x + dx) < 0)
							continue;
						if ((x + dx) >= width)
							continue;
						if ((y + dy) < 0)
							continue;
						if ((y + dy) >= height)
							continue;

						double e = c[x + dx][y + dy] - average;
						etype += e * e;
						count++;
					}
				}
				etype = (int) Math.sqrt(etype / count);
				int threshold = (int) (rank * etype);

				// pixel exceed threshold -> noise
				if ((road >= (threshold * coef)) && (etype > 0)) {
					mask[x][y] = 255;
				} else {
					mask[x][y] = 0;
				}

			}
		}

		return mask;
	}

	/**
	 * Noise estimation using Variance Estimator
	 * 
	 * @param c
	 *            Input data (luminosity/gray-level)
	 * @param aperture
	 *            Window aperture
	 * @param coef
	 *            variance theshold factor
	 * @return mask (255="masked" 0="unmasked")
	 */
	private static int[][] Variance(int[][] c, int aperture, double coef) {
		int width = c.length;
		int height = c[0].length;

		int[][] mask = getIntTable(width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				// compute average
				float average = 0;
				int count = 0;
				for (int dy = -aperture; dy <= aperture; dy++) {
					for (int dx = -aperture; dx <= aperture; dx++) {
						if ((x + dx) < 0)
							continue;
						if ((x + dx) >= width)
							continue;
						if ((y + dy) < 0)
							continue;
						if ((y + dy) >= height)
							continue;
						average += c[x + dx][y + dy];
						count++;
					}
				}
				average /= count;

				// compute variance
				float etype = 0;
				count = 0;
				for (int dy = -aperture; dy <= aperture; dy++) {
					for (int dx = -aperture; dx <= aperture; dx++) {
						if ((x + dx) < 0)
							continue;
						if ((x + dx) >= width)
							continue;
						if ((y + dy) < 0)
							continue;
						if ((y + dy) >= height)
							continue;
						float e = Math.abs(c[x + dx][y + dy] - average);
						etype += e * e;
						count++;
					}
				}
				etype = (float) Math.sqrt(etype / count);

				// pixel exceed threshold -> noise
				if (Math.abs(c[x][y] - average) > coef * etype) {
					mask[x][y] = 255;
				} else {
					mask[x][y] = 0;
				}

			}
		}

		return mask;
	}

	/**
	 * Fast (not accurate) Inpainting
	 * 
	 * @param c
	 *            Input data (luminosity/gray-level)
	 * @param mask
	 *            masked pixel to evaluate
	 * @return Output data (luminosity/gray-level)
	 */
	private static int[][] inpaint(int[][] c, int[][] mask) {
		int width = c.length;
		int height = c[0].length;

		int[][] c2 = getIntTable(width, height);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				c2[x][y] = c[x][y];

		int[][] newmask = getIntTable(width, height);
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				newmask[x][y] = mask[x][y];

		int n = 8;
		int[] dx = new int[] { -1, 0, 1, 1, 1, 0, -1, -1 };
		int[] dy = new int[] { -1, -1, -1, 0, 1, 1, 1, 0 };

		while (true) {

			// front-line masked/unmasked
			ArrayList<int[]> contourInt = new ArrayList<int[]>();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (mask[x][y] == 0)
						continue;
					for (int i = 0; i < n; i++) {
						int xk = x + dx[i];
						int yk = y + dy[i];
						if (xk < 0 || xk >= width)
							continue;
						if (yk < 0 || yk >= height)
							continue;
						if (mask[xk][yk] > 0)
							continue;
						contourInt.add(new int[] { x, y });
						break;
					}
				}
			}

			// exit when no front-line
			if (contourInt.isEmpty())
				break;

			// isophotes continuation
			for (int j = 0; j < contourInt.size(); j++) {
				int[] pixel = (int[]) contourInt.get(j);
				int x = pixel[0];
				int y = pixel[1];

				double value = 0;
				double wsum = 0;
				for (int i = 0; i < n; i++) {
					int xk = x + dx[i];
					int yk = y + dy[i];
					if (xk < 0 || xk >= width)
						continue;
					if (yk < 0 || yk >= height)
						continue;
					if (mask[xk][yk] > 0)
						continue;

					// gradient
					double[] grad = gradient(c, xk, yk);
					double norme = grad[0];
					double angle = grad[1];

					// gradient normal = isophote direction
					angle += Math.PI / 2;

					// Weight of the propagation:

					// 1. dotproduct ( gradient normal . propagation vector )
					double pscal = Math.cos(angle) * (-dx[i]) + (-Math.sin(angle)) * (-dy[i]);
					pscal /= Math.sqrt(dx[i] * dx[i] + dy[i] * dy[i]);

					// 2. gradient magnitude (O -> omnidirectionnal)
					double w = (norme) * Math.abs(pscal) + (1 - norme) * 1;

					value += w * c[xk][yk];
					wsum += w;
				}
				if (wsum <= 0)
					continue;
				value /= wsum;

				// set new value
				c2[x][y] = (int) value;

				// pixel becomes unmasked
				newmask[x][y] = 0;
			}

			mask = newmask;
		}

		return c2;
	}

	/**
	 * Compute the local Gradient of one pixel
	 * 
	 * @param c
	 *            Input Data
	 * @param x
	 *            X coord
	 * @param y
	 *            Y Coord
	 * @return double[0] = gradient norme (0..1), double[1] = gradient direction
	 *         (0..2*PI)
	 */
	private static double[] gradient(int[][] c, int x, int y) {
		int width = c.length;
		int height = c[0].length;

		double cst1 = (0.25 * (2 - Math.sqrt(2.0)));
		double cst2 = (0.5f * (Math.sqrt(2.0) - 1));

		int px = x - 1;
		int nx = x + 1;
		int py = y - 1;
		int ny = y + 1;
		if (px < 0)
			px = 0;
		if (nx >= width)
			nx = width - 1;
		if (py < 0)
			py = 0;
		if (ny >= height)
			ny = height - 1;

		int Ipp = c[px][py];
		int Ipc = c[px][y];
		int Ipn = c[px][ny];
		int Icp = c[x][py];
		int Icn = c[x][ny];
		int Inp = c[nx][py];
		int Inc = c[nx][y];
		int Inn = c[nx][ny];

		double IppInn = cst1 * (Inn - Ipp);
		double IpnInp = cst1 * (Ipn - Inp);
		int gradx = (int) (IppInn - IpnInp - cst2 * Ipc + cst2 * Inc);
		int grady = (int) (IppInn + IpnInp - cst2 * Icp + cst2 * Icn);

		double norme = Math.sqrt(gradx * gradx + grady * grady);

		double angle = 0;
		if (norme > 0) {
			angle = Math.acos(gradx / norme);
			if (grady > 0)
				angle = 2 * Math.PI - angle;
		}

		norme /= 255;

		return new double[] { norme, angle };
	}
}
