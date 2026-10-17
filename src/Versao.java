import java.util.Objects;

public final class Versao implements Comparable<Versao> {
    private final int maior;
    private final int menor;
    private final int correcao;

    public Versao(int maior, int menor, int correcao) {
        if (maior < 0 || menor < 0 || correcao < 0) {
            throw new IllegalArgumentException("Os numeros da versao nao podem ser negativos.");
        }
        this.maior = maior;
        this.menor = menor;
        this.correcao = correcao;
    }

    public static Versao interpretar(String texto) {
        if (texto == null || !texto.matches("\\d+\\.\\d+\\.\\d+")) {
            throw new IllegalArgumentException("Versao invalida: " + texto);
        }
        String[] partes = texto.split("\\.");
        try {
            return new Versao(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException("Numero da versao muito grande: " + texto, erro);
        }
    }

    @Override
    public int compareTo(Versao outra) {
        int resultado = Integer.compare(this.maior, outra.maior);
        if (resultado != 0) return resultado;
        resultado = Integer.compare(this.menor, outra.menor);
        if (resultado != 0) return resultado;
        return Integer.compare(this.correcao, outra.correcao);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Versao)) return false;
        Versao outra = (Versao) objeto;
        return maior == outra.maior && menor == outra.menor && correcao == outra.correcao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maior, menor, correcao);
    }

    @Override
    public String toString() {
        return maior + "." + menor + "." + correcao;
    }
}
