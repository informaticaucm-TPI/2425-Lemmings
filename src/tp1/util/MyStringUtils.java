package tp1.util;

/**
 * Utilidades para cadenas
 * 
 * @author Equipo TP1
 * @version 1.0 17/9/2023
 * 
 */
public class MyStringUtils {

	/**
	 * Concatena una cadena consigo misma un determinado número de veces
	 * 
	 * @param x cadena
	 * @param n repeticiones
	 * @return El resultado de concatenar x consigo misma n veces
	 */
	public static String repeat(String x, int n) {
		StringBuilder result = new StringBuilder(x.length() * n);

		
		for (int i = 0; i < n; i++)
			result.append(x);
		
		//result.repeat(x, n); //puedes reemplazar el bucle anterior por esta línea si usas Java 21

		return result.toString();
	}

	/**
	 * Centra un texto en un espacio de un determinado ancho 
	 * 
	 * @param texto   cadena a centrar
	 * @param ancho   tamaño de la cadena resultado
	 * @return Una cadena en la que texto queda centrado
	 */
	public static String center(String texto, int ancho) {
		return center(texto, ancho, ' ');
	}

	/**
	 * Centra un texto en un espacio de un determinado ancho (el resto se rellena
	 * con el carácter que se determine)
	 * 
	 * @param texto   cadena a centrar
	 * @param ancho   tamaño de la cadena resultado
	 * @param relleno carácter con el que rellenar
	 * @return Una cadena de tamaño ancho en la que texto queda centrado
	 */
	public static String center(String texto, int ancho, char relleno) {
		if (ancho > texto.length()) {
			StringBuilder result = new StringBuilder(ancho);
			
			int vacio = ancho - texto.length();
			int pre = vacio / 2;
			int post = (vacio + 1) / 2;
			
			result.append(repeat("" + relleno, pre));
			result.append(texto);
			result.append(repeat("" + relleno, post));
			
			return result.toString();
		}
		else
			return texto.substring(0, ancho);
	}
}