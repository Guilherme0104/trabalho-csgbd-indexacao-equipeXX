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

    /**
     * Construtor do Hash Extensível.
     * @param tamanhoBucket O tamanho máximo de chaves por bucket.
     */
    public ExtendibleHash(int tamanhoBucket) {
        this.tamanhoBucket = tamanhoBucket;
        this.profundidadeGlobal = 1; // Começa com profundidade 1
        this.diretorio = new ArrayList<>();

        // Inicializa o diretório com 2 buckets (profundidade global 1)
        // Ambos buckets têm profundidade local 1
        Bucket b1 = new Bucket(1, tamanhoBucket);
        Bucket b2 = new Bucket(1, tamanhoBucket);
        
        diretorio.add(b1); // Índice 0 (aponta para b1)
        diretorio.add(b2); // Índice 1 (aponta para b2)
    }

    /**
     * Calcula o índice do diretório para uma chave.
     * Usa os 'profundidadeGlobal' bits menos significativos da chave.
     */
    private int calcularIndice(int chave) {
        // Ex: profundidadeGlobal = 3. Máscara = (1 << 3) - 1 = 8 - 1 = 7 (binário 111)
        // Ex: chave = 10 (binário 1010). 1010 & 0111 = 0010 (decimal 2)
        int mascara = (1 << profundidadeGlobal) - 1;
        return chave & mascara;
    }

    // --- MÉTODOS DA API OBRIGATÓRIA ---

    /**
     * Busca uma chave na estrutura.
     * @param chave A chave para buscar.
     * @return true se encontrou, false se não.
     */
    public boolean busca(int chave) {
        // 1. Achar o índice do diretório
        int indice = calcularIndice(chave);
        
        // 2. Pegar o bucket correspondente
        Bucket bucketAlvo = diretorio.get(indice);
        
        // 3. Deixar o bucket fazer a busca interna
        return bucketAlvo.busca(chave);
    }

    /**
     * Insere uma chave na estrutura.
     * (Ainda falta implementar a lógica de 'split')
     * @param chave A chave para inserir.
     */
    public void insere(int chave) {
        // 1. Achar o índice do diretório
        int indice = calcularIndice(chave);
        
        // 2. Pegar o bucket correspondente
        Bucket bucketAlvo = diretorio.get(indice);
        
        // 3. Tentar inserir no bucket
        boolean conseguiuInserir = bucketAlvo.insere(chave);
        
        // 4. Se não conseguiu (bucket está cheio), precisamos fazer o 'split'
        if (!conseguiuInserir) {
            // --- Lógica do Split (PRÓXIMO PASSO) ---
            System.out.println("Bucket cheio! Precisamos fazer o split para a chave: " + chave);
            // splitBucket(indice, bucketAlvo, chave); // <-- Nosso próximo passo
        }
    }

    /**
     * Remove uma chave da estrutura.
     * (Ainda falta implementar a lógica de 'merge')
     * @param chave A chave para remover.
     * @return true se removeu, false se não encontrou.
     */
    public boolean remove(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        
        // A lógica de 'merge' (fusão de buckets) pode ser implementada aqui
        // mas é mais complexa e opcional no início.
        return bucketAlvo.remove(chave);
    }

    /**
     * Exibe o estado atual do Hash (Diretório e Buckets).
     */
    public void exibe() {
        System.out.println("--- Estado do Hash Extensível ---");
        System.out.println("Profundidade Global: " + profundidadeGlobal);
        
        // Para não imprimir o mesmo bucket várias vezes
        List<Bucket> bucketsImpressos = new ArrayList<>();
        
        for (int i = 0; i < diretorio.size(); i++) {
            Bucket bucket = diretorio.get(i);
            
            // Converte o índice para binário para ficar mais fácil de ler
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

    // --- Métodos de Split (A SEREM IMPLEMENTADOS) ---
    
    // private void splitBucket(int indice, Bucket bucketCheio, int chaveParaInserir) {
    //    // ... Lógica de divisão do bucket ...
    // }
    
    // private void duplicarDiretorio() {
    //    // ... Lógica para dobrar o diretório ...
    // }
}