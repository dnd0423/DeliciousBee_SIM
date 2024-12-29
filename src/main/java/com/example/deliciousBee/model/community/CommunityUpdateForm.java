package com.example.deliciousBee.model.community;

import java.time.LocalDateTime;

import com.example.deliciousBee.*;
import com.example.deliciousBee.model.member.BeeMember;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CommunityUpdateForm {
	
	private Long id;
	
	@NotBlank(message = "제목을 입력해 주세요!")
	private String title;
	
	@NotBlank(message = "내용을 입력해 주세요!")
	private String content;
	
	private BeeMember member;
	
	private Long hit;
	
	private LocalDateTime createdTime;
	
	private boolean fileRemoved;
	
	public static CommunityBoard toCommunityBoard(CommunityUpdateForm communityUpdateForm) {
		CommunityBoard communityBoard = new CommunityBoard();
		
		communityBoard.setId(communityUpdateForm.getId());
		communityBoard.setTitle(communityUpdateForm.getTitle());
		communityBoard.setContent(communityUpdateForm.getContent());
		communityBoard.setHit(communityUpdateForm.getHit());
		communityBoard.setCreatedTime(communityUpdateForm.getCreatedTime());
		
		return communityBoard;
	}
	
}
