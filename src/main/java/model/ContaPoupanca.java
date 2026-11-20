package model;

public class ContaPoupanca extends Conta {

    public ContaPoupanca() {
    }

    public ContaPoupanca(String numero, String titular, double saldo) {
        super(numero, titular, saldo);
    }

    public ContaPoupanca(Integer contaId, String numero, String titular, double saldo) {
        super(contaId, numero, titular, saldo);
    }

    @Override
    public String getTipo() {
        return "Conta Poupança";
    }

    @Override
    public boolean podeSacar(double valor) {
        return valor > 0 && valor <= getSaldo();
    }
}