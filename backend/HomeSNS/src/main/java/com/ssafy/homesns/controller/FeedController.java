package com.ssafy.homesns.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ssafy.homesns.dto.FeedDto;
import com.ssafy.homesns.dto.FeedParamDto;
import com.ssafy.homesns.dto.FeedResultDto;
import com.ssafy.homesns.dto.GroupMemberDto;
import com.ssafy.homesns.dto.LocationFavoriteDto;
import com.ssafy.homesns.dto.MainFeedResultDto;
import com.ssafy.homesns.jwt.JwtFilter;
import com.ssafy.homesns.service.FeedService;

@CrossOrigin(origins = { "http://localhost:5500", "http://172.30.1.59:5500", "http://192.168.0.100:5500",
		"http://192.168.0.40:5500", "https://i6e205.p.ssafy.io" }, allowCredentials = "true", // axios가 sessionId를 계속 다른것을
																																													// 보내는데, 이것을 고정시켜준다
		allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
				RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.OPTIONS })

@RestController
@RequestMapping(value = "/api")
public class FeedController {

	@Autowired
	FeedService feedService;

	private static final int SUCCESS = 1;

	// 태현이가 만듦!!!!!!!!!!!!!
	// 현재 그룹의 feedList를 가져온다. -> mainPage
	// 파라미터로 받은 groupId를 feedMainPage로 넘겨주고 feedResultDto 값을 넘겨받는다.
	// feedResultDto에 feedList를 담아 리턴한다.
	@GetMapping(value = "/main")
	public ResponseEntity<MainFeedResultDto> mainPage(FeedParamDto feedParamDto) {

		MainFeedResultDto mainFeedResultDto = feedService.feedMain(feedParamDto);

		if (mainFeedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<MainFeedResultDto>(mainFeedResultDto, HttpStatus.OK);
		}
		return new ResponseEntity<MainFeedResultDto>(mainFeedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// feedId로 해당 feed 상세조회
	// author항목에 들어있는 userSeq를 user테이블과 join하여 사용자이름을 받아올 것
	@GetMapping(value = "/feed/{feedId}")
	public ResponseEntity<FeedResultDto> feedDetail(@PathVariable int feedId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		int userSeq = Integer.parseInt(authentication.getName());

		FeedResultDto feedResultDto = feedService.feedDetail(feedId, userSeq);

		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		}
		return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	// feed추가
	// file받을때 미디어 타입을 모두 받는다는 코드..
	@PostMapping(consumes = MediaType.ALL_VALUE, value = "/feed")
	public ResponseEntity<FeedResultDto> feedInsert(
			@ModelAttribute FeedDto feedDto,
			MultipartHttpServletRequest request) {

		// 작성자 seq jwt에서 받아오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		int authorSeq = Integer.parseInt(authentication.getName());
		// 작성자 셋팅
		feedDto.setFeedAuthorSeq(authorSeq);

		FeedResultDto feedResultDto = feedService.feedInsert(feedDto, request);
		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// feed 추가페이지 로딩 요청시
	// 추가시 프런트에서 필요한 데이터넘겨줌
	@GetMapping(value = "/feed/info/{groupId}")
	public ResponseEntity<FeedResultDto> feedCreateInfo(@PathVariable int groupId) {

		HttpHeaders httpHeaders = new HttpHeaders();
		// CORS이슈
		System.out.println("httpHeaders : " + httpHeaders);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		int userSeq = Integer.parseInt(authentication.getName());

		GroupMemberDto params = new GroupMemberDto();
		params.setUserSeq(userSeq);
		params.setGroupId(groupId);

		FeedResultDto feedResultDto = feedService.feedCreateInfo(params);

		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// feed 수정
	// multipart/form-data 는 put 요청이 안됨으로 post 요청으로 받음
	// file관련해서는 모두 delete -> insert
	// 나머지 feed데이터 관련해서는 update 하는 로직으로 구성
	@PostMapping(value = "/feed/update")
	public ResponseEntity<FeedResultDto> feedUpdate(FeedDto feedDto, MultipartHttpServletRequest request) {
		System.out.println("update///" + feedDto);

		// 작성자 seq jwt에서 받아오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		int authorSeq = Integer.parseInt(authentication.getName());
		// 작성자 셋팅
		feedDto.setFeedAuthorSeq(authorSeq);

		FeedResultDto feedResultDto = feedService.feedUpdate(feedDto, request);
		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// feed 삭제
	@DeleteMapping(value = "/feed/{feedId}")
	public ResponseEntity<FeedResultDto> feedDelete(@PathVariable int feedId) {

		FeedResultDto feedResultDto = feedService.feedDelete(feedId);

		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 장소 즐겨찾기 삭제
	// 받은locationId 와 JWT토큰에서 userSeq를 받아서 삭제한다.
	@DeleteMapping(value = "/locationFav/{locationId}")
	public ResponseEntity<FeedResultDto> LocationFavoriteDelete(@PathVariable int locationId) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(authentication);
		int userSeq = Integer.parseInt(authentication.getName());

		LocationFavoriteDto locationFavoriteDto = new LocationFavoriteDto();
		locationFavoriteDto.setLocationId(locationId);
		locationFavoriteDto.setUserSeq(userSeq);

		FeedResultDto feedResultDto = feedService.locationFavoriteDelete(locationFavoriteDto);

		if (feedResultDto.getResult() == SUCCESS) {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<FeedResultDto>(feedResultDto, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
