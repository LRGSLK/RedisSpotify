/**
 * La clase {@code Artista} representa a un artista en el proyecto RedisSpotify.
 * Contiene información como ID, nombre, género, popularidad y número de seguidores.
 */
package RedisSpotify.Project;

public class Artista {

	private String id;
	private String nombre;
	private String genero;
	private int popularidad;
	private int seguidores;
	/**
     * Construye un objeto Artista con los parámetros especificados.
     *
     * @param id          el identificador único del artista
     * @param nombre      el nombre del artista
     * @param genero      el género musical del artista
     * @param popularidad la popularidad del artista
     * @param seguidores  el número de seguidores del artista
     */
	public Artista(String id, String nombre, String genero, int popularidad, int seguidores) {
		this.id = id;
		this.nombre = nombre;
		this.genero = genero;
		this.popularidad = popularidad;
		this.seguidores = seguidores;
	}
	/**
     * Obtiene el género musical del artista.
     *
     * @return el género musical del artista
     */
	public String getGenero() {
		return genero;
	}
	public void setGenero(String genero) {
		this.genero = genero;
	}
	public int getPopularidad() {
		return popularidad;
	}
	public void setPopularidad(int popularidad) {
		this.popularidad = popularidad;
	}
	public int getSeguidores() {
		return seguidores;
	}
	public void setSeguidores(int seguidores) {
		this.seguidores = seguidores;
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
	/**
     * Devuelve una representación en cadena del objeto Artista.
     *
     * @return una representación en cadena del objeto Artista
     */
	@Override
	public String toString() {
		  return "Artista:\n"
			         + " - ID: " + id + "\n"
			         + " - Nombre: " + nombre + "\n"
			         + " - Genero: " + genero + "\n"
		  			 + " - Popularidad: " + popularidad + "\n"
			         + " - Seguidores: " + seguidores + "\n";


			}
}
