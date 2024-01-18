package RedisSpotify.Project;

public class Cancion {
	private String id;
    private String nombre;
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
	public Cancion(String id, String nombre) {

		this.id = id;
		this.nombre = nombre;
	}
	@Override
	public String toString() {
		return "Cancion [id=" + id + ", nombre=" + nombre + "]";
	}
}
