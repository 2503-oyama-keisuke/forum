package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class ForumController {
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;
    @Autowired
    HttpSession session;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(@RequestParam(name="start", required = false) String start,@RequestParam(name="end",  required = false) String end) throws ParseException {
        ModelAndView mav = new ModelAndView();
        CommentForm commentForm = new CommentForm();
        // 投稿を全件取得
        List<ReportForm> contentData = reportService.findAllReport(start, end);
        List<CommentForm> commentData = commentService.findAllComment();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを保管
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentData);
        mav.addObject("formModel", commentForm);

        if(!CollectionUtils.isEmpty((Collection<?>) session.getAttribute("errorMessages"))){
            mav.addObject("errorMessages", session.getAttribute("errorMessages"));
        }
        session.invalidate();

        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult result) throws ParseException {

        if(result.hasErrors()){
            return new ModelAndView("/new");
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        reportService.deleteReport(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        ReportForm report = reportService.editReport(id);
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    @PutMapping("/update/{id}")
    public ModelAndView updateContent(@PathVariable Integer id, @ModelAttribute("formModel") @Validated ReportForm report, BindingResult result) throws ParseException {
        if(result.hasErrors()){
            return new ModelAndView("/edit");
        }
        report.setId(id);
        // 投稿をテーブルに格納
        reportService.saveReport(report);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @PostMapping("/addComment/{id}")
    public ModelAndView addComment(@PathVariable Integer id, @ModelAttribute("formModel") @Validated CommentForm commentForm, BindingResult result) throws ParseException {

        if(result.hasErrors()){
            List<String> errorMessages = new ArrayList<>();
            String errorMessage;
            String errorField;
            for(FieldError error : result.getFieldErrors()){
                errorMessage = error.getDefaultMessage();
                errorMessages.add(errorMessage);
            }
            session.setAttribute("errorMessages",errorMessages);
            return new ModelAndView("redirect:/");
        }

        commentForm.setReportId(id);
        ReportForm reportForm = new ReportForm();
        reportForm.setId(id);
        // 投稿をテーブルに格納
        commentService.saveComment(commentForm);
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/editComment/{id}")
    public ModelAndView editComment(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        CommentForm comment = commentService.editComment(id);
        mav.addObject("formModel", comment);
        // 画面遷移先を指定
        mav.setViewName("/editComment");
        return mav;
    }

    @PutMapping("/updateComment/{id}/{reportId}/{createdDate}")
    public ModelAndView updateComment(@PathVariable Integer id, @PathVariable Integer reportId, @ModelAttribute("formModel") @Validated CommentForm comment, BindingResult result) throws ParseException {
        if(result.hasErrors()){
            return new ModelAndView("/editComment");
        }
        comment.setId(id);
        comment.setReportId(reportId);
        // 投稿をテーブルに格納
        commentService.saveComment(comment);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    @DeleteMapping("/deleteComment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        // 投稿をテーブルに格納
        commentService.deleteComment(id);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }
}