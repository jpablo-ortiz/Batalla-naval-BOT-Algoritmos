package jugadorbatallanaval;

import co.edu.javeriana.algoritmos.proyecto.Casilla;
import co.edu.javeriana.algoritmos.proyecto.Jugador;
import co.edu.javeriana.algoritmos.proyecto.RespuestaJugada;
import co.edu.javeriana.algoritmos.proyecto.ResumenTablero;
import co.edu.javeriana.algoritmos.proyecto.Tablero;

public class JugadorGuyIncognito implements Jugador
{
    //===============================//
    //========== Atributos ==========//
    //===============================//

    private String nombre;
    private TableroJugador tableroJugador;
    private TableroEnemigo tableroContrincante;

    //===============================//
    //======== Constructores ========//
    //===============================//
    
    public JugadorGuyIncognito() //Debe ser un constructor vacío
    {
        this.nombre = "Guy Incognito";
    }

    //===============================//
    //======= Métodos Override ======//
    //===============================//
    
    @Override
    public String obtenerNombre()
    {
        return nombre;
    }
    
    @Override
    public int numeroBarcosNoHundidos()
    {
        return tableroJugador.numeroBarcosNoHundidos();
    }
    
    @Override
    public void recibirResumenRival(ResumenTablero resumen)
    {
        tableroContrincante.recibirResumenRival(resumen);
    }
    
    @Override
    public RespuestaJugada registrarDisparoAPosicion(Casilla posicion)
    {
        return tableroJugador.dispararACasilla(posicion);
    }
    
    @Override
    public Tablero iniciarTablero(int dimension, int[] barcos)
    {
        //Inicializar Tablero Jugador
        tableroJugador = new TableroJugador(dimension, barcos, 1);
        
        tableroContrincante = new TableroEnemigo(dimension, barcos, 1);
        
        return tableroJugador;
    }

    @Override
    public Casilla realizarDisparo()
    {
        return tableroContrincante.darDisparoAEnemigo();
    }

    @Override
    public void procesarResultadoDisparo(RespuestaJugada resultado)
    {
        tableroContrincante.actualizarProbabilidadesTablero(resultado);
    }   

    
    //ELIMINAR
    public TableroJugador getTableroJugador()
    {
        return tableroJugador;
    }

    public TableroEnemigo getTableroContrincante()
    {
        return tableroContrincante;
    }

    
}
