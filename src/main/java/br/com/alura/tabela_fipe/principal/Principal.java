package br.com.alura.tabela_fipe.principal;

import br.com.alura.tabela_fipe.models.Dados;
import br.com.alura.tabela_fipe.models.Modelos;
import br.com.alura.tabela_fipe.models.Veiculo;
import br.com.alura.tabela_fipe.services.ConsumoApi;
import br.com.alura.tabela_fipe.services.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados converteDados = new ConverteDados();

    public void exibeMenu() {
        var menu = """
                *** OPÇÕES ***
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        var opcao = leitura.nextLine();

        String endereco;

        if (opcao.toLowerCase().contains("car")) {
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        } else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumoApi.obterDados(endereco);
        var marcas = converteDados.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta");
        var codigoMarca = leitura.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumoApi.obterDados(endereco);
        var modeloLista = converteDados.obterDados(json, Modelos.class);

        System.out.println("Modelos dessa marca: ");
        modeloLista.modelos()
                .stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Digite um trecho do nome a ser buscado");
        var veiculoBuscado = leitura.nextLine();

        List<Dados> modelosFiltrados = modeloLista.modelos()
                .stream()
                .filter(m -> m.nome().toLowerCase().contains(veiculoBuscado.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("Modelos Filtrados");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para buscar os valores de avaliação: ");
        var codigoModelo = leitura.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumoApi.obterDados(endereco);

        List<Dados> anos = converteDados.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumoApi.obterDados(enderecoAnos);
            Veiculo veiculo = converteDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("Todos veiculos filtrados com avaliação por ano: ");
        veiculos.forEach(System.out::println);
    }
}
