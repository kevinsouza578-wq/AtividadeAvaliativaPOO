import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Software {
    private final String nome;
    private Versao versaoInstalada;
    private final List<Versao> historico = new ArrayList<Versao>();

    public Software(String nome, Versao versaoInicial) {
        if (nome == null || nome.trim().isEmpty() || versaoInicial == null) {
            throw new IllegalArgumentException("Nome e versao inicial sao obrigatorios.");
        }
        this.nome = nome;
        this.versaoInstalada = versaoInicial;
        this.historico.add(versaoInicial);
    }

    public String getNome() {
        return nome;
    }

    public Versao getVersaoInstalada() {
        return versaoInstalada;
    }

    public List<Versao> getHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void instalarVersao(Versao novaVersao) {
        if (novaVersao == null || novaVersao.compareTo(versaoInstalada) <= 0) {
            throw new IllegalArgumentException("A versao nova deve ser superior a instalada.");
        }
        versaoInstalada = novaVersao;
        historico.add(novaVersao);
    }
}
