package co.edu.javeriana.algoritmos.proyecto;

import java.util.List;

/**
 * Interfaz que el tablero que ustedes generan debe implementar.  
 * @author danilo
 * NO CAMBIE NADA EN ESTE ARCHIVO, NI LO MUEVA, NI LO COPIE.
 */
public interface Tablero 
{
	/**
	 * Obtiene el resumen que se le envía al jugador rival
	 * @return el resumen que se le envía al jugador rival
	 */
	ResumenTablero obtenerResumen();
	
	/**
	 * Retorna una lista con las casillas ocupadas por un barco, dado su número
	 * @param numeroBarco Número del barco a averiguar.
	 * @return Lista de casillas ocupadas por el barco.
	 */
	List<Casilla> obtenerCasillasOcupadasPorBarco( int numeroBarco );
	
	/**
	 * Informa al tablero que el jugador rival disparó contra una casilla.
	 * @param casilla Casilla a la cual se disparó.
	 * @return el resultado del disparo hecho.
	 */
	RespuestaJugada dispararACasilla( Casilla casilla );
	
	/**
	 * 
	 * @return El número de barcos no hundidos aún.
	 */
	int numeroBarcosNoHundidos();
	
}
