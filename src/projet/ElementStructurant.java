package projet;

import java.awt.image.Kernel;

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
}
