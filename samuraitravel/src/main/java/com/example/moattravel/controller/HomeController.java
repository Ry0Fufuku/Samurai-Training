package com.example.moattravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	//ルート画面にGetリクエストでtemplates直下のindexという名前のhtmlを返す
	@GetMapping("/")
	public String index() {
		return "index";
	}
}
