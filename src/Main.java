import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Main {

	private int numLinhas;
	private int numColunas;
	private int tamanhoPopulacao;
	private int taxaMutacao;
	private int condParada;
	private int qtdNovidade;
	private int qtdBuscaLocal;
	private boolean utilizarBuscaLocal;
	Long tempoInicio = (long) 0;
	ArrayList<DadosColunas> listDados = new ArrayList<>();
	ArrayList<DadosLinhas> listLinhas = new ArrayList<>();
	Random gerador = new Random();

	public static void main(String[] args) {
		Scanner leitor = new Scanner(System.in);
		Main main = new Main();

		System.out.println("-----------------------------------------------------------------");
		System.out.println("Algoritmo Genético para o Problema de Cobertura de Conjuntos");
		System.out.println("-----------------------------------------------------------------");
		System.out.print("Qual a quantidade de população desejada: ");
		main.tamanhoPopulacao = leitor.nextInt();
		System.out.print("Informe a taxa de mutação desejada em porcentagem (0 a 100): ");
		main.taxaMutacao = leitor.nextInt();
		System.out.print("Informe a condição de parada: ");
		main.condParada = leitor.nextInt();
		System.out.print("Deseja utilizar a Busca Local ?? (0-Não/1-Sim): ");
		int response = leitor.nextInt();
		
		if(response == 0) {
			main.utilizarBuscaLocal = false;
		}
		else {
			main.utilizarBuscaLocal = true;
			System.out.print("Qual a quantidade de Busca Local realizar: ");
			main.qtdBuscaLocal = leitor.nextInt();
		}
		
		System.out.println("-----------------------------------------------------------------");
		
		main.tempoInicio = System.currentTimeMillis();
		
		main.lerArquivo();
		main.algoritmoGenetico();

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

	public void geraLista() {
		for (int i = 0; i < numLinhas; i++) {
			DadosLinhas linhas = new DadosLinhas();
			linhas.setLinha(i);
			listLinhas.add(linhas);
		}
		listLinhas.size();
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
		Individuo dois = eliminaRedundancia3(individuo.getColunas());
		
		if(individuo.getPeso() != dois.getPeso()) {
			qtdNovidade++;
			//System.out.println("Peguei INICIO");
			//System.out.println(individuo.getPeso() + " " + dois.getPeso());
		}

		return dois;
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
		Individuo dois = eliminaRedundancia3(individuo.getColunas());
		
		if(individuo.getPeso() != dois.getPeso()) {
			qtdNovidade++;
			//System.out.println("Peguei Aqui");
			//System.out.println(individuo.getPeso() + " " + dois.getPeso());
		}
		
		return dois;
	}
	
	public Individuo realizaMutacao(List<DadosColunas> listaColunas) {
		
		List<DadosColunas> listaGerada = new ArrayList<>();
		
		listaGerada.addAll(listaColunas);
		
		for(int i = 0; i < listaColunas.size()/2; i++) {
			listaGerada.add(listDados.get(gerador.nextInt(listDados.size())));
		}
		
		Individuo individuo = eliminaRedundancia2(listaGerada);
		Individuo dois = eliminaRedundancia3(individuo.getColunas());
		
		if(individuo.getPeso() != dois.getPeso()) {
			qtdNovidade++;
			//System.out.println("Peguei");
			//System.out.println(individuo.getPeso() + " " + dois.getPeso());
		}

		return dois;
	}
	
	public Individuo realizaMutacao2(List<DadosColunas> listaColunas) {
		
		List<DadosColunas> listaGerada = new ArrayList<>();
		
		listaGerada.addAll(listaColunas);
		
		listaGerada.add(listDados.get(gerador.nextInt(listDados.size())));
		
		Individuo individuo = eliminaRedundancia2(listaGerada);

		return individuo;
	}
	
	public Individuo realizaBuscaLocal(List<DadosColunas> listaColunas, double peso) {
		for(int i = 0; i < qtdBuscaLocal; i++) {
			Individuo novo = realizaMutacao2(listaColunas);
			
			if(novo.getPeso() < peso) {
				return novo;
			}
		}
		
		return null;
	}

	public List<Individuo> geraPopulacaoInicial() {
		List<Individuo> pop = new ArrayList<>();
		System.out.println("Gerando População Inicial com " + tamanhoPopulacao + " indíviduos -> Aguarde ...");
		do {
			Individuo individuo = gerarIndividuo();
			pop.add(individuo);
		} while (pop.size() < tamanhoPopulacao);
		
		Collections.sort(pop);
		
		return pop;
	}
	
	public void algoritmoGenetico() {
		List<Individuo> pop = geraPopulacaoInicial();
		int t = 0;
		int contadorExecucao = 0;
		System.out.println("Iniciando Execução do Algorítmo Genético -> Aguarde ...");
		do {
			boolean modificado = false;
			Individuo novoIndividuo = realizaCruzamento(pop.get(gerador.nextInt(pop.size()/3)).getColunas(),
					pop.get(gerador.nextInt(pop.size()/3)).getColunas());
			if(gerador.nextInt(100) < taxaMutacao) {
				novoIndividuo = realizaMutacao(novoIndividuo.getColunas());
			}
			
			if(utilizarBuscaLocal) {
				Individuo buscaLocalIndividuo = realizaBuscaLocal(novoIndividuo.getColunas(), novoIndividuo.getPeso());
				if(buscaLocalIndividuo != null && buscaLocalIndividuo.getPeso() < novoIndividuo.getPeso()) {
					novoIndividuo = buscaLocalIndividuo;
				}
			}
			
			Individuo piorIndividuo = pop.get(pop.size()-1);
			
			if(novoIndividuo.getPeso() < piorIndividuo.getPeso()) {
				Individuo melhorIndividuo = pop.get(0);
				if(novoIndividuo.getPeso() < melhorIndividuo.getPeso()){
					System.out.println("* Nova melhor solução encontrada -> Peso: " + novoIndividuo.getPeso() +
							" Peso antigo: " + melhorIndividuo.getPeso());
				}
				pop.remove(piorIndividuo);
				pop.add(novoIndividuo);
				modificado = true;
				Collections.sort(pop);
			}
			
			if(modificado) {
				t = 0;
			}
			else {
				t++;
			}
			
			contadorExecucao++;
			
			if(contadorExecucao%500 == 0) {
				System.out.println("Executando Algoritmo Genético -> " + contadorExecucao + " Operações realizadas ...");
			}
				
		} while (t < condParada);
	
		System.out.println("-----------------------------------------------------------------");
		System.out.println("O Algoritmo Genetico foi executado " + condParada + " vezes sem nenhuma melhora ...");
		System.out.println("- Solução encontrada: " + pop.get(0).getPeso());
		System.out.println("- Tempo de execução: " + ((System.currentTimeMillis()-tempoInicio) / 1000L) + "s");
		System.out.println("- Parametros utilizados nesta execução: Tamanho População: " + tamanhoPopulacao +
				" Taxa de Mutação: " + taxaMutacao + " Condição de parada: " + condParada);
		System.out.println("- Colunas Participantes da solução: " + pop.get(0).getColunas());
		if(utilizarBuscaLocal) {
			System.out.println("* A Busca Local estava ativada.");
		}
		else {
			System.out.println("* A Busca Local estava desativada.");
		}
		System.out.println("- Nossa melhoria no algoritmo de eliminar redundancia ajudou a melhorar " + qtdNovidade + " individuos durante essa execução.");
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