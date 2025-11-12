import java.util.Scanner;

import bplus.BPlusTree;

public class MainBPlus {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("--- Criador da Árvore B+ ---");
        System.out.print("Defina a ORDEM da árvore (ex: 3 ou 4): ");
        int ordem = 3;
        try {
            ordem = Integer.parseInt(scanner.nextLine());
            if (ordem < 3) {
                System.out.println("Ordem inválida, usando 3.");
                ordem = 3;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando ordem padrão: 3");
        }

        BPlusTree<Integer, String> arvore = new BPlusTree<>(ordem);
        System.out.println("Árvore B+ de ordem " + ordem + " criada.");
        System.out.println(arvore.toString());

        boolean executando = true;
        while (executando) {
            System.out.println("\n--- Menu Árvore B+ ---");
            System.out.println("1. Inserir (Chave, Valor)");
            System.out.println("2. Buscar (Chave)");
            System.out.println("3. Remover (Chave)");
            System.out.println("4. Buscar por Intervalo");
            System.out.println("5. Exibir Árvore");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            String escolha = scanner.nextLine();
            int chave = 0;
            int chaveFim = 0;
            String valor = "";

            try {
                switch (escolha) {
                    case "1":
                        System.out.print("Digite a Chave (int): ");
                        chave = Integer.parseInt(scanner.nextLine());
                        System.out.print("Digite o Valor (String): ");
                        valor = scanner.nextLine();
                        System.out.println("Inserindo {" + chave + ", " + valor + "}...");
                        arvore.insert(chave, valor);
                        System.out.println("--- Árvore após INSERIR ---");
                        System.out.println(arvore.toString());
                        break;
                    
                    case "2":
                        System.out.print("Digite a Chave (int) para BUSCAR: ");
                        chave = Integer.parseInt(scanner.nextLine());
                        valor = arvore.search(chave);
                        if (valor != null) {
                            System.out.println("Resultado: Chave " + chave + " encontrada. Valor = " + valor);
                        } else {
                            System.out.println("Resultado: Chave " + chave + " NÃO encontrada.");
                        }
                        break;
                        
                    case "3":
                        System.out.print("Digite a Chave (int) para REMOVER: ");
                        chave = Integer.parseInt(scanner.nextLine());
                        System.out.println("Removendo " + chave + "...");
                        arvore.delete(chave);
                        System.out.println("--- Árvore após REMOVER ---");
                        System.out.println(arvore.toString());
                        break;
                        
                    case "4":
                        System.out.print("Digite a Chave INICIAL (int): ");
                        chave = Integer.parseInt(scanner.nextLine());
                        System.out.print("Digite a Chave FINAL (int): ");
                        chaveFim = Integer.parseInt(scanner.nextLine());
                        System.out.println("Buscando valores no intervalo [" + chave + ", " + chaveFim + "]");
                        System.out.println("Resultado: " + arvore.searchRange(chave, chaveFim));
                        break;

                    case "5":
                        System.out.println("--- Estrutura Atual da Árvore ---");
                        System.out.println(arvore.toString());
                        break;
                        
                    case "0":
                        executando = false;
                        System.out.println("Encerrando teste...");
                        break;
                        
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Erro: Você deve digitar um número inteiro para a chave.");
            }
        }
        
        scanner.close();
    }
}