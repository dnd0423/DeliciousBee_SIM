package com.example.deliciousBee.service;

import java.util.List;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.example.deliciousBee.model.community.*;
import com.example.deliciousBee.repository.CommunityBoardRepository;
import com.example.deliciousBee.repository.FileRepository;
import com.example.deliciousBee.repository.ForumFileRepository;
import com.example.deliciousBee.util.FileService;
import com.example.deliciousBee.util.ForumFileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {
	
	@Value("${file.upload.path}")
    private String uploadPath;
	
	private final CommunityBoardRepository communityRepository;
	private final ForumFileRepository fileRepository;
	private final ForumFileService ForumFileService;
//	private final ForumFileRepository ForumFileRepository;
	
	//게시물 등록
	@Transactional
	public void saveBoard(CommunityBoard board,  ForumAttachedFile attachedFile) {
		//첨부파일 있으면
		if(attachedFile != null) {
			communityRepository.save(board);
			fileRepository.save(attachedFile);
		}
		//첨부파일 없으면
		else {
			communityRepository.save(board);
		}
	}
	
	//게시글 전체 목록
	public Page<CommunityBoard> findAll(Pageable pageable) {
		Page<CommunityBoard> page = communityRepository.findAll(pageable);
		return page;
	}
	
	public CommunityBoard findById(Long id) {
		return communityRepository.findById(id).orElse(null);
	}
	
	public void deleteById(Long id) {
		communityRepository.deleteById(id);
	}
	
//	public CommunityBoard findBoard(String title) {
//		Optional<CommunityBoard> communityBoard = communityRepository.findByTitle(title);
//		
//		return communityBoard.orElseThrow(() -> new RuntimeException("No board found with title: " + title));
//	}
	
	//게시물 수정
		@Transactional
		public void updateBoard(CommunityBoard updateBoard, boolean isFileRemoved, MultipartFile file) {
			CommunityBoard findBoard = findById(updateBoard.getId());
			
			findBoard.setTitle(updateBoard.getTitle());
			findBoard.setContent(updateBoard.getContent());
			findBoard.setHit(updateBoard.getHit());
			
			//첨부파일 체크
			ForumAttachedFile attachedFile = findFileByBoardId(findBoard);
			if(attachedFile != null && (isFileRemoved || (file != null && file.getSize() > 0) )) {
				//첨부파일 삭제 (서버에서도 지우고 DB에서도 지우고)
				//파일 삭제를 요청했거나 새로운 파일이 업로드 되면 기존 파일을 삭제
				removeFile(attachedFile);
				
			}
			
			//새로 저장할 파일이 있으면
			if(file != null && file.getSize() > 0) {
				//첨부파일을 서버에 저장한다.
				ForumAttachedFile savedFile = ForumFileService.saveFile(file);
				//데이터베이스에 저장될 보드 세팅
				savedFile.setCommunityBoard(findBoard);
				//첨부파일 내용을 데이터베이스에 저장
				saveBoard(findBoard, savedFile);
			}
			
			communityRepository.save(findBoard);
		}
	
		//게시물 삭제
		@Transactional
		public void removeBoard(CommunityBoard board) {
			//첨부파일 삭제
			ForumAttachedFile attachedFile = findFileByBoardId(board);
			if(attachedFile != null) {
				removeFile(attachedFile);
			}
			
			
			//게시글 삭제
			communityRepository.deleteById(board.getId());
		}
	
	//첨부파일 삭제
	public void removeFile(ForumAttachedFile attachedFile) {
			//DB에서 삭제
			fileRepository.deleteById(attachedFile.getAttachedFile_id());
			//서버(로컬)에서 삭제
			String fullPath = uploadPath + "/" + attachedFile.getSaved_filename();
			ForumFileService.deleteFile(fullPath);
	}
	
	public ForumAttachedFile findFileByBoardId(CommunityBoard board) {
		ForumAttachedFile attachedFile = fileRepository.findByCommunityBoard(board);
		return attachedFile;
	}

	public ForumAttachedFile findFileByAttachedFileId(Long id) {
		ForumAttachedFile attachedFile = fileRepository.findById(id).get();
		return attachedFile;
	}

	public int getTotal() {
		return (int)communityRepository.count();
	}

	public Page<CommunityBoard> findSearch(String searchText, Pageable pageable) {
		Page<CommunityBoard> searchList = communityRepository.findByTitleContaining(searchText, pageable);
		return searchList;
	}
	
	
	
}
