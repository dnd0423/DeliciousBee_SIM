package com.example.deliciousBee.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.model.report.ReportReason;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.ReportRepository;
import com.example.deliciousBee.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
	
	private final ReportRepository reportRepository;
	private final ReviewRepository reviewRepository;
	
	// 신고 보내는 기능
	public boolean sendReport(Long reviewId, Report report) {
		try {
			Review findReview = reviewRepository.findById(reviewId).get();
			
			// 리뷰 유무 체크
			if(findReview == null) {
				return false;
			}
			
			// 신고 등록
			report.setReview(findReview);
			reportRepository.save(report);
			return true;
		} catch (Exception e) {
			log.error("신고 서버 제출에 실패하였습니다.", e);
			return false;
		}
	}

}
