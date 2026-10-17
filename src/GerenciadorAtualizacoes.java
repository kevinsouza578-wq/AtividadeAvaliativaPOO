import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Classe utilitaria que consulta, baixa e instala atualizacoes. */
public final class GerenciadorAtualizacoes {
    private final String urlBase;
    private final Path pastaDownloads;

    public GerenciadorAtualizacoes(String urlBase, Path pastaDownloads) {
        this.urlBase = urlBase;
        this.pastaDownloads = pastaDownloads;
    }

    public List<PacoteAtualizacao> listarPacotes() throws IOException {
        List<PacoteAtualizacao> pacotes = new ArrayList<PacoteAtualizacao>();
        HttpURLConnection conexao = abrir(urlBase + "/catalogo");
        try {
            verificarResposta(conexao);
            try (BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream(), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue;
                    String[] campos = linha.split("\\|", -1);
                    if (campos.length != 3) throw new IOException("Catalogo malformado.");
                    String caminho = campos[2];
                    if (!caminho.matches("/arquivos/firmware-[0-9]+\\.[0-9]+\\.[0-9]+\\.pkg")) {
                        throw new IOException("Link de download invalido no catalogo.");
                    }
                    try {
                        Versao versao = Versao.interpretar(campos[0]);
                        if (!caminho.equals("/arquivos/firmware-" + versao + ".pkg")) {
                            throw new IOException("A versao nao corresponde ao arquivo do catalogo.");
                        }
                        pacotes.add(new PacoteAtualizacao(versao, campos[1], urlBase + caminho));
                    } catch (IllegalArgumentException erro) {
                        throw new IOException("Dados invalidos no catalogo: " + erro.getMessage(), erro);
                    }
                }
            }
        } finally {
            conexao.disconnect();
        }
        return pacotes;
    }

    public PacoteAtualizacao buscarMaisRecente(Televisao tv) throws IOException {
        PacoteAtualizacao melhor = null;
        Versao instalada = tv.getSoftware().getVersaoInstalada();
        for (PacoteAtualizacao pacote : listarPacotes()) {
            if (pacote.getVersao().compareTo(instalada) > 0
                    && (melhor == null || pacote.getVersao().compareTo(melhor.getVersao()) > 0)) {
                melhor = pacote;
            }
        }
        return melhor;
    }

    public Path baixarEInstalar(Televisao tv) throws IOException {
        PacoteAtualizacao pacote = buscarMaisRecente(tv);
        if (pacote == null) return null;

        Files.createDirectories(pastaDownloads);
        Path destino = pastaDownloads.resolve("firmware-" + pacote.getVersao() + ".pkg");
        HttpURLConnection conexao = abrir(pacote.getUrlDownload());
        try {
            verificarResposta(conexao);
            try (InputStream entrada = conexao.getInputStream()) {
                Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException erro) {
            Files.deleteIfExists(destino);
            throw erro;
        } finally {
            conexao.disconnect();
        }
        tv.getSoftware().instalarVersao(pacote.getVersao());
        return destino;
    }

    private HttpURLConnection abrir(String endereco) throws IOException {
        HttpURLConnection conexao = (HttpURLConnection) new URL(endereco).openConnection();
        conexao.setRequestMethod("GET");
        conexao.setConnectTimeout(3000);
        conexao.setReadTimeout(3000);
        return conexao;
    }

    private void verificarResposta(HttpURLConnection conexao) throws IOException {
        if (conexao.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Erro HTTP " + conexao.getResponseCode());
        }
    }
}
