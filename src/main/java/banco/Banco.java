package banco;

import config.Configuracoes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {

    private String servidor;
    private String porta;
    private String banco;
    private String usuario;
    private String senha;

    public Banco(Configuracoes configuracoes) {
        this.servidor = configuracoes.getConfiguracao("bd.servidor");
        this.porta = configuracoes.getConfiguracao("bd.porta");
        this.banco = configuracoes.getConfiguracao("bd.banco");
        this.usuario = configuracoes.getConfiguracao("bd.usuario");
        this.senha = configuracoes.getConfiguracao("bd.senha");
    }

    public Connection conectar() throws SQLException {
        String url = "jdbc:mysql://" + servidor + ":" + porta + "/" + banco
                + "?useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true";
        return DriverManager.getConnection(url, usuario, senha);
    }
}