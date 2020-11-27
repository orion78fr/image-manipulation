package projet;

public class Reponses {
	private int nbQuestion;
	private int nbReponse;
	private boolean[][] reponses;
	public int getNbQuestion() {
		return nbQuestion;
	}
	public int getNbReponse() {
		return nbReponse;
	}
	public boolean getReponse(int i, int j) {
		return reponses[i][j];
	}
	public Reponses(int nbQuestion, int nbReponse, boolean[][] reponses) {
		super();
		this.nbQuestion = nbQuestion;
		this.nbReponse = nbReponse;
		this.reponses = reponses;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<nbQuestion; i++){
			sb.append("Question " + (i+1) + " : ");
			for(int j=0; j<nbReponse; j++){
				sb.append(reponses[i][j] ? "vrai " : "faux ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
