package RedisSpotify.Project;

import java.util.List;

public class Cancion {
	private String id;
	private String nombre;
	List<String> artistas;

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

	@Override
	public String toString() {
		return "Cancion [id=" + id + ", nombre=" + nombre + ", artistas=" + artistas + "]";
	}

}
