package RedisSpotify.Project;

/**
 * Clase que representa un artista de Spotify con su identificador y nombre.
 *
 * @version 1.0
 */
public class Artista {

	private String id;
	private String nombre;
	private String genero;
	private int popularidad;
	private int seguidores;

	/**
	 * Constructor para la clase Artista.
	 *
	 * @param id     Identificador único del artista.
	 * @param nombre Nombre del artista.
	 */
	

	public String getGenero() {
		return genero;
	}

	public Artista(String id, String nombre, String genero, int popularidad, int seguidores) {
		this.id = id;
		this.nombre = nombre;
		this.genero = genero;
		this.popularidad = popularidad;
		this.seguidores = seguidores;
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

	// GETTER Y SETTER
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

	@Override
	public String toString() {
		return "Artista [id=" + id + ", nombre=" + nombre + ", genero=" + genero + ", popularidad=" + popularidad
				+ ", seguidores=" + seguidores + "]";
	}
}
