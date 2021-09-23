package kr.co.softcampus.service;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.softcampus.beans.ContentBean;
import kr.co.softcampus.beans.PageBean;
import kr.co.softcampus.beans.UserBean;
import kr.co.softcampus.dao.BoardDao;

@Service
@PropertySource("WEB-INF/properties/option.properties")
public class BoardService {
	
	@Value("${path.upload}")
	private String path_upload;
	
	@Value("${page.listcnt}")
	private int page_listcnt;

	@Value("${page.paginationcnt}")
	private int page_paginationcnt;
	
	@Autowired
	private BoardDao boardDao;
	
	@Resource(name="loginUserBean")
	private UserBean loginUserBean;
	
	private String saveUploadFile(MultipartFile upload_file){//중복파일이 올라오면 덮어쓰우기가 됨, 예방코자 파일 이름에 현재시간 붙히기
		String file_name = System.currentTimeMillis()+"_"+upload_file.getOriginalFilename();
		
		try {
			upload_file.transferTo(new File(path_upload+"/"+file_name));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return file_name;
	}
	
	public void addContentInfo(ContentBean writeContentBean) {
		MultipartFile upload_file = writeContentBean.getUpload_file();
		
		if(upload_file.getSize() > 0) {
			String file_name = saveUploadFile(upload_file);
			writeContentBean.setContent_file(file_name);
		}
		writeContentBean.setContent_writer_idx(loginUserBean.getUser_idx());
		boardDao.addContentInfo(writeContentBean);
		
	}
	
	public String getBoardInfoName(int board_info_idx) {
		return boardDao.getBoardInfoName(board_info_idx);
	}
	
	public List<ContentBean> getContentList (int board_info_idx, int page){
		
		int start = (page - 1) * page_listcnt;
		RowBounds rowBounds = new RowBounds(start, page_listcnt);
		//1page = (1-1)*10 = 0 (1페이지: 0부터 카운터 시작)
		//2page = (2-1)*10 = 1 (2페이지)
		return boardDao.getContentList(board_info_idx, rowBounds);
		
	}
	public ContentBean getContentInfo(int content_idx) {
		return boardDao.getContentInfo(content_idx);
	}
	public void modifyContentInfo(ContentBean modifyContentBean) {
		
		MultipartFile upload_file = modifyContentBean.getUpload_file();
		if(upload_file.getSize() > 0) {						//업로드할 파일 크기가 0보다 크면 (파일이 있으면)
			String file_name = saveUploadFile(upload_file);//file_name 변수에 upload_file명을 주입
			modifyContentBean.setContent_file(file_name); // modifyContentBean에 content_file명으로 content_file에 주입된 파일명 주입
		}
		boardDao.modifyContentInfo(modifyContentBean); //boardDao에 modifyContentInfo 메서드에 modifyContentBean 전달
	}
	public void deleteContentInfo(int content_idx) {
		boardDao.deleteContentInfo(content_idx);
	}
	public PageBean getContentCnt(int content_board_idx, int currentPage) {
		
		int content_cnt = boardDao.getContentCnt(content_board_idx);
		PageBean pageBean = new PageBean(content_cnt, currentPage, page_listcnt, page_paginationcnt);
		return pageBean;
		
	}
}
