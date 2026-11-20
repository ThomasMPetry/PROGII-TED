import banco.Banco;
import config.Configuracoes;
import controller.ContaController;
import dao.ContaDAO;
import model.Conta;
import model.Operacao;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Configuracoes config = new Configuracoes();
        Banco banco = new Banco(config);
        ContaDAO contaDAO = new ContaDAO(banco);
        ContaController controller = new ContaController(contaDAO);

        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n================ SISTEMA BANCÁRIO (JDBC) ================");
            System.out.println("1. Cadastrar conta");
            System.out.println("2. Listar contas");
            System.out.println("3. Consultar conta");
            System.out.println("4. Realizar depósito");
            System.out.println("5. Realizar saque");
            System.out.println("6. Verificar extrato de uma conta");
            System.out.println("7. Excluir conta");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida! Digite um número.");
                continue;
            }

            switch (opcao) {
                case 1 -> {
                    System.out.println("\n--- Cadastrar Conta ---");
                    System.out.print("Tipo (1 - Conta Corrente | 2 - Conta Poupança): ");
                    int tipo = Integer.parseInt(scanner.nextLine());
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    System.out.print("Titular: ");
                    String titular = scanner.nextLine();
                    System.out.print("Saldo inicial: ");
                    double saldo = Double.parseDouble(scanner.nextLine().replace(",", "."));

                    double limite = 0;
                    if (tipo == 1) {
                        System.out.print("Limite da conta corrente: ");
                        limite = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    }

                    System.out.println(controller.cadastrarConta(tipo, numero, titular, saldo, limite));
                }
                case 2 -> {
                    System.out.println("\n--- Lista de Contas Cadastradas ---");
                    try {
                        List<Conta> contas = controller.listarContas();
                        if (contas.isEmpty()) {
                            System.out.println("Nenhuma conta cadastrada.");
                        } else {
                            contas.forEach(System.out::println);
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao listar contas: " + e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.println("\n--- Consultar Conta ---");
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    try {
                        Conta conta = controller.consultarPorNumero(numero);
                        if (conta == null) {
                            System.out.println("Conta não encontrada.");
                        } else {
                            System.out.println(conta);
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao consultar conta: " + e.getMessage());
                    }
                }
                case 4 -> {
                    System.out.println("\n--- Realizar Depósito ---");
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    System.out.print("Valor do depósito: ");
                    double valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.println(controller.depositar(numero, valor));
                }
                case 5 -> {
                    System.out.println("\n--- Realizar Saque ---");
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    System.out.print("Valor do saque: ");
                    double valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
                    System.out.println(controller.sacar(numero, valor));
                }
                case 6 -> {
                    System.out.println("\n--- Extrato da Conta ---");
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    try {
                        Conta conta = controller.consultarPorNumero(numero);
                        if (conta == null) {
                            System.out.println("Conta não encontrada.");
                        } else {
                            System.out.println("Conta: " + conta);
                            List<Operacao> extrato = controller.consultarExtrato(numero);
                            if (extrato.isEmpty()) {
                                System.out.println("Nenhuma operação registrada nesta conta.");
                            } else {
                                extrato.forEach(System.out::println);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao buscar extrato: " + e.getMessage());
                    }
                }
                case 7 -> {
                    System.out.println("\n--- Excluir Conta ---");
                    System.out.print("Número da conta: ");
                    String numero = scanner.nextLine();
                    System.out.println(controller.excluirConta(numero));
                }
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida!");
            }
        }
        scanner.close();
    }
}