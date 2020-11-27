package projet;


import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class Filtres
{

	public static BufferedImage filtreFlou(BufferedImage source)
	{
		float[] matrix = { 1f, 1f, 1f, 
				1f, 1f, 1f, 
				1f, 1f, 1f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreContrasteur(BufferedImage source)
	{
		float[] matrix = { 0f, -1f, 0f, 
				-1f, 5f, -1f, 
				0f, -1f, 0f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreBords(BufferedImage source)
	{
		float[] matrix = { 0f, 1f, 0f, 
				1f, -4f, 1f, 
				0f, 1f, 0f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreRepoussage(BufferedImage source)
	{
		float[] matrix = { -2f, -1f, 0f, 
				-1f, 1f, 1f, 
				0f, 1f, 2f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreContour(BufferedImage source)
	{
		float[] matrix = { -1f, -1f, -1f, 
				-1f, 8f, -1f, 
				-1f, -1f, -1f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreNettete(BufferedImage source)
	{
		float[] matrix = { -1f, -1f, -1f, 
				-1f, 9f, -1f, 
				-1f, -1f, -1f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage filtreEstampage(BufferedImage source)
	{
		float[] matrix = { -2f, 0f, 0f, 
				0f, 1f, 0f, 
				0f, 0f, 2f };

		return applyFiltre(source, matrix);
	}

	public static BufferedImage flouGaussien(BufferedImage source)
	{
		float[] matrix = { 0f, 0f, 0f, 5f, 0f, 0f, 0f,
				0f, 5f, 18f, 32f, 18f, 5f, 0f,
				0f, 18f, 64f, 100f, 64f, 18f, 0f,
				5f, 32f, 100f, 100f, 100f, 32f, 5f,
				0f, 18f, 64f, 100f, 64f, 18f, 0f,
				0f, 5f, 18f, 32f, 18f, 5f, 0f,
				0f, 0f, 0f, 5f, 0f, 0f, 0f};

		return applyFiltre(source, matrix);
	}

	private static BufferedImage applyFiltre(BufferedImage source, float[] matrix){
		int taille = (int)Math.sqrt(matrix.length);

		float sum = 0;
		for(int i = 0; i< matrix.length; i++) sum+=matrix[i];
		if(sum<=0) sum = 1;
		for(int i = 0; i< matrix.length; i++) matrix[i]/=sum;

		BufferedImageOp op = new ConvolveOp(new Kernel(taille, taille, matrix));

		return op.filter(source, null);
	}
	
}
