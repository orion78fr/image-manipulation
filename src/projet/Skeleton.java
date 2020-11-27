package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Skeleton (v2)
 * 
 * Original algorithm : Dr. Chai Quek
 * Modified algorithm : Xavier Philippeau 
 * 
 * @author Xavier Philippeau (based on work of Dr. Chai Quek)
 * 
 */
public class Skeleton {
 
	// Smoothing pattern
	private byte[] pattern1={-1,1,0,1,0,0,0,0};
	private byte[] pattern2={0,1,0,1,-1,0,0,0};
	private byte[] pattern3={0,0,-1,1,0,1,0,0};
	private byte[] pattern4={0,0,0,1,0,1,-1,0};
	private byte[] pattern5={0,0,0,0,-1,1,0,1};
	private byte[] pattern6={-1,0,0,0,0,1,0,1};
	private byte[] pattern7={0,1,0,0,0,0,-1,1};
	private byte[] pattern8={0,1,-1,0,0,0,0,1};
 
	// Neighbourhood
	private int neighbourhood(byte[][] c,int x,int y) {
		int neighbourhood=0;
		if (c[x-1][y-1]==1) neighbourhood++;
		if (c[x-1][y  ]==1) neighbourhood++;
		if (c[x-1][y+1]==1) neighbourhood++;
		if (c[x  ][y+1]==1) neighbourhood++;
		if (c[x+1][y+1]==1) neighbourhood++;
		if (c[x+1][y  ]==1) neighbourhood++;
		if (c[x+1][y-1]==1) neighbourhood++;
		if (c[x  ][y-1]==1) neighbourhood++;
		return neighbourhood;
	}
 
	// Transitions Count
	private int transitions(byte[][] c,int x,int y) {
		int transitions=0;
		if (c[x-1][y-1]==0 && c[x-1][y  ]==1) transitions++;
		if (c[x-1][y  ]==0 && c[x-1][y+1]==1) transitions++;
		if (c[x-1][y+1]==0 && c[x  ][y+1]==1) transitions++;
		if (c[x  ][y+1]==0 && c[x+1][y+1]==1) transitions++;
		if (c[x+1][y+1]==0 && c[x+1][y  ]==1) transitions++;
		if (c[x+1][y  ]==0 && c[x+1][y-1]==1) transitions++;
		if (c[x+1][y-1]==0 && c[x  ][y-1]==1) transitions++;
		if (c[x  ][y-1]==0 && c[x-1][y-1]==1) transitions++;
		return transitions;
	}
 
	// Match a pattern
	private boolean matchPattern(byte[][] c,int x,int y,byte[] pattern) {
		if (pattern[0]!=-1 && pattern[0]!=c[x-1][y-1]) return false;
		if (pattern[1]!=-1 && pattern[1]!=c[x-1][y  ]) return false;
		if (pattern[2]!=-1 && pattern[2]!=c[x-1][y+1]) return false;
		if (pattern[3]!=-1 && pattern[3]!=c[x  ][y+1]) return false;
		if (pattern[4]!=-1 && pattern[4]!=c[x+1][y+1]) return false;
		if (pattern[5]!=-1 && pattern[5]!=c[x+1][y  ]) return false;
		if (pattern[6]!=-1 && pattern[6]!=c[x+1][y-1]) return false;
		if (pattern[7]!=-1 && pattern[7]!=c[x  ][y-1]) return false;
		return true;
	}
 
	// Match one of the 8 patterns
	private boolean matchOneOfPatterns(byte[][] c,int x,int y) {
		if (matchPattern(c,x,y,pattern1)) return true;
		if (matchPattern(c,x,y,pattern2)) return true;
		if (matchPattern(c,x,y,pattern3)) return true;
		if (matchPattern(c,x,y,pattern4)) return true;
		if (matchPattern(c,x,y,pattern5)) return true;
		if (matchPattern(c,x,y,pattern6)) return true;
		if (matchPattern(c,x,y,pattern7)) return true;
		if (matchPattern(c,x,y,pattern8)) return true;
		return false;
	}
 
 
	/**
	 * Skeletonize the image using succesive thinning.
	 * 
	 * @param image  the image in an array[x][y] of values "0" or "1" 
	 * @param width of the image = 1st dimension of the array
	 * @param height of the image = 2nd dimension of the array
	 */
	public void thinning(byte[][] image,int width,int height) {
 
		// 3 columns back-buffer (original values)
		byte[][] buffer = new byte[3][height];
 
		// initialize the back-buffer
		for(int y=0;y<height;y++) {
			buffer[0][y]=0;
			buffer[1][y]=image[0][y];
			buffer[2][y]=image[1][y];
		}
 
		// loop until idempotence
		for(int loop=0;;loop++) {
 
			boolean changed=false;
 
			// for each columns
			for(int x=1;x<(width-1);x++) {
 
				// shift the back-buffer + set the last column
				byte[] swp0 = buffer[0]; buffer[0]=buffer[1]; buffer[1]=buffer[2]; buffer[2]=swp0;
				for(int y=0;y<height;y++) buffer[2][y]=image[x+1][y];
 
				// for each pixel
				for(int y=1;y<(height-1);y++) {
 
					// pixel value
					int v = image[x][y];
 
					// pixel not set -> next
					if (v==0) continue;
 
					// is a boundary/extremity ?
					int currentNeighbourhood = neighbourhood(buffer,1,y);
					if (currentNeighbourhood<=1) continue;
					if (currentNeighbourhood>=6) continue;
 
					// is a connection ?
					int transitionsCount = transitions(image,x,y);
					if (transitionsCount==1 && currentNeighbourhood<=3) continue;
 
					// no -> remove this pixel
					if (transitionsCount==1) {
						changed=true;
						image[x][y]=0;
						continue;
					}
 
					// can we delete this pixel ?
					boolean matchOne = matchOneOfPatterns(image,x,y);
 
					// yes -> remove this pixel
					if (matchOne) {
						changed=true;
						image[x][y]=0;
						continue;
					}
				}
			}
 
			// no change -> return result
			if (!changed) return;
		}
	}
	public static BufferedImage squelettiser(BufferedImage source){
		BufferedImage imFin = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		byte[][] bytearray= new byte[source.getHeight()][source.getWidth()];
		for(int i = 0;i<source.getHeight();i++){
			for(int j = 0;j<source.getWidth();j++){
				if(source.getRGB(j, i) == Color.BLACK.getRGB()) bytearray[i][j] = 1;
				else bytearray[i][j] = 0;
			}
		}
		// appel du filtre
		new Skeleton().thinning(bytearray,bytearray.length, bytearray[0].length);
		 
		// le tableau bytearray contient maintenant le squelette
		for(int i = 0;i<source.getHeight();i++){
			for(int j = 0;j<source.getWidth();j++){
				if(bytearray[i][j] == 1) imFin.setRGB(j, i, Color.BLACK.getRGB());
				else imFin.setRGB(j, i, Color.WHITE.getRGB());
			}
		}
		return imFin;
	}
}