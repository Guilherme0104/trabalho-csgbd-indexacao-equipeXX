package hash;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtendibleHash {

    private int profundidadeGlobal;
    private int tamanhoBucket;
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

    public boolean busca(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        return bucketAlvo.busca(chave);
    }

    public void insere(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        
        boolean conseguiuInserir = bucketAlvo.insere(chave);
        
        while (!conseguiuInserir) {
            System.out.println("Bucket cheio! Acionando split para a chave: " + chave);
            splitBucket(indice, bucketAlvo);
            indice = calcularIndice(chave);
            bucketAlvo = diretorio.get(indice);
            conseguiuInserir = bucketAlvo.insere(chave);
        }
    }

    /*
     * Remove uma chave da estrutura.
     * Contém a lógica de 'merge' (fusão).
     */
    public boolean remove(int chave) {
        int indice = calcularIndice(chave);
        Bucket bucketAlvo = diretorio.get(indice);
        
        boolean removido = bucketAlvo.remove(chave);
        
        if (!removido) {
            return false;
        }

        // --- INÍCIO DA LÓGICA DE FUSÃO (MERGE) ---
        // A fusão não é um método separado, ela acontece aqui.
        
        if (bucketAlvo.getChaves().isEmpty() && bucketAlvo.getProfundidadeLocal() > 1) {
            
            int profundidadeLocal = bucketAlvo.getProfundidadeLocal();
            int indiceIrmao = indice ^ (1 << (profundidadeLocal - 1));
            Bucket bucketIrmao = diretorio.get(indiceIrmao);

            // Verifica se o irmão pode ser fundido
            if (bucketIrmao.getProfundidadeLocal() == profundidadeLocal) {
                System.out.println("### FUSÃO DE BUCKET (Bucket " + indice + " com " + indiceIrmao + ") ###");
                
                // 1. A FUSÃO: Aponta todos os ponteiros do bucket vazio para o irmão
                for (int i = 0; i < diretorio.size(); i++) {
                    if (diretorio.get(i) == bucketAlvo) {
                        diretorio.set(i, bucketIrmao);
                    }
                }
                
                // 2. Atualiza a profundidade local do irmão
                bucketIrmao.setProfundidadeLocal(profundidadeLocal - 1);
                
                // 3. Tenta encolher o diretório (CHAMA O MÉTODO DE ENCOLHIMENTO)
                tentarEncolherDiretorio();
            }
        }

        return true;
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

    private void duplicarDiretorio() {
        List<Bucket> diretorioAntigo = this.diretorio;
        int tamanhoAntigo = diretorioAntigo.size();
        System.out.println("### DUPLICANDO DIRETÓRIO (PG de " + profundidadeGlobal + " para " + (profundidadeGlobal + 1) + ") ###");
        this.profundidadeGlobal++;
        int novoTamanho = 1 << this.profundidadeGlobal;
        this.diretorio = new ArrayList<>(novoTamanho);
        for (int i = 0; i < novoTamanho; i++) {
            this.diretorio.add(diretorioAntigo.get(i % tamanhoAntigo));
        }
    }

    private void splitBucket(int indiceBucketCheio, Bucket bucketCheio) {
        if (bucketCheio.getProfundidadeLocal() == this.profundidadeGlobal) {
            duplicarDiretorio();
        }
        int novaProfundidadeLocal = bucketCheio.getProfundidadeLocal() + 1;
        Bucket novoBucket = new Bucket(novaProfundidadeLocal, this.tamanhoBucket);
        bucketCheio.setProfundidadeLocal(novaProfundidadeLocal);
        List<Integer> chavesAntigas = new ArrayList<>(bucketCheio.getChaves());
        bucketCheio.setChaves(new ArrayList<>());
        int bitDiferenciador = 1 << (novaProfundidadeLocal - 1);
        for (int i = 0; i < this.diretorio.size(); i++) {
            if (this.diretorio.get(i) == bucketCheio) {
                if ((i & bitDiferenciador) != 0) {
                    this.diretorio.set(i, novoBucket);
                }
            }
        }
        for (int chave : chavesAntigas) {
            int indice = calcularIndice(chave);
            Bucket bucketAlvo = this.diretorio.get(indice);
            bucketAlvo.insere(chave);
        }
    }
    
    // --- MÉTODO DE ENCOLHIMENTO (SHRINK) ---
    /**
     * Verifica se o diretório pode ser encolhido (shrink) após uma fusão (merge).
     * Este método é chamado *depois* que a fusão ocorre.
     */
    private void tentarEncolherDiretorio() {
        boolean podeEncolher = true;
        
        if (profundidadeGlobal == 1) {
            return;
        }

        Set<Bucket> bucketsUnicos = new HashSet<>(this.diretorio);
        for (Bucket b : bucketsUnicos) {
            if (b.getProfundidadeLocal() >= this.profundidadeGlobal) {
                podeEncolher = false;
                break;
            }
        }
        
        if (podeEncolher) {
            System.out.println("### ENCOLHENDO DIRETÓRIO (PG de " + profundidadeGlobal + " para " + (profundidadeGlobal - 1) + ") ###");
            
            this.profundidadeGlobal--;
            int novoTamanho = 1 << this.profundidadeGlobal;
            
            List<Bucket> novoDiretorio = new ArrayList<>(this.diretorio.subList(0, novoTamanho));
            this.diretorio = novoDiretorio;
            
            // Chama recursivamente
            tentarEncolherDiretorio();
        }
    }
}