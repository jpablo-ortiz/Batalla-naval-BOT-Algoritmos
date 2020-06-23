package jugadorbatallanaval;

import Estructuras.Vertice;
import co.edu.javeriana.algoritmos.proyecto.Casilla;
import java.util.ArrayList;

public class InspeccionDisparo
{

    private ArrayList<Vertice<Casilla>> inspeccionActual;
    private Vertice<Casilla> cabeza;
    private Vertice<Casilla> cola;
    private int direccion; // 1 = horizontal, 2 = vertical, 3 = diagonal abajo derecha, 4 = diagonal abajo izquierda

    public InspeccionDisparo()
    {
        inspeccionActual = new ArrayList<>();
    }

    public void insertarDisparo(Vertice<Casilla> disparoCertero)
    {
        inspeccionActual.add(disparoCertero);
        switch(inspeccionActual.size())
        {
            case 1:
                cabeza = disparoCertero;
                cola = disparoCertero;
                break;

            case 2:
                if(disparoCertero.getValor().getFila() == cola.getValor().getFila())
                {
                    direccion = 1; //1 horizontal
                }
                else if(disparoCertero.getValor().getColumna() == cola.getValor().getColumna())
                {
                    direccion = 2; //2 vertical
                }
                else if((disparoCertero.getValor().getFila() > cola.getValor().getFila() && disparoCertero.getValor().getColumna() > cola.getValor().getColumna()) || (disparoCertero.getValor().getFila() < cola.getValor().getFila() && disparoCertero.getValor().getColumna() < cola.getValor().getColumna()))
                {
                    direccion = 3; //3 = diagonal abajo derecha
                }
                else
                {
                    direccion = 4; //4 = diagonal abajo izquierda
                }
                //break;

            default:
                switch(direccion)
                {
                    case 1:
                        //Si la que se pone esta atras de la cola volverlo la nueva cola
                        if(disparoCertero.getValor().getFila() == cola.getValor().getFila() && disparoCertero.getValor().getColumna() < cola.getValor().getColumna())
                        {
                            cola = disparoCertero;
                        }
                        else //Si la que se pone esta delante de la cabeza volverlo la nueva cabeza
                        {
                            cabeza = disparoCertero;
                        }
                        break;

                    case 2:
                        //Si la que se pone esta atras de la cola volverlo la nueva cola
                        if(disparoCertero.getValor().getFila() > cola.getValor().getFila() && disparoCertero.getValor().getColumna() == cola.getValor().getColumna())
                        {
                            cola = disparoCertero;
                        }
                        else //Si la que se pone esta delante de la cabeza volverlo la nueva cabeza
                        {
                            cabeza = disparoCertero;
                        }
                        break;

                    case 3:
                        //Si la que se pone esta atras de la cola volverlo la nueva cola
                        if(disparoCertero.getValor().getFila() > cola.getValor().getFila() && disparoCertero.getValor().getColumna() > cola.getValor().getColumna())
                        {
                            cola = disparoCertero;
                        }
                        else //Si la que se pone esta delante de la cabeza volverlo la nueva cabeza
                        {
                            cabeza = disparoCertero;
                        }
                        break;

                    case 4:
                        //Si la que se pone esta atras de la cola volverlo la nueva cola
                        if(disparoCertero.getValor().getFila() > cola.getValor().getFila() && disparoCertero.getValor().getColumna() < cola.getValor().getColumna())
                        {
                            cola = disparoCertero;
                        }
                        else //Si la que se pone esta delante de la cabeza volverlo la nueva cabeza
                        {
                            cabeza = disparoCertero;
                        }
                        break;
                }
                break;
        }
    }

    public void clear()
    {
        cabeza = null;
        cola = null;
        direccion = 0;
        inspeccionActual = new ArrayList<>();
    }

    public ArrayList<Vertice<Casilla>> getInspeccionActual()
    {
        return inspeccionActual;
    }

    public void setInspeccionActual(ArrayList<Vertice<Casilla>> inspeccionActual)
    {
        this.inspeccionActual = inspeccionActual;
    }

    public Vertice<Casilla> getCabeza()
    {
        return cabeza;
    }

    public void setCabeza(Vertice<Casilla> cabeza)
    {
        this.cabeza = cabeza;
    }

    public Vertice<Casilla> getCola()
    {
        return cola;
    }

    public void setCola(Vertice<Casilla> cola)
    {
        this.cola = cola;
    }

    public int getDireccion()
    {
        return direccion;
    }

    public void setDireccion(int direccion)
    {
        this.direccion = direccion;
    }

}
