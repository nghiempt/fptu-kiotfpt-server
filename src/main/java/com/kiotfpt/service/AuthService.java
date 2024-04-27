package com.kiotfpt.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.kiotfpt.model.Account;
import com.kiotfpt.model.ResponseObject;
import com.kiotfpt.model.Shop;
import com.kiotfpt.model.Status;
import com.kiotfpt.repository.AccountRepository;
import com.kiotfpt.repository.ShopRepository;
import com.kiotfpt.repository.StatusRepository;
import com.kiotfpt.utils.JsonReader;
import com.kiotfpt.utils.MD5;

@Service
public class AuthService {
	@Autowired
	private AccountRepository repository;

//	@Autowired
//	private RoleRepository rolerepository;
	
	@Autowired
	private ShopRepository shoprepository;

	@Autowired
	private StatusRepository statusRepository;
//
//	@Autowired
//	private AccountProfileRepository profileRepository;

	HashMap<String, String> responseMessage = new JsonReader().readJsonFile();

	public ResponseEntity<ResponseObject> signIn(String username, String password) {
		if (username.trim() == "" || password.trim() == "") 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "Input can not be empty", new int[0]));

		Optional<Account> account = repository.findByAccountUsername(username);
		if (!account.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
					HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("accountNotFound"), new int[0]));
		} else if (!account.get().getAccount_password().equals(MD5.generateMD5Hash(password))) 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "Wrong password", new int[0]));
		
		if (account.get().getStatus().getValue().equals("inactive"))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject(false, HttpStatus.BAD_REQUEST.toString().split(" ")[0],
							responseMessage.get("accountNotActivate"), new int[0]));

		Map<String, String> map = new HashMap<>();
		map.put("account_id", String.valueOf(account.get().getAccount_id()));
		map.put("role", account.get().getRole().getRole_value());
		if (map.get("role").equals("Seller")) {
			Optional<Shop> shop = shoprepository.findByAccount(account.get());
			map.put("shop_id", String.valueOf(shop.get().getShop_id()));
		}
		return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
				HttpStatus.OK.toString().split(" ")[0], responseMessage.get("signInSuccess"), map));
	}

	public ResponseEntity<ResponseObject> signUp(Account account) throws AddressException, MessagingException {
		Map<String, String> errors = new HashMap<>();
		Optional<Account> foundAccount = repository.findByAccountUsername(account.getAccount_username().trim());

		if (foundAccount != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(false,
					HttpStatus.CONFLICT.toString().split(" ")[0], responseMessage.get("userNameExisted"), ""));
		} else {
			Optional<Status> st = statusRepository.findById(1);
			account.setStatus(st.get());;

			if (account.getAccount_username().equals("")) {
				errors.put("emptyUserName", "Username can't be empty!");
			} else if (account.getAccount_username().length() < 6 || account.getAccount_username().length() > 100) {
				errors.put("usernameLength", "Username's length must be from 6 to 100!");
			}
			if (account.getAccount_password().equals("")) {
				errors.put("emptyPassword", "Password can't be empty!");
			} else if (account.getAccount_password().length() < 6 || account.getAccount_password().length() > 32) {
				errors.put("passwordLength", "Password's length must be from 6 to 32!");
			}

			if (errors.isEmpty()) {
				repository.save(account);
				account.setAccount_password(MD5.generateMD5Hash(account.getAccount_password()));
				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
						HttpStatus.OK.toString().split(" ")[0], responseMessage.get("signUpSuccess"), account));
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ResponseObject(false, HttpStatus.BAD_REQUEST.toString().split(" ")[0],
							responseMessage.get("signUpFailed"), errors.values()));
		}
	}
	
//	public ResponseEntity<ResponseObject> signUp(Map<String, String> map) {
//		String username = map.get("username").strip();
//		String password = map.get("password").strip();
//		String name = map.get("name").strip();
//		String thumbnail = map.get("thumbnail").strip();
//		String birthday = map.get("birthday").strip();
//		String phone = map.get("phone").strip();
//		String email = map.get("email").strip();
//		String address = map.get("address").strip();
//		if (username == "" || password == "" || name == "" || birthday == "" || phone == "" || email == ""
//				|| address == "")
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "input can not be empty", new int[0]));
//		if (username.length() > 49 || password.length() > 19 || name.length() > 255 || address.length() > 255)
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "input too long", new int[0]));
//		if (username.length() < 6 || password.length() < 6)
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "input too short", new int[0]));
//		Optional<Account> checkAccount = repository.findByAccountUsername(username);
//		if (!checkAccount.isEmpty())
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "username has already existed", new int[0]));
//
//		if (!validationHelper.isValidEmail(email))
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "wrong email", new int[0]));
//		if (!validationHelper.isVietnamPhoneNumber(phone))
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "wrong phone number", new int[0]));
//		String dateFormat = "MM-dd-yyyy";
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
//		LocalDate date;
//		try {
//			date = LocalDate.parse(birthday, formatter);
//		} catch (DateTimeParseException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], "date not exist", new int[0]));
//		}
//		Account account = new Account();
//		AccountProfile profile = new AccountProfile();
//		account.setAccount_username(username);
//		account.setAccount_password(md5generate.generateMD5Hash(password));
//		Optional<Role> role = rolerepository.findById(0);
//		if (role.isPresent()) {
//			account.setRole(role.get());
//		}
//		account.setStatus(statusRepository.findById(7).get());
//		account = repository.save(account);
//
//		profile.setAccount_profile_address(address);
//		profile.setAccount_profile_thumbnail(thumbnail);
//		profile.setAccount_profile_birthday(java.sql.Date.valueOf(date));
//		profile.setAccount_profile_email(email);
//		profile.setAccount_profile_name(name);
//		profile.setAccount_profile_phone(phone);
//		profile = profileRepository.save(profile);
//
//		account = repository.save(account);
//
//		return ResponseEntity.status(HttpStatus.OK)
//				.body(new ResponseObject(true, HttpStatus.OK.toString().split(" ")[0], "sing up successful", account));
//	}

//	public ResponseEntity<ResponseObject> signOut(HttpServletRequest httpRequest) {
//		Account account = repository.findByToken(httpRequest.getHeader("Authorization").split(" ")[1]);
//		if (account != null) {
//			account.setToken(null);
//			repository.save(account);
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
//					HttpStatus.OK.toString().split(" ")[0], responseMessage.get("signOutSuccess"), ""));
//		}
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
//				HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("signOutFailed"), ""));
//	}
//
//	public ResponseEntity<ResponseObject> signIn(Account account) {
//		Account acc = repository.findByUsername(account.getUsername().trim());
//		Map<String, String> errors = new HashMap<>();
//		Map<String,String> object = new HashMap<>();
//
//		if (account.getUsername().equals("")) {
//			errors.put("emptyUserName", "Username can't be empty!");
//		} else if (account.getUsername().length() < 6 || account.getUsername().length() > 100) {
//			errors.put("usernameLength", "Username's length must be from 6 to 100!");
//		}
//		if (account.getPassword().equals("")) {
//			errors.put("emptyPassword", "Password can't be empty!");
//		} else if (account.getPassword().length() < 6 || account.getPassword().length() > 32) {
//			errors.put("passwordLength", "Password's length must be from 6 to 32!");
//		}
//		if (errors.isEmpty()) {
//			if (acc == null) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
//						HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("accountNotFound"), ""));
//			} else {
//				if (MD5.generateMD5Hash(account.getPassword()).equals(acc.getPassword())) {
//					if (acc.getStatus() == 1) {
//						String jwtToken = jwtService.generateToken(acc);
//						object.put("token", jwtToken);
//						object.put("username",acc.getUsername());
//						return ResponseEntity.status(HttpStatus.OK)
//								.body(new ResponseObject(true, HttpStatus.OK.toString().split(" ")[0],
//										responseMessage.get("signInSuccess"), object));
//					} else {
//						return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//								.body(new ResponseObject(false, HttpStatus.UNAUTHORIZED.toString().split(" ")[0],
//										responseMessage.get("accountNotActivate"), ""));
//					}
//				} else {
//					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//							HttpStatus.BAD_REQUEST.toString().split(" ")[0], responseMessage.get("signInFailed"), ""));
//				}
//			}
//		} else {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(new ResponseObject(false, HttpStatus.BAD_REQUEST.toString().split(" ")[0],
//							responseMessage.get("signInError"), errors.values()));
//		}
//
//	}
//


//	public ResponseEntity<ResponseObject> signUp(Account account) throws AddressException, MessagingException {
//		Account foundAccount = repository.findByUsername(account.getUsername().trim());
//
//		if (foundAccount != null) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(false,
//					HttpStatus.CONFLICT.toString().split(" ")[0], responseMessage.get("userNameExisted"), ""));
//		} else {
//			String Phonereg = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
//			String Mailreg = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
//			String Pwdreg = "(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9]).*$";
//			Map<String, String> errors = new HashMap<>();
//			Optional<Role> role = roleRepository.findById(0);
//			if (role.isPresent()) {
//				account.setRole(role.get());
//			}
//			account.setStatus(0);
//			account.setWallet(0.0);
//			Optional<Level> level = levelRepository.findById(0);
//			if (role.isPresent()) {
//				account.setLevel(level.get());
//			}
//
//			if (account.getUsername().equals("")) {
//				errors.put("emptyUserName", "Username can't be empty!");
//			} else if (account.getUsername().length() < 6 || account.getUsername().length() > 100) {
//				errors.put("usernameLength", "Username's length must be from 6 to 100!");
//			}
//			if (account.getPassword().equals("")) {
//				errors.put("emptyPassword", "Password can't be empty!");
//			} else if (account.getPassword().length() < 6 || account.getPassword().length() > 32) {
//				errors.put("passwordLength", "Password's length must be from 6 to 32!");
//			} else if (!account.getPassword().matches(Pwdreg)) {
//				errors.put("wrongPwd",
//						"Password must contain at least one digit, one uppercase letter and one lowercase letter!");
//			}
//			if (account.getAccountProfile().getAddress().equals("")) {
//				errors.put("emptyAddress", "Address can't be empty!");
//			}
//			if (account.getAccountProfile().getEmail().equals("")) {
//				errors.put("emptyEmail", "Email can't be empty!");
//			} else if (!account.getAccountProfile().getEmail().matches(Mailreg)) {
//				errors.put("wrongMail", "Mail invalid!");
//			}
//			if (account.getAccountProfile().getName().equals("")) {
//				errors.put("emptyName", "Name can't be empty!");
//			}
//			if (account.getAccountProfile().getPhone().equals("")) {
//				errors.put("emptyPhone", "Username can't be empty!");
//			} else if (!account.getAccountProfile().getPhone().matches(Phonereg)) {
//				errors.put("wrongPhone", "Phone invalid!");
//			}
//
//			if (errors.isEmpty()) {
//				repository.save(account);
//				MimeMessage message = mailSender.createMimeMessage();
//				message.setFrom(new InternetAddress("mappe.help@gmail.com"));
//				message.setRecipients(MimeMessage.RecipientType.TO, account.getAccountProfile().getEmail());
//				message.setSubject("Email for sign up account");
//				String htmlContent = "<h2>Welcome to Mappe!</h2>"
//						+ "<img src=\"https://i.imgur.com/3lYeErR.png\" alt=\"Mappe Shop\" style=\"display: block; margin: 0 auto; width: 150px;\">"
//						+ "<div style='background-color: #f2f2f2; padding: 20px; text-align: center;'>" + "<p>Hello, "
//						+ account.getAccountProfile().getName() + "</p>"
//						+ "<p>Thank you for submitting an account registration request on Mappe!</p>"
//						+ "<p>Please do not share the link with anyone else to avoid losing your account.</p>"
//						+ "<p>Please click this " + "<a href=\"http://localhost:8080/api/v1/auth/confirm-sign-up/"
//						+ account.getID() + "\">" + "<p style='font-size: 24px;'><strong>confirm sign-up</strong></p>"
//						+ "</a> to activate your account.</p>" + "</div>";
//				message.setContent(htmlContent, "text/html; charset=utf-8");
//				mailSender.send(message);
//				account.setPassword(MD5.generateMD5Hash(account.getPassword()));
//				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
//						HttpStatus.OK.toString().split(" ")[0], responseMessage.get("signUpSuccess"), account));
//			}
//
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(new ResponseObject(false, HttpStatus.BAD_REQUEST.toString().split(" ")[0],
//							responseMessage.get("signUpFailed"), errors.values()));
//		}
//	}

//	public String confirmSignup(int id) {
//		Optional<Account> account = repository.findById(id);
//		account.get().setPassword(MD5.generateMD5Hash(account.get().getPassword()));
//		if (account.get().getStatus() == 0) {
//			account.get().setStatus(1);
//			repository.save(account.get());
//			return responseMessage.get("confirmSignUp");
//		}
//		return responseMessage.get("confirmSignUpFailed");
//	}
//
//	public ResponseEntity<ResponseObject> checkus(Account account) throws AddressException, MessagingException {
//		if (!account.getUsername().equals("")) {
//			pwd = repository.findByUsername(account.getUsername().trim());
//			if (pwd == null) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,
//						HttpStatus.NOT_FOUND.toString().split(" ")[0], responseMessage.get("userNameNotFound"), ""));
//			} else {
//				Random random = new Random();
//				randomWithNextInt = String.format("%04d", Integer.valueOf(random.nextInt(9999)));
//				MimeMessage message = mailSender.createMimeMessage();
//				message.setFrom(new InternetAddress("mappe.help@gmail.com"));
//				message.setRecipients(MimeMessage.RecipientType.TO, pwd.getAccountProfile().getEmail());
//				message.setSubject("Reset password instructions");
//				String htmlContent = "<h2>Welcome to Mappe!</h2>"
//						+ "<img src=\"https://i.imgur.com/3lYeErR.png\" alt=\"Mappe Shop\" style=\"display: block; margin: 0 auto; width: 150px;\">"
//						+ "<div style='background-color: #f2f2f2; padding: 20px; text-align: center;'>" + "<p>Hello, "
//						+ pwd.getAccountProfile().getName() + "</p>"
//						+ "<p>Someone, hopefully you, has requested to reset the password for your account on Mappe!</p>"
//						+ "<p>Please do not share the code with anyone else to avoid losing your account.</p>"
//						+ "<p>The code is: </p>" + "<p style='font-size: 24px;'><strong>" + randomWithNextInt
//						+ "</strong></p>" + "</div>";
//				message.setContent(htmlContent, "text/html; charset=utf-8");
//				mailSender.send(message);
//				return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
//						HttpStatus.OK.toString().split(" ")[0], responseMessage.get("checkMail"), ""));
//
//			}
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//				HttpStatus.BAD_REQUEST.toString().split(" ")[0], responseMessage.get("userNameEmpty"), ""));
//	}
//
//	public ResponseEntity<ResponseObject> checkOtpForgot(Map<String, String> obj) {
//		if (obj.get("otp").equals("")) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//					HttpStatus.BAD_REQUEST.toString().split(" ")[0], responseMessage.get("OtpEmpty"), ""));
//		}
//		if (obj.get("otp").equals(randomWithNextInt)) {
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
//					HttpStatus.OK.toString().split(" ")[0], responseMessage.get("OtpCorrect"), ""));
//		}
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false,
//				HttpStatus.BAD_REQUEST.toString().split(" ")[0], responseMessage.get("OtpWrong"), ""));
//	}
//
//	public ResponseEntity<ResponseObject> resetPassword(Map<String, String> obj) {
//		Map<String, String> errors = new HashMap<>();
//		String Pwdreg = "(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9]).*$";
//
//		if (!obj.get("otp").equals(randomWithNextInt)) {
//			errors.put("wrongOtp", "Otp is wrong!");
//		} else {
//			if (obj.get("username").equals("")) {
//				errors.put("emptyUserName", "Username can't be empty!");
//			}
//
//			if (obj.get("password").equals("")) {
//				errors.put("emptyPassword", "Password can't be empty!");
//			}
//
//			if (errors.isEmpty()) {
//				if (obj.get("username").length() < 6 || obj.get("username").length() > 100) {
//					errors.put("usernameLength", "Username's length must be from 6 to 100!");
//				} else if (repository.findByUsername(obj.get("username")) == null) {
//					errors.put("AccountNotFound", "Account does not existed!");
//				} else {
//					if (obj.get("password").length() < 6 || obj.get("password").length() > 32) {
//						errors.put("passwordLength", "Password's length must be from 6 to 32!");
//					} else if (!obj.get("password").matches(Pwdreg)) {
//						errors.put("wrongPwd",
//								"Password must contain at least one digit, one uppercase letter and one lowercase letter!");
//					}
//				}
//			}
//		}
//
//		if (errors.isEmpty()) {
//			Account acc = repository.findByUsername(obj.get("username"));
//			acc.setPassword(MD5.generateMD5Hash(obj.get("password")));
//			repository.save(acc);
//			randomWithNextInt = "";
//			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true,
//					HttpStatus.OK.toString().split(" ")[0], responseMessage.get("resetPwdSuccess"), ""));
//		} else {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(new ResponseObject(false, HttpStatus.BAD_REQUEST.toString().split(" ")[0],
//							responseMessage.get("resetPwdFailed"), errors.values()));
//		}
//	}

}