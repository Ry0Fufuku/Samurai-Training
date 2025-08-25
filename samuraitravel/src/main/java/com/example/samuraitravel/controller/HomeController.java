package com.example.samuraitravel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.repository.HouseRepository;

@Controller
public class HomeController {

	private final HouseRepository houseRepository;
	public HomeController(HouseRepository houseRepository) {
		this.houseRepository = houseRepository;
	}

	//ルート画面にGetリクエストでtemplates直下のindexという名前のhtmlを返す
	@GetMapping("/")
	public String index(Model model) {
		List<House> newHouses = houseRepository.findTop10ByOrderByCreatedAtDesc();
		model.addAttribute("newHouses", newHouses);
		return "index";
	}
}
