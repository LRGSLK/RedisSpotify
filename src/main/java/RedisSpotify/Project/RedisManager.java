package RedisSpotify.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class RedisManager {

public static void main(String[] args) {}
	
	//--------------------------------------------ARTISTAS
	protected static void guardarArtistaRedis(Jedis jedis, Artista artista) {
		 String artistKey = "artista:" + artista.getId();
		    Map<String, String> artistData = new HashMap<>();
		    artistData.put("nombre", artista.getNombre());
		    artistData.put("genero", artista.getGenero());
		    artistData.put("popularidad", String.valueOf(artista.getPopularidad()));
		    artistData.put("seguidores", String.valueOf(artista.getSeguidores()));

		    String indexKey = "indices:artistas";
		    Transaction t = jedis.multi();
		    try {
		        t.hmset(artistKey, artistData);
		        t.sadd(indexKey, artistKey);
		        t.exec();
		    } catch (Exception e) {
		        t.discard();
		        e.printStackTrace();
		    }
		}
	protected static void modificarArtista(Jedis jedis, String nombreArtista) {
	    Scanner sc= new Scanner(System.in);
		String artistKey = buscarArtistaPorNombre(jedis, nombreArtista);
	    if (artistKey != null) {
	        Map<String, String> artistData = jedis.hgetAll(artistKey);
	        
	        System.out.println("Introduce el nuevo NOMBRE ");
	        String nombre=sc.nextLine();
	        System.out.println("Introduce el nuevo GENERO");
	        String genero=sc.nextLine();
	        System.out.println("Introduce el nuevo POPULARIDAD");
	        int popularidad=sc.nextInt();
	        System.out.println("Introduce el nuevo SEGUIDORES");
	        int seguidores=sc.nextInt();
	        
	        artistData.put("popularidad", String.valueOf(popularidad));
	        artistData.put("genero",genero);
	        artistData.put("nombre",nombre);
	        artistData.put("seguidores", String.valueOf(seguidores));
	        Transaction t = jedis.multi();
	        try {
	            t.hmset(artistKey, artistData);
	            t.exec();
	            System.out.println("Artista modificado exitosamente.");
	            Artista artistaModificado = new Artista(
	                artistData.get("id"),
	                artistData.get("nombre"),
	                artistData.get("genero"),
	                Integer.parseInt(artistData.get("popularidad")),
	                Integer.parseInt(artistData.get("seguidores"))
	            );
	            System.out.println("Artista modificado: ");
	            System.out.println(artistaModificado);
	        } catch (Exception e) {
	            t.discard();
	            e.printStackTrace();
	            System.err.println("Error al modificar el artista en Redis.");
	        }
	    } else {
	        System.err.println("Artista no encontrado en Redis.");
	    }
	}
	private static String buscarArtistaPorNombre(Jedis jedis, String nombreArtista) {
	    String indexKey = "indices:artistas";
	    Set<String> artistKeys = jedis.smembers(indexKey);

	    for (String artistKey : artistKeys) {
	        Map<String, String> artistData = jedis.hgetAll(artistKey);
	        if (artistData.containsKey("nombre") && artistData.get("nombre").equalsIgnoreCase(nombreArtista)) {
	            return artistKey;
	        }
	    }
	    return null; 
	}
	
	protected static void borrarArtista(Jedis jedis, String nombreArtista) {
	    String artistKey = buscarArtistaPorNombre(jedis, nombreArtista);
	    if (artistKey != null) {
	        Transaction t = jedis.multi();
	        try {
	            t.del(artistKey);
	            String indexKey = "indices:artistas";
	            t.srem(indexKey, artistKey);
	            t.exec();
	            System.out.println("Artista eliminado exitosamente.");
	        } catch (Exception e) {
	            t.discard();
	            e.printStackTrace();
	            System.err.println("Error al eliminar el artista de Redis.");
	        }
	    } else {
	        System.err.println("Artista no encontrado en Redis.");
	    }
	}
	//--------------------------------------------CANCIONES
	protected static void guardarCancionRedis(Jedis jedis, Cancion cancion) {
	    String cancionKey = "cancion:" + cancion.getNombre();
	    Map<String, String> cancionData = new HashMap<>();
	    cancionData.put("nombre", cancion.getNombre());

	    // Convertir la lista de artistas en una cadena separada por comas
	    String artistas = String.join(",", cancion.getArtistas());
	    cancionData.put("artistas", artistas);

	    // Agregar otros campos específicos de canción según tus necesidades

	    String indexKey = "indices:canciones";
	    Transaction t = jedis.multi();
	    try {
	        t.hmset(cancionKey, cancionData);
	        t.sadd(indexKey, cancionKey);
	        t.exec();
	    } catch (Exception e) {
	        t.discard();
	        e.printStackTrace();
	    }
	}
	protected static void modificarCancion(Jedis jedis, String nombreCancion) {
	    Scanner sc = new Scanner(System.in);
	    String songKey = buscarCancionPorNombre(jedis, nombreCancion);
	    
	    if (songKey != null) {
	        Map<String, String> songData = jedis.hgetAll(songKey);
	        
	        System.out.println("Introduce el nuevo NOMBRE ");
	        String nombre = sc.nextLine();
	        // Puedes agregar más campos para modificar según sea necesario

	        // Modifica los campos de la canción
	        songData.put("nombre", nombre);
	        // Agrega más campos para modificar según sea necesario

	        Transaction t = jedis.multi();
	        try {
	            t.hmset(songKey, songData);
	            t.exec();
	            System.out.println("Canción modificada exitosamente.");
	            List<String> artistas = Arrays.asList(songData.get("artistas").split(","));

	            Cancion cancionModificada = new Cancion(
	                songData.get("id"),
	                songData.get("nombre"),
	                artistas
	            );
	            System.out.println("Canción modificada: ");
	            System.out.println(cancionModificada);
	        } catch (Exception e) {
	            t.discard();
	            e.printStackTrace();
	            System.err.println("Error al modificar la canción en Redis.");
	        }
	    } else {
	        System.err.println("Canción no encontrada en Redis.");
	    }
	}
	protected static String buscarCancionPorNombre(Jedis jedis, String nombreCancion) {
	    // Define la clave del índice de canciones por nombre
	    String indexKey = "indices:canciones:nombre";

	    // Busca la clave de la canción por su nombre en el índice
	    String songKey = jedis.hget(indexKey, nombreCancion);

	    // Retorna la clave de la canción (o null si no se encuentra)
	    return songKey;
	}
}
