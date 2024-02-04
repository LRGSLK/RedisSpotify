/**
 * La clase {@code APISpotify} proporciona métodos para interactuar con la API de Spotify.
 */
package RedisSpotify.Project;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
/**
 * La clase  APISpotify ofrece una serie de métodos estáticos para interactuar con la API de Spotify.
 * Proporciona funcionalidades para autenticarse mediante el flujo de credenciales de cliente y realizar
 * búsquedas de artistas, playlists, álbumes y canciones específicas dentro del catálogo de Spotify.
 * Utiliza HTTP para las solicitudes y JSON para el manejo de las respuestas.
 *
 * Requiere las dependencias de Apache HTTP para realizar las solicitudes HTTP y Gson para el análisis de JSON.
 */
public class APISpotify {
	private static final Logger LOGGER = LoggerFactory.getLogger(APISpotify.class);

	private static final String clientId = "543191e2fffa48ba958c42ea57c49ec0";
	private static final String clientSecret = "a4a4d78e02f04bf68f301a9ae1f627bb";

	 /**
     * Obtiene un token de acceso de la API de Spotify utilizando el flujo de credenciales de cliente.
     * Este token es necesario para realizar solicitudes autenticadas a los endpoints de la API de Spotify.
     *
     * @return Una cadena que representa el token de acceso.
     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
     */
	static String obtenerAcceso() throws Exception {
		HttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost("https://accounts.spotify.com/api/token");

		String encoding = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
		post.setHeader("Authorization", "Basic " + encoding);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(new StringEntity("grant_type=client_credentials"));

		HttpResponse response = client.execute(post);
		String json = EntityUtils.toString(response.getEntity());

		JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
		return jsonObject.get("access_token").getAsString();
	}

	   /**
     * Busca un artista en Spotify por nombre y devuelve un objeto  Artista con los detalles del primer resultado.
     *
     * @param accessToken Token de acceso válido para la API de Spotify.
     * @param nombreArtista El nombre del artista a buscar.
     * @return Un objeto  Artista con los detalles del artista encontrado, o null si no se encuentra ninguno.
     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
     */
	static Artista buscarArtistaPorNombre(String accessToken, String nombreArtista) {
	    Artista artista = null;
	    try {
	        HttpClient client = HttpClients.createDefault();
	        HttpGet request = new HttpGet("https://api.spotify.com/v1/search?q="
	                + URLEncoder.encode(nombreArtista, StandardCharsets.UTF_8) + "&type=artist");

	        request.setHeader("Authorization", "Bearer " + accessToken);

	        HttpResponse response = client.execute(request);
	        String json = EntityUtils.toString(response.getEntity());
	        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
	        JsonArray artists = jsonObject.getAsJsonObject("artists").getAsJsonArray("items");

	        if (artists != null)  {
	                JsonObject artist = artists.get(0).getAsJsonObject();
	                String id = artist.get("id").getAsString();
	                String nombre = artist.get("name").getAsString();
	                JsonArray genresArray = artist.getAsJsonArray("genres");
	                String genero = (genresArray != null && genresArray.size() > 0) ? genresArray.get(0).getAsString() : "Unknown";
	                int popularidad = artist.get("popularity").getAsInt();
	                int seguidores = artist.getAsJsonObject("followers").get("total").getAsInt();

	                artista =new Artista(id, nombre, genero, popularidad, seguidores);
	        }
	    } catch (Exception e) {
	    	LOGGER.error("Error en la busqueda de artista por nombre " + e.getMessage());
	    }

	    return artista;
	}

	 /**
     * Busca playlists en Spotify por nombre y devuelve un objeto  Playlist con los detalles del primer resultado.
     *
     * @param accessToken Token de acceso válido para la API de Spotify.
     * @param playlistName El nombre de la playlist a buscar.
     * @return Un objeto  Playlist con los detalles de la playlist encontrada, o code null si no se encuentra ninguna.
     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
     */
	static Playlist buscarPlayListPorNombre(String accessToken, String playlistName) {
	    Playlist playlist = null;
	    try {
	        // Primero, buscar playlists por nombre
	        HttpClient client = HttpClients.createDefault();
	        HttpGet searchRequest = new HttpGet("https://api.spotify.com/v1/search?q="
	                + URLEncoder.encode(playlistName, StandardCharsets.UTF_8) + "&type=playlist");

	        searchRequest.setHeader("Authorization", "Bearer " + accessToken);

	        HttpResponse searchResponse = client.execute(searchRequest);
	        String searchJson = EntityUtils.toString(searchResponse.getEntity());
	        
	        
	        JsonObject searchObject = JsonParser.parseString(searchJson).getAsJsonObject();
	        JsonArray items = searchObject.getAsJsonObject("playlists").getAsJsonArray("items");
	        

	        // Luego, para cada playlist encontrada, obtener detalles y canciones
	        if (items.size() > 0) {
	            JsonObject item = items.get(0).getAsJsonObject();
	            String id = item.get("id").getAsString();
	            String nombre = item.get("name").getAsString();

	            // Solicitud adicional para obtener canciones de la playlist
	            HttpGet tracksRequest = new HttpGet("https://api.spotify.com/v1/playlists/" + id + "/tracks");
	            tracksRequest.setHeader("Authorization", "Bearer " + accessToken);

	            HttpResponse tracksResponse = client.execute(tracksRequest);
	            String tracksJson = EntityUtils.toString(tracksResponse.getEntity());
	           

	            JsonObject tracksObject = JsonParser.parseString(tracksJson).getAsJsonObject();
	            JsonArray tracks = tracksObject.getAsJsonArray("items");

	            List<Cancion> canciones = new ArrayList<>();
	            for (int j = 0; j < tracks.size(); j++) {
	                JsonObject track = tracks.get(j).getAsJsonObject().getAsJsonObject("track");
	                String trackId = track.get("id").getAsString();
	                String trackName = track.get("name").getAsString();
	                // Obtener nombres de artistas para cada canción
	                List<String> artistasCancion = new ArrayList<>();
	                JsonArray artistsCancion = track.getAsJsonArray("artists");
	                for (JsonElement artistElem : artistsCancion) {
	                    JsonObject artistObj = artistElem.getAsJsonObject();
	                    artistasCancion.add(artistObj.get("name").getAsString());
	                }

	                canciones.add(new Cancion(trackId, trackName, artistasCancion)); // Asegúrate de que la clase Cancion pueda manejar artistas
	             }

	            playlist = new Playlist(id, nombre, canciones);
	        }
	    } catch (Exception e) {
	    	LOGGER.error("Error en la busqueda de Playlist por nombre " + e.getMessage());
	    }

	    return playlist;
	}
	/**
     * Busca un álbum en Spotify por ID y devuelve un objeto Album con los detalles del álbum.
     *
     * @param accessToken Token de acceso válido para la API de Spotify.
     * @param albumId El ID único del álbum a buscar.
     * @return Un objeto Album con los detalles del álbum, o  null si no se encuentra.
     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
     */
	 public static Album buscarAlbumPorID(String accessToken, String albumId) {
	        Album album = null;
	        try {
	            // Solicitar detalles del álbum
	            HttpClient client = HttpClients.createDefault();
	            HttpGet albumRequest = new HttpGet("https://api.spotify.com/v1/albums/"
	                    + URLEncoder.encode(albumId, StandardCharsets.UTF_8));

	            albumRequest.setHeader("Authorization", "Bearer " + accessToken);

	            HttpResponse albumResponse = client.execute(albumRequest);
	            String albumJson = EntityUtils.toString(albumResponse.getEntity());

	            JsonObject albumObject = JsonParser.parseString(albumJson).getAsJsonObject();

	            String id = albumObject.get("id").getAsString();
	            String nombre = albumObject.get("name").getAsString();
	            String fechaLanzamiento = albumObject.get("release_date").getAsString();

	            // Obtener artistas del álbum
	            List<String> artistas = new ArrayList<>();
	            JsonArray artistsArray = albumObject.getAsJsonArray("artists");
	            for (int i = 0; i < artistsArray.size(); i++) {
	                JsonObject artist = artistsArray.get(i).getAsJsonObject();
	                artistas.add(artist.get("name").getAsString());
	            }

	            // Obtener canciones del álbum
	            List<Cancion> canciones = new ArrayList<>();
	            JsonArray tracks = albumObject.getAsJsonObject("tracks").getAsJsonArray("items");
	            for (int j = 0; j < tracks.size(); j++) {
	                JsonObject track = tracks.get(j).getAsJsonObject();
	                String trackId = track.get("id").getAsString();
	                String trackName = track.get("name").getAsString();
	                
	                // Obtener nombres de artistas para cada canción
	                List<String> artistasCancion = new ArrayList<>();
	                JsonArray artistsCancion = track.getAsJsonArray("artists");
	                for (JsonElement artistElem : artistsCancion) {
	                    JsonObject artistObj = artistElem.getAsJsonObject();
	                    artistasCancion.add(artistObj.get("name").getAsString());
	                }

	                canciones.add(new Cancion(trackId, trackName, artistasCancion)); // Asegúrate de que la clase Cancion pueda manejar artistas
	           
	            }

	            album = new Album(id, nombre, artistas, fechaLanzamiento, canciones);

	        } catch (Exception e) {
		    	LOGGER.error("Error en la busqueda de album por ID " + e.getMessage());
	        }

	        return album;
	    }
	 /**
	     * Busca álbumes en Spotify por nombre y devuelve un objeto code Album con los detalles del primer resultado.
	     *
	     * @param accessToken Token de acceso válido para la API de Spotify.
	     * @param albumName El nombre del álbum a buscar.
	     * @return Un objeto  Album con los detalles del álbum encontrado, o  null si no se encuentra ninguno.
	     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
	     */
	 public static Album buscarAlbumPorNombre(String accessToken, String albumName) {
		    Album album = null;
		    try {
		        // Buscar álbumes por nombre
		        HttpClient client = HttpClients.createDefault();
		        HttpGet searchRequest = new HttpGet("https://api.spotify.com/v1/search?q="
		                + URLEncoder.encode(albumName, StandardCharsets.UTF_8) + "&type=album");

		        searchRequest.setHeader("Authorization", "Bearer " + accessToken);

		        HttpResponse searchResponse = client.execute(searchRequest);
		        String searchJson = EntityUtils.toString(searchResponse.getEntity());

		        JsonObject searchObject = JsonParser.parseString(searchJson).getAsJsonObject();
		        JsonArray items = searchObject.getAsJsonObject("albums").getAsJsonArray("items");

		        if (items.size() > 0) {
		            JsonObject item = items.get(0).getAsJsonObject();
		            String id = item.get("id").getAsString();
		            return buscarAlbumPorID(accessToken, id);
		        }
		    } catch (Exception e) {
		    	LOGGER.error("Error en la busqueda de album por nombre " + e.getMessage());
		    }

		    return album;
		}
	 /**
	     * Busca canciones en Spotify por nombre y devuelve un objeto  Cancion con los detalles del primer resultado.
	     *
	     * @param accessToken Token de acceso válido para la API de Spotify.
	     * @param songName El nombre de la canción a buscar.
	     * @return Un objeto  Cancion con los detalles de la canción encontrada, o  null si no se encuentra ninguna.
	     * @throws Exception Si ocurre un error durante la solicitud HTTP o el análisis de la respuesta.
	     */
	  static Cancion searchSongsByName(String accessToken, String songName) {
		    Cancion cancion = null;
		    try {
		        // Crear cliente HTTP y configurar la solicitud
		        HttpClient client = HttpClients.createDefault();
		        HttpGet searchRequest = new HttpGet("https://api.spotify.com/v1/search?q="
		                + URLEncoder.encode(songName, StandardCharsets.UTF_8) + "&type=track");

		        searchRequest.setHeader("Authorization", "Bearer " + accessToken);

		        // Enviar solicitud y obtener respuesta
		        HttpResponse searchResponse = client.execute(searchRequest);
		        String searchJson = EntityUtils.toString(searchResponse.getEntity());

		        // Parsear respuesta JSON y obtener canciones
		        JsonObject searchObject = JsonParser.parseString(searchJson).getAsJsonObject();
		        JsonArray items = searchObject.getAsJsonObject("tracks").getAsJsonArray("items");

		        if (items.size() > 0) {
		            JsonObject item = items.get(0).getAsJsonObject();
		            String id = item.get("id").getAsString();
		            String nombre = item.get("name").getAsString();

		            // Obtener nombres de artistas
		            JsonArray artists = item.getAsJsonArray("artists");
		            List<String> nombresArtistas = new ArrayList<>();
		            for (JsonElement artistElem : artists) {
		                JsonObject artistObj = artistElem.getAsJsonObject();
		                nombresArtistas.add(artistObj.get("name").getAsString());
		            }

		            // Crear y agregar objeto Cancion a la lista
		             cancion = new Cancion(id, nombre, nombresArtistas); // Asume que Cancion tiene un constructor adecuado
		           
		        }
		    } catch (Exception e) {
		    	LOGGER.error("Error en la busqueda de cancion por nombre " + e.getMessage());
		    }

		    return cancion;
		}
}
