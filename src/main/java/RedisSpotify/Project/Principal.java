package RedisSpotify.Project;

import java.util.List;
import java.util.Scanner;
import redis.clients.jedis.Jedis;

/**
 * Clase principal que ejecuta la aplicación de búsqueda de artistas en Spotify
 * y almacena la información en una base de datos Redis.
 *
 * @version 1.0
 */
public class Principal {

	/**
	 * Método principal que inicia la aplicación.
	 *
	 * @param args Argumentos de línea de comandos (no utilizado).
	 */
	public static void main(String[] args) {
		try (Jedis jedis = new Jedis("localhost", 6379)) {
			List<Artista> artistIds = null;

			try {
				// Obtener el token de acceso de Spotify
				String accessToken = APISpotify.getAccessToken();

				// Solicitar al usuario el nombre del artista a buscar
				Scanner scanner = new Scanner(System.in);
				System.out.println("Introduce el nombre del artista a buscar:");
				String nombreArtista = scanner.nextLine();

				// Obtener la lista de artistas a partir del nombre
				artistIds = APISpotify.getSampleArtistIds(accessToken, nombreArtista);

				// Imprimir y almacenar cada artista en Redis
				for (Artista ar : artistIds) {
					System.out.println(ar);
					guardarArtistaRedis(jedis, ar);
				}
			} catch (Exception e) {
				// Manejar cualquier excepción ocurrida durante la ejecución
				e.printStackTrace();
			}
		}
	}

	/**
	 * Guarda la información de un artista en una base de datos Redis.
	 *
	 * @param jedis   Cliente Redis.
	 * @param artista Objeto Artista que se va a almacenar.
	 */
	private static void guardarArtistaRedis(Jedis jedis, Artista artista) {
		String key = "artista:" + artista.getId();
		String nombre = artista.getNombre();

		// Iniciar una transacción para almacenar la información del artista en Redis
		try {
			jedis.set(key, nombre);
		} catch (Exception e) {
			// Manejar la excepción en caso de error durante la transacción
			e.printStackTrace();
		}
	}
}
