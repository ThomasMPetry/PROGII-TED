package dao;

import banco.Banco;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Operacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO implements CrudDAO<Conta> {

    private Banco banco;

    public ContaDAO(Banco banco) {
        this.banco = banco;
    }

    @Override
    public Conta inserir(Conta entidade) throws SQLException {
        String sqlConta = "INSERT INTO tb_conta (numero, titular, saldo) VALUES (?, ?, ?)";
        String sqlCorrente = "INSERT INTO tb_conta_corrente (conta_id, limite) VALUES (?, ?)";

        try (Connection conn = banco.conectar()) {
            conn.setAutoCommit(false); // Inicia transação JDBC

            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setString(1, entidade.getNumero());
                stmtConta.setString(2, entidade.getTitular());
                stmtConta.setDouble(3, entidade.getSaldo());
                stmtConta.executeUpdate();

                try (ResultSet rs = stmtConta.getGeneratedKeys()) {
                    if (rs.next()) {
                        entidade.setContaId(rs.getInt(1));
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado para a conta.");
                    }
                }

                // Se for Conta Corrente, grava também na tabela especializada
                if (entidade instanceof ContaCorrente cc) {
                    try (PreparedStatement stmtCC = conn.prepareStatement(sqlCorrente)) {
                        stmtCC.setInt(1, cc.getContaId());
                        stmtCC.setDouble(2, cc.getLimite());
                        stmtCC.executeUpdate();
                    }
                }

                // Registra abertura de conta se houver saldo inicial
                if (entidade.getSaldo() > 0) {
                    registrarOperacao(conn, entidade.getContaId(), "DEPÓSITO INICIAL", entidade.getSaldo());
                }

                conn.commit(); // Confirma todas as inserções
                return entidade;

            } catch (SQLException e) {
                conn.rollback(); // Desfaz tudo em caso de erro (ex: número duplicado)
                throw e;
            }
        }
    }

    @Override
    public boolean atualizar(Conta entidade) throws SQLException {
        String sqlConta = "UPDATE tb_conta SET titular = ?, saldo = ? WHERE conta_id = ?";
        String sqlCorrente = "UPDATE tb_conta_corrente SET limite = ? WHERE conta_id = ?";

        try (Connection conn = banco.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlConta)) {
                stmt.setString(1, entidade.getTitular());
                stmt.setDouble(2, entidade.getSaldo());
                stmt.setInt(3, entidade.getContaId());
                int linhas = stmt.executeUpdate();

                if (entidade instanceof ContaCorrente cc) {
                    try (PreparedStatement stmtCC = conn.prepareStatement(sqlCorrente)) {
                        stmtCC.setDouble(1, cc.getLimite());
                        stmtCC.setInt(2, cc.getContaId());
                        stmtCC.executeUpdate();
                    }
                }

                conn.commit();
                return linhas > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public boolean deletar(Conta entidade) throws SQLException {
        String sql = "DELETE FROM tb_conta WHERE conta_id = ?";

        try (Connection conn = banco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entidade.getContaId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Conta> listar() throws SQLException {
        String sql = "SELECT c.conta_id, c.numero, c.titular, c.saldo, cc.limite "
                + "FROM tb_conta c "
                + "LEFT JOIN tb_conta_corrente cc ON c.conta_id = cc.conta_id "
                + "ORDER BY c.conta_id";

        List<Conta> contas = new ArrayList<>();

        try (Connection conn = banco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contas.add(mapearConta(rs));
            }
        }
        return contas;
    }

    @Override
    public List<Conta> busca(Integer id) throws SQLException {
        List<Conta> resultado = new ArrayList<>();
        Conta conta = buscaID(id);
        if (conta != null) {
            resultado.add(conta);
        }
        return resultado;
    }

    @Override
    public Conta buscaID(Integer id) throws SQLException {
        String sql = "SELECT c.conta_id, c.numero, c.titular, c.saldo, cc.limite "
                + "FROM tb_conta c "
                + "LEFT JOIN tb_conta_corrente cc ON c.conta_id = cc.conta_id "
                + "WHERE c.conta_id = ?";

        try (Connection conn = banco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConta(rs);
                }
            }
        }
        return null;
    }

    // --- MÉTODOS ESPECÍFICOS DO SISTEMA BANCÁRIO ---

    public Conta buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT c.conta_id, c.numero, c.titular, c.saldo, cc.limite "
                + "FROM tb_conta c "
                + "LEFT JOIN tb_conta_corrente cc ON c.conta_id = cc.conta_id "
                + "WHERE c.numero = ?";

        try (Connection conn = banco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearConta(rs);
                }
            }
        }
        return null;
    }

    public boolean depositar(Conta conta, double valor) throws SQLException {
        String sqlAtualizaSaldo = "UPDATE tb_conta SET saldo = saldo + ? WHERE conta_id = ?";

        try (Connection conn = banco.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlAtualizaSaldo)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, conta.getContaId());
                int linhas = stmt.executeUpdate();

                registrarOperacao(conn, conta.getContaId(), "DEPÓSITO", valor);

                conn.commit();
                return linhas > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public boolean sacar(Conta conta, double valor) throws SQLException {
        String sqlAtualizaSaldo = "UPDATE tb_conta SET saldo = saldo - ? WHERE conta_id = ?";

        try (Connection conn = banco.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlAtualizaSaldo)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, conta.getContaId());
                int linhas = stmt.executeUpdate();

                registrarOperacao(conn, conta.getContaId(), "SAQUE", valor);

                conn.commit();
                return linhas > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Operacao> listarExtrato(int contaId) throws SQLException {
        String sql = "SELECT operacoes_id, operacao, valor, conta_id "
                + "FROM tb_operacoes "
                + "WHERE conta_id = ? "
                + "ORDER BY operacoes_id";

        List<Operacao> extrato = new ArrayList<>();

        try (Connection conn = banco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    extrato.add(new Operacao(
                            rs.getInt("operacoes_id"),
                            rs.getString("operacao"),
                            rs.getDouble("valor"),
                            rs.getInt("conta_id")
                    ));
                }
            }
        }
        return extrato;
    }

    private void registrarOperacao(Connection conn, int contaId, String descricao, double valor) throws SQLException {
        String sqlOperacao = "INSERT INTO tb_operacoes (operacao, valor, conta_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmtOp = conn.prepareStatement(sqlOperacao)) {
            stmtOp.setString(1, descricao);
            stmtOp.setDouble(2, valor);
            stmtOp.setInt(3, contaId);
            stmtOp.executeUpdate();
        }
    }

    private Conta mapearConta(ResultSet rs) throws SQLException {
        int id = rs.getInt("conta_id");
        String numero = rs.getString("numero");
        String titular = rs.getString("titular");
        double saldo = rs.getDouble("saldo");
        double limite = rs.getDouble("limite");

        // Se rs.wasNull() for false, existe registro correspondente em tb_conta_corrente
        if (!rs.wasNull()) {
            return new ContaCorrente(id, numero, titular, saldo, limite);
        }
        return new ContaPoupanca(id, numero, titular, saldo);
    }
}