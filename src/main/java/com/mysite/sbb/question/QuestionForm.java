package com.mysite.sbb.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {

    @NotBlank(message = "제목은 필수항목입니다.")
    @Size(max = 200, message = "제목은 200자 이하입니다.")
    private String subject;

    @NotBlank(message = "내용은 필수항목입니다.")
    @Size(min = 5, message = "내용은 5자 이상입니다.")
    private String content;
}
