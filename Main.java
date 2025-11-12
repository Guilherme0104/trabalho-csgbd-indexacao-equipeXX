import java.util.Scanner;

import hash.ExtendibleHash;

/*
 * Classe principal interativa para testar o Hash Extensível.
 * Permite ao usuário definir o tamanho do bucket e executar operações.
 */
public class Main {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        //Defini o tamanho do bucket
        System.out.println("--- Criador do Hash Extensível ---");
        System.out.print("Defina o tamanho máximo de chaves por bucket (ex: 2): ");
        int tamanhoBucket = 2;
        try {
            tamanhoBucket = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Usando tamanho padrão: 2");
        }

        //Cria a Hash
        ExtendibleHash hash = new ExtendibleHash(tamanhoBucket);
        System.out.println("Hash Extensível criado com buckets de tamanho " + tamanhoBucket);
        hash.exibe();

        boolean executando = true;
        while (executando) {
            System.out.println("\n--- Menu de Teste ---");
            System.out.println("1. Inserir chave");
            System.out.println("2. Buscar chave");
            System.out.println("3. Remover chave");
            System.out.println("4. Exibir estrutura");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            String escolha = scanner.nextLine();
            int chave = 0;
            
            try {
                switch (escolha) {
                    case "1":
                        System.out.print("Digite a chave para INSERIR: ");
                        chave = Integer.parseInt(scanner.nextLine());
                        System.out.println("Inserindo " + chave + "...");
                        hash.insere(chave);
                        hash.exibe();
                        break;
                    
                    case "2":
                        System.out.print("Digite a chave para BUSCAR: ");
                        chave = Integer.parseInt(scanner.nextLine());
                        boolean encontrado = hash.busca(chave);
                        System.out.println("Resultado da busca por " + chave + ": " + encontrado);
                        break;
                        
                    case "3":
                        System.out.print("Digite a chave para REMOVER: ");
                        chave = Integer.parseInt(scanner.nextLine());
                        boolean removido = hash.remove(chave);
                        System.out.println("Resultado da remoção de " + chave + ": " + removido);
                        if(removido) {
                            hash.exibe();
                        }
                        break;
                        
                    case "4":
                        hash.exibe();
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