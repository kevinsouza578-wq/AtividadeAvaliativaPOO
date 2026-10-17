import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/** Ponto de entrada: demonstra as acoes do sistema em um menu simples. */
public final class Main {
    public static void main(String[] args) {
        try (ServidorAtualizacoes servidor = new ServidorAtualizacoes()) {
            servidor.iniciar();

            // Objetos de exemplo: uma TV comprada e ainda na versao 1.0.0.
            Software software = new Software("SmartOS", Versao.interpretar("1.0.0"));
            Televisao tv = new Televisao("UNI TV 43", "TV-0001", software);
            GerenciadorAtualizacoes gerenciador =
                    new GerenciadorAtualizacoes(servidor.getUrlBase(), Paths.get("downloads"));

            if (args.length > 0 && "--teste".equals(args[0])) {
                executarTeste(tv, gerenciador);
                return;
            }

            System.out.println("=== SISTEMA DE ATUALIZACAO DE SMART TV ===");
            System.out.println("Servidor da fabricante: " + servidor.getUrlBase());
            System.out.println("Sem internet externa: o servidor funciona neste computador.");
            System.out.println("Pacotes .pkg sao simulados, NAO sao firmware real.\n");

            try (Scanner entrada = new Scanner(System.in)) {
                boolean ativo = true;
                while (ativo) {
                    System.out.println("\n1 - Exibir dados da TV");
                    System.out.println("2 - Buscar atualizacao");
                    System.out.println("3 - Baixar e instalar atualizacao");
                    System.out.println("4 - Listar versoes no servidor");
                    System.out.println("5 - Historico da TV");
                    System.out.println("0 - Encerrar");
                    System.out.print("Escolha: ");
                    if (!entrada.hasNextLine()) break;
                    String opcao = entrada.nextLine().trim();
                    try {
                        switch (opcao) {
                            case "1":
                                System.out.println(tv.mostrarInformacoes());
                                break;
                            case "2":
                                PacoteAtualizacao nova = gerenciador.buscarMaisRecente(tv);
                                if (nova == null) {
                                    System.out.println("A TV ja esta na versao mais recente.");
                                } else {
                                    System.out.println("Atualizacao encontrada: " + nova.getVersao());
                                    System.out.println("Correcao: " + nova.getDescricao());
                                    System.out.println("Link: " + nova.getUrlDownload());
                                }
                                break;
                            case "3":
                                Path arquivo = gerenciador.baixarEInstalar(tv);
                                if (arquivo == null) {
                                    System.out.println("Nenhuma atualizacao pendente.");
                                } else {
                                    System.out.println("Download concluido!");
                                    System.out.println("Arquivo: " + arquivo.toAbsolutePath());
                                    System.out.println("Nova versao instalada na simulacao: " + software.getVersaoInstalada());
                                }
                                break;
                            case "4":
                                System.out.println("Versoes oferecidas pela fabricante:");
                                for (PacoteAtualizacao pacote : gerenciador.listarPacotes()) {
                                    System.out.println("- " + pacote.getVersao() + ": " + pacote.getDescricao());
                                }
                                break;
                            case "5":
                                System.out.println("Versoes instaladas nesta TV: " + software.getHistorico());
                                break;
                            case "0":
                                ativo = false;
                                break;
                            default:
                                System.out.println("Opcao invalida. Tente novamente.");
                        }
                    } catch (IOException erro) {
                        System.out.println("Erro de comunicacao/download: " + erro.getMessage());
                    }
                }
            }
            System.out.println("Programa encerrado.");
        } catch (IOException erro) {
            System.err.println("Nao foi possivel iniciar o servidor local: " + erro.getMessage());
            System.exit(1);
        }
    }

    private static void executarTeste(Televisao tv, GerenciadorAtualizacoes gerenciador) throws IOException {
        if (!Versao.interpretar("1.0.0").equals(tv.getSoftware().getVersaoInstalada())) {
            throw new AssertionError("Versao inicial incorreta.");
        }
        PacoteAtualizacao disponivel = gerenciador.buscarMaisRecente(tv);
        if (disponivel == null || !Versao.interpretar("1.2.0").equals(disponivel.getVersao())) {
            throw new AssertionError("A ultima versao esperada nao foi encontrada.");
        }
        Path baixado = gerenciador.baixarEInstalar(tv);
        if (baixado == null || !java.nio.file.Files.isRegularFile(baixado)) {
            throw new AssertionError("O arquivo de atualizacao nao foi salvo.");
        }
        if (!Versao.interpretar("1.2.0").equals(tv.getSoftware().getVersaoInstalada())) {
            throw new AssertionError("A versao da TV nao mudou.");
        }
        if (gerenciador.buscarMaisRecente(tv) != null) {
            throw new AssertionError("Ainda foi indicada atualizacao apos instalar a mais recente.");
        }
        if (tv.getSoftware().getHistorico().size() != 2) {
            throw new AssertionError("O historico de atualizacoes esta errado.");
        }
        System.out.println("TESTES OK: busca, versao, download HTTP, instalacao simulada e historico.");
        System.out.println("Pacote salvo em: " + baixado.toAbsolutePath());
    }
}
