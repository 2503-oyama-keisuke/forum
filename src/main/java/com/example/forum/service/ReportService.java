package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findAllReport(String start, String end) throws ParseException {
        if (!StringUtils.isBlank(start)) {
            start += " 00:00:00.000";
        } else {
            start = "2020-01-01 00:00:00.000";
        }

        if (!StringUtils.isBlank(end)) {
            end += " 23:59:59.999";
        } else {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String nowDate = sdf.format(now);
            end = nowDate;
        }

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date startDate = sdFormat.parse(start);
        Date endDate = sdFormat.parse(end);


        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(startDate, endDate);
        List<ReportForm> reports = setReportForm(results);
        return reports;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) throws ParseException {
        Report saveReport = setReportEntity(reqReport);
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) throws ParseException {
        Report report = new Report();
        report.setId(reqReport.getId());
        if(reqReport.getContent() != null) {
            report.setContent(reqReport.getContent());
        }else{

        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String nowDate = sdf.format(now);
        Date nowDateTime = sdf.parse(nowDate);
        report.setUpdatedDate(nowDateTime);
        return report;
    }

    public void deleteReport(Integer id){
        Report deleteReport = new Report();
        deleteReport.setId(id);
        reportRepository.delete(deleteReport);
    }

    public ReportForm editReport(Integer id){
        List<Report> results = new ArrayList<>();
        results.add((Report) reportRepository.findById(id).orElse(null));
        List<ReportForm> reports = setReportForm(results);
        return reports.get(0);
    }
}