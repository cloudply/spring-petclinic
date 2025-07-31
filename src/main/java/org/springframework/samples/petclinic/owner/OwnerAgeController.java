package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for handling owner age-related operations.
 */
@Controller
public class OwnerAgeController {

	private final OwnerRepository owners;

	public OwnerAgeController(OwnerRepository owners) {
		this.owners = owners;
	}

	/**
	 * Find owners by age.
	 * @param age the age to search for
	 * @param page the page number
	 * @param model the model to add attributes to
	 * @return the view name
	 */
	@GetMapping("/owners/age")
	public String findOwnersByAge(@RequestParam int age, @RequestParam(defaultValue = "1") int page, Model model) {
		Page<Owner> ownersPage = findPaginatedByAge(page, age);
		
		if (ownersPage.isEmpty()) {
			// No owners found
			model.addAttribute("message", "No owners found with age " + age);
			return "owners/findOwners";
		}
		
		return addPaginationModel(page, model, ownersPage);
	}
	
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}
	
	private Page<Owner> findPaginatedByAge(int page, int age) {
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByAge(age, pageable);
	}
}
