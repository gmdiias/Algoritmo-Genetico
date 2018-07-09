import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

class Main {

	private int numLinhas;
	private int numColunas;
	ArrayList<DadosColunas> listDados = new ArrayList<>();
	ArrayList<DadosLinhas> listLinhas = new ArrayList<>();
	
	public static void main(String[] args) {
		Main main = new Main();
		
		main.lerArquivo();
		main.printDadosObtidos();
		
		List<Individuo> listIndividuo = main.geraPopulacaoInicial();
		
		listIndividuo.forEach(dado -> System.out.println("Peso: " + dado.getPeso() + " Colunas: " + dado.getColunas().toString()));
		
	}
	
	public void lerArquivo() {
		BufferedReader br;
		try {
			FileReader ler = new FileReader("./src/entrada.txt");
            BufferedReader reader = new BufferedReader(ler);  
            String linha;
            linha = reader.readLine();
            linha = linha.replace(" ", "0");
            numLinhas = Integer.parseInt(linha.substring(8, linha.length()));
            linha = reader.readLine();
            linha = linha.replace(" ", "0");
            numColunas = Integer.parseInt(linha.substring(8, linha.length()));
            linha = reader.readLine();
            
            geraLista();
            
            while((linha = reader.readLine()) != null ){
            	DadosColunas dado = new DadosColunas();
            	String[] texto = linha.split("\\s+");
            	dado.indice = Integer.parseInt(texto[1]);
            	dado.peso = Double.parseDouble(texto[2]);
            	for(int i = 3; i < texto.length; i++) {
            		texto[i] = texto[i].replaceAll(" ", "");
            		int nLinha = Integer.parseInt(texto[i]);
            		dado.linhasCobertas.add(nLinha);
            		listLinhas.get(nLinha-1).listaDados.add(dado);
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
		
		listLinhas.forEach(linha -> System.out.println(linha.linha + " " + linha.listaDados.size()));
		
	}
	
	public void geraLista() {
		for(int i = 0; i < numLinhas; i++) {
			DadosLinhas linhas = new DadosLinhas();
			linhas.setLinha(i);
			listLinhas.add(linhas);
		}
		listLinhas.size();
	}
	
	public void gerarInicial() {
		int colunasAnalizadas = 0;
		HashSet<Integer> linhasCobertas = new HashSet<>();
		Double pesoTotal = new Double(0);
		
		while((linhasCobertas.size() < numLinhas) && (colunasAnalizadas < 300)) {
			DadosColunas dadoAtual = listDados.get(colunasAnalizadas);
			pesoTotal += dadoAtual.getPeso();
			List<Integer> cobertura = dadoAtual.getLinhasCobertas();
			linhasCobertas.addAll(cobertura);
			colunasAnalizadas += 1;
		}
		
		System.out.println(pesoTotal);
	}
	
	public Individuo gerarIndividuo() {
		List<Integer> s = new ArrayList<>();
		HashMap<Integer, Integer> u = inicializaU();
		int w = 0;
		Random gerador = new Random();
		while (!u.isEmpty()) {
			Integer linha;
			do {
				linha = u.get(gerador.nextInt(numLinhas));
			} while(linha == null);
			double peso = 999;
			DadosColunas selecionado = new DadosColunas(); 
			for(DadosColunas dado : listLinhas.get(linha-1).listaDados) {
				double pesoColuna = dado.getPeso()/dado.getLinhasCobertas().size();
				if(pesoColuna < peso) {
					peso = pesoColuna;
					selecionado = dado;
				}
			}
			s.add(selecionado.getIndice());
			for(int teste : selecionado.getLinhasCobertas()) {
				w++;
				u.remove(teste);
				
			}			
		}
		Individuo individuo = new Individuo();
		double pesoTotal = 0;
		for(int teste : s) {
			pesoTotal += listDados.get(teste-1).getPeso();
			individuo.colunas.add(listDados.get(teste-1));
		}
		individuo.setPeso(pesoTotal);

		return individuo;
	}
	
	public List<Individuo> geraPopulacaoInicial(){
		List<Individuo> pop = new ArrayList<>();
		do {
			Individuo individuo = gerarIndividuo();
			pop.add(individuo);
		} while (pop.size() < 100);
		
		return pop;
	}
	
	public HashMap<Integer, Integer> inicializaU() {
		HashMap<Integer, Integer> u = new HashMap<>(); 
		for(int i = 1; i <= numLinhas; i++) {
			u.put(i, new Integer(i));
		}
		return u;
	}
	
	static class Individuo {
		private double peso;
		private List<DadosColunas> colunas = new ArrayList<>();
		
		public double getPeso() {
			return peso;
		}
		public void setPeso(double peso) {
			this.peso = peso;
		}
		public List<DadosColunas> getColunas() {
			return colunas;
		}
		public void setColunas(List<DadosColunas> colunas) {
			this.colunas = colunas;
		}
		
	}
	
	static class DadosLinhas {
		private int linha;
		private List<DadosColunas> listaDados = new ArrayList<>();
		
		public int getLinha() {
			return linha;
		}
		public void setLinha(int linha) {
			this.linha = linha;
		}
		public List<DadosColunas> getListaDados() {
			return listaDados;
		}
		public void setListaDados(List<DadosColunas> listaDados) {
			this.listaDados = listaDados;
		}
		
	}
	
	static class DadosColunas {
		private int indice;
		private double peso;
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
		public List<Integer> getLinhasCobertas() {
			return linhasCobertas;
		}
		public void setLinhasCobertas(List<Integer> linhasCobertas) {
			this.linhasCobertas = linhasCobertas;
		}
		public String toString() {
			return "(" + peso + " " + linhasCobertas.toString() + ")";
		}
	}
}