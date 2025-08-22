package com.example.moattravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.moattravel.entity.House;
import com.example.moattravel.form.HouseRegisterForm;
import com.example.moattravel.repository.HouseRepository;
import com.example.moattravel.service.HouseService;

@Controller
@RequestMapping("/admin/houses") //ルートパス基準値設定アノ
public class AdminHouseController {
	//管理者家画面に関すること

	private final HouseRepository houseRepository;
	private final HouseService houseService;


	public AdminHouseController(HouseRepository houseRepository,HouseService houseService) {

		this.houseRepository = houseRepository;
		this.houseService = houseService;

	}

	//コントローラーからビューにデータを渡す場合Modelクラスを使う
	//houseリポジトリからHouseエンティティをすべて取得しそれをHTMLに渡すためにmodelに追加している。"houses"という名前でテンプレートで参照可能にしている
	//Spring由来のModelオブジェクトは画面(HTML)に渡したいデータを追加するために使う
	//htmlでadmin/houses/下のindexを返す
	@GetMapping
	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {

		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {

			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);

		} else {

			housePage = houseRepository.findAll(pageable);

		}

		//第1引数：ビュー側から参照する変数名,第2引数：ビューに渡すデータ

		model.addAttribute("housePage", housePage);

		model.addAttribute("keyword", keyword);

		return "admin/houses/index";

	}

	@GetMapping("/{id}") //パスからidとって変数idにバインド
	public String show(@PathVariable(name = "id") Integer id, Model model) {

		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);

		return "admin/houses/show";

	}

	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("houseRegisterForm", new HouseRegisterForm());
		return "admin/houses/register";
	}

	@PostMapping("/create")//フォーム登録先//フォームバインド
	public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {//バリチェックエラーなら戻す
			return "admin/houses/register";
		}
		houseService.create(houseRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");
		return "redirect:/admin/houses";
	}
}
