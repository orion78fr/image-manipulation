package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageFilter;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ElementStructurant {
	private int abscisseOrgRepere;
	private int ordoneeOrgRepere;
	private Kernel kernel;
	public ElementStructurant(Kernel kernel, int origineXReprere, int origineYReprere) {
		this.kernel = kernel;
		if(origineXReprere > kernel.getWidth() || origineYReprere > kernel.getHeight() ){
			this.abscisseOrgRepere = kernel.getXOrigin();
			this.ordoneeOrgRepere = kernel.getYOrigin();
		}
		else{
			this.abscisseOrgRepere = origineXReprere;
			this.ordoneeOrgRepere = origineYReprere;
		}
	}
	public Kernel getKernel() {
		return kernel;
	}
	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}
	public float[][] getElementTab2D(){
		float[][] tab = new float[kernel.getHeight()][kernel.getWidth()];
		float[] kernelTab = this.kernel.getKernelData(null);
		for(int kj = 0; kj< this.kernel.getHeight(); kj++){
			for(int ki = 0; ki< this.kernel.getWidth(); ki++)
			{
				tab[kj][ki] = kernelTab[ki+kj*this.kernel.getWidth()];
			}
		}
		return tab;
	}
	public void setCentreX(int centreX) {
		if ((centreX > -1) && (centreX < this.kernel.getWidth()) ) {
			this.abscisseOrgRepere = centreX;
		}
		else System.out.println("echec");
	}
	public void setCentreY(int centreY) {
		if ((centreY > -1) && (centreY < this.kernel.getHeight()) ) {
			this.ordoneeOrgRepere = centreY;
		}
	}
	public int getPaddingTop(){
		return(this.ordoneeOrgRepere);
	}
	public int getPaddingBottom(){
		return((this.kernel.getHeight()-1) - this.ordoneeOrgRepere);
	}
	public int getPaddingLeft(){
		return(this.abscisseOrgRepere);
	}
	public int getPaddingRight(){
		return((this.kernel.getWidth()-1) - this.abscisseOrgRepere);
	}
	public int getAbscisseOrgRepere() {
		return abscisseOrgRepere;
	}
	public int getOrdoneeOrgRepere() {
		return ordoneeOrgRepere;
	}
	public String toString() {
		float[][] tab = this.getElementTab2D();
		StringBuffer sb = new StringBuffer();
		for(int kj = 0; kj< this.kernel.getHeight(); kj++){
			for(int ki = 0; ki< this.kernel.getWidth(); ki++)
			{
				sb.append(tab[kj][ki]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	public static void testSoustraction() {
		File f1 = new File("/home/romain/workspace/Java/projetImage/test2.png");
		File f2 = new File("/home/romain/workspace/Java/projetImage/test2.png");

		BufferedImage baseImage = null;
		BufferedImage baseImage1 = null;

		try {
			baseImage = ImageIO.read(f1);
			baseImage1 = ImageIO.read(f2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage r= new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		WritableRaster r1 = baseImage.getRaster(), r2 = baseImage1.getRaster();
		//copie = OperationCouleur.seuillage(baseImage, InfoImage.getSeuilAutoOtsu(baseImage));
		//copie1 = OperationCouleur.seuillage(baseImage1, InfoImage.getSeuilAutoOtsu(baseImage1));
		int colorStockR, colorStockG, colorStockB;
		ColorModel modeleCouleur = baseImage.getColorModel();
		ColorModel modeleCouleur1 = baseImage1.getColorModel();
		Object pixel, pixel1;
		System.out.println(baseImage.getWidth() + " " + baseImage.getWidth());
		System.out.println(baseImage1.getHeight() + " " + baseImage1.getHeight());
		for(int i = 0;i<r2.getWidth();i++){
			for(int j = 0;j<r2.getHeight();j++){
				pixel = r1.getDataElements(i, j, null);
				pixel1 = r2.getDataElements(i, j, null);
				colorStockR = modeleCouleur.getRed(pixel)-modeleCouleur1.getRed(pixel1);
				colorStockB = modeleCouleur.getBlue(pixel)-modeleCouleur1.getBlue(pixel1);
				colorStockG = modeleCouleur.getGreen(pixel)-modeleCouleur1.getGreen(pixel1);
				if(colorStockR <0) colorStockR = 0;
				if(colorStockG <0) colorStockG = 0;
				if(colorStockB <0) colorStockB = 0;
				System.out.println(colorStockR + " " + colorStockG + " "+ colorStockB);
				r.setRGB(i, j, new Color((colorStockR), (colorStockG), (colorStockB)).getRGB());
			}
		}
		JFrame jFrame =  new JFrame();

		JLabel label = new JLabel(new ImageIcon(r));
		jFrame.getContentPane().add(label);

		jFrame.pack();
		jFrame.setVisible(true);

		try {
			ImageIO.write(r, "png", new File("y1.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
		}

	}
	public static boolean r(int abscisse, int ordonnee, BufferedImage im){
		boolean test = false;
		System.out.println("deb");
		if(im.getRGB(abscisse, ordonnee) == Color.BLACK.getRGB()){
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}
		else test = false;
		if(im.getRGB(abscisse, ordonnee+1) == Color.BLACK.getRGB()){
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}
		else test = false;
		if(im.getRGB(abscisse+1, ordonnee) == Color.BLACK.getRGB()) {
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}else test = false;
		if(im.getRGB(abscisse+1, ordonnee+1) == Color.BLACK.getRGB()){
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}else test = false;
		if(im.getRGB(abscisse+2, ordonnee) == Color.BLACK.getRGB()) {
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}
		if(im.getRGB(abscisse+2, ordonnee) == Color.BLACK.getRGB()) {
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}
		else test = false;
		if(im.getRGB(abscisse+2, ordonnee+2) == Color.BLACK.getRGB()) {
			System.out.println("pixel "+abscisse +","+ordonnee);
			test = true;
		}
		else test = false;
		System.out.println("fin");
		return test;
	}
	public static BufferedImage test(BufferedImage source){
		BufferedImage destination = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		for(int i = 0;i<source.getWidth();i++){
			for(int j = 0;j<source.getHeight();j++){
				if(i<source.getWidth()-2 && j<source.getHeight()-2){
					if(ElementStructurant.r(i, j, source)){
						destination.setRGB(i, j, Color.BLACK.getRGB());
					}
					else destination.setRGB(i, j, Color.WHITE.getRGB());
				}
				else destination.setRGB(i, j, source.getRGB(i, j));
			}
		}
		return destination;
	}
	public static void main(String[] args) {
		BufferedImage im = null;
		try {
			im = ImageIO.read(new File("/home/romain/workspace/Java/projetImage/BanqueQCM/Grille-QCM-en-tete-copie.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JLabel(new ImageIcon(test(im))));
		frame.pack();
		frame.setVisible(true);
	}
	
}
