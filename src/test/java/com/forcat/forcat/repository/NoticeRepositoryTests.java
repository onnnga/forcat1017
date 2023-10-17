package com.forcat.forcat.repository;


import com.forcat.forcat.dto.PageRequestDTO;
import com.forcat.forcat.dto.PageResponseDTO;
import com.forcat.forcat.dto.board.BoardDTO;
import com.forcat.forcat.dto.board.BoardListAllDTO;
import com.forcat.forcat.dto.board.BoardListReplyCountDTO;
import com.forcat.forcat.dto.notice.*;
import com.forcat.forcat.entity.Board;
import com.forcat.forcat.entity.BoardImage;
import com.forcat.forcat.entity.Notice;
import com.forcat.forcat.entity.NoticeImage;
import com.forcat.forcat.service.BoardService;
import com.forcat.forcat.service.NoticeService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest//스프링부트 테스트 명시
@Log4j2//로그 사용 명시
public class NoticeRepositoryTests {

    @Autowired//빈 생성
    private NoticeRepository noticeRepository;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeReplyRepository noticeReplyRepository;

    @Test//게시글 조회 테스트
    public void testSelect () {
        Long noticeNo = 99L;//n번째 게시글
        //boardRepository을 사용하여 n번째 게시글을 찾는다.
        //Optional 객체는 null 에러 방지
        Optional<Notice> result = noticeRepository.findById (noticeNo);
        //result가 null일 경우 board에 담는다.
        Notice notice = result.orElseThrow ();
        log.info (notice);
    }

    @Test//게시글 수정 테스트
    public void testUpdate () {
        Long noticeNo = 99L;//n번째 게시글
        //boardRepository을 사용하여 n번째 게시글을 찾는다.
        //Optional 객체는 null 에러 방지
        Optional<Notice> result = noticeRepository.findById (noticeNo);
        //result가 null일 경우 board에 담는다.
        Notice notice = result.orElseThrow ();
        //board 클래스의 change 메서드 실행
        notice.change ("update..title 99", "update content 99");
        //업데이트 내용 DB 저장
        noticeRepository.save (notice);
    }

    @Test//게시글 삭제 테스트
    public void testDelete () {
        Long noticeNo = 99L;
        noticeRepository.deleteById (noticeNo);
    }

    @Test//게시글 페이징 테스트
    public void testPaging () {
        //1 page order by bno desc
        //Pageable 0번째 페이지, 10개 게시글, bno기준 내림차순 정렬
        Pageable pageable = PageRequest.of (0, 10, Sort.by ("noticeNo").descending ());
        //DB에 Pageable 조건 조회
        Page<Notice> result = noticeRepository.findAll (pageable);
        log.info ("total count: " + result.getTotalElements ());
        log.info ("total pages:" + result.getTotalPages ());
        log.info ("page number: " + result.getNumber ());
        log.info ("page size: " + result.getSize ());
        List<Notice> todoList = result.getContent ();
        todoList.forEach (notice -> log.info (notice));
    }

    @Test
    public void testSearch1 () {
        //2 page order by bno desc
        Pageable pageable = PageRequest.of (1, 10, Sort.by ("noticeNo").descending ());
        noticeRepository.searchNotice1 (pageable);
    }

    @Test//Querydsl 검색 테스트
    public void testSearchAll () {
        String[] types = {"t", "c", "w"};//검색할 항목 명시
        String keyword = "1";//검색 키워드 명시
        Pageable pageable = PageRequest.of (0, 10, Sort.by ("noticeNo").descending ());//페이지 네이션 정보 명시
        Page<Notice> result = noticeRepository.searchNoticeAll (types, keyword, pageable);
    }

    @Test
    public void testSearchAll2 () {
        String[] types = {"t", "c", "w"};//검색할 항목 명시
        String keyword = "1";//검색 키워드 명시
        Pageable pageable = PageRequest.of (0, 10, Sort.by ("noticeNo").descending ());//페이지 네이션 정보 명시
        Page<Notice> result = noticeRepository.searchNoticeAll (types, keyword, pageable);
        //total pages
        log.info (result.getTotalPages ());
        //pag size
        log.info (result.getSize ());
        //pageNumber
        log.info (result.getNumber ());
        //prev next
        log.info (result.hasPrevious () + ": " + result.hasNext ());
        result.getContent ().forEach (notice -> log.info (notice));
    }

    @Test//게시글 등록 테스트
    public void testRegister () {
        log.info (noticeService.getClass ().getName ());
        NoticeDTO noticeDTO = NoticeDTO.builder ()//BoardDTO 객체 생성 및 초기화, 정보를 담는다.
                .noticeTitle ("Sample Title...").noticeContent ("Sample Content...").noticeWriter ("user00").build ();
        Long noticeNo = noticeService.NoticeRegister (noticeDTO);//boardService의 register() 메서드를 호출하여 boardDTO를 이용해 게시글을 등록
        log.info ("noticeNo : " + noticeNo);
    }

    @Test//게시글 조회 테스트
    public void testReadOne () {
        Long noticeNo = 2L;
        noticeService.NoticeReadOne (noticeNo);
        log.info (noticeService.NoticeReadOne (noticeNo));
    }

    @Test//게시글 수정 테스트
    public void testModify () {
        //변경에 필요한 데이터만 입력
        NoticeDTO noticeDTO = NoticeDTO.builder ().noticeNo (1L).noticeTitle ("Updated Title...100").noticeContent ("Updated Content...100").build ();
        noticeService.NoticeModify (noticeDTO);
    }

    @Test//게시글 목록/검색 테스트
    public void testList () {
        // PageRequestDTO 객체를 생성하여 테스트에 필요한 페이지네이션 및 검색 조건을 설정
        NoticePageRequestDTO noticePageRequestDTO = NoticePageRequestDTO.builder ().type ("tcw")//검색 조건
                .keyword ("1")//검색 키워드
                .noticePage (1)//현재 페이지 번호
                .noticeSize (10)//페이지 크기
                .build ();
        // 게시글 목록 조회 또는 검색을 수행하고, 결과를 responseDTO에 저장
        NoticePageResponseDTO<NoticeDTO> responseDTO = noticeService.noticeList (noticePageRequestDTO);
        log.info (responseDTO);
    }

    @Test
    public void testSearchReplyCount () {
        String[] types = {"t", "c", "w"};
        String keyword = "1";
        Pageable pageable = PageRequest.of (0, 10, Sort.by ("noticeNo").descending ());
        Page<NoticeListReplyCountDTO> result = noticeRepository.searchWithNoticeReplyCount (types, keyword, pageable);
        //total pages
        log.info (result.getTotalPages ());
        //pag size
        log.info (result.getSize ());
        //pageNumber
        log.info (result.getNumber ());
        //prev next
        log.info (result.hasPrevious () + ": " + result.hasNext ());
        result.getContent ().forEach (notice -> log.info (notice));
    }

    @Test//게시글, 게시글 이미지 입력
    public void testInsertWithImages () {
        Notice notice = Notice.builder ().noticeTitle ("Image Test").noticeContent ("첨부파일 테스트").noticeWriter ("tester").build ();
        for (int i = 0; i < 3; i++) {
            notice.addImage (UUID.randomUUID ().toString (), "file" + i + ".jpg");
        }//end for
        noticeRepository.save (notice);
    }

    @Transactional
    @Test
    public void testReadWithImages () {
        //반드시 존재하는 bno로 확인
        Optional<Notice> result = noticeRepository.findByIdWithNoticeImages (2L);
        Notice notice = result.orElseThrow ();
        log.info (notice);
        log.info ("--------------------a");
        //log.info(board.getImageSet());
        for (NoticeImage noticeImage : notice.getImageSet ()) {
            log.info (noticeImage);
        }
    }

    @Transactional
    @Commit
    @Test
    public void testModifyImages () {
        Optional<Notice> result = noticeRepository.findByIdWithNoticeImages (1L);
        Notice notice = result.orElseThrow ();
        //기존의 첨부파일들은 삭제
        notice.clearImages ();
        //새로운 첨부파일들
        for (int i = 0; i < 2; i++) {
            notice.addImage (UUID.randomUUID ().toString (), "updatefile" + i + ".jpg");
        }
        noticeRepository.save (notice);
    }

    @Test//게시물과 첨부파일 삭제
    @Transactional
    @Commit
    public void testRemoveAll () {
        Long noticeNo = 98L;
        noticeReplyRepository.deleteByNotice_NoticeNo (noticeNo);
        noticeRepository.deleteById (noticeNo);
    }

    @Test
    public void testInsertAll () {
        for (int i = 1; i <= 100; i++) {
            Notice notice = Notice.builder ().noticeTitle ("Title.." + i).noticeContent ("Content.." + i).noticeWriter ("writer.." + i).build ();
            for (int j = 0; j < 3; j++) {
                if (i % 5 == 0) {
                    continue;
                }
                notice.addImage (UUID.randomUUID ().toString (), i + "file" + j + ".jpg");
            }
            noticeRepository.save (notice);
        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount () {
        Pageable pageable = PageRequest.of (0, 10, Sort.by ("noticeNo").descending ());
        //boardRepository.searchWithAll(null, null,pageable);
        Page<NoticeListAllDTO> result = noticeRepository.searchWithNoticeAll (null, null, pageable);
        log.info ("---------------------------");
        log.info (result.getTotalElements ());
        result.getContent ().forEach (noticeListAllDTO -> log.info (noticeListAllDTO));
    }


}
