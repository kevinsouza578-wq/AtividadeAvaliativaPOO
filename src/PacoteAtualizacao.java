/** Dados sobre um pacote disponibilizado pela fabricante. */
public final class PacoteAtualizacao {
    private final Versao versao;
    private final String descricao;
    private final String urlDownload;

    public PacoteAtualizacao(Versao versao, String descricao, String urlDownload) {
        if (versao == null) {
            throw new IllegalArgumentException("A versao e obrigatoria.");
        }
        if (descricao == null || descricao.trim().isEmpty()
                || urlDownload == null || urlDownload.trim().isEmpty()) {
            throw new IllegalArgumentException("Descricao e link sao obrigatorios.");
        }
        this.versao = versao;
        this.descricao = descricao;
        this.urlDownload = urlDownload;
    }

    public Versao getVersao() { return versao; }
    public String getDescricao() { return descricao; }
    public String getUrlDownload() { return urlDownload; }
}
