package com.peas.model;

import java.util.List;

public class Trilha {
	
	List<Tile> tilesAlvo;
	List<Tile> juntas;
	List<Tile> pecas;
	List<Tile> listaGeral;
	Tile peca;
	/*
	 * 
	 * 
	 * 
	 *   List<TileController> _tilesAlvo = [];
  List<TileController> _juntas = [];
  List<TileController> _pecas = [];
  List<TileController> _listaGeral = []; //LinkedHashSet();
  TileController _peca;*/
	public List<Tile> getTilesAlvo() {
		return tilesAlvo;
	}
	public void setTilesAlvo(List<Tile> tilesAlvo) {
		this.tilesAlvo = tilesAlvo;
	}
	public List<Tile> getJuntas() {
		return juntas;
	}
	public void setJuntas(List<Tile> juntas) {
		this.juntas = juntas;
	}
	public List<Tile> getPecas() {
		return pecas;
	}
	public void setPecas(List<Tile> pecas) {
		this.pecas = pecas;
	}
	public List<Tile> getListaGeral() {
		return listaGeral;
	}
	public void setListaGeral(List<Tile> listaGeral) {
		this.listaGeral = listaGeral;
	}
	public Tile getPeca() {
		return peca;
	}
	public void setPeca(Tile peca) {
		this.peca = peca;
	}
}
