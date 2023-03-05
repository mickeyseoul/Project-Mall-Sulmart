package admin.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import alcohol.model.AlCateBean;
import alcohol.model.AlCateDao;
import alcohol.model.AlcoholBean;
import alcohol.model.AlcoholDao;
import utility.Paging;

@Controller
public class AdminAlcoholInsertController {
	
	private final String command = "/insertAlcohol.ad";
	private String gotoPage = "redirect:/alcoholList.ad";
	private String getPage = "/alcoholList";
	
	@Autowired
	private AlcoholDao alcoholDao;
	
	@Autowired
	ServletContext application;
	
	@Autowired
	private AlCateDao alCateDao;
	
	@RequestMapping(command)
	public String insert(@ModelAttribute("alcohol") @Valid AlcoholBean alcohol, BindingResult result,
			@RequestParam(value="pageNumber", required = false) String pageNumber,
			@RequestParam(value="whatColumn", required = false) String whatColumn,
			@RequestParam(value="keyword", required = false) String keyword,
			HttpServletRequest request,
			Model model,
			HttpServletResponse response) throws IOException {
		
		if(result.hasErrors()) {
			
			/*
			PrintWriter pw = response.getWriter();
			response.setContentType("text/html;charset=UTF-8"); //내보내는 데이터의 한글처리

			pw.println("<script>");
			pw.println("alert('카테고리를 선택해 주세요');");
			pw.println("</script>");
			pw.flush(); // 안쓰면 안뜸
			*/ //쓰면 리턴 페이지가 안나옴
			
			//검색어
			Map<String, String> map = new HashMap<String, String>();
			map.put("whatColumn", whatColumn);
			map.put("keyword", "%"+keyword+"%");
			//System.out.println("whatColumn "+whatColumn);
			//System.out.println("keyword "+keyword);
			
			//페이징
			int totalCount = alcoholDao.getTotalCount1(map);
			String url = request.getContextPath()+"/"+command;
			
			Paging pageInfo = new Paging(pageNumber,"5",totalCount,url,whatColumn,keyword,null);
			
			//주류 리스트 가져오기
			List<AlcoholBean> lists = new ArrayList<AlcoholBean>();
			lists = alcoholDao.getAllAlcohol(map,pageInfo);
			
			//주류 카테고리 가져오기
			List<AlCateBean> lists2 = new ArrayList<AlCateBean>();
			lists2 = alCateDao.getAllAlCate();
			
			model.addAttribute("lists", lists);
			model.addAttribute("lists2", lists2);
			model.addAttribute("pageInfo", pageInfo);
			model.addAttribute("totalCount", totalCount);
			
			return getPage;
			
		}
		
		System.out.println("AdminAlcoholInsertController");
		System.out.println(alcohol.getUpload());
		System.out.println(alcohol.getImage());
		
		//파일 업로드
		String path = application.getRealPath("/resources/images/alcohol");
		//System.out.println(path);
		//C:\Project\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\Mall_Sulmart\resources\images\alcohol
		
		MultipartFile multi = alcohol.getUpload(); //파일자체
		MultipartFile multi2 = alcohol.getUpload2();
		
		File file = new File(path+"/"+multi.getOriginalFilename()); //경로/파일이름의 파일 생성
		File file2 = new File(path+"/"+multi2.getOriginalFilename());
		
		try {
			multi.transferTo(file); //파일(멀티)을 '경로/파일이름'파일에 집어 넣기
			multi2.transferTo(file2);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		alcoholDao.insertAlcohol(alcohol);
		
		return gotoPage;
	}

}
