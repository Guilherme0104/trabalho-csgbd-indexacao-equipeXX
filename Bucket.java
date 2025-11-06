import java.util.ArrayList;
import java.util.List;

/**
 * Classe do Bucket (ou Página).
 * Guarda as chaves e a profundidade local.
 */
public class Bucket {

    private int profundidadeLocal;
    private int tamanhoMaximo;
    private List<Integer> chaves;

    /**
     * Constrói um novo bucket.
     * @param profundidadeLocal profundidade inicial
     * @param tamanhoMaximo capacidade máxima de chaves
     */
    public Bucket(int profundidadeLocal, int tamanhoMaximo) {
        this.profundidadeLocal = profundidadeLocal;
        this.tamanhoMaximo = tamanhoMaximo;
        this.chaves = new ArrayList<>();
    }

    /**
     * Tenta inserir uma chave.
     * @param chave Chave para inserir
     * @return false se o bucket estiver cheio, true se conseguiu (ou se a chave já existe).
     */
    public boolean insere(int chave) {
        // Se a chave já existe, não faz nada
        if (this.chaves.contains(chave)) {
            return true;
        }

        // Se está cheio, avisa o ExtendibleHash que precisa de split
        if (this.estaCheio()) {
            return false;
        }

        // Se tem espaço, adiciona
        this.chaves.add(chave);
        return true;
    }

    /**
     * Procura uma chave no bucket.
     * @param chave Chave para buscar
     * @return true se achou, false se não.
     */
    public boolean busca(int chave) {
        return this.chaves.contains(chave);
    }

    /**
     * Tenta remover uma chave.
     * @param chave Chave para remover
     * @return true se removeu, false se não encontrou.
     */
    public boolean remove(int chave) {
        // Usa Integer.valueOf() para remover o objeto (a chave)
        // e não o elemento no índice (posição)
        return this.chaves.remove(Integer.valueOf(chave));
    }

    /**
     * Confere se o bucket está cheio.
     * @return true se cheio, false se ainda tem espaço.
     */
    public boolean estaCheio() {
        return this.chaves.size() >= this.tamanhoMaximo;
    }

    // --- Getters e Setters ---
    // (Necessários para o 'split' na classe principal)

    public int getProfundidadeLocal() {
        return profundidadeLocal;
    }

    public void setProfundidadeLocal(int profundidadeLocal) {
        this.profundidadeLocal = profundidadeLocal;
    }

    public List<Integer> getChaves() {
        return chaves;
    }
    
    // Usado no 'split' para limpar o bucket antigo
    public void setChaves(List<Integer> novasChaves) {
        this.chaves = novasChaves;
    }
    
    /**
     * Método para ajudar a exibir/imprimir o bucket
     */
    @Override
    public String toString() {
        return "Bucket [profundidadeLocal=" + profundidadeLocal + ", chaves=" + chaves + "]";
    }
}