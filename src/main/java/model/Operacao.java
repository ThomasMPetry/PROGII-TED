package model;

public class Operacao {

    private Integer operacoesId;
    private String operacao;
    private double valor;
    private Integer contaId;

    public Operacao() {
    }

    public Operacao(Integer operacoesId, String operacao, double valor, Integer contaId) {
        this.operacoesId = operacoesId;
        this.operacao = operacao;
        this.valor = valor;
        this.contaId = contaId;
    }

    public Integer getOperacoesId() {
        return operacoesId;
    }

    public void setOperacoesId(Integer operacoesId) {
        this.operacoesId = operacoesId;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Integer getContaId() {
        return contaId;
    }

    public void setContaId(Integer contaId) {
        this.contaId = contaId;
    }

    @Override
    public String toString() {
        return String.format("Op #%d | %-25s | Valor: R$ %.2f", operacoesId, operacao, valor);
    }
}