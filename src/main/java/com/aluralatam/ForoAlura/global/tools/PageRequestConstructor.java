package com.aluralatam.ForoAlura.global.tools;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import org.springframework.data.domain.*;
public class PageRequestConstructor{
    /**
     * Crea un PageRequest a partir de un QueryPageable.
     *
     * @param queryPageable objeto que contiene la información de paginación y ordenamiento.
     * @return objeto PageRequest construido con los parámetros proporcionados.
     * @throws BusinessRuleException Si viola una regla de negocio
     */
    public static PageRequest buildPageRequest(QueryPageable queryPageable) throws BusinessRuleException {
            var pagNum = queryPageable.getPage();
            var pagTam = queryPageable.getElementByPage();
            if(pagNum<1)
                throw new BusinessRuleException(Message.NUMBER_EXCEPTION+" (Numero de pagina invalido.)");
            if(pagTam<1)
                throw new BusinessRuleException(Message.NUMBER_EXCEPTION+" (Numero de recursos invalido.)");
            String field = queryPageable.sortingParams()[0];
            String sortingDirection = queryPageable.sortingParams()[1];

            Sort.Direction direction =
                sortingDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return PageRequest.of(pagNum -1,pagTam, Sort.by(direction,field));
    }
}