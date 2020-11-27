package projet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

public class OperationGeo {

	public static BufferedImage rotation(BufferedImage source, int angle) {
		BufferedImage dest = source.getSubimage(0, 0, source.getWidth(), source.getHeight());
		while(angle <0){
			angle += 360;
		}
		while (angle > 90) {
			angle -= 90;
			dest = rotation(dest, 90);
		}

		AffineTransform at = new AffineTransform();

		at.rotate(angle * Math.PI / 180, dest.getWidth() / 2, dest.getHeight() / 2);

		/*
		 * translate to make sure the rotation doesn't cut off any image data
		 */
		AffineTransform translationTransform;
		translationTransform = findTranslation(at, dest);
		at.preConcatenate(translationTransform);

		BufferedImageOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(dest, null);
	}

	// http://www.dreamincode.net/forums/topic/233807-rotating-a-bufferedimage/
	private static AffineTransform findTranslation(AffineTransform at, BufferedImage source) {
		Point2D p2din, p2dout;

		p2din = new Point2D.Double(0.0, 0.0);
		p2dout = at.transform(p2din, null);
		double ytrans = p2dout.getY();

		p2din = new Point2D.Double(0, source.getHeight());
		p2dout = at.transform(p2din, null);
		double xtrans = p2dout.getX();

		AffineTransform tat = new AffineTransform();
		tat.translate(-xtrans, -ytrans);
		return tat;

	}

	public static BufferedImage zoom(BufferedImage source, float scale) {
		/*BufferedImage dest = new BufferedImage(source.getWidth() * 2, source.getHeight() * 2, source.getType());
		for (int i = 0; i < dest.getWidth(); i++) {
			for (int j = 0; j < dest.getHeight(); j++) {
				dest.setRGB(i, j, source.getRGB(i / 2, j / 2));
			}
		}
		return dest;*/
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(source, null);
	}

	public static BufferedImage dezoom(BufferedImage source) {
		BufferedImage dest = new BufferedImage(source.getWidth() / 2, source.getHeight() / 2, source.getType());
		for (int i = 0; i < dest.getWidth(); i++) {
			for (int j = 0; j < dest.getHeight(); j++) {
				dest.setRGB(i, j, source.getRGB(i * 2, j * 2));
			}
		}
		return dest;
	}

	public static BufferedImage miroirHorizontal(BufferedImage source) {
		BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int i = 0; i < dest.getWidth(); i++) {
			for (int j = 0; j < dest.getHeight(); j++) {
				dest.setRGB(i, j, source.getRGB(dest.getWidth() - i - 1, j));
			}
		}
		return dest;
	}

	public static BufferedImage miroirVertical(BufferedImage source) {
		BufferedImage dest = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for (int i = 0; i < dest.getWidth(); i++) {
			for (int j = 0; j < dest.getHeight(); j++) {
				dest.setRGB(i, j, source.getRGB(i, dest.getHeight() - j - 1));
			}
		}
		return dest;
	}

	// Effectue rotation d'une image depuis son centre avec angle en parametre
	// ex Math.PI/2
	private static BufferedImage rotateImg(BufferedImage img, double angle) {

		AffineTransform tx = new AffineTransform();

		tx.translate(img.getHeight() / 2, img.getWidth() / 2);
		tx.rotate(angle);
		// centrer image a l origine
		tx.translate(-img.getWidth() / 2, -img.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

		// Attention l image avec rotation sera une image avec un canal alpha
		// pour
		// permettre de remplir les zones vides aprÃ¨s rotation(background)
		// de blanc et pas noir par defaut en JPEG.
		BufferedImage bIm = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = bIm.createGraphics();
		graphics2D.setBackground(new Color(255, 255, 255, 0));
		graphics2D.setColor(new Color(255, 255, 255, 0));
		op.filter(img, bIm);

		return bIm;
	}

	// Renvoie un histo de projection horizontal d'une image
	// Tab de taille la hauteur de l'img
	private static int[] horizontalProjection(BufferedImage img) {

		int[] histoProj = new int[img.getHeight()];

		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				Color c = new Color(img.getRGB(x, y));
				// if(y==0)System.out.println(c.getGreen());
				if (c.getGreen() == 0 && c.getAlpha() == 255) {
					histoProj[y]++;
				}
			}
		}
		return histoProj;
	}

	// Renvoie un score de periodicite de l histogramme
	// le max correspond a un histo periodique
	// typiquement du aux sauts de lignes en projection horizontales
	// Histo de taille la hauteur de l image.
	// les val sont comprises entre 0 et largeur de l'image
	// basiquement on va tester le nombre de ligne blanche (ou quasi) de 1px de
	// haut
	// suivies par une ligne contenant des px de texte
	private static int getPeriodicity(int[] histo) {

		int p = 0;
		for (int i = 0; i < histo.length - 1; ++i) {
			if (histo[i] < 10 && histo[i + 1] > 20) // ligne blanche suivi de
													// ligne non blanche
				p++;
		}
		return p;
	}

	// Oriente automatiquement l'image dans la bonne direction
	// En binarisant puis test histo projection horizontal pour
	// differents angles.
	// L'histogramme si l'image est horizontal est typique avec des trous
	// rÃ©gulier
	// On supposera l'image scannee decalee de moins de 90 degres (ici 30) car
	// sinon
	// il peut y avoir 2 orientations possible: droite ou a l'envers totalement.
	// A noter: Il existe des methodes directes.(transformee de hough par ex)
	public static BufferedImage toGoodOrientation(BufferedImage img) {

		// BufferedImage newImage = rotateImg(img,Math.PI/2);
		// BufferedImage imBW = toBW(img);
		BufferedImage imBin = OperationCouleur.seuillage(img, InfoImage.getSeuilAutoOtsu(img));
		int ScorePeriodMax = 0;
		double oriMax = 0;

		for (int i = -44; i < 45; i+=2) {
			BufferedImage newImage = rotateImg(imBin, i * Math.PI / 180);
			int[] histoH = horizontalProjection(newImage);
			int period = getPeriodicity(histoH);
			if (period > ScorePeriodMax) {
				ScorePeriodMax = period;
				oriMax = i * Math.PI / 180;
			}
			System.out.println("Testing rotation: " + i * Math.PI / 180);
		}

		System.out.println("Best rotation for horizontal view: " + oriMax);
		return OperationGeo.rotation(img, (int)(oriMax * 180 / Math.PI));
	}
}
