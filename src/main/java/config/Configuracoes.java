package config;

import java.io.InputStream;
import java.util.Properties;

public class Configuracoes {

    private Properties propriedades;

    public Configuracoes() {
        propriedades = new Properties();
        try (InputStream lerArquivo = Configuracoes.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (lerArquivo == null) {
                throw new IllegalStateException("Arquivo db.properties não encontrado em resources.");
            }
            propriedades.load(lerArquivo);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao carregar o arquivo db.properties: " + e.getMessage(), e);
        }
    }

    public String getConfiguracao(String chave) {
        return propriedades.getProperty(chave);
    }
}