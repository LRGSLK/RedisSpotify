/**
 * La clase {@code Cancion} representa una canción en el proyecto RedisSpotify.
 * Contiene información como ID, nombre y lista de artistas.
 */
package RedisSpotify.Project;

import java.util.List;

/**
 * Clase que representa una canción en el proyecto RedisSpotify.
 */
public class Cancion {
	private String id;
	private String nombre;
	List<String> artistas;
	/**
     * Constructor de un objeto Cancion con los parámetros especificados.
     *
     * @param id       el identificador único de la canción
     * @param nombre   el nombre de la canción
     * @param artistas la lista de artistas asociados a la canción
     */
	public Cancion(String id, String nombre, List<String> artistas) {
		this.id = id;
		this.nombre = nombre;
		this.artistas = artistas;
	}

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

	public List<String> getArtistas() {
		return artistas;
	}

	public void setArtistas(List<String> artistas) {
		this.artistas = artistas;
	}
	
	/**
     * Devuelve una representación en cadena del objeto Cancion.
     *
     * @return una representación en cadena del objeto Cancion
     */
	@Override
	public String toString() {
	    return "Cancion:\n"
	         + " - ID: " + id + "\n"
	         + " - Nombre: " + nombre + "\n"
	         + " - Artistas: " + artistas + "\n";
	}


}
