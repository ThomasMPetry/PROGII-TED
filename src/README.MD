# Sistema Bancário com Java, MySQL e JDBC

Aplicação console em Java para gerenciamento persistente de contas bancárias (Conta Corrente e Conta Poupança) utilizando JDBC, padrão DAO e transações SQL.

## Pré-requisitos
- Java JDK 17 ou superior
- Maven
- MySQL Server 8.4+ ou 9.x

## Configuração do Banco de Dados
1. Execute o arquivo `script.sql` no MySQL Workbench ou terminal MySQL para criar o banco de dados `banco` e as tabelas `tb_conta`, `tb_conta_corrente` e `tb_operacoes`.
2. Abra o arquivo `src/main/resources/db.properties` e preencha suas credenciais de acesso:
   ```properties
   bd.servidor=localhost
   bd.porta=3306
   bd.banco=banco
   bd.usuario=seu_usuario
   bd.senha=sua_senha