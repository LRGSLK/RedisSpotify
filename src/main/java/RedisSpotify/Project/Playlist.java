package RedisSpotify.Project;

import java.util.List;

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
	public Playlist(String id, String nombre, List<Cancion> canciones) {
		this.id = id;
		this.nombre = nombre;
		this.canciones = canciones;
	}
	@Override
	public String toString() {
		return "Playlist [id=" + id + ", nombre=" + nombre + ", canciones=" + canciones + "]";
	}
	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}
}