package model;

public abstract class Conta {

    private Integer contaId;
    private String numero;
    private String titular;
    private double saldo;

    public Conta() {
    }

    public Conta(String numero, String titular, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
    }

    public Conta(Integer contaId, String numero, String titular, double saldo) {
        this.contaId = contaId;
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldo;
    }

    public abstract String getTipo();

    public abstract boolean podeSacar(double valor);

    public Integer getContaId() {
        return contaId;
    }

    public void setContaId(Integer contaId) {
        this.contaId = contaId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Tipo: %s | Número: %s | Titular: %s | Saldo: R$ %.2f",
                contaId, getTipo(), numero, titular, saldo);
    }
}