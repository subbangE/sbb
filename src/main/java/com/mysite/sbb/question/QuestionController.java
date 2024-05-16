package com.mysite.sbb.question;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService qService;

    @Autowired
    private UserService uService;

    @RequestMapping("/list")                                            // jpa에서 첫번째 페이지는 "0"
    public String questionList(Model model, @RequestParam(value = "page", defaultValue = "0") int page){
        Page<Question> paging = qService.getList(page);
        model.addAttribute("paging", paging);
        return "question_list";
    }

    //url 주소에 /{변수} => PathVariable 주소변수
    @RequestMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") int id,
                         AnswerForm answerForm){
        Question q = qService.getQuestionById(id);
        model.addAttribute("question", q);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm,
                         BindingResult result, Principal principal){
        if(result.hasErrors()){
            return "question_form"; //되돌아감
        }
        SiteUser siteUser = uService.getUser(principal.getName());
        //질문 저장하기
        qService.create(questionForm.getSubject(), questionForm.getContent(),siteUser);
        return "redirect:/question/list";
    }

    // 질문 수정하기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.qService.getQuestionById(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal){
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = qService.getQuestionById(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        qService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    // 삭제하기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id){
        Question question = qService.getQuestionById(id);
            // 작성자가 동일할 경우에만 질문 삭제 가능, 동일하지 않으면 삭제 권한이 없다는 예외를 발생 시킴
            // ResponseStatusException은 예외 발생시키는데 사용 => HTTP 상태 코드 400
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        qService.delete(question);
        return "redirect:/";
    }

    // 추천인 저장하기
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id){
        Question question = qService.getQuestionById(id);
        SiteUser siteUser = uService.getUser(principal.getName());
        qService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
