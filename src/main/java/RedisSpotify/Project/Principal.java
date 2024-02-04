package RedisSpotify.Project;

import java.util.Scanner;

import redis.clients.jedis.Jedis;

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int num=1;
        int opcion;
        Jedis jedis =null;

        System.out.println("Bienvenido a la base de datos de Redis");
        System.out.println("Por favor, selecciona en qué base de datos quieres trabajar (0-15): ");
        int dbIndex;
        do {
            dbIndex = scanner.nextInt();
            if (dbIndex >= 0 && dbIndex <= 15) {
                jedis=Metodos.creacionBD(dbIndex);
                break;
            } else {
                System.out.println("Número inválido. Por favor, selecciona un número entre 0 y 15.");
            }
        } while (true);       
        do {
            System.out.println("Seleccione alguna de las opciones disponibles: ");
            System.out.println("1. Insertar un objeto.");
            System.out.println("2. Modificar un objeto.");
            System.out.println("3. Borrar un objeto.");
            System.out.println("4. Realizar una consulta.");
            System.out.println("5. Borrar la base de datos actual.");
            System.out.println("6. Salir");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    System.out.println("Has seleccionado la opción 1.");
                    Metodos.insertar(jedis);
                    break;
                case 2:
                    System.out.println("Has seleccionado la opción 2.");
                    Metodos.modificar(jedis);
                    break;
                case 3:
                    System.out.println("Has seleccionado la opción 3.");
                    Metodos.borrar(jedis);
                    break;
                case 4:
                    System.out.println("Has seleccionado la opción 4.");
                    Metodos.consulta(jedis);
                    break;
                case 5:
                    System.out.println("Has seleccionado la opción 5.");
                    Metodos.borrarBD(jedis);
                    break;
                case 6:
                    System.out.println("Saliendo del programa. ¡Hasta luego!");
                    jedis.close();
                    break;
                default:
                    System.out.println("Opción no valida. Por favor, selecciona una opción del 1 al 8.");
            }
        } while (opcion != 7);
        scanner.close();
    }
}
