/**
 * La clase {@code RedisManager} proporciona métodos para interactuar con la base de datos Redis.
 * Contiene operaciones para guardar, modificar, y borrar artistas, canciones, álbumes y playlists en Redis.
 * Además, incluye consultas específicas para obtener información de artistas, canciones, álbumes y playlists.
 */
package RedisSpotify.Project;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
/**
 * Clase que gestiona las operaciones en la base de datos Redis para artistas, canciones, álbumes y playlists.
 */
public class RedisManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);
	/**
     * Método principal de la clase (main), que no se utiliza en este contexto.
     *
     * @param args Los argumentos de la línea de comandos (no se utilizan en este programa).
     */

	
	//--------------------------------------------ARTISTAS
    /**
     * Guarda un objeto Artista en Redis.
     *
     * @param jedis   Cliente Jedis para la conexión a Redis.
     * @param artista Objeto Artista que se va a guardar en Redis.
     */
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
		        LOGGER.info("Artista guardado satisfactoriamente ");
		    } catch (Exception e) {
		        t.discard();
		        LOGGER.error("Error al guardar el artista "+e.getMessage());
		    }
		}
	/**
     * Modifica un artista existente en Redis.
     *
     * @param jedis         Cliente Jedis para la conexión a Redis.
     * @param nombreArtista Nombre del artista que se va a modificar.
     */
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
	            LOGGER.info("Artista modificado exitosamente.");
	            Artista artistaModificado = new Artista(
	                artistData.get("id"),
	                artistData.get("nombre"),
	                artistData.get("genero"),
	                Integer.parseInt(artistData.get("popularidad")),
	                Integer.parseInt(artistData.get("seguidores"))
	            );
	            LOGGER.info("Artista modificado: ");
	            System.out.println(artistaModificado);
	        } catch (Exception e) {
	            t.discard();
	            e.printStackTrace();
	            LOGGER.error("Error al modificar el artista en Redis.");
	        }
	    } else {
	        LOGGER.warn("Artista no encontrado en Redis.");
	    }
	}
	/**
     * Busca un artista por su nombre en Redis.
     *
     * @param jedis         Cliente Jedis para la conexión a Redis.
     * @param nombreArtista Nombre del artista a buscar.
     * @return Clave del artista si se encuentra, o null si no se encuentra.
     */
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
	/**
     * Elimina un artista de Redis.
     *
     * @param jedis         Cliente Jedis para la conexión a Redis.
     * @param nombreArtista Nombre del artista que se va a eliminar.
     */
	protected static void borrarArtista(Jedis jedis, String nombreArtista) {
	    String artistKey = buscarArtistaPorNombre(jedis, nombreArtista);
	    if (artistKey != null) {
	        Transaction t = jedis.multi();
	        try {
	            t.del(artistKey);
	            String indexKey = "indices:artistas";
	            t.srem(indexKey, artistKey);
	            t.exec();
	            LOGGER.info("Artista eliminado exitosamente.");
	        } catch (Exception e) {
	            t.discard();
	            LOGGER.error("Error al eliminar el artista de Redis."+e.getMessage());
	        }
	    } else {
	        LOGGER.warn("Artista no encontrado en Redis.");
	    }
	}
	//--------------------------------------------CANCIONES
	/**
     * Guarda un objeto Cancion en Redis.
     *
     * @param jedis   Cliente Jedis para la conexión a Redis.
     * @param cancion Objeto Cancion que se va a guardar en Redis.
     */
	protected static void guardarCancionRedis(Jedis jedis, Cancion cancion) {
	    String cancionKey = "cancion:" + cancion.getId();
	    Map<String, String> cancionData = new HashMap<>();
	    cancionData.put("nombre", cancion.getNombre());
	    String artistas = String.join(",", cancion.getArtistas());
	    cancionData.put("artistas", artistas);
	    String indexKey = "indices:canciones";
	    Transaction t = jedis.multi();
	    try {
	        t.hmset(cancionKey, cancionData);
	        t.sadd(indexKey, cancionKey);
	        t.exec();
	        LOGGER.info("Cancion guardada satisfactoriamente");
	    } catch (Exception e) {
	        t.discard();
	        LOGGER.error("Error al guardar la cancion "+e.getMessage());
	    }
	}
	/**
     * Modifica una canción existente en Redis.
     *
     * @param jedis          Cliente Jedis para la conexión a Redis.
     * @param nombreCancion  Nombre de la canción que se va a modificar.
     */
	protected static void modificarCancion(Jedis jedis, String nombreCancion) {
        String cancionKey = buscarCancionPorNombre(jedis, nombreCancion);

        if (cancionKey != null) {
            Map<String, String> cancionData = jedis.hgetAll(cancionKey);
            Scanner sc = new Scanner(System.in);
            System.out.println("Introduce el nuevo NOMBRE de la canción:");
            String nombre = sc.nextLine();
            System.out.println("Introduce el nuevo ARTISTA:");
            String artista = sc.nextLine();
            Transaction t = jedis.multi();
            try {
                cancionData.put("nombre", nombre);
                cancionData.put("artistas", artista);
                t.hmset(cancionKey, cancionData);
                t.exec();
                LOGGER.info("Canción modificada exitosamente.");
                System.out.println(cancionData);
            } catch (Exception e) {
                t.discard();
                e.printStackTrace();
                LOGGER.error("Error al modificar la canción en Redis.");
            }
        } else {
        	LOGGER.warn("Canción no encontrada en Redis.");
        }
    }
	/**
     * Busca una canción por su nombre en Redis.
     *
     * @param jedis          Cliente Jedis para la conexión a Redis.
     * @param nombreCancion  Nombre de la canción a buscar.
     * @return Clave de la canción si se encuentra, o null si no se encuentra.
     */
    private static String buscarCancionPorNombre(Jedis jedis, String nombreCancion) {
        String indexKey = "indices:canciones";
        Set<String> cancionesKeys = jedis.smembers(indexKey);

        for (String cancionKey : cancionesKeys) {
            Map<String, String> cancionData = jedis.hgetAll(cancionKey);
            if (cancionData.containsKey("nombre") && cancionData.get("nombre").equalsIgnoreCase(nombreCancion)) {
                return cancionKey;
            }
        }
        return null;
    }
    /**
     * Elimina una canción de Redis.
     *
     * @param jedis          Cliente Jedis para la conexión a Redis.
     * @param nombreCancion  Nombre de la canción que se va a eliminar.
     */
    protected static void borrarCancion(Jedis jedis, String nombreCancion) {
        String cancionKey = buscarCancionPorNombre(jedis, nombreCancion);
        if (cancionKey != null) {
            Transaction t = jedis.multi();
            try {
                t.del(cancionKey);
                String indexKey = "indices:canciones";
                t.srem(indexKey, cancionKey);
                t.exec();
                LOGGER.info("Canción eliminada exitosamente.");
            } catch (Exception e) {
                t.discard();
                e.printStackTrace();
                LOGGER.error("Error al eliminar la canción de Redis.");
            }
        } else {
        	LOGGER.warn("Canción no encontrada en Redis.");
        }
    }
	//--------------------------------------------ALBUM
    /**
     * Guarda un objeto Album en Redis.
     *
     * @param jedis Cliente Jedis para la conexión a Redis.
     * @param album Objeto Album que se va a guardar en Redis.
     */
    protected static void guardarAlbumRedis(Jedis jedis, Album album) {
        String albumKey = "album:" + album.getID();
        Map<String, String> albumData = new HashMap<>();
        albumData.put("nombre", album.getNombre());
        String artistas = String.join(",", album.getArtistas());
        albumData.put("artistas", artistas);
        albumData.put("fechaLanzamiento", album.getFechaLanzamiento());
        List<String> cancionesIds = album.getCanciones().stream().map(Cancion::getId).collect(Collectors.toList());
        String cancionesStr = String.join(",", cancionesIds);
        albumData.put("canciones", cancionesStr);

        String indexKey = "indices:albumes";
        Transaction t = jedis.multi();
        try {
            t.hmset(albumKey, albumData);
            t.sadd(indexKey, albumKey);
            for (Cancion cancion : album.getCanciones()) {
                Map<String, String> cancionData = new HashMap<>();
                cancionData.put("nombre", cancion.getNombre());
                String cancionArtistas = String.join(",", cancion.getArtistas());
                cancionData.put("artistas", cancionArtistas);
                t.hmset("cancion:" + cancion.getId(), cancionData);
            }
            t.exec();
            LOGGER.info("Album guardado satisfactoriamente");
        } catch (Exception e) {
            t.discard();
            LOGGER.error("Error al guardar el Album. "+e.getMessage());
        }
    }
    /**
     * Modifica un álbum existente en Redis.
     *
     * @param jedis        Cliente Jedis para la conexión a Redis.
     * @param nombreAlbum  Nombre del álbum que se va a modificar.
     */
	protected static void modificarAlbum(Jedis jedis, String nombreAlbum) {
        String albumKey = buscarAlbumPorNombre(jedis, nombreAlbum);

        if (albumKey != null) {
            Map<String, String> albumData = jedis.hgetAll(albumKey);
            Scanner sc = new Scanner(System.in);
            System.out.println("Introduce el nuevo NOMBRE del álbum:");
            String nombre = sc.nextLine();  
            System.out.println("Introduce la nueva FECHA DE LANZAMEINTO del álbum:");
            String fecha = sc.nextLine();
            Transaction t = jedis.multi();
            try {
                albumData.put("nombre", nombre);
                albumData.put("fechaLanzamiento", fecha);
                t.hmset(albumKey, albumData);
                t.exec();
                LOGGER.info("Álbum modificado exitosamente.");
                System.out.println("Álbum modificado: ");
                System.out.println(albumData);
            } catch (Exception e) {
                t.discard();
            
                LOGGER.error("Error al modificar el álbum en Redis."+e.getMessage());
            }
        } else {
            LOGGER.warn("Álbum no encontrado en Redis.");
        }
    }
	/**
     * Busca un álbum por su nombre en Redis.
     *
     * @param jedis        Cliente Jedis para la conexión a Redis.
     * @param nombreAlbum  Nombre del álbum a buscar.
     * @return Clave del álbum si se encuentra, o null si no se encuentra.
     */
    private static String buscarAlbumPorNombre(Jedis jedis, String nombreAlbum) {
        String indexKey = "indices:albumes";
        Set<String> albumesKeys = jedis.smembers(indexKey);

        for (String albumKey : albumesKeys) {
            Map<String, String> albumData = jedis.hgetAll(albumKey);
            if (albumData.containsKey("nombre") && albumData.get("nombre").equalsIgnoreCase(nombreAlbum)) {
                return albumKey;
            }
        }
        return null;
    }
    /**
     * Elimina un álbum de Redis.
     *
     * @param jedis        Cliente Jedis para la conexión a Redis.
     * @param nombreAlbum  Nombre del álbum que se va a eliminar.
     */
    protected static void borrarAlbum(Jedis jedis, String nombreAlbum) {
        String albumKey = buscarAlbumPorNombre(jedis, nombreAlbum);
        if (albumKey != null) {
            Transaction t = jedis.multi();
            try {
                t.del(albumKey);
                String indexKey = "indices:albumes";
                t.srem(indexKey, albumKey);
                t.exec();
                LOGGER.info("Álbum eliminado exitosamente.");
            } catch (Exception e) {
                t.discard();
                e.printStackTrace();
                LOGGER.error("Error al eliminar el álbum de Redis.");
            }
        } else {
        	LOGGER.warn("Álbum no encontrado en Redis.");
        }
    }
	//--------------------------------------------PLAYLIST
    /**
     * Guarda un objeto Playlist en Redis.
     *
     * @param jedis    Cliente Jedis para la conexión a Redis.
     * @param playlist Objeto Playlist que se va a guardar en Redis.
     */
	protected static void guardarPlaylistRedis(Jedis jedis, Playlist playlist) {
	    String playlistKey = "playlist:" + playlist.getId();
	    Map<String, String> playlistData = new HashMap<>();
	    playlistData.put("nombre", playlist.getNombre());
	    List<Cancion> canciones = playlist.getCanciones();
	    List<String> cancionesIds = canciones.stream().map(Cancion::getNombre).collect(Collectors.toList());
	    String cancionesStr = String.join(",", cancionesIds);
	    playlistData.put("canciones", cancionesStr);
	    String indexKey = "indices:playlists";
	    Transaction t = jedis.multi();
	    try {
	        t.hmset(playlistKey, playlistData);
	        t.sadd(indexKey, playlistKey);
	        t.exec();
	        LOGGER.info("Playlist guardada satisfactoriamente");
	    } catch (Exception e) {
	        t.discard();
	        LOGGER.error("Error al guardar la playlist");

	    }
	}
	/**
     * Modifica una playlist existente en Redis.
     *
     * @param jedis           Cliente Jedis para la conexión a Redis.
     * @param nombrePlaylist  Nombre de la playlist que se va a modificar.
     */
	protected static void modificarPlaylist(Jedis jedis, String nombrePlaylist) {
        String playlistKey = buscarPlaylistPorNombre(jedis, nombrePlaylist);

        if (playlistKey != null) {
            Map<String, String> playlistData = jedis.hgetAll(playlistKey);

            Scanner sc = new Scanner(System.in);

            System.out.println("Introduce el nuevo NOMBRE de la playlist:");
            String nombre = sc.nextLine();
            Transaction t = jedis.multi();
            try {
                playlistData.put("nombre", nombre);
                t.hmset(playlistKey, playlistData);
                t.exec();
                LOGGER.info("Playlist modificada exitosamente.");
                System.out.println("Playlist modificada: ");
                System.out.println(playlistData);
            } catch (Exception e) {
                t.discard();
                e.printStackTrace();
                LOGGER.error("Error al modificar la playlist en Redis.");
            }
        } else {
        	LOGGER.warn("Playlist no encontrada en Redis.");
        }
    }
	 /**
     * Busca una playlist por su nombre en Redis.
     *
     * @param jedis           Cliente Jedis para la conexión a Redis.
     * @param nombrePlaylist  Nombre de la playlist a buscar.
     * @return Clave de la playlist si se encuentra, o null si no se encuentra.
     */
    private static String buscarPlaylistPorNombre(Jedis jedis, String nombrePlaylist) {
        String indexKey = "indices:playlists";
        Set<String> playlistsKeys = jedis.smembers(indexKey);

        for (String playlistKey : playlistsKeys) {
            Map<String, String> playlistData = jedis.hgetAll(playlistKey);
            if (playlistData.containsKey("nombre") && playlistData.get("nombre").equalsIgnoreCase(nombrePlaylist)) {
                return playlistKey;
            }
        }
        return null;
    }
    /**
     * Elimina una playlist de Redis.
     *
     * @param jedis           Cliente Jedis para la conexión a Redis.
     * @param nombrePlaylist  Nombre de la playlist que se va a eliminar.
     */
    protected static void borrarPlaylist(Jedis jedis, String nombrePlaylist) {
        String playlistKey = buscarPlaylistPorNombre(jedis, nombrePlaylist);
        if (playlistKey != null) {
            Transaction t = jedis.multi();
            try {
                t.del(playlistKey);
                String indexKey = "indices:playlists";
                t.srem(indexKey, playlistKey);
                t.exec();
                LOGGER.info("Playlist eliminada exitosamente.");
            } catch (Exception e) {
                t.discard();
                e.printStackTrace();
                LOGGER.error("Error al eliminar la playlist de Redis.");
            }
        } else {
        	LOGGER.warn("Playlist no encontrada en Redis.");
        }
    }
    //-------------------------------------------CONSULTAS-ARTISTAS
    /**
     * Realiza una consulta de artistas en Redis según la opción especificada.
     *
     * @param jedis  Cliente Jedis para la conexión a Redis.
     * @param opcion Opción de consulta de artistas.
     */
    protected static void consultaArtista(Jedis jedis, int opcion) {
        Scanner sc = new Scanner(System.in);
        String eleccion;
        Set<String> artistKeys = jedis.smembers("indices:artistas");

        switch (opcion) {
            case 1:
                artistKeys.forEach(artistKey -> {
                    Map<String, String> artistData = jedis.hgetAll(artistKey);
                    System.out.println(new Artista(artistKey, artistData.get("nombre"), artistData.get("genero"),
                            Integer.parseInt(artistData.get("popularidad")), 
                            Integer.parseInt(artistData.get("seguidores"))));
                });
                break;
            case 2:
                System.out.println("Escribe el nombre del artista:");
                String nombre = sc.nextLine();
                buscarYMostrarArtistaPorAtributo(jedis, artistKeys, "nombre", nombre);
                break;
            case 3:
                System.out.println("Escribe el género del artista:");
                String genero = sc.nextLine();
                buscarYMostrarArtistaPorAtributo(jedis, artistKeys, "genero", genero);
                break;
            case 4:
                System.out.println("Escribe la popularidad del artista:");
                int popularidad = sc.nextInt();
                buscarYMostrarArtistaPorAtributoNumerico(jedis, artistKeys, "popularidad", popularidad);
                break;
            case 5:
                System.out.println("Escribe el número de seguidores del artista:");
                int seguidores = sc.nextInt();
                buscarYMostrarArtistaPorAtributoNumerico(jedis, artistKeys, "seguidores", seguidores);
                break;
            default:
                System.out.println("Opción no válida.");
                break;
        }
    }
    /**
     * Busca y muestra artistas según un atributo y valor dados en Redis.
     *
     * @param jedis     Cliente Jedis para la conexión a Redis.
     * @param artistKeys Conjunto de claves de artistas en Redis.
     * @param atributo   Atributo por el cual realizar la búsqueda.
     * @param valor      Valor del atributo por el cual realizar la búsqueda.
     */
    private static void buscarYMostrarArtistaPorAtributo(Jedis jedis, Set<String> artistKeys, String atributo, String valor) {
        artistKeys.stream()
                // Filtra primero por claves que potencialmente contienen el atributo deseado para optimizar
                .filter(key -> jedis.hget(key, atributo) != null && valor.equalsIgnoreCase(jedis.hget(key, atributo)))
                .forEach(key -> {
                    Map<String, String> artistData = jedis.hgetAll(key); // Obtén todos los datos de un artista específico

                    // Extrae el ID del artista desde la clave (asumiendo que la clave sigue el formato "artista:id")
                    String id = key.split(":")[1]; // Ajusta este índice si el formato de la clave es diferente

                    // Crea y muestra el objeto Artista usando el ID extraído y los datos recuperados
                    System.out.println(new Artista(
                            id,
                            artistData.get("nombre"),
                            artistData.get("genero"),
                            Integer.parseInt(artistData.get("popularidad")),
                            Integer.parseInt(artistData.get("seguidores"))
                    ));
                });
    }

    /**
     * Busca y muestra artistas según un atributo numérico y valor dados en Redis.
     *
     * @param jedis      Cliente Jedis para la conexión a Redis.
     * @param artistKeys Conjunto de claves de artistas en Redis.
     * @param atributo    Atributo numérico por el cual realizar la búsqueda.
     * @param valor      Valor numérico del atributo por el cual realizar la búsqueda.
     */
    private static void buscarYMostrarArtistaPorAtributoNumerico(Jedis jedis, Set<String> artistKeys, String atributo, int valor) {
        artistKeys.stream()
                .map(jedis::hgetAll)
                .filter(artistData -> valor == Integer.parseInt(artistData.get(atributo)))
                .forEach(artistData -> System.out.println(new Artista(
                        artistData.get("id"),
                        artistData.get("nombre"),
                        artistData.get("genero"),
                        Integer.parseInt(artistData.get("popularidad")),
                        Integer.parseInt(artistData.get("seguidores"))
                )));
    }
    //-------------------------------------------CONSULTAS-CANCION
    /**
     * Realiza una consulta de canciones en Redis según la opción especificada.
     *
     * @param jedis Cliente Jedis para la conexión a Redis.
     * @param op    Opción de consulta de canciones.
     */
    protected static void consultaCancion(Jedis jedis, int op) {
        Scanner sc = new Scanner(System.in);
        Set<String> cancionKeys = jedis.smembers("indices:canciones");

        if (op == 1) {
            cancionKeys.forEach(cancionKey -> {
                Map<String, String> cancionData = jedis.hgetAll(cancionKey);
                System.out.println("Canción: " + cancionData.get("nombre") + ", Artistas: " + cancionData.get("artistas"));
            });
        } else if (op == 2) {
            System.out.println("Escribe el nombre del artista:");
            String nombreArtista = sc.nextLine();
            cancionKeys.stream()
                .map(jedis::hgetAll)
                .filter(cancionData -> Arrays.asList(cancionData.get("artistas").split(",")).contains(nombreArtista))
                .forEach(cancionData -> System.out.println("Canción: " + cancionData.get("nombre") + ", Artistas: " + cancionData.get("artistas")));
        } else if (op == 3) {
            System.out.println("Escribe el nombre de la canción:");
            String nombreCancion = sc.nextLine();
            cancionKeys.stream()
                .map(jedis::hgetAll)
                .filter(cancionData -> cancionData.get("nombre").equalsIgnoreCase(nombreCancion))
                .forEach(cancionData -> System.out.println("Canción: " + cancionData.get("nombre") + ", Artistas: " + cancionData.get("artistas")));
        }
    }
    //-------------------------------------------CONSULTAS-ALBUM
    /**
     * Realiza una consulta de álbumes en Redis según la opción especificada.
     *
     * @param jedis Cliente Jedis para la conexión a Redis.
     * @param op    Opción de consulta de álbumes.
     */
    protected static void consultaAlbum(Jedis jedis, int op) {
        Scanner sc = new Scanner(System.in);
        Set<String> albumKeys = jedis.smembers("indices:albumes");

        if (op == 1) {
            albumKeys.forEach(albumKey -> {
                Map<String, String> albumData = jedis.hgetAll(albumKey);
                System.out.println("Álbum: " + albumData.get("nombre") + ", Artistas: " + albumData.get("artistas") + ", Fecha de lanzamiento: " + albumData.get("fechaLanzamiento"));
                mostrarCancionesDelAlbum(jedis, albumData.get("canciones"));
            });
        } else if (op == 2) {
            System.out.println("Escribe el nombre del álbum:");
            String nombreAlbum = sc.nextLine();
            albumKeys.stream()
                .map(jedis::hgetAll)
                .filter(albumData -> albumData.get("nombre").equalsIgnoreCase(nombreAlbum))
                .forEach(albumData -> {
                    System.out.println("Álbum: " + albumData.get("nombre") + ", Artistas: " + albumData.get("artistas") + ", Fecha de lanzamiento: " + albumData.get("fechaLanzamiento"));
                    mostrarCancionesDelAlbum(jedis, albumData.get("canciones"));
                });
        }
    }
    /**
     * Realiza una consulta de album en Redis según la opción especificada.
     *
     * @param jedis Cliente Jedis para la conexión a Redis.
     * @param op    Opción de consulta de album.
     */
    private static void mostrarCancionesDelAlbum(Jedis jedis, String cancionesStr) {
        if (cancionesStr != null && !cancionesStr.isEmpty()) {
            List<String> cancionesIds = Arrays.asList(cancionesStr.split(","));
            System.out.println("Canciones del Álbum:");
            for (String cancionId : cancionesIds) {
                Map<String, String> cancionData = jedis.hgetAll("cancion:" + cancionId);
                System.out.println("- " + cancionData.get("nombre") + " (Artistas: " + cancionData.get("artistas") + ")");
            }
        }else System.out.println("Esta vacio");
    }

    //-------------------------------------------CONSULTAS-PLAYLIST
    /**
     * Realiza una consulta de playlists en Redis según la opción especificada.
     *
     * @param jedis Cliente Jedis para la conexión a Redis.
     * @param op    Opción de consulta de playlists.
     */
    protected static void consultaPlaylist(Jedis jedis, int op) {
        Scanner sc = new Scanner(System.in);
        Set<String> playlistKeys = jedis.smembers("indices:playlists");

        if (op == 1) {
            playlistKeys.forEach(playlistKey -> {
                Map<String, String> playlistData = jedis.hgetAll(playlistKey);
                System.out.println("Playlist: " + playlistData.get("nombre") + ", Canciones: " + playlistData.get("canciones"));
            });
        } else if (op == 2) {
            System.out.println("Escribe el nombre de la playlist:");
            String nombrePlaylist = sc.nextLine();
            playlistKeys.stream()
                .map(jedis::hgetAll)
                .filter(playlistData -> playlistData.get("nombre").equalsIgnoreCase(nombrePlaylist))
                .forEach(playlistData -> System.out.println("Playlist: " + playlistData.get("nombre") + ", Canciones: " + playlistData.get("canciones")));
        }
    }
}
