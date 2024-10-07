package web.mvc.controller;


import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import web.mvc.dto.Electronics;
import web.mvc.service.ElectronicsService;
import web.mvc.service.ElectronicsServiceImpl;


public class ElectronicsController implements Controller {
	
	private ElectronicsService elecService = new ElectronicsServiceImpl();
	public ElectronicsController() {
		System.out.println("ElectronicsController 생성자 호출됨....");
	}


	
	/**
	 *  전체검색
	 * */
	public ModelAndView select(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		
		  String pageNo = request.getParameter("pageNo");
		  if(pageNo==null || pageNo.equals("")) {
			  pageNo="1";
		  }
		
		//List<Electronics> list = elecService.selectAll();
		
		  List<Electronics> list = elecService.selectAll( Integer.parseInt(pageNo) );
		  
		request.setAttribute("list", list);//뷰에서 ${list}
		request.setAttribute("pageNo", pageNo); //뷰에서 ${pageNo}
		
		return new ModelAndView("elec/list.jsp"); //forward방식으로 이동
	}
	
	
	/**
	 * 등록하기
	 * */
	public ModelAndView insert(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//기능
		
		//전송된 데이터 받기 
		String modelNum = request.getParameter("model_num");
		String modelName = request.getParameter("model_name");
		String price = request.getParameter("price");
		String description = request.getParameter("description");
		String password = request.getParameter("password");
		
		Electronics elec = 
			new Electronics(modelNum, modelName, Integer.parseInt(price), description, password);
		
		Part part = request.getPart("file");//Servlet 3.0버전부터 제공되는 Part API를 이용한 방법인데, getPart() 메서드
		if(part!=null) {
			String fileName = this.getFilename(part);
			System.out.println("fileName = " + fileName);
			String saveDir = request.getServletContext().getRealPath("/save"); 
			//String saveDir = "C:\\Edu\\save"; 
			
			if (fileName!=null && !fileName.equals("")) {
	            part.write( saveDir + "/"+ fileName);//서버폴더에 파일 저장=업로드
	            elec.setFname(fileName);
	            elec.setFsize( (int)part.getSize() );
	        }
		}
		
		elecService.insert(elec);

	   return new ModelAndView("front", true);//key=elec&methodName=select 기본으로 설정된다.	
	}
	
	/**
	 * 전송된 파일정보에서 파일이름만 추출해 내는 과정 
	 * */
	private String getFilename(Part part) {
        String headerContent = part.getHeader("content-disposition");
        
        //contentDisp의 결과 form-data; name="fileName"; filename="추가한 파일 이름"
        System.out.println(headerContent);
        
        String[] split = headerContent.split(";");
        for (int i = 0; i < split.length; i++) {
            String temp = split[i];
            if (temp.trim().startsWith("filename")) {
            	System.out.println("temp = " + temp);
            	System.out.println("temp.indexOf(=) = " + temp.indexOf("=") );
            	
                return temp.substring( temp.indexOf("=") + 2 ,  temp.length() - 1);
            }
        }
        return null;
    }
	
	
	/**
	 * 상세보기 
	 * */

	public ModelAndView selectByModelNum(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		 String modelNum = request.getParameter("modelNum");
		 boolean state = request.getParameter("flag")==null ? true : false;
		 
		String pageNo =  request.getParameter("pageNo");
		 
		 //두번째 인수 boolean 조회수 증가여부를판단할 인수(true이면, false이면 증가안함)
		Electronics electronics = elecService.selectByModelnum(modelNum, state);
		request.setAttribute("elec", electronics);
		request.setAttribute("pageNo", pageNo);
		
		return new ModelAndView("elec/read.jsp"); //forward방식 
	}
	
	
	/**
	 *  수정폼
	 * */
	public ModelAndView updateForm(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String modelNum  = request.getParameter("modelNum");
		String pageNo  = request.getParameter("pageNo");
		
		Electronics elec = elecService.selectByModelnum(modelNum, false);
		request.setAttribute("elec", elec);
		request.setAttribute("pageNo", pageNo);
		
		return new ModelAndView("elec/update.jsp");//forward방식
	}
	
	/**
	 * 수정완료
	 * */
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
	   //수정할 정보 5개 받기
		String modelNum = request.getParameter("modelNum");
		String modelName = request.getParameter("modelName");
		String price = request.getParameter("price");
		String description = request.getParameter("description");
		String password = request.getParameter("password");
		
		String pageNo = request.getParameter("pageNo");
		
		elecService.update( new Electronics(modelNum, modelName, Integer.parseInt(price), 
				description, password) );
		
		//수정이 완료가 된후....
		ModelAndView mv = new ModelAndView();
		mv.setViewName("front?key=elec&methodName=selectByModelNum&modelNum="+modelNum+"&flag=1&pageNo="+pageNo);
	    mv.setRedirect(true);
		return mv;
	}
	/**
	 * 삭제
	 * 
	 * */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//전송되는 2개받기
		String modelNum = request.getParameter("modelNum");
		String password = request.getParameter("password");
		
		//첨부된 파일을 삭제하는 경우라면 삭제가 되었을때 폴더에서 파일도 삭제한다. - 이 기능을 service한다.
		String saveDir = request.getServletContext().getRealPath("/save");
		
		elecService.delete(modelNum, password , saveDir);
		
		return new ModelAndView("front", true);
	}
	
}//classEnd



























