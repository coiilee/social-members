package kh.edu.social_members.controller;

import kh.edu.social_members.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class KakaoController {
    //Kakao Dev application name = Myapp
    //WEB URI      = http://localhost:8085/
    //Redirect URI = http://localhost:8085/login/oauth2/code/kakao

    // 필수 동의 항목 : 프로필정보(닉네임) , 이름, 이메일
    // 추가 동의 항목 : 프로필 사진, 성별

//    @Value("${kakao.client-id}")
//    private String kakaoClientId;

//    @GetMapping("/kakao/callback")
//    public String kakaoLogin(Model model) {
//
//    }

    //${변수이름} 은 application.properties 나 config.properties 에 작성한 변수 이름 가져오기
    //변수이름에 해당하는 값을 불러오기

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Autowired
    private MemberServiceImpl memberService;

    /* config.properties에서 kakao.redirect-uri를 아래처럼 직접적으로 가져올 수 있음.
    private String kakao.redirect-uri=http://localhost:8080/oauth/kakao/callback

    하지만, java-spring 자체에서 보안을 가장 중요하게 생각하기 때문에
    아이디, 비밀번호와 같은 중요한 정보는 properties 파일로 나누어서
    @Value 값으로 호출해서 사용할 수 있도록 분류해주는 것이 바람직함.

     */
    @Value("${kakao.redirect-uri}") // = ${REDIRECT-URI}
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> getKakaoLoginUrl() { //ResponseEntity<?>는 작성을 안해도 됨.
        // 현재 제대로 진행되고 있는지 상태 확인일 뿐.

        // 카카오톡 개발 문서에서 카카오로그인 > 예제 > 요청에 작성된 주소를 그대로 가져온 상태
        String url = "https://kauth.kakao.com/oauth/authorize?response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri;
        return ResponseEntity.ok(url);
    }

    //kakao.redirect-uri=http://localhost:8080/oauth/kakao/callback
    // 위에서 login 할건지 말건지 정해서 로그인 할거면 토큰을 주는것임
    @GetMapping("/login/oauth2/code/kakao")    //oauth/kakao/callback
    public String handleCallback(@RequestParam String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();

        // 카카오 개발자 문서에 헤더 요청,설명 제공했기 때문에 필수 작성한 것
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");//charset=utf-8 작성 안할경우 한글 깨짐

        // 카카오 개발자 문서 내 본문에 제공된 이름,타입,설명에 맞는 코드 작성한 것 (grant_type, client_id, redirect_uri, code 등)
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        if (kakaoClientSecret != null) {
            params.add("client_secret", kakaoClientSecret);
        }

        // 개발자 문서 내 응답 (HttpEntity로 주로 사용됨)
        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 개발자 문서 내 응답 > 본문에 제공된 이름,타입,설명 필수 부분 작성한 것
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        String accessToken = (String) response.getBody().get("access_token");

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.postForEntity(userInfoUrl, userRequest, Map.class);

        // 여기 아래부터 프로젝트에 맞게 카카오에서 가져올 값 수정해서 사용
        Map userInfo = userResponse.getBody();
        System.out.println("==============Controller : userinfo=====================");
        System.out.println(userInfo);

        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        String nickname = (String) properties.get("nickname");
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);

        String profileImage = (String) properties.get("profile_image");

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) kakaoAccount.get("name");

        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8); //한글 깨짐 방지
        String gender = (String) kakaoAccount.get("gender");




        // 키-값 받아오기 위해 키-값 시작을 알리는 것은 '?' 기호를 사용함
        // 키-값 여러 값을 받아오고 전달할 경우는 '&' 기호로 키-값 다수 사용
        return "redirect:/signup?nickname=" + encodedNickname + "&email=" + email +
                "&name=" + encodedName + "&gender=" + gender
                + "&profile_image=" + profileImage;

        /* signup.html 에서 회원가입란을 작성하지 않고, 카카오 로그인 클릭 후 바로 db에 저장하는 방식

           예전에는 아래와 같은 방식을 주로 사용
           로그인하는 회사별로 사용하는 json 형식을 모두 파악
           service에서 개발자가 처리하는 로직에서 문제가 발생
           DB에 값이 제대로 넘어오지 않는 경우 존재 -> 소셜 변수명 변경, JSON 형식 변경했을 때
           memberService.insertMember(nickname, name, email, gender);
           return "DB에 저장완료";

         */


    }


}
