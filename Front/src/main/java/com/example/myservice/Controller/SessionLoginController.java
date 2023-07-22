package com.example.myservice.Controller;
import com.example.myservice.DTO.*;
import com.example.myservice.Data.TemperatureData;
import com.example.myservice.Member.User;
import com.example.myservice.Member.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/session-login")
public class SessionLoginController {

    private final UserService userService;
    private final TemperatureDataRepository temperatureDataRepository;
    private final PhotoDataRepository photoDataRepository;
    @GetMapping(value = {"", "/"})
    public String home(Model model, @SessionAttribute(name = "userId", required = false) Long userId) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser != null) {
            model.addAttribute("nickname", loginUser.getNickname());
        }

        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        model.addAttribute("joinRequest", new JoinRequest());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute JoinRequest joinRequest, BindingResult bindingResult, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        // loginId 중복 체크
        if(userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.addError(new FieldError("joinRequest", "loginId", "로그인 아이디가 중복됩니다."));
        }
        // 닉네임 중복 체크
        if(userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.addError(new FieldError("joinRequest", "nickname", "닉네임이 중복됩니다."));
        }
        // password와 passwordCheck가 같은지 체크
        if(!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "바밀번호가 일치하지 않습니다."));
        }

        if(bindingResult.hasErrors()) {
            return "join";
        }

        userService.join(joinRequest);
        return "redirect:/session-login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
                        HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User user = userService.login(loginRequest);

        // 로그인 아이디나 비밀번호가 틀린 경우 global error return
        if(user == null) {
            bindingResult.reject("loginFail", "로그인 아이디 또는 비밀번호가 틀렸습니다.");
        }

        if(bindingResult.hasErrors()) {
            return "login";
        }

        // 로그인 성공 => 세션 생성

        // 세션을 생성하기 전에 기존의 세션 파기
        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);  // Session이 없으면 생성
        // 세션에 userId를 넣어줌
        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(1800); // Session이 30분동안 유지

        return "redirect:/session-login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        HttpSession session = request.getSession(false);  // Session이 없으면 null return
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/session-login";
    }

    @GetMapping("/info")
    public String userInfo(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/session-login/login";
        }

        model.addAttribute("user", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(@SessionAttribute(name = "userId", required = false) Long userId, Model model) {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");

        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/session-login/login";
        }

        if(!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/session-login";
        }

        return "admin";
    }
    @GetMapping("/mainData")
    public String mainDataPage(@SessionAttribute(name = "userId", required = false) Long userId, Model model){
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");
        User loginUser = userService.getLoginUserById(userId);

        if(loginUser == null) {
            return "redirect:/session-login/login";
        }

        return "mainData";
    }


    @GetMapping("/ledOn")
    public String ledOn(@SessionAttribute(name = "userId", required = false) Long userId, Model model) throws IOException {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");
        User loginUser = userService.getLoginUserById(userId);

        if (loginUser == null) {
            return "redirect:/session-login";
        }
        if (!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/session-login";
        }
        String Url = "http://165.246.116.17:80/red_led_on";
        URL controlURL = new URL(Url);
        HttpURLConnection conn = (HttpURLConnection) controlURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "text/plain");
        System.out.println("Response code: "+conn.getResponseCode());
        if(conn.getResponseCode() ==200){
            System.out.println("LED ON!");
        }

        conn.disconnect();
        return "admin";
    }
    @GetMapping("/ledOff")
    public String ledOff(@SessionAttribute(name = "userId", required = false) Long userId, Model model) throws IOException {
        model.addAttribute("loginType", "session-login");
        model.addAttribute("pageName", "세션 로그인");
        User loginUser = userService.getLoginUserById(userId);

        if (loginUser == null) {
            return "redirect:/session-login/login";
        }
        if (!loginUser.getRole().equals(UserRole.ADMIN)) {
            return "redirect:/session-login/login";
        }
        String Url = "http://165.246.116.17:80/red_led_off";
        URL controlURL = new URL(Url);
        HttpURLConnection conn = (HttpURLConnection) controlURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "text/plain");
        System.out.println("Response code: "+conn.getResponseCode());
        if(conn.getResponseCode() ==200){
            System.out.println("LED OFF!");
        }

        conn.disconnect();
        return "admin";
    }
}
