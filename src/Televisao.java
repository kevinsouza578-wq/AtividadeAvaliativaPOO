/** Heranca de Dispositivo + composicao: a TV possui um Software. */
public final class Televisao extends Dispositivo {
    private final Software software;

    public Televisao(String modelo, String numeroSerie, Software software) {
        super(modelo, numeroSerie);
        if (software == null) {
            throw new IllegalArgumentException("A TV precisa de um software.");
        }
        this.software = software;
    }

    public Software getSoftware() {
        return software;
    }

    @Override
    public String getTipo() {
        return "Smart TV";
    }

    public String mostrarInformacoes() {
        return getTipo() + " | Modelo: " + getModelo() + " | Serie: " + getNumeroSerie()
                + " | " + software.getNome() + " " + software.getVersaoInstalada();
    }
}
