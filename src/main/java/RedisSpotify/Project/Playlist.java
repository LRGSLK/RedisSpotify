/**
 * La clase {@code Playlist} representa una lista de reproducción en el proyecto RedisSpotify.
 * Contiene información sobre el identificador, nombre y las canciones que la componen.
 */
package RedisSpotify.Project;

import java.util.List;
/**
 * Clase que representa una lista de reproducción en el proyecto RedisSpotify.
 */
public class Playlist {
    private String id;
    private String nombre;
    private List<Cancion> canciones;
    
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
	public List<Cancion> getCanciones() {
		return canciones;
	}
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}
	/**
     * Constructor de la clase Playlist.
     *
     * @param id        El identificador único de la lista de reproducción.
     * @param nombre    El nombre de la lista de reproducción.
     * @param canciones La lista de canciones que pertenecen a la lista de reproducción.
     */
	public Playlist(String id, String nombre, List<Cancion> canciones) {
		this.id = id;
		this.nombre = nombre;
		this.canciones = canciones;
	}
	/**
     * Representación en cadena de la lista de reproducción, incluyendo su identificador,
     * nombre y la lista de canciones.
     *
     * @return Una cadena que representa la lista de reproducción.
     */
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Detalles de la Playlist:\n");
	    sb.append("ID: ").append(id).append("\n");
	    sb.append("Nombre: ").append(nombre).append("\n");
	    sb.append("Canciones:\n");
	    
	    for (Cancion cancion : canciones) {
	        sb.append(" - ").append(cancion.toString()).append("\n");
	    }

	    return sb.toString();
	}
	
}