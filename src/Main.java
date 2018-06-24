import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Main {

	private int numLinhas;
	private int numColunas;
	ArrayList<Dados> listDados = new ArrayList<>();
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.lerArquivo();
		main.printDadosObtidos();

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
            	dado.indice = Integer.parseInt(texto[1]);
            	dado.peso = Double.parseDouble(texto[2]);
            	for(int i = 3; i < texto.length; i++) {
            		texto[i] = texto[i].replaceAll(" ", "");
            		dado.linhasCobertas.add(Integer.parseInt(texto[i]));
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
		listDados.forEach(dado -> System.out.println(dado.indice + " " + dado.peso + " " + dado.linhasCobertas.toString()));
	}
	
	static class Dados {
		private int indice;
		private double peso;
		ArrayList<String> linhas;
		private List<Integer> linhasCobertas = new ArrayList<>();
		
		public int getIndice() {
			return indice;
		}
		public void setIndice(int indice) {
			this.indice = indice;
		}
		public double getPeso() {
			return peso;
		}
		public void setPeso(double peso) {
			this.peso = peso;
		}
//		public ArrayList<int[]> getLinhasCobertas() {
//			return linhasCobertas;
//		}
//		public void setLinhasCobertas(ArrayList<int[]> linhasCobertas) {
//			this.linhasCobertas = linhasCobertas;
//		}
	}
}