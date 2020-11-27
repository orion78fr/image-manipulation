package projet;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import projet.HarrisFast.Corner;

public class Test {
	List<Corner> points;
	private BufferedImage bImage;
	//idéal serait de créer une interface ou l'utilisateur rentrera sa correction afin de rendre la démo plus impressionnante pour limiter la casse 
	Reponses reponses;

	public Test(BufferedImage source, Reponses reponses){
		bImage = source;
		points = new HarrisFast(source).crosslessFilter(1.2, 0.06, 6);
		this.reponses = reponses;
	}

	/**
	 * Vérifie qu'une case est bien cochée
	 * @param pointRef
	 * @param PointVoisins
	 * @return
	 */
	public boolean verifcase(Corner pointRef, ArrayList<Corner>PointVoisins, int taux){
		int compteurI = 0, compteurJ = PointVoisins.get(0).x -pointRef.x,compteurNoir = 0;
		for(int i = pointRef.x;i<=PointVoisins.get(0).x;i++){
			for(int j = pointRef.y;j<=PointVoisins.get(1).y;j++){
				//System.out.println("J"+j);
				Color color = new Color(bImage.getRGB(i, j));
				if((color.getRed()<=70)&&(color.getBlue()<=70)&&(color.getGreen()<=70))compteurNoir++;
			}
			compteurI++;
			//System.out.println("ty"+PointVoisins.get(1).x);
		}
		/*System.out.println("case ("+pointRef.x+","+pointRef.y+")");
		System.out.println("largeur : "+compteurI+" hauteur : "+compteurJ);
		System.out.println("pixels noirs : "+compteurNoir);*/

		//System.out.println((compteurNoir/(compteurI*compteurJ*1.0))*100.0);
		if((compteurNoir/(compteurI*compteurJ*1.0))*100 >= taux){
			return true;
		}
		else return false;
	}
	/**
	 * 
	 * @param list_of_corners : liste de points repérés par methode de Harris
	 * @param corner : coin étudié
	 * @return
	 */
	public ArrayList<Corner> pointsComparaison(List<Corner> list_of_corners, Corner corner){
		ArrayList<Corner> resultat = new ArrayList<HarrisFast.Corner>();
		int absc_corner = corner.x;
		int ordo_corner = corner.y;
		Iterator<Corner> it = list_of_corners.iterator();
		while(it.hasNext()){
			Corner temp = it.next();
			if((temp.x<=absc_corner+11)&&(temp.x>absc_corner-2)&&(temp.y<=ordo_corner+11)&&(temp.y>ordo_corner-2)){
				if(!temp.equals(corner)){
					resultat.add(temp);
				}	
			}
		}
		/*if(!resultat.isEmpty()){
			System.out.println("debut");
			for(Corner c : resultat) System.out.println(c.x+" "+c.y);
			System.out.println("fin");
		}//*/
		return resultat;
	}
	/**
	 * Corrige le qcm
	 */
	public void correction(){
		ArrayList<Integer> posVert = this.positionVerticale();
		ArrayList<Boolean>[] tab = new ArrayList[posVert.size()];
		int valHorizontale = 0;
		for(int i= 0;i<tab.length;i++){
			tab[i]= new ArrayList<Boolean>();
		}
		Iterator<Corner> iter = this.points.iterator();
		while(iter.hasNext()){
			Corner c =iter.next();
			ArrayList<Corner> res = this.pointsComparaison(this.points, c);
			if(res.size()==3){
				boolean bool = this.verifcase(c, res, 19);
				boolean isCase = this.verifcase(c, res, 70);
				for(int i=0;i<posVert.size();i++){
					if ((c.y<posVert.get(i)+2)&&(c.y>=posVert.get(i)-2)&&bool){
						if(c.x>valHorizontale){
							tab[i].add(isCase);
							System.out.println(c.x+" "+c.y);
							i = posVert.size()+3;
							valHorizontale = c.x;
						}
						else{
							tab[i].add(0,isCase);
							System.out.println(c.x+" "+c.y);
							i = posVert.size()+3;
							valHorizontale = c.x;
						}
					}

				}
			}
		}
		for(int i = 0;i<tab.length;i++){
			for(int j = 0;j<tab[i].size();j++){
				System.out.print(tab[i].get(j)+ " ");
			}
			System.out.println();
		}
		boolean[] reponse = resultat(tab);
		int note = 0;
		for(boolean b : reponse) if(b)note++;
		JOptionPane.showMessageDialog(new JFrame(), "Votre note est de "+note+" sur " + this.reponses.getNbQuestion());
	}
	/**
	 * Calcule la position a la verticale des cases
	 * @return
	 */
	public ArrayList<Integer> positionVerticale(){
		ArrayList<Integer> resultat = new ArrayList<Integer>();
		Iterator<Corner> iter = this.points.iterator();
		while(iter.hasNext()){
			Corner c =iter.next();
			ArrayList<Corner> res = this.pointsComparaison(this.points, c);
			if(!resultat.contains(c.y) && res.size()==3){
				resultat.add(c.y);
			}
		}
		rectificationTab(resultat);
		/*if(!resultat.isEmpty()){
			System.out.println("debut");
			for(Integer c : resultat) System.out.println(c);
			System.out.println("fin");
		}//*/
		return resultat;
	}
	/**
	 * linéarise le tableau issu de positionVerticale()
	 * @param tab
	 * @return
	 */
	public void rectificationTab(ArrayList<Integer> tab){
		for(int i = 0;i<tab.size()-1;i++){
			if((tab.get(i+1)<=tab.get(i)+2)&&(tab.get(i+1)>tab.get(i))){
				tab.remove(i+1);
			}
		}
		//return tab;
	}
	/**
	 * Calcule le résultat du qcm
	 * @param tab
	 * @return
	 */
	public boolean[] resultat(ArrayList<Boolean>[] tab){
		boolean valeurReponse = true;
		boolean[] feuilleReponse = new boolean[tab.length];
		for(int i = 0;i<tab.length;i++){
			for(int j = 0;j<tab[i].size() && valeurReponse;j++){
				// System.out.println(tab[i].get(j)+" "+reponses.getReponse(i,j));
				if(tab[i].get(j) != reponses.getReponse(i,j)){
					valeurReponse = false;
				}
			}
			feuilleReponse[i] = valeurReponse;
			valeurReponse = true;
		}
		for(boolean b : feuilleReponse) System.out.println(b + " ");
		return feuilleReponse;
	}
}
