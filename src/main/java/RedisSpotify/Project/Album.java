package RedisSpotify.Project;

import java.util.List;

public class Album {
	private String ID;
	private String nombre;
	private List<String> artistas;
	private String FechaLanzamiento;
	private List<Cancion> canciones;

	public Album(String iD, String nombre, List<String> artistas, String fechaLanzamiento, List<Cancion> canciones) {
		ID = iD;
		this.nombre = nombre;
		this.artistas = artistas;
		FechaLanzamiento = fechaLanzamiento;
		this.canciones = canciones;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
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

	public String getFechaLanzamiento() {
		return FechaLanzamiento;
	}

	public void setFechaLanzamiento(String fechaLanzamiento) {
		FechaLanzamiento = fechaLanzamiento;
	}

	public List<Cancion> getCanciones() {
		return canciones;
	}

	public void setCanciones(List<Cancion> canciones) {
		this.canciones = canciones;
	}

	@Override
	public String toString() {
		return "Album [ID=" + ID + ", nombre=" + nombre + ", artistas=" + artistas + ", FechaLanzamiento="
				+ FechaLanzamiento + ", canciones=" + canciones + "]";
	}

}
