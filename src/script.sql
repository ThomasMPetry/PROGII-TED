-- Script de criação do banco de dados e tabelas - Sistema Bancário (JDBC)

CREATE DATABASE IF NOT EXISTS banco
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banco;

CREATE TABLE IF NOT EXISTS tb_conta (
                                        conta_id INT AUTO_INCREMENT PRIMARY KEY,
                                        numero VARCHAR(20) NOT NULL UNIQUE,
                                        titular VARCHAR(100) NOT NULL,
                                        saldo DECIMAL(12,2) NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tb_conta_corrente (
                                                 conta_id INT PRIMARY KEY,
                                                 limite DECIMAL(12,2) NOT NULL,
                                                 FOREIGN KEY (conta_id)
                                                     REFERENCES tb_conta(conta_id)
                                                     ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tb_operacoes (
                                            operacoes_id INT AUTO_INCREMENT PRIMARY KEY,
                                            operacao VARCHAR(100) NOT NULL,
                                            valor DECIMAL(12,2) NOT NULL,
                                            conta_id INT,
                                            FOREIGN KEY (conta_id)
                                                REFERENCES tb_conta(conta_id)
                                                ON DELETE CASCADE
) ENGINE=InnoDB;