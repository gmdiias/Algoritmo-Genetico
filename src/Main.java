import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

class Main {

	private int numLinhas;
	private int numColunas;
	ArrayList<DadosColunas> listDados = new ArrayList<>();
	ArrayList<DadosLinhas> listLinhas = new ArrayList<>();
	Random gerador = new Random();

	public static void main(String[] args) {
		Main main = new Main();

		main.lerArquivo();
		main.printDadosObtidos();

		List<Individuo> listIndividuo = main.geraPopulacaoInicial();
		
		Collections.sort(listIndividuo);
		
		System.out.println("Melhor: " + listIndividuo.get(0).getPeso() + " Colunas: " + listIndividuo.get(0).getColunas());

	}

	public void lerArquivo() {
		BufferedReader br;
		try {
			FileReader ler = new FileReader("./src/entrada6.txt");
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

			while ((linha = reader.readLine()) != null) {
				DadosColunas dado = new DadosColunas();
				String[] texto = linha.split("\\s+");
				dado.indice = Integer.parseInt(texto[1]);
				dado.peso = Double.parseDouble(texto[2]);
				for (int i = 3; i < texto.length; i++) {
					texto[i] = texto[i].replaceAll(" ", "");
					int nLinha = Integer.parseInt(texto[i]);
					dado.linhasCobertas.add(nLinha);
					listLinhas.get(nLinha - 1).listaDados.add(dado);
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
		listDados.forEach(
				dado -> System.out.println(dado.indice + " " + dado.peso + " " + dado.linhasCobertas.toString()));

		listLinhas.forEach(linha -> System.out.println(linha.linha + " " + linha.listaDados.size()));

	}

	public void geraLista() {
		for (int i = 0; i < numLinhas; i++) {
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

		while ((linhasCobertas.size() < numLinhas) && (colunasAnalizadas < 300)) {
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
		while (!u.isEmpty()) {
			Integer linha;
			do {
				linha = u.get(gerador.nextInt(numLinhas+1));
			} while (linha == null);
			double peso = 999;
			DadosColunas selecionado = new DadosColunas();
			for (DadosColunas dado : listLinhas.get(linha - 1).listaDados) {
				int dividendo = verificaIntersecao(dado.getLinhasCobertas(), u);
				double pesoColuna = dado.getPeso() / dividendo;
				if (pesoColuna <= peso) {
					peso = pesoColuna;
					selecionado = dado;
				}
			}
			s.add(selecionado.getIndice());
			for (int teste : selecionado.getLinhasCobertas()) {
				w++;
				u.remove(teste);
			}
			
		}
		Individuo individuo = eliminaRedundancia(s);
		return individuo;
	}
	
	public Individuo eliminaRedundancia(List<Integer> s) {
		List<DadosColunas> listaColunas = new ArrayList<>();
		for (int teste : s) {
			listaColunas.add(listDados.get(teste - 1));
		}
		
		HashMap<Integer, Integer> u = inicializaU();
		Random gerador = new Random();
		DadosColunas dado;
		int cont = 0; 
		do {
			do {
				dado = listaColunas.get(gerador.nextInt(listaColunas.size()));
			} while (dado == null);
			listaColunas.remove(dado);
			if(!verificaIndividuo(listaColunas)) {
				listaColunas.add(dado);
				cont ++;
			}
			else {
				cont = 0;
			}
		} while(!u.isEmpty() && cont < numLinhas);
		
		Individuo individuo = new Individuo();
		double pesoTotal = 0;
		for(DadosColunas dados : listaColunas) {
			pesoTotal += dados.getPeso();
		}
		individuo.setColunas(listaColunas);
		individuo.setPeso(pesoTotal);
		
		return individuo;
	}
	
	public Individuo eliminaRedundancia2(List<DadosColunas> listaColunas) {		
		HashMap<Integer, Integer> u = inicializaU();
		DadosColunas dado;
		int cont = 0; 
		do {
			do {
				dado = listaColunas.get(gerador.nextInt(listaColunas.size()));
			} while (dado == null);
			listaColunas.remove(dado);
			if(!verificaIndividuo(listaColunas)) {
				listaColunas.add(dado);
				cont ++;
			}
			else {
				cont = 0;
			}
		} while(!u.isEmpty() && cont < numLinhas);
		
		Individuo individuo = new Individuo();
		double pesoTotal = 0;
		for(DadosColunas dados : listaColunas) {
			pesoTotal += dados.getPeso();
		}
		individuo.setColunas(listaColunas);
		individuo.setPeso(pesoTotal);
		
		return individuo;
	}
	
	public Individuo eliminaRedundancia3(List<DadosColunas> listaColunas) {		
		HashMap<Integer, Integer> u = inicializaU();
		DadosColunas dado;
		int cont = 0; 
		int i = listaColunas.size()-1;
		do {
			dado = listaColunas.get(i);
			listaColunas.remove(dado);
			if(!verificaIndividuo(listaColunas)) {
				listaColunas.add(dado);
				cont ++;
			}
			else {
				cont = 0;
			}
			i--;
			if(i < 1) {
				i = 0;
			}
		} while(!u.isEmpty() && cont < numLinhas);
		
		Individuo individuo = new Individuo();
		double pesoTotal = 0;
		for(DadosColunas dados : listaColunas) {
			pesoTotal += dados.getPeso();
		}
		individuo.setColunas(listaColunas);
		individuo.setPeso(pesoTotal);
		
		return individuo;
	}
	
	public boolean verificaIndividuo(List<DadosColunas> cobertura) {
		HashSet<Integer> linhasCobertas = new HashSet<>();
		
		for(DadosColunas dados : cobertura) {
			dados.linhasCobertas.forEach(dado -> linhasCobertas.add(dado));
		}
		
		if(linhasCobertas.size() == numLinhas) {
			return true;
		}
		
		return false;
	}

	public int verificaIntersecao(List<Integer> linhasCobertas, HashMap<Integer, Integer> linhasDescobertas) {
		int contador = 0;
		for (int teste : linhasCobertas) {
			if (linhasDescobertas.get(teste) != null) {
				contador++;
			}
		}
		if (contador == 0) {
			return 1;
		}
		return contador;
	}
	
	public Individuo realizaCruzamento(List<DadosColunas> listaColunasIndividuoA,
			List<DadosColunas> listaColunasIndividuoB) {
		
		List<DadosColunas> listaGerada = new ArrayList<>();
		
		listaGerada.addAll(listaColunasIndividuoA);
		listaGerada.addAll(listaColunasIndividuoB);
		
		Individuo individuo = eliminaRedundancia2(listaGerada);
		
		return individuo;
	}
	
	public Individuo realizaMutacao(List<DadosColunas> listaColunas) {
		
		List<DadosColunas> listaGerada = new ArrayList<>();
		
		listaGerada.addAll(listaColunas);
		
		listaGerada.add(listDados.get(gerador.nextInt(listDados.size())));
		
		Individuo individuo = eliminaRedundancia3(listaGerada);

		return individuo;
	}

	public List<Individuo> geraPopulacaoInicial() {
		List<Individuo> pop = new ArrayList<>();
		do {
			Individuo individuo = gerarIndividuo();
			pop.add(individuo);
		} while (pop.size() < 10000);
		
		Collections.sort(pop);
		
		List<Individuo> popCruzamento = new ArrayList<>();
		do{
			popCruzamento.add(realizaCruzamento(pop.get(gerador.nextInt(10000/100)).getColunas(), pop.get(gerador.nextInt(10000/100)).getColunas()));
		} while (popCruzamento.size() < 10000);
		
		Collections.sort(popCruzamento);
		
		List<Individuo> popMutacao = new ArrayList<>();
		do {
			popMutacao.add(realizaMutacao(popCruzamento.get(gerador.nextInt(10000)).getColunas()));
		} while (popMutacao.size() < 2000);
		
		Collections.sort(popMutacao);
		
		return pop;
	}

	public HashMap<Integer, Integer> inicializaU() {
		HashMap<Integer, Integer> u = new HashMap<>();
		for (int i = 1; i <= numLinhas; i++) {
			u.put(i, new Integer(i));
		}
		return u;
	}

	static class Individuo implements Comparable<Individuo> {
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

		@Override
		public int compareTo(Individuo arg0) {
			 if (this.peso > arg0.getPeso()) {
		          return 1;
		     }
		     if (this.peso < arg0.getPeso()) {
		          return -1;
		     }
		     return 0;
		}
		
		public void clonarIndividuo(Individuo antigo) {
			this.peso = antigo.getPeso();
			this.colunas = antigo.getColunas();
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