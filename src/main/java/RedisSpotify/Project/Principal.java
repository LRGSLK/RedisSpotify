package RedisSpotify.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

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
			String accessToken = null;

			try {
				// Obtener el token de acceso de Spotify
				accessToken = APISpotify.getAccessToken();
				Scanner scanner = new Scanner(System.in);
				System.out.println("Introduce el nombre del album a buscar:");
				String nombreAlbum = scanner.nextLine();
				Album album = APISpotify.searchAlbumByName(accessToken, nombreAlbum);
				System.out.println(album.toString());

				Scanner scanner1 = new Scanner(System.in);
				System.out.println("Introduce el nombre de la cacion a buscar:");
				String nombreCacion = scanner1.nextLine();
				Cancion cancion = APISpotify.searchSongsByName(accessToken, nombreCacion);
				System.out.println(cancion.toString());

				Scanner scanner2 = new Scanner(System.in);
				System.out.println("Introduce el nombre de la Playlist a buscar:");
				String nombrePlaylist = scanner2.nextLine();
				Playlist playlist = APISpotify.searchPlaylistsByName(accessToken, nombrePlaylist);
				System.out.println(playlist.toString());

				Scanner scanner3 = new Scanner(System.in);
				System.out.println("Introduce el nombre del Artista a buscar:");
				String nombreArtista = scanner3.nextLine();
				Artista artista = APISpotify.seachArtistByName(accessToken, nombreArtista);
				System.out.println(artista.toString());

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
		String artistKey = "artista:" + artista.getNombre();
		Map<String, String> artistData = new HashMap<>();
		artistData.put("IdArtista", artista.getId());
		artistData.put("genero", artista.getGenero());
		artistData.put("popularidad", String.valueOf(artista.getPopularidad()));
		artistData.put("seguidores", String.valueOf(artista.getSeguidores()));

		String indexKey = "indices:artistas";
		// Iniciar una transacción para almacenar la información del artista en Redis
		Transaction t = jedis.multi();
		try {
			// Almacenar la información del artista
			t.hmset(artistKey, artistData);
			// Agregar la clave del artista al índice
			t.sadd(indexKey, artistKey);

			// Ejecutar la transacción
			t.exec();
		} catch (Exception e) {
			// En caso de error, descartar la transacción
			t.discard();
			e.printStackTrace();
		}
	}

	private static void actualizarGeneroArtistaRedis(Jedis jedis, String artistaId, String nuevoGenero) {
		String artistKey = "artista:" + artistaId;

		// Iniciar una transacción para actualizar la información del artista en Redis
		Transaction t = jedis.multi();
		try {
			// Actualizar solo el campo 'genero' del artista
			t.hset(artistKey, "genero", nuevoGenero);

			// Ejecutar la transacción
			t.exec();
		} catch (Exception e) {
			// En caso de error, descartar la transacción
			t.discard();
			e.printStackTrace();
		}
	}
}
