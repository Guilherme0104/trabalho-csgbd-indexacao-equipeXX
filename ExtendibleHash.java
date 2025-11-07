import java.util.ArrayList;
import java.util.List;

/**
 * Implementa a estrutura de Hash Extensível.
 * Gerencia o diretório e os buckets.
 */
public class ExtendibleHash {

    private int profundidadeGlobal;
    private int tamanhoBucket; // Tamanho máximo de chaves por bucket
    private List<Bucket> diretorio;

    public ExtendibleHash(int tamanhoBucket) {
        this.tamanhoBucket = tamanhoBucket;
        this.profundidadeGlobal = 1; 
        this.diretorio = new ArrayList<>();
        Bucket b1 = new Bucket(1, tamanhoBucket);
        Bucket b2 = new Bucket(1, tamanhoBucket);
        diretorio.add(b1); 
        diretorio.add(b2); 
    }

    private int calcularIndice(int chave) {
        int mascara = (1 << profundidadeGlobal) - 1;
        return chave & mascara;
    }

    // --- MÉTODOS DA API OBRIGATÓRIA ---

    public boolean busca(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        return bucketAlvo.busca(chave);
    }

    /**
     * Insere uma chave na estrutura.
     * (*** AQUI ESTÁ A CORREÇÃO: 'if' foi mudado para 'while' ***)
     * @param chave A chave para inserir.
     */
    public void insere(int chave) {
        // 1. Achar o bucket
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        
        // 2. Tentar inserir
        boolean conseguiuInserir = bucketAlvo.insere(chave);
        
        // 3. Se falhar, fazer split E TENTAR DE NOVO (em loop)
        // A MUDANÇA ESTÁ AQUI: de 'if' para 'while'
        while (!conseguiuInserir) {
            System.out.println("Bucket cheio! Acionando split para a chave: " + chave);
            
            // 3.1. Chama o split (que redistribui as chaves antigas)
            splitBucket(indice, bucketAlvo);
            
            // 3.2. TENTAR DE NOVO (pós-split)
            // O diretório/ponteiros podem ter mudado, então recalculamos
            indice = calcularIndice(chave); 
            bucketAlvo = diretorio.get(indice);
            
            // 3.3. Tenta inserir a chave no bucket (agora correto)
            // O loop 'while' vai verificar se 'conseguiuInserir' é true.
            // Se o bucket ainda estiver cheio (como no seu teste),
            // o loop vai rodar de novo, causando outro split.
            conseguiuInserir = bucketAlvo.insere(chave);
        }
    }


    public boolean remove(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        return bucketAlvo.remove(chave);
    }

    public void exibe() {
        System.out.println("--- Estado do Hash Extensível ---");
        System.out.println("Profundidade Global: " + profundidadeGlobal);
        
        List<Bucket> bucketsImpressos = new ArrayList<>();
        
        for (int i = 0; i < diretorio.size(); i++) {
            Bucket bucket = diretorio.get(i);
            
            String indiceBinario = String.format("%" + profundidadeGlobal + "s", 
                                    Integer.toBinaryString(i)).replace(' ', '0');
            
            System.out.print("Diretório [" + indiceBinario + "] (idx " + i + ") -> ");
            
            if (!bucketsImpressos.contains(bucket)) {
                System.out.println(bucket.toString());
                bucketsImpressos.add(bucket);
            } else {
                System.out.println("(Aponta para bucket já mostrado)");
            }
        }
        System.out.println("---------------------------------");
    }

    // --- MÉTODOS DE SPLIT (Estes estavam corretos) ---
    
    /**
     * Duplica o tamanho do diretório.
     */
    private void duplicarDiretorio() {
        List<Bucket> diretorioAntigo = this.diretorio;
        int tamanhoAntigo = diretorioAntigo.size();
        
        System.out.println("### DUPLICANDO DIRETÓRIO (PG de " + profundidadeGlobal + " para " + (profundidadeGlobal + 1) + ") ###");
        this.profundidadeGlobal++;
        int novoTamanho = 1 << this.profundidadeGlobal; // 2^profundidadeGlobal
        
        this.diretorio = new ArrayList<>(novoTamanho);
        
        for (int i = 0; i < novoTamanho; i++) {
            this.diretorio.add(diretorioAntigo.get(i % tamanhoAntigo));
        }
    }

    /**
     * Divide um bucket cheio, redistribuindo suas chaves ANTIGAS.
     */
    private void splitBucket(int indiceBucketCheio, Bucket bucketCheio) {
        
        // 1. Verifica se precisa duplicar o diretório
        if (bucketCheio.getProfundidadeLocal() == this.profundidadeGlobal) {
            duplicarDiretorio();
        }
        
        // 2. Cria um novo bucket e atualiza as profundidades
        int novaProfundidadeLocal = bucketCheio.getProfundidadeLocal() + 1;
        Bucket novoBucket = new Bucket(novaProfundidadeLocal, this.tamanhoBucket);
        bucketCheio.setProfundidadeLocal(novaProfundidadeLocal);
        
        // 3. Pega SÓ as chaves antigas para redistribuir
        List<Integer> chavesAntigas = new ArrayList<>(bucketCheio.getChaves());
        bucketCheio.setChaves(new ArrayList<>()); // Limpa o bucket antigo
        
        // 4. Atualiza os ponteiros do diretório
        int bitDiferenciador = 1 << (novaProfundidadeLocal - 1);
        
        for (int i = 0; i < this.diretorio.size(); i++) {
            if (this.diretorio.get(i) == bucketCheio) {
                if ((i & bitDiferenciador) != 0) { 
                    this.diretorio.set(i, novoBucket);
                }
            }
        }
        
        // 5. Re-insere as chaves ANTIGAS
        // Chamamos o 'insere' DO BUCKET, não do Hash.
        // Isso é seguro, pois os buckets estão vazios e não vai acionar outro split.
        for (int chave : chavesAntigas) {
            int indice = calcularIndice(chave); // Recalcula com a nova PG
            Bucket bucketAlvo = this.diretorio.get(indice);
            bucketAlvo.insere(chave); // Inserção direta no bucket
        }
    }
}