package co.edu.javeriana.algoritmos.proyecto;

/**
 * Esta es la interfaz que deben implementar con su jugador.  
 * 
 * NOTA: Su implementación debe contar, de manera obligatoria, con una constructora sin parámetros que no lance excepciones.
 * NO CAMBIE NADA EN ESTE ARCHIVO, NI LO MUEVA, NI LO COPIE.
 * @author danilo
 *
 */
public interface Jugador 
{
	/**
	 * 
	 * @return el nombre que identifica a este jugador.
	 */
	String obtenerNombre();
	
	/**
	 * Inicia el jugador con la dimensión y los barcos indicados.
	 * @param dimension Tamaño del lado del tablero.
	 * @param barcos La iésima casilla de este arreglo representa el tamaño en celdas del iésimo barco.
	 * @return Un tablero legal del tamaño indicado con todos los barcos dispuestos
	 */
	Tablero iniciarTablero( int dimension, int[] barcos );
	
	/**
	 * Método a través del cual se entrega a este jugador el resumen del tablero del jugador rival.
	 * @param resumen Dicho resumen.
	 */
	void recibirResumenRival( ResumenTablero resumen );
	
	/**
	 * Registra el disparo realizado por el rival sobre la casilla recibida como parámetro y retorna la respuesta
	 * que el rival debe recibir.
	 * @param posicion Posición a la cual el rival ha disparado
	 * @return el resultado del disparo
	 */
	RespuestaJugada registrarDisparoAPosicion( Casilla posicion );
	
	/**
	 * Retorna la casilla a la cual este jugador dispara en el tablero del rival.
	 * @return la casilla a la cual este jugador dispara en el tablero del rival.
	 */
	Casilla realizarDisparo();
	
	/**
	 * Procesa el resultado del último disparo realizado por este jugador a través del método <code>realizarDisparo</code>.
	 * @param resultado El resultado del disparo
	 */
	void procesarResultadoDisparo( RespuestaJugada resultado );
	
	/**
	 * 
	 * @return el número de barcos no hundidos que este jugador aún tiene.
	 */
	int numeroBarcosNoHundidos();
}
