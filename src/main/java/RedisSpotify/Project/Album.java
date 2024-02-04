/**
 * La clase {@code Album} representa un álbum en el proyecto RedisSpotify.
 * Contiene información como ID, nombre, artistas, fecha de lanzamiento y una lista de canciones.
 */
package RedisSpotify.Project;

import java.util.List;
/**
 * Representa un álbum en el proyecto RedisSpotify.
 */
public class Album {
	private String ID;
	private String nombre;
	private List<String> artistas;
	private String FechaLanzamiento;
	private List<Cancion> canciones;
	 /**
     * Constructor de un objeto Album con los parámetros especificados.
     *
     * @param iD                el identificador único del álbum
     * @param nombre            el nombre del álbum
     * @param artistas          la lista de artistas asociados al álbum
     * @param fechaLanzamiento  la fecha de lanzamiento del álbum
     * @param canciones         la lista de canciones en el álbum
     */
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
	/**
     * Devuelve una representación en cadena del objeto Album.
     *
     * @return una representación en cadena del objeto Album
     */
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Album Details:\n");
	    sb.append("ID: ").append(ID).append("\n");
	    sb.append("Nombre: ").append(nombre).append("\n");
	    sb.append("Artistas: ").append(artistas).append("\n");
	    sb.append("Fecha de Lanzamiento: ").append(FechaLanzamiento).append("\n");
	    sb.append("Canciones:\n");
	    
	    for (Cancion cancion : canciones) {
	        sb.append(" - ").append(cancion.toString()).append("\n");
	    }

	    return sb.toString();
	}

}
