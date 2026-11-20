package controller;

import dao.ContaDAO;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Operacao;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ContaController {

    private ContaDAO contaDAO;

    public ContaController(ContaDAO contaDAO) {
        this.contaDAO = contaDAO;
    }

    public String cadastrarConta(int tipo, String numero, String titular, double saldoInicial, double limite) {
        if (numero == null || numero.isBlank() || titular == null || titular.isBlank()) {
            return "Erro: Número da conta e titular são obrigatórios.";
        }
        if (saldoInicial < 0) {
            return "Erro: O saldo inicial não pode ser negativo.";
        }

        try {
            Conta novaConta;
            if (tipo == 1) {
                if (limite < 0) {
                    return "Erro: O limite da conta corrente não pode ser negativo.";
                }
                novaConta = new ContaCorrente(numero.trim(), titular.trim(), saldoInicial, limite);
            } else if (tipo == 2) {
                novaConta = new ContaPoupanca(numero.trim(), titular.trim(), saldoInicial);
            } else {
                return "Erro: Tipo de conta inválido.";
            }

            contaDAO.inserir(novaConta);
            return "Conta cadastrada com sucesso! ID gerado no banco: " + novaConta.getContaId();

        } catch (SQLIntegrityConstraintViolationException e) {
            return "Erro: Já existe uma conta cadastrada com o número '" + numero + "'.";
        } catch (SQLException e) {
            return "Erro ao cadastrar conta no banco: " + e.getMessage();
        }
    }

    public List<Conta> listarContas() throws SQLException {
        return contaDAO.listar();
    }

    public Conta consultarPorNumero(String numero) throws SQLException {
        return contaDAO.buscarPorNumero(numero.trim());
    }

    public String depositar(String numero, double valor) {
        if (valor <= 0) {
            return "Erro: O valor do depósito deve ser maior que zero.";
        }

        try {
            Conta conta = contaDAO.buscarPorNumero(numero.trim());
            if (conta == null) {
                return "Erro: Conta '" + numero + "' não encontrada.";
            }

            contaDAO.depositar(conta, valor);
            Conta atualizada = contaDAO.buscaID(conta.getContaId());
            return String.format("Depósito de R$ %.2f realizado! Novo saldo: R$ %.2f", valor, atualizada.getSaldo());

        } catch (SQLException e) {
            return "Erro ao realizar depósito: " + e.getMessage();
        }
    }

    public String sacar(String numero, double valor) {
        if (valor <= 0) {
            return "Erro: O valor do saque deve ser maior que zero.";
        }

        try {
            Conta conta = contaDAO.buscarPorNumero(numero.trim());
            if (conta == null) {
                return "Erro: Conta '" + numero + "' não encontrada.";
            }

            if (!conta.podeSacar(valor)) {
                return "Saque não permitido! Saldo/limite insuficiente para sacar R$ " + String.format("%.2f", valor);
            }

            contaDAO.sacar(conta, valor);
            Conta atualizada = contaDAO.buscaID(conta.getContaId());
            return String.format("Saque de R$ %.2f realizado! Novo saldo: R$ %.2f", valor, atualizada.getSaldo());

        } catch (SQLException e) {
            return "Erro ao realizar saque: " + e.getMessage();
        }
    }

    public List<Operacao> consultarExtrato(String numero) throws SQLException {
        Conta conta = contaDAO.buscarPorNumero(numero.trim());
        if (conta == null) {
            return null;
        }
        return contaDAO.listarExtrato(conta.getContaId());
    }

    public String excluirConta(String numero) {
        try {
            Conta conta = contaDAO.buscarPorNumero(numero.trim());
            if (conta == null) {
                return "Erro: Conta '" + numero + "' não encontrada.";
            }

            if (Math.abs(conta.getSaldo()) > 0.001) {
                return String.format("Erro: Não é possível excluir a conta pois o saldo atual é R$ %.2f (deve ser R$ 0,00).",
                        conta.getSaldo());
            }

            contaDAO.deletar(conta);
            return "Conta '" + numero + "' excluída com sucesso (juntamente com seus registros vinculados).";

        } catch (SQLException e) {
            return "Erro ao excluir conta: " + e.getMessage();
        }
    }
}