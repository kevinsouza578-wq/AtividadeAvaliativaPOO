import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ServidorAtualizacoes implements AutoCloseable {
    private final HttpServer servidor;
    private final Map<String, byte[]> arquivos = new LinkedHashMap<String, byte[]>();
    private final Map<String, String> descricoes = new LinkedHashMap<String, String>();

    public ServidorAtualizacoes() throws IOException {
        adicionar("1.1.0", "Correcao de vulnerabilidade de autenticacao");
        adicionar("1.2.0", "Reforco de seguranca e correcoes adicionais");

        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/catalogo", new HttpHandler() {
            public void handle(HttpExchange troca) throws IOException {
                if (!somenteGet(troca)) return;
                StringBuilder resposta = new StringBuilder();
                for (Map.Entry<String, byte[]> item : arquivos.entrySet()) {
                    String versao = item.getKey();
                    String caminho = "/arquivos/firmware-" + versao + ".pkg";
                    resposta.append(versao).append('|')
                            .append(descricoes.get(versao)).append('|')
                            .append(caminho).append('\n');
                }
                enviar(troca, 200, resposta.toString().getBytes(StandardCharsets.UTF_8), "text/plain; charset=utf-8");
            }
        });
        servidor.createContext("/arquivos/", new HttpHandler() {
            public void handle(HttpExchange troca) throws IOException {
                if (!somenteGet(troca)) return;
                String caminho = troca.getRequestURI().getPath();
                for (Map.Entry<String, byte[]> item : arquivos.entrySet()) {
                    if (caminho.equals("/arquivos/firmware-" + item.getKey() + ".pkg")) {
                        enviar(troca, 200, item.getValue(), "application/octet-stream");
                        return;
                    }
                }
                enviar(troca, 404, "Arquivo nao encontrado".getBytes(StandardCharsets.UTF_8), "text/plain");
            }
        });
    }

    private void adicionar(String versao, String descricao) {
        String conteudo = "PACOTE DIDATICO - SMART TV\nVersao: " + versao
                + "\nAlteracoes: " + descricao + "\n";
        arquivos.put(versao, conteudo.getBytes(StandardCharsets.UTF_8));
        descricoes.put(versao, descricao);
    }

    public void iniciar() {
        servidor.start();
    }

    public String getUrlBase() {
        return "http://127.0.0.1:" + servidor.getAddress().getPort();
    }

    private static boolean somenteGet(HttpExchange troca) throws IOException {
        if ("GET".equals(troca.getRequestMethod())) return true;
        enviar(troca, 405, "Metodo nao permitido".getBytes(StandardCharsets.UTF_8), "text/plain");
        return false;
    }

    private static void enviar(HttpExchange troca, int codigo, byte[] bytes, String tipo) throws IOException {
        troca.getResponseHeaders().set("Content-Type", tipo);
        troca.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    @Override
    public void close() {
        servidor.stop(0);
    }
}
