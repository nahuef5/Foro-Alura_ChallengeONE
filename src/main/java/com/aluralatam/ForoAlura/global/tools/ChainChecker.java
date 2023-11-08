package com.aluralatam.ForoAlura.global.tools;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
public class ChainChecker{
    /**
     * Verifica si el string proporcionado es inválido.
     * @param string string a verificar.
     * @return mensaje de error si el string es inválido, o null si es válido.
     */
    public static String invalidText(String string){
        if(string.isEmpty())
            return Message.EMPTY_FIELD;
        else if(string.matches("C\\+\\+"))
            return null;
        else if(string.matches("C#"))
            return null;
        else if(!string.matches("^[a-zA-Z ]+$"))
            return "No debe ser numerico el campo";
        return null;
    }
    /**
     * Convierte el status proporcionado a un objeto StatusTopico.
     * @param status status a formatear.
     * @return El objeto StatusTopico formateado.
     */
    public static StatusTopico formattedStatus(String status){
        var statusTopico = status.toUpperCase()
                .replace(" ", "_");
        StatusTopico value=StatusTopico.valueOf(statusTopico);
        return value;
    }
    /**
     * Verifica si el string proporcionadp es un status válido.
     * @param status status a verificar.
     * @return true si el status es válido, false en caso contrario.
     */
    public static boolean isStatus(String status){
        try{
            var replace=status.toUpperCase().replace(" ","_");
            StatusTopico.valueOf(replace);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
    /**
     * Verifica si el string proporcionado es un país válido.
     * @param pais país a verificar.
     * @return true si el país es válido, false en caso contrario.
     */
    public static boolean isCountry(String pais){
        try{
            var replace=pais.toUpperCase().replace(" ","_");
            Countries.valueOf(replace);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
    /**
     * Verifica si el email proporcionado es inválido.
     * @param email email a verificar.
     * @return mensaje de error si el email es inválido, o null si es válido.
     */
    public static String invalidEmail(String email){
        final String regex="^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if(email.isEmpty())
            return Message.EMPTY_FIELD;
        if(!email.matches(regex))
            return "Debe cumplir con un formato de mail el campo";
        return null;
    }
}