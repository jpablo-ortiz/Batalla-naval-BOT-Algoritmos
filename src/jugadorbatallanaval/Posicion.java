
package jugadorbatallanaval;

import co.edu.javeriana.algoritmos.proyecto.Casilla;

public class Posicion extends Casilla
{
    //===============================//
    //========== Atributos ==========//
    //===============================//
    
    private boolean destruido; //verdadero = disparado, falso = no disparado
    private boolean esCasillaPosicion; // Verdadero = para Nodo Tablero Jugador Nuestro ((i,0)->filas, (0,i)->columnas), falso = para Barco
            
    //===============================//
    //======== Constructores ========//
    //===============================//
    
    public Posicion(int fila, int columna, boolean esCasillaPosicion)
    {
        super(fila, columna);
        this.esCasillaPosicion = esCasillaPosicion;
    }

    //===============================//
    //====== Getters y Setters ======//
    //===============================//
    
    public boolean esDestruido()
    {
        return destruido;
    }

    public void setDestruido(boolean estado)
    {
        this.destruido = estado;
    }

    public boolean esCasillaPosicion()
    {
        return esCasillaPosicion;
    }

    public void setEsCasillaPosicion(boolean esCasillaPosicion)
    {
        this.esCasillaPosicion = esCasillaPosicion;
    }

}
