/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private final VetRepository vetRepository;

	public VetController(VetRepository clinicService) {
		this.vetRepository = clinicService;
	}

	@GetMapping("/vets.html")
	public String showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		Page<Vet> paginated = findPaginated(page);
		vets.getVetList().addAll(paginated.toList());
		return addPaginationModel(page, paginated, model);
	}

	private String addPaginationModel(int page, Page<Vet> paginated, Model model) {
		List<Vet> listVets = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listVets", listVets);
		return "vets/vetList";
	}

	private Page<Vet> findPaginated(int page) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return vetRepository.findAll(pageable);
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vetRepository.findAll());
		return vets;
	}

	@GetMapping("/vets/most-active")
	public @ResponseBody List<Map<String, Object>> getVetsWithMostPets() {
		List<Object[]> results = this.vetRepository.findVetsWithPetCounts();
		return results.stream()
			.map(result -> {
				Map<String, Object> map = new HashMap<>();
				map.put("vet", result[0]);
				map.put("petCount", result[1]);
				return map;
			})
			.collect(Collectors.toList());
	}

	@GetMapping("/vets/with-pets")
	public @ResponseBody List<Map<String, Object>> getVetsWithTreatedPets() {
		List<Object[]> results = this.vetRepository.findVetsWithTreatedPets();
		Map<Vet, Set<Pet>> vetPetsMap = new HashMap<>();
		
		results.forEach(result -> {
			Vet vet = (Vet) result[0];
			Pet pet = (Pet) result[1];
			vetPetsMap.computeIfAbsent(vet, k -> new HashSet<>());
			if (pet != null) {
				vetPetsMap.get(vet).add(pet);
			}
		});

		return vetPetsMap.entrySet().stream()
			.map(entry -> {
				Map<String, Object> map = new HashMap<>();
				map.put("vet", entry.getKey());
				map.put("treatedPets", entry.getValue());
				return map;
			})
			.collect(Collectors.toList());
	}

}
