package com.br.alura.literalura.principal;

import com.br.alura.literalura.models.*;
import com.br.alura.literalura.repositories.AutorRepository;
import com.br.alura.literalura.repositories.LivroRepository;
import com.br.alura.literalura.services.ConsumoApi;
import com.br.alura.literalura.services.ConverterJsonToObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static java.util.Comparator.comparing;

public class Principal {

    private AutorRepository autorRepository;
    private LivroRepository livroRepository;
    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://gutendex.com/books/?search=";
    Scanner scanner = new Scanner(System.in);
    ConsumoApi consumoApi = new ConsumoApi();
    ConverterJsonToObject conversor = new ConverterJsonToObject();
    List<Livro> livros = new ArrayList<>();
    List<Autor> autores = new ArrayList<>();

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository){
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public Principal() {

    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 9) {
            var menu = """
                    *** Catálogo de livros ***
                    
                    Escolha o número de sua opção:
                    
                    1- Buscar livro por título
                    2- Listar livros registrados
                    3- Listar autores registrados
                    4- Listar autores vivos em um determinado ano
                    5- Listar livros em um determinado idioma

                    9 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:

                    System.out.println("Opção " + opcao + " selecionada: Buscar um livro");
                    getLivroFromApi();
                    break;
                case 2:
                    System.out.println("Opção " + opcao + " selecionada: Listar livros registrados");
                    getAllLivrosFromDb();
                    break;
                case 3:
                    System.out.println("Opção " + opcao + " selecionada: Listar autores registrados");
                    getAllAutoresFromDb();
                    break;
                case 4:
                    System.out.println("Opção " + opcao + " selecionada: Listar autores vivos em determinado ano");
                    getAutoresVivoAno();
                    break;
                case 5:
                    System.out.println("Opção " + opcao + " selecionada: Listar livros em determinado idioma");
                    getLivrosNaLingua();
                    break;
                case 9:
                    System.out.println("Encerrando a aplicação, muito obrigada!");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private DataLivraria getLivraria() {
        System.out.println("Digite o nome do livro que deseja buscar:");

        var bookName = scanner.nextLine();

        String json = consumoApi.obterJson(ENDERECO + bookName.replace(" ", "%20").toLowerCase().trim());

        var livraria = conversor.converterDados(json, DataLivraria.class);
        return livraria;

    }
    private void getLivrosNaLingua() {
        scanner.nextLine();
        String menuIdioma =
                """                                              
                Esreva o idioma por favor
                ---------------------------
                 pt - Português
                 en - Inglês
                 es - Espanhol
                 fr - Francês
                ---------------------------
                """;
        System.out.println(menuIdioma);
        String lingua = scanner.nextLine();

        List<Livro> livrosNumaLingua = livroRepository.findByIdioma(lingua);

        if(livrosNumaLingua.isEmpty()){
            System.out.println("Não tem livros registrados no idioma " + lingua);

        }else{
            System.out.println("================ Livros no idioma " + lingua + " ================");
            System.out.println('\n');
            livrosNumaLingua.forEach(l->
                    System.out.println("Titulo: " + l.getTitulo() + " , Autor: " + l.getAutor().getNome()));
        }
    }

    private void getAutoresVivoAno() {
        System.out.println("Digite o ano:");
        Integer ano = scanner.nextInt();
        scanner.nextLine();

        List<Autor> autoresVivos = autorRepository.BuscaAutoresVivosNumAnoDado(ano);

        if(autoresVivos.isEmpty()){
            System.out.println("Não tem registrados autores vivos no ano " + ano);
        }else{
            System.out.println("========= Autores vivos no ano " + ano + " ===============");
            System.out.println('\n');
            imprimirAutores(autoresVivos);
        }
    }

    private void imprimirAutores(List<Autor> autores) {
        autores.forEach(a-> {
            System.out.println("Nome: " + a.getNome());
            System.out.println("Nacimento: " + a.getAnoNac());
            System.out.println("Morte: " + a.getAnoMorte());

            List<String> listaTitulos = new ArrayList<>();
            a.getLivros().forEach(l -> listaTitulos.add(l.getTitulo()));

            System.out.println("Livros: " + listaTitulos);
            System.out.println("-----------------------------------------------------");
        });
    }

    private void getAllAutoresFromDb() {
        autores = autorRepository.findAll();

        if(autores.isEmpty()){
            System.out.println("=========== Não existem autores registrados ============");
        }else{
            System.out.println("=================== Autores ========================");

            autores.sort(comparing(Autor::getNome));
            imprimirAutores(autores);
        }
    }

    private void getAllLivrosFromDb() {
        livros = livroRepository.findAll();
        if(livros.isEmpty()){
            System.out.println("========== Não existem livros registrados ==========");
        }else{
            System.out.println("=================== Autores =====================");

            autores.sort(comparing(Autor::getNome));
            imprimirAutores(autores);
        }
    }

    private void getLivroFromApi() {

        DataLivraria dLivraria = getLivraria();

        Optional<DataLivro> optDataLivro = dLivraria.livros().stream()
                .sorted(comparing(DataLivro::id))
                .findFirst();

        if(optDataLivro.isPresent()){

            DataLivro dataLivro = optDataLivro.get();
            String titulo = dataLivro.titulo();

            Optional<Livro> optLivro = livroRepository.findByTituloEqualsIgnoreCase(titulo);

            if(optLivro.isPresent()){
                System.out.println("Esse livro já está registrado no banco de dados, tente outro por favor.");
            }else{
                imprimirDataLivro(dataLivro);

                System.out.println("Digite 1 se é o livro que procurava ou 2 se não é o livro");
                int achou = scanner.nextInt();
                scanner.nextLine();
                if(achou ==1){

                    Autor autor = new Autor(dataLivro.autores().get(0));
                    Livro livro = new Livro(dataLivro);
                    autor.setLivro(livro);

                    Optional<Autor> optAutor = autorRepository.findByNomeEqualsIgnoreCase(autor.getNome());

                    if (optAutor.isPresent()){
                        Autor autorRegistrado = optAutor.get();
                        livro.setAutor(autorRegistrado);
                        livroRepository.save(livro);
                    }else{
                        autorRepository.save(autor);

                    }
                    System.out.println("Livro salvo com sucesso!");

                }else{
                    System.out.println("Tente agregando mais palavra ao título");

                }
            }

        }else{
            System.out.println("Livro não encontrado");
        }
    }

    private void imprimirDataLivro(DataLivro dataLivro) {
        System.out.println("-------Livro---------");
        System.out.println("Titulo: " + dataLivro.titulo());
        dataLivro.autores().forEach(this::imprimirDataAutor);
        System.out.println("Idioma: " + String.join(" ", dataLivro.idiomas()));
        System.out.println("Numero de downloads: " + dataLivro.downloads());
        System.out.println("Poster: " + dataLivro.formatos().poster());
        System.out.println("---------------------");
        System.out.println("\n");
    }

    private void imprimirDataAutor(DataAutor dataAutor) {
        System.out.println("Autor: " + dataAutor.nome());
    }
}
