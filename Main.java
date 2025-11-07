/**
 * Classe principal para testar a implementação do Hash Extensível.
 */
public class Main {

    public static void main(String[] args) {
        
        // Define o tamanho máximo de chaves por bucket (ex: 2 chaves)
        int tamanhoBucket = 2;
        
        // Cria a estrutura de Hash Extensível
        ExtendibleHash hash = new ExtendibleHash(tamanhoBucket);

        System.out.println("--- INICIANDO TESTE ---");
        System.out.println("Estado Inicial (Tamanho do Bucket = " + tamanhoBucket + ")");
        hash.exibe(); // Mostra o estado inicial

        // --- Teste de Inserção (sem split) ---
        System.out.println("\n--- Inserindo chaves ---");
        
        // Chave 4 (binário 100) -> índice 0
        System.out.println("Inserindo chave: 4");
        hash.insere(4);
        hash.exibe();
        
        // Chave 5 (binário 101) -> índice 1
        System.out.println("Inserindo chave: 5");
        hash.insere(5);
        hash.exibe();
        
        // Chave 8 (binário 1000) -> índice 0
        System.out.println("Inserindo chave: 8");
        hash.insere(8);
        hash.exibe();

        // --- Teste de Busca ---
        System.out.println("\n--- Testando buscas ---");
        System.out.println("Buscando chave 5 (esperado: true): " + hash.busca(5));
        System.out.println("Buscando chave 8 (esperado: true): " + hash.busca(8));
        System.out.println("Buscando chave 99 (esperado: false): " + hash.busca(99));
        
        // --- Teste de Remoção ---
        System.out.println("\n--- Testando remoção ---");
        System.out.println("Removendo chave 5 (esperado: true): " + hash.remove(5));
        System.out.println("Buscando chave 5 de novo (esperado: false): " + hash.busca(5));
        hash.exibe(); // Mostra o estado após a remoção
        
        
        // --- Teste de Gatilho do Split (Parte 1) ---
        // Vamos encher o bucket do índice 1, que está vazio.
        // Chave 1 (binário 001) -> índice 1
        System.out.println("\n--- Inserindo chave 1 (para encher o bucket 1) ---");
        hash.insere(1);
        hash.exibe();
        
        // Chave 9 (binário 1001) -> índice 1
        // Agora o bucket do índice 1 está cheio com [1, 9]
        System.out.println("Inserindo chave 9 (para encher o bucket 1)");
        hash.insere(9);
        hash.exibe();

        // --- Teste de Gatilho do Split (Parte 2 - AGORA DE VERDADE) ---
        // O bucket [1] (índices 01, 11, etc.) agora está cheio com [1, 9].
        // Vamos tentar inserir a chave 17 (binário 10001 -> índice 1)
        System.out.println("\n--- TENTANDO O SPLIT (AGORA DE VERDADE) ---");
        System.out.println("Inserindo chave 17 (AGORA deve falhar e pedir split)");
        hash.insere(17); // Isso deve imprimir "Bucket cheio!"
        hash.exibe(); // O estado não deve ter mudado (chave 17 não foi inserida)
    }
}