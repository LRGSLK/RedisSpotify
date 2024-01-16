package RedisSpotify.Project;

/**
 * Clase que representa un artista de Spotify con su identificador y nombre.
 *
 * @version 1.0
 */
public class Artista {

 
    private String id;
    private String nombre;

    /**
     * Constructor para la clase Artista.
     *
     * @param id     Identificador único del artista.
     * @param nombre Nombre del artista.
     */
    public Artista(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // GETTER Y  SETTER
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Sobrescribe el método toString para obtener una representación en cadena del objeto Artista.
     *
     * @return Representación en cadena del objeto Artista.
     */
    @Override
    public String toString() {
        return "Artista [id = " + id + ", nombre = " + nombre + "]";
    }
}
