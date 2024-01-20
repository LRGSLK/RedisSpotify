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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class APISpotify {

	private static final String clientId = "543191e2fffa48ba958c42ea57c49ec0";
	private static final String clientSecret = "a4a4d78e02f04bf68f301a9ae1f627bb";


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


	static Artista seachArtistByName(String accessToken, String nombreArtista) {
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
	        e.printStackTrace();
	    }

	    return artista;
	}

	static Playlist searchPlaylistsByName(String accessToken, String playlistName) {
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
	        e.printStackTrace();
	    }

	    return playlist;
	}
	 public static Album searchAlbumById(String accessToken, String albumId) {
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
	            e.printStackTrace();
	        }

	        return album;
	    }
	 public static Album searchAlbumByName(String accessToken, String albumName) {
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
		            return searchAlbumById(accessToken, id);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return album;
		}
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
		        e.printStackTrace();
		    }

		    return cancion;
		}
}
