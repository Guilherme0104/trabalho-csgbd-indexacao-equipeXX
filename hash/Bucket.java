package hash;
import java.util.ArrayList;
import java.util.List;

public class Bucket {

    private int profundidadeLocal;
    private int tamanhoMaximo;
    private List<Integer> chaves;
    
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
        if (this.chaves.contains(chave)) {
            return true;
        }

        if (this.estaCheio()) {
            return false;
        }
        
        this.chaves.add(chave);
        return true;
    }

    public boolean busca(int chave) {
        return this.chaves.contains(chave);
    }

    public boolean remove(int chave) {

        return this.chaves.remove(Integer.valueOf(chave));
    }

    public boolean estaCheio() {
        return this.chaves.size() >= this.tamanhoMaximo;
    }

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
    
    @Override
    public String toString() {
        return "Bucket [profundidadeLocal=" + profundidadeLocal + ", chaves=" + chaves + "]";
    }
}