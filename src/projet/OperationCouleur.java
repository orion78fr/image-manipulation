package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class OperationCouleur {

	public static BufferedImage toGS(BufferedImage source) {
		BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color color = new Color(source.getRGB(i, j));
				int GS = (color.getBlue() + color.getGreen() + color.getRed()) / 3;
				dest.setRGB(i, j, new Color(GS, GS, GS).getRGB());
			}
		}
		return dest;
	}

	public static BufferedImage seuillage(BufferedImage source, int seuil) {
		BufferedImage dest = toGS(source);
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color color = new Color(dest.getRGB(i, j));
				if (color.getBlue() >= seuil)
					dest.setRGB(i, j, new Color(255, 255, 255).getRGB());
				else
					dest.setRGB(i, j, new Color(0, 0, 0).getRGB());
			}
		}
		return dest;
	}

	public static BufferedImage negatif(BufferedImage source) {
		/*RescaleOp op = new RescaleOp(-1.0f, 255f, null);
		return op.filter(source, null);*/
		BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int i = 0; i < source.getWidth(); i++) {
			for (int j = 0; j < source.getHeight(); j++) {
				Color color = new Color(source.getRGB(i, j));
				dest.setRGB(i, j, new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue()).getRGB());
			}
		}
		return dest;
	}
}
