import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private int numLinhas;
	private int numColunas;
	ArrayList<Dados> listDados = new ArrayList<>();
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.lerArquivo();
		//main.printDadosObtidos();

	}
	
	public void lerArquivo() {
		BufferedReader br;
		try {
			FileReader ler = new FileReader("C:\\Users\\gmdii\\Documents\\workspace-sts-3.9.4.RELEASE\\Algoritmo Genetico\\src\\entrada.txt");
            BufferedReader reader = new BufferedReader(ler);  
            String linha;
            linha = reader.readLine();
            linha = linha.replace(" ", "0");
            numLinhas = Integer.parseInt(linha.substring(8, linha.length()));
            linha = reader.readLine();
            linha = linha.replace(" ", "0");
            numColunas = Integer.parseInt(linha.substring(8, linha.length()));
            linha = reader.readLine();
            
            while((linha = reader.readLine()) != null ){
            	Dados dado = new Dados();
            	String[] texto = linha.split("\\s+");
            	dado.indice = li
            	for(int i = 3; i < texto.length; i++) {
            		texto[i] = texto[i].replaceAll(" ", "");
            		System.out.println(Double.parseDouble(texto[i]));
            	}
            	listDados.add(dado);
            }
					
		} catch (IOException e) {
			System.err.println("Arquivo de dados não encontrado, verifique o caminho ...");
			e.printStackTrace();
		}
	}
	
	public void printDadosObtidos() {
		System.out.println(numLinhas);
		System.out.println(numColunas);
		listDados.forEach(listDados->System.out.println(listDados.linhas));
	}
	
	static class Dados {
		private int indice;
		private float peso;
		ArrayList<String> linhas;
		private ArrayList<int[]> linhasCobertas;
		
		public int getIndice() {
			return indice;
		}
		public void setIndice(int indice) {
			this.indice = indice;
		}
		public float getPeso() {
			return peso;
		}
		public void setPeso(float peso) {
			this.peso = peso;
		}
		public ArrayList<int[]> getLinhasCobertas() {
			return linhasCobertas;
		}
		public void setLinhasCobertas(ArrayList<int[]> linhasCobertas) {
			this.linhasCobertas = linhasCobertas;
		}
	}
}