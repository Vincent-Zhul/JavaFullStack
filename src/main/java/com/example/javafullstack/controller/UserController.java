package com.example.javafullstack.controller;

import com.example.javafullstack.UserRepository;
import com.example.javafullstack.entity.User;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.*;


import org.springframework.ui.Model;

import java.util.List;


@RequestMapping("/user")
@Controller
public class UserController {
    @PostConstruct
    public void init() {
        System.out.println("UserController init");
    }

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping("/sayhi")
    public String sayHi() {
        logger.trace("========trace========");
        logger.debug("========debug========");
        logger.info("========info========");
        logger.warn("========warn========");
        logger.error("========error========");
        return "index";
    }

//    @Autowired
//    private JdbcTemplate;
//
//    @RequestMapping(value = "/getusers", method=RequestMethod.GET)
//    @ResponseBody
//    public List<Map<String,Object>> getUsers() {
//        String sql = "SELECT * FROM user";
//        List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
//        return list;
//    }


    @Autowired
    private UserRepository userRepository;
    @GetMapping("/greeting")
    public String greetingForm(Model model) {
        model.addAttribute("user", new User());
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute User user, Model model) {
        logger.trace("================ here ================");
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setAge(user.getAge());
        newUser.setGender(user.getGender());
        newUser.setEmail(user.getEmail());
        newUser.setCity(user.getCity());
        userRepository.save(newUser);
        model.addAttribute("newUser", newUser); //result返回最新添加的数据
        return "result";
    }


    @GetMapping("/all")
    public String getMessage(Model model) {
        Iterable<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "all";
    }

    //访问Ask.html,直接用http://localhost:8089/Ask.html
    @Value("${GPT_KEY}")
    private String gptKey;

    @PostMapping("/ask")
    public String ChatGPT(@RequestParam String question, Model model) {
        String token = gptKey;
        System.out.println(question);
        OpenAiService service = new OpenAiService(token);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("text-davinci-003")
                .prompt(question)
                .temperature(0.5)
                .maxTokens(2048)
                .topP(1D)
                .frequencyPenalty(0D)
                .presencePenalty(0D)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);

        List<CompletionChoice> choiceList = service.createCompletion(completionRequest).getChoices();

        String answer = choiceList.get(0).getText();

        model.addAttribute("choices", answer);

        return "GPTAnswers";
    }
}
