package com.br.alura.literalura.services;


import java.util.List;

public interface IConverterJsonToObject{
    <T> T converterDados(String json, Class<T> classe);
    <T> List<T> obterLista (String json, Class<T> classe);
}
