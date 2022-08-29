package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepositry;
import com.smart.dao.MyOrderRepositry;
import com.smart.dao.UserRepositry;
import com.smart.entity.Contact;
import com.smart.entity.MyOrder;
import com.smart.entity.User;
import com.smart.helper.Message;



import com.razorpay.*;
@Controller
@RequestMapping("/user")
public class UserController {
	
	
	
	@Autowired
	private UserRepositry userRepositry;

	@Autowired
	private ContactRepositry contactRepositry;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private MyOrderRepositry myOrderRepositry ;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String username = principal.getName();
		System.out.println("User name : " + username);

		User user = this.userRepositry.getUserByUserName(username);

		System.out.println("User : " + user);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) // principal is security module and through this we will
																// // get username/email
	{
		return "normal/user_dashboard";
	}

	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "add Contacts ");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process_contact")
	public String process(@ModelAttribute Contact contact, BindingResult bindingResult,
			@RequestParam("profileImage") MultipartFile file, // get images
			Model model, Principal principal, HttpSession session) {

		try {
			// Show errors if any found
			if (bindingResult.hasErrors()) {
				model.addAttribute("error", bindingResult);
				return "normal/errors";
			}

			String name = principal.getName(); // get the user id
			User user = this.userRepositry.getUserByUserName(name);

			// Processing and uploading file

			if (file.isEmpty()) {
				// if file is empty then try to pass our message
				System.out.println("Image is empty");
				contact.setImage("default_contact.png");
			} else {
				// if the file is found and the the name to contact
				contact.setImage(file.getOriginalFilename());

				File saveImage = new ClassPathResource("static/images").getFile(); // get the path of the folder where
																					// to copy the image

				Path path = Paths.get(saveImage.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING); // replace file is remove
																								// existance file and
																								// add the new file
				System.out.println("Image is uploaded");
			}

			user.getContacts().add(contact); // it will add contact detail to the contact database

			contact.setUser(user); // it will add user Id to the contact

			this.userRepositry.save(user); // save the user id to the contact id

			System.out.println("Added to data Successfully...");
			System.out.println("Contact Details are" + contact);
			System.out.println("Your form is submitted...");

			session.setAttribute("message", new Message("Your contact is added !!! Add More ", "success"));

		} catch (Exception e) {

			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong !!! Try again...", "danger"));

		}
		return "normal/add_contact_form";
	}

	@GetMapping("/show_contacts")
	public String showContacts(Model model, Principal principal) {

		model.addAttribute("title", "This is my show contacts");

		String name = principal.getName();

		User user = this.userRepositry.getUserByUserName(name);

		List<Contact> contacts = this.contactRepositry.findContactByUser(user.getId());

		model.addAttribute("contacts", contacts);
		return "normal/show_contacts";
	}

	// showing particular/single contact details

	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println("C_id  : " + cid);

		// this method get contact detail
		Optional<Contact> contactOptional = this.contactRepositry.findById(cid);
		Contact contact = contactOptional.get();

		// this line get the user name of the contact
		String userName = principal.getName();

		// this method save the userId to the contact
		User user = this.userRepositry.getUserByUserName(userName);

		// this method restrict another user to access any other details
		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", "This is contact detail page");
		}
		return "/normal/contact_detail";
	}

	@GetMapping("delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, HttpSession session) {

		Optional<Contact> optionalContact = this.contactRepositry.findById(cid);
		Contact contact = optionalContact.get();

		// delete the contact
		this.contactRepositry.delete(contact);

		session.setAttribute("message", new Message("contact deleted successfully", "succes"));

		return "redirect:/user/show_contacts";
	}

	// update form handler

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") int cid, Model model) {
		System.out.println("Update form called !!!");
		model.addAttribute("title", "This is update form");

		Contact contact = this.contactRepositry.findById(cid).get();

		model.addAttribute("contact", contact);

		return "normal/update_form";

	}

	@RequestMapping(value = "/process_update", method = RequestMethod.POST)
	public String updatehandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			HttpSession session, Principal principal) {

		try {

			Contact oldcontactDetail = this.contactRepositry.findById(contact.getCid()).get();

			if (!file.isEmpty()) {
				// file work
				// rewrite

				// delete old photo from file

				File delteFile = new ClassPathResource("static/images").getFile();

				File file1 = new File(delteFile, oldcontactDetail.getImage());
				file1.delete();

				// update new photo

				File savefile = new ClassPathResource("static/images").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());

			} else {
				contact.setImage(oldcontactDetail.getImage());
			}

			User user = this.userRepositry.getUserByUserName(principal.getName());

			contact.setUser(user);

			this.contactRepositry.save(contact);

			session.setAttribute("message", new Message("your contact is updated !!!", "success"));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("contact name : " + contact.getFirstName());
		System.out.println("contact Id : " + contact.getCid());
		return "redirect:/user/" + contact.getCid() + "/contact";
	}

	@GetMapping("/profile")
	public String profile(Model model) {
		System.out.println("profile page called");
		model.addAttribute("title", "Profile Page!!!");
		return "normal/profile";
	}

	// OPEN Setting handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	// Change Password
	@PostMapping("/change-password")
	public String chanegPassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession httpSession) {

		System.out.println("Old Password : " + oldPassword);

		System.out.println("new Password : " + newPassword);

		String username = principal.getName();

		User currentUser = this.userRepositry.getUserByUserName(username);

		System.out.println("current user password : " + currentUser.getPassword());

		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {

			// change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepositry.save(currentUser);

			httpSession.setAttribute("message", new Message("Your password is successfully changed", "Success"));
		} else {

			httpSession.setAttribute("message", new Message("old password can't match", "danger"));
			return "redirect:/user/index";

		}

		return "redirect:/user/index";

	}

	// creating order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data ,Principal principal)throws Exception {
		System.out.println(data);
		
		int amount = Integer.parseInt(data.get("amount").toString());
		
		var client = new RazorpayClient("rzp_test_JveLFJpZmG0SEz", "Ihpx7WR72xjnSiI9iTu80Jsa");
		
		JSONObject ob = new JSONObject();
		ob.put("amount",amount*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_235425");
		
		//creating new order
		Order order = client.orders.create(ob);
		
		System.out.println("order : "+order);
		
		System.out.println("Hey order has done");
		
		// if you want to save your data to the database 
		
		MyOrder myOrder = new MyOrder() ;
		myOrder.setAmount(order.get("amount")+"");
		//myOrder.setMyorderId(order.get("id") );
		myOrder.setPaymentId(null);
		myOrder.setStatus("created");
		myOrder.setUser(this.userRepositry.getUserByUserName(principal.getName()));
		myOrder.setReceipt(order.get("receipt"));
		
		this.myOrderRepositry.save(myOrder);
		
		return order.toString();
	}

	public ResponseEntity<?> updateOrder(@RequestBody Map<String ,Object> data){
		
		MyOrder myOrder = this.myOrderRepositry.findByorderId(data.get("order_id").toString());
		
		myOrder.setPaymentId(data.get("payment_id").toString());
		myOrder.setStatus(data.get("status").toString());
		
		this.myOrderRepositry.save(myOrder);
		System.out.println(data);
		return ResponseEntity.ok(Map.of("msg","updated"));
	}
}
