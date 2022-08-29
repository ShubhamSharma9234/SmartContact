package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepositry;
import com.smart.entity.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder ;
	
	@Autowired
	private UserRepositry userRepositry;

	// this is for testing purpose
	/*
	 * @GetMapping("/test")
	 * 
	 * @ResponseBody public String test() {
	 * 
	 * User user = new User(); user.setName("Shubham Kumar Sharma");
	 * user.setEmail("sharmashubham9234@gmail.com"); Contact contact = new
	 * Contact();
	 * 
	 * user.getContacts().add(contact);
	 * 
	 * System.out.println("Working");
	 * 
	 * userRepositry.save(user);
	 * 
	 * return "working"; }
	 */

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "home-smart contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-smart contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "sign-up-smart contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	//
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") Boolean agreement, Model model,
			HttpSession session) {
		// term and condition check true and false

		try {
			if (!agreement) {
				System.out.println("You have not agreed the term and condition!!!");
				throw new Exception("You have not agreed the term and condition!!!");
			}
			
			if(bindingResult.hasErrors()) {
				System.out.println("Errors are : "+bindingResult.toString() );
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("robot.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword())); //It will encode the password 
			
			System.out.println("Agreement : " + agreement);
			System.out.println("User : " + user);

			// pass the user object
			model.addAttribute("user", user);

			User result = this.userRepositry.save(user);

			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Successfully Registered", "alert-success")); // It will show if your form is submitted
			System.out.println(result);

			return "signup";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong" + e.getMessage(), "alert-danger")); // it will show if your form has got any error

			return "signup";

		}

	}
	
	@RequestMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title","loginPage");
		return "login";
	}
}
