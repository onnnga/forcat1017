package com.forcat.forcat.service;

import com.forcat.forcat.dto.PageRequestDTO;
import com.forcat.forcat.dto.PageResponseDTO;
import com.forcat.forcat.dto.board.BoardDTO;
import com.forcat.forcat.dto.board.BoardImageDTO;
import com.forcat.forcat.dto.board.BoardListAllDTO;
import com.forcat.forcat.dto.notice.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class NoticeServiceTests {

    @Autowired
    private NoticeService noticeService;

    @Test
    public void testRegisterWithImages () {
        log.info (noticeService.getClass ().getName ());
        NoticeDTO noticeDTO = NoticeDTO.builder ().noticeTitle ("File...Sample Title...").noticeContent ("Sample Content...").noticeWriter ("user00").build ();
        noticeDTO.setFileNames (Arrays.asList (UUID.randomUUID () + "_aaa.jpg", UUID.randomUUID () + "_bbb.jpg", UUID.randomUUID () + "_bbb.jpg"));
        Long noticeNo = noticeService.NoticeRegister (noticeDTO);
        log.info ("noticeNo: " + noticeNo);
    }

    @Test//게시물 조회
    public void testReadAll () {
        Long noticeNo = 100L;
        NoticeDTO noticeDTO = noticeService.NoticeReadOne (noticeNo);
        log.info (noticeDTO);
        for (String fileName : noticeDTO.getFileNames ()) {
            log.info (fileName);
        }//end for
    }

    @Test//게시물 수정
    public void testModify () {
        //변경에 필요한 데이터
        NoticeDTO noticeDTO = NoticeDTO.builder ().noticeNo (100L).noticeTitle ("Updated....100").noticeContent ("Updated content 100...").build ();
        //첨부파일을 하나 추가
        noticeDTO.setFileNames (List.of (UUID.randomUUID () + "_zzz.jpg"));
        noticeService.NoticeModify (noticeDTO);
    }

    @Test//게시물 삭제, 댓글이 존재하지 않는 경우
    public void testRemoveAll () {
        Long noticeNo = 100L;
        noticeService.NoticeRemove (noticeNo);
    }

    @Test//게시물 목록 처리
    public void testListWithAll () {
        NoticePageRequestDTO noticePageRequestDTO = NoticePageRequestDTO.builder ()//페이징 관련 정보 담음
                .noticePage (1)//1페이지
                .noticeSize (10)//페이지 크기
                .build ();
        //listWithAll 메서드를 호출하여 페이지 관련 정보를 전달하고, 이를 통해 게시물 목록을 검색
        NoticePageResponseDTO<NoticeListAllDTO> noticeResponseDTO = noticeService.noticeListWithAll (noticePageRequestDTO);
        //게시물 정보와 이미지 정보가 들어있는 DTO 리스트 추출
        List<NoticeListAllDTO> dtoNoticeList = noticeResponseDTO.getDtoNoticeList ();
        //BoardListAllDTO 객체를 순회하며 처리
        dtoNoticeList.forEach (NoticeListAllDTO -> {
            log.info (NoticeListAllDTO.getNoticeNo () + ":" + NoticeListAllDTO.getNoticeTitle ());
            if (NoticeListAllDTO.getNoticeImages () != null) {
                for (NoticeImageDTO noticeImage : NoticeListAllDTO.getNoticeImages ()) {
                    log.info (noticeImage);
                }
            }
            log.info ("-------------------------------");
        });
    }


}
