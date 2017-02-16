package br.com.alura.loja.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.alura.loja.dao.CarrinhoDAO;
import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

import com.thoughtworks.xstream.XStream;

/* ARTIGO QUE AJUDA A ENTENDER BEM O RESTFUL COM JAX-RS e JERSEY
 *  http://www.k19.com.br/artigos/criando-um-webservice-restful-em-java/
 * */

@Path("carrinhos") // path (URI do servidor)
public class CarrinhoResource {
	
	@Path("{id}")
	@GET // Aviso como o método será acessado
	@Produces(MediaType.APPLICATION_XML) // Avisa o JAX-RS que estamos produzindo um XML
	//@Produces(MediaType.APPLICATION_JSON) 
	public String buscar(@PathParam("id") long id){ // Pega um parâmetro
		Carrinho carrinho =  new CarrinhoDAO().busca(id);
		return carrinho.toXML();
		//return carrinho.toJson();
	} // curl -v http://localhost:8080/carrinhos/3
	
	@POST
	@Consumes(MediaType.APPLICATION_XML) // consome XML
	public Response adiciona(String conteudo){
		Carrinho carrinho = (Carrinho) new XStream().fromXML(conteudo);
		new CarrinhoDAO().adiciona(carrinho);
		URI uri = URI.create("/carrinho/" + carrinho.getId());
		return Response.created(uri).build(); // Retorna uma respostas 201 com o endereço do novo registro
	} 
	/* 
	 * curl -v -H "Content-Type: application/xml" -d "<br.com.alura.loja.modelo.Carrinho>  <produtos>    <br.c
	om.alura.loja.modelo.Produto>      <preco>4000.0</preco>      <id>6237</id>      <nome>Videogame 4</nome>
    <quantidade>1</quantidade>    </br.com.alura.loja.modelo.Produto>  </produtos>  <rua>Rua Vergueiro
3185, 8 andar</rua>  <cidade>São Paulo</cidade>  <id>1</id></br.com.alura.loja.modelo.Carrinho>" ht
tp://localhost:8080/carrinhos
	 */
	
	@Path("{id}/produtos/{produtoId}")
	@DELETE
	public Response removeProduto(@PathParam("id") long id, @PathParam("produtoId") long produtoId){
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		carrinho.remove(produtoId);
		return Response.ok().build();
	} // curl -v -X "DELETE" http://localhost:8080/carrinhos/1/produtos/6237
	
	@Path("{id}/produtos/{produtoId}/quantidade")
	@PUT
	public Response alteraProduto(String conteudo, @PathParam("id") long id, @PathParam("produtoId") long produtoId){
		Carrinho carrinho = new CarrinhoDAO().busca(id);
		Produto produto = (Produto) new XStream().fromXML(conteudo);
		carrinho.trocaQuantidade(produto);
		return Response.ok().build();
	}
	/*
	 * curl -v -X "PUT" -d "<br.com.alura.loja.modelo.Produto> <preco>6.0</preco> <id>3467</id> <nome>Jogo de
esporte</nome> <quantidade>15</quantidade> </br.com.alura.loja.modelo.Produto>" http://localhost:8080/car
rinhos/1/produtos/3467/quantidade
	 * */
}
