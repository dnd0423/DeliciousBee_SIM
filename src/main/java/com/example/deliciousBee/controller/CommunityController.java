package com.example.deliciousBee.controller;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.example.deliciousBee.service.*;
import com.example.deliciousBee.util.FileService;
import com.example.deliciousBee.util.ForumFileService;
import com.example.deliciousBee.model.community.ForumAttachedFile;
import com.example.deliciousBee.util.PageNavigator;
import com.example.deliciousBee.*;
import com.example.deliciousBee.*;
import com.example.deliciousBee.model.community.CommunityBoard;
import com.example.deliciousBee.model.community.CommunityUpdateForm;
import com.example.deliciousBee.model.community.CommunityWriteForm;
import com.example.deliciousBee.model.member.BeeMember;
import com.mysql.cj.Session;


@Controller
@Slf4j
@RequestMapping("community")
@RequiredArgsConstructor
public class CommunityController {
	
	@Value("${file.upload.path}")
    private String uploadPath;
	
	//페이징 처리를 위한 상수값
	private final int countPerPage = 10;
	private final int pagePerGroup = 5;
	
	private final CommunityService communityService;
	private final ForumFileService fileService;
	
	@GetMapping("select")
	public String select(Model model , @SessionAttribute(name = "loginMember", required = false) BeeMember loginMember) {
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		return "community/select";
	}
	
	@GetMapping("list")
	public String list(Model model,@SessionAttribute(name = "loginMember", required = false) BeeMember loginMember
					,@RequestParam(name="page", defaultValue="1") int page
					,@PageableDefault(size=10,sort="id",direction = Sort.Direction.ASC) Pageable pageable
					,@RequestParam(name="searchText", defaultValue="") String searchText) {
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		//검색어가 있을 때
		if(!searchText.equals("")) {
			Page<CommunityBoard> searchList = communityService.findSearch(searchText, pageable);
			model.addAttribute("boardList" ,searchList);
			//검색 결과 수
			int totalRecordsCountBySearch = (int)searchList.getTotalElements();
			//검색 결과 페이지 수
			int totalPageCountBySearch = searchList.getTotalPages();
					
			PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, totalRecordsCountBySearch, totalPageCountBySearch);
					
			model.addAttribute("navi", navi);
			model.addAttribute("searchText", searchText);
					
			return "community/list";
		}
		
		//1. DB에서 데이터를 가져오고
		Page<CommunityBoard> boardList = communityService.findAll(pageable);
		log.info("글 목록: {}", boardList);
		//2. 모델에 담기
		model.addAttribute("boardList", boardList);
		//3. list.html에서 루프 돌려서 실제 페이지에 출력
				
		//전체 글 수
		int totalRecordsCount = (int) boardList.getTotalElements();
				
		//전체 페이지 수
		int totalPageCount = boardList.getTotalPages();
				
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, totalRecordsCount, totalPageCount);
				
		model.addAttribute("navi", navi);
		
		
		
		return "community/list";
	}
	
	@GetMapping("write")
	public String write(Model model,@SessionAttribute(name = "loginMember") BeeMember loginMember) {
		
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		
		model.addAttribute("writeForm",new CommunityWriteForm());
		
		
		
		
		return "community/write";
	}
	
	@PostMapping("write")
	public String write(@Validated @ModelAttribute("writeForm") CommunityWriteForm communityWriteForm
						, BindingResult result
						,  @SessionAttribute(name = "loginMember") BeeMember loginMember
						, @RequestParam(name="file", required=false) MultipartFile file) {
		
		log.info("{}", loginMember);
		
		if(result.hasErrors()) {
			return "community/write";
		}
		
		CommunityBoard communityBoard = CommunityWriteForm.toCommunityBoard(communityWriteForm);
		
		communityBoard.setMember(loginMember);
		
				//첨부파일이 있는 경우
				if(!file.isEmpty() && file != null) {
					 try {
				            // 파일 저장 경로 지정 (프로퍼티에서 가져온 경로를 사용)
				 
				            String originalFileName = file.getOriginalFilename();
				            String savedFileName = System.currentTimeMillis() + "_" + originalFileName; // 파일명 중복 방지
				            File targetFile = new File(uploadPath, savedFileName);
				            file.transferTo(targetFile);  // 파일을 서버에 저장

				            // 첨부파일 객체 생성
				            ForumAttachedFile attachedFile = new ForumAttachedFile();
				            attachedFile.setOriginal_filename(originalFileName);
				            attachedFile.setSaved_filename(savedFileName);
				            attachedFile.setFile_size(file.getSize()); // 파일 크기 설정
				            attachedFile.setCommunityBoard(communityBoard);

				            // 파일 정보와 게시물 정보를 DB에 저장
				            communityService.saveBoard(communityBoard, attachedFile);
				        } catch (IOException e) {
				            e.printStackTrace();
				            // 파일 저장 중 오류 발생시 예외 처리
				            return "community/write";
				        }
				    } else {
				        // 첨부파일이 없는 경우
				        communityService.saveBoard(communityBoard, null);
				    }

				    return "redirect:/community/list";
	}
	
	@GetMapping("read")
	public String read(@RequestParam(name = "id", required = false) Long id
						, Model model
						, @SessionAttribute(name = "loginMember" , required = false)BeeMember loginMember) {
		
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		CommunityBoard communityBoard = communityService.findById(id);
		
		if(communityBoard == null) {
			return "redirect:/community/list";
		}
		
		communityBoard.addHit();
		
		communityService.updateBoard(communityBoard, false , null);
		
		model.addAttribute("board", communityBoard);
		
		//첨부파일 있는지 찾기
		ForumAttachedFile attachedFile = communityService.findFileByBoardId(communityBoard);
		log.info("첨부파일:{}", attachedFile);
				
		//첨부파일이 있으면
		if(attachedFile != null) {
			String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
			
			try {
				 String mimeType = Files.probeContentType(new File(fullPath).toPath());
				 boolean isImage = mimeType != null && mimeType.startsWith("image");
				 
				 model.addAttribute("file", attachedFile);
			     model.addAttribute("isImage", isImage);
				 
			     
			     if (isImage) {
		                // 이미지 파일 URL 생성
		                String imageUrl =  "/uploads/" + attachedFile.getSaved_filename();
		                model.addAttribute("imageUrl", imageUrl);
		         }else {
		                // 이미지가 아닌 경우 다운로드 링크 생성
		                String downloadUrl = "/community/download/" + attachedFile.getAttachedFile_id();
		                log.info("다운로드 URL: {}", downloadUrl);
		                model.addAttribute("downloadUrl", downloadUrl);
		            }
				 
			
			}catch(IOException ex) {
				 log.error("파일 MIME 타입을 확인하는 동안 오류가 발생했습니다: {}", ex.getMessage(), ex);
		         model.addAttribute("isImage", false); // 오류 시 기본값으로 처리
			
			}
			
			
		}
		
		
		
		
		return "community/read";
	}
	
	@GetMapping("delete")
	public String remove(@RequestParam(name = "id", required = false) Long id
						, @SessionAttribute(name = "loginMember" , required = false)BeeMember loginMember) {
		
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		CommunityBoard communityBoard = communityService.findById(id);
		
		if(communityBoard == null || !communityBoard.getMember().getMember_id().equals(loginMember.getMember_id())) {
			return "redirect:/community/list";
		}
		
		communityService.deleteById(id);
		
		return "redirect:/community/list";
	}
	
	@GetMapping("update")
	public String updateForm(@RequestParam(name = "id" , required = false) Long id, Model model
			 , @SessionAttribute(name = "loginMember" , required = false)BeeMember loginMember) {
		
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		
		CommunityBoard communityBoard = communityService.findById(id);
	
		if(communityBoard == null || !communityBoard.getMember().getMember_id().equals(loginMember.getMember_id())) {
			return "redirect:/community/list";
		}
		
		CommunityUpdateForm communityUpdateForm = CommunityBoard.toUpdateForm(communityBoard);
		
		model.addAttribute("updateForm", communityUpdateForm);
		
		//첨부파일 체크
		ForumAttachedFile attachedFile = communityService.findFileByBoardId(communityBoard);
				
		if(attachedFile != null) {
			model.addAttribute("file", attachedFile);
		}
		
		return "community/update";
		
		
	}
	
	@PostMapping("update")
	public String update(@Validated @ModelAttribute("updateForm") CommunityUpdateForm communityUpdateForm
 			 			, BindingResult result
 			 			,  @RequestParam(name="file", required=false) MultipartFile file) {
		
		if(result.hasErrors()) {
			return "community/write";
		}
		
		CommunityBoard communityBoard = CommunityUpdateForm.toCommunityBoard(communityUpdateForm);
		
		communityService.updateBoard(communityBoard, communityUpdateForm.isFileRemoved(), file);
		
		
		return "redirect:/community/list";
		
		
		
	}
	
	@GetMapping("download/{id}")
	public ResponseEntity<Resource> download(@PathVariable("id") Long id) throws MalformedURLException{
		//첨부파일 아이디로 첨부파일 정보를 가져온다.
		ForumAttachedFile attachedFile = communityService.findFileByAttachedFileId(id);
		//다운로드 하려는 파일의 절대경로 값을 만든다.
		String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
		
		UrlResource resource = new UrlResource("file:" + fullPath);
		
		//한글 파일명이 깨지지 않도록 UTF_8로 파일명을 인코딩한다.
		String encodingFileName = UriUtils.encode(attachedFile.getOriginal_filename(), 
												  StandardCharsets.UTF_8);
		//응답 헤더에 담을 Content Disposition 값을 생성
		String contentDisposition = "attachment; filename=\"" + encodingFileName + "\"";
		
		return ResponseEntity.ok()
				             .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				             .body(resource);
	}
	
	
}
