package projet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private JLabel imagelabel;
	private BufferedImage baseImage, bufferedImage;
	private boolean resizing;
	private Reponses reponses;

	public MainFrame() {
		this.setTitle("Projet Image - Détection de QCM");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		resizing = false;
		reponses = null;

		imagelabel = new JLabel();
		baseImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		revert();

		JMenuBar bar = new JMenuBar();
		generateMenu(bar);
		this.setJMenuBar(bar);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(imagelabel, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(panel);
		this.getContentPane().add(scroll);
		rafraichir();
		imagelabel.addMouseListener(new MouseSelectListener());
		MouseListener ml = new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//JOptionPane.showMessageDialog(new JFrame(), "position souris : "+e.getPoint().x+" "+e.getPoint().y);
			}
		};
		imagelabel.addMouseListener(ml);
		this.setSize(800, 600);
	}
	private class MouseSelectListener extends MouseAdapter {
		private int xtemp1 = 0;
		private int ytemp1 = 0;
		private int xtemp2 = 0;
		private int ytemp2 = 0;

		@Override
		public void mouseReleased(MouseEvent e) {
			if (resizing) {
				xtemp2 = e.getPoint().x;
				ytemp2 = e.getPoint().y;

				int xmin = Math.max(Math.min(xtemp1, xtemp2), 0);
				int xmax = Math.min(Math.max(xtemp1, xtemp2), bufferedImage.getWidth());
				int ymin = Math.max(Math.min(ytemp1, ytemp2), 0);
				int ymax = Math.min(Math.max(ytemp1, ytemp2), bufferedImage.getHeight());

				bufferedImage = bufferedImage.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin);
				rafraichir();
				resizing = false;
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (resizing) {
				xtemp1 = e.getPoint().x;
				ytemp1 = e.getPoint().y;
			}
		}
	}

	private void generateMenu(JMenuBar bar) {
		JMenu menu;
		JMenuItem item;

		menu = new JMenu("Fichier");

		item = new JMenuItem("Charger...");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(new File("").getAbsolutePath());
				int ret = fc.showDialog(getMe(), "Ouvrir");
				if (ret == JFileChooser.APPROVE_OPTION) {
					try {
						baseImage = ImageIO.read(fc.getSelectedFile());
						revert();
						rafraichir();
					} catch (Exception ex) {
						actionPerformed(e);
					}
				}
			}
		});
		menu.add(item);

		item = new JMenuItem("Retourner à l'image d'origine");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				revert();
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Sélectionner une zone à découper");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				resizing = true;
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Sauver...");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					ImageIO.write(bufferedImage, "png", new File(JOptionPane.showInputDialog("Veuillez rentrez un nom pour l'image. Elle sera enregistrée au format png")+".png"));
				} catch (HeadlessException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Choisir les réponses au QCM");
		item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				initReponses();
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Quitter");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu.add(item);
		bar.add(menu);

		// --------------------------------------

		menu = new JMenu("Opérations Couleur");

		item = new JMenuItem("Nuances de gris");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				bufferedImage = OperationCouleur.toGS(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Seuillage (choix)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int input = -1;
				while (input < 0 || input > 255)
					try {
						input = Integer.parseInt(JOptionPane.showInputDialog(getMe(), "Rentrez le seuil (0 à 255)"));
					} catch (Exception ex) {
					}

				bufferedImage = OperationCouleur.seuillage(bufferedImage, input);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Seuillage (détection auto, Otsu)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = InfoImage.getSeuilAutoOtsu(bufferedImage);

				bufferedImage = OperationCouleur.seuillage(bufferedImage, result);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Négatif");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				bufferedImage = OperationCouleur.negatif(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		bar.add(menu);

		// --------------------------------------

		menu = new JMenu("Opérations Géométriques");

		item = new JMenuItem("Zoom/Dezoom");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				float input = 0;
				while (input <= 0)
					try {
						input = Float.parseFloat(JOptionPane.showInputDialog(getMe(), "Rentrez le zoom (supérieur à 0)\n"
								+ "2 correspond à un zoom x2, 0.5 correspond à un dézoom x2"));
					} catch (Exception ex) {
					}

				bufferedImage = OperationGeo.zoom(bufferedImage, input);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Miroir Vertical");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				bufferedImage = OperationGeo.miroirVertical(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Miroir Horizontal");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				bufferedImage = OperationGeo.miroirHorizontal(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Rotation");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int input = 0;
				while (input == 0)
					try {
						input = Integer.parseInt(JOptionPane.showInputDialog(getMe(), "Rentrez l'angle en degrés"));
					} catch (Exception ex) {
					}
				bufferedImage = OperationGeo.rotation(bufferedImage, input);

				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Rotation auto");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = OperationGeo.toGoodOrientation(bufferedImage);

				rafraichir();
			}
		});
		menu.add(item);

		bar.add(menu);

		// ----------------------------------------

		menu = new JMenu("Filtres");

		item = new JMenuItem("Filtre flou");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreFlou(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre contrasteur");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreContrasteur(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre détection bords");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreBords(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre détection bords 2");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreContour(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre estampage");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreEstampage(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre estampage 2");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreRepoussage(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Filtre netteté");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.filtreNettete(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Flou Gaussien");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Filtres.flouGaussien(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		bar.add(menu);
		// --------------------------------------

		menu = new JMenu("Morphologie");

		item = new JMenuItem("Dilatation");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = OperationMorphologie.filtreDilatation(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Erosion");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = OperationMorphologie.filtreErosion(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Tout ou rien (Binaire)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = OperationMorphologie.flitreToutOuRien(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Squelettisation");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = Skeleton.squelettiser(bufferedImage);
				rafraichir();
			}
		});
		menu.add(item);
		bar.add(menu);
		// --------------------------------------
		menu = new JMenu("Autres");

		item = new JMenuItem("Réduction bruit (road)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = UnNoise.run(bufferedImage, UnNoise.Methode.ROAD, 2, 1.0, 5);
				rafraichir();
			}
		});
		menu.add(item);

		item = new JMenuItem("Réduction bruit (variance)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = UnNoise.run(bufferedImage, UnNoise.Methode.VARIANCE, 2, 1.0, 5);
				rafraichir();
			}
		});
		menu.add(item);

		/*item = new JMenuItem("Réduction bruit (variance) 2");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = UnNoise.run(bufferedImage, UnNoise.Methode.VARIANCE, 2, 1.0, 50);
				rafraichir();
			}
		});
		menu.add(item);*/

		item = new JMenuItem("Détection de points caractéristiques (Harris)");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int[][] output = new HarrisFast(bufferedImage).filter(1.2, 0.06, 6);
				bufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType());
				for (int i = 0; i < bufferedImage.getWidth(); i++) {
					for (int j = 0; j < bufferedImage.getHeight(); j++) {
						bufferedImage.setRGB(i, j, new Color(output[i][j], output[i][j], output[i][j]).getRGB());
					}
				}
				rafraichir();
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Préparation de l'image pour la détection");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				bufferedImage = UnNoise.run(
									OperationMorphologie.filtreErosion(
										OperationMorphologie.filtreDilatation(bufferedImage)), 
									UnNoise.Methode.VARIANCE, 
									2, 
									1.0, 
									5);
				
				int result = InfoImage.getSeuilAutoOtsu(bufferedImage);

				bufferedImage = OperationCouleur.seuillage(bufferedImage, result);
				
				rafraichir();
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Lecture et Correction");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(reponses == null){
					initReponses();
				}
				Test t = new Test(bufferedImage, reponses);
				t.correction();
				//Iterator<Corner> iter = t.points.iterator();
				
				/*while(iter.hasNext()){
					Corner c =iter.next();
					ArrayList<Corner> res = t.pointsComparaison(t.points, c);
					if(res.size()==3){
						boolean bool = t.verifcase(c, res);
						System.out.println("case cochee : " +bool);
					}
				}*/
				rafraichir();
			}
		});
		menu.add(item);

		bar.add(menu);
	}

	private JFrame getMe() {
		return this;
	}

	private void initReponses() {
		int nbQuestion = 0;
		while (nbQuestion <= 0) {
			try {
				nbQuestion = Integer.parseInt(JOptionPane.showInputDialog(getMe(), "Rentrez le nombre de questions de votre QCM"));
			} catch (Exception ex) {
			}
		}
		int nbReponse = 0;
		while (nbReponse <= 0) {
			try {
				nbReponse = Integer.parseInt(JOptionPane.showInputDialog(getMe(), "Rentrez le nombre de réponses à chaque question de votre QCM"));
			} catch (Exception ex) {
			}
		}
		StringBuffer exemple = new StringBuffer("(v pour vrai, f pour faux, exemple: ");
		for(int i=0; i<nbReponse; i++){
			exemple.append((int)(Math.random()*2) % 2 == 0 ? 'v' : 'f');
		}
		exemple.append(")");
		boolean[][] reponses = new boolean[nbQuestion][];
		for(int i=0; i<nbQuestion; i++){
			reponses[i] = new boolean[nbReponse];
			boolean correct = false;
			String reponse = null;
			while(!correct){
				reponse = JOptionPane.showInputDialog(getMe(), "Rentrez la réponse de la question " + (i+1) + ".\n" + exemple.toString());
				if(reponse != null){
					if(reponse.length() == nbReponse){
						if(reponse.matches("[vf]+")){
							correct = true;
						}
					}
				}
			}
			for(int j=0; j<nbReponse; j++){
				reponses[i][j] = reponse.charAt(j) == 'v' ? true : false;
			}
		}
		this.reponses = new Reponses(nbQuestion, nbReponse, reponses);
	}

	private void rafraichir() {
		ImageIcon icon = new ImageIcon(bufferedImage);
		imagelabel.setIcon(icon);
		// this.pack();
	}

	private void revert() {
		bufferedImage = baseImage.getSubimage(0, 0, baseImage.getWidth(), baseImage.getHeight());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainFrame().setVisible(true);
	}
}
