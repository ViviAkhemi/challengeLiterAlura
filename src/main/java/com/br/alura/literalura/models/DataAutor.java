package com.br.alura.literalura.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DataAutor(@JsonAlias( "name" ) String nome,
                        @JsonAlias("birth_year") Integer anoNac,
                        @JsonAlias("death_year") Integer anoMor){

}
