package com.br.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

//public record DataLivro() {
//    public String titulo() {
//    }
//
//    public Integer downloads() {
//    }
//
//    public CharSequence idiomas() {
//    }
//
//    public Object formatos() {
//    }

    public record DataLivro(Integer id,
                            @JsonAlias("title") String titulo,
                            @JsonAlias("authors") List<DataAutor> autores,
                            @JsonAlias("languages") String[] idiomas,
                            @JsonAlias("download_count") Integer downloads,
                            @JsonAlias("formats") DataFormatos formatos)

    {
    }

