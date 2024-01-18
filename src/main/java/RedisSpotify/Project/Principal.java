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

				// Solicitar al usuario el nombre del artista a buscar
				 Scanner scanner = new Scanner(System.in);
					System.out.println("Introduce el nombre del artista a buscar:");
					String nombreArtista = scanner.nextLine();

				// Obtener la lista de artistas a partir del nombre
				artistIds = APISpotify.getArtistInfo(accessToken, nombreArtista);

				// Imprimir y almacenar cada artista en Redis
				for (Artista ar : artistIds) {
					System.out.println(ar);
					guardarArtistaRedis(jedis, ar);
				}
			} catch (Exception e) {
				// Manejar cualquier excepción ocurrida durante la ejecución
				e.printStackTrace();
			}
			 try {
				 Scanner scanner = new Scanner(System.in);
					System.out.println("Introduce el nombre de PlayList a buscar:");
					String nombrePlayList = scanner.nextLine();
				String accessTokena = APISpotify.getAccessToken();
				
				 List<Playlist> foundPlaylists = APISpotify.searchPlaylistsByName(accessTokena,nombrePlayList);

				    if (foundPlaylists != null && !foundPlaylists.isEmpty()) {
				        for (Playlist playlist : foundPlaylists) {
				            System.out.println("Playlist: " + playlist.getNombre());
				            System.out.println("Canciones:");
				            for (Cancion cancion : playlist.getCanciones()) {
				                System.out.println(" - " + cancion.getNombre());
				            }
				            System.out.println();
				        }
				    } else {
				        System.out.println("No se encontraron playlists.");
				    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
		 String artistKey = "artista:" + artista.getId();
		    Map<String, String> artistData = new HashMap<>();
		    artistData.put("nombre", artista.getNombre());
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
}
