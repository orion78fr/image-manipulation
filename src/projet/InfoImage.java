package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;


public class InfoImage
{
	
	public static int getSeuilAutoOtsu(BufferedImage source)
	{
		int histo[] = getHistoGS(source);
		int seuil = 0;
		
		double prob[] = new double[256], omega[] = new double[256]; /*
																	 * prob of
																	 * graylevels
																	 */
		double myu[] = new double[256]; /* mean value for separation */
		double max_sigma, sigma[] = new double[256]; /* inter-class variance */
		
		/* calculation of probability density */
		for (int i = 0; i < 256; i++)
		{
			prob[i] = (double) histo[i]
					/ (source.getWidth() * source.getHeight());
		}
		
		/* omega & myu generation */
		omega[0] = prob[0];
		myu[0] = 0.0; /* 0.0 times prob[0] equals zero */
		for (int i = 1; i < 256; i++)
		{
			omega[i] = omega[i - 1] + prob[i];
			myu[i] = myu[i - 1] + i * prob[i];
		}
		
		/*
		 * sigma maximization sigma stands for inter-class variance and
		 * determines optimal threshold value
		 */
		seuil = 0;
		max_sigma = 0.0;
		for (int i = 0; i < 256 - 1; i++)
		{
			if (omega[i] != 0.0 && omega[i] != 1.0) sigma[i] = Math.pow(
					myu[256 - 1] * omega[i] - myu[i], 2)
					/ (omega[i] * (1.0 - omega[i]));
			else sigma[i] = 0.0;
			if (sigma[i] > max_sigma)
			{
				max_sigma = sigma[i];
				seuil = i;
			}
		}
		
		return seuil;
	}
	
	private static int[] getHistoGS(BufferedImage source)
	{
		int histo[] = new int[256];
		BufferedImage GS = OperationCouleur.toGS(source);
		for (int i = 0; i < 256; i++)
			histo[i] = 0;
		for (int x = 0; x < GS.getWidth(); x++)
		{
			for (int y = 0; y < GS.getHeight(); y++)
			{
				Color color = new Color(GS.getRGB(x, y));
				histo[color.getBlue()]++;
			}
		}
		return histo;
	}
	
}
