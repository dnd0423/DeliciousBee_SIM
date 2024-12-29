package com.example.deliciousBee.model.community;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.GenderType;
import com.example.deliciousBee.model.report.ReportReason;
import com.example.deliciousBee.model.review.AttachedFile;
import com.example.deliciousBee.model.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityBoard {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "bee_member_id")
	private BeeMember member;
	
	private Long hit;
	
	private LocalDateTime createdTime;
	
	@OneToMany(mappedBy = "communityBoard" , cascade = CascadeType.REMOVE)
	private List<ForumAttachedFile> attachedFile;
	
	public void addHit() {
		//조회수 증가
	    this.hit++;
	}
	
	public static CommunityUpdateForm toUpdateForm(CommunityBoard communityBoard) {
		CommunityUpdateForm communityUpdateForm = new CommunityUpdateForm();
		
		communityUpdateForm.setId(communityBoard.getId());
		communityUpdateForm.setTitle(communityBoard.getTitle());
		communityUpdateForm.setContent(communityBoard.getContent());
		communityUpdateForm.setMember(communityBoard.getMember());
		communityUpdateForm.setHit(communityBoard.getHit());
		communityUpdateForm.setCreatedTime(communityBoard.getCreatedTime());
		
		return communityUpdateForm;
		
		
		
		
		
	}
	
}
