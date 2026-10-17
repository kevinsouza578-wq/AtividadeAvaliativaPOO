/** Abstracao: comportamento e dados comuns aos dispositivos da fabricante. */
public abstract class Dispositivo {
    private final String modelo;
    private final String numeroSerie;

    protected Dispositivo(String modelo, String numeroSerie) {
        if (modelo == null || modelo.trim().isEmpty() || numeroSerie == null || numeroSerie.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo e numero de serie sao obrigatorios.");
        }
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
    }

    public String getModelo() {
        return modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public abstract String getTipo();
}
