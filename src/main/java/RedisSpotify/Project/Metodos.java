package RedisSpotify.Project;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Clase que contiene métodos para gestionar la base de datos RedisSpotify.
 */
public class Metodos {
	/**
     * Método principal que sirve como punto de entrada para ejecutar las operaciones
     * de gestión de la base de datos.
     *
     * @param args los argumentos de la línea de comandos (no se utiliza en este caso)
     */

	/**
     * Método protegido para la creación de una base de datos en un espacio específico.
     */
	private static final Logger LOGGER = LoggerFactory.getLogger(Metodos.class);

	protected static Jedis creacionBD(int SelectDB) {
	  
	    Jedis jedis=new Jedis("localhost",6379,SelectDB);
	    try {
	        jedis.select(SelectDB);
	        System.out.println("Base de datos seleccionada con exito en el entorno " + SelectDB);
	    } catch (Exception e) {
	        LOGGER.error("Error en la seleccion  de la base de datos: " + e.getMessage());
	    }
		return jedis;
	}
	/**
     * Método protegido para la inserción de datos en la base de datos.
     */
	protected static void insertar(Jedis jedis) {
		Scanner sc = new Scanner(System.in);
		String accessToken = null;
		try {
			accessToken = APISpotify.obtenerAcceso();
			String opcion;	
		do {
	        System.out.println("Ingresa el nombre de la clase para saber en qué tabla insertar:");
	        System.out.println("#Tienes las tablas Artista, Cancion, Album y Playlist");
	        opcion = sc.nextLine().toLowerCase(); 
	        if (opcion.contains("artista")) {
	            System.out.println("Has seleccionado Artista.");
	            Artista lista = null;
	            try {
					System.out.println("Introduce el nombre del artista que desea insertar:");
					String nombre = sc.nextLine();
					lista = APISpotify.buscarArtistaPorNombre(accessToken, nombre);
					System.out.println(lista);
					RedisManager.guardarArtistaRedis(jedis, lista);
				} catch (Exception e) {
				    LOGGER.error("Error al buscar o guardar el Artista en Redis. Asegúrese de que el nombre de la playlist es correcto y que el servicio de Redis está funcionando correctamente."+e.getMessage());


				}
	        } else if (opcion.contains("cancion")) {
	            System.out.println("Has seleccionado Canción.");
	            Cancion lista = null;
	            try {
					System.out.println("Introduce el nombre de la cancion que desea insertar:");
					String nombre = sc.nextLine();
					lista = APISpotify.searchSongsByName(accessToken, nombre);
					System.out.println(lista);
					RedisManager.guardarCancionRedis(jedis, lista);
				} catch (Exception e) {
				    LOGGER.error("Error al buscar o guardar La cancion en Redis. Asegúrese de que el nombre de la playlist es correcto y que el servicio de Redis está funcionando correctamente."+e.getMessage());

				}
	        } else if (opcion.contains("album")) {
	            System.out.println("Has seleccionado Album.");
	            Album lista = null;
	            try {
					System.out.println("Introduce el nombre del album que desea insertar:");
					String nombre = sc.nextLine();
					lista = APISpotify.buscarAlbumPorNombre(accessToken, nombre);
					System.out.println(lista);
					RedisManager.guardarAlbumRedis(jedis, lista);
				} catch (Exception e) {
				    LOGGER.error("Error al buscar o guardar el album en Redis. Asegúrese de que el nombre de la playlist es correcto y que el servicio de Redis está funcionando correctamente."+e.getMessage());
				}
	        } else if (opcion.contains("playlist")) {
	            System.out.println("Has seleccionado Playlist.");
	            Playlist lista = null;
	            try {
					System.out.println("Introduce el nombre de la playlist que desea insertar:");
					String nombre = sc.nextLine();
					lista = APISpotify.buscarPlayListPorNombre(accessToken, nombre);
					System.out.println(lista);
					RedisManager.guardarPlaylistRedis(jedis, lista);
				} catch (Exception e) {
				    LOGGER.error("Error al buscar o guardar la Playlist en Redis. Asegúrese de que el nombre de la playlist es correcto y que el servicio de Redis está funcionando correctamente."+e.getMessage());

				}
	        } else {
	            System.out.println("Opción no válida. Introduzca una opción valida.");
	        }
	    } while (!opcion.contains("artista") && !opcion.contains("cancion") && !opcion.contains("album") && !opcion.contains("playlist"));
	} catch (Exception e) {
	    LOGGER.error("Error al obtener el acceso de la API"+e.getMessage());

	}   
}	
	 /**
     * Método protegido para la modificación de datos en la base de datos.
     */
	protected static void modificar(Jedis jedis) {
		Scanner sc = new Scanner(System.in);
		try {
	
			String opcion;	
		do {
	        System.out.println("Ingresa el nombre de la clase para saber en qué tabla modificar:");
	        System.out.println("#Tienes las tablas Artista, Cancion, Album y Playlist");
	        opcion = sc.nextLine().toLowerCase(); 
	        String nombre;
	        if (opcion.contains("artista")) {
	            System.out.println("Has seleccionado Artista.");
	            try {
					System.out.println("Introduce el nombre del artista que desea modificar:");
					String nombreArtista = sc.nextLine();
					RedisManager.modificarArtista(jedis, nombreArtista);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de modificación de artista Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        }else if (opcion.contains("cancion")) {
	            System.out.println("Has seleccionado Cancion.");
	            try {
					System.out.println("Introduce el nombre de la cancion que desea modificar:");
					String nombreArtista = sc.nextLine();
					RedisManager.modificarCancion(jedis, nombreArtista);		
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de modificación de cancion. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else if (opcion.contains("album")) {
	            System.out.println("Has seleccionado Album.");
	            try {
					System.out.println("Introduce el nombre del album que desea modificar:");
					String nombreArtista = sc.nextLine();
					RedisManager.modificarAlbum(jedis, nombreArtista);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de modificación de album. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else if (opcion.contains("playlist")) {
	            System.out.println("Has seleccionado Playlist.");
	            try {
					System.out.println("Introduce el nombre de la Playlist que desea modificar:");
					String nombreArtista = sc.nextLine();
					RedisManager.modificarPlaylist(jedis, nombreArtista);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de modificación de playlist. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else {
	            System.out.println("Opción no válida. Introduzca una opción valida.");
	        }
	    } while (!opcion.contains("artista") && !opcion.contains("cancion") && !opcion.contains("album") && !opcion.contains("playlist"));
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error inesperado durante el proceso de modificación. Por favor, revise la entrada proporcionada y asegúrese de que el sistema de base de datos Redis está operativo."+e.getMessage());
		}   
	}
	/**
     * Método protegido para el borrado de datos en la base de datos.
     */
	protected static void borrar(Jedis jedis) {
		Scanner sc = new Scanner(System.in);
	
		try {

			String opcion;	
		do {
	        System.out.println("Ingresa el nombre de la clase para saber en qué tabla insertar:");
	        System.out.println("#Tienes las tablas Artista, Cancion, Album y Playlist");
	        opcion = sc.nextLine().toLowerCase(); 
	        if (opcion.contains("artista")) {
	            System.out.println("Has seleccionado Artista.");
	            try {
					System.out.println("Introduce el nombre del artista a borrar:");
					String nombre = sc.nextLine();
					RedisManager.borrarArtista(jedis, nombre);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de borrado de artista. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        }else if (opcion.contains("cancion")) {
	            System.out.println("Has seleccionado Canción.");
	            try {
					System.out.println("Introduce el nombre de la cancion a borrar:");
					String nombre = sc.nextLine();
					RedisManager.borrarCancion(jedis, nombre);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de borrado de cancion. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else if (opcion.contains("album")) {
	            System.out.println("Has seleccionado Album.");
	            try {
					System.out.println("Introduce el nombre del album a borrar:");
					String nombre = sc.nextLine();
					RedisManager.borrarAlbum(jedis, nombre);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de borrado de album. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else if (opcion.contains("playlist")) {
	            System.out.println("Has seleccionado Playlist.");
	            try {
					System.out.println("Introduce el nombre de la playlist a borrar:");
					String nombre = sc.nextLine();
					RedisManager.borrarPlaylist(jedis, nombre);
				} catch (Exception e) {
					LOGGER.error("Se ha producido un error durante el proceso de borrado de playlist. Por favor, revise la entrada proporcionada y asegurate que  se encuentra en la BD" +e.getMessage());
				}
	        } else {
	            System.out.println("Opción no válida. Introduzca una opción valida.");
	        }
	    } while (!opcion.contains("artista") && !opcion.contains("cancion") && !opcion.contains("album") && !opcion.contains("playlist"));
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error inesperado durante el proceso de error. Por favor, revise la entrada proporcionada y asegúrese de que el sistema de base de datos Redis está operativo."+e.getMessage());
	}   
	}
	/**
     * Método protegido para realizar consultas en la base de datos.
     */
	protected static void consulta(Jedis jedis) {
		Scanner sc = new Scanner(System.in);
	
		int op;
		try {
	
			String opcion;	
			do {
		        System.out.println("¿Que clase desea consultar?");
		        System.out.println("#Tienes las tablas Artista, Cancion, Album y Playlist");
		        opcion = sc.nextLine().toLowerCase(); 
		        String nombre;
		        if (opcion.contains("artista")) {
		            System.out.println("Has seleccionado Artista.");
		            System.out.println("Que te gustaria buscar?\n#Tenemos:\n1-Mostrar todos\n2-Busqueda por nombre\n3-Busqueda por genero\n4-Busqueda por popularidad\n5-Busqueda por seguidores");
		            op =sc.nextInt();
		            RedisManager.consultaArtista(jedis,op);
		        } else if (opcion.contains("cancion")) {
		            System.out.println("Has seleccionado Canción.");
		            System.out.println("Que te gustaria buscar?\n#Tenemos:\n1-Mostrar todos\n2-Busqueda por nombre del artista\n3-Busqueda por nombre de la cancion");
		            op =sc.nextInt();
		            RedisManager.consultaCancion(jedis,op);
		        } else if (opcion.contains("album")) {
		            System.out.println("Has seleccionado Album.");
		            System.out.println("Que te gustaria buscar?\n#Tenemos:\n1-Mostrar todos\n2-Busqueda por nombre");
		            op =sc.nextInt();
		            RedisManager.consultaAlbum(jedis,op);
		        } else if (opcion.contains("playlist")) {
		            System.out.println("Has seleccionado Playlist.");
		            System.out.println("Que te gustaria buscar?\n#Tenemos:\n1-Mostrar todos\n2-Busqueda por nombre");
		            op =sc.nextInt();
		            RedisManager.consultaPlaylist(jedis,op);
		        } else {
		            System.out.println("Opción no válida. Introduzca una opción valida.");
		        }
		    } while (!opcion.contains("artista") && !opcion.contains("cancion") && !opcion.contains("album") && !opcion.contains("playlist"));
		} catch (Exception e) {
			LOGGER.error("Se ha producido un error inesperado durante el proceso de consulta. Por favor, revise la entrada proporcionada y asegúrese de que el sistema de base de datos Redis está operativo."+e.getMessage());
	}   
}	
	/**
     * Método protegido para borrar toda la base de datos.
     */
	protected static void borrarBD(Jedis jedis) {

	    Scanner sc = new Scanner(System.in);
	    String opcion;
	    System.out.println("¿Desea borrar la BD?");
	    opcion= sc.next();
	    if(opcion.contains("si")){
	    	System.out.println("¿Esta seguro?");
	    	opcion=sc.next();
	    	if(opcion.contains("si")){
	    		System.out.println("Pienseselo, es la última oportunidad");
	    		opcion=sc.next();
	    		if(opcion.contains("si")){
	    			System.out.println("Bueno, se borrará la base de datos.");
	    			jedis.flushDB();
	    			System.out.println("Borrado exitoso");
	    		}else if(opcion.contains("no")){
			    	System.out.println("Uff, suerte que se lo ha pensado, nos iba a costar millones a la empresa.");
			    }else System.out.println("Como no entendi lo que escribío entenderé que no quiere borrar la BD."); 	 
	    	}else if(opcion.contains("no")){
		    	System.out.println("Uff, suerte que se lo ha pensado, nos iba a costar millones a la empresa.");
		    }else System.out.println("Como no entendi lo que escribío entenderé que no quiere borrar la BD."); 	 	
	    }else if(opcion.contains("no")){
	    	System.out.println("Uff, suerte que se lo ha pensado, nos iba a costar millones a la empresa.");
	    }else System.out.println("Como no entendi lo que escribío entenderé que no quiere borrar la BD."); 	
	}
}

