package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 // 질문 번호

    @Column(length = 200)
    private String subject;             // 제목

    @Column(columnDefinition = "TEXT")
    private String content;             // 내용

    private LocalDateTime createDate;   // 작성 일자

    private LocalDateTime modifyDate;   // 수정 일자

    // 답변 리스트
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;
}
