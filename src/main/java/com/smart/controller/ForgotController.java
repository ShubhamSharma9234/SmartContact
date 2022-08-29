package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepositry;
import com.smart.entity.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private UserRepositry userRepositry;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder ;

	// emailId form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {

		return "forgot_email_form";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession httpSession) {

		System.out.println("Email Id : " + email);

		// generating otp of four digit
		int otp = random.nextInt(9999);
		System.out.println("OTP : " + otp);

		String subject = "OTP from smart contact manager ";

		String message = "<div style='border:1px solid blue; padding:20px;'>" + "<h1>"
				+ " OTP IS SEND FROM SMART CONTACT MANAGER 	" + "<b style='color:cyan'>" + otp + "</b>" + "</h1>"
				+ "</div>";

		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);

		if (flag) {

			httpSession.setAttribute("myOtp", otp);
			httpSession.setAttribute("email", email);
			return "verify-otp";

		} else {

			httpSession.setAttribute("message", "check your email Id");

			return "forgot_email_form";
		}

	}

	// verify otp
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam("otp") int otp, HttpSession httpSession) {

		int myOtp = (int) httpSession.getAttribute("myOtp");
		String email = (String) httpSession.getAttribute("email");
		if (myOtp == otp) {
			// password change form

			User user = this.userRepositry.getUserByUserName(email);

			if (user == null) {
				// send Error message

				httpSession.setAttribute("message", "user does not exist with this email !!!");
				return "forgot_email_form";
				
			} else {
				// send
			}
			return "password_change_form";
		} else {

			httpSession.setAttribute("message", "You have entered wrong otp!!!");
			return "verify-otp";
		}

	}
	
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("newpassword")String newpassword , HttpSession session){
		
		String email = (String)session.getAttribute("email");
		User user = this.userRepositry.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepositry.save(user);
		
		return "redirect:/signin?change=password change successfully" ;
	}

}
