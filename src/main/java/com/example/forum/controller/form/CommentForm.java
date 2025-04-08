package com.example.forum.controller.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {

    private int id;
    private String content;
    private int reportId;
}