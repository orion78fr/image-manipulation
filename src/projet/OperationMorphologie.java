package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Collections;

public class OperationMorphologie {

	public static BufferedImage filtreErosion(BufferedImage source) {

		int[] matrix = { 0, 1, 0, 1, 1, 1, 0, 1, 0 };

		BufferedImage dest = new BufferedImage(source.getWidth() - 2, source.getHeight() - 2, source.getType());

		ArrayList<Integer> bufferR = new ArrayList<Integer>();
		ArrayList<Integer> bufferG = new ArrayList<Integer>();
		ArrayList<Integer> bufferB = new ArrayList<Integer>();

		for (int j = 1; j < source.getHeight() - 1; j++) {
			for (int i = 1; i < source.getWidth() - 1; i++) {
				int couleurPixelR = new Color(source.getRGB(i, j)).getRed();
				int couleurPixelG = new Color(source.getRGB(i, j)).getGreen();
				int couleurPixelB = new Color(source.getRGB(i, j)).getBlue();

				for (int kj = 0; kj < 3; kj++) {
					for (int ki = 0; ki < 3; ki++) {
						bufferR.add(couleurPixelR - matrix[ki + kj * 3] * new Color(source.getRGB(i + ki - 1, j + kj - 1)).getRed());
						bufferG.add(couleurPixelG - matrix[ki + kj * 3] * new Color(source.getRGB(i + ki - 1, j + kj - 1)).getGreen());
						bufferB.add(couleurPixelB - matrix[ki + kj * 3] * new Color(source.getRGB(i + ki - 1, j + kj - 1)).getBlue());
					}
				}

				int miniR = Collections.min(bufferR);
				int miniG = Collections.min(bufferG);
				int miniB = Collections.min(bufferB);

				bufferR.clear();
				bufferG.clear();
				bufferB.clear();

				int couleurFinaleR = Math.min(Math.max(couleurPixelR - miniR, 0), 255);
				int couleurFinaleG = Math.min(Math.max(couleurPixelG - miniG, 0), 255);
				int couleurFinaleB = Math.min(Math.max(couleurPixelB - miniB, 0), 255);

				dest.setRGB(i - 1, j - 1, new Color(couleurFinaleR, couleurFinaleG, couleurFinaleB).getRGB());
			}
		}
		return dest;
	}

	public static BufferedImage filtreDilatation(BufferedImage source) {
		// On fait le négatif car c'est dilatation des blanc la formule
		BufferedImage source2 = OperationCouleur.negatif(source);

		int[] matrix = { 0, 1, 0, 1, 1, 1, 0, 1, 0 };

		BufferedImage dest = new BufferedImage(source2.getWidth() - 2, source2.getHeight() - 2, source2.getType());

		ArrayList<Integer> bufferR = new ArrayList<Integer>();
		ArrayList<Integer> bufferG = new ArrayList<Integer>();
		ArrayList<Integer> bufferB = new ArrayList<Integer>();

		for (int j = 1; j < source2.getHeight() - 1; j++) {
			for (int i = 1; i < source2.getWidth() - 1; i++) {
				int couleurPixelR = new Color(source2.getRGB(i, j)).getRed();
				int couleurPixelG = new Color(source2.getRGB(i, j)).getGreen();
				int couleurPixelB = new Color(source2.getRGB(i, j)).getBlue();

				for (int kj = 0; kj < 3; kj++) {
					for (int ki = 0; ki < 3; ki++) {
						bufferR.add(-couleurPixelR + matrix[ki + kj * 3] * new Color(source2.getRGB(i + ki - 1, j + kj - 1)).getRed());
						bufferG.add(-couleurPixelG + matrix[ki + kj * 3] * new Color(source2.getRGB(i + ki - 1, j + kj - 1)).getGreen());
						bufferB.add(-couleurPixelB + matrix[ki + kj * 3] * new Color(source2.getRGB(i + ki - 1, j + kj - 1)).getBlue());
					}
				}

				int maxiR = Collections.max(bufferR);
				int maxiG = Collections.max(bufferG);
				int maxiB = Collections.max(bufferB);

				bufferR.clear();
				bufferG.clear();
				bufferB.clear();

				int couleurFinaleR = Math.min(Math.max(couleurPixelR + maxiR, 0), 255);
				int couleurFinaleG = Math.min(Math.max(couleurPixelG + maxiG, 0), 255);
				int couleurFinaleB = Math.min(Math.max(couleurPixelB + maxiB, 0), 255);

				dest.setRGB(i - 1, j - 1, new Color(couleurFinaleR, couleurFinaleG, couleurFinaleB).getRGB());
			}

		}
		return OperationCouleur.negatif(dest);
	}

	public static BufferedImage flitreToutOuRien(BufferedImage source) {
		BufferedImage destination = new BufferedImage(source.getWidth() - 2, source.getHeight() - 2, source.getType());
		int comptPixObjet = 0;
		float[] matrix = { 1f, 1f, 0f, 1f, 0f, 0f, 0f, 0f, 0f };

		Kernel k = new Kernel(3, 3, matrix);
		ElementStructurant e_struct = new ElementStructurant(k, 0, 0);

		for (int j = ((k.getHeight() - 1) - e_struct.getOrdoneeOrgRepere()); j < destination.getHeight() - (k.getHeight() - 1) - e_struct.getOrdoneeOrgRepere(); j++) {
			for (int i = (k.getWidth() - 1) - e_struct.getAbscisseOrgRepere(); i < destination.getWidth() - (k.getHeight() - 1) - e_struct.getAbscisseOrgRepere(); i++) {
				for (int kj = 0; kj < k.getHeight(); kj++) {
					for (int ki = 0; ki < k.getWidth(); ki++) {
						if (matrix[ki + kj * 3] * source.getRGB(i + ki - e_struct.getAbscisseOrgRepere(), j + kj - e_struct.getOrdoneeOrgRepere()) == Color.BLACK.getRGB()) {
							comptPixObjet++;
						}
					}
				}
				if (comptPixObjet == 3)
					destination.setRGB(i - 1, j - 1, Color.BLACK.getRGB());
				else
					destination.setRGB(i - 1, j - 1, Color.WHITE.getRGB());
				comptPixObjet = 0;
			}

		}
		return destination;
	}

}
