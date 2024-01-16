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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class APISpotify {

	private static final String clientId = "543191e2fffa48ba958c42ea57c49ec0";
	private static final String clientSecret = "a4a4d78e02f04bf68f301a9ae1f627bb";

	/**
	 * Obtiene un token de acceso para realizar solicitudes a la API de Spotify.
	 *
	 * @return Token de acceso
	 * @throws Exception Si hay un error durante la solicitud del token.
	 */
	static String getAccessToken() throws Exception {
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
	 * Obtiene información sobre un artista específico en base a su nombre.
	 *
	 * @param accessToken   Token de acceso para autenticar la solicitud.
	 * @param nombreArtista Nombre del artista a buscar.
	 * @return Lista de objetos Artista con información sobre el artista encontrado.
	 */
	static List<Artista> getArtistInfo(String accessToken, String nombreArtista) {
	    List<Artista> artistas = new ArrayList<>();
	    try {
	        HttpClient client = HttpClients.createDefault();
	        HttpGet request = new HttpGet("https://api.spotify.com/v1/search?q="
	                + URLEncoder.encode(nombreArtista, StandardCharsets.UTF_8) + "&type=artist");

	        request.setHeader("Authorization", "Bearer " + accessToken);

	        HttpResponse response = client.execute(request);
	        String json = EntityUtils.toString(response.getEntity());
	        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
	        JsonArray artists = jsonObject.getAsJsonObject("artists").getAsJsonArray("items");

	        if (artists != null) {
	            for (int i = 0; i < 4; i++) {
	                JsonObject artist = artists.get(i).getAsJsonObject();
	                String id = artist.get("id").getAsString();
	                String nombre = artist.get("name").getAsString();
	                // Corrección en la obtención del género
	                JsonArray genresArray = artist.getAsJsonArray("genres");
	                String genero = (genresArray != null && genresArray.size() > 0) ? genresArray.get(0).getAsString() : "Unknown";
	                int popularidad = artist.get("popularity").getAsInt();
	                int seguidores = artist.getAsJsonObject("followers").get("total").getAsInt();

	                artistas.add(new Artista(id, nombre, genero, popularidad, seguidores));
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return artistas;
	}

	/**
	 * Obtiene información detallada sobre un artista específico en base a su ID.
	 *
	 * @param artistId ID del artista para obtener información.
	 * @return Información detallada del artista en formato JSON.
	 * @throws Exception Si hay un error durante la solicitud de información del
	 *                   artista.
	 */
	static String getArtistInfo(String artistId) throws Exception {
		String accessToken = getAccessToken();
		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("https://api.spotify.com/v1/artists/" + artistId);
		request.setHeader("Authorization", "Bearer " + accessToken);

		HttpResponse response = client.execute(request);
		return EntityUtils.toString(response.getEntity());
	}
}
