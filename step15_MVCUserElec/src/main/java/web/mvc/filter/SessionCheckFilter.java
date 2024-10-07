package web.mvc.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class SessionCheckFilter
 */
@WebFilter("/front")
public class SessionCheckFilter implements Filter {
	
    public SessionCheckFilter() {
    	System.out.println("SessionCheckFilter 생성됨...");
    }
    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("SessionCheck....");
		//사전처리 -> 인증여부를 체크한다.
		String key = request.getParameter("key");
		if(key==null || key.equals("elec")) {
			//인증된 사용자만 해라...
			HttpServletRequest req = (HttpServletRequest)request;
			HttpSession session = req.getSession();
			
			if(session.getAttribute("loginUser") == null) {
				req.setAttribute("errorMsg", "로그인하고 이용해주세요.^^");
				req.getRequestDispatcher("error/error.jsp").forward(request, response);
				return ;//함수를 빠져나가라
			}
		}
		
		chain.doFilter(request, response);
	}

	
}









