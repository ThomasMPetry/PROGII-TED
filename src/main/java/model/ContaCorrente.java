package model;

public class ContaCorrente extends Conta {

    private double limite;

    public ContaCorrente() {
    }

    public ContaCorrente(String numero, String titular, double saldo, double limite) {
        super(numero, titular, saldo);
        this.limite = limite;
    }

    public ContaCorrente(Integer contaId, String numero, String titular, double saldo, double limite) {
        super(contaId, numero, titular, saldo);
        this.limite = limite;
    }

    @Override
    public String getTipo() {
        return "Conta Corrente";
    }

    @Override
    public boolean podeSacar(double valor) {
        return valor > 0 && valor <= (getSaldo() + limite);
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Limite: R$ %.2f | Disponível p/ Saque: R$ %.2f",
                limite, getSaldo() + limite);
    }
}